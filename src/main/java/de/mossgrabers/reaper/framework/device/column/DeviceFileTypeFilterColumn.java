// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.reaper.framework.device.DeviceFileType;
import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.ArrayList;
import java.util.List;


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
        return DeviceManager.get ().getAvailableFileTypes ().size ();
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getAllItems ()
    {
        final List<DeviceFileType> types = DeviceManager.get ().getAvailableFileTypes ();
        final List<String> result = new ArrayList<> (types.size ());
        for (final DeviceFileType location: types)
            result.add (location.getName ());
        return result;
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
            final List<DeviceFileType> types = DeviceManager.get ().getAvailableFileTypes ();
            final int pos = this.position - 1;
            return pos < types.size () ? types.get (pos).getName () : "";
        }


        /** {@inheritDoc} */
        @Override
        protected int getCachedHitCount ()
        {
            if (this.position == 0)
                return DeviceManager.get ().getNumDevices ();
            final List<DeviceFileType> types = DeviceManager.get ().getAvailableFileTypes ();
            final int pos = this.position - 1;
            return pos < types.size () ? DeviceManager.get ().filterByFileType (types.get (pos)).size () : 0;
        }
    }
}
