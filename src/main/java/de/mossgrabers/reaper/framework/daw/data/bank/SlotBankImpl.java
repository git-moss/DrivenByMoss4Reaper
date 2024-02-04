// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.empty.EmptySlot;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.SlotImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Encapsulates the data of a slot bank.
 *
 * @author Jürgen Moßgraber
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
        this.trackIndex = trackIndex;
    }


    /**
     * Copy constructor for slot banks with a different page size.
     *
     * @param slotBankImpl The slot bank to wrap
     * @param numSlots The new page size
     */
    protected SlotBankImpl (final SlotBankImpl slotBankImpl, final int numSlots)
    {
        super (slotBankImpl.dataSetup, numSlots, EmptySlot.INSTANCE, slotBankImpl.items);

        this.sceneBank = slotBankImpl.sceneBank;
        this.trackIndex = slotBankImpl.trackIndex;
    }


    /** {@inheritDoc}} */
    @Override
    protected SlotImpl createItem (final int position)
    {
        return new SlotImpl (this.dataSetup, this.trackIndex, this.pageSize == 0 ? 0 : position % this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISlot> getSelectedItem ()
    {
        if (this.items.isEmpty ())
            return Optional.empty ();

        for (int i = 0; i < this.getPageSize (); i++)
        {
            final ISlot slot = this.getItem (i);
            if (slot.isSelected ())
                return Optional.of (slot);
        }

        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public List<ISlot> getSelectedItems ()
    {
        final Optional<ISlot> selectedItem = this.getSelectedItem ();
        return selectedItem.isEmpty () ? Collections.emptyList () : Collections.singletonList (selectedItem.get ());
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISlot> getEmptySlot (final int startFrom)
    {
        return Optional.of (new SlotImpl (this.dataSetup, this.trackIndex, startFrom));
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