// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * Implementation for a note repeat.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatImpl implements INoteRepeat
{
    private static final String             NOTEREPEAT_TAG = "noterepeat";

    private static final ArpeggiatorMode [] ARP_MODES      =
    {
        new ArpeggiatorMode (0, "Down", "Down"),
        new ArpeggiatorMode (1, "Up", "Up"),
        new ArpeggiatorMode (2, "Down Alt", "Down Alt"),
        new ArpeggiatorMode (3, "Up Alt", "Up Alt")
    };

    private final MessageSender             sender;
    private boolean                         isNoteRepeat;
    private double                          noteRepeatPeriod;
    private double                          noteLength;
    private boolean                         usePressure;
    private ArpeggiatorMode                 mode;


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
        this.sender.processBooleanArg (NOTEREPEAT_TAG, "active", !this.isNoteRepeat);
    }


    /** {@inheritDoc} */
    @Override
    public void setActive (final boolean active)
    {
        this.sender.processBooleanArg (NOTEREPEAT_TAG, "active", active);
    }


    /** {@inheritDoc} */
    @Override
    public void setPeriod (final double period)
    {
        this.sender.processDoubleArg (NOTEREPEAT_TAG, "rate", 1.0 / period);
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
        this.sender.processDoubleArg (NOTEREPEAT_TAG, "notelength", length);
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
        this.sender.processBooleanArg (NOTEREPEAT_TAG, "velocity", !this.usePressure);
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
    public void setMode (final String mode)
    {
        for (int i = 0; i < ARP_MODES.length; i++)
        {
            if (ARP_MODES[i].getValue ().equals (mode))
            {
                this.sender.processIntArg (NOTEREPEAT_TAG, "mode", i);
                return;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode getMode ()
    {
        return this.mode;
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode [] getModes ()
    {
        return ARP_MODES;
    }


    /** {@inheritDoc} */
    @Override
    public void changeMode (final boolean increase)
    {
        final int index = this.mode.getIndex ();
        final int newIndex = Math.max (0, Math.min (ARP_MODES.length - 1, index + (increase ? 1 : -1)));
        this.setMode (ARP_MODES[newIndex].getValue ());
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
        this.mode = ARP_MODES[modeIndex >= 0 && modeIndex < ARP_MODES.length ? modeIndex : 0];
    }
}
