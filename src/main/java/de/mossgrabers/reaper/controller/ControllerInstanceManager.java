// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller;

import de.mossgrabers.controller.apc.APCControllerDefinition;
import de.mossgrabers.controller.apcmini.APCminiControllerDefinition;
import de.mossgrabers.controller.beatstep.BeatstepControllerDefinition;
import de.mossgrabers.controller.generic.GenericFlexiControllerDefinition;
import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1ControllerDefinition;
import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2ControllerDefinition;
import de.mossgrabers.controller.launchpad.LaunchpadControllerDefinition;
import de.mossgrabers.controller.mcu.MCUControllerDefinition;
import de.mossgrabers.controller.osc.OSCControllerDefinition;
import de.mossgrabers.controller.push.PushControllerDefinition;
import de.mossgrabers.controller.sl.SLControllerDefinition;
import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.apc.APC40mkIControllerInstance;
import de.mossgrabers.reaper.controller.apc.APC40mkIIControllerInstance;
import de.mossgrabers.reaper.controller.apcmini.APCminiControllerInstance;
import de.mossgrabers.reaper.controller.beatstep.BeatstepControllerInstance;
import de.mossgrabers.reaper.controller.beatstep.BeatstepProControllerInstance;
import de.mossgrabers.reaper.controller.generic.GenericFlexiControllerInstance;
import de.mossgrabers.reaper.controller.kontrol.usb.mki.KontrolMkIS25ControllerInstance;
import de.mossgrabers.reaper.controller.kontrol.usb.mki.KontrolMkIS49ControllerInstance;
import de.mossgrabers.reaper.controller.kontrol.usb.mki.KontrolMkIS61ControllerInstance;
import de.mossgrabers.reaper.controller.kontrol.usb.mki.KontrolMkIS88ControllerInstance;
import de.mossgrabers.reaper.controller.kontrol.usb.mkii.KontrolMkIIS49ControllerInstance;
import de.mossgrabers.reaper.controller.kontrol.usb.mkii.KontrolMkIIS61ControllerInstance;
import de.mossgrabers.reaper.controller.launchpad.LaunchpadMkIIControllerInstance;
import de.mossgrabers.reaper.controller.launchpad.LaunchpadProControllerInstance;
import de.mossgrabers.reaper.controller.mcu.MCU1ControllerInstance;
import de.mossgrabers.reaper.controller.osc.OSCControllerInstance;
import de.mossgrabers.reaper.controller.push.Push1ControllerInstance;
import de.mossgrabers.reaper.controller.push.Push2ControllerInstance;
import de.mossgrabers.reaper.controller.sl.SLMkIControllerInstance;
import de.mossgrabers.reaper.controller.sl.SLMkIIControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;

import java.awt.Window;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Manager for all controller instances and definitions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControllerInstanceManager
{
    private static final String                   CONTROLLER_INSTANCE_TAG = "CONTROLLER_INSTANCE";

    private static final Class<?> []              CLASSES                 =
    {
        Push1ControllerInstance.class,
        Push2ControllerInstance.class,
        APC40mkIControllerInstance.class,
        APC40mkIIControllerInstance.class,
        APCminiControllerInstance.class,
        BeatstepControllerInstance.class,
        BeatstepProControllerInstance.class,
        GenericFlexiControllerInstance.class,
        KontrolMkIS25ControllerInstance.class,
        KontrolMkIS49ControllerInstance.class,
        KontrolMkIS61ControllerInstance.class,
        KontrolMkIS88ControllerInstance.class,
        // TODO Reaper Not yet implemented at all
        KontrolMkIIS49ControllerInstance.class,
        KontrolMkIIS61ControllerInstance.class,
        // TODO Reaper Support protocol with more than 8 bank pages
        // KontrolMkIIControllerInstance.class,
        LaunchpadMkIIControllerInstance.class,
        LaunchpadProControllerInstance.class,
        MCU1ControllerInstance.class,
        // TODO Reaper Support protocol with more than 8 bank pages
        // MCU2ControllerInstance.class,
        // MCU3ControllerInstance.class,
        // MCU4ControllerInstance.class,
        OSCControllerInstance.class,
        SLMkIControllerInstance.class,
        SLMkIIControllerInstance.class
    };

    private static final IControllerDefinition [] DEFINITIONS             =
    {
        new PushControllerDefinition (false),
        new PushControllerDefinition (true),
        new APCControllerDefinition (false),
        new APCControllerDefinition (true),
        new APCminiControllerDefinition (),
        new BeatstepControllerDefinition (false),
        new BeatstepControllerDefinition (true),
        new GenericFlexiControllerDefinition (),
        new Kontrol1ControllerDefinition (0),
        new Kontrol1ControllerDefinition (1),
        new Kontrol1ControllerDefinition (2),
        new Kontrol1ControllerDefinition (3),
        // TODO Reaper Not yet implemented at all
        new Kontrol2ControllerDefinition (0),
        new Kontrol2ControllerDefinition (1),
        // TODO Reaper Support protocol with more than 8 bank pages
        // new KontrolOSCControllerDefinition (),
        new LaunchpadControllerDefinition (true),
        new LaunchpadControllerDefinition (false),
        new MCUControllerDefinition (0),
        // TODO Reaper Support protocol with more than 8 bank pages
        // new MCUControllerDefinition (1),
        // new MCUControllerDefinition (2),
        // new MCUControllerDefinition (3),
        new OSCControllerDefinition (),
        new SLControllerDefinition (false),
        new SLControllerDefinition (true)
    };

    private static final Class<?> []              CONSTRUCTOR_TYPES       =
    {
        LogModel.class,
        Window.class,
        MessageSender.class,
        IniFiles.class
    };

    private final List<IControllerInstance>       instances               = new ArrayList<> ();
    private final LogModel                        logModel;
    private final Window                          window;
    private final MessageSender                   sender;
    private final IniFiles                        iniFiles;


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param window The owner window for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public ControllerInstanceManager (final LogModel logModel, final Window window, final MessageSender sender, final IniFiles iniFiles)
    {
        this.logModel = logModel;
        this.window = window;
        this.sender = sender;
        this.iniFiles = iniFiles;
    }


    /**
     * Get all controller definitions.
     *
     * @return All registeredcontroller definitions
     */
    public IControllerDefinition [] getDefinitions ()
    {
        return DEFINITIONS;
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
     * @param definitionIndex The index of the controller definition
     * @return The instance
     */
    public IControllerInstance instantiate (final int definitionIndex)
    {
        return this.instantiateController (CLASSES[definitionIndex]);
    }


    /**
     * Test if a controller definition is instantiated.
     *
     * @param definitionIndex The index of the controller definition
     * @return True if instance exists
     */
    public boolean isInstantiated (final int definitionIndex)
    {
        for (final IControllerInstance inst: this.instances)
        {
            if (inst.getClass ().equals (CLASSES[definitionIndex]))
                return true;
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
            final Class<?> clazz = lookupClass (className);
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
            final IControllerInstance newInstance = (IControllerInstance) constructor.newInstance (this.logModel, this.window, this.sender, this.iniFiles);
            this.instances.add (newInstance);
            return newInstance;
        }
        catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex)
        {
            this.logModel.error ("Could not instantiate controller  class: " + clazz.getName () + ".", ex);
            return null;
        }
    }


    private static Class<?> lookupClass (final String className)
    {
        for (final Class<?> clazz: CLASSES)
        {
            if (clazz.getName ().equals (className))
                return clazz;
        }
        return null;
    }
}
