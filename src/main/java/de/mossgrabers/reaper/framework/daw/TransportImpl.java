// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.MetronomeVolumeParameterImpl;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * Encapsulates the Transport instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportImpl extends BaseImpl implements ITransport
{
    private static final int             PUNCH_OFF     = 0;
    private static final int             PUNCH_ITEMS   = 1;
    private static final int             PUNCH_LOOP    = 2;

    private static final Object          UPDATE_LOCK   = new Object ();

    private final IModel                 model;
    private IniFiles                     iniFiles;

    private double                       position      = 0;            // Time
    private String                       positionStr   = "";
    private double                       tempo         = 120.0;
    private String                       beatsStr      = "";

    private boolean                      isMetronomeOn = false;
    private boolean                      isPlaying     = false;
    private boolean                      isRecording   = false;
    private boolean                      isLooping     = false;

    private int                          numerator     = 4;
    private int                          denominator   = 4;
    private boolean                      prerollClick  = false;
    private int                          preroll       = 2;

    private int                          punchMode     = PUNCH_OFF;
    private MetronomeVolumeParameterImpl metronomeVolumeParameter;


    /**
     * Constructor
     *
     * @param dataSetup Some configuration variables
     * @param model The DAW model
     * @param iniFiles The INI configuration files
     */
    public TransportImpl (final DataSetupEx dataSetup, final IModel model, final IniFiles iniFiles)
    {
        super (dataSetup);

        this.iniFiles = iniFiles;
        this.model = model;

        this.metronomeVolumeParameter = new MetronomeVolumeParameterImpl (dataSetup);
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
    public void record ()
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
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleOverdub ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLauncherOverdub ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setLauncherOverdub (final boolean on)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLauncherOverdub ()
    {
        // Not supported
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
        return this.prerollClick;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePrerollMetronome ()
    {
        // Toggle recording pre-roll
        this.invokeAction (Actions.RECORD_PREROLL);
    }


    /** {@inheritDoc} */
    @Override
    public String getPreroll ()
    {
        this.preroll = (int) Math.floor (this.iniFiles.getMainIniDouble ("reaper", "prerollmeas", 2.0));

        switch (this.preroll)
        {
            case 0:
                return TransportConstants.PREROLL_NONE;
            case 1:
                return TransportConstants.PREROLL_1_BAR;
            case 2:
                return TransportConstants.PREROLL_2_BARS;
            case 4:
                return TransportConstants.PREROLL_4_BARS;
            default:
                // Other values are not supported, set to the default value
                this.setPreroll (TransportConstants.PREROLL_2_BARS);
                return TransportConstants.PREROLL_2_BARS;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setPreroll (final String preroll)
    {
        switch (preroll)
        {
            case TransportConstants.PREROLL_NONE:
                this.setPrerollAsBars (0);
                break;
            case TransportConstants.PREROLL_1_BAR:
                this.setPrerollAsBars (1);
                break;
            case TransportConstants.PREROLL_2_BARS:
                this.setPrerollAsBars (2);
                break;
            case TransportConstants.PREROLL_4_BARS:
                this.setPrerollAsBars (4);
                break;
            default:
                // Other values are not supported
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getPrerollAsBars ()
    {
        return this.preroll;
    }


    /** {@inheritDoc} */
    @Override
    public void setPrerollAsBars (final int preroll)
    {
        this.sender.processIntArg (Processor.INIFILE, "reaper/prerollmeas", preroll);
        this.iniFiles.updateMainIniInteger ("reaper", "prerollmeas", preroll);
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
        TrackImpl selectedTrack = (TrackImpl) this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            selectedTrack = (TrackImpl) this.model.getMasterTrack ();
        return selectedTrack.getAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutomationWriteMode (final AutomationMode mode)
    {
        final TrackImpl selectedTrack = (TrackImpl) this.model.getCurrentTrackBank ().getSelectedItem ();
        final String modeName = "auto" + mode.getIdentifier ();
        if (selectedTrack == null)
            this.sender.processIntArg (Processor.MASTER, modeName, 1);
        else
            this.sender.processIntArg (Processor.TRACK, selectedTrack.getPosition () + "/" + modeName, 1);
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
        final String [] split = positionStr.split ("\\.");
        if (split.length == 0)
        {
            this.positionStr = "0:000";
            return;
        }
        final String replace = split[0].replace (':', '.');
        if (split.length == 1)
            this.positionStr = replace;
        else
            this.positionStr = replace + ":" + split[1];
    }


    /**
     * Set the text for the playback position (beats).
     *
     * @param beats The position in beats
     */
    public void setBeats (final String beats)
    {
        final int pos = beats.lastIndexOf ('.');
        if (pos < 1)
        {
            this.beatsStr = "0:00";
            return;
        }

        this.beatsStr = beats.substring (0, pos) + ":" + beats.substring (pos + 1);
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
    public void changePosition (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.setPosition (increase ? this.position + frac : Math.max (this.position - frac, 0.0));
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
        this.sender.processNoArg (Processor.TEMPO, increase ? slow ? "+" : "++" : slow ? "-" : "--");
    }


    /**
     * Set the tempo value.
     *
     * @param tempo The value
     */
    public void setTempoState (final double tempo)
    {
        this.tempo = tempo;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getTempoParameter ()
    {
        // Not supported since not used
        return EmptyParameter.INSTANCE;
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
        return this.tempo;
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempo (final double tempo)
    {
        final NumberFormat instance = NumberFormat.getNumberInstance (Locale.US);
        instance.setMaximumFractionDigits (2);
        return instance.format (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempoNoFraction (final double tempo)
    {
        final NumberFormat instance = NumberFormat.getNumberInstance (Locale.US);
        instance.setMaximumFractionDigits (0);
        return instance.format (tempo);
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
     * @param numerator THe numerator
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


    /**
     * Dis-/Enable the preroll click.
     *
     * @param enable True to enable
     */
    public void setPrerollClick (final boolean enable)
    {
        this.prerollClick = enable;
    }


    /** {@inheritDoc} */
    @Override
    public double rescaleTempo (final double tempo, final int maxValue)
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
}