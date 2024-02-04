// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import java.util.List;


/**
 * A wrapper for MIDI device which allows to change its' name.
 *
 * @author Jürgen Moßgraber
 */
public class RenamedMidiDevice implements MidiDevice
{
    private final MidiDevice device;
    private final Info       info;


    /**
     * Constructor.
     *
     * @param name The name to set for the device
     * @param device The device to wrap
     */
    public RenamedMidiDevice (final String name, final MidiDevice device)
    {
        this.device = device;
        this.info = new RenamedInfo (name, device.getDeviceInfo ());
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
        this.device.open ();
    }


    /** {@inheritDoc} */
    @Override
    public void close ()
    {
        this.device.close ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOpen ()
    {
        return this.device.isOpen ();
    }


    /** {@inheritDoc} */
    @Override
    public long getMicrosecondPosition ()
    {
        return this.device.getMicrosecondPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public int getMaxReceivers ()
    {
        return this.device.getMaxReceivers ();
    }


    /** {@inheritDoc} */
    @Override
    public int getMaxTransmitters ()
    {
        return this.device.getMaxTransmitters ();
    }


    /** {@inheritDoc} */
    @Override
    public Receiver getReceiver () throws MidiUnavailableException
    {
        return this.device.getReceiver ();
    }


    /** {@inheritDoc} */
    @Override
    public List<Receiver> getReceivers ()
    {
        return this.device.getReceivers ();
    }


    /** {@inheritDoc} */
    @Override
    public Transmitter getTransmitter () throws MidiUnavailableException
    {
        return this.device.getTransmitter ();
    }


    /** {@inheritDoc} */
    @Override
    public List<Transmitter> getTransmitters ()
    {
        return this.device.getTransmitters ();
    }


    /** Necessary to make sub-classing possible. */
    public static class RenamedInfo extends MidiDevice.Info
    {
        protected RenamedInfo (final String name, final Info info)
        {
            super (name, info.getVendor (), info.getDescription (), info.getVersion ());
        }
    }
}
