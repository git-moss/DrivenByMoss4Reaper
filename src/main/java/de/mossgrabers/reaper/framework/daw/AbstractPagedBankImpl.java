// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.data.IItem;


/**
 * An abstract bank which supports paging of items.
 *
 * @param <S> The internal specific item type of the bank item
 * @param <T> The specific item type of the bank item
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
    public AbstractPagedBankImpl (final DataSetupEx dataSetup, final int pageSize, final T emptyItem)
    {
        super (dataSetup, pageSize);

        this.emptyItem = emptyItem;
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
        if (this.pageSize > 0)
            this.bankOffset = Math.min (this.bankOffset, this.itemCount / this.pageSize * this.pageSize);
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
        final T sel = this.getSelectedItem ();
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
        final T sel = this.getSelectedItem ();
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
    }


    protected void scrollPageForwards ()
    {
        if (this.bankOffset + this.pageSize < this.getItemCount ())
            this.bankOffset += this.pageSize;
    }


    /**
     * Set the bank offset. Used for keeping in sync with another bank.
     *
     * @param bankOffset The bank offset
     */
    public void setBankOffset (final int bankOffset)
    {
        this.bankOffset = bankOffset;
    }


    /**
     * Check if the given position is on the currently selected page.
     *
     * @param position The position of the item
     * @return True if on selected page
     */
    protected boolean isOnSelectedPage (final int position)
    {
        return position >= this.bankOffset && position < this.bankOffset + this.pageSize;
    }
}
