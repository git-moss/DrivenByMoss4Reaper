package de.mossgrabers.transformator.midi;

import uk.co.xfactorylibrarians.coremidi4j.CoreMidiDeviceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import java.util.List;


/**
 * Helper functions for looking up midi devices.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Midi
{
    private final static ObservableList<MidiDevice> INPUTS  = FXCollections.observableArrayList ();
    private final static ObservableList<MidiDevice> OUTPUTS = FXCollections.observableArrayList ();


    /**
     * Read information about all available midi devices in the system.
     *
     * @throws MidiUnavailableException Midi is not available on this system
     */
    public static void readDeviceMetadata () throws MidiUnavailableException
    {
        INPUTS.clear ();
        OUTPUTS.clear ();

        // Using the provider lookup instead of MidiSystem.getMidiDeviceInfo () ensures that the
        // broken devices on Mac are hidden. The function is transparent on other platforms.
        for (final MidiDevice.Info info: CoreMidiDeviceProvider.getMidiDeviceInfo ())
        {
            final MidiDevice device = MidiSystem.getMidiDevice (info);
            if (device.getMaxReceivers () != 0)
                OUTPUTS.add (device);
            if (device.getMaxTransmitters () != 0)
                INPUTS.add (device);
        }
    }


    /**
     * Get all midi devices which can be used as an output. readDeviceMetadata must have called
     * before.
     *
     * @return All output devices
     */
    public static List<MidiDevice> getOutputDevices ()
    {
        return OUTPUTS;
    }


    /**
     * Get all midi devices which can be used as an input. readDeviceMetadata must have called
     * before.
     *
     * @return All output devices
     */
    public static List<MidiDevice> getInputDevices ()
    {
        return INPUTS;
    }


    /**
     * Get a specific output device.
     *
     * @param name The full name of the device to lookup
     * @return The device or null if not found
     */
    public static MidiDevice getOutputDevice (final String name)
    {
        for (final MidiDevice device: OUTPUTS)
            if (device.getDeviceInfo ().getName ().equals (name))
                return device;
        return null;
    }


    /**
     * Get a specific input device.
     *
     * @param name The full name of the device to lookup
     * @return The device or null if not found
     */
    public static MidiDevice getInputDevice (final String name)
    {
        for (final MidiDevice device: INPUTS)
            if (device.getDeviceInfo ().getName ().equals (name))
                return device;
        return null;
    }
}
