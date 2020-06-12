// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceLocation;
import de.mossgrabers.reaper.framework.device.DeviceManager;


/**
 * A filter column for device locations.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
        super (columnIndex, "Location", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceLocationBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return DeviceLocation.values ().length;
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
            return calcPosition (this.index);
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
            if (this.position == 0)
                return WILDCARD;
            final DeviceLocation [] values = DeviceLocation.values ();
            final int pos = this.position - 1;
            return pos < values.length ? values[pos].getName () : "";
        }


        /** {@inheritDoc} */
        @Override
        protected int getCachedHitCount ()
        {
            if (this.position == 0)
                return DeviceManager.get ().getNumDevices ();
            final DeviceLocation [] values = DeviceLocation.values ();
            final int pos = this.position - 1;
            return pos < values.length ? DeviceManager.get ().filterByLocation (values[pos]).size () : 0;
        }
    }
}
