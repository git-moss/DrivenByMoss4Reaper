// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.empty.EmptySlot;
import de.mossgrabers.reaper.framework.daw.data.SlotImpl;

import java.util.Collections;
import java.util.List;


/**
 * Encapsulates the data of a slot bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotBankImpl extends AbstractPagedBankImpl<SlotImpl, ISlot> implements ISlotBank
{
    private final SceneBankImpl sceneBank;
    private int                 trackIndex;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param sceneBank The scene bank for scrolling the clip pages on all tracks
     * @param trackIndex The track index to which the slot bank belongs
     * @param numSlots The number of slots in the page of the bank
     */
    public SlotBankImpl (final DataSetupEx dataSetup, final SceneBankImpl sceneBank, final int trackIndex, final int numSlots)
    {
        super (dataSetup, numSlots, EmptySlot.INSTANCE);

        this.sceneBank = sceneBank;
    }


    /** {@inheritDoc}} */
    @Override
    protected SlotImpl createItem (final int position)
    {
        return new SlotImpl (this.dataSetup, this.trackIndex, this.pageSize == 0 ? 0 : position % this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSelectedItem ()
    {
        if (this.items.isEmpty ())
            return null;

        for (int i = 0; i < this.getPageSize (); i++)
        {
            final ISlot slot = this.getItem (i);
            if (slot.isSelected ())
                return slot;
        }

        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<ISlot> getSelectedItems ()
    {
        final ISlot selectedItem = this.getSelectedItem ();
        return selectedItem == null ? Collections.emptyList () : Collections.singletonList (selectedItem);
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getEmptySlot (final int startFrom)
    {
        return new SlotImpl (this.dataSetup, this.trackIndex, startFrom);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        if (this.pageSize == 0)
            return;

        // Clips must be scrolled via SceneBank!
        this.sceneBank.scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        if (this.pageSize == 0)
            return;

        // Clips must be scrolled via SceneBank!
        this.sceneBank.scrollPageForwards ();
    }


    /**
     * Sets the maximum number of slots over all tracks.
     *
     * @param maxSlotCount The maximum number of slots
     */
    public void setMaxSlotCount (final int maxSlotCount)
    {
        // Make sure that there are no 'old' entries in the list if we increase it
        while (this.items.size () > this.itemCount)
            this.items.remove (this.items.size () - 1);

        this.itemCount = maxSlotCount;
    }


    /**
     * Update the track to which the slot bank belongs.
     *
     * @param trackIndex The index of the track
     */
    public void setTrack (final int trackIndex)
    {
        this.trackIndex = trackIndex;
        synchronized (this.items)
        {
            for (final ISlot slot: this.items)
                ((SlotImpl) slot).setTrack (trackIndex);
        }
    }
}