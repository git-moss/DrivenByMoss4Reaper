// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.AbstractBrowser;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.data.AbstractItemImpl;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.framework.device.Device;
import de.mossgrabers.reaper.framework.device.DeviceCollection;
import de.mossgrabers.reaper.framework.device.DeviceFileType;
import de.mossgrabers.reaper.framework.device.DeviceLocation;
import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.device.DeviceType;
import de.mossgrabers.reaper.framework.device.column.DeviceCategoryFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceCollectionFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceCreatorFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceFileTypeFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceLocationFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceTagsFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceTypeFilterColumn;
import de.mossgrabers.reaper.framework.device.column.EmptyFilterColumn;
import de.mossgrabers.transformator.communication.MessageSender;

import java.util.Collections;
import java.util.List;


/**
 * Provides access to the presets of a Reaper FX. Since this is only a flat list, no filter columns
 * are supported.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserImpl extends AbstractBrowser
{
    private enum ContentType
    {
        CONTENTTYPE_DEVICE,
        CONTENTTYPE_PRESET
    }

    private static final String []     CONTENT_TYPE_NAMES = new String []
    {
        "Devices",
        "Presets"
    };

    private ContentType                contentType        = ContentType.CONTENTTYPE_PRESET;

    final DeviceCollectionFilterColumn deviceCollectionFilterColumn;
    final DeviceLocationFilterColumn   deviceLocationFilterColumn;
    final DeviceFileTypeFilterColumn   deviceFileTypeFilterColumn;
    final DeviceCategoryFilterColumn   deviceCategoryFilterColumn;
    final DeviceTagsFilterColumn       deviceTagsFilterColumn;
    final DeviceCreatorFilterColumn    deviceCreatorFilterColumn;
    final DeviceTypeFilterColumn       deviceTypeFilterColumn;

    private boolean                    isBrowserActive;
    String []                          presets            = new String [128];
    private int                        presetCount        = 0;
    int                                selectedIndex;
    List<Device>                       filteredDevices    = Collections.emptyList ();

    private MessageSender              sender;
    private IBrowserColumn [] []       columnDataContentTypes;

    private int                        insertPosition;


    /**
     * Constructor.
     *
     * @param sender The OSC sender
     * @param cursorDevice The cursor device
     * @param numFilterColumnEntries The number of entries in a filter column page
     * @param numResults The number of entries in a results column page
     */
    public BrowserImpl (final MessageSender sender, final ICursorDevice cursorDevice, final int numFilterColumnEntries, final int numResults)
    {
        super (cursorDevice, numFilterColumnEntries, numResults);

        this.sender = sender;

        this.deviceCollectionFilterColumn = new DeviceCollectionFilterColumn (0, numFilterColumnEntries);
        this.deviceLocationFilterColumn = new DeviceLocationFilterColumn (1, numFilterColumnEntries);
        this.deviceFileTypeFilterColumn = new DeviceFileTypeFilterColumn (2, numFilterColumnEntries);
        this.deviceCategoryFilterColumn = new DeviceCategoryFilterColumn (3, numFilterColumnEntries);
        this.deviceTagsFilterColumn = new DeviceTagsFilterColumn (4, numFilterColumnEntries);
        this.deviceCreatorFilterColumn = new DeviceCreatorFilterColumn (5, numFilterColumnEntries);
        this.deviceTypeFilterColumn = new DeviceTypeFilterColumn (6, numFilterColumnEntries);

        this.columnDataContentTypes = new IBrowserColumn [2] [];
        this.columnDataContentTypes[ContentType.CONTENTTYPE_DEVICE.ordinal ()] = new IBrowserColumn []
        {
            this.deviceCollectionFilterColumn,
            this.deviceLocationFilterColumn,
            this.deviceFileTypeFilterColumn,
            this.deviceCategoryFilterColumn,
            this.deviceTagsFilterColumn,
            this.deviceCreatorFilterColumn,
            this.deviceTypeFilterColumn
        };
        this.columnDataContentTypes[ContentType.CONTENTTYPE_PRESET.ordinal ()] = new IBrowserColumn []
        {
            new EmptyFilterColumn (0, numFilterColumnEntries),
            new EmptyFilterColumn (1, numFilterColumnEntries),
            new EmptyFilterColumn (2, numFilterColumnEntries),
            new EmptyFilterColumn (3, numFilterColumnEntries),
            new EmptyFilterColumn (4, numFilterColumnEntries),
            new EmptyFilterColumn (5, numFilterColumnEntries),
            new EmptyFilterColumn (6, numFilterColumnEntries),
            new EmptyFilterColumn (7, numFilterColumnEntries)
        };
        this.columnData = this.columnDataContentTypes[ContentType.CONTENTTYPE_PRESET.ordinal ()];

        this.resultData = this.createResultData (this.numResults);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPresetContentType ()
    {
        return this.contentType == ContentType.CONTENTTYPE_PRESET;
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedContentTypeIndex ()
    {
        return this.contentType.ordinal ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousContentType ()
    {
        // No content type switching
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextContentType ()
    {
        // No content type switching
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void previousContentType ()
    {
        // No content type switching
    }


    /** {@inheritDoc} */
    @Override
    public void nextContentType ()
    {
        // No content type switching
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedContentType ()
    {
        return CONTENT_TYPE_NAMES[this.contentType.ordinal ()];
    }


    /** {@inheritDoc} */
    @Override
    public String [] getContentTypeNames ()
    {
        return new String []
        {
            this.getSelectedContentType ()
        };
    }


    private void setContentType (final int ordinal)
    {
        final ContentType [] values = ContentType.values ();
        this.contentType = values[Math.min (Math.max (0, ordinal), values.length - 1)];
        this.columnData = this.columnDataContentTypes[ordinal];
        this.selectedIndex = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void browseForPresets ()
    {
        this.stopBrowsing (false);
        this.insertPosition = this.cursorDevice.getPosition ();
        this.setContentType (ContentType.CONTENTTYPE_PRESET.ordinal ());
        this.isBrowserActive = true;
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertBeforeDevice ()
    {
        this.insertDevice (this.cursorDevice.getPosition ());
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAfterDevice ()
    {
        this.insertDevice (this.cursorDevice.getPosition () + 1);
    }


    private void insertDevice (final int insertPos)
    {
        this.stopBrowsing (false);
        this.insertPosition = insertPos;
        this.setContentType (ContentType.CONTENTTYPE_DEVICE.ordinal ());
        this.isBrowserActive = true;
    }


    /** {@inheritDoc} */
    @Override
    public void stopBrowsing (final boolean commitSelection)
    {
        if (commitSelection && this.insertPosition >= 0)
        {
            switch (this.contentType)
            {
                case CONTENTTYPE_DEVICE:
                    final ResultItem result = this.getSelectedResultDevice ();
                    if (result != null)
                    {
                        final Device device = result.getDevice ();
                        if (device != null)
                            this.sender.sendOSC ("/device/add/" + this.insertPosition + "/", device.getCreationName ());
                    }
                    break;

                case CONTENTTYPE_PRESET:
                    final int index = this.getSelectedResultIndex ();
                    if (index != -1)
                        this.sender.sendOSC ("/device/preset/", Integer.valueOf (index));
                    break;
            }
        }

        this.insertPosition = -1;
        this.isBrowserActive = false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActive ()
    {
        return this.isBrowserActive;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousResult ()
    {
        this.setSelectedResult (this.selectedIndex - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextResult ()
    {
        this.setSelectedResult (this.selectedIndex + 1);
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedResult ()
    {
        final ResultItem result = this.getSelectedResultDevice ();
        return result == null ? null : result.getName ();
    }


    private ResultItem getSelectedResultDevice ()
    {
        for (final IBrowserColumnItem element: this.resultData)
        {
            if (element.isSelected ())
                return (ResultItem) element;
        }
        return null;
    }


    private void setSelectedResult (final int index)
    {
        final int length = this.isPresetContentType () ? this.presetCount : this.filteredDevices.size ();
        this.selectedIndex = Math.min (Math.max (0, index), length - 1);
    }


    /**
     * Select the previous result page.
     */
    public void previousResultPage ()
    {
        this.setSelectedResult (this.selectedIndex - this.numResults);
    }


    /**
     * Select the next result page.
     */
    public void nextResultPage ()
    {
        this.setSelectedResult (this.selectedIndex + this.numResults);
    }


    private IBrowserColumnItem [] createResultData (final int count)
    {
        final IBrowserColumnItem [] items = new IBrowserColumnItem [count];
        for (int i = 0; i < count; i++)
            items[i] = new ResultItem (i);
        return items;
    }


    private void updateFilteredDevices ()
    {
        final DeviceManager deviceManager = DeviceManager.get ();
        final DeviceCollection folder = this.deviceCollectionFilterColumn.getCursorIndex () == 0 ? null : deviceManager.getCollection (this.deviceCollectionFilterColumn.getCursorName ());
        final String category = this.deviceCategoryFilterColumn.getCursorIndex () == 0 ? null : this.deviceCategoryFilterColumn.getCursorName ();
        final DeviceFileType fileType = this.deviceFileTypeFilterColumn.getCursorIndex () == 0 ? null : DeviceFileType.valueOf (this.deviceFileTypeFilterColumn.getCursorName ().toUpperCase ());
        final DeviceLocation location = this.deviceLocationFilterColumn.getCursorIndex () == 0 ? null : DeviceLocation.valueOf (this.deviceLocationFilterColumn.getCursorName ().toUpperCase ());
        // Note: this.deviceTagsFilterColumn currently does nothing
        final String vendor = this.deviceCreatorFilterColumn.getCursorIndex () == 0 ? null : this.deviceCreatorFilterColumn.getCursorName ();
        final DeviceType type = this.deviceTypeFilterColumn.getCursorIndex () == 0 ? null : DeviceType.valueOf (this.deviceTypeFilterColumn.getCursorName ().toUpperCase ().replace (' ', '_'));

        this.filteredDevices = deviceManager.filterBy (fileType, category, vendor, folder, location, type);

        if (this.selectedIndex >= this.filteredDevices.size ())
            this.selectedIndex = 0;
    }


    /** {@inheritDoc} */
    @Override
    public IBrowserColumnItem [] getResultColumnItems ()
    {
        if (!this.isPresetContentType ())
            this.updateFilteredDevices ();
        return super.getResultColumnItems ();
    }


    /**
     * Set a preset.
     *
     * @param index The index of the preset
     * @param name The name of the preset
     */
    public void setPreset (final int index, final String name)
    {
        this.presets[index] = name;

        // Update number of available presets
        for (int i = 0; i < this.presets.length; i++)
        {
            if (this.presets[i] == null)
            {
                this.presetCount = i;
                break;
            }
        }
        if (this.selectedIndex >= this.presetCount)
            this.selectedIndex = this.presetCount - 1;
    }


    /**
     * Set the selected preset.
     *
     * @param index The index of the preset to select
     */
    public void setPresetSelected (final int index)
    {
        this.selectedIndex = index;
    }


    int translateBankIndexToPageOfSelectedIndex (final int index)
    {
        return this.selectedIndex / this.numResults * this.numResults + index;
    }

    /** An item in the result column. */
    private class ResultItem extends AbstractItemImpl implements IBrowserColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public ResultItem (final int index)
        {
            super (index);
        }


        /** {@inheritDoc} */
        @Override
        public boolean doesExist ()
        {
            if (BrowserImpl.this.isPresetContentType ())
                return BrowserImpl.this.presets[this.getIndex ()] != null;
            return this.getIndex () < BrowserImpl.this.filteredDevices.size ();
        }


        /** {@inheritDoc} */
        @Override
        public String getName ()
        {
            final int index = this.getIndex ();
            if (BrowserImpl.this.isPresetContentType ())
                return BrowserImpl.this.presets[index] == null ? "" : BrowserImpl.this.presets[index];
            final int id = BrowserImpl.this.translateBankIndexToPageOfSelectedIndex (index);
            return id < BrowserImpl.this.filteredDevices.size () ? BrowserImpl.this.filteredDevices.get (id).getDisplayName () : "";
        }


        /** {@inheritDoc} */
        @Override
        public String getName (final int limit)
        {
            return StringUtils.optimizeName (this.getName (), limit);
        }


        /** {@inheritDoc} */
        @Override
        public boolean isSelected ()
        {
            return BrowserImpl.this.translateBankIndexToPageOfSelectedIndex (this.getIndex ()) == BrowserImpl.this.selectedIndex;
        }


        /** {@inheritDoc} */
        @Override
        public int getHitCount ()
        {
            return 0;
        }


        public Device getDevice ()
        {
            final int id = BrowserImpl.this.translateBankIndexToPageOfSelectedIndex (this.getIndex ());
            return id < BrowserImpl.this.filteredDevices.size () ? BrowserImpl.this.filteredDevices.get (id) : null;
        }
    }
}