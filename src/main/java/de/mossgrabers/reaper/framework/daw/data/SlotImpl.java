// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Encapsulates the data of a slot.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotImpl extends ItemImpl implements ISlot
{
    private final int trackIndex;


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
    public String getName ()
    {
        return "Slot " + this.getIndex ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasContent ()
    {
        return false;
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
            0,
            0,
            0
        };
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void launch ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void record ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void create (final int length)
    {
        this.sender.sendOSC ("/track/" + (this.trackIndex + 1) + "/createClip", Integer.valueOf (length));
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.invokeAction (Actions.DUPLICATE_ITEMS);
    }


    /** {@inheritDoc} */
    @Override
    public void browse ()
    {
        // Not supported
    }
}
