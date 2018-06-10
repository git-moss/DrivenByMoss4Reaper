// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.framework.configuration.SettingsUI;
import de.mossgrabers.reaper.framework.daw.HostImpl;
import de.mossgrabers.transformator.ConfigurationDialog;
import de.mossgrabers.transformator.communication.MessageParser;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.util.LogModel;
import de.mossgrabers.transformator.util.PropertiesEx;

import javafx.application.Platform;
import javafx.stage.Window;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Abstract implementation for a controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerInstance implements IControllerInstance
{
    protected final LogModel              logModel;
    protected final Window                window;
    protected final MessageSender         sender;
    protected final IControllerDefinition controllerDefinition;

    protected HostImpl                    host;
    protected SettingsUI                  settingsUI;
    protected ReaperSetupFactory          setupFactory;
    protected IControllerSetup            controllerSetup;
    protected PropertiesEx                controllerConfiguration = new PropertiesEx ();

    protected MessageParser               oscParser;

    private boolean                       isRunning               = false;
    private final Object                  startSync               = new Object ();


    /**
     * Constructor.
     *
     * @param controllerDefinition The controller definition
     * @param logModel The logging model
     * @param window The owner window for the configuration dialog
     * @param sender The sender
     */
    public AbstractControllerInstance (final IControllerDefinition controllerDefinition, final LogModel logModel, final Window window, final MessageSender sender)
    {
        this.logModel = logModel;
        this.window = window;
        this.sender = sender;
        this.controllerDefinition = controllerDefinition;
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

            this.logModel.addLogMessage ("Starting controller '" + this.controllerDefinition.toString () + "'");

            this.host = new HostImpl (this.logModel, this.window, this.sender);
            this.settingsUI = new SettingsUI (this.controllerDefinition.getNumMidiInPorts (), this.controllerDefinition.getNumMidiOutPorts (), this.controllerDefinition.getMidiDiscoveryPairs (OperatingSystem.get ()));

            final File configFile = new File (this.getFileName ());
            if (configFile.exists ())
            {
                try (final FileReader reader = new FileReader (configFile))
                {
                    this.controllerConfiguration.load (reader);
                }
                catch (final IOException ex)
                {
                    this.logModel.addLogMessage ("Could not load controller configuration file: " + ex.getLocalizedMessage ());
                }
            }

            final UsbMatcher matcher = this.controllerDefinition.claimUSBDevice ();
            if (matcher != null)
                this.host.addUSBDeviceInfo (matcher);

            this.settingsUI.load (this.controllerConfiguration);

            this.setupFactory = new ReaperSetupFactory (this.sender, this.host, this.logModel, this.settingsUI.getSelectedMidiInputs (), this.settingsUI.getSelectedMidiOutputs ());
            this.controllerSetup = this.createControllerSetup (this.setupFactory);

            Platform.runLater ( () -> {
                this.controllerSetup.init ();

                this.settingsUI.load (this.controllerConfiguration);

                final Configuration configuration = this.controllerSetup.getConfiguration ();
                configuration.addSettingObserver (AbstractConfiguration.QUANTIZE_AMOUNT, () -> this.sender.sendOSC ("/quantize/strength", Integer.valueOf (configuration.getQuantizeAmount ())));

                this.oscParser = new MessageParser (this.controllerSetup);

                this.settingsUI.flush ();

                this.host.scheduleTask (this.controllerSetup::startup, 1000);

                this.isRunning = true;
            });
        }
    }


    protected abstract IControllerSetup createControllerSetup (final ReaperSetupFactory setupFactory);


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        synchronized (this.startSync)
        {
            if (!this.isRunning)
                return;

            this.logModel.addLogMessage ("Closing controller...");
            if (this.controllerSetup != null)
                this.controllerSetup.exit ();

            if (this.host != null)
            {
                this.host.shutdown ();
                this.logModel.addLogMessage ("Release resources...");
                this.host.releaseUsbDevices ();
                this.host.releaseOSC ();
            }

            this.logModel.addLogMessage ("Closing midi connections...");
            if (this.setupFactory != null)
                this.setupFactory.cleanup ();

            this.isRunning = false;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        synchronized (this.startSync)
        {
            if (!this.isRunning)
                return;

            if (this.controllerSetup != null)
                this.controllerSetup.flush ();
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
        new ConfigurationDialog (this.logModel, this.window, this.settingsUI).showAndWait ();

        try (final FileWriter writer = new FileWriter (this.getFileName ()))
        {
            this.settingsUI.store (this.controllerConfiguration);
            this.controllerConfiguration.store (writer, "");
        }
        catch (final IOException ex)
        {
            this.logModel.addLogMessage ("Could not load controller configuration file: " + ex.getLocalizedMessage ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return this.controllerDefinition.toString ();
    }


    private String getFileName ()
    {
        return "DrivenByMoss4Reaper-" + this.controllerDefinition.getHardwareModel ().replace (' ', '-') + ".config";
    }
}
