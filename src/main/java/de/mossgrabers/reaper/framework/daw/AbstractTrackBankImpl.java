// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.EmptyTrackData;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractBankImpl<ITrack> implements ITrackBank
{
    private int        numScenes;
    private int        numSends;
    private ISceneBank sceneBank;

    protected int      bankOffset = 0;


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

        this.sceneBank = new SceneBankImpl (host, sender, this.numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.bankOffset - this.pageSize >= 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.bankOffset + this.pageSize < this.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - this.pageSize);

        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedItem ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getPosition () + "/select", Integer.valueOf (0));

        // Select item on new page
        int selIndex = Math.min (this.getItemCount () - 1, this.bankOffset + this.pageSize - 1);
        this.sendTrackOSC (selIndex + "/select", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        if (this.bankOffset + this.pageSize < this.getItemCount ())
            this.bankOffset += this.pageSize;

        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedItem ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getPosition () + "/select", Integer.valueOf (0));

        // Select item on new page
        this.sendTrackOSC (this.bankOffset + "/select", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        // Items are added on the fly in getItem
    }


    /** {@inheritDoc} */
    @Override
    public ITrack getItem (int index)
    {
        final int id = this.bankOffset + index;
        return id >= 0 && id < this.getItemCount () ? this.getTrack (id) : EmptyTrackData.INSTANCE;
    }


    /**
     * Get a track from the track list. No paging is applied.
     *
     * @param index The index of the track
     * @return The track
     */
    public TrackImpl getTrack (int index)
    {
        synchronized (this.items)
        {
            final int size = this.items.size ();
            final int diff = index - size + 1;
            if (diff > 0)
            {
                for (int i = 0; i < diff; i++)
                    this.items.add (new TrackImpl (this.host, this.sender, this.valueChanger, size + i, this.getPageSize (), this.numSends, this.numScenes));
            }
            return (TrackImpl) this.items.get (index);
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


    protected void sendTrackOSC (final String command, final Object value)
    {
        this.sender.sendOSC ("/track/" + command + "/", value);
    }


    /** {@inheritDoc} */
    @Override
    public void selectChildren ()
    {
        // Intentionally empty
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
    public void scrollTo (final int position)
    {
        if (position >= 0 && position < this.getItemCount ())
            this.sendTrackOSC (position + "/scrollto", null);
    }
}