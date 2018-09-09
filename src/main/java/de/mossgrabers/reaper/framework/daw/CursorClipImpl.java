// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.reaper.communication.MessageSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Proxy to the Bitwig Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipImpl extends BaseImpl implements ICursorClip
{
    private IValueChanger   valueChanger;
    private double          clipStart    = -1;
    private double          clipEnd      = -1;
    private double          playPosition = -1;
    private final double [] color        = new double []
    {
        0,
        0,
        0
    };
    private int             numSteps;
    private int             numRows;
    private double          stepLength;
    private List<NoteImpl>  notes        = new ArrayList<> ();
    private final int [] [] data;
    private int             editPage     = 0;
    private int             maxPage      = 1;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numSteps The number of steps of the clip to monitor
     * @param numRows The number of note rows of the clip to monitor
     */
    public CursorClipImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numSteps, final int numRows)
    {
        super (host, sender);

        this.valueChanger = valueChanger;

        this.numSteps = numSteps;
        this.numRows = numRows;
        this.stepLength = 1.0 / 4.0; // 16th
        this.data = new int [this.numSteps] [];
        for (int step = 0; step < this.numSteps; step++)
        {
            this.data[step] = new int [this.numRows];
            Arrays.fill (this.data[step], 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.sendClipOSC ("color", "RGB(" + Math.round (red * 255) + "," + Math.round (green * 255) + "," + Math.round (blue * 255) + ")");
    }


    /**
     * Set clip color value.
     *
     * @param color Array with 3 elements: red, green, blue (0..1)
     */
    public void setColorValue (final double [] color)
    {
        this.color[0] = color[0];
        this.color[1] = color[1];
        this.color[2] = color[2];
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return this.color;
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayStart ()
    {
        return this.clipStart;
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayStart (final double start)
    {
        this.clipStart = start;
        this.updateNoteData ();
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayStart (final int control)
    {
        if (this.clipStart == -1)
            return;
        this.setPlayStart (Math.max (0, this.clipStart + this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1)));
        this.sendClipOSC ("start", Double.valueOf (this.clipStart));
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayEnd ()
    {
        return this.clipEnd;
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayEnd (final double end)
    {
        this.clipEnd = end;
        this.updateNoteData ();
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayEnd (final int control)
    {
        if (this.clipEnd == -1)
            return;
        final double speed = this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1);
        this.setPlayEnd (Math.max (0, this.clipEnd + speed));
        this.sendClipOSC ("end", Double.valueOf (this.clipEnd));
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayRange (final double start, final double end)
    {
        this.setPlayStart (start);
        this.setPlayEnd (end);
        this.sendClipOSC ("start", Double.valueOf (this.clipStart));
        this.sendClipOSC ("end", Double.valueOf (this.clipEnd));
    }


    /**
     * Calculate the number of available pages.
     */
    private void calcPages ()
    {
        final double length = this.clipEnd - this.clipStart;
        final double pageLength = this.numSteps * this.stepLength;
        this.maxPage = (int) Math.ceil (length / pageLength);
        // Make sure the page is inside the new range
        this.editPage = Math.max (0, Math.min (this.editPage, this.maxPage - 1));
    }


    /** {@inheritDoc} */
    @Override
    public double getLoopStart ()
    {
        return 0.0;
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopStart (final double start)
    {
        this.setPlayStart (start);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopStart (final int control)
    {
        this.changePlayStart (control);
    }


    /** {@inheritDoc} */
    @Override
    public double getLoopLength ()
    {
        return this.getPlayEnd () - this.getPlayStart ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopLength (final int length)
    {
        this.setPlayEnd (this.getPlayStart () + length);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopLength (final int control)
    {
        this.changePlayEnd (control);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLoopEnabled ()
    {
        // No looping of clips in Reaper
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopEnabled (final boolean enable)
    {
        // No looping of clips in Reaper
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShuffleEnabled ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setShuffleEnabled (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public String getFormattedAccent ()
    {
        return Math.round (this.getAccent () * 10000) / 100 + "%";
    }


    /** {@inheritDoc} */
    @Override
    public double getAccent ()
    {
        // Not supported
        return 1;
    }


    /** {@inheritDoc} */
    @Override
    public void resetAccent ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeAccent (final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSteps ()
    {
        return this.numSteps;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumRows ()
    {
        return this.numRows;
    }


    /** {@inheritDoc} */
    @Override
    public int getCurrentStep ()
    {
        if (this.playPosition == -1)
            return -1;

        // Should never happen but who knows...
        final double offset = this.playPosition - this.clipStart;
        if (offset < 0)
            return -1;
        return (int) Math.floor (offset / this.stepLength);
    }


    /** {@inheritDoc} */
    @Override
    public int getStep (final int step, final int row)
    {
        return row < 0 ? 0 : this.data[step][row];
    }


    /** {@inheritDoc} */
    @Override
    public void toggleStep (final int step, final int row, final int velocity)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendClipOSC ("note/" + row + "/toggle", pos + " " + this.stepLength + " " + velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int step, final int row, final int velocity, final double duration)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendClipOSC ("note/" + row + "/set", pos + " " + this.stepLength + " " + velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void clearStep (final int step, final int row)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendClipOSC ("note/" + row + "/clear", Double.valueOf (pos));
    }


    /** {@inheritDoc} */
    @Override
    public void clearRow (final int row)
    {
        this.sendClipOSC ("note/" + row + "/clear", null);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRowData (final int row)
    {
        for (int step = 0; step < this.numSteps; step++)
            if (this.data[step][row] > 0)
                return true;
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getLowerRowWithData ()
    {
        for (int row = 0; row < this.numRows; row++)
            if (this.hasRowData (row))
                return row;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperRowWithData ()
    {
        for (int row = this.numRows - 1; row >= 0; row--)
            if (this.hasRowData (row))
                return row;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setStepLength (final double length)
    {
        this.stepLength = length;
        this.updateNoteData ();
    }


    /** {@inheritDoc} */
    @Override
    public double getStepLength ()
    {
        return this.stepLength;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int step, final int row)
    {
        // Only paged scrolling supported
        this.scrollToPage (step / this.numSteps);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToPage (final int page)
    {
        this.editPage = Math.max (0, Math.min (page, this.maxPage - 1));
        this.updateNoteData ();
    }


    /** {@inheritDoc} */
    @Override
    public int getEditPage ()
    {
        return this.editPage;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollStepsPageBackwards ()
    {
        this.scrollToPage (this.editPage - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollStepsPageForward ()
    {
        this.scrollToPage (this.editPage + 1);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsBackwards ()
    {
        return this.getEditPage () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsForwards ()
    {
        return this.editPage < this.maxPage - 1;
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.sendClipOSC ("duplicate", null);
    }


    /** {@inheritDoc} */
    @Override
    public void duplicateContent ()
    {
        this.sendClipOSC ("duplicateContent", null);
    }


    /** {@inheritDoc} */
    @Override
    public void quantize (final double amount)
    {
        this.sender.sendOSC ("/quantize", null);
    }


    /** {@inheritDoc} */
    @Override
    public void transpose (final int semitones)
    {
        this.sendClipOSC ("transpose", Integer.valueOf (semitones));
    }


    /**
     * Set the play position. If the play cursor is playing a part of the cursor clip.
     *
     * @param playPosition The play position or -1 if the play cursor is not playing a part of the
     *            clip
     */
    public void setPlayPosition (final double playPosition)
    {
        this.playPosition = playPosition;
    }


    /**
     * Update all notes of the clip.
     *
     * @param notes Notes array
     */
    public void setNotes (final List<NoteImpl> notes)
    {
        synchronized (this.notes)
        {
            this.notes.clear ();
            this.notes.addAll (notes);
        }
        this.updateNoteData ();
    }


    private void updateNoteData ()
    {
        // Clear the data array
        for (int row = 0; row < this.numRows; row++)
        {
            for (int step = 0; step < this.numSteps; step++)
                this.data[step][row] = 0;
        }

        synchronized (this.notes)
        {
            for (final NoteImpl note: this.notes)
            {
                // Is the note on the current page window?
                final int row = note.getPitch ();
                if (row < 0 || row >= this.numRows)
                    continue;
                final int step = (int) Math.floor (note.getStart () / this.stepLength);
                final int pageOffset = this.editPage * this.numSteps;
                final int relToPage = step - pageOffset;
                if (relToPage < 0 || relToPage >= this.numSteps)
                    continue;

                // 0: not set, 1: note continues playing, 2: start of note
                this.data[relToPage][row] = 2;
                final int endStep = Math.min ((int) Math.floor (note.getEnd () / this.stepLength) - pageOffset, this.numSteps);
                for (int i = relToPage + 1; i < endStep; i++)
                    this.data[i][row] = 1;
            }

            this.calcPages ();
        }
    }


    protected void sendClipOSC (final String command, final Object value)
    {
        this.sender.sendOSC ("/clip/" + command, value);
    }
}