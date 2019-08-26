// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing of note parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode
{
    INoteClip clip = null;
    int       step = 0;
    int       note = 60;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final PushControlSurface surface, final IModel model)
    {
        super ("Note", surface, model);
    }


    /**
     * Set the values.
     *
     * @param clip The clip to edit
     * @param step The step to edit
     * @param note The note to edit
     */
    public void setValues (final INoteClip clip, final int step, final int note)
    {
        this.clip = clip;
        this.step = step;
        this.note = note;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (this.surface.getDeleteTriggerId ());
            switch (index)
            {
                case 1:
                    this.clip.updateStepDuration (this.step, this.note, 1.0);
                    break;

                case 2:
                    this.clip.updateStepVelocity (this.step, this.note, 1.0);
                    break;

                case 3:
                    this.clip.updateStepReleaseVelocity (this.step, this.note, 1.0);
                    break;

                case 4:
                    this.clip.updateStepPressure (this.step, this.note, 0);
                    break;

                case 5:
                    this.clip.updateStepTimbre (this.step, this.note, 0);
                    break;

                case 6:
                    this.clip.updateStepPan (this.step, this.note, 0);
                    break;

                case 7:
                    this.clip.updateStepTranspose (this.step, this.note, 0);
                    break;

                default:
                    return;
            }
            return;
        }

        this.clip.edit (this.step, this.note, isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        switch (index)
        {
            case 1:
                this.clip.changeStepDuration (this.step, this.note, value);
                break;

            case 2:
                this.clip.changeStepVelocity (this.step, this.note, value);
                break;

            case 3:
                this.clip.changeStepReleaseVelocity (this.step, this.note, value);
                break;

            case 4:
                this.clip.changeStepPressure (this.step, this.note, value);
                break;

            case 5:
                this.clip.changeStepTimbre (this.step, this.note, value);
                break;

            case 6:
                this.clip.changeStepPan (this.step, this.note, value);
                break;

            case 7:
                this.clip.changeStepTranspose (this.step, this.note, value);
                break;

            default:
                return;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final IStepInfo stepInfo = this.clip.getStep (this.step, this.note);
        final double noteVelocity = stepInfo.getVelocity ();
        final double noteReleaseVelocity = stepInfo.getReleaseVelocity ();
        final double notePressure = stepInfo.getPressure ();
        final double noteTimbre = stepInfo.getTimbre ();
        final double notePan = stepInfo.getPan ();
        final double noteTranspose = stepInfo.getTranspose ();

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final int parameterValue = valueChanger.fromNormalizedValue (noteVelocity);
        final int parameterReleaseValue = valueChanger.fromNormalizedValue (noteReleaseVelocity);
        final int parameterPressureValue = valueChanger.fromNormalizedValue (notePressure);
        final int parameterTimbreValue = valueChanger.fromNormalizedValue ((noteTimbre + 1.0) / 2.0);
        final int parameterPanValue = valueChanger.fromNormalizedValue ((notePan + 1.0) / 2.0);
        final int parameterTransposeValue = valueChanger.fromNormalizedValue ((noteTranspose + 24.0) / 48.0);

        display.setCell (0, 1, "Length").setCell (1, 1, this.formatLength (stepInfo.getDuration ()));
        display.setCell (0, 2, "Velocity").setCell (1, 2, formatPercentage (noteVelocity)).setCell (2, 2, parameterValue, Format.FORMAT_VALUE);
        display.setCell (0, 3, "R-Velocity").setCell (1, 3, formatPercentage (noteReleaseVelocity)).setCell (2, 3, parameterReleaseValue, Format.FORMAT_VALUE);
        display.setCell (0, 4, "Pressure").setCell (1, 4, formatPercentage (notePressure)).setCell (2, 4, parameterPressureValue, Format.FORMAT_VALUE);
        display.setCell (0, 5, "Timbre").setCell (1, 5, formatPercentage (noteTimbre)).setCell (2, 5, parameterTimbreValue, Format.FORMAT_VALUE);
        display.setCell (0, 6, "Pan").setCell (1, 6, formatPercentage (notePan)).setCell (2, 6, parameterPanValue, Format.FORMAT_PAN);
        display.setCell (0, 7, "Pitch").setCell (1, 7, String.format ("%.1f", Double.valueOf (noteTranspose))).setCell (2, 7, parameterTransposeValue, Format.FORMAT_PAN);

        display.setCell (3, 0, "Step: " + (this.step + 1));
        display.setCell (3, 1, "Note: " + Scales.formatNoteAndOctave (this.note, -3));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final IStepInfo stepInfo = this.clip.getStep (this.step, this.note);
        final double noteVelocity = stepInfo.getVelocity ();
        final double noteReleaseVelocity = stepInfo.getReleaseVelocity ();
        final double notePressure = stepInfo.getPressure ();
        final double noteTimbre = stepInfo.getTimbre ();
        final double notePan = stepInfo.getPan ();
        final double noteTranspose = stepInfo.getTranspose ();

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final int parameterValue = valueChanger.fromNormalizedValue (noteVelocity);
        final int parameterReleaseValue = valueChanger.fromNormalizedValue (noteReleaseVelocity);
        final int parameterPressureValue = valueChanger.fromNormalizedValue (notePressure);
        final int parameterTimbreValue = valueChanger.fromNormalizedValue ((noteTimbre + 1.0) / 2.0);
        final int parameterPanValue = valueChanger.fromNormalizedValue ((notePan + 1.0) / 2.0);
        final int parameterTransposeValue = valueChanger.fromNormalizedValue ((noteTranspose + 24.0) / 48.0);

        display.addOptionElement ("Step: " + (this.step + 1), "", false, "Note: " + Scales.formatNoteAndOctave (this.note, -3), "", false, false);

        display.addParameterElement ("Length", -1, this.formatLength (stepInfo.getDuration ()), this.isKnobTouched[1], -1);
        display.addParameterElement ("Velocity", parameterValue, formatPercentage (noteVelocity), this.isKnobTouched[2], parameterValue);
        display.addParameterElement ("R-Velocity", parameterReleaseValue, formatPercentage (noteReleaseVelocity), this.isKnobTouched[3], parameterReleaseValue);
        display.addParameterElement ("Pressure", parameterPressureValue, formatPercentage (notePressure), this.isKnobTouched[4], parameterPressureValue);
        display.addParameterElement ("Timbre", parameterTimbreValue, formatPercentage (noteTimbre), this.isKnobTouched[5], parameterTimbreValue);
        display.addParameterElement ("Pan", parameterPanValue, formatPercentage (notePan), this.isKnobTouched[6], parameterPanValue);
        display.addParameterElement ("Pitch", parameterTransposeValue, String.format ("%.1f", Double.valueOf (noteTranspose)), this.isKnobTouched[7], parameterTransposeValue);
    }


    /**
     * Format a velocity percentage.
     *
     * @param noteVelocity The velocity in the range of 0..1.
     * @return The formatted velocity
     */
    private static String formatPercentage (final double noteVelocity)
    {
        return String.format ("%.01f%%", Double.valueOf (noteVelocity * 100.0));
    }


    /**
     * Format the duration of the current note.
     *
     * @param duration The note duration
     * @return The formatted value
     */
    private String formatLength (final double duration)
    {
        return StringUtils.formatMeasuresLong (this.model.getTransport ().getQuartersPerMeasure (), duration, 0);
    }
}