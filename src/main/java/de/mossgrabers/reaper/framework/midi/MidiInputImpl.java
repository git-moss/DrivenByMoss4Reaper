// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.MidiShortCallback;
import de.mossgrabers.framework.daw.midi.MidiSysExCallback;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.midi.MidiConnection;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * A midi input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiInputImpl implements IMidiInput
{
    private static final String       VKB_MIDI   = "/vkb_midi/";

    private final IHost               host;
    private final MessageSender       sender;
    private final MidiConnection      midiConnection;
    private final MidiDevice          device;
    private final NoteInputImpl       defaultNoteInput;
    private final List<NoteInputImpl> noteInputs = new ArrayList<> ();

    private MidiShortCallback         shortCallback;
    private MidiSysExCallback         sysexCallback;


    /**
     * Constructor.
     *
     * @param host The host
     * @param sender The OSC sender
     * @param midiConnection The midi connection
     * @param device The midi device
     * @param filters a filter string formatted as hexadecimal value with `?` as wildcard. For
     *            example `80????` would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     */
    public MidiInputImpl (final IHost host, final MessageSender sender, final MidiConnection midiConnection, final MidiDevice device, final String [] filters)
    {
        this.host = host;
        this.sender = sender;
        this.midiConnection = midiConnection;
        this.device = device;

        this.midiConnection.setInput (this.device, (message, timeStamp) -> {
            try
            {
                this.handleMidiMessage (message);
            }
            catch (final RuntimeException ex)
            {
                host.error ("Could not handle midi message.", ex);
            }
        });

        this.defaultNoteInput = new NoteInputImpl (filters);
        this.noteInputs.add (this.defaultNoteInput);
    }


    /** {@inheritDoc} */
    @Override
    public INoteInput createNoteInput (final String name, final String... filters)
    {
        final NoteInputImpl noteInput = new NoteInputImpl (filters);
        this.noteInputs.add (noteInput);
        return noteInput;
    }


    /** {@inheritDoc} */
    @Override
    public void setMidiCallback (final MidiShortCallback callback)
    {
        this.shortCallback = callback;
    }


    /** {@inheritDoc} */
    @Override
    public void setSysexCallback (final MidiSysExCallback callback)
    {
        this.sysexCallback = callback;
    }


    /** {@inheritDoc} */
    @Override
    public void setKeyTranslationTable (final Integer [] table)
    {
        if (this.defaultNoteInput != null)
            this.defaultNoteInput.setKeyTranslationTable (table);
    }


    /** {@inheritDoc} */
    @Override
    public void setVelocityTranslationTable (final Integer [] table)
    {
        if (this.defaultNoteInput != null)
            this.defaultNoteInput.setVelocityTranslationTable (table);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRepeat ()
    {
        // TODO Reaper We need the current track
        this.sender.sendOSC ("/track/1/noterepeat", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    public void sendRawMidiEvent (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0x0F;

        switch (code)
        {
            case 0xA0:
            case 0xD0:
                this.sender.sendOSC (VKB_MIDI + channel + "/aftertouch/" + data1, Integer.valueOf (data2));
                break;

            case 0xB0:
                this.sender.sendOSC (VKB_MIDI + channel + "/cc/" + data1, Integer.valueOf (data2));
                break;

            case 0xC0:
                this.sender.sendOSC (VKB_MIDI + channel + "/program", Integer.valueOf (data1));
                break;

            case 0xE0:
                this.sender.sendOSC (VKB_MIDI + channel + "/pitch", Integer.valueOf (data2 * 128 + data1));
                break;

            case 0x80:
            case 0x90:
                this.sender.sendOSC (VKB_MIDI + channel + "/note/" + data1, Integer.valueOf (data2));
                break;

            default:
                this.host.println ("sendRawMidiEvent not implemented for status: " + status);
                break;
        }
    }


    private void handleMidiMessage (final MidiMessage message)
    {
        if (message instanceof SysexMessage)
        {
            this.handleSysexMessage ((SysexMessage) message);
            return;
        }

        final byte [] msg = message.getMessage ();
        if (msg.length != 3)
            return;

        final int status = msg[0];
        final byte data1 = msg[1];
        final byte data2 = msg[2];

        for (final NoteInputImpl noteInput: this.noteInputs)
        {
            if (!noteInput.acceptFilter (status, data1))
                continue;
            final int code = status & 0xF0;
            switch (code)
            {
                case 0x80:
                case 0x90:
                    final int key = noteInput.translateKey (data1);
                    if (key >= 0)
                        this.sendRawMidiEvent (status, key, code == 0x80 ? 0 : noteInput.translateVelocity (data2));
                    break;
                default:
                    this.sendRawMidiEvent (status, data1, data2);
                    break;
            }
        }

        if (this.shortCallback != null)
            this.shortCallback.handleMidi (status, data1, data2);
    }


    private void handleSysexMessage (final SysexMessage sysexMessage)
    {
        if (this.sysexCallback == null)
            return;

        // F0 is not included in getData()
        final StringBuilder dataString = new StringBuilder ("F0");
        for (final byte data: sysexMessage.getData ())
            dataString.append (String.format ("%02x", Integer.valueOf (data & 0xFF)));
        this.sysexCallback.handleMidi (dataString.toString ().toUpperCase ());
    }
}
