// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Interface to a clip, which contains note data.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface INoteClip extends IClip
{
    /**
     * Get the row of notes.
     *
     * @return The row of notes
     */
    int getNumRows ();


    /**
     * Get the index of the current step
     *
     * @return The index of the current step
     */
    int getCurrentStep ();


    /**
     * Set the length of a step.
     *
     * @param length The length
     */
    void setStepLength (double length);


    /**
     * Get the length of a step.
     *
     * @return The length
     */
    double getStepLength ();


    /**
     * Get the state of a note.
     *
     * @param step The step
     * @param row The row
     * @return The step info
     */
    IStepInfo getStep (int step, int row);


    /**
     * Toggle a note at a step.
     *
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     */
    void toggleStep (int step, int row, int velocity);


    /**
     * Set a note at a step.
     *
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     * @param duration The length of the note
     */
    void setStep (int step, int row, int velocity, double duration);


    /**
     * Clear a note at a step.
     *
     * @param step The step
     * @param row The note row
     */
    void clearStep (final int step, final int row);


    /**
     * If there is a note started at this position, it will update the duration of the note.
     *
     * @param step The step
     * @param row The note row
     * @param duration The new length of the note
     */
    void updateStepDuration (int step, int row, double duration);


    /**
     * If there is a note started at this position, it will change the duration of the note.
     *
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepDuration (int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the velocity of the note.
     *
     * @param step The step
     * @param row The note row
     * @param velocity The velocity of the note
     */
    void updateStepVelocity (int step, int row, double velocity);


    /**
     * If there is a note started at this position, it will change the velocity of the note.
     *
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepVelocity (int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the release velocity of the note.
     *
     * @param step The step
     * @param row The note row
     * @param releaseVelocity The release velocity of the note
     */
    void updateStepReleaseVelocity (int step, int row, double releaseVelocity);


    /**
     * If there is a note started at this position, it will change the release velocity of the note.
     *
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepReleaseVelocity (int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the pressure of the note.
     *
     * @param step The step
     * @param row The note row
     * @param pressure The pressure of the note from 0 to +1
     */
    void updateStepPressure (int step, int row, double pressure);


    /**
     * If there is a note started at this position, it will change the pressure of the note.
     *
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepPressure (int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the timbre of the note.
     *
     * @param step The step
     * @param row The note row
     * @param timbre The timbre of the note from -1 to +1
     */
    void updateStepTimbre (int step, int row, double timbre);


    /**
     * If there is a note started at this position, it will change the timbre of the note.
     *
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepTimbre (int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the panorama of the note.
     *
     * @param step The step
     * @param row The note row
     * @param panorama The panorama of the note, -1 for left, +1 for right
     */
    void updateStepPan (int step, int row, double panorama);


    /**
     * If there is a note started at this position, it will change the panorama of the note.
     *
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepPan (int step, int row, int control);


    /**
     * If there is a note started at this position, it will update the transposition of the note.
     *
     * @param step The step
     * @param row The note row
     * @param semitones The transposition of the note in semitones, from -24 to +24
     */
    void updateStepTranspose (int step, int row, double semitones);


    /**
     * If there is a note started at this position, it will change the transposition of the note.
     *
     * @param step The step
     * @param row The note row
     * @param control The change value
     */
    void changeStepTranspose (int step, int row, int control);


    /**
     * Start / stop editing the note. Signals to prevent round-trip error by quickly changing values
     * to the DAW, which are not set at the same time.
     *
     * @param step The step
     * @param row The note row
     * @param enable Enable editing
     */
    void edit (int step, int row, boolean enable);


    /**
     * Clear a row (note).
     *
     * @param row The row to clear
     */
    void clearRow (int row);


    /**
     * Does the row contain any notes?
     *
     * @param row The row
     * @return True if it contains at least one note
     */
    boolean hasRowData (int row);


    /**
     * Get the lowest row (note) which contains data.
     *
     * @return The lowest row or -1 if all rows are empty
     */
    int getLowerRowWithData ();


    /**
     * Get the highest row (note) which contains data.
     *
     * @return The highest row or -1 if all rows are empty
     */
    int getUpperRowWithData ();


    /**
     * Scroll the clip view to the given page. Depends on the number of the steps of a page.
     *
     * @param page The page to select
     */
    void scrollToPage (int page);


    /**
     * Get the edit page.
     *
     * @return The edit page
     */
    int getEditPage ();


    /**
     * Scroll the steps one page backwards.
     */
    void scrollStepsPageBackwards ();


    /**
     * Scroll the steps one page forwards.
     */
    void scrollStepsPageForward ();


    /**
     * Value that reports if the note grid steps can be scrolled backwards.
     *
     * @return True if it can be scrolled
     */
    boolean canScrollStepsBackwards ();


    /**
     * Value that reports if the note grid steps can be scrolled forwards.
     *
     * @return True if it can be scrolled
     */
    boolean canScrollStepsForwards ();
}
