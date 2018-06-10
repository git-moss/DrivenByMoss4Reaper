// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.transformator.midi.MidiConnection;


/**
 * A midi output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiOutputImpl implements IMidiOutput
{
    private MidiConnection midiConnection;


    /**
     * Constructor.
     *
     * @param midiConnection The midi connection to send outgoing data to
     */
    public MidiOutputImpl (final MidiConnection midiConnection)
    {
        this.midiConnection = midiConnection;
    }


    /** {@inheritDoc} */
    @Override
    public void sendCC (final int cc, final int value)
    {
        this.midiConnection.sendCC (cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCCEx (final int channel, final int cc, final int value)
    {
        this.midiConnection.sendRaw (0xB0 + channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNote (final int note, final int velocity)
    {
        this.midiConnection.sendNote (0, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNoteEx (final int channel, final int note, final int velocity)
    {
        this.midiConnection.sendNote (channel, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int data1, final int data2)
    {
        this.midiConnection.sendRaw (0xD0, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int channel, final int data1, final int data2)
    {
        this.midiConnection.sendRaw (0xD0 + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int data1, final int data2)
    {
        this.midiConnection.sendRaw (0xE0, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int channel, final int data1, final int data2)
    {
        this.midiConnection.sendRaw (0xE0 + channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final byte [] data)
    {
        this.midiConnection.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final String data)
    {
        this.midiConnection.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public void sendIdentityRequest ()
    {
        this.sendSysex ("F0 7E 7F 06 01 F7");
    }
}