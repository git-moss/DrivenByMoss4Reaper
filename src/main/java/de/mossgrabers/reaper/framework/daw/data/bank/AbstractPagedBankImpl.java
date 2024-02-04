// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;

import java.util.List;
import java.util.Optional;


/**
 * An abstract bank which supports paging of items.
 *
 * @param <S> The internal specific item type of the bank item
 * @param <T> The specific item type of the bank item
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractPagedBankImpl<S extends T, T extends IItem> extends AbstractBankImpl<T>
{
    protected final T emptyItem;
    protected int     bankOffset = 0;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param pageSize The number of elements in a page of the bank
     * @param emptyItem The empty item object
     */
    protected AbstractPagedBankImpl (final DataSetupEx dataSetup, final int pageSize, final T emptyItem)
    {
        super (dataSetup, pageSize);

        this.emptyItem = emptyItem;
    }


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param pageSize The number of elements in a page of the bank
     * @param emptyItem The empty item object
     * @param items The pre-configured bank items
     */
    protected AbstractPagedBankImpl (final DataSetupEx dataSetup, final int pageSize, final T emptyItem, final List<T> items)
    {
        super (dataSetup, pageSize, items);

        this.emptyItem = emptyItem;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.bankOffset - this.getPageSize () >= 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.bankOffset + this.getPageSize () < this.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public T getItem (final int index)
    {
        final int id = this.bankOffset + index;
        return id >= 0 && id < this.getItemCount () ? this.getUnpagedItem (id) : this.emptyItem;
    }


    /**
     * Sets the number of items.
     *
     * @param itemCount The number of items
     */
    public void setItemCount (final int itemCount)
    {
        this.itemCount = itemCount;
        final int ps = this.getPageSize ();
        if (ps > 0)
            this.setBankOffset (Math.min (this.bankOffset, this.itemCount / ps * ps));
    }


    /**
     * Get an item from the item list. No paging is applied.
     *
     * @param position The position of the item
     * @return The item
     */
    @SuppressWarnings("unchecked")
    public S getUnpagedItem (final int position)
    {
        synchronized (this.items)
        {
            final int size = this.items.size ();
            final int diff = position - size + 1;
            if (diff > 0)
            {
                for (int i = 0; i < diff; i++)
                    this.items.add (this.createItem (size + i));
            }
            return (S) this.items.get (position);
        }
    }


    /**
     * Create a new item for the given position in the bank.
     *
     * @param position The position
     * @return The created item
     */
    protected abstract T createItem (final int position);


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.bankOffset;
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final Optional<T> sel = this.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () + 1;
        if (index == this.getPageSize ())
            this.selectNextPage ();
        else
            this.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final Optional<T> sel = this.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () - 1;
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
        this.host.scheduleTask ( () -> this.getItem (this.getPageSize () - 1).select (), 75);
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
        this.setBankOffset (this.bankOffset - this.getPageSize ());
    }


    protected void scrollPageForwards ()
    {
        this.setBankOffset (this.bankOffset + this.getPageSize ());
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.setBankOffset (this.bankOffset - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.setBankOffset (this.bankOffset + 1);
    }


    /**
     * Set the bank offset. Used for keeping in sync with another bank.
     *
     * @param bankOffset The bank offset
     */
    protected void setBankOffset (final int bankOffset)
    {
        this.bankOffset = Math.max (0, Math.min (bankOffset, this.getItemCount () - 1));
        this.firePageObserver ();
    }
}
