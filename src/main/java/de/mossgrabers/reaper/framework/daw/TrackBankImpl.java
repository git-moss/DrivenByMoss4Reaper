// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.TreeNode;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A track bank of all instrument and audio tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackBankImpl extends AbstractTrackBankImpl
{
    private final boolean       hasFlatTrackList;
    private final AtomicBoolean isDirty       = new AtomicBoolean (false);
    private TreeNode<TrackImpl> rootTrack     = new TreeNode<> ();
    private TreeNode<TrackImpl> currentFolder = this.rootTrack;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numTracks The number of tracks in a bank page
     * @param numScenes The number of scenes in a bank page
     * @param numSends The number of sends in a bank page
     * @param hasFlatTrackList True if group navigation should not be supported, instead all tracks
     *            are flat
     */
    public TrackBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends, final boolean hasFlatTrackList)
    {
        super (host, sender, valueChanger, numTracks, numScenes, numSends);

        this.hasFlatTrackList = hasFlatTrackList;
    }


    /** {@inheritDoc} */
    @Override
    public void selectChildren ()
    {
        for (final TreeNode<TrackImpl> node: this.currentFolder.getChildren ())
        {
            if (node.getData ().isSelected ())
            {
                this.currentFolder = node;
                List<TreeNode<TrackImpl>> children = this.currentFolder.getChildren ();
                if (!children.isEmpty ())
                    children.get (0).getData ().select ();
                break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        final TreeNode<TrackImpl> parent = this.currentFolder.getParent ();
        final TrackImpl data = this.currentFolder.getData ();
        this.currentFolder = parent == null ? this.rootTrack : parent;
        data.select ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        return this.currentFolder.getParent () != null;
    }


    /**
     * Handles track changes. Notifies all track change observers.
     *
     * @param track The de-/selected track
     * @param isSelected True if selected
     */
    public void handleBankTrackSelection (final ITrack track, final boolean isSelected)
    {
        final int position = track.getPosition ();

        if (isSelected)
        {
            // Is track on current page? If not adjust the page
            if (position < this.bankOffset || position >= this.bankOffset + this.pageSize)
                this.bankOffset = position / this.pageSize * this.pageSize;
        }

        this.notifySelectionObservers (track.getIndex (), isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        for (int i = 0; i < this.getPageSize (); i++)
        {
            final ISendBank sendBank = this.getItem (i).getSendBank ();
            if (sendBank.getItemCount () > 0)
            {
                final ISend send = sendBank.getItem (sendIndex);
                if (send.doesExist ())
                    return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return this.canEditSend (sendIndex) ? "Send " + (sendIndex + 1) : "";
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public ITrack getItem (final int index)
    {
        if (this.hasFlatTrackList)
            return super.getItem (index);

        this.recalcTree ();
        final List<TreeNode<TrackImpl>> children = this.currentFolder.getChildren ();
        return index < children.size () ? children.get (index).getData () : this.emptyTrack;
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        if (this.hasFlatTrackList)
            return super.getItemCount ();

        this.recalcTree ();
        return this.currentFolder.getChildren ().size ();
    }


    /**
     * Mark the track tree dirty for recalculation.
     */
    public void markDirty ()
    {
        if (this.hasFlatTrackList)
            return;

        synchronized (this.isDirty)
        {
            this.isDirty.set (true);
        }
    }


    /**
     * Recalculate the track tree.
     */
    public void recalcTree ()
    {
        if (this.hasFlatTrackList)
            return;

        synchronized (this.isDirty)
        {
            if (!this.isDirty.get ())
                return;

            final TreeNode<TrackImpl> newRoot = new TreeNode<> ();
            this.currentFolder = null;

            final List<TreeNode<TrackImpl>> hierarchy = new ArrayList<> ();
            hierarchy.add (newRoot);

            for (int i = 0; i < super.getItemCount (); i++)
            {
                final TrackImpl track = (TrackImpl) this.items.get (i);

                final int depth = track.getDepth ();

                final TreeNode<TrackImpl> p = hierarchy.get (depth);
                final TreeNode<TrackImpl> child = p.addChild (track);
                final int childrenSize = p.getChildren ().size ();
                track.setIndex (childrenSize - 1);

                final int index = depth + 1;
                if (index < hierarchy.size ())
                    hierarchy.set (index, child);
                else
                    hierarchy.add (index, child);

                if (track.isSelected ())
                {
                    this.currentFolder = p;
                    this.bankOffset = childrenSize / this.pageSize * this.pageSize;
                }
            }

            this.rootTrack = newRoot;

            if (this.currentFolder == null)
                this.currentFolder = newRoot;

            this.isDirty.set (false);
        }
    }
}