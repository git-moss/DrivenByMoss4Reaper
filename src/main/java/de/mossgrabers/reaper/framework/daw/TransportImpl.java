// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;
import de.mossgrabers.transformator.communication.MessageSender;

import java.text.DecimalFormat;


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

    private int                 crossfade              = 0;
    private double              position               = 0;        // Time
    private String              positionStr            = "";
    private double              tempo                  = 120.0;
    private String              beatsStr               = "";
    private int                 metroVolume            = 512;

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
     * @param sender The OSC sender
     * @param host The DAW host
     * @param trackBank
     * @param valueChanger The value changer
     */
    public TransportImpl (final MessageSender sender, final IHost host, final ITrackBank trackBank, final IValueChanger valueChanger)
    {
        super (sender, host);

        this.trackBank = trackBank;
        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public void play ()
    {
        this.sender.sendOSC ("/play", null);
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
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.sender.sendOSC ("/stop", null);
    }


    /** {@inheritDoc} */
    @Override
    public void stopAndRewind ()
    {
        this.stop ();
        this.sender.sendOSC ("/time", Integer.valueOf (0));
    }


    /** {@inheritDoc} */
    @Override
    public void record ()
    {
        this.sender.sendOSC ("/record", null);
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


    /** {@inheritDoc} */
    @Override
    public void setMetronome (final boolean on)
    {
        this.isMetronomeOn = on;
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
        return Double.toString (this.metroVolume);
    }


    /** {@inheritDoc} */
    @Override
    public void changeMetronomeVolume (final int control)
    {
        this.sender.sendOSC ("/metro_vol/" + (this.valueChanger.calcKnobSpeed (control) > 0 ? '+' : '-'), null);
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeVolume (final double value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getMetronomeVolume ()
    {
        return this.metroVolume;
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
        switch (this.preroll)
        {
            case 0:
                return ITransport.PREROLL_NONE;
            case 1:
                return ITransport.PREROLL_1_BAR;
            case 2:
                return ITransport.PREROLL_2_BARS;
            case 4:
                return ITransport.PREROLL_4_BARS;
            default:
                // Other values are not supported
                return "";
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setPreroll (final String preroll)
    {
        switch (preroll)
        {
            case ITransport.PREROLL_NONE:
                this.setPrerollAsBars (0);
                break;
            case ITransport.PREROLL_1_BAR:
                this.setPrerollAsBars (1);
                break;
            case ITransport.PREROLL_2_BARS:
                this.setPrerollAsBars (2);
                break;
            case ITransport.PREROLL_4_BARS:
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
        this.preroll = preroll;
        this.sender.sendOSC ("/preroll", Integer.valueOf (preroll));
    }


    /** {@inheritDoc} */
    @Override
    public void setLoop (final boolean on)
    {
        if (on && !this.isLooping || !on && this.isLooping)
            this.sender.sendOSC ("/repeat", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLoop ()
    {
        this.sender.sendOSC ("/repeat", Integer.valueOf (1));
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
        final TrackImpl selectedTrack = (TrackImpl) this.trackBank.getSelectedTrack ();
        return selectedTrack == null ? "" : selectedTrack.getAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutomationWriteMode (final String mode)
    {
        final ITrack selectedTrack = this.trackBank.getSelectedTrack ();
        if (selectedTrack != null)
            this.sender.sendOSC ("/track/" + (selectedTrack.getIndex () + 1) + "/auto" + mode, Double.valueOf (1));
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
        this.sender.sendOSC ("/action_ex", "_S&M_REMOVE_ALLENVS");
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


    /** {@inheritDoc} */
    @Override
    public void setPosition (final double beats)
    {
        this.position = beats;
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
        this.position = increase ? this.position + frac : Math.max (this.position - frac, 0.0);
        this.sender.sendOSC ("/time", Double.valueOf (this.position));
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
        this.sender.sendOSC ("/tempo/" + (increase ? isSlow ? "+" : "++" : isSlow ? "-" : "--"), null);
    }


    /** {@inheritDoc} */
    @Override
    public void setTempo (final double tempo)
    {
        this.tempo = tempo;
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
    public void setCrossfade (final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfade ()
    {
        // Not supported
        return this.crossfade;
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
     * Set the number of preroll bars.
     *
     * @param preroll The number of bars
     */
    public void setInternalPreroll (final int preroll)
    {
        this.preroll = preroll;
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