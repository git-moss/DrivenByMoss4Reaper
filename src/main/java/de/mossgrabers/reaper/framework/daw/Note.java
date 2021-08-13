// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
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
    private final boolean isMuted;
    private final double  start;
    private final double  end;
    private final int     pitch;
    private final int     velocity;


    /**
     * Constructor.
     *
     * @param isMuted True if muted
     * @param start The start of the note
     * @param end The end of the note
     * @param channel The MIDI channel
     * @param pitch The pitch of the note
     * @param velocity The velocity of the note
     */
    public Note (final boolean isMuted, final double start, final double end, final int channel, final int pitch, final int velocity)
    {
        this.isMuted = isMuted;
        this.start = start;
        this.end = end;
        this.pitch = pitch;
        this.velocity = velocity;
    }


    /**
     * Check if the note is muted.
     *
     * @return True if muted
     */
    public boolean isMuted ()
    {
        return this.isMuted;
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
                final boolean isMuted = Integer.parseInt (noteParts[0]) > 0;
                final double start = Double.parseDouble (noteParts[1]);
                final double end = Double.parseDouble (noteParts[2]);
                final int channel = Integer.parseInt (noteParts[3]);
                final int pitch = Integer.parseInt (noteParts[4]);
                final int velocity = Integer.parseInt (noteParts[5]);
                notes.add (new Note (isMuted, start, end, channel, pitch, velocity));
            }
        }
        return notes;
    }
}
