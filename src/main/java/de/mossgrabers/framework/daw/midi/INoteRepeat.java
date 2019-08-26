// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Implementation for a note repeat.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteRepeat
{
    /**
     * Toggle if note repeat is active.
     *
     * @param track The track for which to toggle the note repeat
     */
    void toggleActive (ITrack track);


    /**
     * True if note repeat is enabled.
     *
     * @param track The track
     * @return True if note repeat is enabled
     */
    boolean isActive (ITrack track);


    /**
     * Set the note repeat period.
     *
     * @param track The track
     * @param period The period
     */
    void setPeriod (ITrack track, double period);


    /**
     * Get the note repeat period.
     *
     * @param track The track
     * @return The period
     */
    double getPeriod (ITrack track);


    /**
     * Get the note length.
     *
     * @param track The track
     * @param length The length
     */
    void setNoteLength (ITrack track, double length);


    /**
     * Get the note length.
     *
     * @param track The track
     * @return The length
     */
    double getNoteLength (ITrack track);


    /**
     * Check if shuffle is active.
     *
     * @param track The track for which to check the note repeat shuffle
     * @return True if active
     */
    boolean isShuffle (ITrack track);


    /**
     * Toggle if shuffle is active.
     *
     * @param track The track for which to toggle the note repeat shuffle
     */
    void toggleShuffle (ITrack track);


    /**
     * Check if use pressure as the velocity for new notes is active.
     *
     * @param track The track
     * @return True if active
     */
    boolean usePressure (ITrack track);


    /**
     * Use the pressure as the velocity for new notes.
     *
     * @param track The track for which to toggle use pressure
     */
    void toggleUsePressure (ITrack track);


    /**
     * Will accumulate for each new notes.
     *
     * @param track The track
     * @return The value is between -1 and 1
     */
    double getVelocityRamp (ITrack track);


    /**
     * Get the velocity ramp value as a formatted string.
     *
     * @param track The track
     * @return The formatted value
     */
    String getVelocityRampStr (ITrack track);


    /**
     * Will accumulate for each new notes.
     *
     * @param track The track
     * @param value The value is between -1 and 1
     */
    void setVelocityRamp (ITrack track, double value);
}
