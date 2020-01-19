// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;


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
            this.items[i] = new DeviceCategoryBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return DeviceManager.get ().getCategories ().size ();
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
            super (index - 1);

            this.name = this.getCachedName ();
            this.hits = this.getCachedHitCount ();
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceCategoryFilterColumn.this.selectedRow;
        }


        private String getCachedName ()
        {
            if (this.index < 0)
                return WILDCARD;
            return this.index < DeviceCategoryFilterColumn.this.getMaxNumItems () ? DeviceManager.get ().getCategories ().get (this.index) : "";
        }


        private int getCachedHitCount ()
        {
            final DeviceManager deviceManager = DeviceManager.get ();
            if (this.index < 0)
                return deviceManager.getNumDevices ();
            return this.index < DeviceCategoryFilterColumn.this.getMaxNumItems () ? deviceManager.filterByCategory (deviceManager.getCategories ().get (this.index)).size () : 0;
        }
    }
}
