// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.AbstractBrowser;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.data.CursorDeviceImpl;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;
import de.mossgrabers.reaper.framework.device.DeviceArchitecture;
import de.mossgrabers.reaper.framework.device.DeviceCollection;
import de.mossgrabers.reaper.framework.device.DeviceFileType;
import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.device.DeviceMetadataImpl;
import de.mossgrabers.reaper.framework.device.DeviceType;
import de.mossgrabers.reaper.framework.device.column.BaseColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceCategoryFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceCollectionFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceCreatorFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceFileTypeFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceLocationFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceTagsFilterColumn;
import de.mossgrabers.reaper.framework.device.column.DeviceTypeFilterColumn;
import de.mossgrabers.reaper.framework.device.column.EmptyFilterColumn;
import de.mossgrabers.reaper.ui.dialog.BrowserDialog;

import com.nikhaldimann.inieditor.IniEditor;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Provides access to the presets of a Reaper FX. Since this is only a flat list, no filter columns
 * are supported.
 *
 * @author Jürgen Moßgraber
 */
public class BrowserImpl extends AbstractBrowser
{
    private static final String [] CONTENT_TYPE_NAMES = new String []
    {
        "Devices",
        "Presets"
    };


    /** Cached preset files. */
    private static class PresetFileCacheItem
    {
        IniEditor iniFile = new IniEditor (true);
        long      time;
        String [] presets;
    }


    /** Model for dynamically updating the presets. */
    private static class PresetModel extends DefaultListModel<String>
    {
        private static final long serialVersionUID = 1893811425744940327L;


        public void update ()
        {
            this.fireContentsChanged (this, 0, this.getSize () - 1);
        }
    }


    private static final Map<String, PresetFileCacheItem> presetFileCache     = new HashMap<> ();

    private BrowserContentType                            contentType         = BrowserContentType.PRESET;
    private final ICursorDevice                           cursorDevice;

    final DeviceCollectionFilterColumn                    deviceCollectionFilterColumn;
    final DeviceLocationFilterColumn                      deviceLocationFilterColumn;
    final DeviceFileTypeFilterColumn                      deviceFileTypeFilterColumn;
    final DeviceCategoryFilterColumn                      deviceCategoryFilterColumn;
    final DeviceTagsFilterColumn                          deviceTagsFilterColumn;
    final DeviceCreatorFilterColumn                       deviceCreatorFilterColumn;
    final DeviceTypeFilterColumn                          deviceTypeFilterColumn;

    private boolean                                       isBrowserActive     = false;

    private final PresetModel                             presetModel         = new PresetModel ();

    int                                                   selectedIndex;
    List<DeviceMetadataImpl>                              filteredDevices     = Collections.emptyList ();

