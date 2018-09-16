// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * Encapsulates the data of a slot.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotImpl extends ItemImpl implements ISlot
{
    private final int       trackIndex;
    private final double [] color = new double []
    {
        0,
        0,
        0
    };


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param trackIndex The track index
     * @param index The index of the slot
     */
    public SlotImpl (final IHost host, final MessageSender sender, final int trackIndex, final int index)
    {
        super (host, sender, index);

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
        return this.color;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.color[0] = red;
        this.color[1] = green;
        this.color[2] = blue;
    }


    /** {@inheritDoc} */
    @Override
    public void launch ()
    {
        // TODO
    }


    /** {@inheritDoc} */
    @Override
    public void record ()
    {
        // TODO
    }


    /** {@inheritDoc} */
    @Override
    public void create (final int length)
    {
        this.sender.sendOSC ("/track/" + this.trackIndex + "/createClip", Integer.valueOf (length));
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // TODO
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.sender.sendOSC ("/clip/duplicate", null);
    }


    /** {@inheritDoc} */
    @Override
    public void browse ()
    {
        // Not supported
    }
}
