// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
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


/**
 * Handles a midi connection to a midi device which has an input and output.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiConnection
{
    private static final Set<MidiDevice> USED_DEVICES = new HashSet<> (1);

    private MidiDevice                   midiInputDevice;
    private MidiDevice                   midiOutputDevice;
    private Receiver                     receiver;
    private Transmitter                  transmitter;

    private LogModel                     model;


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
     * Sets a midi device as the output device. It opens the device if it is not already in use and
     * creates a receiver. If there is already a receiver opened it is closed.
     *
     * @param device The midi device to set
     */
    public void setOutput (final MidiDevice device)
    {
        if (this.receiver != null)
        {
            this.receiver.close ();
            this.receiver = null;
        }

        if (device == null)
            return;

        USED_DEVICES.add (device);

        try
        {
            this.midiOutputDevice = device;
            if (!this.midiOutputDevice.isOpen ())
                this.midiOutputDevice.open ();
            this.receiver = this.midiOutputDevice.getReceiver ();
        }
        catch (final MidiUnavailableException ex)
        {
            this.model.error ("Midi not available.", ex);
        }
    }


    /**
     * Sets a midi device as the output device. It opens the device if it is not already in use and
     * creates a receiver. If there is already a receiver opened it is closed.
     *
     * @param device The midi device to set
     * @param callback Callback handler for midi message reception
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
    public void sendSysex (final String data)
    {
        final String [] parts = data.split (" ");
        final byte [] bytes = new byte [parts.length];
        for (int i = 0; i < parts.length; i++)
            bytes[i] = (byte) Integer.parseInt (parts[i], 16);
        this.sendSysex (bytes);
    }


    /**
     * Send a system exclusive message to the output.
     *
     * @param data The data to send formatted as hex values separated by spaces
     */
    public void sendSysex (final byte [] data)
    {
        if (!this.isOutputOpen ())
            return;
        try
        {
            this.receiver.send (new SysexMessage (data, data.length), -1);
        }
        catch (final InvalidMidiDataException ex)
        {
            this.model.error ("Invalid Midi data.", ex);
        }
    }


    /**
     * Send a midi CC message to the output.
     *
     * @param cc The CC number
     * @param value The value for the CC
     */
    public void sendCC (final int cc, final int value)
    {
        if (!this.isOutputOpen ())
            return;
        try
        {
            this.receiver.send (new ShortMessage (0xB0, cc, value), 0);
        }
        catch (final InvalidMidiDataException ex)
        {
            this.model.error ("Invalid Midi data.", ex);
        }
    }


    /**
     * Send a midi note to the output.
     *
     * @param channel The midi channel on which to send the note
     * @param note The note to send
     * @param velocity The velocity of the note
     */
    public void sendNote (final int channel, final int note, final int velocity)
    {
        if (!this.isOutputOpen ())
            return;
        try
        {
            this.receiver.send (new ShortMessage (0x90 + channel, note, velocity), 0);
        }
        catch (final InvalidMidiDataException ex)
        {
            this.model.error ("Invalid Note?!", ex);
        }
    }


    /**
     * Send a pitch bend message to the output.
     *
     * @param value The value of the pitch bend
     */
    public void sendPitchbend (final int value)
    {
        if (!this.isOutputOpen ())
            return;
        try
        {
            this.receiver.send (new ShortMessage (0xE0, 0, value), 0);
        }
        catch (final InvalidMidiDataException ex)
        {
            this.model.error ("Invalid Midi pitchbend data?!", ex);
        }
    }


    /**
     * Send a midi note to the output.
     *
     * @param status The status and midi channel
     * @param data1 The first data byte
     * @param data2 The second data byte
     */
    public void sendRaw (final int status, final int data1, final int data2)
    {
        if (!this.isOutputOpen ())
            return;
        try
        {
            this.receiver.send (new ShortMessage (status, data1, data2), 0);
        }
        catch (final InvalidMidiDataException ex)
        {
            this.model.error ("Invalid Midi data.", ex);
        }
    }


    /**
     * Checks if the midi output is open and data can be sent to it.
     *
     * @return True if open
     */
    public boolean isOutputOpen ()
    {
        return this.receiver != null && this.midiOutputDevice.isOpen ();
    }


    /**
     * Checks if the midi input is open and data can be received from it.
     *
     * @return True if open
     */
    public boolean isInputOpen ()
    {
        return this.transmitter != null && this.midiInputDevice.isOpen ();
    }


    /**
     * Closes all used midi devices and frees the resources.
     */
    public void cleanup ()
    {
        if (this.receiver != null)
            this.receiver.close ();
        if (this.transmitter != null)
        {
            final Receiver r = this.transmitter.getReceiver ();
            if (r != null)
                r.close ();
            this.transmitter.setReceiver (null);
            this.transmitter.close ();
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
     * Close devices, which might still be open because the app did not close them.
     */
    public static void cleanupUnusedDevices ()
    {
        for (final MidiDevice device: USED_DEVICES)
            device.close ();
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
