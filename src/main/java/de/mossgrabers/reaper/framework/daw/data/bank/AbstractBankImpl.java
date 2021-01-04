// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.AbstractItemBank;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * An abstract bank which provides some basic features.
 *
 * @param <T> The specific item type of the bank item
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractBankImpl<T extends IItem> extends AbstractItemBank<T>
{
    protected final DataSetupEx   dataSetup;
    protected final MessageSender sender;
    protected final IValueChanger valueChanger;

    protected int                 itemCount;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param pageSize The number of elements in a page of the bank
     */
    public AbstractBankImpl (final DataSetupEx dataSetup, final int pageSize)
    {
        super (dataSetup == null ? null : dataSetup.getHost (), pageSize);

        this.dataSetup = dataSetup;
        this.sender = dataSetup != null ? dataSetup.getSender () : null;
        this.valueChanger = dataSetup != null ? dataSetup.getValueChanger () : null;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.itemCount;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        // Overwrite to support
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        // Overwrite to support
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        synchronized (this.items)
        {
            return !this.items.isEmpty () && this.getItem (0).getPosition () > 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        final T sel = this.getSelectedItem ();
        return sel != null && sel.getPosition () < this.getItemCount () - 1;
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
        // Overwrite to support
    }
}
