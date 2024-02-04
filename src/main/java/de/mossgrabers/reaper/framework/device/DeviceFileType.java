// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

/**
 * All available device file types.
 *
 * @author Jürgen Moßgraber
 */
public enum DeviceFileType
{
    /** A VST 2 devices. */
    VST2("VST2"),
    /** A VST 3 devices. */
    VST3("VST3"),
    /** CLAP devices. */
    CLAP("CLAP"),
    /** AU devices. */
    AU("AU"),
    /** LV2 devices. */
    // LV2("LV2"), -> not used since Reaper does not store this in a file!
    /** JS devices. */
    JS("JS");


    private final String name;


    /**
     * Constructor.
     *
     * @param name The name of the device type
     */
    private DeviceFileType (final String name)
    {
        this.name = name;
    }


    /**
     * Get the name of the device type.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }
}
