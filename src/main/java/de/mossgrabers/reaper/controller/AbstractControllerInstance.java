// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.reaper.communication.MessageParser;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.framework.configuration.SettingsUI;
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
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerInstance implements IControllerInstance
{
    protected final IControllerDefinition controllerDefinition;
    protected final LogModel              logModel;
    protected final WindowManager         windowManager;
    protected final MessageSender         sender;
    protected final IniFiles              iniFiles;

    protected HostImpl                    host;
    protected SettingsUI                  settingsUI;
    protected ReaperSetupFactory          setupFactory;
    protected IControllerSetup<?, ?>      controllerSetup;
    protected PropertiesEx                controllerConfiguration = new PropertiesEx ();

    protected MessageParser               oscParser;

    private boolean                       isRunning               = false;
    private final Object                  startSync               = new Object ();
    private List<JFrame>                  simulators              = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param controllerDefinition The controller definition
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public AbstractControllerInstance (final IControllerDefinition controllerDefinition, final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        this.controllerDefinition = controllerDefinition;
        this.logModel = logModel;
        this.windowManager = windowManager;
        this.sender = sender;
        this.iniFiles = iniFiles;
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

            this.host = new HostImpl (this.logModel, this.windowManager);

            this.loadConfiguration ();
            this.settingsUI = new SettingsUI (this.logModel, this.controllerConfiguration, this.controllerDefinition.getNumMidiInPorts (), this.controllerDefinition.getNumMidiOutPorts (), this.controllerDefinition.getMidiDiscoveryPairs (OperatingSystem.get ()));
            this.settingsUI.initMIDI ();

            if (!this.isEnabled ())
            {
                this.logModel.info (this.controllerDefinition.toString () + ": Deactivated.");
                return;
            }

            this.logModel.info (this.controllerDefinition.toString () + ": Starting...");

            final UsbMatcher matcher = this.controllerDefinition.claimUSBDevice ();
            if (matcher != null)
                this.host.addUSBDeviceInfo (matcher);

            this.setupFactory = new ReaperSetupFactory (this.iniFiles, this.sender, this.host, this.logModel, this.settingsUI.getSelectedMidiInputs (), this.settingsUI.getSelectedMidiOutputs ());
            this.controllerSetup = this.createControllerSetup (this.setupFactory);

            SafeRunLater.execute (this.logModel, this::delayedStart);
        }
    }


    /** {@inheritDoc} */
    @Override
    public SettingsUI getSettingsUI ()
    {
        return this.settingsUI;
    }


    protected abstract IControllerSetup<?, ?> createControllerSetup (final ReaperSetupFactory setupFactory);


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        synchronized (this.startSync)
        {
            if (!this.isRunning)
                return;

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

            this.logModel.info ("Closing midi connections...");
            if (this.setupFactory != null)
                this.setupFactory.cleanup ();

            this.isRunning = false;
        }
    }


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
        if (this.oscParser != null)
            this.oscParser.parseOSC (address, argument);
    }


    /** {@inheritDoc} */
    @Override
    public void edit ()
    {
        new ConfigurationDialog (this.logModel, this.windowManager.getMainFrame (), this.settingsUI).setVisible (true);
        this.storeConfiguration ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.settingsUI != null && this.settingsUI.isEnabled ();
    }


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean isEnabled)
    {
        this.settingsUI.setEnabled (isEnabled);
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void simulateUI (final JFrame frame)
    {
        if (this.controllerSetup == null)
            return;

        if (this.simulators.isEmpty ())
            this.controllerSetup.getSurfaces ().forEach (surface -> this.simulators.add (new SimulatorWindow (surface, this.toString ())));

        this.simulators.forEach (simulator -> simulator.setVisible (true));
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
        this.settingsUI.init ();

        this.oscParser = new MessageParser (this.controllerSetup);

        this.settingsUI.flush ();

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
            this.settingsUI.store (this.controllerConfiguration);
            this.controllerConfiguration.store (writer, "");
        }
        catch (final IOException ex)
        {
            this.logModel.error ("Could not load controller configuration file.", ex);
        }
    }
}
