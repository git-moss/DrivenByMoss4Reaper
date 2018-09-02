// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

/**
 * Implementation of a note.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteImpl
{
    private double start;
    private double end;
    private int    pitch;
    private int    velocity;


    /**
     * Constructor.
     *
     * @param start The start of the note
     * @param end The end of the note
     * @param pitch The pitch of the note
     * @param velocity The velocity of the note
     */
    public NoteImpl (final double start, final double end, final int pitch, final int velocity)
    {
        this.start = start;
        this.end = end;
        this.pitch = pitch;
        this.velocity = velocity;
    }


    /**
     * Get the start of the note.
     *
     * @return The start
     */
    public double getStart ()
    {
        return this.start;
    }


    /**
     * Get the end of the note.
     *
     * @return The end
     */
    public double getEnd ()
    {
        return this.end;
    }


    /**
     * Get the pitch of the note.
     *
     * @return The pitch
     */
    public int getPitch ()
    {
        return this.pitch;
    }


    /**
     * Get the velocity of the note.
     *
     * @return The velocity
     */
    public int getVelocity ()
    {
        return this.velocity;
    }
}
