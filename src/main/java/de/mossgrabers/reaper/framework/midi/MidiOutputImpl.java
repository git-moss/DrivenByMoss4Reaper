// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.AbstractMidiOutputImpl;


/**
 * A midi output
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiOutputImpl extends AbstractMidiOutputImpl
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
    protected void sendMidiShort (final int status, final int data1, final int data2)
    {
        this.midiConnection.sendRaw (status, data1, data2);
    }
}