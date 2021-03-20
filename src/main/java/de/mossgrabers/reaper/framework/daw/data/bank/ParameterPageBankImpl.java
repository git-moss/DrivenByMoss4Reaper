// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.bank.AbstractBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.observer.IItemSelectionObserver;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Encapsulates the data of parameter pages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterPageBankImpl extends AbstractBank<String> implements IParameterPageBank
{
    private IParameterBank parameterBank;


    /**
     * Constructor.
     *
     * @param numParameterPages The number of parameter pages in the page of the bank
     * @param parameterBank The parameter bank
     */
    public ParameterPageBankImpl (final int numParameterPages, final IParameterBank parameterBank)
    {
        super (null, numParameterPages);

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
        return this.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.getSelectedItemPosition () < this.parameterBank.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.parameterBank.getScrollPosition () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.parameterBank.getScrollPosition () + this.parameterBank.getPageSize () < this.parameterBank.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.parameterBank.scrollBackwards ();
        ((ParameterBankImpl) this.parameterBank).firePageObserver ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.parameterBank.scrollForwards ();
        ((ParameterBankImpl) this.parameterBank).firePageObserver ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.parameterBank.scrollTo (position * this.parameterBank.getPageSize ());
        ((ParameterBankImpl) this.parameterBank).firePageObserver ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
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
        return this.getSelectedItemPosition () % this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItem ()
    {
        return Optional.of (this.getItem (this.getSelectedItemIndex ()));
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getSelectedItems ()
    {
        final Optional<String> selectedItem = this.getSelectedItem ();
        return Collections.singletonList (selectedItem.isPresent () ? selectedItem.get () : "");
    }


    /** {@inheritDoc} */
    @Override
    public void selectPage (final int index)
    {
        this.scrollTo (this.getScrollPosition () + index);
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final IItemSelectionObserver observer)
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
        this.parameterBank.selectPreviousPage ();
        ((ParameterBankImpl) this.parameterBank).firePageObserver ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.parameterBank.selectNextPage ();
        ((ParameterBankImpl) this.parameterBank).firePageObserver ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
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