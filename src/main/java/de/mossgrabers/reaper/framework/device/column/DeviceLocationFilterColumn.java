// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
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
            super (index - 1);
        }


        /** {@inheritDoc} */
        @Override
        public String getName ()
        {
            if (this.index < 0)
                return WILDCARD;
            final DeviceLocation [] values = DeviceLocation.values ();
            return this.index < values.length ? values[this.index].getName () : "";
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceLocationFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        public int getHitCount ()
        {
            if (this.index < 0)
                return DeviceManager.get ().getNumDevices ();
            final DeviceLocation [] values = DeviceLocation.values ();
            return this.index < values.length ? DeviceManager.get ().filterByLocation (values[this.index]).size () : 0;
        }
    }
}
