// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Proxy to the Bitwig Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipImpl extends BaseImpl implements INoteClip
{
    private static final String      PATH_NOTE    = "note/";

    private double                   clipStart    = -1;
    private double                   clipEnd      = -1;
    private boolean                  isLooped     = false;
    private double                   red          = 0;
    private double                   green        = 0;
    private double                   blue         = 0;
    private double                   playPosition = -1;
    private int                      numSteps;
    private int                      numRows;
    private double                   stepLength;
    private List<Note>               notes        = new ArrayList<> ();
    private final StepInfoImpl [] [] data;
    private int                      editPage     = 0;
    private int                      maxPage      = 1;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numSteps The number of steps of the clip to monitor
     * @param numRows The number of note rows of the clip to monitor
     */
    public CursorClipImpl (final DataSetupEx dataSetup, final int numSteps, final int numRows)
    {
        super (dataSetup);

        this.numSteps = numSteps;
        this.numRows = numRows;
        this.stepLength = 1.0 / 4.0; // 16th
        this.data = new StepInfoImpl [this.numSteps] [];
        for (int step = 0; step < this.numSteps; step++)
        {
            this.data[step] = new StepInfoImpl [this.numRows];
            for (int row = 0; row < this.numRows; row++)
                this.data[step][row] = new StepInfoImpl ();
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
        this.red = color[0];
        this.green = color[1];
        this.blue = color[2];
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return new double []
        {
            this.red,
            this.green,
            this.blue
        };
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
        this.sendClipOSC ("start", this.clipStart);
        this.updateNoteData ();
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayStart (final int control)
    {
        if (this.clipStart == -1)
            return;
        this.setPlayStart (Math.max (0, this.clipStart + this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1)));
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
        this.sendClipOSC ("end", this.clipEnd);
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
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayRange (final double start, final double end)
    {
        final double playStart = this.getPlayStart ();
        this.setPlayStart (playStart + start);
        this.setPlayEnd (playStart + end);
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
        this.setPlayStart (this.getPlayStart () + start);
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
        return this.isLooped;
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopEnabled (final boolean enable)
    {
        this.sendClipOSC ("loop", enable);
    }


    /**
     * Set the loop enabled state.
     *
     * @param isLoopEnabled True to enable
     */
    public void setLoopEnabledState (final boolean isLoopEnabled)
    {
        this.isLooped = isLoopEnabled;
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
    public IStepInfo getStep (final int step, final int row)
    {
        synchronized (this.notes)
        {
            return this.data[step][row];
        }
    }


    /** {@inheritDoc} */
    @Override
    public void toggleStep (final int step, final int row, final int velocity)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendClipOSC (PATH_NOTE + row + "/toggle", pos + " " + this.stepLength + " " + velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int step, final int row, final int velocity, final double duration)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendClipOSC (PATH_NOTE + row + "/set", pos + " " + this.stepLength + " " + velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepDuration (final int step, final int row, final double duration)
    {
        // TODO Implement
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepVelocity (final int step, final int row, final double velocity)
    {
        // TODO Implement
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepDuration (final int step, final int row, final int control)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void changeStepVelocity (final int step, final int row, final int control)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void updateStepReleaseVelocity (final int step, final int row, final double releaseVelocity)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void changeStepReleaseVelocity (final int step, final int row, final int control)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPressure (final int step, final int row, final double pressure)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPressure (final int step, final int row, final int control)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTimbre (final int step, final int row, final double timbre)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTimbre (final int step, final int row, final int control)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPan (final int step, final int row, final double panorama)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPan (final int step, final int row, final int control)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTranspose (final int step, final int row, final double semitones)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTranspose (final int step, final int row, final int control)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void edit (final int step, final int row, final boolean enable)
    {
        // TODO Auto-generated method stub

    }


    /** {@inheritDoc} */
    @Override
    public void clearStep (final int step, final int row)
    {
        this.sendClipOSC (PATH_NOTE + row + "/clear", (step + this.editPage * this.numSteps) * this.stepLength);
    }


    /** {@inheritDoc} */
    @Override
    public void clearRow (final int row)
    {
        this.sendClipOSC (PATH_NOTE + row + "/clear");
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRowData (final int row)
    {
        synchronized (this.notes)
        {
            for (int step = 0; step < this.numSteps; step++)
            {
                if (this.data[step][row].getState () > 0)
                    return true;
            }
            return false;
        }
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
        this.sendClipOSC ("duplicate");
    }


    /** {@inheritDoc} */
    @Override
    public void duplicateContent ()
    {
        this.sendClipOSC ("duplicateContent");
    }


    /** {@inheritDoc} */
    @Override
    public void quantize (final double amount)
    {
        this.sender.processDoubleArg ("quantize", amount);
    }


    /** {@inheritDoc} */
    @Override
    public void transpose (final int semitones)
    {
        this.sendClipOSC ("transpose", semitones);
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
     * Set the play start (only stores the value).
     *
     * @param start The play start
     */
    public void setPlayStartIntern (final double start)
    {
        this.clipStart = start;
        this.updateNoteData ();
    }


    /**
     * Set the play end (only stores the value).
     *
     * @param end The play end
     */
    public void setPlayEndIntern (final double end)
    {
        this.clipEnd = end;
        this.updateNoteData ();
    }


    /**
     * Update all notes of the clip.
     *
     * @param notes Notes array
     */
    public void setNotes (final List<Note> notes)
    {
        synchronized (this.notes)
        {
            this.notes.clear ();
            this.notes.addAll (notes);
            this.updateNoteData ();
        }
    }


    private void updateNoteData ()
    {
        synchronized (this.notes)
        {
            // Clear the data array
            for (int row = 0; row < this.numRows; row++)
            {
                for (int step = 0; step < this.numSteps; step++)
                    this.data[step][row].setState (0);
            }

            for (final Note note: this.notes)
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
                this.data[relToPage][row].setState (2);
                final int endStep = Math.min ((int) Math.floor (note.getEnd () / this.stepLength) - pageOffset, this.numSteps);
                for (int i = relToPage + 1; i < endStep; i++)
                    this.data[i][row].setState (1);
            }

            this.calcPages ();
        }
    }


    protected void sendClipOSC (final String command)
    {
        this.sender.processNoArg ("clip", command);
    }


    protected void sendClipOSC (final String command, final int value)
    {
        this.sender.processIntArg ("clip", command, value);
    }


    protected void sendClipOSC (final String command, final double value)
    {
        this.sender.processDoubleArg ("clip", command, value);
    }


    protected void sendClipOSC (final String command, final String value)
    {
        this.sender.processStringArg ("clip", command, value);
    }


    protected void sendClipOSC (final String command, final boolean value)
    {
        this.sender.processBooleanArg ("clip", command, value);
    }
}