// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.TestCallback;
import de.mossgrabers.reaper.communication.MessageParser;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.framework.configuration.DocumentSettingsUI;
import de.mossgrabers.reaper.framework.configuration.GlobalSettingsUI;
import de.mossgrabers.reaper.framework.daw.HostImpl;
import de.mossgrabers.reaper.ui.SimulatorWindow;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.dialog.ConfigurationDialog;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.JFrame;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Abstract implementation for a controller instance.
 *
 * @param <C> The type of the configuration
 * @param <S> The type of the control surface
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerInstance<S extends IControlSurface<C>, C extends Configuration> implements IControllerInstance, TestCallback
{
    protected final IControllerDefinition controllerDefinition;
    protected final LogModel              logModel;
    protected final WindowManager         windowManager;
    protected final MessageSender         sender;
    protected final IniFiles              iniFiles;

    protected HostImpl                    host;
    protected GlobalSettingsUI            globalSettingsUI;
    protected final DocumentSettingsUI    documentSettingsUI;
    protected ReaperSetupFactory          setupFactory;
    protected IControllerSetup<?, ?>      controllerSetup;
    protected PropertiesEx                controllerConfiguration = new PropertiesEx ();

    protected MessageParser               oscParser;

    private boolean                       isRunning               = false;
    private final Object                  startSync               = new Object ();
    private final List<JFrame>            simulators              = new ArrayList<> ();
    private ConfigurationDialog           configurationDialog;


    /**
     * Constructor.
     *
     * @param controllerDefinition The controller definition
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    protected AbstractControllerInstance (final IControllerDefinition controllerDefinition, final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        this.controllerDefinition = controllerDefinition;
        this.logModel = logModel;
        this.windowManager = windowManager;
        this.sender = sender;
        this.iniFiles = iniFiles;

        this.documentSettingsUI = new DocumentSettingsUI (this.logModel);
    }


    /** {@inheritDoc} */
    @Override
    public IControllerDefinition getDefinition ()
    {
        return this.controllerDefinition;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRunning ()
    {
        synchronized (this.startSync)
        {
            return this.isRunning;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void start ()
    {
        synchronized (this.startSync)
        {
            if (this.isRunning)
                return;

            this.host = new HostImpl (this.logModel, this.windowManager, this);

            this.loadConfiguration ();
            this.documentSettingsUI.clearWidgets ();
            this.globalSettingsUI = new GlobalSettingsUI (this.sender, this.logModel, this.controllerConfiguration, this.controllerDefinition.getNumMidiInPorts (), this.controllerDefinition.getNumMidiOutPorts (), this.controllerDefinition.getMidiDiscoveryPairs (OperatingSystem.get ()));
            this.globalSettingsUI.initMIDI ();

            if (!this.isEnabled ())
            {
                this.logModel.info (this.controllerDefinition.toString () + ": Deactivated.");
                return;
            }

            this.logModel.info (this.controllerDefinition.toString () + ": Starting...");

            final UsbMatcher matcher = this.controllerDefinition.claimUSBDevice ();
            if (matcher != null)
                this.host.addUSBDeviceInfo (matcher);

            this.setupFactory = new ReaperSetupFactory (this.iniFiles, this.sender, this.host, this.logModel, this.globalSettingsUI.getSelectedMidiInputs (), this.globalSettingsUI.getSelectedMidiOutputs ());
            this.controllerSetup = this.createControllerSetup (this.setupFactory);

            SafeRunLater.execute (this.logModel, this::delayedStart);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void restart ()
    {
        synchronized (this.startSync)
        {
            this.stop ();
            this.start ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        synchronized (this.startSync)
        {
            if (!this.isRunning)
                return;

            if (this.globalSettingsUI.isDirty ())
            {
                this.logModel.info ("Storing configuration...");
                this.storeConfiguration ();
            }

            this.logModel.info ("Closing controller...");
            if (this.controllerSetup != null)
                this.controllerSetup.exit ();

            if (this.host != null)
            {
                this.host.shutdown ();
                this.logModel.info ("Release resources...");
                this.host.releaseUsbDevices ();
                this.host.releaseOSC ();
            }

            this.logModel.info ("Closing MIDI connections...");
            if (this.setupFactory != null)
                this.setupFactory.cleanup ();

            this.isRunning = false;
        }
    }


    /** {@inheritDoc} */
    @Override
    public GlobalSettingsUI getGlobalSettingsUI ()
    {
        return this.globalSettingsUI;
    }


    /** {@inheritDoc} */
    @Override
    public DocumentSettingsUI getDocumentSettingsUI ()
    {
        return this.documentSettingsUI;
    }


    /**
     * Create an instance of the related controller setup.
     *
     * @param setupFactory The setup factory
     * @return The instance
     */
    protected abstract IControllerSetup<S, C> createControllerSetup (final ReaperSetupFactory setupFactory);


    /**
     * Get the host implementation.
     *
     * @return The host implementation
     */
    public HostImpl getHost ()
    {
        return this.host;
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        synchronized (this.startSync)
        {
            if (!this.isEnabled () || !this.isRunning)
                return;

            if (this.controllerSetup != null)
                this.controllerSetup.flush ();

            this.simulators.forEach (JFrame::repaint);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void parse (final String address, final String argument)
    {
        if (this.oscParser == null)
            return;

        if ("/action/select".equals (address))
        {
            if (this.configurationDialog != null)
                this.configurationDialog.setAction (argument);
        }
        else
            this.oscParser.parseOSC (address, argument);
    }


    /** {@inheritDoc} */
    @Override
    public void edit ()
    {
        this.configurationDialog = new ConfigurationDialog (this.logModel, this.windowManager.getMainFrame (), this.globalSettingsUI);
        this.configurationDialog.showDialog ();
        this.configurationDialog = null;
        this.storeConfiguration ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.globalSettingsUI != null && this.globalSettingsUI.isEnabled ();
    }


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean isEnabled)
    {
        this.globalSettingsUI.setEnabled (isEnabled);
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void simulateUI ()
    {
        if (this.controllerSetup == null)
            return;

        if (this.simulators.isEmpty ())
            this.controllerSetup.getSurfaces ().forEach (surface -> this.simulators.add (new SimulatorWindow (surface, this.toString (), true)));

        this.simulators.forEach (simulator -> simulator.setVisible (true));
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void testUI ()
    {
        if (this.controllerSetup == null)
            return;

        this.controllerSetup.test (this);
    }


    /** {@inheritDoc} */
    @Override
    public void startTesting ()
    {
        this.logModel.info ("Enable testing.");
        Actions.setTesting (true);
    }


    /** {@inheritDoc} */
    @Override
    public void endTesting ()
    {
        this.logModel.info ("Testing finished.");
        Actions.setTesting (false);
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return this.controllerDefinition.toString ();
    }


    private File getFileName ()
    {
        return new File (this.iniFiles.getIniPath (), "DrivenByMoss4Reaper-" + this.controllerDefinition.getHardwareModel ().replace (' ', '-').replace ('/', '-') + ".config");
    }


    private void delayedStart ()
    {
        this.controllerSetup.init ();

        // 2nd load to also load the settings
        this.globalSettingsUI.init ();

        this.oscParser = new MessageParser (this.controllerSetup);

        this.globalSettingsUI.flush ();

        this.host.scheduleTask ( () -> {
            try
            {
                this.controllerSetup.startup ();
            }
            catch (final RuntimeException ex)
            {
                this.logModel.error (this.controllerDefinition.toString () + ": Could not start controller.", ex);
            }
        }, 1000);

        this.isRunning = true;

        this.logModel.info (this.controllerDefinition.toString () + ": Running.");
    }


    private void loadConfiguration ()
    {
        final File configFile = this.getFileName ();
        if (!configFile.exists ())
            return;

        try (final FileReader reader = new FileReader (configFile))
        {
            this.controllerConfiguration.load (reader);
        }
        catch (final IOException ex)
        {
            this.logModel.error ("Could not load controller configuration file.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void storeConfiguration ()
    {
        try (final FileWriter writer = new FileWriter (this.getFileName ()))
        {
            this.globalSettingsUI.store (this.controllerConfiguration);
            this.controllerConfiguration.store (writer, "");
        }
        catch (final IOException ex)
        {
            this.logModel.error ("Could not save controller configuration file.", ex);
        }
    }
}
