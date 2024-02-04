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
    private static final Map<String, MidiDevice> INPUTS  = new TreeMap<> ();
    private static final Map<String, MidiDevice> OUTPUTS = new TreeMap<> ();


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

        final Map<String, Integer> outputNames = new TreeMap<> ();
        final Map<String, Integer> inputNames = new TreeMap<> ();

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

        for (final MidiDevice.Info info: midiDeviceInfo)
        {
            MidiDevice device = MidiSystem.getMidiDevice (info);

            if (device.getMaxReceivers () != 0)
            {
                // Workaround for not unique names since there is no ID for MIDI devices available
                // (note: this info is not available on Windows at all)
                String name = info.getName ();
                Integer count = outputNames.get (name);
                if (count == null)
                    count = Integer.valueOf (1);
                else
                {
                    count = Integer.valueOf (count.intValue () + 1);
                    name = name + String.format (" (%d)", count);
                    device = new RenamedMidiDevice (name, device);
                }
                outputNames.put (name, count);

                OUTPUTS.put (name, device);
                if (OperatingSystem.isMacOS ())
                    OUTPUTS.put ("CoreMIDI4J - " + name, device);
            }

            if (device.getMaxTransmitters () != 0)
            {
                // Workaround for not unique names since there is no ID for MIDI devices available
                // (note: this info is not available on Windows at all)
                String name = info.getName ();
                Integer count = inputNames.get (name);
                if (count == null)
                    count = Integer.valueOf (1);
                else
                {
                    count = Integer.valueOf (count.intValue () + 1);
                    name = name + String.format (" (%d)", count);
                    device = new RenamedMidiDevice (name, device);
                }
                inputNames.put (name, count);

                INPUTS.put (name, device);
                if (OperatingSystem.isMacOS ())
                    INPUTS.put ("CoreMIDI4J - " + name, device);
            }
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
}
