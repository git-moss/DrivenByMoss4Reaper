// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IButton;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;


/**
 * Abstract base class for controller extensions.
 *
 * @param <C> The type of the configuration
 * @param <S> The type of the control surface
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerSetup<S extends IControlSurface<C>, C extends Configuration> implements IControllerSetup<S, C>
{
    protected final List<S>       surfaces    = new ArrayList<> ();
    protected final IHost         host;
    protected final ISettingsUI   globalSettings;
    protected final ISettingsUI   documentSettings;
    protected final ISetupFactory factory;

    protected Scales              scales;
    protected IModel              model;
    protected C                   configuration;
    protected ColorManager        colorManager;
    protected IValueChanger       valueChanger;
    protected Modes               currentMode = null;


    /**
     * Constructor.
     *
     * @param factory The factory
     * @param host The host
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    protected AbstractControllerSetup (final ISetupFactory factory, final IHost host, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        this.factory = factory;
        this.host = host;
        this.globalSettings = globalSettings;
        this.documentSettings = documentSettings;
    }


    /** {@inheritDoc} */
    @Override
    public S getSurface ()
    {
        return this.surfaces.get (0);
    }


    /** {@inheritDoc} */
    @Override
    public S getSurface (final int index)
    {
        return this.surfaces.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public IModel getModel ()
    {
        return this.model;
    }


    /** {@inheritDoc} */
    @Override
    public C getConfiguration ()
    {
        return this.configuration;
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        this.initConfiguration ();
        this.createScales ();
        this.createModel ();
        this.createSurface ();
        this.createModes ();
        this.createObservers ();
        this.createViews ();
        this.registerTriggerCommands ();
        this.registerContinuousCommands ();
        if (this.model != null)
            this.model.ensureClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.configuration.clearSettingObservers ();
        for (final S surface: this.surfaces)
            surface.shutdown ();
        this.host.println ("Exited.");
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        for (final S surface: this.surfaces)
            surface.flush ();

        this.updateButtons ();
    }


    /**
     * Update all button LEDs, except the ones controlled by the views. Refreshed on flush.
     */
    @Deprecated
    protected void updateButtons ()
    {
        // Overwrite to update button LEDs
    }


    /**
     * Initialize the configuration settings.
     */
    protected void initConfiguration ()
    {
        this.configuration.init (this.globalSettings, this.documentSettings);
    }


    /**
     * Create the scales object.
     */
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 100, 8, 8);
    }


    /**
     * Create the model.
     */
    protected abstract void createModel ();


    /**
     * Create the surface.
     */
    protected abstract void createSurface ();


    /**
     * Create the modes.
     */
    protected void createModes ()
    {
        // Intentionally empty
    }


    /**
     * Create the views.
     */
    protected void createViews ()
    {
        // Intentionally empty
    }


    /**
     * Create the listeners.
     */
    protected void createObservers ()
    {
        // Intentionally empty
    }


    /**
     * Create and register the trigger commands.
     */
    protected void registerTriggerCommands ()
    {
        // Intentionally empty
    }


    /**
     * Create and register the continuous commands.
     */
    protected void registerContinuousCommands ()
    {
        // Intentionally empty
    }


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC for the first
     * device.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     */
    @Deprecated
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final TriggerCommand command)
    {
        this.addTriggerCommand (commandID, midiCC, command, 0);
    }


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     * @param deviceIndex The index of the device
     */
    @Deprecated
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final TriggerCommand command, final int deviceIndex)
    {
        final S surface = this.surfaces.get (deviceIndex);
        surface.getViewManager ().registerTriggerCommand (commandID, command);
        surface.assignTriggerCommand (midiCC, commandID);
    }


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param command The command to register
     */
    @Deprecated
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final int midiChannel, final TriggerCommand command)
    {
        this.addTriggerCommand (commandID, midiCC, midiChannel, command, 0);
    }


    /**
     * Register a (global) trigger command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param command The command to register
     * @param deviceIndex The index of the device
     */
    @Deprecated
    protected void addTriggerCommand (final TriggerCommandID commandID, final int midiCC, final int midiChannel, final TriggerCommand command, final int deviceIndex)
    {
        final S surface = this.surfaces.get (deviceIndex);
        surface.getViewManager ().registerTriggerCommand (commandID, command);
        surface.assignTriggerCommand (midiChannel, midiCC, commandID);
    }


    /**
     * Create a hardware button on/off proxy on controller device 1, bind a trigger command to it
     * and bind it to a MIDI CC on MIDI channel 1. State colors are ON and HI.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     */
    protected void setupButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue, final BooleanSupplier supplier)
    {
        this.setupButton (buttonID, label, command, midiValue, () -> supplier.getAsBoolean () ? 1 : 0, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
    }


    /**
     * Create a hardware button on/off proxy on controller device 1, bind a trigger command to it
     * and bind it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     * @param colorIdOn The color ID for on state
     * @param colorIdHi The color ID for off state
     */
    protected void setupButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue, final BooleanSupplier supplier, final String colorIdOn, final String colorIdHi)
    {
        this.setupButton (buttonID, label, command, midiValue, () -> supplier.getAsBoolean () ? 1 : 0, colorIdOn, colorIdHi);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1. The button has an on/off state.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     */
    protected void setupButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue)
    {
        this.setupButton (buttonID, label, command, midiValue, (IntSupplier) null, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiValaue The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void setupButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValaue, final IntSupplier supplier, final String... colorIds)
    {
        this.setupButton (0, buttonID, label, command, midiValaue, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param deviceIndex The index of the device
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void setupButton (final int deviceIndex, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue, final IntSupplier supplier, final String... colorIds)
    {
        this.setupButton (this.surfaces.get (deviceIndex), buttonID, label, command, midiValue, supplier, colorIds);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     */
    protected void setupButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue, final IntSupplier supplier)
    {
        this.setupButton (0, buttonID, label, command, midiValue, supplier);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param deviceIndex The index of the device
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     */
    protected void setupButton (final int deviceIndex, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue, final IntSupplier supplier)
    {
        this.setupButton (this.surfaces.get (deviceIndex), buttonID, label, command, midiValue, supplier);
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void setupButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue, final IntSupplier supplier, final String... colorIds)
    {
        final IButton button = surface.createButton (buttonID, label).bind (command);
        button.bind (surface.getInput (), this.getTriggerBindType (), midiValue);
        final IntSupplier supp = supplier == null ? () -> button.isPressed () ? 1 : 0 : supplier;
        button.addLight (surface.createLight ( () -> {
            final int state = supp.getAsInt ();
            return this.colorManager.getColor (state < 0 ? ColorManager.BUTTON_STATE_OFF : colorIds[state]);
        }, color -> surface.setTrigger (midiValue, color)));
    }


    /**
     * Create a hardware button proxy on controller device 1, bind a trigger command to it and bind
     * it to a MIDI CC on MIDI channel 1.
     *
     * @param surface The control surface
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light, the state is the color value
     *            of the device
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     */
    protected void setupButton (final S surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiValue, final IntSupplier supplier)
    {
        final IButton button = surface.createButton (buttonID, label).bind (command);
        button.bind (surface.getInput (), this.getTriggerBindType (), midiValue);
        final IntSupplier supp = supplier == null ? () -> button.isPressed () ? 1 : 0 : supplier;
        button.addLight (surface.createLight (supp, color -> surface.setTrigger (midiValue, color)));
    }


    /**
     * Get the default bind type for triggering buttons.
     *
     * @return The default, returns CC as default
     */
    protected BindType getTriggerBindType ()
    {
        return BindType.CC;
    }


    /**
     * Create a hardware fader proxy on controller device 1, bind a continuous command to it and
     * bind it to a MIDI CC on MIDI channel 1.
     *
     * @param label The label of the fader
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     * @param bindType The MIDI bind type
     */
    protected void setupFader (final String label, final ContinuousCommand command, final BindType bindType, final int midiValue)
    {
        this.setupFader (this.getSurface (), label, command, bindType, midiValue);
    }


    /**
     * Create a hardware fader proxy on a controller, bind a continuous command to it and bind it to
     * a MIDI CC on MIDI channel 1.
     *
     * @param surface The control surface
     * @param label The label of the fader
     * @param midiValue The MIDI CC or note
     * @param command The command to bind
     * @param bindType The MIDI bind type
     */
    protected void setupFader (final S surface, final String label, final ContinuousCommand command, final BindType bindType, final int midiValue)
    {
        surface.createFader (label).bind (command).bind (surface.getInput (), bindType, midiValue);
    }


    /**
     * Register a (global) continuous command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param midiChannel The midi channel to assign to
     * @param command The command to register
     */
    @Deprecated
    protected void addContinuousCommand (final ContinuousCommandID commandID, final int midiCC, final int midiChannel, final ContinuousCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerContinuousCommand (commandID, command);
        surface.assignContinuousCommand (midiChannel, midiCC, commandID);
    }


    /**
     * Register a (global) continuous command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param midiCC The midi CC
     * @param command The command to register
     */
    @Deprecated
    protected void addContinuousCommand (final ContinuousCommandID commandID, final int midiCC, final ContinuousCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerContinuousCommand (commandID, command);
        surface.assignContinuousCommand (midiCC, commandID);
    }


    /**
     * Register a (global) note command for all views and assign it to a MIDI CC.
     *
     * @param commandID The ID of the command to register
     * @param note The midi note
     * @param command The command to register
     */
    @Deprecated
    protected void addNoteCommand (final TriggerCommandID commandID, final int note, final TriggerCommand command)
    {
        final S surface = this.surfaces.get (0);
        surface.getViewManager ().registerNoteCommand (commandID, command);
        surface.assignNoteCommand (note, commandID);
    }


    /**
     * Update the DAW indications for the given mode.
     *
     * @param mode The new mode
     */
    protected abstract void updateIndication (final Modes mode);


    /**
     * Register observers for all scale settings. Stores the changed value in the scales object and
     * updates the actives views note mapping.
     *
     * @param conf The configuration
     */
    protected void createScaleObservers (final C conf)
    {
        conf.addSettingObserver (AbstractConfiguration.SCALES_SCALE, () -> {
            this.scales.setScaleByName (conf.getScale ());
            this.updateViewNoteMapping ();
        });
        conf.addSettingObserver (AbstractConfiguration.SCALES_BASE, () -> {
            this.scales.setScaleOffsetByName (conf.getScaleBase ());
            this.updateViewNoteMapping ();
        });
        conf.addSettingObserver (AbstractConfiguration.SCALES_IN_KEY, () -> {
            this.scales.setChromatic (!conf.isScaleInKey ());
            this.updateViewNoteMapping ();
        });
        conf.addSettingObserver (AbstractConfiguration.SCALES_LAYOUT, () -> {
            this.scales.setScaleLayoutByName (conf.getScaleLayout ());
            this.updateViewNoteMapping ();
        });
    }


    /**
     * Update the active views note mapping.
     */
    protected void updateViewNoteMapping ()
    {
        for (final S surface: this.surfaces)
        {
            final View view = surface.getViewManager ().getActiveView ();
            if (view != null)
                view.updateNoteMapping ();
        }
    }
}
