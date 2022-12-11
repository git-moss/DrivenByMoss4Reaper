// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

/**
 * All available device architectures.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum DeviceArchitecture
{
    /** Devices based on a script. */
    SCRIPT("Script"),
    /** Intel x64 architecture. */
    X64("X64"),
    /** ARM architecture. */
    ARM("ARM");


    private String name;


    /**
     * Constructor.
     *
     * @param name The name of the device architecture
     */
    private DeviceArchitecture (final String name)
    {
        this.name = name;
    }


    /**
     * Get the name of the device architecture.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }
}
