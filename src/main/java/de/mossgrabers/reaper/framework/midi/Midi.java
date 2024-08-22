// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.utils.OperatingSystem;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiDeviceProvider;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;


/**
 * Helper functions for looking up MIDI devices.
 *
 * @author Jürgen Moßgraber
 */
public class Midi
{
    private static final String                  CORE_MIDI4J = "CoreMIDI4J - ";
    private static final Map<String, MidiDevice> INPUTS      = new TreeMap<> ();
    private static final Map<String, MidiDevice> OUTPUTS     = new TreeMap<> ();


    /**
     * Utility class.
     */
    private Midi ()
    {
        // Intentionally empty
    }


    /**
     * Read information about all available MIDI devices in the system.
     *
     * @throws MidiUnavailableException MIDI is not available on this system
     */
    public static void readDeviceMetadata () throws MidiUnavailableException
    {
        INPUTS.clear ();
        OUTPUTS.clear ();

        // Using the provider lookup instead of MidiSystem.getMidiDeviceInfo () ensures that the
        // broken devices on Mac are hidden. The function is transparent on other platforms.
        Info [] midiDeviceInfo;
        try
        {
            midiDeviceInfo = CoreMidiDeviceProvider.getMidiDeviceInfo ();
        }
        catch (final java.lang.NoClassDefFoundError error)
        {
            midiDeviceInfo = MidiSystem.getMidiDeviceInfo ();
        }

        final Map<String, Integer> keyedNames = new TreeMap<> ();
        for (final MidiDevice.Info info: midiDeviceInfo)
        {
            MidiDevice device = MidiSystem.getMidiDevice (info);

            final boolean isInput = device.getMaxTransmitters () != 0;
            final boolean isOutput = device.getMaxReceivers () != 0;
            if (!isInput && !isOutput)
                continue;

            // Workaround for not unique names since there is no ID for MIDI devices available
            // (note: this info is not available on Windows at all)
            String name = updateName (info.getName ());
            final String key = (isInput ? "I" : "O") + name;
            final Integer count = keyedNames.get (key);
            final Integer newCount = Integer.valueOf (count == null ? 1 : count.intValue () + 1);
            keyedNames.put (key, newCount);

            if (count != null)
                name = String.format ("%s (%d)", name, newCount);
            device = new RenamedMidiDevice (name, device);

            if (isOutput)
                OUTPUTS.put (name, device);
            else
                INPUTS.put (name, device);
        }
    }


    /**
     * Get all MIDI devices which can be used as an output. readDeviceMetadata must have called
     * before.
     *
     * @return All output devices
     */
    public static Collection<MidiDevice> getOutputDevices ()
    {
        return OUTPUTS.values ();
    }


    /**
     * Get all MIDI devices which can be used as an input. readDeviceMetadata must have called
     * before.
     *
     * @return All output devices
     */
    public static Collection<MidiDevice> getInputDevices ()
    {
        return INPUTS.values ();
    }


    /**
     * Get a specific output device.
     *
     * @param name The full name of the device to lookup
     * @return The device or null if not found
     */
    public static MidiDevice getOutputDevice (final String name)
    {
        return OUTPUTS.get (name);
    }


    /**
     * Get a specific input device.
     *
     * @param name The full name of the device to lookup
     * @return The device or null if not found
     */
    public static MidiDevice getInputDevice (final String name)
    {
        return INPUTS.get (name);
    }


    private static String updateName (final String name)
    {
        if (OperatingSystem.isMacOS () && name.startsWith (CORE_MIDI4J))
            return name.substring (CORE_MIDI4J.length ());
        return name;
    }
}
