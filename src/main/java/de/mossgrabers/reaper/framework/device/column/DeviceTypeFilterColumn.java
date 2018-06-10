// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.device.DeviceType;


/**
 * A filter column for device types.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceTypeFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public DeviceTypeFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "Device Type", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceTypeBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return DeviceType.values ().length;
    }

    /** An item of the column. */
    private class DeviceTypeBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public DeviceTypeBrowserColumnItem (final int index)
        {
            super (index - 1);
        }


        /** {@inheritDoc} */
        @Override
        public String getName ()
        {
            if (this.index < 0)
                return WILDCARD;
            final DeviceType [] values = DeviceType.values ();
            return this.index < values.length ? values[this.index].getName () : "";
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceTypeFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        public int getHitCount ()
        {
            if (this.index < 0)
                return DeviceManager.get ().getNumDevices ();
            final DeviceType [] values = DeviceType.values ();
            return this.index < values.length ? DeviceManager.get ().filterByType (values[this.index]).size () : 0;
        }
    }
}
