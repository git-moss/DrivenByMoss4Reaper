// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.controller.hardware.BindException;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteControl;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.MidiShortCallback;
import de.mossgrabers.framework.daw.midi.MidiSysExCallback;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.reaper.communication.MessageSender;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;


/**
 * A MIDI input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiInputImpl implements IMidiInput
{
    private final IHost                                               host;
    private final MessageSender                                       sender;
    private final MidiConnection                                      midiConnection;
    private final MidiDevice                                          device;
    private final NoteInputImpl                                       defaultNoteInput;
    private final List<NoteInputImpl>                                 noteInputs                  = new ArrayList<> ();

    private MidiShortCallback                                         shortCallback;
    private MidiSysExCallback                                         sysexCallback;

    private final Map<Integer, Map<Integer, Map<Integer, IHwButton>>> ccButtonMatchers            = new HashMap<> ();
    private final Map<Integer, Map<Integer, Map<Integer, IHwButton>>> noteButtonMatchers          = new HashMap<> ();
    private final Map<Integer, Map<Integer, IHwContinuousControl>>    ccContinuousMatchers        = new HashMap<> ();
    private final Map<Integer, IHwContinuousControl>                  pitchbendContinuousMatchers = new HashMap<> ();
    private final Map<Integer, Map<Integer, IHwContinuousControl>>    ccTouchMatchers             = new HashMap<> ();
    private final Map<Integer, Map<Integer, IHwContinuousControl>>    noteTouchMatchers           = new HashMap<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param sender The OSC sender
     * @param midiConnection The MIDI connection
     * @param device The MIDI device
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
    public void bind (final IHwButton button, final BindType type, final int channel, final int control)
    {
        this.bind (button, type, channel, control, -1);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwButton button, final BindType type, final int channel, final int control, final int value)
    {
        final Map<Integer, Map<Integer, IHwButton>> controlMap;
        if (type == BindType.CC)
            controlMap = this.ccButtonMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<> ());
        else if (type == BindType.NOTE)
            controlMap = this.noteButtonMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<> ());
        else
            throw new BindException (type);

        controlMap.computeIfAbsent (Integer.valueOf (control), key -> new HashMap<> ()).put (Integer.valueOf (value), button);
    }


    /** {@inheritDoc} */
    @Override
    public void unbind (final IHwButton button)
    {
        for (final Map<Integer, Map<Integer, IHwButton>> m: this.ccButtonMatchers.values ())
        {
            for (final Map<Integer, IHwButton> v: m.values ())
            {
                final Collection<IHwButton> values = v.values ();
                if (values.contains (button))
                {
                    values.remove (button);
                    return;
                }
            }
        }
        for (final Map<Integer, Map<Integer, IHwButton>> m: this.noteButtonMatchers.values ())
        {
            for (final Map<Integer, IHwButton> v: m.values ())
            {
                final Collection<IHwButton> values = v.values ();
                if (values.contains (button))
                {
                    values.remove (button);
                    return;
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwRelativeKnob knob, final BindType type, final int channel, final int control, final RelativeEncoding encoding)
    {
        // No encoding
        this.bindContinuous (knob, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwFader fader, final BindType type, final int channel, final int control)
    {
        this.bindContinuous (fader, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwAbsoluteKnob knob, final BindType type, final int channel, final int control)
    {
        this.bindContinuous (knob, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IHwAbsoluteControl absoluteControl, final BindType type, final int channel, final int control)
    {
        this.bindContinuous (absoluteControl, type, channel, control);
    }


    private void bindContinuous (final IHwContinuousControl continuousControl, final BindType type, final int channel, final int control)
    {
        switch (type)
        {
            case CC:
                this.ccContinuousMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<> ()).put (Integer.valueOf (control), continuousControl);
                break;
            case PITCHBEND:
                this.pitchbendContinuousMatchers.put (Integer.valueOf (channel), continuousControl);
                break;
            default:
                throw new BindException (type);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final IHwRelativeKnob relativeKnob, final BindType type, final int channel, final int control)
    {
        this.bindTouchContinuous (relativeKnob, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final IHwFader fader, final BindType type, final int channel, final int control)
    {
        this.bindTouchContinuous (fader, type, channel, control);
    }


    private void bindTouchContinuous (final IHwContinuousControl continuousControl, final BindType type, final int channel, final int control)
    {
        switch (type)
        {
            case CC:
                this.ccTouchMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<> ()).put (Integer.valueOf (control), continuousControl);
                break;
            case NOTE:
                this.noteTouchMatchers.computeIfAbsent (Integer.valueOf (channel), key -> new HashMap<> ()).put (Integer.valueOf (control), continuousControl);
                break;
            default:
                throw new BindException (type);
        }
    }


    /**
     * Handle all kind of MIDI messages.
     *
     * @param message The message to handle
     */
    public void handleMidiMessage (final MidiMessage message)
    {
        try
        {
            if (message instanceof SysexMessage sysex)
                this.handleSysexMessage (sysex);
            else if (message instanceof ShortMessage sm)
                this.handleShortMessage (sm);
            else
                this.host.error ("Unknown MIDI class.");
        }
        catch (final RuntimeException ex)
        {
            this.host.error ("Could not handle midi message.", ex);
        }
    }


    /**
     * Handle MIDI short messages (3 bytes).
     *
     * @param message The message to handle
     */
    private void handleShortMessage (final ShortMessage message)
    {
        final int status = message.getStatus ();
        int data1 = message.getData1 ();
        int data2 = message.getData2 ();

        final int command = status & 0xF0;
        final int channel = status & 0xF;

        // Fix for wrong pitchbend values, see https://bugs.openjdk.java.net/browse/JDK-8075073
        if (OperatingSystem.get () == OperatingSystem.LINUX && command == 0xE0)
        {
            int pitchbendValue = data2 * 128 + data1;
            pitchbendValue = pitchbendValue ^ 0x2000;
            data1 = pitchbendValue % 128;
            data2 = pitchbendValue / 128;
        }

        final boolean isProcessed = this.handleControls (command, channel, data1, data2);

        for (final NoteInputImpl noteInput: this.noteInputs)
        {
            if (!noteInput.acceptFilter (status, data1))
                continue;
            switch (command)
            {
                case 0x80:
                case 0x90:
                    final int key = noteInput.translateKey (data1);
                    if (key >= 0)
                        this.sendRawMidiEvent (status, key, command == 0x80 ? 0 : noteInput.translateVelocity (data2));
                    break;

                default:
                    if (!isProcessed)
                        this.sendRawMidiEvent (status, data1, data2);
                    break;
            }
        }

        if (isProcessed)
            return;

        // Ignore active sensing
        if (status == 0xF8)
            return;

        if (this.shortCallback != null)
            this.shortCallback.handleMidi (status, data1, data2);
    }


    /**
     * Match controls bound to MIDI events and execute their commands.
     *
     * @param code The MIDI command (on channel 1)
     * @param channel The MIDI channel
     * @param data1 The first data byte
     * @param data2 The second data byte
     * @return True if the midi event was matched
     */
    private boolean handleControls (final int code, final int channel, final int data1, final int data2)
    {
        switch (code)
        {
            case 0xB0:
                return this.handleControlsCC (channel, data1, data2);

            case 0x80:
            case 0x90:
                return this.handleControlsNote (channel, data1, data2, code == 0x80 || data2 == 0);

            case 0xE0:
                return this.handleControlsPitchbend (channel, data1, data2);

            default:
                return false;
        }
    }


    /**
     * Handle all controls bound to a MIDI note event.
     *
     * @param channel The MIDI channel
     * @param data1 The first data byte
     * @param data2 The second data byte
     * @param isNoteOff True if the MIDI event is Note Off (otherwise Note On)
     * @return True if the midi event was matched
     */
    protected boolean handleControlsNote (final int channel, final int data1, final int data2, final boolean isNoteOff)
    {
        final Map<Integer, Map<Integer, IHwButton>> noteMap = this.noteButtonMatchers.get (Integer.valueOf (channel));
        if (noteMap != null)
        {
            final Map<Integer, IHwButton> valueMap = noteMap.get (Integer.valueOf (data1));
            if (valueMap != null)
            {
                for (final Entry<Integer, IHwButton> valueButtonPair: valueMap.entrySet ())
                {
                    final int value = valueButtonPair.getKey ().intValue ();
                    if (value == -1 || value == data2)
                    {
                        valueButtonPair.getValue ().trigger (isNoteOff ? ButtonEvent.UP : ButtonEvent.DOWN, data2 / 127.0);
                        return true;
                    }
                }
            }
        }

        final Map<Integer, IHwContinuousControl> noteTouchMap = this.noteTouchMatchers.get (Integer.valueOf (channel));
        if (noteTouchMap != null)
        {
            final IHwContinuousControl ccButton = noteTouchMap.get (Integer.valueOf (data1));
            if (ccButton != null && ccButton.isBound ())
            {
                ccButton.triggerTouch (!isNoteOff);
                return true;
            }
        }

        return false;
    }


    protected boolean handleControlsCC (final int channel, final int data1, final int data2)
    {
        final Map<Integer, Map<Integer, IHwButton>> ccButtonMap = this.ccButtonMatchers.get (Integer.valueOf (channel));
        if (ccButtonMap != null)
        {
            final Map<Integer, IHwButton> valueMap = ccButtonMap.get (Integer.valueOf (data1));
            if (valueMap != null)
            {
                for (final Entry<Integer, IHwButton> valueButtonPair: valueMap.entrySet ())
                {
                    final int value = valueButtonPair.getKey ().intValue ();
                    if (value == -1 || value == data2)
                    {
                        valueButtonPair.getValue ().trigger (value == 0 || data2 > 0 ? ButtonEvent.DOWN : ButtonEvent.UP);
                        return true;
                    }
                }
            }

        }

        final Map<Integer, IHwContinuousControl> ccTouchMap = this.ccTouchMatchers.get (Integer.valueOf (channel));
        if (ccTouchMap != null)
        {
            final IHwContinuousControl ccButton = ccTouchMap.get (Integer.valueOf (data1));
            if (ccButton != null && ccButton.isBound ())
            {
                ccButton.triggerTouch (data2 > 0);
                return true;
            }
        }

        final Map<Integer, IHwContinuousControl> ccContinuousMap = this.ccContinuousMatchers.get (Integer.valueOf (channel));
        if (ccContinuousMap != null)
        {
            final IHwContinuousControl ccContinuous = ccContinuousMap.get (Integer.valueOf (data1));
            if (ccContinuous != null && ccContinuous.isBound ())
            {
                ccContinuous.handleValue (data2 / 127.0);
                return true;
            }
        }

        return false;
    }


    protected boolean handleControlsPitchbend (final int channel, final int data1, final int data2)
    {
        final IHwContinuousControl ccContinuous = this.pitchbendContinuousMatchers.get (Integer.valueOf (channel));
        if (ccContinuous != null && ccContinuous.isBound ())
        {
            final int pitchbendValue = data2 * 128 + data1;
            ccContinuous.handleValue (pitchbendValue / 16383.0);
            return true;
        }
        return false;
    }


    private void handleSysexMessage (final SysexMessage sysexMessage)
    {
        if (this.sysexCallback == null)
            return;

        // F0 is not included in getData()
        final StringBuilder dataString = new StringBuilder ("F0");
        for (final byte data: sysexMessage.getData ())
            dataString.append (String.format ("%02x", Integer.valueOf (data & 0xFF)));
        this.sysexCallback.handleMidi (dataString.toString ().toUpperCase (Locale.US));
    }
}
