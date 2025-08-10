// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.communication.Processor;

import java.util.List;


/**
 * Implementation for a note repeat.
 *
 * @author Jürgen Moßgraber
 */
public class NoteRepeatImpl implements INoteRepeat
{
    /** The available arpeggiator modes. */
    public static final List<ArpeggiatorMode> ARP_MODES    = List.of (ArpeggiatorMode.DOWN, ArpeggiatorMode.UP, ArpeggiatorMode.DOWN_UP, ArpeggiatorMode.UP_DOWN);

    private final BackendExchange               sender;
    private boolean                           isNoteRepeat = false;
    private double                            noteRepeatPeriod;
    private double                            noteLength;
    private boolean                           usePressure;
    private ArpeggiatorMode                   mode;


    /**
     * Constructor.
     *
     * @param sender The sender
     */
    public NoteRepeatImpl (final BackendExchange sender)
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
        this.sender.processBooleanArg (Processor.NOTEREPEAT, "active", !this.isNoteRepeat);
    }


    /** {@inheritDoc} */
    @Override
    public void setActive (final boolean active)
    {
        if (this.isNoteRepeat != active)
        {
            this.isNoteRepeat = active;
            this.sender.processBooleanArg (Processor.NOTEREPEAT, "active", active);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setPeriod (final double period)
    {
        this.sender.processDoubleArg (Processor.NOTEREPEAT, "rate", 1.0 / period);
    }


    /** {@inheritDoc} */
    @Override
    public double getPeriod ()
    {
        return this.noteRepeatPeriod;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteLength (final double length)
    {
        this.sender.processDoubleArg (Processor.NOTEREPEAT, "notelength", length);
    }


    /** {@inheritDoc} */
    @Override
    public double getNoteLength ()
    {
        return this.noteLength;
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
        return this.usePressure;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleUsePressure ()
    {
        this.sender.processBooleanArg (Processor.NOTEREPEAT, "velocity", !this.usePressure);
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
    public void setMode (final ArpeggiatorMode mode)
    {
        final int index = ARP_MODES.indexOf (mode);
        if (index >= 0)
            this.sender.processIntArg (Processor.NOTEREPEAT, "mode", index);
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode getMode ()
    {
        return this.mode;
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


    /** {@inheritDoc} */
    @Override
    public void toggleLatchActive ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLatchActive ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setLatchActive (final boolean active)
    {
        // Not supported
    }


    /**
     * Set the period for note repeat.
     *
     * @param period The period
     */
    public void setInternalPeriod (final double period)
    {
        this.noteRepeatPeriod = period;
    }


    /**
     * Set the note length for note repeat.
     *
     * @param length The length
     */
    public void setInternalNoteLength (final double length)
    {
        this.noteLength = length;
    }


    /**
     * Set the use pressure / velocity for note repeat.
     *
     * @param usePressure True to enable to use velocity
     */
    public void setInternalUsePressure (final boolean usePressure)
    {
        this.usePressure = usePressure;
    }


    /**
     * Set the arpeggiator mode.
     *
     * @param modeIndex The index of the mode
     */
    public void setInternalMode (final int modeIndex)
    {
        this.mode = ARP_MODES.get (modeIndex >= 0 && modeIndex < ARP_MODES.size () ? modeIndex : 0);
    }
}
