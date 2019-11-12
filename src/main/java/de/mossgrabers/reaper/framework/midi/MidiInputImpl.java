// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.controller.hardware.BindException;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IButton;
import de.mossgrabers.framework.controller.hardware.IFader;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.MidiShortCallback;
import de.mossgrabers.framework.daw.midi.MidiSysExCallback;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.reaper.communication.MessageSender;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A midi input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiInputImpl implements IMidiInput
{
    private final IHost                               host;
    private final MessageSender                       sender;
    private final MidiConnection                      midiConnection;
    private final MidiDevice                          device;
    private final NoteInputImpl                       defaultNoteInput;
    private final List<NoteInputImpl>                 noteInputs         = new ArrayList<> ();

    private MidiShortCallback                         shortCallback;
    private MidiSysExCallback                         sysexCallback;

    private final Map<Integer, Map<Integer, IButton>> ccButtonMatchers   = new HashMap<> ();
    private final Map<Integer, Map<Integer, IButton>> noteButtonMatchers = new HashMap<> ();
    private final Map<Integer, Map<Integer, IFader>>  ccFaderMatchers    = new HashMap<> ();


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

        this.midiConnection.setInput (this.device, (message, timeStamp) -> this.handleMidiMessage (message));
        this.defaultNoteInput = new NoteInputImpl (sender, filters);
        this.noteInputs.add (this.defaultNoteInput);
    }


    /** {@inheritDoc} */
    @Override
    public INoteInput createNoteInput (final String name, final String... filters)
    {
        final NoteInputImpl noteInput = new NoteInputImpl (this.sender, filters);
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
    public void sendRawMidiEvent (final int status, final int data1, final int data2)
    {
        this.sender.processMidiArg (status, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public INoteInput getDefaultNoteInput ()
    {
        return this.defaultNoteInput;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IButton button, final BindType type, final int channel, final int value)
    {
        if (type == BindType.CC)
            this.ccButtonMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<Integer, IButton> ()).put (Integer.valueOf (value), button);
        else if (type == BindType.NOTE)
            this.noteButtonMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<Integer, IButton> ()).put (Integer.valueOf (value), button);
        else
            throw new BindException (type);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IFader fader, final BindType type, final int channel, final int value)
    {
        if (type == BindType.CC)
            this.ccFaderMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<Integer, IFader> ()).put (Integer.valueOf (value), fader);
        else
            throw new BindException (type);
    }


    private void handleMidiMessage (final MidiMessage message)
    {
        try
        {
            if (message instanceof SysexMessage)
                this.handleSysexMessage ((SysexMessage) message);
            else if (message instanceof ShortMessage)
                this.handleShortMessage ((ShortMessage) message);
            else
                this.host.error ("Unknown MIDI class.");
        }
        catch (final RuntimeException ex)
        {
            this.host.error ("Could not handle midi message.", ex);
        }
    }


    private void handleShortMessage (final ShortMessage message)
    {
        final int status = message.getStatus ();
        final int data1 = message.getData1 ();
        final int data2 = message.getData2 ();

        final int code = status & 0xF0;
        final int channel = status & 0xF;

        if (this.handleControls (code, channel, data1, data2))
            return;

        for (final NoteInputImpl noteInput: this.noteInputs)
        {
            if (!noteInput.acceptFilter (status, data1))
                continue;
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


    private boolean handleControls (final int code, final int channel, final int data1, final int data2)
    {
        switch (code)
        {
            case 0xB0:
                final Map<Integer, IButton> ccButtonMap = this.ccButtonMatchers.get (Integer.valueOf (channel));
                if (ccButtonMap != null)
                {
                    final IButton ccButton = ccButtonMap.get (Integer.valueOf (data1));
                    if (ccButton != null)
                    {
                        ccButton.trigger (data2 > 0 ? ButtonEvent.DOWN : ButtonEvent.UP);
                        return true;
                    }
                }
                final Map<Integer, IFader> ccFaderMap = this.ccFaderMatchers.get (Integer.valueOf (channel));
                if (ccFaderMap != null)
                {
                    final IFader ccFader = ccFaderMap.get (Integer.valueOf (data1));
                    if (ccFader != null)
                    {
                        ccFader.handleValue (data2 / 127.0);
                        return true;
                    }
                }
                return false;

            case 0x80:
            case 0x90:
                final Map<Integer, IButton> noteMap = this.noteButtonMatchers.get (Integer.valueOf (channel));
                if (noteMap == null)
                    return false;
                final IButton noteButton = noteMap.get (Integer.valueOf (data1));
                if (noteButton == null)
                    return false;
                noteButton.trigger (data2 > 0 && code != 0x80 ? ButtonEvent.DOWN : ButtonEvent.UP);
                return true;

            default:
                return false;
        }
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
