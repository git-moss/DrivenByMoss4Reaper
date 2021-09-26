// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.ableton.push.Push1ControllerInstance;
import de.mossgrabers.reaper.controller.ableton.push.Push2ControllerInstance;
import de.mossgrabers.reaper.controller.akai.acvs.ACVSLiveControllerInstance;
import de.mossgrabers.reaper.controller.akai.apc.APC40mkIControllerInstance;
import de.mossgrabers.reaper.controller.akai.apc.APC40mkIIControllerInstance;
import de.mossgrabers.reaper.controller.akai.apcmini.APCminiControllerInstance;
import de.mossgrabers.reaper.controller.akai.fire.FireControllerInstance;
import de.mossgrabers.reaper.controller.arturia.beatstep.BeatstepControllerInstance;
import de.mossgrabers.reaper.controller.generic.GenericFlexiControllerInstance;
import de.mossgrabers.reaper.controller.mackie.hui.HUI1ControllerInstance;
import de.mossgrabers.reaper.controller.mackie.hui.HUI2ControllerInstance;
import de.mossgrabers.reaper.controller.mackie.hui.HUI3ControllerInstance;
import de.mossgrabers.reaper.controller.mackie.mcu.MCU1ControllerInstance;
import de.mossgrabers.reaper.controller.mackie.mcu.MCU2ControllerInstance;
import de.mossgrabers.reaper.controller.mackie.mcu.MCU3ControllerInstance;
import de.mossgrabers.reaper.controller.mackie.mcu.MCU4ControllerInstance;
import de.mossgrabers.reaper.controller.ni.kontrol.mki.KontrolMkIS25ControllerInstance;
import de.mossgrabers.reaper.controller.ni.kontrol.mki.KontrolMkIS49ControllerInstance;
import de.mossgrabers.reaper.controller.ni.kontrol.mki.KontrolMkIS61ControllerInstance;
import de.mossgrabers.reaper.controller.ni.kontrol.mki.KontrolMkIS88ControllerInstance;
import de.mossgrabers.reaper.controller.ni.kontrol.mkii.KontrolProtocolV1ControllerInstance;
import de.mossgrabers.reaper.controller.ni.kontrol.mkii.KontrolProtocolV2ControllerInstance;
import de.mossgrabers.reaper.controller.ni.maschine.jam.MaschineJamControllerInstance;
import de.mossgrabers.reaper.controller.ni.maschine.mk3.MaschineMikroMk3ControllerInstance;
import de.mossgrabers.reaper.controller.ni.maschine.mk3.MaschineMk2ControllerInstance;
import de.mossgrabers.reaper.controller.ni.maschine.mk3.MaschineMk3ControllerInstance;
import de.mossgrabers.reaper.controller.ni.maschine.mk3.MaschinePlusControllerInstance;
import de.mossgrabers.reaper.controller.ni.maschine.mk3.MaschineStudioControllerInstance;
import de.mossgrabers.reaper.controller.novation.launchkey.LaunchkeyMiniMk3ControllerInstance;
import de.mossgrabers.reaper.controller.novation.launchkey.LaunchkeyMk3ControllerInstance;
import de.mossgrabers.reaper.controller.novation.launchpad.LaunchpadMiniMkIIIControllerInstance;
import de.mossgrabers.reaper.controller.novation.launchpad.LaunchpadMkIIControllerInstance;
import de.mossgrabers.reaper.controller.novation.launchpad.LaunchpadProControllerInstance;
import de.mossgrabers.reaper.controller.novation.launchpad.LaunchpadProMk3ControllerInstance;
import de.mossgrabers.reaper.controller.novation.launchpad.LaunchpadXControllerInstance;
import de.mossgrabers.reaper.controller.novation.sl.SLMkIControllerInstance;
import de.mossgrabers.reaper.controller.novation.sl.SLMkIIControllerInstance;
import de.mossgrabers.reaper.controller.novation.slmkiii.SLMkIIIControllerInstance;
import de.mossgrabers.reaper.controller.osc.OSCControllerInstance;
import de.mossgrabers.reaper.controller.utilities.autocolor.AutoColorInstance;
import de.mossgrabers.reaper.controller.utilities.midimonitor.MidiMonitorInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.configuration.GlobalSettingsUI;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.dialog.ProjectSettingsDialog;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;

