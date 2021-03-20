// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.controller.ControllerInstanceManager;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.configuration.DocumentSettingsUI;
import de.mossgrabers.reaper.framework.configuration.IfxSetting;
import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.graphics.SVGImage;
import de.mossgrabers.reaper.framework.midi.Midi;
import de.mossgrabers.reaper.framework.midi.MidiConnection;
import de.mossgrabers.reaper.ui.MainFrame;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Main window which provides the user interface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MainApp implements MessageSender, AppCallback, WindowManager
{
    private static final int          DEVICE_UPDATE_RATE = 30;
    private static final Pattern      TAG_PATTERN        = Pattern.compile ("(.*?)=\"(.*?)\"\\s*");

    private final LogModel            logModel           = new LogModel ();

    private final MainConfiguration   mainConfiguration  = new MainConfiguration ();
    private final Object              mainFrameLock      = new Object ();
    private MainFrame                 mainFrame;

    private ControllerInstanceManager instanceManager;
    private final Timer               animationTimer;
    private String                    iniPath;
    private final IniFiles            iniFiles           = new IniFiles ();


    /**
     * Constructor.
     *
     * @param iniPath The Reaper path which contains all INI files
     */
    public MainApp (final String iniPath)
    {
        this.iniPath = iniPath;

        // To not again loose any exception from executors or delayed methods...
        Thread.setDefaultUncaughtExceptionHandler ( (thread, throwable) -> {
            final StringWriter writer = new StringWriter ();
            throwable.printStackTrace (new PrintWriter (writer));
            this.logModel.info (writer.toString ());
        });

        this.instanceManager = new ControllerInstanceManager (this.logModel, this, this, this.iniFiles);

        if (this.iniPath.isEmpty ())
            this.logModel.info ("Missing INI path parameter! Cannot start the application.");
        else
        {
            this.loadConfig ();
            this.loadINIFiles (this.iniPath);
        }

        this.animationTimer = new Timer (DEVICE_UPDATE_RATE, event -> {
            try
            {
                this.flushToController ();
            }
            catch (final RuntimeException ex)
            {
                this.logModel.error ("Crash in flush timer.", ex);
            }
        });

        if (this.iniPath != null)
        {
            this.initUSB ();
            SafeRunLater.execute (this.logModel, this::startupInfrastructure);
        }
    }


    /**
     * Initialise USB.
     */
    protected void initUSB ()
    {
        try
        {
            final int result = LibUsb.init (null);
            if (result != LibUsb.SUCCESS)
                throw new LibUsbException ("Unable to initialize libusb.", result);

            // Print LibUsb errors and warnings
            LibUsb.setOption (null, LibUsb.OPTION_LOG_LEVEL, LibUsb.LOG_LEVEL_WARNING);
        }
        catch (final LibUsbException ex)
        {
            this.logModel.error ("Could not initialise LibUsb.", ex);
        }
        catch (final Throwable ex)
        {
            this.logModel.error ("Could not LibUsb library.", ex);
        }
    }


    /**
     * Exits the application.
     */
    public void exit ()
    {
        this.logModel.info ("Exiting platform...");

        this.logModel.info ("Stopping flush timer...");
        this.animationTimer.stop ();

        this.instanceManager.stopAll ();

        SVGImage.clearCache ();

        this.logModel.info ("Storing configuration...");
        this.instanceManager.save (this.mainConfiguration);
        this.saveConfig ();

        MidiConnection.cleanupUnusedDevices ();

        this.logModel.info ("Shutting down USB...");
        // Don't execute on Mac since it hangs in the function, the memory is cleaned up on exit
        // anyway
        if (OperatingSystem.get () != OperatingSystem.MAC)
            LibUsb.exit (null);
    }


    /**
     * Load the settings from the config file.
     */
    protected void loadConfig ()
    {
        try
        {
            this.mainConfiguration.load (this.iniPath);
        }
        catch (final IOException ex)
        {
            this.logModel.error ("Could not load main configuration.", ex);
        }

        SVGImage.clearCache ();
    }


    /**
     * Save the settings from the config file.
     */
    protected void saveConfig ()
    {
        if (this.iniPath == null)
            return;

        try
        {
            if (this.mainFrame != null)
                this.mainConfiguration.storeStagePlacement (this.mainFrame);
            this.mainConfiguration.save (this.iniPath);
        }
        catch (final IOException ex)
        {
            this.logModel.error ("Could not store configuration file.", ex);
        }
    }


    /**
     * Start scripting engine, open osc and midi ports.
     */
    private void startupInfrastructure ()
    {
        this.startFlushTimer ();
        this.updateMidiDevices ();
        this.instanceManager.load (this.mainConfiguration);
        this.startControllers ();
    }


    /**
     * Read all available MIDI devices.
     */
    private void updateMidiDevices ()
    {
        try
        {
            Midi.readDeviceMetadata ();
        }
        catch (final MidiUnavailableException ex)
        {
            this.logModel.error ("Midi not available.", ex);
        }
    }


    /**
     * Start all configured controllers.
     */
    private void startControllers ()
    {
        this.instanceManager.startAll ();
        SafeRunLater.execute (this.logModel, this::sendRefreshCommand);
    }


    /**
     * Restart all configured controllers.
     */
    private void restartControllers ()
    {
        this.instanceManager.stopAll ();
        this.startControllers ();
    }


    /**
     * Send a controller flush message to the script.
     */
    void flushToController ()
    {
        this.instanceManager.flushAll ();
    }


    /**
     * Starts the controller flush loop for display updates.
     */
    private void startFlushTimer ()
    {
        this.animationTimer.start ();
    }


    /**
     * Closes the given socket if it is not null.
     *
     * @param socket The socket to close
     */
    public void closeSocket (final Socket socket)
    {
        if (socket == null)
            return;
        try
        {
            socket.close ();
        }
        catch (final IOException ex)
        {
            this.logModel.error ("Could not close socket.", ex);
        }
    }


    /**
     * Parse an incoming DAW message into all configured controllers.
     *
     * @param address The message address
     * @param argument The argument
     */
    private void handleReceiveOSC (final String address, final String argument)
    {
        this.instanceManager.parseAll (address, argument);
    }


    /** {@inheritDoc} */
    @Override
    public void invokeAction (final int actionID)
    {
        if (Actions.isBlocked (actionID))
            return;

        this.processIntArg ("action", "", actionID);
    }


    /** {@inheritDoc} */
    @Override
    public void processNoArg (final Processor processor, final String command)
    {
        this.processNoArg (processor.name ().toLowerCase (Locale.US), command);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     */
    public native void processNoArg (final String processor, final String command);


    /** {@inheritDoc} */
    @Override
    public void processStringArg (final Processor processor, final String command, final String value)
    {
        this.processStringArg (processor.name ().toLowerCase (Locale.US), command, value);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value A string value
     */
    public native void processStringArg (final String processor, final String command, final String value);


    /** {@inheritDoc} */
    @Override
    public void processIntArg (final Processor processor, final String command, final int value)
    {
        this.processIntArg (processor.name ().toLowerCase (Locale.US), command, value);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value An integer value
     */
    public native void processIntArg (final String processor, final String command, final int value);


    /** {@inheritDoc} */
    @Override
    public void processDoubleArg (final Processor processor, final String command, final double value)
    {
        this.processDoubleArg (processor.name ().toLowerCase (Locale.US), command, value);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value A double value
     */
    public native void processDoubleArg (final String processor, final String command, final double value);


    /** {@inheritDoc} */
    @Override
    public void delayUpdates (final Processor processor)
    {
        this.delayUpdates (processor.name ().toLowerCase (Locale.US));
    }


    /**
     * Delay updates for a specific processor. Use to prevents that Reaper sends old values before
     * the latest ones are applied.
     *
     * @param processor The processor to delay
     */
    public native void delayUpdates (final String processor);


    /** {@inheritDoc} */
    @Override
    public void enableUpdates (final Processor processor, final boolean enable)
    {
        this.enableUpdates (processor.name ().toLowerCase (Locale.US), enable);
    }


    /**
     * Dis-/enable an update processor for performance improvements.
     *
     * @param processor The processor to The processor to dis-/enable
     * @param enable True to enable processor updates, false to disable
     */
    public native void enableUpdates (String processor, boolean enable);


    /** {@inheritDoc} */
    @Override
    public native void processMidiArg (final int status, final int data1, final int data2);


    /** {@inheritDoc} */
    @Override
    public IControllerInstance addController (final IControllerDefinition definition)
    {
        if (this.instanceManager.isInstantiated (definition))
        {
            this.logModel.info ("Only one instance of a controller type is supported!");
            return null;
        }

        final IControllerInstance controllerInstance = this.instanceManager.instantiate (definition);
        controllerInstance.start ();
        this.sendRefreshCommand ();
        return controllerInstance;
    }


    /** {@inheritDoc} */
    @Override
    public List<IControllerInstance> detectControllers ()
    {
        // In case a device was hot-swapped
        this.updateMidiDevices ();

        final List<IControllerInstance> addedControllers = new ArrayList<> ();

        for (final IControllerDefinition definition: this.instanceManager.getDefinitions ())
        {
            // Already detected
            if (this.instanceManager.isInstantiated (definition))
                continue;

            for (final Pair<String [], String []> pair: definition.getMidiDiscoveryPairs (OperatingSystem.get ()))
            {
                final String [] ins = pair.getKey ();
                final String [] outs = pair.getValue ();

                // Ignore utility extensions, which would always match
                if (ins.length == 0 && outs.length == 0)
                    continue;

                // Is there a match?
                final List<MidiDevice> inputDevices = lookupInputs (ins);
                if (ins.length != inputDevices.size ())
                    continue;
                final List<MidiDevice> outputDevices = lookupOutputs (outs);
                if (outs.length != outputDevices.size ())
                    continue;

                // Is one of the midi device already in use?
                if (this.instanceManager.areInUse (inputDevices, outputDevices))
                    continue;

                // Found!
                final IControllerInstance c = this.addController (definition);
                if (c != null)
                    addedControllers.add (c);
            }
        }

        return addedControllers;
    }


    private static List<MidiDevice> lookupOutputs (final String [] outs)
    {
        final List<MidiDevice> outputDevices = new ArrayList<> ();
        for (final String outputName: outs)
        {
            final MidiDevice outputDevice = Midi.getOutputDevice (outputName);
            if (outputDevice != null)
                outputDevices.add (outputDevice);
        }
        return outputDevices;
    }


    private static List<MidiDevice> lookupInputs (final String [] ins)
    {
        final List<MidiDevice> inputDevices = new ArrayList<> ();
        for (final String inputName: ins)
        {
            final MidiDevice inputDevice = Midi.getInputDevice (inputName);
            if (inputDevice != null)
                inputDevices.add (inputDevice);
        }
        return inputDevices;
    }


    /** {@inheritDoc} */
    @Override
    public void editController (final int controllerIndex)
    {
        this.instanceManager.edit (controllerIndex);
        this.restartControllers ();
    }


    /** {@inheritDoc} */
    @Override
    public void removeController (final int controllerIndex)
    {
        this.instanceManager.remove (controllerIndex);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnableController (final int controllerIndex)
    {
        final IControllerInstance controller = this.instanceManager.getInstances ().get (controllerIndex);
        controller.storeConfiguration ();
        if (controller.isEnabled ())
        {
            controller.start ();
            this.sendRefreshCommand ();
        }
        else
            controller.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void projectSettings ()
    {
        this.instanceManager.projectSettings ();
    }


    /** {@inheritDoc} */
    @Override
    public void sendRefreshCommand ()
    {
        this.processNoArg (Processor.REFRESH);
    }


    /** {@inheritDoc} */
    @Override
    public void clearLogMessage ()
    {
        this.logModel.clearLogMessage ();
    }


    /**
     * Update the data model.
     *
     * @param data The data formatted as pseudo OSC commands, separated by line breaks
     */
    public void updateModel (final String data)
    {
        if (data == null || data.isEmpty ())
            return;

        SafeRunLater.execute (this.logModel, () -> {
            for (final String command: data.split ("\n"))
            {
                final String [] split = command.split (" ");
                final String params = split.length == 1 ? null : command.substring (split[0].length () + 1);
                try
                {
                    this.handleReceiveOSC (split[0], params);
                }
                catch (final IllegalArgumentException ex)
                {
                    final StringWriter sw = new StringWriter ();
                    ex.printStackTrace (new PrintWriter (sw));
                    this.logModel.info (sw.toString ());
                }
            }
        });
    }


    /**
     * Set the default initial settings for the document/project.
     */
    public void setDefaultDocumentSettings ()
    {
        for (final IControllerInstance instance: this.instanceManager.getInstances ())
            instance.getDocumentSettingsUI ().getSettings ().forEach (IfxSetting::reset);
    }


    /**
     * Get the formatted document settings formatted to be stored in the Reaper extension data
     *
     * @return The formatted document settings
     */
    public String getFormattedDocumentSettings ()
    {
        final StringBuilder data = new StringBuilder ();

        for (final IControllerInstance instance: this.instanceManager.getInstances ())
        {
            final IControllerDefinition definition = instance.getDefinition ();

            final DocumentSettingsUI documentSettingsUI = instance.getDocumentSettingsUI ();
            documentSettingsUI.store ();
            final PropertiesEx properties = documentSettingsUI.store ();
            final StringWriter writer = new StringWriter ();
            final Encoder encoder = Base64.getEncoder ();
            try
            {
                properties.store (writer, "");
                final String str = writer.toString ();
                final String encodedString = encoder.encodeToString (str.getBytes (StandardCharsets.UTF_8));
                final String tag = definition.getHardwareModel ().replace (' ', '_').replace ('/', '_').toUpperCase (Locale.US);
                data.append (tag).append ("=\"").append (encodedString).append ("\"\n");
            }
            catch (final IOException ex)
            {
                final StringWriter sw = new StringWriter ();
                ex.printStackTrace (new PrintWriter (sw));
                this.logModel.info (sw.toString ());
            }
        }

        return data.toString ();
    }


    /**
     * Get the formatted document settings formatted to be stored in the Reaper extension data
     *
     * @param data The formatted document settings
     */
    public void setFormattedDocumentSettings (final String data)
    {
        final Matcher matcher = TAG_PATTERN.matcher (data);
        final Decoder decoder = Base64.getDecoder ();
        final Map<String, String> found = new HashMap<> ();
        while (matcher.find ())
        {
            if (matcher.groupCount () != 2)
                break;

            final String tag = matcher.group (1);
            final String propertiesText = new String (decoder.decode (matcher.group (2)), StandardCharsets.UTF_8);
            found.put (tag, propertiesText);
        }

        for (final IControllerInstance instance: this.instanceManager.getInstances ())
        {
            final IControllerDefinition definition = instance.getDefinition ();
            final String tag = definition.getHardwareModel ().replace (' ', '_').replace ('/', '_').toUpperCase (Locale.US);

            final String propertiesText = found.get (tag);
            if (propertiesText != null)
                instance.getDocumentSettingsUI ().parse (propertiesText);
        }
    }


    /**
     * Load and parse all Reaper INI files, which contain information about the available devices.
     *
     * @param path The path which contains the INI files
     */
    private final void loadINIFiles (final String path)
    {
        this.logModel.info ("Loading device INI files from " + path + " ...");
        this.iniFiles.init (path, this.logModel);
        DeviceManager.get ().parseINIFiles (this.iniFiles, this.logModel);
    }


    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    public void showStage ()
    {
        SafeRunLater.execute (this.logModel, () -> {
            try
            {
                final MainFrame win = this.getMainFrame ();
                win.setVisible (true);
                win.toFront ();
            }
            catch (final RuntimeException ex)
            {
                final StringWriter sw = new StringWriter ();
                ex.printStackTrace (new PrintWriter (sw));
                this.logModel.info (sw.toString ());
            }
        });
    }


    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages. After
     * that the project settings window is displayed.
     */
    public void showProjectWindow ()
    {
        SafeRunLater.execute (this.logModel, () -> {
            try
            {
                final MainFrame win = this.getMainFrame ();
                win.setVisible (true);
                win.toFront ();
                win.projectSettings ();
            }
            catch (final RuntimeException ex)
            {
                final StringWriter sw = new StringWriter ();
                ex.printStackTrace (new PrintWriter (sw));
                this.logModel.info (sw.toString ());
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    public MainFrame getMainFrame ()
    {
        synchronized (this.mainFrameLock)
        {
            if (this.mainFrame == null)
            {
                setSystemLF ();
                this.mainFrame = new MainFrame (this, this.instanceManager, this.logModel);
                this.mainConfiguration.restoreStagePlacement (this.mainFrame);
            }
            return this.mainFrame;
        }
    }


    private static void setSystemLF ()
    {
        try
        {
            UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
        }
        catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            // Ignore
        }
    }
}
