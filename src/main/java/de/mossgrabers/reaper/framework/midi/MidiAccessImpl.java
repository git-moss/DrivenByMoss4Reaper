// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.midi.MidiConnection;
import de.mossgrabers.transformator.util.LogModel;

import javax.sound.midi.MidiDevice;


/**
 * Reaper implementation to access MIDI input and outputs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiAccessImpl implements IMidiAccess
{
    private final IHost             host;
    private final MessageSender     sender;
    private final MidiConnection [] midiConnections;
    private final MidiDevice []     inputs;
    private final MidiDevice []     outputs;


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param host The host
     * @param sender The OSC sender
     * @param inputs The midi input devices
     * @param outputs The midi output devices
     */
    public MidiAccessImpl (final LogModel logModel, final IHost host, final MessageSender sender, final MidiDevice [] inputs, final MidiDevice [] outputs)
    {
        this.host = host;
        this.sender = sender;
        this.inputs = inputs;
        this.outputs = outputs;

        final int midiConnectionSize = Math.max (inputs.length, outputs.length);
        this.midiConnections = new MidiConnection [midiConnectionSize];
        for (int i = 0; i < midiConnectionSize; i++)
            this.midiConnections[i] = new MidiConnection (logModel);
    }


    /**
     * Cleanup all midi connections.
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
        return new MidiInputImpl (this.host, this.sender, this.midiConnections[index], this.inputs[index], filters);
    }
}
