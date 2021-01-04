// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

/**
 * All available device types.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum DeviceType
{
    /** An audio effect. */
    AUDIO_EFFECT("Audio Effect"),
    /** An instrument. */
    INSTRUMENT("Instrument"),
    /** A Midi Effect. */
    MIDI_EFFECT("Midi Effect");


    private String name;


    /**
     * Constructor.
     *
     * @param name The name of the device type
     */
    private DeviceType (final String name)
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
