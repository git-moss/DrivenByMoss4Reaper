// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.observer.IIndexedValueObserver;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractBankImpl<ITrack> implements ITrackBank
{
    protected final ITrack emptyTrack;

    private int            numScenes;
    private int            numSends;
    private ISceneBank     sceneBank;

    protected int          bankOffset = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractTrackBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, sender, valueChanger, numTracks);
        this.numScenes = numScenes;
        this.numSends = numSends;

        this.emptyTrack = new TrackImpl (host, sender, this, valueChanger, -1, numTracks, numSends, numScenes);
        this.sceneBank = new SceneBankImpl (host, sender, this, this.numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.bankOffset - this.pageSize >= 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.bankOffset + this.pageSize < this.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        // Items are added on the fly in getItem
    }


    /** {@inheritDoc} */
    @Override
    public ITrack getItem (final int index)
    {
        final int id = this.bankOffset + index;
        return id >= 0 && id < this.getItemCount () ? this.getTrack (id) : this.emptyTrack;
    }


    /**
     * Get a track from the track list. No paging is applied.
     *
     * @param position The position of the track
     * @return The track
     */
    public TrackImpl getTrack (final int position)
    {
        synchronized (this.items)
        {
            final int size = this.items.size ();
            final int diff = position - size + 1;
            if (diff > 0)
            {
                for (int i = 0; i < diff; i++)
                    this.items.add (new TrackImpl (this.host, this.sender, this, this.valueChanger, size + i, this.getPageSize (), this.numSends, this.numScenes));
            }
            return (TrackImpl) this.items.get (position);
        }
    }


    /**
     * Sets the number of tracks.
     *
     * @param trackCount The number of tracks
     */
    public void setTrackCount (final int trackCount)
    {
        this.itemCount = trackCount;
    }


    protected void sendTrackOSC (final String command, final int value)
    {
        this.sender.processIntArg ("track", command, value);
    }


    protected void sendTrackOSC (final String command)
    {
        this.sender.processNoArg ("track", command);
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final ITrack sel = this.getSelectedItem ();
        if (sel == null)
            return DAWColors.COLOR_OFF;
        final double [] color = sel.getColor ();
        return DAWColors.getColorIndex (color[0], color[1], color[2]);
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.sceneBank;
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IIndexedValueObserver<String> observer)
    {
        for (int index = 0; index < this.getPageSize (); index++)
        {
            final TrackImpl track = this.getTrack (index);
            track.addNameObserver (value -> observer.update (track.getIndex (), value));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.scrollTo (position, true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        if (position < 0 || position >= this.getItemCount ())
            return;
        final int pageSize = this.getPageSize ();
        final int pos = adjustPage ? position / pageSize * pageSize : position;
        this.sendTrackOSC (pos + "/scrollto");
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final ITrack sel = this.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == this.pageSize)
            this.selectNextPage ();
        else
            this.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final ITrack sel = this.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1)
            this.selectPreviousPage ();
        else
            this.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        if (!this.canScrollPageBackwards ())
            return;
        this.scrollPageBackwards ();
        this.host.scheduleTask ( () -> this.getItem (this.pageSize - 1).select (), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        if (!this.canScrollPageForwards ())
            return;
        this.scrollPageForwards ();
        this.host.scheduleTask ( () -> this.getItem (0).select (), 75);
    }


    protected void scrollPageBackwards ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - this.pageSize);

        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedItem ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getPosition () + "/select", 0);

        // Select item on new page
        final int selIndex = this.pageSize - 1;
        final int selPos = this.getItem (selIndex).getPosition ();
        this.sendTrackOSC (selPos + "/select", 1);
    }


    protected void scrollPageForwards ()
    {
        if (this.bankOffset + this.pageSize < this.getItemCount ())
            this.bankOffset += this.pageSize;

        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedItem ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getPosition () + "/select", 0);

        // Select item on new page
        final int selPos = this.getItem (0).getPosition ();
        this.sendTrackOSC (selPos + "/select", 1);
    }


    protected void updateSlotBanks (final int slotBankOffset)
    {
        final int trackCount = this.items.size ();
        for (int position = 0; position < trackCount; position++)
            ((SlotBankImpl) this.getTrack (position).getSlotBank ()).setBankOffset (slotBankOffset);
    }


    /**
     * Check if any of the tracks is soloed.
     *
     * @return True if there is at least one soloed track
     */
    public boolean hasSolo ()
    {
        for (int i = 0; i < this.items.size (); i++)
        {
            if (this.items.get (i).isSolo ())
                return true;
        }
        return false;
    }


    /**
     * Deactivate all solo states of all tracks.
     */
    public void deactivateSolo ()
    {
        for (int i = 0; i < this.items.size (); i++)
            this.items.get (i).setSolo (false);
    }


    /**
     * Deactivate all mute states of all tracks.
     */
    public void deactivateMute ()
    {
        for (int i = 0; i < this.items.size (); i++)
            this.items.get (i).setMute (false);
    }
}