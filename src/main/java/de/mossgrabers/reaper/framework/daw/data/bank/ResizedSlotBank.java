package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IItemSelectionObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Provides access to the slot bank of the cursor track but with a different page size.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ResizedSlotBank implements ISlotBank
{
    private static final Map<ISlotBank, SlotBankImpl> resizedBanks = new HashMap<> ();

    private final ICursorTrack                        cursorTrack;
    private final int                                 differentPageSize;


    /**
     * Constructor.
     * 
     * @param cursorTrack The cursor track
     * @param differentPageSize The new page size
     */
    public ResizedSlotBank (final ICursorTrack cursorTrack, final int differentPageSize)
    {
        this.cursorTrack = cursorTrack;
        this.differentPageSize = differentPageSize;
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return this.differentPageSize;
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.getResizedBank ().getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasExistingItems ()
    {
        return this.getResizedBank ().hasExistingItems ();
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getItem (int index)
    {
        return this.getResizedBank ().getItem (index);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISlot> getSelectedItem ()
    {
        return this.getResizedBank ().getSelectedItem ();
    }


    /** {@inheritDoc} */
    @Override
    public List<ISlot> getSelectedItems ()
    {
        return this.getResizedBank ().getSelectedItems ();
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (IItemSelectionObserver observer)
    {
        this.getResizedBank ().addSelectionObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void removeSelectionObserver (IItemSelectionObserver observer)
    {
        this.getResizedBank ().removeSelectionObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void addPageObserver (IBankPageObserver observer)
    {
        this.getResizedBank ().addPageObserver (observer);

    }


    /** {@inheritDoc} */
    @Override
    public void removePageObserver (IBankPageObserver observer)
    {
        this.getResizedBank ().removePageObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.getResizedBank ().canScrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.getResizedBank ().canScrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.getResizedBank ().canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.getResizedBank ().canScrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.getResizedBank ().scrollBackwards ();

    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.getResizedBank ().scrollForwards ();

    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (int position)
    {
        this.getResizedBank ().scrollTo (position);

    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (int position, boolean adjustPage)
    {
        this.getResizedBank ().scrollTo (position, adjustPage);
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.getResizedBank ().getScrollPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        return this.getResizedBank ().getPositionOfLastItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (int position)
    {
        this.getResizedBank ().selectItemAtPosition (position);

    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.getResizedBank ().selectNextItem ();

    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.getResizedBank ().selectPreviousItem ();

    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.getResizedBank ().selectNextPage ();

    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.getResizedBank ().selectPreviousPage ();

    }


    /** {@inheritDoc} */
    @Override
    public void setSkipDisabledItems (boolean shouldSkip)
    {
        this.getResizedBank ().setSkipDisabledItems (shouldSkip);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (boolean enable)
    {
        this.getResizedBank ().enableObservers (enable);

    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISlot> getEmptySlot (int startFrom)
    {
        return this.getResizedBank ().getEmptySlot (startFrom);
    }


    private ISlotBank getResizedBank ()
    {
        // This points to the slot bank of a track object and is only redirected via the cursor
        // track
        final ISlotBank cursorSlotBank = this.cursorTrack.getSlotBank ();
        synchronized (resizedBanks)
        {
            final SlotBankImpl slotBankImpl = (SlotBankImpl) cursorSlotBank;
            return resizedBanks.computeIfAbsent (cursorSlotBank, bank -> new SlotBankImpl ((SlotBankImpl) bank, this.differentPageSize)
            {
                /** {@inheritDoc} */
                @Override
                public int getItemCount ()
                {
                    return slotBankImpl.getItemCount ();
                }
            });
        }
    }
}
