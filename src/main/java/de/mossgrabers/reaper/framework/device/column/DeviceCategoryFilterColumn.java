// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.List;


/**
 * A filter column for device categories.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceCategoryFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public DeviceCategoryFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "Category", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceCategoryBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getAllItems ()
    {
        return DeviceManager.get ().getCategories ();
    }


    /** An item of the column. */
    private class DeviceCategoryBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public DeviceCategoryBrowserColumnItem (final int index)
        {
            super (index);
        }


        /** {@inheritDoc} */
        @Override
        public int getIndex ()
        {
            return DeviceCategoryFilterColumn.this.calcPosition (this.index);
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.getIndex () == DeviceCategoryFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        protected String getCachedName ()
        {
            if (this.position == 0)
                return WILDCARD;
            final int pos = this.position - 1;
            return pos < DeviceCategoryFilterColumn.this.getMaxNumItems () ? DeviceCategoryFilterColumn.this.getAllItems ().get (pos) : "";
        }


        /** {@inheritDoc} */
        @Override
        protected int getCachedHitCount ()
        {
            final DeviceManager deviceManager = DeviceManager.get ();
            if (this.position == 0)
                return deviceManager.getNumDevices ();
            final int pos = this.position - 1;
            return pos < DeviceCategoryFilterColumn.this.getMaxNumItems () ? deviceManager.filterByCategory (DeviceCategoryFilterColumn.this.getAllItems ().get (pos)).size () : 0;
        }
    }
}
