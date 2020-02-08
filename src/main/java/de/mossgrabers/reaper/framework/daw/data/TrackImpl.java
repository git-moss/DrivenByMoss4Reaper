// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.reaper.framework.daw.AbstractTrackBankImpl;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.SceneBankImpl;
import de.mossgrabers.reaper.framework.daw.SlotBankImpl;
import de.mossgrabers.reaper.framework.daw.TrackBankImpl;

import java.util.EnumMap;
import java.util.Map;


/**
 * The data of a track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackImpl extends ChannelImpl implements ITrack
{
    /** Record monitoring is off. */
    public static final int                              MONITOR_OFF      = 0;
    /** Record monitoring is on. */
    public static final int                              MONITOR_ON       = 1;
    /** Record monitoring is automatic. */
    public static final int                              MONITOR_AUTO     = 2;

    /** Automation write is trim. */
    public static final String                           AUTOMATION_TRIM  = "trim";
    /** Automation write is read. */
    public static final String                           AUTOMATION_READ  = "read";
    /** Automation write is touch. */
    public static final String                           AUTOMATION_TOUCH = "touch";
    /** Automation write is latch. */
    public static final String                           AUTOMATION_LATCH = "latch";
    /** Automation write is write. */
    public static final String                           AUTOMATION_WRITE = "write";

    private static final Map<RecordQuantization, Double> QUANT_MAP        = new EnumMap<> (RecordQuantization.class);
    static
    {
        QUANT_MAP.put (RecordQuantization.RES_OFF, Double.valueOf (0));
        QUANT_MAP.put (RecordQuantization.RES_1_32, Double.valueOf (0.125));
        QUANT_MAP.put (RecordQuantization.RES_1_16, Double.valueOf (0.25));
        QUANT_MAP.put (RecordQuantization.RES_1_8, Double.valueOf (0.5));
        QUANT_MAP.put (RecordQuantization.RES_1_4, Double.valueOf (1));
    }

    protected final AbstractTrackBankImpl trackBank;

    private boolean                       isRecArm;
    private boolean                       monitor;
    private boolean                       autoMonitor;
    private String                        automation = AUTOMATION_TRIM;
    private final ISlotBank               slotBank;
    private int                           depth;
    private boolean                       recordQuantizationNoteLength;
    private RecordQuantization            recordQuantization;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param trackBank The track bank for folder navigation
     * @param index The index of the track in the page
     * @param numTracks The number of tracks of a bank
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     */
    public TrackImpl (final DataSetupEx dataSetup, final AbstractTrackBankImpl trackBank, final int index, final int numTracks, final int numSends, final int numScenes)
    {
        super (dataSetup, index, numSends);

        this.trackBank = trackBank;
        this.slotBank = new SlotBankImpl (dataSetup, (SceneBankImpl) trackBank.getSceneBank (), index, numScenes);
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
    public boolean hasParent ()
    {
        return this.trackBank.hasParent ();
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
    public boolean isRecordQuantizationNoteLength ()
    {
        return this.recordQuantizationNoteLength;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecordQuantizationNoteLength ()
    {
        // Not supported
    }


    /**
     * Dis-/enable the record quantization length.
     *
     * @param isEnabled True to enable
     */
    public void setRecordQuantizationNoteLengthState (final boolean isEnabled)
    {
        this.recordQuantizationNoteLength = isEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public RecordQuantization getRecordQuantizationGrid ()
    {
        return this.recordQuantization;
    }


    /** {@inheritDoc} */
    @Override
    public void setRecordQuantizationGrid (final RecordQuantization recordQuantization)
    {
        this.setRecordQuantizationGridState (recordQuantization);
        this.sendTrackOSC ("inQuantResolution", QUANT_MAP.get (recordQuantization).doubleValue ());
    }


    /**
     * Set the record quantization grid resolution. If the resolution is not supported the closest
     * resolution is selected.
     *
     * @param resolutionValue The value to set
     */
    public void setRecordQuantizationGrid (final double resolutionValue)
    {
        double diff = 1;
        RecordQuantization result = RecordQuantization.RES_OFF;
        for (final RecordQuantization rq: RecordQuantization.values ())
        {
            final double newDiff = Math.abs (QUANT_MAP.get (rq).doubleValue () - resolutionValue);
            if (newDiff < diff)
            {
                result = rq;
                diff = newDiff;
            }
        }
        this.setRecordQuantizationGridState (result);
    }


    /**
     * Set the record quantization grid.
     *
     * @param recordQuantization The value to set
     */
    public void setRecordQuantizationGridState (final RecordQuantization recordQuantization)
    {
        this.recordQuantization = recordQuantization;
    }


    /** {@inheritDoc} */
    @Override
    public ISlotBank getSlotBank ()
    {
        return this.slotBank;
    }


    /** {@inheritDoc} */
    @Override
    public void createClip (final int slotIndex, final int lengthInBeats)
    {
        this.sendTrackOSC ("createClip", lengthInBeats);
    }


    /**
     * Start recording a clip on the track at the current play position.
     */
    public void recordClip ()
    {
        this.sendTrackOSC ("recordClip");
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
