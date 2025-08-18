// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.NIHIASysExCallback;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The Komplete Kontrol MkII control surface.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolControlSurface extends AbstractControlSurface<KontrolProtocolConfiguration>
{
    ///////////////////////////////////////////////////////////////////////////////
    // SYSEX

    /** Switches a parameter on or off on. */
    public static final int          KONTROL_SURFACE_CONFIGURATION              = 0x03;
    /** New for v4. */
    public static final int          KONTROL_IDENTITY                           = 0x07;
    /** New for v4. */
    public static final int          KONTROL_SET_TEMPO                          = 0x19;

    /** Track available (actually the type the track, see TrackType). */
    public static final int          KONTROL_TRACK_AVAILABLE                    = 0x40;
    /** Name of the Komplete-plugin ID on the track, if exists. */
    public static final int          KONTROL_TRACK_INSTANCE                     = 0x41;
    /** Select a track. */
    public static final int          KONTROL_TRACK_SELECTED                     = 0x42;
    /** Mute a track. */
    public static final int          KONTROL_TRACK_MUTE                         = 0x43;
    /** Solo a track. */
    public static final int          KONTROL_TRACK_SOLO                         = 0x44;
    /** Arm a track. */
    public static final int          KONTROL_TRACK_RECARM                       = 0x45;
    /** Volume of a track. */
    public static final int          KONTROL_TRACK_VOLUME_TEXT                  = 0x46;
    /** Panning of a track. */
    public static final int          KONTROL_TRACK_PAN_TEXT                     = 0x47;
    /** Name of a track. */
    public static final int          KONTROL_TRACK_NAME                         = 0x48;
    /** VU of a track. */
    public static final int          KONTROL_TRACK_VU                           = 0x49;
    /** Track muted by solo. */
    public static final int          KONTROL_TRACK_MUTED_BY_SOLO                = 0x4A;
    /** New for v4: Track color. */
    public static final int          KONTROL_TRACK_COLOR                        = 0x4B;

    ///////////////////////////////////////////////////////////////////////////////
    // MIDI CC

    /** Command to initialize the protocol handshake (and acknowledge). */
    public static final int          CMD_HELLO                                  = 0x01;
    /** Command to stop the protocol. */
    public static final int          CMD_GOODBYE                                = 0x02;

    /** New for v4: The Shift button. */
    public static final int          KONTROL_SHIFT                              = 0x04;

    /** The play button. */
    public static final int          KONTROL_PLAY                               = 0x10;
    /** The restart button (Shift+Play). No LED. */
    public static final int          KONTROL_RESTART                            = 0x11;
    /** The record button. */
    public static final int          KONTROL_RECORD                             = 0x12;
    /** The count-in button (Shift+Rec). */
    public static final int          KONTROL_COUNT_IN                           = 0x13;
    /** The stop button. */
    public static final int          KONTROL_STOP                               = 0x14;
    /** The clear button. */
    public static final int          KONTROL_CLEAR                              = 0x15;
    /** The loop button. */
    public static final int          KONTROL_LOOP                               = 0x16;
    /** The metro button. */
    public static final int          KONTROL_METRO                              = 0x17;
    /** The tempo button. No LED. */
    public static final int          KONTROL_TAP_TEMPO                          = 0x18;

    /** The undo button. */
    public static final int          KONTROL_UNDO                               = 0x20;
    /** The redo button (Shift+Undo). */
    public static final int          KONTROL_REDO                               = 0x21;
    /** The quantize button. */
    public static final int          KONTROL_QUANTIZE                           = 0x22;
    /** The auto button. */
    public static final int          KONTROL_AUTOMATION                         = 0x23;

    /** Track navigation. */
    public static final int          KONTROL_NAVIGATE_TRACKS                    = 0x30;
    /** Track bank navigation. */
    public static final int          KONTROL_NAVIGATE_BANKS                     = 0x31;
    /** Clip navigation. */
    public static final int          KONTROL_NAVIGATE_CLIPS                     = 0x32;

    /** Transport navigation. */
    public static final int          KONTROL_NAVIGATE_MOVE_TRANSPORT            = 0x34;
    /** Loop navigation. */
    public static final int          KONTROL_NAVIGATE_MOVE_LOOP                 = 0x35;

    /** Change the volume of a track 0x50 - 0x57. */
    public static final int          KONTROL_TRACK_VOLUME                       = 0x50;
    /** Change the panning of a track 0x58 - 0x5F. */
    public static final int          KONTROL_TRACK_PAN                          = 0x58;

    /** Play the currently selected clip. */
    public static final int          KONTROL_PLAY_SELECTED_CLIP                 = 0x60;
    /** Stop the clip playing on the currently selected track. */
    public static final int          KONTROL_STOP_CLIP                          = 0x61;
    /** Start the currently selected scene. */
    public static final int          KONTROL_PLAY_SCENE                         = 0x62;
    /** Record Session button pressed. */
    public static final int          KONTROL_RECORD_SESSION                     = 0x63;
    /** Increase/decrease volume of selected track. */
    public static final int          KONTROL_CHANGE_SELECTED_TRACK_VOLUME       = 0x64;
    /** Increase/decrease pan of selected track. */
    public static final int          KONTROL_CHANGE_SELECTED_TRACK_PAN          = 0x65;
    /** Toggle mute of the selected track / Selected track muted. */
    public static final int          KONTROL_SELECTED_TRACK_MUTE                = 0x66;
    /** Toggle solo of the selected track / Selected track soloed. */
    public static final int          KONTROL_SELECTED_TRACK_SOLO                = 0x67;
    /** Selected track available. */
    public static final int          KONTROL_SELECTED_TRACK_AVAILABLE           = 0x68;
    /** Selected track muted by solo. */
    public static final int          KONTROL_SELECTED_TRACK_MUTED_BY_SOLO       = 0x69;

    /** New for v4. */
    public static final int          KONTROL_SELECTED_TRACK_SELECT_PLUGIN       = 0x70;
    public static final int          KONTROL_SELECTED_TRACK_PLUGIN_CHAIN_INFO   = 0x71;
    public static final int          KONTROL_SELECTED_TRACK_PARAM_NAME          = 0x72;
    public static final int          KONTROL_SELECTED_TRACK_PARAM_DISPLAY_VALUE = 0x73;
    public static final int          KONTROL_SELECTED_TRACK_PARAM_PAGE_NUM      = 0x74;
    public static final int          KONTROL_ADJUST_PARAMETER_VALUE             = 0x7F;

    private static final double      TEN_NS_PER_MINUTE                          = 60e8;

    private static final byte []     NHIA_SYSEX_HEADER                          = new byte []
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x09,
        0x00,
        0x00,
        0x44,
        0x43,
        0x01,
        0x00
    };

    private final int                requiredVersion;
    private int                      protocolVersion                            = KontrolProtocol.MAX_VERSION;
    private final NIHIASysExCallback sysexCallback;

    private final Object             cacheLock                                  = new Object ();
    private final ValueCache         valueCache                                 = new ValueCache ();
    private double                   cachedTempo                                = 0;
    private final Object             handshakeLock                              = new Object ();
    private boolean                  isConnectedToNIHIA                         = false;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param sysexCallback Callback for value changes received via SysEx
     * @param version The version number of the NIHIA protocol to request
     */
    public KontrolProtocolControlSurface (final IHost host, final ColorManager colorManager, final KontrolProtocolConfiguration configuration, final IMidiOutput output, final IMidiInput input, final NIHIASysExCallback sysexCallback, final int version)
    {
        super (host, configuration, colorManager, output, input, null, 800, 300);

        this.requiredVersion = version;
        this.defaultMidiChannel = 15;
        this.sysexCallback = sysexCallback;

        input.setSysexCallback (this::handleSysEx);
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        super.internalShutdown ();

        synchronized (this.handshakeLock)
        {
            // Stop flush
            this.isConnectedToNIHIA = false;

            for (int i = 0; i < 8; i++)
                this.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_AVAILABLE, TrackType.EMPTY, i);

            this.sendCommand (KontrolProtocolControlSurface.CMD_GOODBYE, 0);
        }
    }


    /**
     * Returns true if the handshake with the Native Instruments Host Integration was successfully
     * executed.
     *
     * @return True if connected to the NIHIA
     */
    public boolean isConnectedToNIHIA ()
    {
        synchronized (this.handshakeLock)
        {
            return this.isConnectedToNIHIA;
        }
    }


    /**
     * Initialize the handshake with the NIHIA.
     */
    public void initHandshake ()
    {
        this.sendCommand (CMD_HELLO, this.requiredVersion);
    }


    /**
     * Call if the handshake response was successfully received from the NIHIA.
     *
     * @param protocol The protocol version
     */
    public void handshakeSuccess (final int protocol)
    {
        synchronized (this.handshakeLock)
        {
            this.setProtocolVersion (protocol);

            // Initial flush of the whole DAW state...
            this.clearCache ();

            this.isConnectedToNIHIA = true;
        }

        this.sysexCallback.sendDAWInfo ();
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        this.sendCommand (cc, value);
    }


    /**
     * Send a command to the Komplete Kontrol.
     *
     * @param command The command number
     * @param value The value
     */
    public void sendCommand (final int command, final int value)
    {
        this.output.sendCCEx (15, command, value);
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param track The track index (0-7)
     */
    public void sendKontrolTrackSysEx (final int stateID, final int value, final int track)
    {
        this.sendKontrolTrackSysEx (stateID, value, track, "");
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param track The track index (0-7)
     * @param info An info string
     */
    public void sendKontrolTrackSysEx (final int stateID, final int value, final int track, final String info)
    {
        this.sendKontrolTrackSysEx (stateID, value, track, StringUtils.fixASCII (info).chars ().toArray ());
    }


    /**
     * Send SysEx to the Kontrol.
     *
     * @param stateID The state ID (command)
     * @param value The value to send
     * @param track The track index (0-7)
     * @param info Further info data
     */
    public void sendKontrolTrackSysEx (final int stateID, final int value, final int track, final int [] info)
    {
        synchronized (this.cacheLock)
        {
            if (this.valueCache.store (track, stateID, value, info))
                return;
        }

        final byte [] data = new byte [3 + info.length];
        data[0] = (byte) stateID;
        data[1] = (byte) value;
        data[2] = (byte) track;
        for (int i = 0; i < info.length; i++)
            data[3 + i] = (byte) info[i];

        this.sendNHIASysEx (data);
    }


    /**
     * Send some global values like tempo to the Kontrol.
     * 
     * @param model The data model
     */
    public void sendGlobalValues (final IModel model)
    {
        this.sendTempo (model.getTransport ().getTempo (), false);
    }


    /**
     * Send the tempo to the Kontrol.
     * 
     * @param tempo The tempo
     * @param ignoreCache Clears the cache and resends always
     */
    public void sendTempo (final double tempo, final boolean ignoreCache)
    {
        synchronized (this.cacheLock)
        {
            if (!ignoreCache && Double.compare (tempo, this.cachedTempo) == 0)
                return;
            this.cachedTempo = tempo;
        }

        final byte [] data = new byte [8];
        data[0] = KONTROL_SET_TEMPO;
        data[1] = 0;
        data[2] = 0;
        final long nsPerMinute = (long) (TEN_NS_PER_MINUTE / tempo);
        for (int i = 0; i < 5; i++)
            data[3 + i] = (byte) ((nsPerMinute >> (i * 7)) & 0x7F);

        this.sendNHIASysEx (data);
    }


    /**
     * Sends information about the connected DAW.
     * 
     * @param versionMajor The major version of the DAW
     * @param versionMinor The minor version of the DAW
     * @param dawName The name of the DAW
     */
    public void sendDAWInfo (final int versionMajor, final int versionMinor, final String dawName)
    {
        final int [] array = StringUtils.fixASCII (dawName).chars ().toArray ();
        final byte [] data = new byte [3 + array.length];
        data[0] = KONTROL_IDENTITY;
        data[1] = (byte) versionMajor;
        data[2] = (byte) versionMinor;
        for (int i = 0; i < array.length; i++)
            data[3 + i] = (byte) array[i];
        this.sendNHIASysEx (data);
    }


    /** {@inheritDoc} */
    @Override
    public void clearCache ()
    {
        synchronized (this.cacheLock)
        {
            this.valueCache.clearCache ();
        }

        super.clearCache ();
    }


    private void sendNHIASysEx (final byte [] data)
    {
        super.sendSysex (NHIA_SYSEX_HEADER, data);
    }


    /**
     * Handle incoming system exclusive messages.
     *
     * @param data The data of the system exclusive message
     */
    private void handleSysEx (final String data)
    {
        final int [] byteData = StringUtils.fromHexStr (data);
        if (!startsWithPrefix (byteData))
        {
            this.host.error (String.format ("Unused sysex command: %s", data));
            return;
        }

        switch (byteData[10])
        {
            case KONTROL_SET_TEMPO:
                long nsPerMinute = 0;
                for (int i = 0; i < 5; i++)
                    nsPerMinute |= (long) byteData[13 + i] << (i * 7);
                final double tempo = TEN_NS_PER_MINUTE / nsPerMinute;
                final long roundedTo2Fractions = Math.round (tempo * 100.0);
                this.sysexCallback.setTempo (roundedTo2Fractions / 100.0);
                break;

            default:
                this.host.error (String.format ("Unused NHIA sysex command: %02X", Integer.valueOf (byteData[10])));
                break;
        }
    }


    private static boolean startsWithPrefix (final int [] byteData)
    {
        if (byteData.length < NHIA_SYSEX_HEADER.length)
            return false;
        for (int i = 0; i < NHIA_SYSEX_HEADER.length; i++)
        {
            // & 0xFF ensures the int is compared as an unsigned byte
            if ((byteData[i] & 0xFF) != (NHIA_SYSEX_HEADER[i] & 0xFF))
                return false;
        }
        return true;
    }


    /**
     * Get the protocol number of the currently connected Komplete Kontrol.
     *
     * @return The protocol number
     */
    public int getProtocolVersion ()
    {
        return this.protocolVersion;
    }


    /**
     * Set the protocol number of the currently connected Komplete Kontrol.
     *
     * @param protocolVersion The protocol number
     */
    public void setProtocolVersion (final int protocolVersion)
    {
        this.protocolVersion = protocolVersion;
    }


    /**
     * Caches the values of the system exclusive values.
     */
    private static class ValueCache
    {
        private final List<List<int []>> cache = new ArrayList<> (8);


        /**
         * Constructor.
         */
        public ValueCache ()
        {
            this.clearCache ();
        }


        /**
         * Clear the cache.
         */
        public final void clearCache ()
        {
            for (int i = 0; i < 8; i++)
            {
                final List<int []> e = new ArrayList<> (128);
                for (int j = 0; j < 128; j++)
                    e.add (new int [0]);
                this.cache.add (e);
            }
        }


        /**
         * Stores the value and data in the cache for the track and stateID.
         *
         * @param track The track number
         * @param stateID The state id
         * @param value The value
         * @param data Further data
         * @return False if cache was updated otherwise the given value and data are already stored
         */
        public boolean store (final int track, final int stateID, final int value, final int [] data)
        {
            final List<int []> trackItem = this.cache.get (track);
            final int [] values = trackItem.get (stateID);

            final int [] newValues = new int [1 + data.length];
            newValues[0] = value;
            if (data.length > 0)
                System.arraycopy (data, 0, newValues, 1, data.length);

            if (Arrays.equals (values, newValues))
                return true;

            trackItem.set (stateID, newValues);
            return false;
        }
    }
}