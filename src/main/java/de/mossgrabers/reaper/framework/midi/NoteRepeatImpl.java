// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * Implementation for a note repeat.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatImpl implements INoteRepeat
{
    private final MessageSender sender;
    private boolean             isNoteRepeat;
    private double              noteRepeatPeriod;


    /**
     * Constructor.
     *
     * @param sender The sender
     */
    public NoteRepeatImpl (final MessageSender sender)
    {
        this.sender = sender;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActive ()
    {
        return this.isNoteRepeat;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleActive ()
    {
        this.sender.processBooleanArg ("noterepeat", "active", !this.isNoteRepeat);
    }


    /** {@inheritDoc} */
    @Override
    public void setPeriod (final double length)
    {
        this.sender.processDoubleArg ("noterepeat", "length", length);
    }


    /** {@inheritDoc} */
    @Override
    public double getPeriod ()
    {
        return this.noteRepeatPeriod;
    }


    /**
     * Set if repeat is enabled.
     *
     * @param enable True if enabled
     */
    public void setInternalActive (final boolean enable)
    {
        this.isNoteRepeat = enable;
    }


    /**
     * Set the note length for note repeat.
     *
     * @param length The length
     */
    public void setInternalPeriod (final double length)
    {
        this.noteRepeatPeriod = length;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteLength (final double length)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public double getNoteLength ()
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShuffle ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleShuffle ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean usePressure ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleUsePressure ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getOctaves ()
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setOctaves (final int octaves)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public String getMode ()
    {
        // Not supported
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public void setMode (final String mode)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFreeRunning ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsFreeRunning ()
    {
        // Not supported
    }
}
