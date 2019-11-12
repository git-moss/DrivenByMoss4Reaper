// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push;

import de.mossgrabers.controller.push.command.continuous.ConfigurePitchbendCommand;
import de.mossgrabers.controller.push.command.continuous.MastertrackTouchCommand;
import de.mossgrabers.controller.push.command.continuous.SmallKnobTouchCommand;
import de.mossgrabers.controller.push.command.pitchbend.PitchbendCommand;
import de.mossgrabers.controller.push.command.pitchbend.PitchbendSessionCommand;
import de.mossgrabers.controller.push.command.trigger.AccentCommand;
import de.mossgrabers.controller.push.command.trigger.AutomationCommand;
import de.mossgrabers.controller.push.command.trigger.ClipCommand;
import de.mossgrabers.controller.push.command.trigger.DeviceCommand;
import de.mossgrabers.controller.push.command.trigger.FixedLengthCommand;
import de.mossgrabers.controller.push.command.trigger.LayoutCommand;
import de.mossgrabers.controller.push.command.trigger.MastertrackCommand;
import de.mossgrabers.controller.push.command.trigger.MuteCommand;
import de.mossgrabers.controller.push.command.trigger.OctaveCommand;
import de.mossgrabers.controller.push.command.trigger.PageLeftCommand;
import de.mossgrabers.controller.push.command.trigger.PageRightCommand;
import de.mossgrabers.controller.push.command.trigger.PanSendCommand;
import de.mossgrabers.controller.push.command.trigger.PushBrowserCommand;
import de.mossgrabers.controller.push.command.trigger.PushQuantizeCommand;
import de.mossgrabers.controller.push.command.trigger.RasteredKnobCommand;
import de.mossgrabers.controller.push.command.trigger.ScalesCommand;
import de.mossgrabers.controller.push.command.trigger.SelectCommand;
import de.mossgrabers.controller.push.command.trigger.SelectPlayViewCommand;
import de.mossgrabers.controller.push.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.controller.push.command.trigger.SetupCommand;
import de.mossgrabers.controller.push.command.trigger.ShiftCommand;
import de.mossgrabers.controller.push.command.trigger.SoloCommand;
import de.mossgrabers.controller.push.command.trigger.TrackCommand;
import de.mossgrabers.controller.push.command.trigger.VolumeCommand;
import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.Push2Display;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.AccentMode;
import de.mossgrabers.controller.push.mode.AutomationMode;
import de.mossgrabers.controller.push.mode.ConfigurationMode;
import de.mossgrabers.controller.push.mode.FixedMode;
import de.mossgrabers.controller.push.mode.FrameMode;
import de.mossgrabers.controller.push.mode.GrooveMode;
import de.mossgrabers.controller.push.mode.InfoMode;
import de.mossgrabers.controller.push.mode.MarkersMode;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.controller.push.mode.NoteRepeatMode;
import de.mossgrabers.controller.push.mode.NoteViewSelectMode;
import de.mossgrabers.controller.push.mode.QuantizeMode;
import de.mossgrabers.controller.push.mode.RibbonMode;
import de.mossgrabers.controller.push.mode.ScaleLayoutMode;
import de.mossgrabers.controller.push.mode.ScalesMode;
import de.mossgrabers.controller.push.mode.SessionMode;
import de.mossgrabers.controller.push.mode.SessionViewSelectMode;
import de.mossgrabers.controller.push.mode.SetupMode;
import de.mossgrabers.controller.push.mode.TransportMode;
import de.mossgrabers.controller.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.controller.push.mode.device.DeviceChainsMode;
import de.mossgrabers.controller.push.mode.device.DeviceLayerMode;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModePan;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModeSend;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModeVolume;
import de.mossgrabers.controller.push.mode.device.DeviceParamsMode;
import de.mossgrabers.controller.push.mode.device.UserParamsMode;
import de.mossgrabers.controller.push.mode.track.ClipMode;
import de.mossgrabers.controller.push.mode.track.CrossfaderMode;
import de.mossgrabers.controller.push.mode.track.LayerDetailsMode;
import de.mossgrabers.controller.push.mode.track.MasterMode;
import de.mossgrabers.controller.push.mode.track.PanMode;
import de.mossgrabers.controller.push.mode.track.SendMode;
import de.mossgrabers.controller.push.mode.track.TrackDetailsMode;
import de.mossgrabers.controller.push.mode.track.TrackMode;
import de.mossgrabers.controller.push.mode.track.VolumeMode;
import de.mossgrabers.controller.push.view.ClipView;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.controller.push.view.DrumView;
import de.mossgrabers.controller.push.view.DrumView4;
import de.mossgrabers.controller.push.view.DrumView64;
import de.mossgrabers.controller.push.view.DrumView8;
import de.mossgrabers.controller.push.view.PianoView;
import de.mossgrabers.controller.push.view.PlayView;
import de.mossgrabers.controller.push.view.PolySequencerView;
import de.mossgrabers.controller.push.view.PrgChangeView;
import de.mossgrabers.controller.push.view.RaindropsView;
import de.mossgrabers.controller.push.view.ScenePlayView;
import de.mossgrabers.controller.push.view.SequencerView;
import de.mossgrabers.controller.push.view.SessionView;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractViewCommand;
import de.mossgrabers.framework.command.continuous.FootswitchCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterVolumeCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.application.DeleteCommand;
import de.mossgrabers.framework.command.trigger.application.DuplicateCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.ConvertCommand;
import de.mossgrabers.framework.command.trigger.clip.DoubleCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.NoteRepeatCommand;
import de.mossgrabers.framework.command.trigger.clip.StopAllClipsCommand;
import de.mossgrabers.framework.command.trigger.device.AddEffectCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.command.trigger.mode.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.AddTrackCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Ableton Push 1 and Push 2 controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushControllerSetup extends AbstractControllerSetup<PushControlSurface, PushConfiguration>
{
    protected final boolean isPush2;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param isPush2 True if Push 2
     */
    public PushControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final boolean isPush2)
    {
        super (factory, host, globalSettings, documentSettings);
        this.isPush2 = isPush2;
        this.colorManager = new ColorManager ();
        PushColors.addColors (this.colorManager, isPush2);
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.configuration = new PushConfiguration (host, this.valueChanger, isPush2);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        final PushControlSurface surface = this.getSurface ();
        this.updateMode (surface.getModeManager ().getActiveOrTempModeId ());

        final View activeView = surface.getViewManager ().getActiveView ();
        if (activeView == null)
            return;
        final de.mossgrabers.framework.command.core.PitchbendCommand pitchbendCommand = activeView.getPitchbendCommand ();
        if (pitchbendCommand != null)
            pitchbendCommand.updateValue ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        if (this.isPush2)
        {
            ms.setNumFilterColumnEntries (48);
            ms.setNumResults (48);
        }
        ms.setNumMarkers (8);
        ms.setHasFlatTrackList (false);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        this.model.getMasterTrack ().addSelectionObserver ( (index, isSelected) -> {
            final PushControlSurface surface = this.getSurface ();
            final ModeManager modeManager = surface.getModeManager ();
            if (isSelected)
                modeManager.setActiveMode (Modes.MASTER);
            else if (modeManager.isActiveOrTempMode (Modes.MASTER))
                modeManager.restoreMode ();
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */, "B040??" /* Sustainpedal */);
        final PushControlSurface surface = new PushControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);

        if (this.isPush2)
            surface.addGraphicsDisplay (new Push2Display (this.host, this.valueChanger.getUpperBound (), this.configuration));
        else
            surface.addTextDisplay (new Push1Display (this.host, this.valueChanger.getUpperBound (), output, this.configuration));

        surface.getModeManager ().setDefaultMode (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.PAN, new PanMode (surface, this.model));
        modeManager.registerMode (Modes.CROSSFADER, new CrossfaderMode (surface, this.model));

        final SendMode modeSend = new SendMode (surface, this.model);
        modeManager.registerMode (Modes.SEND1, modeSend);
        modeManager.registerMode (Modes.SEND2, modeSend);
        modeManager.registerMode (Modes.SEND3, modeSend);
        modeManager.registerMode (Modes.SEND4, modeSend);
        modeManager.registerMode (Modes.SEND5, modeSend);
        modeManager.registerMode (Modes.SEND6, modeSend);
        modeManager.registerMode (Modes.SEND7, modeSend);
        modeManager.registerMode (Modes.SEND8, modeSend);

        modeManager.registerMode (Modes.MASTER, new MasterMode (surface, this.model, false));
        modeManager.registerMode (Modes.MASTER_TEMP, new MasterMode (surface, this.model, true));

        modeManager.registerMode (Modes.TRACK, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.TRACK_DETAILS, new TrackDetailsMode (surface, this.model));
        modeManager.registerMode (Modes.DEVICE_LAYER_DETAILS, new LayerDetailsMode (surface, this.model));
        modeManager.registerMode (Modes.CLIP, new ClipMode (surface, this.model));
        modeManager.registerMode (Modes.NOTE, new NoteMode (surface, this.model));
        modeManager.registerMode (Modes.FRAME, new FrameMode (surface, this.model));
        modeManager.registerMode (Modes.SCALES, new ScalesMode (surface, this.model));
        modeManager.registerMode (Modes.SCALE_LAYOUT, new ScaleLayoutMode (surface, this.model));
        modeManager.registerMode (Modes.ACCENT, new AccentMode (surface, this.model));
        modeManager.registerMode (Modes.FIXED, new FixedMode (surface, this.model));
        modeManager.registerMode (Modes.RIBBON, new RibbonMode (surface, this.model));

        modeManager.registerMode (Modes.GROOVE, new GrooveMode (surface, this.model));
        modeManager.registerMode (Modes.REC_ARM, new QuantizeMode (surface, this.model));

        modeManager.registerMode (Modes.VIEW_SELECT, new NoteViewSelectMode (surface, this.model));
        modeManager.registerMode (Modes.MARKERS, new MarkersMode (surface, this.model));

        modeManager.registerMode (Modes.AUTOMATION, new AutomationMode (surface, this.model));
        modeManager.registerMode (Modes.TRANSPORT, new TransportMode (surface, this.model));

        modeManager.registerMode (Modes.DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
        modeManager.registerMode (Modes.DEVICE_CHAINS, new DeviceChainsMode (surface, this.model));
        modeManager.registerMode (Modes.DEVICE_LAYER, new DeviceLayerMode ("Layer", surface, this.model));

        modeManager.registerMode (Modes.BROWSER, new DeviceBrowserMode (surface, this.model));

        modeManager.registerMode (Modes.DEVICE_LAYER_VOLUME, new DeviceLayerModeVolume (surface, this.model));
        modeManager.registerMode (Modes.DEVICE_LAYER_PAN, new DeviceLayerModePan (surface, this.model));
        final DeviceLayerModeSend modeLayerSend = new DeviceLayerModeSend (surface, this.model);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND1, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND2, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND3, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND4, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND5, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND6, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND7, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND8, modeLayerSend);

        if (this.host.hasUserParameters ())
            modeManager.registerMode (Modes.USER, new UserParamsMode (surface, this.model));

        if (this.isPush2)
        {
            modeManager.registerMode (Modes.SETUP, new SetupMode (surface, this.model));
            modeManager.registerMode (Modes.INFO, new InfoMode (surface, this.model));
        }
        else
            modeManager.registerMode (Modes.CONFIGURATION, new ConfigurationMode (surface, this.model));

        modeManager.registerMode (Modes.SESSION, new SessionMode (surface, this.model));
        modeManager.registerMode (Modes.SESSION_VIEW_SELECT, new SessionViewSelectMode (surface, this.model));

        modeManager.registerMode (Modes.REPEAT_NOTE, new NoteRepeatMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final PushControlSurface surface = this.getSurface ();
        if (this.configuration.isPush2 ())
        {
            this.configuration.addSettingObserver (PushConfiguration.DISPLAY_BRIGHTNESS, surface::sendDisplayBrightness);
            this.configuration.addSettingObserver (PushConfiguration.LED_BRIGHTNESS, surface::sendLEDBrightness);
            this.configuration.addSettingObserver (PushConfiguration.PAD_SENSITIVITY, () -> {
                surface.sendPadVelocityCurve ();
                surface.sendPadThreshold ();
            });
            this.configuration.addSettingObserver (PushConfiguration.PAD_GAIN, () -> {
                surface.sendPadVelocityCurve ();
                surface.sendPadThreshold ();
            });
            this.configuration.addSettingObserver (PushConfiguration.PAD_DYNAMICS, () -> {
                surface.sendPadVelocityCurve ();
                surface.sendPadThreshold ();
            });
        }
        else
        {
            this.configuration.addSettingObserver (PushConfiguration.VELOCITY_CURVE, surface::sendPadSensitivity);
            this.configuration.addSettingObserver (PushConfiguration.PAD_THRESHOLD, surface::sendPadSensitivity);
        }

        surface.getModeManager ().addModeListener ( (oldMode, newMode) -> this.updateMode (newMode));
        surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.onViewChange ());

        this.configuration.addSettingObserver (PushConfiguration.RIBBON_MODE, this::updateRibbonMode);
        this.configuration.addSettingObserver (PushConfiguration.DEBUG_MODE, () -> {
            final ModeManager modeManager = surface.getModeManager ();
            final Modes debugMode = this.configuration.getDebugMode ();
            if (modeManager.getMode (debugMode) != null)
                modeManager.setActiveMode (debugMode);
            else
                this.host.error ("Mode " + debugMode + " not registered.");
        });

        if (this.isPush2)
            this.configuration.addSettingObserver (PushConfiguration.DEBUG_WINDOW, this.getSurface ().getGraphicsDisplay ()::showDebugWindow);

        this.configuration.addSettingObserver (PushConfiguration.DISPLAY_SCENES_CLIPS, () -> {
            if (Views.isSessionView (this.getSurface ().getViewManager ().getActiveViewId ()))
            {
                final ModeManager modeManager = this.getSurface ().getModeManager ();
                if (modeManager.isActiveMode (Modes.SESSION))
                    modeManager.restoreMode ();
                else
                    modeManager.setActiveMode (Modes.SESSION);
            }
        });

        this.configuration.addSettingObserver (PushConfiguration.SESSION_VIEW, () -> {
            final ViewManager viewManager = this.getSurface ().getViewManager ();
            if (!Views.isSessionView (viewManager.getActiveViewId ()))
                return;
            if (this.configuration.isScenesClipViewSelected ())
                viewManager.setActiveView (Views.SCENE_PLAY);
            else
                viewManager.setActiveView (Views.SESSION);
        });

        this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SPEED_NORMAL, () -> this.valueChanger.setFractionValue (this.configuration.getKnobSpeedNormal ()));
        this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SPEED_SLOW, () -> this.valueChanger.setSlowFractionValue (this.configuration.getKnobSpeedSlow ()));

        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.PIANO, new PianoView (surface, this.model));
        viewManager.registerView (Views.PRG_CHANGE, new PrgChangeView (surface, this.model));
        viewManager.registerView (Views.CLIP, new ClipView (surface, this.model));
        viewManager.registerView (Views.COLOR, new ColorView (surface, this.model));

        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.POLY_SEQUENCER, new PolySequencerView (surface, this.model, true));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.DRUM4, new DrumView4 (surface, this.model));
        viewManager.registerView (Views.DRUM8, new DrumView8 (surface, this.model));
        viewManager.registerView (Views.RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.registerView (Views.SCENE_PLAY, new ScenePlayView (surface, this.model));

        viewManager.registerView (Views.DRUM64, new DrumView64 (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ModeManager modeManager = surface.getModeManager ();

        final ITransport t = this.model.getTransport ();

        this.setupButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_PLAY, t::isPlaying, PushColors.PUSH_BUTTON_STATE_PLAY_ON, PushColors.PUSH_BUTTON_STATE_PLAY_HI);

        this.setupButton (ButtonID.RECORD, "Record", new RecordCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_RECORD, () -> {

            if (this.isRecordShifted (surface))
                return t.isLauncherOverdub () ? 3 : 2;
            return t.isRecording () ? 1 : 0;

        }, PushColors.PUSH_BUTTON_STATE_REC_ON, PushColors.PUSH_BUTTON_STATE_REC_HI, PushColors.PUSH_BUTTON_STATE_OVR_ON, PushColors.PUSH_BUTTON_STATE_OVR_HI);

        this.setupButton (ButtonID.NEW, "New", new NewCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_NEW);
        this.setupButton (ButtonID.FIXED_LENGTH, "Fixed Length", new FixedLengthCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_FIXED_LENGTH, () -> modeManager.isActiveOrTempMode (Modes.VOLUME, Modes.FIXED));
        this.setupButton (ButtonID.DUPLICATE, "Duplicate", new DuplicateCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_DUPLICATE);
        this.setupButton (ButtonID.QUANTIZE, "Quantize", new PushQuantizeCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_QUANTIZE);
        this.setupButton (ButtonID.DELETE, "Delete", new DeleteCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_DELETE);
        this.setupButton (ButtonID.DOUBLE, "Double", new DoubleCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_DOUBLE);
        this.setupButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_UNDO);

        this.setupButton (ButtonID.AUTOMATION, "Automate", new AutomationCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_AUTOMATION, () -> {

            if (this.isRecordShifted (surface))
                return t.isWritingClipLauncherAutomation () ? 3 : 2;
            return t.isWritingArrangerAutomation () ? 1 : 0;

        }, PushColors.PUSH_BUTTON_STATE_REC_ON, PushColors.PUSH_BUTTON_STATE_REC_HI, PushColors.PUSH_BUTTON_STATE_OVR_ON, PushColors.PUSH_BUTTON_STATE_OVR_HI);

        this.setupButton (ButtonID.VOLUME, "Volume", new VolumeCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_VOLUME, () -> modeManager.isActiveOrTempMode (Modes.VOLUME, Modes.CROSSFADER));
        this.setupButton (ButtonID.PAN_SEND, "Pan/Send", new PanSendCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_PAN_SEND, () -> modeManager.isActiveOrTempMode (Modes.PAN) || Modes.isSendMode (modeManager.getActiveOrTempModeId ()));
        this.setupButton (ButtonID.TRACK, "Track", new TrackCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_TRACK, () -> this.isPush2 ? Modes.isMixMode (modeManager.getActiveOrTempModeId ()) : modeManager.isActiveOrTempMode (Modes.TRACK));
        this.setupButton (ButtonID.DEVICE, "Device", new DeviceCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_DEVICE, () -> Modes.isDeviceMode (modeManager.getActiveOrTempModeId ()));
        this.setupButton (ButtonID.BROWSE, "Browse", new PushBrowserCommand (Modes.BROWSER, this.model, surface), PushControlSurface.PUSH_BUTTON_BROWSE, () -> modeManager.isActiveOrTempMode (Modes.BROWSER));
        this.setupButton (ButtonID.CLIP, "Clip", new ClipCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_CLIP, () -> modeManager.isActiveOrTempMode (Modes.CLIP));

        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            this.setupButton (ButtonID.get (ButtonID.ROW1_1, i), "Row 1: " + (i + 1), new ButtonRowModeCommand<> (0, i, this.model, surface), PushControlSurface.PUSH_BUTTON_ROW1_1 + i, () -> {

                final Mode mode = modeManager.getActiveOrTempMode ();
                return mode == null ? 0 : mode.getFirstRowColor (index);

            });

            this.setupButton (ButtonID.get (ButtonID.ROW2_1, i), "Row 2: " + (i + 1), new ButtonRowModeCommand<> (1, i, this.model, surface), PushControlSurface.PUSH_BUTTON_ROW2_1 + i, () -> {

                final Mode mode = modeManager.getActiveOrTempMode ();
                return mode == null ? 0 : mode.getSecondRowColor (index);

            });

            this.setupButton (ButtonID.get (ButtonID.SCENE1, i), "Scene " + (i + 1), new SceneCommand<> (7 - i, this.model, surface), PushControlSurface.PUSH_BUTTON_SCENE1 + i, () -> {

                final View activeView = viewManager.getActiveView ();
                if (activeView instanceof SceneView)
                    return this.colorManager.getColor (((SceneView) activeView).getSceneButtonColor (index));
                return 0;

            });
        }

        this.setupButton (ButtonID.SHIFT, "Shift", new ShiftCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SHIFT);
        this.setupButton (ButtonID.SELECT, "Select", new SelectCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SELECT);
        this.setupButton (ButtonID.LAYOUT, "Layout", new LayoutCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_LAYOUT);
        this.setupButton (ButtonID.TAP_TEMPO, "Tap Tempo", new TapTempoCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_TAP);
        this.setupButton (ButtonID.METRONOME, "Metronome", new MetronomeCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_METRONOME, t::isMetronomeOn);
        this.setupButton (ButtonID.MASTERTRACK, "Mastertrack", new MastertrackCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_MASTER, () -> Modes.isMasterMode (modeManager.getActiveOrTempModeId ()));
        this.setupButton (ButtonID.PAGE_LEFT, "Page Left", new PageLeftCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_DEVICE_LEFT, () -> {

            if (viewManager.isActiveView (Views.SESSION))
                return this.model.getCurrentTrackBank ().canScrollPageBackwards ();
            final View activeView = viewManager.getActiveView ();
            final INoteClip clip = activeView instanceof AbstractSequencerView && !(activeView instanceof ClipView) ? ((AbstractSequencerView<?, ?>) activeView).getClip () : null;
            return clip != null && clip.doesExist () && clip.canScrollStepsBackwards ();

        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.setupButton (ButtonID.PAGE_RIGHT, "Page Right", new PageRightCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_DEVICE_RIGHT, () -> {

            if (viewManager.isActiveView (Views.SESSION))
                return this.model.getCurrentTrackBank ().canScrollPageForwards ();
            final View activeView = viewManager.getActiveView ();
            final INoteClip clip = activeView instanceof AbstractSequencerView && !(activeView instanceof ClipView) ? ((AbstractSequencerView<?, ?>) activeView).getClip () : null;
            return clip != null && clip.doesExist () && clip.canScrollStepsForwards ();

        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        this.setupButton (ButtonID.MUTE, "Mute", new MuteCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_MUTE, this::getMuteState, PushColors.PUSH_BUTTON_STATE_MUTE_ON, PushColors.PUSH_BUTTON_STATE_MUTE_HI);
        this.setupButton (ButtonID.SOLO, "Solo", new SoloCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SOLO, this::getSoloState, PushColors.PUSH_BUTTON_STATE_SOLO_ON, PushColors.PUSH_BUTTON_STATE_SOLO_HI);
        this.setupButton (ButtonID.SCALES, "Scale", new ScalesCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SCALES, () -> modeManager.isActiveOrTempMode (Modes.SCALES));
        this.setupButton (ButtonID.ACCENT, "Accent", new AccentCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_ACCENT, this.configuration::isAccentActive);
        this.setupButton (ButtonID.ADD_EFFECT, "Add Device", new AddEffectCommand<> (Modes.BROWSER, this.model, surface), PushControlSurface.PUSH_BUTTON_ADD_EFFECT);
        this.setupButton (ButtonID.ADD_TRACK, "Add Track", new AddTrackCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_ADD_TRACK);
        this.setupButton (ButtonID.NOTE, "Note", new SelectPlayViewCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_NOTE, () -> !Views.isSessionView (viewManager.getActiveViewId ()));

        final CursorCommand<PushControlSurface, PushConfiguration> cursorDownCommand = new CursorCommand<> (Direction.DOWN, this.model, surface);
        this.setupButton (ButtonID.ARROW_DOWN, "Down", cursorDownCommand, PushControlSurface.PUSH_BUTTON_DOWN, cursorDownCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        final CursorCommand<PushControlSurface, PushConfiguration> cursorUpCommand = new CursorCommand<> (Direction.UP, this.model, surface);
        this.setupButton (ButtonID.ARROW_UP, "Up", cursorUpCommand, PushControlSurface.PUSH_BUTTON_UP, cursorUpCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        final CursorCommand<PushControlSurface, PushConfiguration> cursorLeftCommand = new CursorCommand<> (Direction.LEFT, this.model, surface);
        this.setupButton (ButtonID.ARROW_LEFT, "Left", cursorLeftCommand, PushControlSurface.PUSH_BUTTON_LEFT, cursorLeftCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        final CursorCommand<PushControlSurface, PushConfiguration> cursorRightCommand = new CursorCommand<> (Direction.RIGHT, this.model, surface);
        this.setupButton (ButtonID.ARROW_RIGHT, "Right", cursorRightCommand, PushControlSurface.PUSH_BUTTON_RIGHT, cursorRightCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        this.setupButton (ButtonID.OCTAVE_DOWN, "Octave Down", new OctaveCommand (false, this.model, surface), PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, () -> {

            final View activeView = viewManager.getActiveView ();
            return activeView instanceof TransposeView ? ((TransposeView) activeView).isOctaveDownButtonOn () : false;

        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.setupButton (ButtonID.OCTAVE_UP, "Octave Up", new OctaveCommand (true, this.model, surface), PushControlSurface.PUSH_BUTTON_OCTAVE_UP, () -> {

            final View activeView = viewManager.getActiveView ();
            return activeView instanceof TransposeView ? ((TransposeView) activeView).isOctaveUpButtonOn () : false;

        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        if (this.isPush2)
        {
            this.setupButton (ButtonID.SETUP, "Setup", new SetupCommand (this.isPush2, this.model, surface), PushControlSurface.PUSH_BUTTON_SETUP, () -> modeManager.isActiveOrTempMode (Modes.SETUP));
            this.setupButton (ButtonID.CONVERT, "Convert", new ConvertCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_CONVERT, this.model::canConvertClip);
            this.setupButton (ButtonID.USER, "User", this.host.hasUserParameters () ? new ModeSelectCommand<> (this.model, surface, Modes.USER) : NopCommand.INSTANCE, PushControlSurface.PUSH_BUTTON_USER_MODE, () -> this.host.hasUserParameters () && modeManager.isActiveOrTempMode (Modes.USER));
        }
        else
            this.setupButton (ButtonID.SETUP, "User", new SetupCommand (this.isPush2, this.model, surface), PushControlSurface.PUSH_BUTTON_USER_MODE, () -> modeManager.isActiveOrTempMode (Modes.SETUP));

        this.setupButton (ButtonID.STOP_CLIP, "Stop Clip", new StopAllClipsCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_STOP_CLIP, () -> surface.isPressed (ButtonID.STOP_CLIP), PushColors.PUSH_BUTTON_STATE_STOP_ON, PushColors.PUSH_BUTTON_STATE_STOP_HI);
        this.setupButton (ButtonID.SESSION, "Session", new SelectSessionViewCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SESSION, () -> Views.isSessionView (viewManager.getActiveViewId ()));
        this.setupButton (ButtonID.REPEAT, "Repeat", new NoteRepeatCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_REPEAT, surface.getInput ().getDefaultNoteInput ().getNoteRepeat ()::isActive);
    }


    private boolean isRecordShifted (final PushControlSurface surface)
    {
        final boolean isShift = surface.isShiftPressed ();
        final boolean isFlipRecord = this.configuration.isFlipRecord ();
        return isShift && !isFlipRecord || !isShift && isFlipRecord;
    }


    /** {@inheritDoc} */
    @SuppressWarnings(
    {
        "rawtypes",
        "unchecked"
    })
    @Override
    protected void registerContinuousCommands ()
    {
        final PushControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.KNOB1, i), PushControlSurface.PUSH_KNOB1 + i, new KnobRowModeCommand<> (i, this.model, surface));

        this.addContinuousCommand (ContinuousCommandID.MASTER_KNOB, PushControlSurface.PUSH_KNOB9, new MasterVolumeCommand<> (this.model, surface));
        this.addContinuousCommand (ContinuousCommandID.TEMPO, PushControlSurface.PUSH_SMALL_KNOB1, new RasteredKnobCommand (this.model, surface));
        this.addContinuousCommand (ContinuousCommandID.PLAY_POSITION, PushControlSurface.PUSH_SMALL_KNOB2, new PlayPositionCommand<> (this.model, surface));
        this.addContinuousCommand (ContinuousCommandID.FOOTSWITCH, PushControlSurface.PUSH_FOOTSWITCH2, new FootswitchCommand<> (this.model, surface));

        this.addNoteCommand (TriggerCommandID.KNOB1_TOUCH, PushControlSurface.PUSH_KNOB1_TOUCH, new KnobRowTouchModeCommand<> (0, this.model, surface));
        this.addNoteCommand (TriggerCommandID.KNOB2_TOUCH, PushControlSurface.PUSH_KNOB2_TOUCH, new KnobRowTouchModeCommand<> (1, this.model, surface));
        this.addNoteCommand (TriggerCommandID.KNOB3_TOUCH, PushControlSurface.PUSH_KNOB3_TOUCH, new KnobRowTouchModeCommand<> (2, this.model, surface));
        this.addNoteCommand (TriggerCommandID.KNOB4_TOUCH, PushControlSurface.PUSH_KNOB4_TOUCH, new KnobRowTouchModeCommand<> (3, this.model, surface));
        this.addNoteCommand (TriggerCommandID.KNOB5_TOUCH, PushControlSurface.PUSH_KNOB5_TOUCH, new KnobRowTouchModeCommand<> (4, this.model, surface));
        this.addNoteCommand (TriggerCommandID.KNOB6_TOUCH, PushControlSurface.PUSH_KNOB6_TOUCH, new KnobRowTouchModeCommand<> (5, this.model, surface));
        this.addNoteCommand (TriggerCommandID.KNOB7_TOUCH, PushControlSurface.PUSH_KNOB7_TOUCH, new KnobRowTouchModeCommand<> (6, this.model, surface));
        this.addNoteCommand (TriggerCommandID.KNOB8_TOUCH, PushControlSurface.PUSH_KNOB8_TOUCH, new KnobRowTouchModeCommand<> (7, this.model, surface));
        this.addNoteCommand (TriggerCommandID.TEMPO_TOUCH, PushControlSurface.PUSH_SMALL_KNOB1_TOUCH, new SmallKnobTouchCommand (this.model, surface, true));
        this.addNoteCommand (TriggerCommandID.PLAYCURSOR_TOUCH, PushControlSurface.PUSH_SMALL_KNOB2_TOUCH, new SmallKnobTouchCommand (this.model, surface, false));
        this.addNoteCommand (TriggerCommandID.CONFIGURE_PITCHBEND, PushControlSurface.PUSH_RIBBON_TOUCH, new ConfigurePitchbendCommand (this.model, surface));
        this.addNoteCommand (TriggerCommandID.MASTERTRACK_TOUCH, PushControlSurface.PUSH_KNOB9_TOUCH, new MastertrackTouchCommand (this.model, surface));

        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerPitchbendCommand (new PitchbendCommand (this.model, surface));

        final Views [] views =
        {
            Views.PLAY,
            Views.PIANO,
            Views.DRUM,
            Views.DRUM64
        };
        for (final Views viewID: views)
        {
            final AbstractView view = (AbstractView) viewManager.getView (viewID);
            view.registerAftertouchCommand (new AftertouchAbstractViewCommand<> (view, this.model, surface));
        }

        viewManager.getView (Views.SESSION).registerPitchbendCommand (new PitchbendSessionCommand (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final PushControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (this.configuration.getDefaultNoteView ());

        surface.sendPressureMode (true);
        surface.getOutput ().sendSysex (DeviceInquiry.createQuery ());

        if (this.isPush2)
            surface.updateColorPalette ();
    }


    /**
     * Called when a new view is selected.
     */
    private void onViewChange ()
    {
        final PushControlSurface surface = this.getSurface ();

        // Update ribbon mode
        if (surface.getViewManager ().isActiveView (Views.SESSION))
            surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_PAN);
        else
            this.updateRibbonMode ();

        this.updateIndication (null);
    }


    private void updateMode (final Modes mode)
    {
        if (mode != null)
            this.updateIndication (mode);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode == mode)
            return;

        if (mode != null)
            this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final PushControlSurface surface = this.getSurface ();
        final boolean isSession = surface.getViewManager ().isActiveView (Views.SESSION);
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isTrackMode = Modes.TRACK == this.currentMode;
        final boolean isVolume = Modes.VOLUME == this.currentMode;
        final boolean isPan = Modes.PAN == this.currentMode;
        final boolean isDevice = Modes.isDeviceMode (this.currentMode) || Modes.isLayerMode (this.currentMode);
        final boolean isUserMode = Modes.USER == this.currentMode;

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i && isTrackMode;
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && (isVolume || hasTrackSel));
            track.setPanIndication (!isEffect && (isPan || hasTrackSel));

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < sendBank.getPageSize (); j++)
                sendBank.getItem (j).setIndication (!isEffect && (this.currentMode.ordinal () - Modes.SEND1.ordinal () == j || hasTrackSel));

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect);
                fxTrack.setPanIndication (isEffect && isPan);
            }

            parameterBank.getItem (i).setIndication (isDevice);
        }

        if (this.host.hasUserParameters ())
        {
            final IParameterBank userParameterBank = this.model.getUserParameterBank ();
            for (int i = 0; i < userParameterBank.getPageSize (); i++)
                userParameterBank.getItem (i).setIndication (isUserMode);
        }
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected)
            return;

        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ModeManager modeManager = surface.getModeManager ();

        // Recall last used view (if we are not in session mode)
        if (!viewManager.isActiveView (Views.SESSION))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Views preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (preferredView == null ? this.configuration.getDefaultNoteView () : preferredView);
            }
        }

        if (modeManager.isActiveOrTempMode (Modes.MASTER))
            modeManager.setActiveMode (Modes.TRACK);

        if (viewManager.isActiveView (Views.PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.DRUM))
            viewManager.getView (Views.DRUM).updateNoteMapping ();
    }


    private void updateRibbonMode ()
    {
        final PushControlSurface surface = this.getSurface ();
        surface.setRibbonValue (0);

        switch (this.configuration.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_CC:
            case PushConfiguration.RIBBON_MODE_FADER:
                surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_VOLUME);
                break;

            default:
                surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_PITCHBEND);
                break;
        }
    }


    private boolean getMuteState ()
    {
        final PushControlSurface surface = this.getSurface ();
        if (this.isPush2)
        {
            final ModeManager modeManager = surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.DEVICE_LAYER))
            {
                final ICursorDevice cd = this.model.getCursorDevice ();
                final IChannel layer = cd.getLayerOrDrumPadBank ().getSelectedItem ();
                return layer != null && layer.isMute ();
            }
            final ITrack selTrack = modeManager.isActiveOrTempMode (Modes.MASTER) ? this.model.getMasterTrack () : this.model.getSelectedTrack ();
            return selTrack != null && selTrack.isMute ();
        }
        return surface.getConfiguration ().isMuteState ();
    }


    private boolean getSoloState ()
    {
        final PushControlSurface surface = this.getSurface ();
        if (this.isPush2)
        {
            final ModeManager modeManager = surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.DEVICE_LAYER))
            {
                final ICursorDevice cd = this.model.getCursorDevice ();
                final IChannel layer = cd.getLayerOrDrumPadBank ().getSelectedItem ();
                return layer != null && layer.isSolo ();
            }
            final ITrack selTrack = modeManager.isActiveOrTempMode (Modes.MASTER) ? this.model.getMasterTrack () : this.model.getSelectedTrack ();
            return selTrack != null && selTrack.isSolo ();
        }
        return surface.getConfiguration ().isSoloState ();
    }
}
