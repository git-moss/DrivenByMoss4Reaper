// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiShortCallback;
import de.mossgrabers.framework.daw.midi.MidiSysExCallback;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.midi.MidiConnection;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import java.util.HashSet;
import java.util.Set;


/**
 * A midi input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class MidiInputImpl implements IMidiInput
{
    private final IHost          host;
    private final MessageSender  sender;
    private final MidiConnection midiConnection;
    private final MidiDevice     device;

    private MidiShortCallback    shortCallback;
    private MidiSysExCallback    sysexCallback;

    private Integer []           keyTranslationTable;
    private Integer []           velocityTranslationTable;

    private Set<String>          filters = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param sender The OSC sender
     * @param midiConnection The midi connection
     * @param device The midi device
     */
    public MidiInputImpl (final IHost host, final MessageSender sender, final MidiConnection midiConnection, final MidiDevice device)
    {
        this.host = host;
        this.sender = sender;
        this.midiConnection = midiConnection;
        this.device = device;

        this.midiConnection.setInput (this.device, (message, timeStamp) -> this.handleMidiMessage (message));
    }


    /** {@inheritDoc} */
    @Override
    public void createNoteInput (final String name, final String... filters)
    {
        if (filters.length == 0)
        {
            this.filters.add ("90");
            this.filters.add ("80");
            return;
        }

        // Remove questionmarks for faster comparison
        for (final String filter: filters)
            this.filters.add (filter.replace ('?', ' ').trim ());
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
        this.keyTranslationTable = table;
    }


    /** {@inheritDoc} */
    @Override
    public void setVelocityTranslationTable (final Integer [] table)
    {
        this.velocityTranslationTable = table;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRepeat ()
    {
        // TODO we need the current track
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
                this.sender.sendOSC ("/vkb_midi/" + channel + "/aftertouch/" + data1, Integer.valueOf (data2));
                break;

            case 0xB0:
                this.sender.sendOSC ("/vkb_midi/" + channel + "/cc/" + data1, Integer.valueOf (data2));
                break;

            case 0xC0:
                this.sender.sendOSC ("/vkb_midi/" + channel + "/program", Integer.valueOf (data1));
                break;

            case 0xE0:
                this.sender.sendOSC ("/vkb_midi/" + channel + "/pitch", Integer.valueOf (data2 * 128 + data1));
                break;

            case 0x80:
            case 0x90:
                this.sender.sendOSC ("/vkb_midi/" + channel + "/note/" + data1, Integer.valueOf (data2));
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

        final String statusHex = StringUtils.toHexStr (Byte.toUnsignedInt ((byte) status));
        boolean sendThru = this.filters.contains (statusHex);
        if (!sendThru)
            sendThru = this.filters.contains (statusHex + StringUtils.toHexStr (data1));

        if (sendThru)
        {
            final int code = status & 0xF0;
            switch (code)
            {
                case 0x80:
                case 0x90:
                    final int key = this.translateKey (data1);
                    if (key >= 0)
                        this.sendRawMidiEvent (status, key, code == 0x80 ? 0 : this.translateVelocity (data2));
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


    private int translateKey (final int key)
    {
        return this.keyTranslationTable == null ? key : this.keyTranslationTable[key].intValue ();
    }


    private int translateVelocity (final int velocity)
    {
        return this.velocityTranslationTable == null ? velocity : this.velocityTranslationTable[velocity].intValue ();
    }
}
