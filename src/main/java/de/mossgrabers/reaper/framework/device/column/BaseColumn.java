// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;

import java.util.HashSet;
import java.util.Set;


/**
 * Base class for a filter column in the browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseColumn extends ItemImpl implements IBrowserColumn
{
    protected static final String         WILDCARD    = "All";

    protected final IBrowserColumnItem [] items;

    private final Set<FilterListener>     listeners   = new HashSet<> (1);

    private final String                  name;
    private final int                     numItemsPerPage;

    int                                   selectedRow = 0;


    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param name The name of the column
     * @param numItemsPerPage The number of items on one page
     */
    protected BaseColumn (final int columnIndex, final String name, final int numItemsPerPage)
    {
        super (null, null, columnIndex);

        this.name = name;
        this.numItemsPerPage = numItemsPerPage;

        this.items = new IBrowserColumnItem [numItemsPerPage];
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public String getWildcard ()
    {
        return WILDCARD;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesCursorExist ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getCursorName ()
    {
        return this.items[this.selectedRow].getName ();
    }


    /** {@inheritDoc} */
    @Override
    public String getCursorName (final int limit)
    {
        return StringUtils.optimizeName (this.getCursorName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public IBrowserColumnItem [] getItems ()
    {
        return this.items;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollItemPageUp ()
    {
        this.setCursorIndex (this.selectedRow - this.numItemsPerPage);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollItemPageDown ()
    {
        this.setCursorIndex (this.selectedRow + this.numItemsPerPage);
    }


    /** {@inheritDoc} */
    @Override
    public void resetFilter ()
    {
        this.setCursorIndex (0);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.setCursorIndex (this.selectedRow - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.setCursorIndex (this.selectedRow + 1);
    }


    /** {@inheritDoc} */
    @Override
    public int getCursorIndex ()
    {
        return this.selectedRow;
    }


    /** {@inheritDoc} */
    @Override
    public void setCursorIndex (final int index)
    {
        this.selectedRow = Math.max (0, Math.min (index, this.getMaxNumItems ()));
        this.notifyListeners ();
    }


    /**
     * Get the maximum number of items available in this filter.
     *
     * @return The number
     */
    protected abstract int getMaxNumItems ();


    /**
     * Register a selection listener.
     *
     * @param listener THe listener
     */
    public void addSelectionListener (final FilterListener listener)
    {
        this.listeners.add (listener);
    }


    /**
     * Notify all listeners.
     */
    private void notifyListeners ()
    {
        for (final FilterListener listener: this.listeners)
            listener.hasChanged ();
    }
}
