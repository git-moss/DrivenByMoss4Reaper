// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceCollection;
import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A filter column for device folders.
 *
 * @author Jürgen Moßgraber
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


    /** {@inheritDoc} */
    @Override
    public List<String> getAllItems ()
    {
        final List<DeviceCollection> collections = DeviceManager.get ().getCollections ();
        final List<String> result = new ArrayList<> (collections.size ());
        for (final DeviceCollection collection: collections)
            result.add (collection.getName ());
        return result;
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
            super (index);
        }


        /** {@inheritDoc} */
        @Override
        public int getIndex ()
        {
            return DeviceCollectionFilterColumn.this.calcPosition (this.index);
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return this.getIndex () == DeviceCollectionFilterColumn.this.selectedRow;
        }


        /** {@inheritDoc} */
        @Override
        protected String getCachedName ()
        {
            if (this.position == 0)
                return WILDCARD;
            final int pos = this.position - 1;
            return pos < DeviceCollectionFilterColumn.this.getMaxNumItems () ? DeviceManager.get ().getCollections ().get (pos).getName () : "";
        }


        /** {@inheritDoc} */
        @Override
        protected int getCachedHitCount ()
        {
            final DeviceManager deviceManager = DeviceManager.get ();
            if (this.position == 0)
                return deviceManager.getNumDevices ();
            final int pos = this.position - 1;
            return pos < DeviceCollectionFilterColumn.this.getMaxNumItems () ? deviceManager.filterByCollection (deviceManager.getCollections ().get (pos)).size () : 0;
        }
    }
}
