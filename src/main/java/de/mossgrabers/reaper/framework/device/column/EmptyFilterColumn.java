// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;


/**
 * An empty filter column (actually no filter at all).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public EmptyFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new EmptyBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return DeviceManager.get ().getCategories ().size ();
    }

    /** An item of the column. */
    private class EmptyBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public EmptyBrowserColumnItem (final int index)
        {
            super (index - 1);
        }


        /** {@inheritDoc} */
        @Override
        public String getName ()
        {
            return "";
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return false;
        }


        /** {@inheritDoc} */
        @Override
        public int getHitCount ()
        {
            return 0;
        }
    }
}
