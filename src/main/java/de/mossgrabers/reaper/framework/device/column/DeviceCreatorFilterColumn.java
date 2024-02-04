// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.List;


/**
 * A filter column for device vendors.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceCreatorFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public DeviceCreatorFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "Creator", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceVendorBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getAllItems ()
    {
        return DeviceManager.get ().getVendors ();
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
            super (index);
        }


        /** {@inheritDoc} */
        @Override
        public int getIndex ()
        {
            return DeviceCreatorFilterColumn.this.calcPosition (this.index);
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.getIndex () == DeviceCreatorFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        protected String getCachedName ()
        {
            if (this.position == 0)
                return WILDCARD;
            final int pos = this.position - 1;
            return pos < DeviceCreatorFilterColumn.this.getMaxNumItems () ? DeviceManager.get ().getVendors ().get (pos) : "";
        }


        /** {@inheritDoc} */
        @Override
        protected int getCachedHitCount ()
        {
            final DeviceManager deviceManager = DeviceManager.get ();
            if (this.position == 0)
                return deviceManager.getNumDevices ();
            final int pos = this.position - 1;
            return pos < DeviceCreatorFilterColumn.this.getMaxNumItems () ? deviceManager.filterByVendor (deviceManager.getVendors ().get (pos)).size () : 0;
        }
    }
}
