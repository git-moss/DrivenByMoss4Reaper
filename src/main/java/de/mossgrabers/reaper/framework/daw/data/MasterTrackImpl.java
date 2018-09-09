// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ItemSelectionObserver;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.reaper.communication.MessageSender;

import java.util.ArrayList;
import java.util.List;


/**
 * The master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterTrackImpl extends TrackImpl implements IMasterTrack
{
    private final List<ItemSelectionObserver> observers = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The valueChanger
     * @param numSends The number of sends on a page
     */
    public MasterTrackImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numSends)
    {
        super (host, sender, valueChanger, 0, 1, numSends, 0);
        // Master channel does always exist
        this.setExists (true);
        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final ItemSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Master";
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return ChannelType.MASTER;
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final double value)
    {
        this.volume = (int) value;
        this.sender.sendOSC ("/master/volume", Double.valueOf (this.valueChanger.toNormalizedValue (this.volume)));
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        super.setSelected (isSelected);
        for (final ItemSelectionObserver observer: this.observers)
            observer.call (-1, isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        this.sender.sendOSC ("/master/volume/touch", Integer.valueOf (isBeingTouched ? 1 : 0));
        this.handleVolumeTouch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final double value)
    {
        this.pan = (int) value;
        this.sender.sendOSC ("/master/pan", Double.valueOf (this.valueChanger.toNormalizedValue (this.getPan ())));
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        this.sender.sendOSC ("/master/pan/touch", Integer.valueOf (isBeingTouched ? 1 : 0));
        this.handlePanTouch (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        this.setMuteState (value);
        this.sender.sendOSC ("/master/mute", Boolean.valueOf (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        this.setSoloState (value);
        this.sender.sendOSC ("/master/solo", Boolean.valueOf (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setRecArm (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.sender.sendOSC ("/master/select", Integer.valueOf (1));
    }
}