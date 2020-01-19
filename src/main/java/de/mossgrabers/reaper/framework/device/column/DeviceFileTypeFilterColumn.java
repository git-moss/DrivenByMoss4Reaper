// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
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
         * @param index The index of the item
         */
        public DeviceTypeBrowserColumnItem (final int index)
        {
            super (index - 1);

            this.name = this.getCachedName ();
            this.hits = this.getCachedHitCount ();
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceFileTypeFilterColumn.this.selectedRow;
        }


        private String getCachedName ()
        {
            if (this.index < 0)
                return WILDCARD;
            final DeviceFileType [] values = DeviceFileType.values ();
            return this.index < values.length ? values[this.index].getName () : "";
        }


        private int getCachedHitCount ()
        {
            if (this.index < 0)
                return DeviceManager.get ().getNumDevices ();
            final DeviceFileType [] values = DeviceFileType.values ();
            return this.index < values.length ? DeviceManager.get ().filterByFileType (values[this.index]).size () : 0;
        }
    }
}