import javax.sound.midi.MidiDevice;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Manager for all controller instances and definitions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControllerInstanceManager
{
    private static final String                               CONTROLLER_INSTANCE_TAG = "CONTROLLER_INSTANCE";
    private static final Map<IControllerDefinition, Class<?>> DEF_TO_CLASS            = new HashMap<> ();
    private static final Map<String, Class<?>>                NAME_TO_CLASS           = new HashMap<> ();

    static
    {
        DEF_TO_CLASS.put (AutoColorInstance.CONTROLLER_DEFINITION, AutoColorInstance.class);
        DEF_TO_CLASS.put (APC40mkIControllerInstance.CONTROLLER_DEFINITION, APC40mkIControllerInstance.class);
        DEF_TO_CLASS.put (APC40mkIIControllerInstance.CONTROLLER_DEFINITION, APC40mkIIControllerInstance.class);
        DEF_TO_CLASS.put (APCminiControllerInstance.CONTROLLER_DEFINITION, APCminiControllerInstance.class);
        DEF_TO_CLASS.put (BeatstepControllerInstance.CONTROLLER_DEFINITION, BeatstepControllerInstance.class);
        DEF_TO_CLASS.put (FireControllerInstance.CONTROLLER_DEFINITION, FireControllerInstance.class);
        DEF_TO_CLASS.put (GenericFlexiControllerInstance.CONTROLLER_DEFINITION, GenericFlexiControllerInstance.class);
        DEF_TO_CLASS.put (HUI1ControllerInstance.CONTROLLER_DEFINITION, HUI1ControllerInstance.class);
        DEF_TO_CLASS.put (HUI2ControllerInstance.CONTROLLER_DEFINITION, HUI2ControllerInstance.class);
        DEF_TO_CLASS.put (HUI3ControllerInstance.CONTROLLER_DEFINITION, HUI3ControllerInstance.class);
        DEF_TO_CLASS.put (KontrolMkIS25ControllerInstance.CONTROLLER_DEFINITION, KontrolMkIS25ControllerInstance.class);
        DEF_TO_CLASS.put (KontrolMkIS49ControllerInstance.CONTROLLER_DEFINITION, KontrolMkIS49ControllerInstance.class);
        DEF_TO_CLASS.put (KontrolMkIS61ControllerInstance.CONTROLLER_DEFINITION, KontrolMkIS61ControllerInstance.class);
        DEF_TO_CLASS.put (KontrolMkIS88ControllerInstance.CONTROLLER_DEFINITION, KontrolMkIS88ControllerInstance.class);
        DEF_TO_CLASS.put (KontrolProtocolV1ControllerInstance.CONTROLLER_DEFINITION, KontrolProtocolV1ControllerInstance.class);
        DEF_TO_CLASS.put (KontrolProtocolV2ControllerInstance.CONTROLLER_DEFINITION, KontrolProtocolV2ControllerInstance.class);
        DEF_TO_CLASS.put (LaunchkeyMiniMk3ControllerInstance.CONTROLLER_DEFINITION, LaunchkeyMiniMk3ControllerInstance.class);
        DEF_TO_CLASS.put (LaunchkeyMk3ControllerInstance.CONTROLLER_DEFINITION, LaunchkeyMk3ControllerInstance.class);
        DEF_TO_CLASS.put (LaunchpadMkIIControllerInstance.CONTROLLER_DEFINITION, LaunchpadMkIIControllerInstance.class);
        DEF_TO_CLASS.put (LaunchpadProControllerInstance.CONTROLLER_DEFINITION, LaunchpadProControllerInstance.class);
        DEF_TO_CLASS.put (LaunchpadXControllerInstance.CONTROLLER_DEFINITION, LaunchpadXControllerInstance.class);
        DEF_TO_CLASS.put (LaunchpadMiniMkIIIControllerInstance.CONTROLLER_DEFINITION, LaunchpadMiniMkIIIControllerInstance.class);
        DEF_TO_CLASS.put (LaunchpadProMk3ControllerInstance.CONTROLLER_DEFINITION, LaunchpadProMk3ControllerInstance.class);
        DEF_TO_CLASS.put (MaschineMikroMk3ControllerInstance.CONTROLLER_DEFINITION, MaschineMikroMk3ControllerInstance.class);
        DEF_TO_CLASS.put (MaschineMk2ControllerInstance.CONTROLLER_DEFINITION, MaschineMk2ControllerInstance.class);
        DEF_TO_CLASS.put (MaschineMk3ControllerInstance.CONTROLLER_DEFINITION, MaschineMk3ControllerInstance.class);
        DEF_TO_CLASS.put (MaschinePlusControllerInstance.CONTROLLER_DEFINITION, MaschinePlusControllerInstance.class);
        DEF_TO_CLASS.put (MaschineStudioControllerInstance.CONTROLLER_DEFINITION, MaschineStudioControllerInstance.class);
        DEF_TO_CLASS.put (MaschineJamControllerInstance.CONTROLLER_DEFINITION, MaschineJamControllerInstance.class);
        DEF_TO_CLASS.put (MidiMonitorInstance.CONTROLLER_DEFINITION, MidiMonitorInstance.class);
        DEF_TO_CLASS.put (MCU1ControllerInstance.CONTROLLER_DEFINITION, MCU1ControllerInstance.class);
        DEF_TO_CLASS.put (MCU2ControllerInstance.CONTROLLER_DEFINITION, MCU2ControllerInstance.class);
        DEF_TO_CLASS.put (MCU3ControllerInstance.CONTROLLER_DEFINITION, MCU3ControllerInstance.class);
        DEF_TO_CLASS.put (MCU4ControllerInstance.CONTROLLER_DEFINITION, MCU4ControllerInstance.class);
        DEF_TO_CLASS.put (ACVSLiveControllerInstance.CONTROLLER_DEFINITION, ACVSLiveControllerInstance.class);
        DEF_TO_CLASS.put (OSCControllerInstance.CONTROLLER_DEFINITION, OSCControllerInstance.class);
        DEF_TO_CLASS.put (Push1ControllerInstance.CONTROLLER_DEFINITION, Push1ControllerInstance.class);
        DEF_TO_CLASS.put (Push2ControllerInstance.CONTROLLER_DEFINITION, Push2ControllerInstance.class);
        DEF_TO_CLASS.put (SLMkIControllerInstance.CONTROLLER_DEFINITION, SLMkIControllerInstance.class);
        DEF_TO_CLASS.put (SLMkIIControllerInstance.CONTROLLER_DEFINITION, SLMkIIControllerInstance.class);
        DEF_TO_CLASS.put (SLMkIIIControllerInstance.CONTROLLER_DEFINITION, SLMkIIIControllerInstance.class);

        for (final Class<?> clazz: DEF_TO_CLASS.values ())
            NAME_TO_CLASS.put (clazz.getName (), clazz);

        // Backwards compatibility before v12
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.autocolor.AutoColorInstance", AutoColorInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.apc.APC40mkIControllerInstance", APC40mkIControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.apc.APC40mkIIControllerInstance", APC40mkIIControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.apcmini.APCminiControllerInstance", APCminiControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.beatstep.BeatstepControllerInstance", BeatstepControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.fire.FireControllerInstance", FireControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.hui.HUI1ControllerInstance", HUI1ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.hui.HUI2ControllerInstance", HUI2ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.hui.HUI3ControllerInstance", HUI3ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.kontrol.mki.KontrolMkIS25ControllerInstance", KontrolMkIS25ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.kontrol.mki.KontrolMkIS49ControllerInstance", KontrolMkIS49ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.kontrol.mki.KontrolMkIS61ControllerInstance", KontrolMkIS61ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.kontrol.mki.KontrolMkIS88ControllerInstance", KontrolMkIS88ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.kontrol.mkii.KontrolProtocolV1ControllerInstance", KontrolProtocolV1ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.kontrol.mkii.KontrolProtocolV2ControllerInstance", KontrolProtocolV2ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.launchkey.LaunchkeyMiniMk3ControllerInstance", LaunchkeyMiniMk3ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.launchkey.LaunchkeyMk3ControllerInstance", LaunchkeyMk3ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.launchpad.LaunchpadMkIIControllerInstance", LaunchpadMkIIControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.launchpad.LaunchpadProControllerInstance", LaunchpadProControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.launchpad.LaunchpadXControllerInstance", LaunchpadXControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.launchpad.LaunchpadMiniMkIIIControllerInstance", LaunchpadMiniMkIIIControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.launchpad.LaunchpadProMk3ControllerInstance", LaunchpadProMk3ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.maschine.MaschineMikroMk3ControllerInstance", MaschineMikroMk3ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.maschine.MaschineMk3ControllerInstance", MaschineMk3ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.midimonitor.MidiMonitorInstance", MidiMonitorInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.mcu.MCU1ControllerInstance", MCU1ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.mcu.MCU2ControllerInstance", MCU2ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.mcu.MCU3ControllerInstance", MCU3ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.mcu.MCU4ControllerInstance", MCU4ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.osc.OSCControllerInstance", OSCControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.push.Push1ControllerInstance", Push1ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.push.Push2ControllerInstance", Push2ControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.sl.SLMkIControllerInstance", SLMkIControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.sl.SLMkIIControllerInstance", SLMkIIControllerInstance.class);
        NAME_TO_CLASS.put ("de.mossgrabers.reaper.controller.slmkiii.SLMkIIIControllerInstance", SLMkIIIControllerInstance.class);
    }

    private static final Class<?> []        CONSTRUCTOR_TYPES =
    {
        LogModel.class,
        WindowManager.class,
        MessageSender.class,
        IniFiles.class
    };

    private final List<IControllerInstance> instances         = new ArrayList<> ();
    private final LogModel                  logModel;
    private final WindowManager             windowManager;
    private final MessageSender             sender;
    private final IniFiles                  iniFiles;


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The owner window for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public ControllerInstanceManager (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        this.logModel = logModel;
        this.windowManager = windowManager;
        this.sender = sender;
        this.iniFiles = iniFiles;
    }


    /**
     * Get all controller definitions.
     *
     * @return All registered controller definitions
     */
    public Set<IControllerDefinition> getDefinitions ()
    {
        return DEF_TO_CLASS.keySet ();
    }


    /**
     * Start all configured controllers.
     */
    public void startAll ()
    {
        this.instances.forEach (IControllerInstance::start);
    }


    /**
     * Stop all configured controllers.
     */
    public void stopAll ()
    {
        this.instances.forEach (IControllerInstance::stop);
    }


    /**
     * Returns true if all configured controllers are running.
     *
     * @return True if all configured controllers are running
     */
    public boolean areRunning ()
    {
        for (final IControllerInstance inst: this.instances)
        {
            if (!inst.isRunning ())
                return false;
        }
        return true;
    }


    /**
     * Flush the data to all configured controller devices.
     */
    public void flushAll ()
    {
        this.instances.forEach (IControllerInstance::flush);
    }


    /**
     * Parse an incoming DAW message into all configured controllers.
     *
     * @param address The message address
     * @param argument The argument
     */
    public void parseAll (final String address, final String argument)
    {
        this.instances.forEach (inst -> {
            try
            {
                if (inst.isEnabled ())
                    inst.parse (address, argument);
            }
            catch (final RuntimeException ex)
            {
                final StringBuilder sb = new StringBuilder ("Could not parse OSC message: ").append (address).append (" ");
                if (argument != null)
                    sb.append (argument);
                this.logModel.error (sb.toString (), ex);
            }
        });
    }


    /**
     * Edit the settings of a controller instance.
     *
     * @param index The index of the controller instance
     */
    public void edit (final int index)
    {
        this.instances.get (index).edit ();
    }


    /**
     * Edit all project settings.
     */
    public void projectSettings ()
    {
        new ProjectSettingsDialog (this.windowManager.getMainFrame (), this.instances).showDialog ();
    }


    /**
     * Remove a controller instance.
     *
     * @param index The index of the controller instance
     */
    public void remove (final int index)
    {
        final IControllerInstance controllerInstance = this.instances.remove (index);
        if (controllerInstance != null)
            controllerInstance.stop ();
    }


    /**
     * Instantiate (add) a controller instance.
     *
     * @param definition The controller definition
     * @return The instance
     */
    public IControllerInstance instantiate (final IControllerDefinition definition)
    {
        return this.instantiateController (DEF_TO_CLASS.get (definition));
    }


    /**
     * Test if a controller definition is instantiated.
     *
     * @param definition The index of the controller definition
     * @return True if instance exists
     */
    public boolean isInstantiated (final IControllerDefinition definition)
    {
        for (final IControllerInstance inst: this.instances)
        {
            if (inst.getDefinition ().equals (definition))
                return true;
        }
        return false;
    }


    /**
     * Is one of the given in- or output devices already in use?
     *
     * @param inputDevices The input devices to test
     * @param outputDevices The output devices to test
     * @return True if one of the devices is already in use
     */
    public boolean areInUse (final List<MidiDevice> inputDevices, final List<MidiDevice> outputDevices)
    {
        for (final IControllerInstance inst: this.instances)
        {
            final GlobalSettingsUI settings = inst.getGlobalSettingsUI ();
            for (final MidiDevice input: settings.getSelectedMidiInputs ())
            {
                if (inputDevices.contains (input))
                    return true;
            }
            for (final MidiDevice output: settings.getSelectedMidiOutputs ())
            {
                if (inputDevices.contains (output))
                    return true;
            }
        }
        return false;
    }


    /**
     * Get all controller instances.
     *
     * @return The controller instances
     */
    public List<IControllerInstance> getInstances ()
    {
        return new ArrayList<> (this.instances);
    }


    /**
     * Load all configured controller instances from the properties.
     *
     * @param properties The properties to parse
     */
    public void load (final PropertiesEx properties)
    {
        int counter = 0;
        String className;
        while ((className = properties.getString (CONTROLLER_INSTANCE_TAG + counter)) != null)
        {
            final Class<?> clazz = NAME_TO_CLASS.get (className);
            if (clazz == null)
                this.logModel.info ("Unknown controller  class: " + className);
            else
                this.instantiateController (clazz);
            counter++;
        }
    }


    /**
     * Wrote all configured controller instances to the properties.
     *
     * @param properties The properties to parse
     */
    public void save (final PropertiesEx properties)
    {
        for (int i = 0; i < this.instances.size (); i++)
        {
            final IControllerInstance inst = this.instances.get (i);
            properties.putString (CONTROLLER_INSTANCE_TAG + i, inst.getClass ().getName ());
        }
        properties.remove (CONTROLLER_INSTANCE_TAG + this.instances.size ());
    }


    private IControllerInstance instantiateController (final Class<?> clazz)
    {
        try
        {
            final Constructor<?> constructor = clazz.getConstructor (CONSTRUCTOR_TYPES);
            final IControllerInstance newInstance = (IControllerInstance) constructor.newInstance (this.logModel, this.windowManager, this.sender, this.iniFiles);
            this.instances.add (newInstance);
            return newInstance;
        }
        catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex)
        {
            this.logModel.error ("Could not instantiate controller  class: " + clazz.getName () + ".", ex);
            return null;
        }
    }
}
