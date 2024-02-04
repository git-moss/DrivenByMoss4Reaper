// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

/**
 * All available device types.
 *
 * @author Jürgen Moßgraber
 */
public enum DeviceType
{
    /** An audio effect. */
    AUDIO_EFFECT("Audio Effect"),
    /** An instrument. */
    INSTRUMENT("Instrument"),
    /** A MIDI Effect. */
    MIDI_EFFECT("Midi Effect");


    private final String name;


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
