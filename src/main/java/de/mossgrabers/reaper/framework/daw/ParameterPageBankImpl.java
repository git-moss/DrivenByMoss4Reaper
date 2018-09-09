// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.daw.ItemSelectionObserver;
import de.mossgrabers.reaper.communication.MessageSender;

import java.util.ArrayList;
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
    private MessageSender sender;
    private List<String>  pageNames = new ArrayList<> ();
    private int           pageSize;
    private int           page      = 0;
    private int           bank      = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numParameterPages The number of parameter pages in the page of the bank
     */
    public ParameterPageBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numParameterPages)
    {
        this.sender = sender;
        this.pageSize = numParameterPages;
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
        return this.pageNames.size ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.page > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.page < this.pageNames.size () - 1;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        // To support displaying the newly selected device
        if (this.page > 0)
        {
            this.page--;
            this.sender.sendOSC ("/device/param/-", null);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        // To support displaying the newly selected device
        if (this.page < this.pageNames.size () - 1)
        {
            this.page++;
            this.sender.sendOSC ("/device/param/+", null);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        this.sender.sendOSC ("/device/param/bank/-", null);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        this.sender.sendOSC ("/device/param/bank/+", null);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.sender.sendOSC ("/device/param/bank/selected", Integer.valueOf (position + 1));
    }


    /**
     * Store the position in the page and bank.
     *
     * @param position The position to store
     */
    public void storePosition (final int position)
    {
        this.page = position % this.pageSize;
        this.bank = this.page / this.pageSize;
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
        final int start = this.getScrollPosition () + index;
        return start >= 0 && start < this.pageNames.size () ? this.pageNames.get (start) : "";
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemPosition ()
    {
        return this.bank * this.pageSize + this.page;
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemIndex ()
    {
        return this.page;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedItem ()
    {
        final int sel = this.getSelectedItemPosition ();
        return sel >= 0 && sel < this.pageNames.size () ? this.pageNames.get (sel) : "";
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
        return this.bank * this.pageSize;
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
        return Math.min (this.getScrollPosition () + this.pageSize, this.pageNames.size ()) - 1;
    }


    /**
     * Set all page names.
     *
     * @param pageNames The page names to set
     */
    public void setPageNames (final String [] pageNames)
    {
        this.pageNames.clear ();
        Collections.addAll (this.pageNames, pageNames);
    }
}