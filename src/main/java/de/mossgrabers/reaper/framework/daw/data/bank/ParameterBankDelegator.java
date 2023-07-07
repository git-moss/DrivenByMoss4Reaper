// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameterBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IItemSelectionObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.framework.daw.data.CursorTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.util.List;
import java.util.Optional;


/**
 * Creates a unique access to the different parameter banks the cursor track may point to.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterBankDelegator implements IParameterBank
{
    private final CursorTrackImpl cursorTrack;
    private final int             numPageSize;


    /**
     * Constructor.
     *
     * @param cursorTrack The cursor track
     * @param numPageSize The size of a parameter page
     */
    public ParameterBankDelegator (final CursorTrackImpl cursorTrack, final int numPageSize)
    {
        this.cursorTrack = cursorTrack;
        this.numPageSize = numPageSize;
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return this.getBank ().getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.getBank ().getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasExistingItems ()
    {
        return this.getBank ().hasExistingItems ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getItem (final int index)
    {
        return this.getBank ().getItem (index);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<IParameter> getSelectedItem ()
    {
        return this.getBank ().getSelectedItem ();
    }


    /** {@inheritDoc} */
    @Override
    public List<IParameter> getSelectedItems ()
    {
        return this.getBank ().getSelectedItems ();
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final IItemSelectionObserver observer)
    {
        this.getBank ().addSelectionObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeSelectionObserver (final IItemSelectionObserver observer)
    {
        this.getBank ().removeSelectionObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void addPageObserver (final IBankPageObserver observer)
    {
        this.getBank ().addPageObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removePageObserver (final IBankPageObserver observer)
    {
        this.getBank ().removePageObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.getBank ().canScrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.getBank ().canScrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.getBank ().canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.getBank ().canScrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.getBank ().scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.getBank ().scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.getBank ().scrollTo (position);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        this.getBank ().scrollTo (position, adjustPage);
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.getBank ().getScrollPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        return this.getBank ().getPositionOfLastItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        this.getBank ().selectItemAtPosition (position);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.getBank ().selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.getBank ().selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.getBank ().selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.getBank ().selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSkipDisabledItems (final boolean shouldSkip)
    {
        this.getBank ().setSkipDisabledItems (shouldSkip);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.getBank ().enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public IParameterPageBank getPageBank ()
    {
        return this.getBank ().getPageBank ();
    }


    private IParameterBank getBank ()
    {
        final TrackImpl selectedTrack = (TrackImpl) this.cursorTrack.getPinnedOrSelectedTrack ();
        return selectedTrack == null ? EmptyParameterBank.getInstance (this.numPageSize) : selectedTrack.getParameterBank ();
    }
}
