// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * Encapsulates the Transport instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportImpl extends BaseImpl implements ITransport
{
    private static final int    PUNCH_OFF              = 0;
    private static final int    PUNCH_ITEMS            = 1;
    private static final int    PUNCH_LOOP             = 2;

    /** 1 beat. */
    private static final double INC_FRACTION_TIME      = 1.0;
    /** 1/20th of a beat. */
    private static final double INC_FRACTION_TIME_SLOW = 1.0 / 20;

    private ITrackBank          trackBank;
    private IValueChanger       valueChanger;
    private IniFiles            iniFiles;

    private double              position               = 0;        // Time
    private String              positionStr            = "";
    private double              tempo                  = 120.0;
    private String              beatsStr               = "";

    private boolean             isMetronomeOn          = false;
    private boolean             isPlaying              = false;
    private boolean             isRecording            = false;
    private boolean             isLooping              = false;

    private int                 numerator              = 4;
    private int                 denominator            = 4;
    private boolean             prerollClick           = false;
    private int                 preroll                = 2;

    private int                 punchMode              = PUNCH_OFF;


    /**
     * Constructor
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param trackBank
     * @param iniFiles The INI configuration files
     */
    public TransportImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final ITrackBank trackBank, final IniFiles iniFiles)
    {
        super (host, sender);

        this.iniFiles = iniFiles;
        this.trackBank = trackBank;
        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public void play ()
    {
        this.sender.processNoArg ("play");
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
        this.sender.processNoArg ("stop");
    }


    /** {@inheritDoc} */
    @Override
    public void stopAndRewind ()
    {
        this.stop ();
        this.sender.processIntArg ("time", 0);
    }


    /** {@inheritDoc} */
    @Override
    public void record ()
    {
        this.sender.processNoArg ("record");
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
    public String getMetronomeVolumeStr ()
    {
        // Not supported
        return Double.toString (this.valueChanger.getUpperBound () / 2.0);
    }


    /** {@inheritDoc} */
    @Override
    public void changeMetronomeVolume (final int control)
    {
        this.sender.processNoArg ("metro_vol", this.valueChanger.calcKnobSpeed (control) > 0 ? "+" : "-");
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeVolume (final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getMetronomeVolume ()
    {
        // Not supported
        return this.valueChanger.getUpperBound () / 2;
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
        this.sender.processIntArg ("inifile", "reaper/prerollmeas", preroll);
        this.iniFiles.updateMainIniInteger ("reaper", "prerollmeas", preroll);
    }


    /** {@inheritDoc} */
    @Override
    public void setLoop (final boolean on)
    {
        if (on && !this.isLooping || !on && this.isLooping)
            this.sender.processIntArg ("repeat", 1);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLoop ()
    {
        this.sender.processIntArg ("repeat", 1);
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
        final String automationWriteMode = this.getAutomationWriteMode ();
        return automationWriteMode.length () != 0 && !TrackImpl.AUTOMATION_READ.equals (automationWriteMode) && !TrackImpl.AUTOMATION_TRIM.equals (automationWriteMode);
    }


    /** {@inheritDoc} */
    @Override
    public String getAutomationWriteMode ()
    {
        // Get from selected track
        final TrackImpl selectedTrack = (TrackImpl) this.trackBank.getSelectedItem ();
        return selectedTrack == null ? "" : selectedTrack.getAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutomationWriteMode (final String mode)
    {
        final ITrack selectedTrack = this.trackBank.getSelectedItem ();
        if (selectedTrack != null)
            this.sender.processIntArg ("track", selectedTrack.getIndex () + "/auto" + mode, 1);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWriteArrangerAutomation ()
    {
        if (this.isWritingArrangerAutomation ())
            this.setAutomationWriteMode (TrackImpl.AUTOMATION_READ);
        else
            this.setAutomationWriteMode (TrackImpl.AUTOMATION_WRITE);
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
        this.positionStr = positionStr.replace ('.', ':');
    }


    /**
     * Set the text for the playback position (beats).
     *
     * @param beats The position in beats
     */
    public void setBeats (final String beats)
    {
        this.beatsStr = beats;
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
        this.position = time;
        this.sender.processDoubleArg ("time", this.position);
    }


    /** {@inheritDoc} */
    @Override
    public void changePosition (final boolean increase)
    {
        this.changePosition (increase, this.valueChanger.isSlow ());
    }


    /** {@inheritDoc} */
    @Override
    public void changePosition (final boolean increase, final boolean slow)
    {
        final double frac = slow ? INC_FRACTION_TIME_SLOW : INC_FRACTION_TIME;
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
    public void changeTempo (final boolean increase)
    {
        final boolean isSlow = this.valueChanger.isSlow ();
        this.sender.processNoArg ("tempo", increase ? isSlow ? "+" : "++" : isSlow ? "-" : "--");
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
    public void setTempo (final double tempo)
    {
        this.sender.processDoubleArg ("tempo", tempo);
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
    public void setCrossfade (final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfade ()
    {
        // Not supported
        return this.valueChanger.getUpperBound () / 2;
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfade (final int control)
    {
        // Not supported
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
}