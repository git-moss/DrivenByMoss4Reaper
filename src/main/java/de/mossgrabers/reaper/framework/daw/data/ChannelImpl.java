// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.SendBankImpl;

import java.util.HashSet;
import java.util.Set;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelImpl extends ItemImpl implements IChannel
{
    private static final String                PATH_TRACK         = "track";
    private static final Object                UPDATE_LOCK        = new Object ();
    private static final ColorEx               GRAY               = new ColorEx (0.2, 0.2, 0.2);

    private final Set<IValueObserver<ColorEx>> colorObservers     = new HashSet<> ();

    private ChannelType                        type;
    protected double                           volume;
    private String                             volumeStr          = "";
    private double                             vuLeft;
    private double                             vuRight;
    private String                             panStr             = "";
    protected double                           pan;
    private boolean                            isMute;
    private boolean                            isSolo;
    private boolean                            isActivated        = true;
    private ColorEx                            color;

    private final ISendBank                    sendBank;

    private boolean                            isVolumeBeingTouched;
    private double                             lastReceivedVolume = -1;
    private boolean                            isPanBeingTouched;
    private double                             lastReceivedPan    = -1;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public ChannelImpl (final DataSetupEx dataSetup, final int index, final int numSends)
    {
        super (dataSetup, index);

        this.setName ("Track");

        this.sendBank = new SendBankImpl (dataSetup, this, numSends);
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Intentionally empty
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
        return this.valueChanger.fromNormalizedValue (this.volume);
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int control)
    {
        this.setVolume (this.valueChanger.changeValue (control, this.getVolume ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final int value)
    {
        synchronized (UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates ("track");
            this.volume = this.valueChanger.toNormalizedValue (value);
            this.sendTrackOSC ("volume", this.volume);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        this.setVolume ((int) (0.6 * this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        this.sendTrackOSC ("volume/touch", isBeingTouched);
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

        if (this.isVolumeBeingTouched || this.lastReceivedVolume < 0)
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
        return this.valueChanger.fromNormalizedValue (this.pan);
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int control)
    {
        this.setPan (this.valueChanger.changeValue (control, this.getPan ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final int value)
    {
        synchronized (UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates ("track");
            this.pan = this.valueChanger.toNormalizedValue (value);
            this.sendTrackOSC ("pan", this.pan);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        this.setPan (this.valueChanger.getUpperBound () / 2);
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        this.sendTrackOSC ("pan/touch", isBeingTouched);
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
    public ColorEx getColor ()
    {
        return this.color == null ? GRAY : this.color;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        final int [] rgb = color.toIntRGB255 ();
        this.sendTrackOSC ("color", "RGB(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");
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
        return this.valueChanger.fromNormalizedValue ((this.vuLeft + this.vuRight) / 2.0);
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        return this.valueChanger.fromNormalizedValue (this.vuLeft);
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        return this.valueChanger.fromNormalizedValue (this.vuRight);
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean enable)
    {
        this.isActivated = enable;
        this.sendTrackOSC ("active", enable);
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
        synchronized (UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates ("track");
            this.setMuteState (value);
            this.sendTrackOSC ("mute", value);
        }
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
        this.sendTrackOSC ("solo", value);
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
     * @param volume The volume normalized to 0..1
     */
    public void setInternalVolume (final double volume)
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
     * @param pan The panorama normalized to 0..1
     */
    public void setInternalPan (final double pan)
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
     * @param vuLeft The left VU normalized to 0..1
     */
    public void setVuLeft (final double vuLeft)
    {
        this.vuLeft = vuLeft;
    }


    /**
     * Set the left VU.
     *
     * @param vuRight The left VU normalized to 0..1
     */
    public void setVuRight (final double vuRight)
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
        this.color = new ColorEx (color);
        this.colorObservers.forEach (observer -> observer.update (this.color));
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
        this.sendTrackOSC ("remove");
    }


    /** {@inheritDoc} */
    @Override
    public ISendBank getSendBank ()
    {
        return this.sendBank;
    }


    /** {@inheritDoc} */
    @Override
    public void addColorObserver (final IValueObserver<ColorEx> observer)
    {
        this.colorObservers.add (observer);
    }


    protected void sendTrackOSC (final String command)
    {
        this.sender.processNoArg (this.getProcessor (), this.createCommand (command));
    }


    protected void sendTrackOSC (final String command, final int value)
    {
        this.sender.processIntArg (this.getProcessor (), this.createCommand (command), value);
    }


    protected void sendTrackOSC (final String command, final boolean value)
    {
        this.sender.processIntArg (this.getProcessor (), this.createCommand (command), value ? 1 : 0);
    }


    protected void sendTrackOSC (final String command, final double value)
    {
        this.sender.processDoubleArg (this.getProcessor (), this.createCommand (command), value);
    }


    protected void sendTrackOSC (final String command, final String value)
    {
        this.sender.processStringArg (this.getProcessor (), this.createCommand (command), value);
    }


    protected String getProcessor ()
    {
        return PATH_TRACK;
    }


    protected String createCommand (final String command)
    {
        return this.getPosition () + "/" + command;
    }
}
