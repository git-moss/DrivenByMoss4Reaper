// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.NoteOccurrenceType;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.GridStep;
import de.mossgrabers.reaper.communication.Processor;

import java.util.ArrayList;
import java.util.List;


/**
 * Proxy to the Bitwig Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipImpl extends BaseImpl implements INoteClip
{
    private static final String       PATH_NOTE    = "note/";
    private static final StepInfoImpl EMPTY_STEP   = new StepInfoImpl ();

    private boolean                   exists       = false;
    private double                    clipStart    = -1;
    private double                    clipEnd      = -1;
    private boolean                   isLooped     = false;
    private ColorEx                   color;
    private double                    playPosition = -1;
    private final int                 numSteps;
    private final int                 numRows;
    private double                    stepLength;
    private final List<Note>          notes        = new ArrayList<> ();
    private final StepInfoImpl [] []  data;
    private int                       editPage     = 0;
    private int                       maxPage      = 1;
    private final List<GridStep>      editSteps    = new ArrayList<> ();


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
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.CLIP, enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.exists;
    }


    /**
     * Set the exists state.
     *
     * @param exists True if a selected clip exists
     */
    public void setExistsValue (final boolean exists)
    {
        this.exists = exists;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        final int [] rgb = color.toIntRGB255 ();
        this.sendOSC ("color", "RGB(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");
    }


    /**
     * Set clip color value.
     *
     * @param color Array with 3 elements: red, green, blue (0..1)
     */
    public void setColorValue (final double [] color)
    {
        this.color = new ColorEx (color);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
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
        this.sendOSC ("start", this.clipStart);
        this.updateNoteData ();
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayStart (final int control, final boolean slow)
    {
        if (this.clipStart == -1)
            return;

        final boolean increase = this.valueChanger.isIncrease (control);
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.setPlayStart (increase ? this.clipStart + frac : Math.max (this.clipStart - frac, 0.0));
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
        this.sendOSC ("end", this.clipEnd);
        this.updateNoteData ();
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayEnd (final int control, final boolean slow)
    {
        if (this.clipEnd == -1)
            return;

        final boolean increase = this.valueChanger.isIncrease (control);
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.setPlayEnd (increase ? this.clipEnd + frac : Math.max (this.clipEnd - frac, 0.0));
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
    public void changeLoopStart (final int control, final boolean slow)
    {
        this.changePlayStart (control, slow);
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
    public void changeLoopLength (final int control, final boolean slow)
    {
        this.changePlayEnd (control, slow);
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
        this.sendOSC ("loop", enable);
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
    public void changeAccent (final int control, final boolean slow)
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
    public StepInfoImpl getStep (final int channel, final int step, final int row)
    {
        synchronized (this.notes)
        {
            if (step < 0 || row < 0 || step >= this.data.length || row >= this.data[step].length)
                return EMPTY_STEP;
            return this.data[step][row];
        }
    }


    /** {@inheritDoc} */
    @Override
    public void toggleStep (final int channel, final int step, final int row, final int velocity)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendOSC (PATH_NOTE + row + "/toggle", pos + " " + this.stepLength + " " + velocity + " " + channel + " 0");
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int channel, final int step, final int row, final IStepInfo noteStep)
    {
        this.setStep (channel, step, row, (int) (noteStep.getVelocity () * 127.0), noteStep.getDuration ());
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int channel, final int step, final int row, final int velocity, final double duration)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendOSC (PATH_NOTE + row + "/set", pos + " " + duration + " " + velocity + " " + channel + " 0");
    }


    /**
     * Update the values of an existing note.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     * @param duration The length of the note
     * @param isMuted True if the note is muted
     */
    public void updateStep (final int channel, final int step, final int row, final int velocity, final double duration, final boolean isMuted)
    {
        final double pos = (step + this.editPage * this.numSteps) * this.stepLength;
        this.sendOSC (PATH_NOTE + row + "/update", pos + " " + duration + " " + velocity + " " + channel + " " + (isMuted ? "1" : "0"));
    }


    /** {@inheritDoc} */
    @Override
    public void changeMuteState (final int channel, final int step, final int row, final int control)
    {
        this.updateMuteState (channel, step, row, this.valueChanger.isIncrease (control));
    }


    /** {@inheritDoc} */
    @Override
    public void updateMuteState (final int channel, final int step, final int row, final boolean isMuted)
    {
        final StepInfoImpl stepInfo = this.getStep (channel, step, row);
        if (stepInfo.getState () == StepState.OFF)
            return;

        stepInfo.setMuted (isMuted);
        if (this.editSteps.isEmpty ())
            return;

        final double velocity = stepInfo.getVelocity ();
        this.updateStep (channel, step, row, (int) (velocity * 127), stepInfo.getDuration (), isMuted);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepDuration (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final boolean increase = this.valueChanger.isIncrease (control);
        final double res = Resolution.RES_1_32.getValue ();
        this.updateStepDuration (channel, step, row, Math.max (0, info.getDuration () + (increase ? res : -res)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepDuration (final int channel, final int step, final int row, final double duration)
    {
        // Duration of 0 fully deletes the note
        if (duration == 0)
            return;

        final StepInfoImpl stepInfo = this.getStep (channel, step, row);
        if (stepInfo.getState () == StepState.OFF)
            return;

        stepInfo.setDuration (duration);
        if (this.editSteps.isEmpty ())
            return;

        final double velocity = stepInfo.getVelocity ();
        this.updateStep (channel, step, row, (int) (velocity * 127), duration, stepInfo.isMuted ());
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepVelocity (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocity = info.getVelocity () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateStepVelocity (channel, step, row, Math.min (1.0, Math.max (0, velocity)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepVelocity (final int channel, final int step, final int row, final double velocity)
    {
        final int midiVelocity = (int) (velocity * 127);

        // Velocity of 0 fully deletes the note
        if (midiVelocity == 0)
            return;

        final StepInfoImpl stepInfo = this.getStep (channel, step, row);
        if (stepInfo.getState () == StepState.OFF)
            return;

        stepInfo.setVelocity (velocity);
        if (this.editSteps.isEmpty ())
            return;

        final double duration = stepInfo.getDuration ();
        this.updateStep (channel, step, row, midiVelocity, duration, stepInfo.isMuted ());
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepReleaseVelocity (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepReleaseVelocity (final int channel, final int step, final int row, final double releaseVelocity)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPressure (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPressure (final int channel, final int step, final int row, final double pressure)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTimbre (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTimbre (final int channel, final int step, final int row, final double timbre)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPan (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPan (final int channel, final int step, final int row, final double panorama)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTranspose (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTranspose (final int channel, final int step, final int row, final double semitones)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepGain (final int channel, final int step, final int row, final double gain)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepGain (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateVelocitySpread (final int channel, final int step, final int row, final double velocitySpread)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void changeVelocitySpread (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateIsChanceEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateChance (final int channel, final int step, final int row, final double chance)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void changeChance (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateIsOccurrenceEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void setPrevNextOccurrence (final int channel, final int step, final int row, final boolean increase)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void setOccurrence (final int channel, final int step, final int row, final NoteOccurrenceType occurrence)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateIsRecurrenceEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateRecurrenceLength (final int channel, final int step, final int row, final int recurrenceLength)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateRecurrenceMask (final int channel, final int step, final int row, final int mask)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void changeRecurrenceLength (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateIsRepeatEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateRepeatCount (final int channel, final int step, final int row, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void changeRepeatCount (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateRepeatCurve (final int channel, final int step, final int row, final double value)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void changeRepeatCurve (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateRepeatVelocityCurve (final int channel, final int step, final int row, final double value)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void changeRepeatVelocityCurve (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void updateRepeatVelocityEnd (final int channel, final int step, final int row, final double value)
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void changeRepeatVelocityEnd (final int channel, final int step, final int row, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void startEdit (final List<GridStep> editSteps)
    {
        // Is there a previous edit, which is not stopped yet?
        this.stopEdit ();

        this.editSteps.addAll (editSteps);
        for (final GridStep step: this.editSteps)
            this.delayedUpdate (step);
    }


    /** {@inheritDoc} */
    @Override
    public void stopEdit ()
    {
        if (this.editSteps.isEmpty ())
            return;

        for (final GridStep editStep: this.editSteps)
            this.sendClipData (editStep.getChannel (), editStep.getStep (), editStep.getNote ());
        this.editSteps.clear ();

        this.updateNoteData ();
    }


    private void delayedUpdate (final GridStep editStep)
    {
        if (this.editSteps.isEmpty ())
            return;
        final int channel = editStep.getChannel ();
        final int step = editStep.getStep ();
        final int note = editStep.getNote ();
        this.sendClipData (channel, step, note);
        this.host.scheduleTask ( () -> this.delayedUpdate (new GridStep (channel, step, note)), 100);
    }


    /**
     * Update the locally changed step data in Reaper.
     *
     * @param channel The MIDI channel
     * @param step The step of the clip
     * @param row The row of the clip
     */
    private void sendClipData (final int channel, final int step, final int row)
    {
        final IStepInfo stepInfo = this.data[step][row];
        final double velocity = stepInfo.getVelocity ();
        this.updateStep (channel, step, row, (int) (velocity * 127), stepInfo.getDuration (), stepInfo.isMuted ());
    }


    /** {@inheritDoc} */
    @Override
    public void clearAll ()
    {
        this.sendOSC ("clear");
    }


    /** {@inheritDoc} */
    @Override
    public void clearStep (final int channel, final int step, final int row)
    {
        this.sendOSC (PATH_NOTE + row + "/clear/" + channel, (step + this.editPage * this.numSteps) * this.stepLength);
    }


    /** {@inheritDoc} */
    @Override
    public void clearRow (final int channel, final int row)
    {
        this.sendOSC (PATH_NOTE + row + "/clear/" + channel);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRowData (final int channel, final int row)
    {
        synchronized (this.notes)
        {
            for (int step = 0; step < this.numSteps; step++)
            {
                if (this.data[step][row].getState () != StepState.OFF)
                    return true;
            }
            return false;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getLowerRowWithData ()
    {
        int min = 128;
        for (int channel = 0; channel < 16; channel++)
        {
            final int lower = this.getLowerRowWithData (channel);
            if (lower >= 0 && lower < min)
                min = lower;
        }
        return min == 128 ? -1 : min;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperRowWithData ()
    {
        int max = -1;
        for (int channel = 0; channel < 16; channel++)
        {
            final int upper = this.getUpperRowWithData (channel);
            if (upper >= 0 && upper > max)
                max = upper;
        }
        return max;
    }


    /** {@inheritDoc} */
    @Override
    public int getLowerRowWithData (final int channel)
    {
        for (int row = 0; row < this.numRows; row++)
            if (this.hasRowData (channel, row))
                return row;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperRowWithData (final int channel)
    {
        for (int row = this.numRows - 1; row >= 0; row--)
            if (this.hasRowData (channel, row))
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
        this.sendOSC ("duplicate");
    }


    /** {@inheritDoc} */
    @Override
    public void duplicateContent ()
    {
        this.sendOSC ("duplicateContent");
    }


    /** {@inheritDoc} */
    @Override
    public void quantize (final double amount)
    {
        this.sender.processDoubleArg (Processor.QUANTIZE, amount);
    }


    /** {@inheritDoc} */
    @Override
    public void transpose (final int semitones)
    {
        this.sendOSC ("transpose", semitones);
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
                {
                    final StepInfoImpl stepInfo = this.data[step][row];
                    if (this.editSteps.isEmpty ())
                        stepInfo.setState (StepState.OFF);
                }
            }
            this.notes.forEach (this::updateNote);
            this.calcPages ();
        }
    }


    private void updateNote (final Note note)
    {
        // Is the note on the current page window?
        final int row = note.getPitch ();
        if (row < 0 || row >= this.numRows)
            return;

        final int step = (int) Math.floor (note.getStart () / this.stepLength);
        final int pageOffset = this.editPage * this.numSteps;
        final int relToPage = step - pageOffset;
        if (relToPage < 0 || relToPage >= this.numSteps)
            return;

        final StepInfoImpl stepInfo = this.data[relToPage][row];
        if (!this.editSteps.isEmpty ())
            return;

        stepInfo.setMuted (note.isMuted ());
        stepInfo.setState (StepState.START);
        stepInfo.setDuration (note.getEnd () - note.getStart ());
        stepInfo.setVelocity (note.getVelocity () / 127.0);

        // Extend longer notes
        final int endStep = Math.min ((int) Math.floor (note.getEnd () / this.stepLength) - pageOffset, this.numSteps);
        for (int i = relToPage + 1; i < endStep; i++)
            this.data[i][row].setState (StepState.CONTINUE);
    }


    /** {@inheritDoc}} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.CLIP;
    }


    /** {@inheritDoc}} */
    @Override
    public boolean isPinned ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc}} */
    @Override
    public void togglePinned ()
    {
        // Not supported
    }


    /** {@inheritDoc}} */
    @Override
    public void setPinned (final boolean isPinned)
    {
        // Not supported
    }
}