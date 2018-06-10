// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.ArrayList;


/**
 * A filter column for device categories.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
            this.items[i] = new DeviceVendorBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return DeviceManager.get ().getCategories ().size ();
    }

    /** An item of the column. */
    private class DeviceVendorBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public DeviceVendorBrowserColumnItem (final int index)
        {
            super (index - 1);
        }


        /** {@inheritDoc} */
        @Override
        public String getName ()
        {
            if (this.index < 0)
                return WILDCARD;
            return this.index < DeviceCategoryFilterColumn.this.getMaxNumItems () ? new ArrayList<> (DeviceManager.get ().getCategories ()).get (this.index) : "";
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceCategoryFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        public int getHitCount ()
        {
            final DeviceManager deviceManager = DeviceManager.get ();
            if (this.index < 0)
                return deviceManager.getNumDevices ();
            return this.index < DeviceCategoryFilterColumn.this.getMaxNumItems () ? deviceManager.filterByCategory (new ArrayList<> (deviceManager.getCategories ()).get (this.index)).size () : 0;
        }
    }
}
