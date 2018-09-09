// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.ControllerInstanceManager;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.graphics.SVGImage;
import de.mossgrabers.reaper.framework.midi.Midi;
import de.mossgrabers.reaper.framework.midi.MidiConnection;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;


/**
 * Main window which provides the user interface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MainFrame extends JFrame implements MessageSender
{
    private static final long                serialVersionUID  = 4251131641194938848L;
    private static final int                 GAP               = 14;

    private JTextArea                        loggingTextArea   = new JTextArea ();
    private final LogModel                   logModel          = new LogModel (this.loggingTextArea);

    protected final MainConfiguration        mainConfiguration = new MainConfiguration ();

    private final JList<IControllerInstance> controllerList    = new JList<> (new DefaultListModel<> ());

    private ControllerInstanceManager        instanceManager;
    private final Timer                      animationTimer;
    private String                           iniPath;
    private IniFiles                         iniFiles          = new IniFiles ();

    private JButton                          addButton;
    private JButton                          removeButton;
    private JButton                          configButton;


    /**
     * Constructor.
     *
     * @param iniPath The Reaper path which contains all INI files
     */
    public MainFrame (final String iniPath)
    {
        this.iniPath = iniPath;

        // To not again loose any exception from executors or delayed methods...
        Thread.setDefaultUncaughtExceptionHandler ( (thread, throwable) -> {
            final StringWriter writer = new StringWriter ();
            throwable.printStackTrace (new PrintWriter (writer));
            this.logModel.info (writer.toString ());
        });

        this.instanceManager = new ControllerInstanceManager (this.logModel, this, this, this.iniFiles);

        this.setType (Type.UTILITY);

        this.updateTitle ();

        if (this.iniPath.isEmpty ())
            this.logModel.info ("Missing INI path parameter! Cannot start the application.");
        else
        {
            this.loadConfig ();
            this.loadINIFiles (this.iniPath);
        }

        this.createUI ();
        this.showStage (this);

        this.animationTimer = new Timer (20, event -> {
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
            SafeRunLater.execute (this::startupInfrastructure);
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
            LibUsb.setDebug (null, LibUsb.LOG_LEVEL_WARNING);

        }
        catch (final LibUsbException ex)
        {
            this.logModel.error ("Could not initialise LibUsb.", ex);
        }
    }


    protected void createUI ()
    {
        // Top pane with options

        // Top pane
        final JButton refreshButton = new JButton ("Refresh");
        refreshButton.addActionListener (event -> this.sendRefreshCommand ());

        // Center pane with device configuration and logging
        this.configButton = new JButton ("Configuration");
        this.configButton.addActionListener (event -> this.editController ());
        this.addButton = new JButton ("Add");
        this.configureAddButton (this.addButton);

        this.removeButton = new JButton ("Remove");
        this.removeButton.addActionListener (event -> this.removeController ());

        final JPanel deviceButtonContainer = new JPanel ();
        deviceButtonContainer.setBorder (new EmptyBorder (0, GAP, 0, 0));
        deviceButtonContainer.setLayout (new GridLayout (4, 1, 0, GAP));

        deviceButtonContainer.add (this.addButton);
        deviceButtonContainer.add (this.removeButton);
        deviceButtonContainer.add (this.configButton);
        deviceButtonContainer.add (refreshButton);

        this.controllerList.setMinimumSize (new Dimension (300, 200));
        final JScrollPane controllerListPane = new JScrollPane (this.controllerList);

        final JPanel controllerConfigurationPane = new JPanel (new BorderLayout ());
        controllerConfigurationPane.add (controllerListPane, BorderLayout.CENTER);
        controllerConfigurationPane.add (new JLabel ("Controller:"), BorderLayout.NORTH);

        controllerConfigurationPane.add (deviceButtonContainer, BorderLayout.EAST);

        final JScrollPane loggingTextAreaPane = new JScrollPane (this.loggingTextArea);
        final JLabel loggingAreaLabel = new JLabel ("Logging:");
        this.createDefaultMenuItems (this.loggingTextArea);
        final JPanel loggingPane = new JPanel (new BorderLayout ());
        loggingPane.setBorder (new EmptyBorder (GAP, 0, 0, 0));
        loggingPane.add (loggingTextAreaPane, BorderLayout.CENTER);
        loggingPane.add (loggingAreaLabel, BorderLayout.NORTH);

        final JPanel root = new JPanel (new BorderLayout ());
        root.setBorder (new EmptyBorder (GAP, GAP, GAP, GAP));
        root.add (loggingPane, BorderLayout.CENTER);
        root.add (controllerConfigurationPane, BorderLayout.NORTH);

        this.add (root);
        this.pack ();
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

        System.exit (0);
    }


    protected void updateTitle ()
    {
        final StringBuilder sb = new StringBuilder ("DrivenByMoss 4 Reaper");

        // TODO Update to Java 9
        final Package p = Package.getPackage ("de.mossgrabers.reaper");
        if (p != null)
        {
            final String implementationVersion = p.getImplementationVersion ();
            if (implementationVersion != null)
                sb.append (" v").append (implementationVersion);
        }

        this.setTitle (sb.toString ());
    }


    /**
     * Configures and shows the stage.
     *
     * @param stage The stage to start
     */
    protected void showStage (final JFrame stage)
    {
        stage.setMinimumSize (new Dimension (840, 500));

        final URL url = ClassLoader.getSystemResource ("images/AppIcon.gif");
        if (url != null)
        {
            final ImageIcon imageIcon = new ImageIcon (url);
            this.setIconImage (imageIcon.getImage ());
        }

        if (!SystemTray.isSupported ())
            stage.setVisible (true);
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

        this.mainConfiguration.restoreStagePlacement (this);

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
            this.mainConfiguration.storeStagePlacement (this);
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

        try
        {
            Midi.readDeviceMetadata ();
        }
        catch (final MidiUnavailableException ex)
        {
            this.logModel.error ("Midi not available.", ex);
        }

        this.instanceManager.load (this.mainConfiguration);
        final DefaultListModel<IControllerInstance> items = (DefaultListModel<IControllerInstance>) this.controllerList.getModel ();
        items.clear ();

        for (final IControllerInstance instance: this.instanceManager.getInstances ())
            items.addElement (instance);

        this.updateWidgetStates ();

        this.startControllers ();
    }


    private void updateWidgetStates ()
    {
        final boolean isEmpty = this.controllerList.getModel ().getSize () == 0;
        boolean hasSelection = this.controllerList.getSelectedIndex () != -1;
        if (!hasSelection && !isEmpty)
        {
            this.controllerList.setSelectedIndex (0);
            hasSelection = true;
        }

        this.configButton.setEnabled (hasSelection);
        this.removeButton.setEnabled (hasSelection);
    }


    /**
     * Start all configured controllers.
     */
    private void startControllers ()
    {
        this.instanceManager.startAll ();
        SafeRunLater.execute (this::sendRefreshCommand);
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
    public void sendOSC (final String command, final Object value)
    {
        if (value == null)
            this.processNoArg (command);
        else if (value instanceof String)
            this.processStringArg (command, (String) value);
        else if (value instanceof Integer)
            this.processIntArg (command, ((Integer) value).intValue ());
        else if (value instanceof Double)
        {
            final Double doubleValue = (Double) value;
            if (value.toString ().endsWith (".0"))
                this.processIntArg (command, doubleValue.intValue ());
            else
                this.processDoubleArg (command, doubleValue.doubleValue ());
        }
        else if (value instanceof Boolean)
            this.processIntArg (command, ((Boolean) value).booleanValue () ? 1 : 0);
        else
            this.logModel.info ("Unsupported type: " + value.getClass ().toString ());
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     */
    public native void processNoArg (final String command);


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     * @param value A string value
     */
    public native void processStringArg (final String command, final String value);


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     * @param value An integer value
     */
    public native void processIntArg (final String command, final int value);


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     * @param value A double value
     */
    public native void processDoubleArg (final String command, final double value);


    /** {@inheritDoc} */
    @Override
    public void invokeAction (final String id)
    {
        this.sendOSC ("/action_ex", id);
    }


    /** {@inheritDoc} */
    @Override
    public void invokeAction (final int id)
    {
        this.sendOSC ("/action", Integer.valueOf (id));
    }


    private void removeController ()
    {
        final int selectedIndex = this.controllerList.getSelectionModel ().getLeadSelectionIndex ();
        if (selectedIndex < 0)
            return;
        ((DefaultListModel<IControllerInstance>) this.controllerList.getModel ()).remove (selectedIndex);
        this.instanceManager.remove (selectedIndex);
        this.updateWidgetStates ();
    }


    private void editController ()
    {
        final int selectedIndex = this.controllerList.getSelectionModel ().getLeadSelectionIndex ();
        if (selectedIndex < 0)
            return;

        this.instanceManager.edit (selectedIndex);
        this.restartControllers ();
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
    }


    private void sendRefreshCommand ()
    {
        this.sendOSC ("/refresh", null);
    }


    private void configureAddButton (final JButton addButton)
    {
        final JPopupMenu popup = new JPopupMenu ();
        final IControllerDefinition [] definitions = this.instanceManager.getDefinitions ();
        for (int i = 0; i < definitions.length; i++)
        {
            final JMenuItem item = new JMenuItem (definitions[i].toString ());
            final int index = i;
            item.addActionListener (event -> {
                if (this.instanceManager.isInstantiated (index))
                {
                    this.logModel.info ("Only one instance of a controller type is supported!");
                    return;
                }
                final IControllerInstance inst = this.instanceManager.instantiate (index);
                ((DefaultListModel<IControllerInstance>) this.controllerList.getModel ()).addElement (inst);
                this.controllerList.setSelectedValue (inst, true);
                this.updateWidgetStates ();
                inst.start ();
                this.sendRefreshCommand ();
            });
            popup.add (item);
        }
        addButton.addActionListener (event -> popup.show (addButton, 0, addButton.getHeight ()));
    }


    private void createDefaultMenuItems (final JTextArea t)
    {
        final JPopupMenu popup = new JPopupMenu ();

        final JMenuItem copy = new JMenuItem ("Copy");
        copy.addActionListener (e -> t.copy ());
        popup.add (copy);

        final JMenuItem clear = new JMenuItem ("Clear");
        clear.addActionListener (e -> this.logModel.clearLogMessage ());
        popup.add (clear);

        popup.addSeparator ();

        final JMenuItem selectAll = new JMenuItem ("Select All");
        selectAll.addActionListener (e -> t.selectAll ());
        popup.add (selectAll);

        t.setComponentPopupMenu (popup);
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
        this.setVisible (true);
        this.toFront ();
    }
}
