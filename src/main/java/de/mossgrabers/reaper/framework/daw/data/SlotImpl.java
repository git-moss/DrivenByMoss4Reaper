// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a slot.
 *
 * @author Jürgen Moßgraber
 */
public class SlotImpl extends ItemImpl implements ISlot
{
    private final ITrack track;
    private boolean      hasContent;
    private boolean      isMuted;
    private ColorEx      color = new ColorEx (0.2, 0.2, 0.2);
    private ISceneBank   sceneBank;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param track The track
     * @param index The index of the slot
     * @param sceneBank The scene bank
     */
    public SlotImpl (final DataSetupEx dataSetup, final ITrack track, final int index, final ISceneBank sceneBank)
    {
        super (dataSetup, index);

        this.track = track;
        this.sceneBank = sceneBank;
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
    public boolean isMuted ()
    {
        return this.isMuted;
    }


    /**
     * Set the clip muted.
     *
     * @param isMuted True if muted
     */
    public void setMuted (final boolean isMuted)
    {
        this.isMuted = isMuted;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecordingQueued ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecording ()
    {
        if (!this.doesExist ())
            return false;

        // Play cursor is in range of clip and recording as well as playback is active
        final IScene scene = this.sceneBank.getItem (this.index);
        if (scene.doesExist () && scene instanceof SceneImpl sceneImpl)
        {
            final ITransport transport = this.dataSetup.getTransport ();
            if (transport.isRecording () && this.track.isRecArm ())
            {
                final double playPosition = transport.getPosition ();
                return playPosition >= sceneImpl.getBeginPosition () && playPosition <= sceneImpl.getEndPosition ();
            }
        }
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
    public boolean isPlaying ()
    {
        if (!(this.doesExist () && this.hasContent ()))
            return false;

        // Play cursor is in range of clip (scene) and playback is active
        final IScene scene = this.sceneBank.getItem (this.index);
        if (scene.doesExist () && scene instanceof SceneImpl sceneImpl)
        {
            final ITransport transport = this.dataSetup.getTransport ();
            if (transport.isPlaying ())
            {
                final double playPosition = transport.getPosition ();
                return playPosition >= sceneImpl.getBeginPosition () && playPosition <= sceneImpl.getEndPosition ();
            }
        }
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
    public void launch (final boolean isPressed, final boolean isAlternative)
    {
        // There is no alternative launch
        if (isPressed && this.getIndex () >= 0)
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
        this.sendOSC (this.track.getIndex () + "/clip/" + this.getPosition () + "/" + command);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.TRACK;
    }
}
