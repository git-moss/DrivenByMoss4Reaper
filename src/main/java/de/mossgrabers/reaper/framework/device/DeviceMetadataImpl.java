// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import de.mossgrabers.framework.daw.data.IDeviceMetadata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Implementation of a metadata description of a device.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceMetadataImpl implements IDeviceMetadata
{
    private final String         name;
    private final String         module;
    private final DeviceFileType fileType;
    private DeviceType           deviceType;
    private String               vendor;
    private final Set<String>    categories = new HashSet<> (1);


    /**
     * Constructor.
     *
     * @param name The name of the device
     * @param module The name of the library module or creation ID
     * @param deviceType The type of the device
     * @param fileType The plugin type of the device
     */
    public DeviceMetadataImpl (final String name, final String module, final DeviceType deviceType, final DeviceFileType fileType)
    {
        this.name = name;
        this.module = module;
        this.deviceType = deviceType;
        this.fileType = fileType;
    }


    /** {@inheritDoc} */
    @Override
    public String name ()
    {
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public String fullName ()
    {
        return String.format ("%s (%s)", this.name, this.fileType.getName ());
    }


    /**
     * Get the display text.
     *
     * @return The text
     */
    public String getDisplayName ()
    {
        return this.name + " (" + this.fileType.getName () + ")";
    }


    /**
     * Get the name which can trigger adding the device in Reaper.
     *
     * @return The name
     */
    public String getCreationName ()
    {
        return this.module;
    }


    /**
     * Get the vendor of the device.
     *
     * @return The vendor
     */
    public String getVendor ()
    {
        return this.vendor;
    }


    /**
     * Set the vendor.
     *
     * @param vendor The vendor
     */
    public void setVendor (final String vendor)
    {
        this.vendor = vendor;
    }


    /**
     * Get the name of the library module. Note: Spaces are replaced with '_'. But '_' is also still
     * '_'!
     *
     * @return The module name
     */
    public String getModule ()
    {
        return this.module;
    }


    /**
     * Get the plugin type (e.g. VST or VSTi).
     *
     * @return The plugin type
     */
    public DeviceFileType getFileType ()
    {
        return this.fileType;
    }


    /**
     * Add the categories to the device.
     *
     * @param categories The categories
     */
    public void addCategories (final Collection<String> categories)
    {
        this.categories.addAll (categories);
    }


    /**
     * Add the category to the device.
     *
     * @param category The category
     */
    public void addCategory (final String category)
    {
        this.categories.add (category);
    }


    /**
     * Set the category for the device. Removes all other categories.
     *
     * @param category The category
     */
    public void setCategory (final String category)
    {
        this.categories.clear ();
        this.categories.add (category);
    }


    /**
     * Returns true if the device has assigned the given category.
     *
     * @param category The category to test
     * @return True if the device has the category assigned
     */
    public boolean hasCategory (final String category)
    {
        return this.categories.contains (category);
    }


    /**
     * Returns true if the device has assigned at least one category.
     *
     * @return True if the device is categorized
     */
    public boolean isCategorized ()
    {
        return !this.categories.isEmpty ();
    }


    /**
     * Get the type.
     *
     * @return The type
     */
    public DeviceType getType ()
    {
        return this.deviceType;
    }


    /**
     * Get the type.
     *
     * @param type The type
     */
    public void setType (final DeviceType type)
    {
        this.deviceType = type;
    }
}
