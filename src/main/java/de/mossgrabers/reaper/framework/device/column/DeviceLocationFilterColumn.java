// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.Collections;
import java.util.List;


/**
 * A filter column for device locations.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceLocationFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public DeviceLocationFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "Architecture", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceLocationBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getAllItems ()
    {
        return Collections.emptyList ();
    }


    /** An item of the column. */
    private class DeviceLocationBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public DeviceLocationBrowserColumnItem (final int index)
        {
            super (index);
        }


        /** {@inheritDoc} */
        @Override
        public int getIndex ()
        {
            return DeviceLocationFilterColumn.this.calcPosition (this.index);
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.getIndex () == DeviceLocationFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        protected String getCachedName ()
        {
            return this.position == 0 ? WILDCARD : "";
        }


        /** {@inheritDoc} */
        @Override
        protected int getCachedHitCount ()
        {
            return this.position == 0 ? DeviceManager.get ().getNumDevices () : 0;
        }
    }
}
