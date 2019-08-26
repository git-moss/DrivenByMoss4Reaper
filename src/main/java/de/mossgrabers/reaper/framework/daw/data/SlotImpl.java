// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a slot.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotImpl extends ItemImpl implements ISlot
{
    private int    trackIndex;
    private double red   = 0;
    private double green = 0;
    private double blue  = 0;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param trackIndex The track index
     * @param index The index of the slot
     */
    public SlotImpl (final DataSetupEx dataSetup, final int trackIndex, final int index)
    {
        super (dataSetup, index);

        this.trackIndex = trackIndex;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasContent ()
    {
        return this.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecording ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlayingQueued ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecordingQueued ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isStopQueued ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return new double []
        {
            this.red,
            this.green,
            this.blue
        };
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        if (this.getIndex () >= 0)
            this.sendTrackClipOSC ("select");
    }


    /** {@inheritDoc} */
    @Override
    public void launch ()
    {
        if (this.getIndex () >= 0)
            this.sendTrackClipOSC ("launch");
    }


    /** {@inheritDoc} */
    @Override
    public void record ()
    {
        if (this.getIndex () >= 0)
            this.sendTrackClipOSC ("record");
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        if (this.getIndex () >= 0)
            this.sendTrackClipOSC ("remove");
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.sender.processNoArg ("clip", "duplicate");
    }


    /** {@inheritDoc} */
    @Override
    public void browse ()
    {
        // Not supported
    }


    private void sendTrackClipOSC (final String command)
    {
        this.sender.processNoArg ("track", this.trackIndex + "/clip/" + this.getPosition () + "/" + command);
    }


    /**
     * Update the track to which the slot belongs.
     *
     * @param trackIndex The index of the track
     */
    public void setTrack (final int trackIndex)
    {
        this.trackIndex = trackIndex;
    }
}
