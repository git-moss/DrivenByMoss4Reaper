// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceFileType;
import de.mossgrabers.reaper.framework.device.DeviceManager;


/**
 * A filter column for device file types.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceFileTypeFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public DeviceFileTypeFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "File Type", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceTypeBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return DeviceFileType.values ().length;
    }


    /** An item of the column. */
    private class DeviceTypeBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item (in the page)
         */
        public DeviceTypeBrowserColumnItem (final int index)
        {
            super (index);
        }


        /** {@inheritDoc} */
        @Override
        public int getIndex ()
        {
            return DeviceFileTypeFilterColumn.this.calcPosition (this.index);
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.getIndex () == DeviceFileTypeFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        protected String getCachedName ()
        {
            if (this.position == 0)
                return WILDCARD;
            final DeviceFileType [] values = DeviceFileType.values ();
            final int pos = this.position - 1;
            return pos < values.length ? values[pos].getName () : "";
        }


        /** {@inheritDoc} */
        @Override
        protected int getCachedHitCount ()
        {
            if (this.position == 0)
                return DeviceManager.get ().getNumDevices ();
            final DeviceFileType [] values = DeviceFileType.values ();
            final int pos = this.position - 1;
            return pos < values.length ? DeviceManager.get ().filterByFileType (values[pos]).size () : 0;
        }
    }
}
