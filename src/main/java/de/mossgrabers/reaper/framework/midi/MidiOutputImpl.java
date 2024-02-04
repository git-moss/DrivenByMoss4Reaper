// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.AbstractMidiOutput;
import de.mossgrabers.framework.utils.FrameworkException;


/**
 * A MIDI output
 *
 * @author Jürgen Moßgraber
 */
class MidiOutputImpl extends AbstractMidiOutput
{
    private final MidiConnection midiConnection;


    /**
     * Constructor.
     *
     * @param midiConnection The MIDI connection to send outgoing data to
     */
    public MidiOutputImpl (final MidiConnection midiConnection)
    {
        this.midiConnection = midiConnection;
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final String data)
    {
        final String [] parts = data.split (" ");
        final byte [] bytes = new byte [parts.length];
        try
        {
            for (int i = 0; i < parts.length; i++)
                bytes[i] = (byte) Integer.parseInt (parts[i], 16);
        }
        catch (final NumberFormatException ex)
        {
            throw new FrameworkException (String.format ("Broken Sysex string: '%s'", data), ex);
        }

        this.sendSysex (bytes);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final byte [] data)
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