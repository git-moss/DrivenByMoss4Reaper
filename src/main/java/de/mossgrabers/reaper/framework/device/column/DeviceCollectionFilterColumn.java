// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceManager;


/**
 * A filter column for device folders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceCollectionFilterColumn extends BaseColumn
{
    /**
     * Constructor.
     *
     * @param columnIndex The index of the column
     * @param numFilterColumnEntries The number of items to display on one page
     */
    public DeviceCollectionFilterColumn (final int columnIndex, final int numFilterColumnEntries)
    {
        super (columnIndex, "Collection", numFilterColumnEntries);

        for (int i = 0; i < numFilterColumnEntries; i++)
            this.items[i] = new DeviceFolderBrowserColumnItem (i);
    }


    /** {@inheritDoc} */
    @Override
    protected int getMaxNumItems ()
    {
        return DeviceManager.get ().getCollections ().size ();
    }


    /** An item of the column. */
    private class DeviceFolderBrowserColumnItem extends BaseColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public DeviceFolderBrowserColumnItem (final int index)
        {
            super (index - 1);

            this.name = this.getCachedName ();
            this.hits = this.getCachedHitCount ();
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.index + 1 == DeviceCollectionFilterColumn.this.selectedRow;
        }


        private String getCachedName ()
        {
            if (this.index < 0)
                return WILDCARD;
            return this.index < DeviceCollectionFilterColumn.this.getMaxNumItems () ? DeviceManager.get ().getCollections ().get (this.index).getName () : "";
        }


        private int getCachedHitCount ()
        {
            final DeviceManager deviceManager = DeviceManager.get ();
            if (this.index < 0)
                return deviceManager.getNumDevices ();
            return this.index < DeviceCollectionFilterColumn.this.getMaxNumItems () ? deviceManager.filterByCollection (deviceManager.getCollections ().get (this.index)).size () : 0;
        }
    }
}
