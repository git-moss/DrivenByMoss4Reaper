// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Base class for an item in a filter column.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Device
{
    private final String         name;
    private final String         module;
    private final DeviceFileType fileType;
    private final String         creationName;
    private String               vendor;
    private final Set<String>    categories = new HashSet<> (1);


    /**
     * Constructor.
     *
     * @param creationName The name which can trigger adding the device in Reaper
     * @param name The name of the device
     * @param module The name of the library module
     * @param type The type of the device (plugin type)
     */
    public Device (final String creationName, final String name, final String module, final DeviceFileType type)
    {
        this.creationName = creationName;
        this.name = name;
        this.module = module;
        this.fileType = type;
    }


    /**
     * Get the name of the device.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
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
     * Get the location of the device.
     *
     * @return JS or VST
     */
    public DeviceLocation getLocation ()
    {
        return this.fileType == DeviceFileType.JS ? DeviceLocation.JS : DeviceLocation.VST;
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
     * Returns true if the device has assigned the given catory.
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
        if (this.fileType == DeviceFileType.VST || this.fileType == DeviceFileType.VST3)
            return DeviceType.AUDIO_EFFECT;

        if (this.fileType == DeviceFileType.VSTI || this.fileType == DeviceFileType.VST3I)
            return DeviceType.INSTRUMENT;

        return this.hasCategory ("MIDI") ? DeviceType.MIDI_EFFECT : DeviceType.AUDIO_EFFECT;
    }
}
