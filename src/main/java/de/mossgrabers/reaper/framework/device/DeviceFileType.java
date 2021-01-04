// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

/**
 * All available device file types.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum DeviceFileType
{
    /** A VST 2 instrument. */
    VSTI("VSTi"),
    /** A VST 3 instrument. */
    VST3I("VST3i"),
    /** A VST 2 effect. */
    VST("VST"),
    /** A VST 3 effect. */
    VST3("VST3"),
    /** AU effects. */
    AU("AU"),
    /** AU instruments. */
    AUI("AUi"),
    /** JS devices. */
    JS("JS");


    private String name;


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
