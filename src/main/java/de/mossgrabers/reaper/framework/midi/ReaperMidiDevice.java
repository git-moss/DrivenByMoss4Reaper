// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.reaper.communication.BackendExchange;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import java.util.Collections;
import java.util.List;


/**
 * Reaper implementation to access MIDI input and outputs on the C++ backend.
 *
 * @author Jürgen Moßgraber
 */
public class ReaperMidiDevice implements MidiDevice, Receiver
{
    private final int             index;
    private final String          name;
    private final Info            info;
    private final boolean         isInput;
    private final BackendExchange backend;
    private final Object          lifecycleLock = new Object ();
    private final Transmitter     transmitter;

    private boolean               isOpen        = false;


    /**
     * Constructor.
     *
     * @param index The index of the MIDI device
     * @param name The name of the MIDI device
     * @param isInput True if it is an input otherwise it is an output
     * @param backend Interface to the C++ backend
     */
    public ReaperMidiDevice (final int index, final String name, final boolean isInput, final BackendExchange backend)
    {
        this.index = index;
        this.name = name;
        this.isInput = isInput;
        this.backend = backend;

        this.info = new Info (name);
        this.transmitter = this.isInput ? new ReaperTransmitter () : null;
    }


    /**
     * Get the ID of the device.
     *
     * @return The ID
     */
    public int getDeviceID ()
    {
        return this.index;
    }


    /**
     * Handle the MIDI message which was received from the backend.
     *
     * @param message The message to handle
     */
    public void handleMidiMessageFromBackend (final MidiMessage message)
    {
        final Receiver receiver = this.transmitter.getReceiver ();
        if (receiver != null)
            receiver.send (message, -1);
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
        synchronized (this.lifecycleLock)
        {
            if (this.isOpen)
                return;

            if (this.isInput)
            {
                if (!this.backend.openMidiInput (this.index))
                    throw new MidiUnavailableException ("MIDI input port #" + this.index + " " + this.name + " is not enabled in Reaper.");
            }
            else
            {
                if (!this.backend.openMidiOutput (this.index))
                    throw new MidiUnavailableException ("MIDI output port #" + this.index + " " + this.name + " is not enabled in Reaper.");
            }

            this.isOpen = true;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void close ()
    {
        // Note: this is both called from MidiDevice as well as Receiver!

        synchronized (this.lifecycleLock)
        {
            if (!this.isOpen)
                return;
            this.isOpen = false;

            if (this.isInput)
                this.backend.closeMidiInput (this.index);
            else
                this.backend.closeMidiOutput (this.index);
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOpen ()
    {
        synchronized (this.lifecycleLock)
        {
            return this.isOpen;
        }
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
        return this.isInput ? 1 : 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getMaxTransmitters ()
    {
        return this.isInput ? 0 : 1;
    }


    /** {@inheritDoc} */
    @Override
    public Receiver getReceiver () throws MidiUnavailableException
    {
        synchronized (this.lifecycleLock)
        {
            if (!this.isOpen)
                throw new MidiUnavailableException ("This MIDI output (#" + this.index + " " + this.name + ") has not beend opened.");
        }
        if (this.isInput)
            throw new MidiUnavailableException ("This MIDI device is not an output port #" + this.index + " " + this.name);
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public List<Receiver> getReceivers ()
    {
        synchronized (this.lifecycleLock)
        {
            return this.isInput || !this.isOpen ? Collections.emptyList () : Collections.singletonList (this);
        }
    }


    /** {@inheritDoc} */
    @Override
    public Transmitter getTransmitter () throws MidiUnavailableException
    {
        synchronized (this.lifecycleLock)
        {
            if (!this.isOpen)
                throw new MidiUnavailableException ("This MIDI input (#" + this.index + " " + this.name + ") has not beend opened.");
        }
        if (!this.isInput)
            throw new MidiUnavailableException ("This MIDI device is not an input port #" + this.index + " " + this.name);
        return this.transmitter;
    }


    /** {@inheritDoc} */
    @Override
    public List<Transmitter> getTransmitters ()
    {
        synchronized (this.lifecycleLock)
        {
            return this.isInput && this.isOpen ? Collections.singletonList (this.transmitter) : Collections.emptyList ();
        }
    }


    /**
     * Receiver interface for MIDI output. {@inheritDoc}
     */
    @Override
    public void send (final MidiMessage message, final long timeStamp)
    {
        this.backend.sendMidiData (this.index, message.getMessage ());
    }


    /** Transmitter interface for MIDI input. */
    private class ReaperTransmitter implements Transmitter
    {
        Receiver receiver = null;


        /** {@inheritDoc} */
        @Override
        public void setReceiver (final Receiver receiver)
        {
            this.receiver = receiver;
        }


        /** {@inheritDoc} */
        @Override
        public Receiver getReceiver ()
        {
            return this.receiver;
        }


        /** {@inheritDoc} */
        @Override
        public void close ()
        {
            ReaperMidiDevice.this.close ();
        }
    }


    /** Info about the device. Only the name is filled. */
    public static class Info extends MidiDevice.Info
    {
        protected Info (final String name)
        {
            super (name, "", "", "");
        }
    }
}
