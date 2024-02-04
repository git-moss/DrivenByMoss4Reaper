// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of a note.
 *
 * @author Jürgen Moßgraber
 */
public class Note
{
    private final boolean isSelected;
    private final boolean isMuted;
    private final double  start;
    private final double  end;
    private final int     channel;
    private final int     pitch;
    private final int     velocity;


    /**
     * Constructor.
     *
     * @param isSelected True if selected
     * @param isMuted True if muted
     * @param start The start of the note
     * @param end The end of the note
     * @param channel The MIDI channel
     * @param pitch The pitch of the note
     * @param velocity The velocity of the note
     */
    public Note (final boolean isSelected, final boolean isMuted, final double start, final double end, final int channel, final int pitch, final int velocity)
    {
        this.isSelected = isSelected;
        this.isMuted = isMuted;
        this.start = start;
        this.end = end;
        this.channel = channel;
        this.pitch = pitch;
        this.velocity = velocity;
    }


    /**
     * Check if the note is selected.
     *
     * @return True if muted
     */
    public boolean isSelected ()
    {
        return this.isSelected;
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
     * Get the MIDI channel of the note.
     *
     * @return The channel
     */
    public int getChannel ()
    {
        return this.channel;
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
                final boolean isSelected = Integer.parseInt (noteParts[0]) > 0;
                final boolean isMuted = Integer.parseInt (noteParts[1]) > 0;
                final double start = Double.parseDouble (noteParts[2]);
                final double end = Double.parseDouble (noteParts[3]);
                final int channel = Integer.parseInt (noteParts[4]);
                final int pitch = Integer.parseInt (noteParts[5]);
                final int velocity = Integer.parseInt (noteParts[6]);
                notes.add (new Note (isSelected, isMuted, start, end, channel, pitch, velocity));
            }
        }
        return notes;
    }
}
