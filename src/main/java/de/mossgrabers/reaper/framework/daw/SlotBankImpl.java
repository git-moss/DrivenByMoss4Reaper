// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.data.SlotImpl;

import java.util.Collections;
import java.util.List;


/**
 * Encapsulates the data of a slot bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotBankImpl extends AbstractBankImpl<ISlot> implements ISlotBank
{
    protected final ISlot emptySlot;
    protected int         bankOffset = 0;
    private int           trackIndex;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param trackIndex The track index to which the slot bank belongs
     * @param numSlots The number of slots in the page of the bank
     */
    public SlotBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int trackIndex, final int numSlots)
    {
        super (host, sender, valueChanger, numSlots);
        this.initItems ();

        this.emptySlot = new SlotImpl (host, sender, trackIndex, -1);
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
        return Collections.singletonList (this.getSelectedItem ());
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getEmptySlot (final int startFrom)
    {
        return new SlotImpl (this.host, this.sender, this.trackIndex, -1);
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
        throw new RuntimeException ("Clips must be scrolled via SceneBank!");
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        throw new RuntimeException ("Clips must be scrolled via SceneBank!");
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getItem (final int index)
    {
        final int id = this.bankOffset + index;
        return id >= 0 && id < this.getItemCount () ? this.getSlot (id) : this.emptySlot;
    }


    /**
     * Get a slot from the slot list. No paging is applied.
     *
     * @param position The position of the slot
     * @return The slot
     */
    public SlotImpl getSlot (final int position)
    {
        synchronized (this.items)
        {
            final int size = this.items.size ();
            final int diff = position - size + 1;
            if (diff > 0)
            {
                for (int i = 0; i < diff; i++)
                    this.items.add (new SlotImpl (this.host, this.sender, this.trackIndex, this.pageSize == 0 ? 0 : (size + i) % this.pageSize));
            }
            return (SlotImpl) this.items.get (position);
        }
    }


    /**
     * Sets the number of slots.
     *
     * @param slotCount The number of slots
     */
    public void setSlotCount (final int slotCount)
    {
        this.itemCount = slotCount;
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


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        // Items are added on the fly in getItem
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


    /**
     * Set the bank offset. Used for keeping in sync with scene bank.
     *
     * @param bankOffset The bank offset
     */
    public void setBankOffset (final int bankOffset)
    {
        this.bankOffset = bankOffset;
    }
}