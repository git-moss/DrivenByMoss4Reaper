// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.NoteObserver;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.AbstractTrackBankImpl;
import de.mossgrabers.reaper.framework.daw.SlotBankImpl;
import de.mossgrabers.reaper.framework.daw.TrackBankImpl;


/**
 * The data of a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackImpl extends ChannelImpl implements ITrack
{
    /** Record monitoring is off. */
    public static final int             MONITOR_OFF      = 0;
    /** Record monitoring is on. */
    public static final int             MONITOR_ON       = 1;
    /** Record monitoring is automatic. */
    public static final int             MONITOR_AUTO     = 2;

    /** Automation write is trim. */
    public static final String          AUTOMATION_TRIM  = "trim";
    /** Automation write is read. */
    public static final String          AUTOMATION_READ  = "read";
    /** Automation write is touch. */
    public static final String          AUTOMATION_TOUCH = "touch";
    /** Automation write is latch. */
    public static final String          AUTOMATION_LATCH = "latch";
    /** Automation write is write. */
    public static final String          AUTOMATION_WRITE = "write";

    private final AbstractTrackBankImpl trackBank;
    private boolean                     isRecArm;
    private boolean                     monitor;
    private boolean                     autoMonitor;
    private String                      automation       = AUTOMATION_TRIM;
    private final ISlotBank             slotBank;
    private boolean                     isNoteRepeat;
    private double                      noteRepeatLength;
    private int                         depth;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param trackBank The track bank for folder navigation
     * @param valueChanger The value changer
     * @param index The index of the track in the page
     * @param numTracks The number of tracks of a bank
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public TrackImpl (final IHost host, final MessageSender sender, final AbstractTrackBankImpl trackBank, final IValueChanger valueChanger, final int index, final int numTracks, final int numSends, final int numScenes)
    {
        super (host, sender, valueChanger, index, numSends);
        this.trackBank = trackBank;
        this.slotBank = new SlotBankImpl (host, sender, valueChanger, index, numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        if (!(this.trackBank instanceof TrackBankImpl))
            return;

        // Only group tracks can be entered
        if (!this.isGroup ())
            return;

        // If this track is already the cursor track, enter it straight away
        if (this.isSelected ())
        {
            ((TrackBankImpl) this.trackBank).enterCurrentFolder ();
            return;
        }

        // Make the track cursor track
        this.select ();
        // Delay the child selection a bit to ensure the track is selected
        this.host.scheduleTask (((TrackBankImpl) this.trackBank)::enterCurrentFolder, 100);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGroup ()
    {
        return this.getType () == ChannelType.GROUP;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecArm ()
    {
        return this.doesExist () && this.isRecArm;
    }


    /** {@inheritDoc} */
    @Override
    public void setRecArm (final boolean value)
    {
        this.setRecArmState (value);
        this.sendTrackOSC ("recarm", value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        this.setRecArm (!this.isRecArm ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMonitor ()
    {
        return this.doesExist () && this.monitor;
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        this.setMonitorState (value);
        this.sendTrackOSC ("monitor", this.monitor ? 1 : 0);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        this.setMonitor (!this.monitor);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoMonitor ()
    {
        return this.doesExist () && this.autoMonitor;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleNoteRepeat ()
    {
        this.sendTrackOSC ("noterepeat", this.isNoteRepeat);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNoteRepeat ()
    {
        return this.isNoteRepeat;
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteRepeatLength (final double length)
    {
        this.sendTrackOSC ("noterepeatlength", length);
    }


    /** {@inheritDoc} */
    @Override
    public double getNoteRepeatLength ()
    {
        return this.noteRepeatLength;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldNotes ()
    {
        // In Reaper you can throw everything on a track
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        this.setAutoMonitorState (value);
        this.sendTrackOSC ("autoMonitor", this.autoMonitor);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        this.setAutoMonitor (!this.autoMonitor);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldAudioData ()
    {
        // In Reaper you can throw everything on a track
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        if (this.doesExist ())
            this.sendTrackOSC ("select", 1);
    }


    /**
     * Set recording arm state.
     *
     * @param isRecArm The state
     */
    public void setRecArmState (final boolean isRecArm)
    {
        this.isRecArm = isRecArm;
    }


    /**
     * Set recording monitoring state.
     *
     * @param monitorState The state
     */
    public void setMonitorState (final boolean monitorState)
    {
        this.monitor = monitorState;
    }


    /**
     * Set recording monitoring state.
     *
     * @param autoMonitorState The state
     */
    public void setAutoMonitorState (final boolean autoMonitorState)
    {
        this.autoMonitor = autoMonitorState;
    }


    /**
     * Set the automation write type.
     *
     * @param automation The type
     */
    public void setAutomation (final String automation)
    {
        this.automation = automation;
    }


    /**
     * Get the automation write type.
     *
     * @return The automation type
     */
    public String getAutomation ()
    {
        return this.automation;
    }


    /**
     * Set if repeat is enabled.
     *
     * @param enable True if enabled
     */
    public void setInternalNoteRepeat (final boolean enable)
    {
        this.isNoteRepeat = enable;
    }


    /**
     * Set the note length for note repeat.
     *
     * @param length The length
     */
    public void setInternalNoteRepeatLength (final double length)
    {
        this.noteRepeatLength = length;
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfadeModeAsNumber (final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public String getCrossfadeMode ()
    {
        // Not supported
        return "AB";
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeMode (final String mode)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfadeModeAsNumber ()
    {
        // Not supported
        return 1;
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeModeAsNumber (final int modeValue)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCrossfadeMode ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public ISlotBank getSlotBank ()
    {
        return this.slotBank;
    }


    /** {@inheritDoc} */
    @Override
    public void addNoteObserver (final NoteObserver observer)
    {
        // Monitoring played notes from the DAW is not supported
    }


    /**
     * Set the 'depth' of the track.
     *
     * @param depth The level of the track if nested in folders
     */
    public void setDepth (final int depth)
    {
        this.depth = depth;
    }


    /**
     * Get the depth of the track.
     *
     * @return The depth
     */
    public int getDepth ()
    {
        return this.depth;
    }
}