    private final MessageSender                           sender;
    private final IBrowserColumn [] []                    columnDataContentTypes;
    private final BrowserDialog                           browserWindow;
    private int                                           insertPosition;
    private final Object                                  parsePresetFileLock = new Object ();


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param cursorDevice The cursor device
     * @param numFilterColumnEntries The number of entries in a filter column page
     * @param numResults The number of entries in a results column page
     */
    public BrowserImpl (final DataSetupEx dataSetup, final ICursorDevice cursorDevice, final int numFilterColumnEntries, final int numResults)
    {
        super (numFilterColumnEntries, numResults);

        this.cursorDevice = cursorDevice;
        this.sender = dataSetup.getSender ();

        this.deviceCollectionFilterColumn = new DeviceCollectionFilterColumn (0, numFilterColumnEntries);
        this.deviceLocationFilterColumn = new DeviceLocationFilterColumn (1, numFilterColumnEntries);
        this.deviceFileTypeFilterColumn = new DeviceFileTypeFilterColumn (2, numFilterColumnEntries);
        this.deviceCategoryFilterColumn = new DeviceCategoryFilterColumn (3, numFilterColumnEntries);
        this.deviceTagsFilterColumn = new DeviceTagsFilterColumn (4, numFilterColumnEntries);
        this.deviceCreatorFilterColumn = new DeviceCreatorFilterColumn (5, numFilterColumnEntries);
        this.deviceTypeFilterColumn = new DeviceTypeFilterColumn (6, numFilterColumnEntries);

        this.columnDataContentTypes = new IBrowserColumn [2] [];
        this.columnDataContentTypes[BrowserContentType.DEVICE.ordinal ()] = new IBrowserColumn []
        {
            this.deviceCollectionFilterColumn,
            this.deviceLocationFilterColumn,
            this.deviceFileTypeFilterColumn,
            this.deviceCategoryFilterColumn,
            this.deviceTagsFilterColumn,
            this.deviceCreatorFilterColumn,
            this.deviceTypeFilterColumn
        };
        this.columnDataContentTypes[BrowserContentType.PRESET.ordinal ()] = new IBrowserColumn []
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
        this.columnData = this.columnDataContentTypes[BrowserContentType.PRESET.ordinal ()];

        this.resultData = this.createResultData (this.numResults);

        for (final IBrowserColumn column: this.columnDataContentTypes[BrowserContentType.DEVICE.ordinal ()])
            ((BaseColumn) column).addSelectionListener ( () -> this.updateFilteredDevices (true));

        this.browserWindow = ((HostImpl) dataSetup.getHost ()).getWindowManager ().getMainFrame ().getBrowserDialog ();

        this.enableObservers (false);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.BROWSER, enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPresetContentType ()
    {
        return this.contentType == BrowserContentType.PRESET;
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


    /**
     * Get the content type object.
     *
     * @return THe content type
     */
    public BrowserContentType getContentType ()
    {
        return this.contentType;
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


    private void setContentType (final BrowserContentType contentType)
    {
        final BrowserContentType [] values = BrowserContentType.values ();
        final int id = contentType.ordinal ();
        this.contentType = values[Math.min (Math.max (0, id), values.length - 1)];
        this.columnData = this.columnDataContentTypes[id];
        this.selectedIndex = 0;

        if (contentType == BrowserContentType.DEVICE)
            this.updateFilteredDevices (false);

        SwingUtilities.invokeLater ( () -> {
            this.browserWindow.updateFilters ();
            this.browserWindow.updateResults (this.selectedIndex);
            this.browserWindow.setVisible (true);
            this.browserWindow.toFront ();
        });
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPreviewEnabled ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePreviewEnabled ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setPreviewEnabled (final boolean isEnabled)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void replace (final IItem item)
    {
        // Slot and Drum Pad not supported
        if (item instanceof final CursorDeviceImpl cdi)
        {
            final String name = item.getName ();
            this.infoText = "Replace: " + (name.length () == 0 ? "Empty" : name);

            this.browse (BrowserContentType.PRESET, cdi.getPosition ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void addDevice (final IChannel channel)
    {
        this.infoText = "Add device to: " + channel.getName ();

        this.browse (BrowserContentType.DEVICE, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void insertBeforeCursorDevice ()
    {
        this.infoText = INSERT_DEVICE_BEFORE + this.cursorDevice.getName ();

        this.browse (BrowserContentType.DEVICE, this.cursorDevice.getPosition ());
    }


    /** {@inheritDoc} */
    @Override
    public void insertAfterCursorDevice ()
    {
        this.infoText = INSERT_DEVICE_AFTER + this.cursorDevice.getName ();

        this.browse (BrowserContentType.DEVICE, this.cursorDevice.getPosition () + 1);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleInsertionPoint ()
    {
        if (this.infoText.startsWith (INSERT_DEVICE_BEFORE))
            this.insertAfterCursorDevice ();
        else
            this.insertBeforeCursorDevice ();
    }


    private void browse (final BrowserContentType contentType, final int insertPos)
    {
        this.stopBrowsing (false);

        SwingUtilities.invokeLater ( () -> this.browserWindow.open (this));

        this.enableObservers (true);
        this.insertPosition = insertPos;
        this.setContentType (contentType);
        this.isBrowserActive = true;
        this.fireActiveObserver (this.isBrowserActive);
    }


    /** {@inheritDoc} */
    @Override
    public void stopBrowsing (final boolean commitSelection)
    {
        this.browserWindow.close (false, false);

        if (!this.isBrowserActive)
            return;

        if (commitSelection && this.insertPosition >= 0)
        {
            switch (this.contentType)
            {
                case DEVICE:
                    final ResultItem result = this.getSelectedResultDevice ();
                    if (result != null)
                    {
                        final DeviceMetadataImpl device = result.getDevice ();
                        if (device != null)
                            this.sender.processStringArg (Processor.DEVICE, "add/" + this.insertPosition + "/", device.getCreationName ());
                    }
                    break;

                case PRESET:
                    final int index = this.getSelectedResultIndex ();
                    if (index != -1)
                        this.sender.processIntArg (Processor.DEVICE, "preset", index);
                    break;

                default:
                    // Not used
                    break;
            }
        }

        this.enableObservers (false);
        this.insertPosition = -1;
        this.isBrowserActive = false;
        this.fireActiveObserver (this.isBrowserActive);
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


    /**
     * Select an item in the results.
     *
     * @param index The in the items
     */
    public void setSelectedResult (final int index)
    {
        final int length = this.isPresetContentType () ? this.presetModel.getSize () : this.filteredDevices.size ();
        this.selectedIndex = Math.min (Math.max (0, index), length - 1);
        SwingUtilities.invokeLater ( () -> this.browserWindow.updateResultSelection (this.selectedIndex));
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


    private void updateFilteredDevices (final boolean alsoUpdateResults)
    {
        // There are 2 modes: presets and devices
        if (this.isPresetContentType ())
            return;

        final DeviceManager deviceManager = DeviceManager.get ();
        final DeviceCollection folder = this.deviceCollectionFilterColumn.getCursorIndex () == 0 ? null : deviceManager.getCollection (this.deviceCollectionFilterColumn.getCursorName ());
        final String category = this.deviceCategoryFilterColumn.getCursorIndex () == 0 ? null : this.deviceCategoryFilterColumn.getCursorName ();
        final DeviceFileType fileType = this.deviceFileTypeFilterColumn.getCursorIndex () == 0 ? null : DeviceFileType.valueOf (this.deviceFileTypeFilterColumn.getCursorName ().toUpperCase (Locale.US));
        final DeviceArchitecture location = this.deviceLocationFilterColumn.getCursorIndex () == 0 ? null : DeviceArchitecture.valueOf (this.deviceLocationFilterColumn.getCursorName ().toUpperCase (Locale.US));
        // Note: this.deviceTagsFilterColumn currently does nothing
        final String vendor = this.deviceCreatorFilterColumn.getCursorIndex () == 0 ? null : this.deviceCreatorFilterColumn.getCursorName ();
        final DeviceType type = this.deviceTypeFilterColumn.getCursorIndex () == 0 ? null : DeviceType.valueOf (this.deviceTypeFilterColumn.getCursorName ().toUpperCase (Locale.US).replace (' ', '_'));

        this.filteredDevices = deviceManager.filterBy (fileType, category, vendor, folder, location, type);

        if (this.selectedIndex >= this.filteredDevices.size ())
            this.selectedIndex = 0;

        if (alsoUpdateResults)
            SwingUtilities.invokeLater ( () -> this.browserWindow.updateResults (this.selectedIndex));
    }


    /**
     * Get the visible devices filtered by the currently selected filters.
     *
     * @return The filtered devices
     */
    public List<DeviceMetadataImpl> getFilteredDevices ()
    {
        return this.filteredDevices;
    }


    /** An item in the result column. */
    private class ResultItem extends ItemImpl implements IBrowserColumnItem
    {
        /**
         * Constructor.
         *
         * @param index The index of the item
         */
        public ResultItem (final int index)
        {
            super (null, index);
        }


        /** {@inheritDoc} */
        @Override
        public boolean doesExist ()
        {
            final int resultIndex = this.getIndex ();
            if (BrowserImpl.this.isPresetContentType ())
                return resultIndex < BrowserImpl.this.presetModel.getSize ();
            return resultIndex < BrowserImpl.this.filteredDevices.size ();
        }


        /** {@inheritDoc} */
        @Override
        public String getName ()
        {
            final int index = this.getIndex ();
            if (BrowserImpl.this.isPresetContentType ())
                return index < BrowserImpl.this.presetModel.getSize () ? BrowserImpl.this.presetModel.get (index) : "";
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


        public DeviceMetadataImpl getDevice ()
        {
            final int id = BrowserImpl.this.translateBankIndexToPageOfSelectedIndex (this.getIndex ());
            return id < BrowserImpl.this.filteredDevices.size () ? BrowserImpl.this.filteredDevices.get (id) : null;
        }


        /** {@inheritDoc} */
        @Override
        protected Processor getProcessor ()
        {
            // Never used
            return null;
        }
    }


    /**
     * Get all available presets for the selected device.
     *
     * @return The names of the presets
     */
    public DefaultListModel<String> getPresets ()
    {
        return this.presetModel;
    }


    /**
     * Set the file which contains the presets of the currently selected device.
     *
     * @param filename The preset filename
     */
    public void setPresetsFile (final String filename)
    {
        if (filename == null)
            return;

        synchronized (this.parsePresetFileLock)
        {
            PresetFileCacheItem presetFileCacheItem;

            try
            {
                final File file = new File (filename);
                if (!file.exists ())
                    return;

                presetFileCacheItem = presetFileCache.get (filename);
                if (presetFileCacheItem == null || presetFileCacheItem.time != file.lastModified ())
                {
                    if (presetFileCacheItem == null)
                    {
                        presetFileCacheItem = new PresetFileCacheItem ();
                        presetFileCache.put (filename, presetFileCacheItem);
                    }
                    presetFileCacheItem.iniFile.load (file.getAbsolutePath ());
                    final int presetCount = Integer.parseInt (presetFileCacheItem.iniFile.get ("General", "NbPresets"));
                    presetFileCacheItem.presets = new String [presetCount];
                    for (int i = 0; i < presetCount; i++)
                        presetFileCacheItem.presets[i] = presetFileCacheItem.iniFile.get ("Preset" + i, "Name");
                    presetFileCacheItem.time = file.lastModified ();
                }
            }
            catch (final IOException | NumberFormatException ex)
            {
                throw new FrameworkException ("Could not load file: " + filename, ex);
            }

            this.presetModel.clear ();
            this.presetModel.addAll (Arrays.asList (presetFileCacheItem.presets));
            this.presetModel.update ();

            this.selectedIndex = 0;
        }
    }
}