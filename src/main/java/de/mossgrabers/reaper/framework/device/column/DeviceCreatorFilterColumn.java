// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;


/**
 * A filter column for device vendors.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
    protected int getMaxNumItems ()
    {
        return DeviceManager.get ().getVendors ().size ();
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

            this.name = this.getCachedName ();
            this.hits = this.getCachedHitCount ();
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceCreatorFilterColumn.this.selectedRow;
        }


        private String getCachedName ()
        {
            if (this.index < 0)
                return WILDCARD;
            return this.index < DeviceCreatorFilterColumn.this.getMaxNumItems () ? DeviceManager.get ().getVendors ().get (this.index) : "";
        }


        private int getCachedHitCount ()
        {
            final DeviceManager deviceManager = DeviceManager.get ();
            if (this.index < 0)
                return deviceManager.getNumDevices ();
            return this.index < DeviceCreatorFilterColumn.this.getMaxNumItems () ? deviceManager.filterByVendor (deviceManager.getVendors ().get (this.index)).size () : 0;
        }
    }
}
