// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;


/**
 * A filter column for device tags.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceTagsFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public DeviceTagsFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "Tags", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceTagsBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return 0;
    }

    /** An item of the column. */
    private class DeviceTagsBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public DeviceTagsBrowserColumnItem (final int index)
        {
            super (index - 1);
        }


        /** {@inheritDoc} */
        @Override
        public String getName ()
        {
            if (this.index < 0)
                return WILDCARD;
            return "";
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceTagsFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        public int getHitCount ()
        {
            if (this.index < 0)
                return DeviceManager.get ().getNumDevices ();
            return 0;
        }
    }
}
