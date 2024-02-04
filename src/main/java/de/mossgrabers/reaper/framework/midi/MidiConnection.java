// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.reaper.ui.utils.LogModel;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Handles a MIDI connection to a MIDI device which has an input and output.
 *
 * @author Jürgen Moßgraber
 */
public class MidiConnection
{
    private static final Set<MidiDevice> USED_DEVICES   = new HashSet<> (1);

    private MidiDevice                   midiInputDevice;
    private MidiDevice                   midiOutputDevice;
    private Receiver                     receiver;
    private Transmitter                  transmitter;

    private final LogModel               model;
    private final Object                 sendLock       = new Object ();

    private final AtomicInteger          maxSysexLength = new AtomicInteger (0);


    /**
     * Constructor.
     *
     * @param model For providing logging
     */
    public MidiConnection (final LogModel model)
    {
        this.model = model;
    }


    /**
     * Sets a MIDI device as the output device. It opens the device if it is not already in use and
     * creates a receiver. If there is already a receiver opened it is closed.
     *
     * @param device The MIDI device to set
     */
    public void setOutput (final MidiDevice device)
    {
        synchronized (this.sendLock)
        {
            if (this.receiver != null)
            {
                this.receiver.close ();
                this.receiver = null;
            }

            if (device == null)
                return;

            try
            {
                this.midiOutputDevice = device;
                if (!this.midiOutputDevice.isOpen ())
                    this.midiOutputDevice.open ();
                if (!this.midiOutputDevice.isOpen ())
                    throw new MidiUnavailableException ("Could not open MIDI output device: " + this.midiOutputDevice.getDeviceInfo ().getName ());
                this.receiver = this.midiOutputDevice.getReceiver ();

                USED_DEVICES.add (device);
            }
            catch (final MidiUnavailableException ex)
            {
                this.model.error ("Midi not available.", ex);
            }
        }
    }


    /**
     * Sets a MIDI device as the output device. It opens the device if it is not already in use and
     * creates a receiver. If there is already a receiver opened it is closed.
     *
     * @param device The MIDI device to set
     * @param callback Callback handler for MIDI message reception
     */
    public void setInput (final MidiDevice device, final MidiMessageHandler callback)
    {
        if (this.transmitter != null)
        {
            this.transmitter.close ();
            this.transmitter = null;
        }

        if (device == null)
            return;

        USED_DEVICES.add (device);

        try
        {
            this.midiInputDevice = device;
            if (!this.midiInputDevice.isOpen ())
                this.midiInputDevice.open ();

            this.transmitter = this.midiInputDevice.getTransmitter ();
            this.transmitter.setReceiver (new InternalMidiReceiver (callback));
        }
        catch (final MidiUnavailableException ex)
        {
            this.model.error ("Midi not available.", ex);
        }
    }


    /**
     * Send a system exclusive message to the output.
     *
     * @param data The data to send formatted as hex values separated by spaces
     */
    public void sendSysex (final byte [] data)
    {
        try
        {
            byte [] d = data;
            final int oldmax = this.maxSysexLength.get ();
            if (oldmax > data.length)
            {
                // Workaround for bug in the JDK.If a longer Sysex message was sent before, sending
                // a short one will result in CC messages sent containing the diff bytes. Therefore,
                // store the maxmimum length of a message sent and add F7s to fill the space.

                d = new byte [oldmax];
                System.arraycopy (data, 0, d, 0, data.length);
                for (int i = data.length; i < oldmax; i++)
                    d[i] = (byte) 0xF7;
            }
            this.maxSysexLength.set (data.length);
            this.send (new SysexMessage (d, d.length));
        }
        catch (final InvalidMidiDataException ex)
        {
            this.model.error ("Invalid Midi data.", ex);
        }
    }


    /**
     * Send a MIDI CC message to the output.
     *
     * @param cc The CC number
     * @param value The value for the CC
     */
    public void sendCC (final int cc, final int value)
    {
        this.send (this.createShortMessage (0xB0, cc, value));
    }


    /**
     * Send a MIDI note to the output.
     *
     * @param channel The MIDI channel on which to send the note
     * @param note The note to send
     * @param velocity The velocity of the note
     */
    public void sendNote (final int channel, final int note, final int velocity)
    {
        this.send (this.createShortMessage (0x90 + channel, note, velocity));
    }


    /**
     * Send a pitch bend message to the output.
     *
     * @param value The value of the pitch bend
     */
    public void sendPitchbend (final int value)
    {
        this.send (this.createShortMessage (0xE0, 0, value));
    }


    /**
     * Send a MIDI note to the output.
     *
     * @param status The status and MIDI channel
     * @param data1 The first data byte
     * @param data2 The second data byte
     */
    public void sendRaw (final int status, final int data1, final int data2)
    {
        this.send (this.createShortMessage (status, data1, data2));
    }


    /**
     * Checks if the MIDI input is open and data can be received from it.
     *
     * @return True if open
     */
    public boolean isInputOpen ()
    {
        return this.transmitter != null && this.midiInputDevice.isOpen ();
    }


    /**
     * Closes all used MIDI devices and frees the resources.
     */
    public void cleanup ()
    {
        synchronized (this.sendLock)
        {
            if (this.receiver != null)
            {
                this.receiver.close ();
                this.receiver = null;
            }
        }
        if (this.transmitter != null)
        {
            final Receiver r = this.transmitter.getReceiver ();
            if (r != null)
                r.close ();
            this.transmitter.setReceiver (null);
            this.transmitter.close ();
            this.transmitter = null;
        }
        if (this.midiInputDevice != null)
        {
            this.midiInputDevice.close ();
            USED_DEVICES.remove (this.midiInputDevice);
        }
        if (this.midiOutputDevice != null)
        {
            this.midiOutputDevice.close ();
            USED_DEVICES.remove (this.midiOutputDevice);
        }
    }


    /**
     * Close devices, which might still be open because the application did not close them.
     */
    public static void cleanupUnusedDevices ()
    {
        for (final MidiDevice device: USED_DEVICES)
            device.close ();
    }


    private void send (final MidiMessage message)
    {
        if (message == null)
            return;
        synchronized (this.sendLock)
        {
            if (this.receiver == null)
                return;

            if (this.midiOutputDevice.isOpen ())
                this.receiver.send (message, -1);
            else
                this.model.error ("Attempt to send to closed MIDI output: " + this.midiOutputDevice.getDeviceInfo ().getName (), null);
        }
    }


    private ShortMessage createShortMessage (final int status, final int data1, final int data2)
    {
        try
        {
            return new ShortMessage (status, data1, data2);
        }
        catch (final InvalidMidiDataException ex)
        {
            this.model.error ("Invalid MIDI data.", ex);
            return null;
        }
    }


    private final class InternalMidiReceiver implements Receiver
    {
        private MidiMessageHandler callback;


        InternalMidiReceiver (final MidiMessageHandler callback)
        {
            this.callback = callback;
        }


        /** {@inheritDoc} */
        @Override
        public void close ()
        {
            this.callback = null;
        }


        /** {@inheritDoc} */
        @Override
        public void send (final MidiMessage message, final long timeStamp)
        {
            if (this.callback != null)
                this.callback.handleMidiMessage (message, timeStamp);
        }
    }
}
