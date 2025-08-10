// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.ui.utils.LogModel;

import javax.sound.midi.MidiDevice;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Reaper implementation to access MIDI input and outputs.
 *
 * @author Jürgen Moßgraber
 */
public class MidiAccessImpl implements IMidiAccess
{
    private static BackendExchange                     backend;
    private static final Map<String, ReaperMidiDevice> INPUTS  = new TreeMap<> ();
    private static final Map<String, ReaperMidiDevice> OUTPUTS = new TreeMap<> ();

    private final IHost                                host;
    private final MidiConnection []                    midiConnections;
    private final MidiDevice []                        inputs;
    private final MidiDevice []                        outputs;


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param host The host
     * @param inputs The MIDI input devices
     * @param outputs The MIDI output devices
     */
    public MidiAccessImpl (final LogModel logModel, final IHost host, final MidiDevice [] inputs, final MidiDevice [] outputs)
    {
        this.host = host;
        this.inputs = inputs;
        this.outputs = outputs;

        final int midiConnectionSize = Math.max (inputs.length, outputs.length);
        this.midiConnections = new MidiConnection [midiConnectionSize];
        for (int i = 0; i < midiConnectionSize; i++)
            this.midiConnections[i] = new MidiConnection (logModel);
    }


    /**
     * Initializes MIDI access.
     *
     * @param backendExchange Interface to the C++ backend
     */
    public static void init (final BackendExchange backendExchange)
    {
        backend = backendExchange;
    }


    /**
     * Cleanup all MIDI connections.
     */
    public void cleanup ()
    {
        for (final MidiConnection midiConnection: this.midiConnections)
            midiConnection.cleanup ();
    }


    /** {@inheritDoc} */
    @Override
    public IMidiOutput createOutput ()
    {
        return this.createOutput (0);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiOutput createOutput (final int index)
    {
        this.midiConnections[index].setOutput (this.outputs[index]);
        return new MidiOutputImpl (this.midiConnections[index]);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiInput createInput (final String name, final String... filters)
    {
        return this.createInput (0, name, filters);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiInput createInput (final int index, final String name, final String... filters)
    {
        return new MidiInputImpl (this.host, backend, this.midiConnections[index], this.inputs[index], filters);
    }


    /**
     * Read information about all available MIDI devices in the system.
     */
    public static void readDeviceMetadata ()
    {
        INPUTS.clear ();
        OUTPUTS.clear ();

        final Map<String, Integer> keyedNames = new TreeMap<> ();

        final Map<Integer, String> midiInputs = backend.getMidiInputs ();
        for (final Map.Entry<Integer, String> info: midiInputs.entrySet ())
            addDevice (keyedNames, info, true);

        final Map<Integer, String> midiOutputs = backend.getMidiOutputs ();
        for (final Map.Entry<Integer, String> info: midiOutputs.entrySet ())
            addDevice (keyedNames, info, false);
    }


    private static void addDevice (final Map<String, Integer> keyedNames, final Entry<Integer, String> info, final boolean isInput)
    {
        // Workaround for not unique names since there is no ID for MIDI devices available
        // (note: this info is not available on Windows at all)
        String name = info.getValue ();
        final String key = (isInput ? "I" : "O") + name;
        final Integer count = keyedNames.get (key);
        final Integer newCount = Integer.valueOf (count == null ? 1 : count.intValue () + 1);
        keyedNames.put (key, newCount);

        if (count != null)
            name = String.format ("%s (%d)", name, newCount);

        final ReaperMidiDevice device = new ReaperMidiDevice (info.getKey ().intValue (), name, isInput, backend);
        if (isInput)
            INPUTS.put (name, device);
        else
            OUTPUTS.put (name, device);
    }


    /**
     * Get all MIDI devices which can be used as an output. readDeviceMetadata must have called
     * before.
     *
     * @return All output devices
     */
    public static Collection<ReaperMidiDevice> getOutputDevices ()
    {
        return OUTPUTS.values ();
    }


    /**
     * Get all MIDI devices which can be used as an input. readDeviceMetadata must have called
     * before.
     *
     * @return All output devices
     */
    public static Collection<ReaperMidiDevice> getInputDevices ()
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
