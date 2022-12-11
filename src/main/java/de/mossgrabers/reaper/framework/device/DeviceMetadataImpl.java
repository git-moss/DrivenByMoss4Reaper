// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import de.mossgrabers.framework.daw.data.IDeviceMetadata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Implementation of a metadata description of a device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceMetadataImpl implements IDeviceMetadata
{
    private final String             name;
    private final String             module;
    private final DeviceFileType     fileType;
    private final DeviceArchitecture architecture;
    private DeviceType               deviceType;
    private final String             creationName;
    private String                   vendor;
    private final Set<String>        categories = new HashSet<> (1);


    /**
     * Constructor.
     *
     * @param creationName The name which can trigger adding the device in Reaper
     * @param name The name of the device
     * @param module The name of the library module
     * @param deviceType The type of the device
     * @param fileType The plugin type of the device
     * @param architecture The architecture
     */
    public DeviceMetadataImpl (final String creationName, final String name, final String module, final DeviceType deviceType, final DeviceFileType fileType, final DeviceArchitecture architecture)
    {
        this.creationName = creationName;
        this.name = name;
        this.module = module;
        this.deviceType = deviceType;
        this.fileType = fileType;
        this.architecture = architecture;
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
        return String.format ("%s (%s - %s)", this.name, this.fileType.getName (), this.architecture);
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
        return this.creationName;
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
     * Get the architecture of the device.
     *
     * @return Script, x64, ...
     */
    public DeviceArchitecture getArchitecture ()
    {
        return this.architecture;
    }


    /**
     * Set the categories of the device.
     *
     * @param categories The categories
     */
    public void setCategories (final Collection<String> categories)
    {
        this.categories.addAll (categories);
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
