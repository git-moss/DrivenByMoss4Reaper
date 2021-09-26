// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a slot.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotImpl extends ItemImpl implements ISlot
{
    private int     trackIndex;
    private boolean hasContent;
    private ColorEx color = new ColorEx (0.2, 0.2, 0.2);


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
        return this.hasContent;
    }


    /**
     * Set the 'has content' state.
     *
     * @param hasContent True if the slot has content
     */
    public void setHasContent (final boolean hasContent)
    {
        this.hasContent = hasContent;
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
    public ColorEx getColor ()
    {
        return this.color;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        this.color = color;
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
    public void startRecording ()
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
        if (this.getIndex () >= 0)
            this.sendTrackClipOSC ("duplicate");
    }


    /** {@inheritDoc} */
    @Override
    public void paste (final ISlot slot)
    {
        slot.duplicate ();
    }


    private void sendTrackClipOSC (final String command)
    {
        this.sendOSC (this.trackIndex + "/clip/" + this.getPosition () + "/" + command);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.TRACK;
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
