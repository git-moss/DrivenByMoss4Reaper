// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.bank.AbstractTrackBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.SceneBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.SlotBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.TrackBankImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;

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
    private AutomationMode                automationMode = AutomationMode.TRIM_READ;
    private final ISlotBank               slotBank;
    private int                           depth;
    private boolean                       recordQuantizationNoteLength;
    private RecordQuantization            recordQuantization;
    private final IParameter              crossfadeParameter;


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
        this.crossfadeParameter = new CrossfadeParameter (this.valueChanger, index);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        final CursorTrackImpl cursorTrack = this.dataSetup.getCursorTrack ();
        if (cursorTrack.isPinned ())
            return this == cursorTrack.getPinnedTrack ();
        return super.isSelected ();
    }


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param trackBank The track bank for folder navigation
     * @param index The index of the track in the page
     * @param numTracks The number of tracks of a bank
     * @param numSends The number of sends of a bank
     * @param numScenes The number of scenes of a bank
     * @param volumeParameter The volume parameter
     * @param panParameter The panorama parameter
     */
    public TrackImpl (final DataSetupEx dataSetup, final AbstractTrackBankImpl trackBank, final int index, final int numTracks, final int numSends, final int numScenes, final ParameterImpl volumeParameter, final ParameterImpl panParameter)
    {
        super (dataSetup, index, numSends, volumeParameter, panParameter);

        this.trackBank = trackBank;
        this.slotBank = new SlotBankImpl (dataSetup, (SceneBankImpl) trackBank.getSceneBank (), index, numScenes);
        this.crossfadeParameter = new CrossfadeParameter (this.valueChanger, index);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        this.sendPositionedItemOSC ("name", name);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getCrossfadeParameter ()
    {
        return this.crossfadeParameter;
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Only group tracks can be entered
        if (this.trackBank instanceof final TrackBankImpl tbi && this.isGroup ())
        {
            // If this track is already the cursor track, enter it straight away
            if (this.isSelected ())
            {
                tbi.enterCurrentFolder ();
                return;
            }

            // Make the track cursor track
            this.select ();
            // Delay the child selection a bit to ensure the track is selected
            this.host.scheduleTask (((TrackBankImpl) this.trackBank)::enterCurrentFolder, 100);
        }
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
        this.sendPositionedItemOSC ("recarm", value);
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
        this.sendPositionedItemOSC ("monitor", this.monitor ? 1 : 0);
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
        this.sendPositionedItemOSC ("autoMonitor", this.autoMonitor);
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
            this.sendPositionedItemOSC ("select", 1);
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
     * @param automationMode The type
     */
    public void setAutomation (final AutomationMode automationMode)
    {
        this.automationMode = automationMode;
    }


    /**
     * Get the automation write type.
     *
     * @return The automation type
     */
    public AutomationMode getAutomation ()
    {
        return this.automationMode;
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
        this.sendPositionedItemOSC ("inQuantResolution", QUANT_MAP.get (recordQuantization).doubleValue ());
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
        this.sendPositionedItemOSC ("createClip", lengthInBeats);
    }


    /** {@inheritDoc} */
    @Override
    public void addEqualizerDevice ()
    {
        this.sender.processNoArg (Processor.DEVICE, "eq-add");
    }


    /**
     * Start recording a clip on the track at the current play position.
     */
    public void recordClip ()
    {
        this.sendPositionedItemOSC ("recordClip");
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
