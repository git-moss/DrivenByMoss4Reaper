// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.LaunchQuantization;
import de.mossgrabers.framework.daw.constants.PostRecordingAction;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.MetronomeVolumeParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.TempoParameterImpl;

import java.text.DecimalFormat;
import java.util.Optional;


/**
 * Encapsulates the Transport instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportImpl extends BaseImpl implements ITransport
{
    private static final int                   PUNCH_OFF          = 0;
    private static final int                   PUNCH_ITEMS        = 1;
    private static final int                   PUNCH_LOOP         = 2;

    private static final Object                UPDATE_LOCK        = new Object ();

    private final IModel                       model;

    private double                             position           = 0;            // Time
    private String                             positionStr        = "";
    private String                             beatsStr           = "";
    private double                             loopStart          = 0;
    private double                             loopLength         = 0;
    private String                             loopStartBeatsStr  = "";
    private String                             loopLengthBeatsStr = "";
    private String                             loopStartStr       = "";
    private String                             loopLengthStr      = "";

    private boolean                            isMetronomeOn      = false;
    private boolean                            isPlaying          = false;
    private boolean                            isRecording        = false;
    private boolean                            isLooping          = false;

    private int                                numerator          = 4;
    private int                                denominator        = 4;
    private boolean                            prerollMetronome   = false;
    private int                                prerollMeasures    = 2;

    private int                                punchMode          = PUNCH_OFF;
    private final MetronomeVolumeParameterImpl metronomeVolumeParameter;
    private final TempoParameterImpl           tempoParameter;


    /**
     * Constructor
     *
     * @param dataSetup Some configuration variables
     * @param model The DAW model
     */
    public TransportImpl (final DataSetupEx dataSetup, final IModel model)
    {
        super (dataSetup);

        this.model = model;

        this.metronomeVolumeParameter = new MetronomeVolumeParameterImpl (dataSetup);
        this.tempoParameter = new TempoParameterImpl (dataSetup);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.TRANSPORT, enable);
    }


    /** {@inheritDoc} */
    @Override
    public void play ()
    {
        if (this.isPlaying && this.isRecording)
            this.sender.processNoArg (Processor.RECORD);
        this.sender.processNoArg (Processor.PLAY);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return this.isPlaying;
    }


    /** {@inheritDoc} */
    @Override
    public void restart ()
    {
        if (this.isPlaying)
            this.stop ();
        this.play ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        if (this.isPlaying && this.isRecording)
            this.sender.processNoArg (Processor.RECORD);
        this.sender.processNoArg (Processor.STOP);
    }


    /** {@inheritDoc} */
    @Override
    public void stopAndRewind ()
    {
        this.stop ();
        this.sender.processIntArg (Processor.TIME, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void startRecording ()
    {
        this.sender.processNoArg (Processor.RECORD);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecording ()
    {
        return this.isRecording;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isArrangerOverdub ()
    {
        return this.isLauncherOverdub ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleOverdub ()
    {
        this.toggleLauncherOverdub ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLauncherOverdub ()
    {
        final Optional<ITrack> selectedTrackOpt = this.model.getCurrentTrackBank ().getSelectedItem ();
        return selectedTrackOpt.isPresent () && selectedTrackOpt.get () instanceof final TrackImpl trackImpl && trackImpl.isOverdub ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLauncherOverdub (final boolean on)
    {
        final Optional<ITrack> selectedTrackOpt = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (!selectedTrackOpt.isEmpty ())
            this.sender.processBooleanArg (Processor.TRACK, selectedTrackOpt.get ().getPosition () + "/overdub", on);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLauncherOverdub ()
    {
        this.setLauncherOverdub (!this.isLauncherOverdub ());
    }


    /**
     * Set the metronome state.
     *
     * @param on True to enable
     */
    public void setMetronomeState (final boolean on)
    {
        this.isMetronomeOn = on;
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronome (final boolean on)
    {
        this.invokeAction (on ? Actions.ENABLE_METRONOME : Actions.DISABLE_METRONOME);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMetronomeOn ()
    {
        return this.isMetronomeOn;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMetronome ()
    {
        this.invokeAction (Actions.TOGGLE_METRONOME);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMetronomeTicksOn ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMetronomeTicks ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeTicks (final boolean on)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getMetronomeVolumeParameter ()
    {
        return this.metronomeVolumeParameter;
    }


    /** {@inheritDoc} */
    @Override
    public String getMetronomeVolumeStr ()
    {
        return this.metronomeVolumeParameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeMetronomeVolume (final int control)
    {
        this.metronomeVolumeParameter.inc (this.valueChanger.isIncrease (control) ? 1 : -1);
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeVolume (final int value)
    {
        this.metronomeVolumeParameter.setValue (value);
    }


    /**
     * Set the value.
     *
     * @param metronomeVolume The value normalized to 0..1
     */
    public void setInternalMetronomeVolume (final double metronomeVolume)
    {
        this.metronomeVolumeParameter.setInternalMetronomeVolume (metronomeVolume);
    }


    /** {@inheritDoc} */
    @Override
    public int getMetronomeVolume ()
    {
        return this.metronomeVolumeParameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPrerollMetronomeEnabled ()
    {
        return this.prerollMetronome;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePrerollMetronome ()
    {
        // Toggle recording pre-roll
        this.invokeAction (Actions.RECORD_PREROLL);
    }


    /**
     * Disable/Enable the preroll click.
     *
     * @param enable True to enable
     */
    public void setPrerollMetronomeInternal (final boolean enable)
    {
        this.prerollMetronome = enable;
    }


    /** {@inheritDoc} */
    @Override
    public int getPrerollMeasures ()
    {
        return this.prerollMeasures;
    }


    /** {@inheritDoc} */
    @Override
    public void setPrerollMeasures (final int measures)
    {
        this.prerollMeasures = measures;
        this.sender.processIntArg (Processor.INIFILE, "reaper/prerollmeas", measures);
    }


    /**
     * Set the preroll measures.
     *
     * @param measures The preroll measures
     */
    public void setPrerollMeasuresInternal (final int measures)
    {
        this.prerollMeasures = measures;
        if (this.prerollMeasures < 0 || this.prerollMeasures > 4)
            this.prerollMeasures = 2;
    }


    /** {@inheritDoc} */
    @Override
    public void setLoop (final boolean on)
    {
        if (on && !this.isLooping || !on && this.isLooping)
            this.sender.processIntArg (Processor.REPEAT, 1);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLoop ()
    {
        this.sender.processIntArg (Processor.REPEAT, 1);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLoop ()
    {
        return this.isLooping;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWritingClipLauncherAutomation ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWritingArrangerAutomation ()
    {
        final AutomationMode automationWriteMode = this.getAutomationWriteMode ();
        return automationWriteMode != AutomationMode.READ && automationWriteMode != AutomationMode.TRIM_READ;
    }


    /** {@inheritDoc} */
    @Override
    public AutomationMode [] getAutomationWriteModes ()
    {
        return AutomationMode.values ();
    }


    /** {@inheritDoc} */
    @Override
    public AutomationMode getAutomationWriteMode ()
    {
        // Get from selected track
        final Optional<ITrack> selectedTrackOpt = this.model.getCurrentTrackBank ().getSelectedItem ();
        final TrackImpl selectedTrack;
        if (selectedTrackOpt.isEmpty ())
            selectedTrack = (TrackImpl) this.model.getMasterTrack ();
        else
            selectedTrack = (TrackImpl) selectedTrackOpt.get ();
        return selectedTrack.getAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutomationWriteMode (final AutomationMode mode)
    {
        final Optional<ITrack> selectedTrackOpt = this.model.getCurrentTrackBank ().getSelectedItem ();
        final String modeName = "auto" + mode.getIdentifier ();
        if (selectedTrackOpt.isEmpty ())
            this.sender.processIntArg (Processor.MASTER, modeName, 1);
        else
            this.sender.processIntArg (Processor.TRACK, selectedTrackOpt.get ().getPosition () + "/" + modeName, 1);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWriteArrangerAutomation ()
    {
        if (this.isWritingArrangerAutomation ())
            this.setAutomationWriteMode (AutomationMode.TRIM_READ);
        else
            this.setAutomationWriteMode (AutomationMode.WRITE);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWriteClipLauncherAutomation ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetAutomationOverrides ()
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
    public String getPositionText ()
    {
        return this.positionStr;
    }


    /** {@inheritDoc} */
    @Override
    public String getBeatText ()
    {
        return this.beatsStr;
    }


    /**
     * Set the text for the playback position (time).
     *
     * @param positionStr The position as a string
     */
    public void setPositionText (final String positionStr)
    {
        this.positionStr = reformatTime (positionStr);
    }


    /**
     * Set the text for the playback position (beats).
     *
     * @param beats The position in beats
     */
    public void setBeats (final String beats)
    {
        this.beatsStr = reformatBeats (beats, 1);
    }


    /**
     * Set the position value.
     *
     * @param time The position in seconds
     */
    public void setPositionValue (final double time)
    {
        this.position = time;
    }


    /** {@inheritDoc} */
    @Override
    public void setPosition (final double time)
    {
        synchronized (UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates (Processor.TRANSPORT);
            this.position = time;
            this.sender.processDoubleArg (Processor.TIME, this.position);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setPositionToEnd ()
    {
        this.invokeAction (40043);
    }


    /** {@inheritDoc} */
    @Override
    public void changePosition (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.setPosition (increase ? this.position + frac : Math.max (this.position - frac, 0.0));
    }


    /**
     * Set the loop start value.
     *
     * @param time The loop start in seconds
     */
    public void setLoopStartValue (final double time)
    {
        this.loopStart = time;
    }


    /**
     * Set the text for the loop start (time).
     *
     * @param time The loop start in seconds
     */
    public void setLoopStartText (final String time)
    {
        this.loopStartStr = reformatTime (time);
    }


    /**
     * Get the text for the loop start (time).
     *
     * @return The loop start in seconds
     */
    public String getLoopStartStr ()
    {
        return this.loopStartStr;
    }


    /**
     * Set the text for the loop start (beats).
     *
     * @param beats The loop start in beats
     */
    public void setLoopStartBeatText (final String beats)
    {
        this.loopStartBeatsStr = reformatBeats (beats, 1);
    }


    /** {@inheritDoc} */
    @Override
    public String getLoopStartBeatText ()
    {
        return this.loopStartBeatsStr;
    }


    /**
     * Set the arranger loop start.
     *
     * @param time The loop start to set
     */
    public void setLoopStart (final double time)
    {
        synchronized (UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates (Processor.TRANSPORT);
            this.loopStart = time;
            this.sender.processDoubleArg (Processor.TIME, "loop/start", this.loopStart);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopStart (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.setLoopStart (increase ? this.loopStart + frac : Math.max (this.loopStart - frac, 0.0));
    }


    /** {@inheritDoc} */
    @Override
    public void selectLoopStart ()
    {
        this.setPosition (this.loopStart);
    }


    /** {@inheritDoc} */
    @Override
    public void selectLoopEnd ()
    {
        this.setPosition (this.loopLength);
    }


    /**
     * Set the text for the loop length (time).
     *
     * @param time The loop length in seconds
     */
    public void setLoopLengthText (final String time)
    {
        this.loopLengthStr = reformatTime (time);
    }


    /**
     * Get the text for the loop length (time).
     *
     * @return The loop length in seconds
     */
    public String getLoopLengthStr ()
    {
        return this.loopLengthStr;
    }


    /**
     * Set the loop length value.
     *
     * @param time The loop length in seconds
     */
    public void setLoopLengthValue (final double time)
    {
        this.loopLength = time;
    }


    /**
     * Set the text for the loop length (beats).
     *
     * @param beats The loop length in beats
     */
    public void setLoopLengthBeatText (final String beats)
    {
        this.loopLengthBeatsStr = reformatBeats (beats, 0);
    }


    /** {@inheritDoc} */
    @Override
    public String getLoopLengthBeatText ()
    {
        return this.loopLengthBeatsStr;
    }


    /**
     * Set the arranger loop length.
     *
     * @param time The loop length to set
     */
    public void setLoopLength (final double time)
    {
        synchronized (UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates (Processor.TRANSPORT);
            this.loopLength = time;
            this.sender.processDoubleArg (Processor.TIME, "loop/length", this.loopLength);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopLength (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.setLoopLength (increase ? this.loopLength + frac : Math.max (this.loopLength - frac, 0.0));
    }


    /** {@inheritDoc} */
    @Override
    public void togglePunchIn ()
    {
        this.setPunchIn (!this.isPunchInEnabled ());
    }


    /** {@inheritDoc} */
    @Override
    public void setPunchIn (final boolean enable)
    {
        if (enable)
        {
            this.invokeAction (Actions.RECORD_MODE_AUTO_PUNCH);
            this.punchMode = PUNCH_LOOP;
        }
        else
        {
            this.invokeAction (Actions.RECORD_MODE_NORMAL);
            this.punchMode = PUNCH_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPunchInEnabled ()
    {
        return this.punchMode != PUNCH_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePunchOut ()
    {
        this.setPunchOut (!this.isPunchOutEnabled ());
    }


    /** {@inheritDoc} */
    @Override
    public void setPunchOut (final boolean enable)
    {
        if (enable)
        {
            this.invokeAction (Actions.RECORD_MODE_PUNCH_ITEMS);
            this.punchMode = PUNCH_ITEMS;
        }
        else
        {
            this.invokeAction (Actions.RECORD_MODE_NORMAL);
            this.punchMode = PUNCH_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPunchOutEnabled ()
    {
        return this.punchMode != PUNCH_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void tapTempo ()
    {
        this.invokeAction (Actions.TRANSPORT_TAP_TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    public void changeTempo (final boolean increase, final boolean slow)
    {
        final String dir;
        if (increase)
            dir = slow ? "+" : "++";
        else
            dir = slow ? "-" : "--";
        this.sender.processNoArg (Processor.TEMPO, dir);
    }


    /** {@inheritDoc} */
    @Override
    public TempoParameterImpl getTempoParameter ()
    {
        return this.tempoParameter;
    }


    /** {@inheritDoc} */
    @Override
    public void setTempo (final double tempo)
    {
        this.sender.processDoubleArg (Processor.TEMPO, tempo);
    }


    /** {@inheritDoc} */
    @Override
    public double getTempo ()
    {
        return this.tempoParameter.getInternalValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempo (final double tempo)
    {
        return new DecimalFormat ("#.00").format (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempoNoFraction (final double tempo)
    {
        return new DecimalFormat ("###").format (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public void setTempoIndication (final boolean isTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getCrossfadeParameter ()
    {
        return ((MasterTrackImpl) this.model.getMasterTrack ()).getCrossfaderParameter ();
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfade (final int value)
    {
        this.getCrossfadeParameter ().setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfade ()
    {
        final IParameter crossfaderParameter = this.getCrossfadeParameter ();
        if (crossfaderParameter.doesExist ())
            return crossfaderParameter.getValue ();
        return this.valueChanger.getUpperBound () / 2;
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfade (final int control)
    {
        ((MasterTrackImpl) this.model.getMasterTrack ()).getCrossfaderParameter ().changeValue (control);
    }


    /** {@inheritDoc} */
    @Override
    public int getNumerator ()
    {
        return this.numerator;
    }


    /** {@inheritDoc} */
    @Override
    public int getDenominator ()
    {
        return this.denominator;
    }


    /** {@inheritDoc} */
    @Override
    public int getQuartersPerMeasure ()
    {
        return 4 * this.getNumerator () / this.getDenominator ();
    }


    /**
     * Set the play state.
     *
     * @param isPlaying True if playing
     */
    public void setPlayState (final boolean isPlaying)
    {
        this.isPlaying = isPlaying;
    }


    /**
     * Set the record state.
     *
     * @param isRecording True if recording
     */
    public void setRecordState (final boolean isRecording)
    {
        this.isRecording = isRecording;
    }


    /**
     * Set the loop state.
     *
     * @param isLooping True if loop is enabled
     */
    public void setLoopingState (final boolean isLooping)
    {
        this.isLooping = isLooping;
    }


    /**
     * Set the numerator.
     *
     * @param numerator The numerator
     */
    public void setNumerator (final int numerator)
    {
        this.numerator = numerator;
    }


    /**
     * Set the denominator.
     *
     * @param denominator The denominator
     */
    public void setDenominator (final int denominator)
    {
        this.denominator = denominator;
    }


    /** {@inheritDoc} */
    @Override
    public double scaleTempo (final double tempo, final int maxValue)
    {
        final double v = tempo - TransportConstants.MIN_TEMPO;
        return v * (maxValue - 1) / (TransportConstants.MAX_TEMPO - TransportConstants.MIN_TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.TRANSPORT;
    }


    /** {@inheritDoc} */
    @Override
    public PostRecordingAction getClipLauncherPostRecordingAction ()
    {
        // Not supported
        return PostRecordingAction.OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void setClipLauncherPostRecordingAction (final PostRecordingAction action)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public double getClipLauncherPostRecordingTimeOffset ()
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setClipLauncherPostRecordingTimeOffset (final double beats)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public LaunchQuantization getDefaultLaunchQuantization ()
    {
        // Not supported
        return LaunchQuantization.RES_NONE;
    }


    /** {@inheritDoc} */
    @Override
    public void setDefaultLaunchQuantization (final LaunchQuantization launchQuantization)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFillModeActive ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setFillModeActive (final boolean isActive)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleFillModeActive ()
    {
        // Not supported
    }


    private static String reformatBeats (final String beatsStr, final int offset)
    {
        final int pos = beatsStr.lastIndexOf ('.');
        if (pos < 1)
            return "0:00";

        final String ticksStr = beatsStr.substring (pos + 1);
        // Ticks are 0-99
        int ticks = Integer.parseInt (ticksStr);
        // Scale to 1-4:0-99 (= 0-399)
        final int scaled = (int) Math.round (ticks * 399.0 / 99.0);
        final int subBeats = scaled / 100;
        ticks = scaled % 100;
        return String.format ("%s.%d:%02d", beatsStr.substring (0, pos), Integer.valueOf (subBeats + offset), Integer.valueOf (ticks));
    }


    private static String reformatTime (final String timeStr)
    {
        final String [] split = timeStr.split ("\\.");
        if (split.length == 0)
            return "0:000";

        final String replace = split[0].replace (':', '.');
        if (split.length == 1)
            return replace;
        return replace + ":" + split[1];
    }
}