// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.daw.SendBankImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelImpl extends ItemImpl implements IChannel
{
    protected IValueChanger valueChanger;

    private ChannelType     type;
    protected int           volume;
    private String          volumeStr          = "";
    private int             vuLeft;
    private int             vuRight;
    private String          panStr             = "";
    protected int           pan;
    private boolean         isMute;
    private boolean         isSolo;
    private boolean         isActivated        = true;
    private double []       color;

    private final ISendBank sendBank;

    private boolean         isVolumeBeingTouched;
    private int             lastReceivedVolume = -1;
    private boolean         isPanBeingTouched;
    private int             lastReceivedPan    = -1;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public ChannelImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int index, final int numSends)
    {
        super (host, sender, index);
        this.valueChanger = valueChanger;

        this.setName ("Track " + (index + 1));

        this.sendBank = new SendBankImpl (host, sender, valueChanger, index, numSends);
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return this.type == null ? ChannelType.UNKNOWN : this.type;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        return this.doesExist () && this.isActivated;
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr ()
    {
        return this.doesExist () ? this.volumeStr : "";
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr (final int limit)
    {
        final String vs = this.getVolumeStr ();
        return vs.length () > limit ? vs.substring (0, limit) : vs;
    }


    /** {@inheritDoc} */
    @Override
    public int getVolume ()
    {
        return this.volume;
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int control)
    {
        this.setVolume (this.valueChanger.changeValue (control, this.getVolume ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final double value)
    {
        this.volume = (int) value;
        this.sendTrackOSC ("volume", Double.valueOf (this.valueChanger.toNormalizedValue (this.getVolume ())));
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        this.setVolume (0.6 * this.valueChanger.getUpperBound ());
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        this.sendTrackOSC ("volume/touch", Integer.valueOf (isBeingTouched ? 1 : 0));
        this.handleVolumeTouch (isBeingTouched);
    }


    /**
     * Prevent updating of the value from the DAW when the user edits the value, otherwise the value
     * "jumps" due to roundtrip delays.
     *
     * @param isBeingTouched True if touched
     */
    protected void handleVolumeTouch (final boolean isBeingTouched)
    {
        this.isVolumeBeingTouched = isBeingTouched;

        if (this.isVolumeBeingTouched || this.lastReceivedVolume == -1)
            return;

        this.volume = this.lastReceivedVolume;
        this.lastReceivedVolume = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final boolean indicate)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedVolume ()
    {
        return this.getVolume ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr ()
    {
        return this.doesExist () ? this.panStr : "";
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr (final int limit)
    {
        final String ps = this.getPanStr ();
        return ps.length () > limit ? ps.substring (0, limit) : ps;
    }


    /** {@inheritDoc} */
    @Override
    public int getPan ()
    {
        return this.pan;
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int control)
    {
        this.setPan (this.valueChanger.changeValue (control, this.getPan ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final double value)
    {
        this.pan = (int) value;
        this.sendTrackOSC ("pan", Double.valueOf (this.valueChanger.toNormalizedValue (this.getPan ())));
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        this.setPan (0.5 * this.valueChanger.getUpperBound ());
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        this.sendTrackOSC ("pan/touch", Integer.valueOf (isBeingTouched ? 1 : 0));
        this.handlePanTouch (isBeingTouched);
    }


    /**
     * Prevent updating of the value from the DAW when the user edits the value, otherwise the value
     * "jumps" due to roundtrip delays.
     *
     * @param isBeingTouched True if touched
     */
    protected void handlePanTouch (final boolean isBeingTouched)
    {
        this.isPanBeingTouched = isBeingTouched;

        if (this.isPanBeingTouched || this.lastReceivedPan == -1)
            return;

        this.pan = this.lastReceivedPan;
        this.lastReceivedPan = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final boolean indicate)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        return this.getPan ();
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return this.color == null ? new double []
        {
            0.2,
            0.2,
            0.2
        } : this.color;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.sendTrackOSC ("color", "RGB(" + Math.round (red * 255) + "," + Math.round (green * 255) + "," + Math.round (blue * 255) + ")");
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        return this.doesExist () && this.isMute;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        return this.doesExist () && this.isSolo;
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        return (this.vuLeft + this.vuRight) / 2;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        return this.vuLeft;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        return this.vuRight;
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        if (value)
        {
            this.sender.invokeAction (Actions.UNLOCK_TRACK_CONTROLS);
            this.sender.invokeAction (Actions.SET_ALL_FX_ONLINE);
            this.sender.invokeAction (Actions.UNMUTE_ALL_RECEIVES_ON_SELECTED_TRACKS);
            this.sender.invokeAction (Actions.UNMUTE_ALL_SENDS_ON_SELECTED_TRACKS);
            this.sender.invokeAction (Actions.UNBYPASS_ALL_FX_ON_SELECTED_TRACKS);
            this.sender.invokeAction (Actions.UNMUTE_TRACKS);
        }
        else
        {
            this.sender.invokeAction (Actions.MUTE_TRACKS);
            this.sender.invokeAction (Actions.BYPASS_ALL_FX_ON_SELECTED_TRACKS);
            this.sender.invokeAction (Actions.MUTE_ALL_SENDS_ON_SELECTED_TRACKS);
            this.sender.invokeAction (Actions.MUTE_ALL_RECEIVES_ON_SELECTED_TRACKS);
            this.sender.invokeAction (Actions.SET_ALL_FX_OFFLINE);
            this.sender.invokeAction (Actions.LOCK_TRACK_CONTROLS);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        this.setIsActivated (!this.isActivated);
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        this.setMuteState (value);
        this.sendTrackOSC ("mute", Boolean.valueOf (value));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        this.setMute (!this.isMute ());
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        this.setSoloState (value);
        this.sendTrackOSC ("solo", Boolean.valueOf (value));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        this.setSolo (!this.isSolo ());
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        throw new UnsupportedOperationException ("ChannelImpl.select not implemented. Only use with TrackImpl!");
    }


    /**
     * Store the type of the track.
     *
     * @param type The type
     */
    public void setType (final ChannelType type)
    {
        this.type = type;
    }


    /**
     * Set the volume of the channel
     *
     * @param volume The volume
     */
    public void setInternalVolume (final int volume)
    {
        if (this.isVolumeBeingTouched)
            this.lastReceivedVolume = volume;
        else
            this.volume = volume;
    }


    /**
     * Set the volume as text of the channel.
     *
     * @param volumeStr The text
     */
    public void setVolumeStr (final String volumeStr)
    {
        this.volumeStr = volumeStr;
    }


    /**
     * Set the panorama.
     *
     * @param pan The panorama
     */
    public void setInternalPan (final int pan)
    {
        if (this.isPanBeingTouched)
            this.lastReceivedPan = pan;
        else
            this.pan = pan;
    }


    /**
     * Set the panorama as text.
     *
     * @param panStr The text
     */
    public void setPanStr (final String panStr)
    {
        this.panStr = panStr;
    }


    /**
     * Set the left VU.
     *
     * @param vuLeft The left VU
     */
    public void setVuLeft (final int vuLeft)
    {
        this.vuLeft = vuLeft;
    }


    /**
     * Set the left VU.
     *
     * @param vuRight The left VU
     */
    public void setVuRight (final int vuRight)
    {
        this.vuRight = vuRight;
    }


    /**
     * Set the mute state.
     *
     * @param isMute The mute state
     */
    public void setMuteState (final boolean isMute)
    {
        this.isMute = isMute;
    }


    /**
     * Set the solo state.
     *
     * @param isSolo The solo state
     */
    public void setSoloState (final boolean isSolo)
    {
        this.isSolo = isSolo;
    }


    /**
     * Set the activated state.
     *
     * @param isActivated True if is activated
     */
    public void setInternalIsActivated (final boolean isActivated)
    {
        this.isActivated = isActivated;
    }


    /**
     * Set the color.
     *
     * @param color The color
     */
    public void setColorState (final double [] color)
    {
        this.color = color;
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.select ();
        this.host.scheduleTask ( () -> this.sender.invokeAction (Actions.DUPLICATE_TRACKS), 200);
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        this.sendTrackOSC ("remove", null);
    }


    /** {@inheritDoc} */
    @Override
    public ISendBank getSendBank ()
    {
        return this.sendBank;
    }


    protected void sendTrackOSC (final String command, final Object value)
    {
        this.sender.sendOSC ("/track/" + (this.getIndex () + 1) + "/" + command, value);
    }
}
