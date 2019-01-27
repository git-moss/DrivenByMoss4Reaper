// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.observer.ItemSelectionObserver;

import java.util.Collections;
import java.util.List;


/**
 * Encapsulates the data of parameter pages. Bitwig pages have no banking, we need to do it
 * ourselves.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterPageBankImpl implements IParameterPageBank
{
    private IParameterBank parameterBank;
    private int            pageSize;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param numParameterPages The number of parameter pages in the page of the bank
     * @param parameterBank The parameter bank
     */
    public ParameterPageBankImpl (final IHost host, final IValueChanger valueChanger, final int numParameterPages, final IParameterBank parameterBank)
    {
        this.pageSize = numParameterPages;
        this.parameterBank = parameterBank;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        final int itemCount = this.parameterBank.getItemCount ();
        final int ps = this.parameterBank.getPageSize ();
        return itemCount / ps + (itemCount % ps > 0 ? 1 : 0);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.parameterBank.getScrollPosition () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.parameterBank.getScrollPosition () + this.parameterBank.getPageSize () < this.parameterBank.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.parameterBank.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.parameterBank.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        this.parameterBank.scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        this.parameterBank.scrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.parameterBank.scrollTo (position * this.parameterBank.getPageSize ());
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (int position, boolean adjustPage)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public String getItem (final int index)
    {
        final int pos = this.getScrollPosition () + index;
        return pos < this.getItemCount () ? "Page " + (pos + 1) : "";
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemPosition ()
    {
        final int ps = this.parameterBank.getPageSize ();
        final int scrollPosition = this.parameterBank.getScrollPosition ();
        return scrollPosition / ps + (scrollPosition % ps > 0 ? 1 : 0);
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemIndex ()
    {
        return getSelectedItemPosition () % this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedItem ()
    {
        return this.getItem (this.getSelectedItemIndex ());
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getSelectedItems ()
    {
        return Collections.singletonList (this.getSelectedItem ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectPage (final int index)
    {
        this.scrollTo (this.getScrollPosition () + index);
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final ItemSelectionObserver observer)
    {
        // Not selected
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        final int scrollPosition = this.getSelectedItemPosition ();
        return scrollPosition / this.pageSize * this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (int position)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        return Math.min (this.getScrollPosition () + this.pageSize, this.getItemCount ()) - 1;
    }
}