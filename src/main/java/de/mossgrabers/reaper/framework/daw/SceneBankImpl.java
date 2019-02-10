// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.data.SceneImpl;


/**
 * Encapsulates the data of a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneBankImpl extends AbstractBankImpl<IScene> implements ISceneBank
{
    private final AbstractTrackBankImpl trackBank;
    private final IScene                emptyScene;
    private int                         bankOffset = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param trackBank The track bank to which the scene bank belongs
     * @param numScenes The number of scenes in the page of the bank
     */
    public SceneBankImpl (final IHost host, final MessageSender sender, final AbstractTrackBankImpl trackBank, final int numScenes)
    {
        super (host, sender, null, numScenes);

        this.trackBank = trackBank;

        this.initItems ();

        this.emptyScene = new SceneImpl (host, sender, -1);
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        // Items are added on the fly in getItem
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.bankOffset - 1 >= 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        final int calculatedCount = this.getItemCount ();
        return this.bankOffset + 1 < calculatedCount;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - 1);
        this.trackBank.updateSlotBanks (this.bankOffset);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        if (this.bankOffset + 1 < this.getItemCount ())
        {
            this.bankOffset += 1;
            this.trackBank.updateSlotBanks (this.bankOffset);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - this.pageSize);
        this.trackBank.updateSlotBanks (this.bankOffset);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        if (this.bankOffset + this.pageSize < this.getItemCount ())
        {
            this.bankOffset += this.pageSize;
            this.trackBank.updateSlotBanks (this.bankOffset);
        }
    }


    /** {@inheritDoc} */
    @Override
    public IScene getItem (final int index)
    {
        final int id = this.bankOffset + index;
        return id >= 0 && id < this.getItemCount () ? this.getScene (id) : this.emptyScene;
    }


    /**
     * Get a scene from the scene list. No paging is applied.
     *
     * @param position The position of the scene
     * @return The scene
     */
    public SceneImpl getScene (final int position)
    {
        synchronized (this.items)
        {
            final int size = this.items.size ();
            final int diff = position - size + 1;
            if (diff > 0)
            {
                for (int i = 0; i < diff; i++)
                    this.items.add (new SceneImpl (this.host, this.sender, this.pageSize == 0 ? 0 : (size + i) % this.pageSize));
            }
            return (SceneImpl) this.items.get (position);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        int maxCount = this.itemCount;

        // Since scenes (Reaper regions) are not related to the number of clips on a track we need
        // to find the maximum

        final int trackCount = this.trackBank.getItemCount ();
        for (int position = 0; position < trackCount; position++)
        {
            final int size = this.trackBank.getTrack (position).getSlotBank ().getItemCount ();
            if (size > maxCount)
                maxCount = size;
        }
        return maxCount;
    }


    /**
     * Sets the number of scenes.
     *
     * @param sceneCount The number of scenes
     */
    public void setSceneCount (final int sceneCount)
    {
        this.itemCount = sceneCount;
    }
}