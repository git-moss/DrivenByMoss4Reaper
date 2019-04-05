// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
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
    private final boolean       hasFullFlatTrackList;
    private final ITrack        master;
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
     * @param hasFullFlatTrackList True if the track navigation should include effect and master
     *            tracks if flat
     * @param master If set the track navigation should include master tracks if flat
     */
    public TrackBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends, final boolean hasFlatTrackList, final boolean hasFullFlatTrackList, final ITrack master)
    {
        super (host, sender, valueChanger, numTracks, numScenes, numSends);

        this.hasFlatTrackList = hasFlatTrackList;
        this.hasFullFlatTrackList = hasFullFlatTrackList;
        this.master = master;
    }


    /**
     * Enter the current folder if hierarchical track navigation is enabled.
     */
    public void enterCurrentFolder ()
    {
        if (this.hasFlatTrackList)
            return;

        final List<TreeNode<TrackImpl>> tracks = this.currentFolder.getChildren ();
        // Find the selected track in the current children, which has to be a group
        for (final TreeNode<TrackImpl> node: tracks)
        {
            final TrackImpl data = node.getData ();
            if (data.isSelected () && data.isGroup ())
            {
                // Make the found track the new current folder
                this.currentFolder = node;
                final List<TreeNode<TrackImpl>> children = node.getChildren ();
                if (children.isEmpty ())
                    break;
                children.get (0).getData ().select ();
                break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        if (this.hasFlatTrackList)
            return;

        final TreeNode<TrackImpl> parent = this.currentFolder.getParent ();
        this.currentFolder = parent == null ? this.rootTrack : parent;
        final TrackImpl data = this.currentFolder.getData ();
        if (data != null)
            data.select ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        if (this.hasFlatTrackList)
            return false;

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
        if (this.hasFlatTrackList)
        {
            if (track instanceof IMasterTrack)
            {
                if (this.master != null && isSelected)
                {
                    final int position = super.getItemCount ();
                    // Is mastertrack on current page? If not adjust the page
                    if (position < this.bankOffset || position >= this.bankOffset + this.pageSize)
                        this.bankOffset = position / this.pageSize * this.pageSize;
                }
                return;
            }

            if (isSelected)
            {
                final int position = track.getPosition ();
                // Is track on current page? If not adjust the page
                if (position < this.bankOffset || position >= this.bankOffset + this.pageSize)
                    this.bankOffset = position / this.pageSize * this.pageSize;
            }
        }
        else
        {
            if (track instanceof IMasterTrack)
                return;

            // Find the selected track in the tree, focus the page and select its parent as the new
            // folder
            this.findSelectedTrack (this.rootTrack);
        }

        this.notifySelectionObservers (track.getIndex (), isSelected);
    }


    private boolean findSelectedTrack (final TreeNode<TrackImpl> node)
    {
        final List<TreeNode<TrackImpl>> children = node.getChildren ();
        for (int i = 0; i < children.size (); i++)
        {
            final TreeNode<TrackImpl> child = children.get (i);

            final TrackImpl track = child.getData ();
            if (track.isSelected ())
            {
                this.currentFolder = node;
                this.bankOffset = i / this.pageSize * this.pageSize;
                return true;
            }

            if (track.isGroup () && this.findSelectedTrack (child))
                return true;
        }
        return false;
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
        this.recalcTree ();

        final int id = this.bankOffset + index;

        if (this.hasFlatTrackList)
        {
            if (this.hasFullFlatTrackList && this.master != null && super.getItemCount () == id)
                return this.master;
            return super.getItem (index);
        }

        final List<TreeNode<TrackImpl>> children = this.currentFolder.getChildren ();
        return id < children.size () ? children.get (id).getData () : this.emptyTrack;
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        this.recalcTree ();

        if (this.hasFlatTrackList)
        {
            int size = super.getItemCount ();
            if (this.hasFullFlatTrackList && this.master != null)
                size++;
            return size;
        }

        return this.currentFolder.getChildren ().size ();
    }


    /**
     * Mark the track tree dirty for recalculation.
     */
    public void markDirty ()
    {
        synchronized (this.isDirty)
        {
            this.isDirty.set (true);
        }
    }


    /**
     * Recalculate the track tree (and the flat numbering).
     */
    public void recalcTree ()
    {
        synchronized (this.isDirty)
        {
            if (!this.isDirty.get ())
                return;

            synchronized (this.items)
            {
                if (this.hasFlatTrackList)
                {
                    for (int i = 0; i < super.getItemCount (); i++)
                        this.getTrack (i).setIndex (i % this.pageSize);
                }
                else
                {
                    final TreeNode<TrackImpl> newRoot = new TreeNode<> ();
                    this.currentFolder = null;

                    final List<TreeNode<TrackImpl>> hierarchy = new ArrayList<> ();
                    hierarchy.add (newRoot);

                    for (int i = 0; i < super.getItemCount (); i++)
                    {
                        final TrackImpl track = this.getTrack (i);

                        final int depth = track.getDepth ();

                        final TreeNode<TrackImpl> p = hierarchy.get (depth);
                        final TreeNode<TrackImpl> child = p.addChild (track);
                        final int childrenSize = p.getChildren ().size ();
                        track.setIndex ((childrenSize - 1) % this.pageSize);

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
                }
            }

            this.isDirty.set (false);
        }
    }
}