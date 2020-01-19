// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of a note.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Note
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
     * @param channel The MIDI channel
     * @param pitch The pitch of the note
     * @param velocity The velocity of the note
     */
    public Note (final double start, final double end, final int channel, final int pitch, final int velocity)
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


    /**
     * Parses notes from a string.
     *
     * @param notesStr Formatted like start1:end1:pitch1:velocity1;...;startN:endN:pitchN:velocityN;
     * @return The parsed notes
     */
    public static List<Note> parseNotes (final String notesStr)
    {
        final List<Note> notes = new ArrayList<> ();
        if (notesStr != null)
        {
            for (final String part: notesStr.trim ().split (";"))
            {
                final String [] noteParts = part.split (":");
                notes.add (new Note (Double.parseDouble (noteParts[0]), Double.parseDouble (noteParts[1]), Integer.parseInt (noteParts[2]), Integer.parseInt (noteParts[3]), Integer.parseInt (noteParts[4])));
            }
        }
        return notes;
    }
}
