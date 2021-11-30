// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import java.util.Collections;
import java.util.List;


/**
 * A dummy MIDI device for missing or not selected MIDI devices.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MissingMidiDevice implements MidiDevice
{
    /** The 'no device selected' device. */
    public static final MissingMidiDevice NONE = new MissingMidiDevice ("None");

    private final Info                    info;


    /**
     * Constructor.
     *
     * @param name The name of the missing device
     */
    public MissingMidiDevice (final String name)
    {
        this.info = new Info (name);
    }


    /** {@inheritDoc} */
    @Override
    public Info getDeviceInfo ()
    {
        return this.info;
    }


    /** {@inheritDoc} */
    @Override
    public void open () throws MidiUnavailableException
    {
        // No need to
    }


    /** {@inheritDoc} */
    @Override
    public void close ()
    {
        // No need to
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOpen ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public long getMicrosecondPosition ()
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getMaxReceivers ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getMaxTransmitters ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public Receiver getReceiver () throws MidiUnavailableException
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<Receiver> getReceivers ()
    {
        return Collections.emptyList ();
    }


    /** {@inheritDoc} */
    @Override
    public Transmitter getTransmitter () throws MidiUnavailableException
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<Transmitter> getTransmitters ()
    {
        return Collections.emptyList ();
    }


    /** Necessary to make sub-classing possible. */
    public static class Info extends MidiDevice.Info
    {
        protected Info (final String name)
        {
            super (name, "", "", "");
        }
    }
}
