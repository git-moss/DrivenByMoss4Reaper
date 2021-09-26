// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.communication;

import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.reaper.framework.daw.ApplicationImpl;
import de.mossgrabers.reaper.framework.daw.ArrangerImpl;
import de.mossgrabers.reaper.framework.daw.BrowserImpl;
import de.mossgrabers.reaper.framework.daw.ModelImpl;
import de.mossgrabers.reaper.framework.daw.Note;
import de.mossgrabers.reaper.framework.daw.ProjectImpl;
import de.mossgrabers.reaper.framework.daw.TransportImpl;
import de.mossgrabers.reaper.framework.daw.data.CursorDeviceImpl;
import de.mossgrabers.reaper.framework.daw.data.EqualizerDeviceImpl;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;
import de.mossgrabers.reaper.framework.daw.data.MarkerImpl;
import de.mossgrabers.reaper.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.SceneImpl;
import de.mossgrabers.reaper.framework.daw.data.SendImpl;
import de.mossgrabers.reaper.framework.daw.data.SpecificDeviceImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.MarkerBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.ParameterBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.SceneBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.SendBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.TrackBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.UserParameterBankImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.GrooveParameter;
import de.mossgrabers.reaper.framework.daw.data.parameter.MetronomeVolumeParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;
import de.mossgrabers.reaper.framework.midi.NoteRepeatImpl;

import java.util.Collections;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * Parser for messages sent from Reaper.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MessageParser
{
    private static final String          TAG_ACTIVE = "active";
    private static final String          TAG_PARAM  = "param";
    private static final String          TAG_COLOR  = "color";
    private static final String          TAG_SELECT = "select";
    private static final String          TAG_NUMBER = "number";
    private static final String          TAG_COUNT  = "count";
    private static final String          TAG_EXISTS = "exists";
    private static final String          TAG_NAME   = "name";
    private static final String          TAG_VOLUME = "volume";

    private final IControllerSetup<?, ?> controllerSetup;

    private final IHost                  host;
    private final IProject               project;
    private final ApplicationImpl        application;
    private final ArrangerImpl           arranger;
    private final MasterTrackImpl        masterTrack;
    private final TransportImpl          transport;
    private final CursorDeviceImpl       cursorDevice;
    private final CursorDeviceImpl       instrumentDevice;
    private final EqualizerDeviceImpl    eqDevice;
    private final IBrowser               browser;
    private final IModel                 model;


    /**
     * Constructor.
     *
     * @param controllerSetup The model
     */
    public MessageParser (final IControllerSetup<?, ?> controllerSetup)
    {
        this.controllerSetup = controllerSetup;
        this.model = controllerSetup.getModel ();
        if (this.model == null)
        {
            this.host = null;
            this.project = null;
            this.application = null;
            this.arranger = null;
            this.transport = null;
            this.masterTrack = null;
            this.cursorDevice = null;
            this.instrumentDevice = null;
            this.eqDevice = null;
            this.browser = null;
        }
        else
        {
            this.host = this.model.getHost ();
            this.project = this.model.getProject ();
            this.application = (ApplicationImpl) this.model.getApplication ();
            this.arranger = (ArrangerImpl) this.model.getArranger ();
            this.transport = (TransportImpl) this.model.getTransport ();
            this.masterTrack = (MasterTrackImpl) this.model.getMasterTrack ();
            this.cursorDevice = (CursorDeviceImpl) this.model.getCursorDevice ();
            this.instrumentDevice = (CursorDeviceImpl) this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
            this.eqDevice = (EqualizerDeviceImpl) this.model.getSpecificDevice (DeviceID.EQ);
            this.browser = this.model.getBrowser ();
        }
    }


    /**
     * Parse OSC coming from Reaper.
     *
     * @param osc The OSC string
     * @param value The OSC value
     */
    public void parseOSC (final String osc, final String value)
    {
        if (this.model == null)
            return;

        final Queue<String> parts = parsePath (osc);
        if (parts.isEmpty ())
            return;

        final String command = parts.poll ();

        if (this.parseTransport (command, parts, value))
            return;

        switch (command)
        {
            case "project":
                final String projectCmd = parts.poll ();
                switch (projectCmd)
                {
                    case TAG_NAME:
                        ((ProjectImpl) this.project).setInternalName (value);
                        this.updateNoteMapping ();
                        break;
                    case "engine":
                        this.application.setInternalEngineActive (Integer.parseInt (value) > 0);
                        break;
                    default:
                        this.host.error ("Unhandled Project parameter: " + projectCmd);
                        break;
                }
                break;

            case "click":
                this.parseClick (parts, value);
                break;

            case "track":
                this.parseTrack (parts, value);
                break;

            case "master":
                this.parseMasterTrackValue (this.masterTrack, parts, value);
                break;

            case "device":
                this.parseDevice (this.cursorDevice, value, parts);
                break;

            case "primary":
                if (this.instrumentDevice != null)
                    this.parseDevice (this.instrumentDevice, value, parts);
                break;

            case "eq":
                if (this.eqDevice != null)
                    this.parseDevice (this.eqDevice, value, parts);
                break;

            case "user":
                this.parseUserParameter (value, parts);
                break;

            case "clip":
                this.parseClipValue (parts, value);
                break;

            case "browser":
                this.parseBrowserValue (parts, value);
                break;

            case "marker":
                this.parseMarker (parts, value);
                break;

            case "scene":
                this.parseScene (parts, value);
                break;

            case "quantize":
                if ("strength".equals (parts.poll ()))
                    this.controllerSetup.getConfiguration ().setQuantizeAmount (Integer.parseInt (value));
                break;

            case "noterepeat":
                this.parseNoteRepeat (parts, value);
                break;

            case "groove":
                this.parseGroove (parts, value);
                break;

            default:
                this.host.error ("Unhandled OSC address: " + osc + " " + value);
                return;
        }
    }


    private void parseClick (final Queue<String> parts, final String value)
    {
        if (parts.isEmpty ())
        {
            this.transport.setMetronomeState (Double.parseDouble (value) > 0);
            return;
        }

        final String clickCommand = parts.poll ();
        switch (clickCommand)
        {
            case "preroll":
                this.transport.setPrerollClick (Integer.parseInt (value) > 0);
                break;

            case TAG_VOLUME:
                this.transport.setInternalMetronomeVolume (Double.parseDouble (value));
                break;

            case "volumeStr":
                ((MetronomeVolumeParameterImpl) this.transport.getMetronomeVolumeParameter ()).setMetronomeVolumeStr (value);
                break;

            default:
                this.host.error ("Unhandled Click Parameter: " + clickCommand);
                break;
        }
    }


    private boolean parseTransport (final String command, final Queue<String> parts, final String value)
    {
        switch (command)
        {
            case "play":
                this.transport.setPlayState (Double.parseDouble (value) > 0);
                break;

            case "stop":
                this.transport.setPlayState (Double.parseDouble (value) == 0);
                break;

            case "repeat":
                this.transport.setLoopingState (Double.parseDouble (value) > 0);
                break;

            case "record":
                this.transport.setRecordState (Double.parseDouble (value) > 0);
                break;

            case "tempo":
                this.transport.getTempoParameter ().setInternalTempo (Double.parseDouble (value));
                break;

            case "time":
                if (parts.isEmpty ())
                    this.transport.setPositionValue (Double.parseDouble (value));
                else if ("str".equals (parts.poll ()))
                    this.transport.setPositionText (value);
                break;

            case "beat":
                this.transport.setBeats (value);
                break;

            case "numerator":
                final int numerator = (int) Double.parseDouble (value);
                if (numerator > 0)
                    this.transport.setNumerator (numerator);
                break;

            case "denominator":
                final int denominator = (int) Double.parseDouble (value);
                if (denominator > 0)
                    this.transport.setDenominator (denominator);
                break;

            case "followPlayback":
                this.arranger.setPlaybackFollow (Double.parseDouble (value) > 0);
                break;

            default:
                // Not a Transport command
                return false;
        }
        return true;
    }


    private void parseTrack (final Queue<String> parts, final String value)
    {
        final TrackBankImpl tb = (TrackBankImpl) this.model.getTrackBank ();
        final String part = parts.poll ();
        try
        {
            final int position = Integer.parseInt (part);
            this.parseTrackValue (tb, tb.getUnpagedItem (position), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            // The number of tracks
            if (TAG_COUNT.equals (part))
            {
                tb.setItemCount (Integer.parseInt (value));
                tb.markDirty ();
                this.rebindKnobs ();
            }
            else
                this.host.error ("Unhandled Track command: " + part);
        }
    }


    private void parseMasterTrackValue (final MasterTrackImpl masterTrack, final Queue<String> parts, final String value)
    {
        final String type = parts.peek ();

        if ("user".equals (type))
        {
            // Drop user
            parts.poll ();
            final String cmd = parts.poll ();
            if (TAG_PARAM.equals (cmd))
            {
                // Drop index
                parts.poll ();
                final IParameter crossfaderParam = masterTrack.getCrossfaderParameter ();
                this.parseDeviceParamValue (0, crossfaderParam, parts, value);
                return;
            }
        }

        this.parseTrackValue (null, masterTrack, parts, value);
    }


    private void parseTrackValue (final TrackBankImpl tb, final TrackImpl track, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case TAG_EXISTS:
                track.setExists (Double.parseDouble (value) > 0);
                break;

            case "depth":
                track.setDepth (Integer.parseInt (value));
                ((TrackBankImpl) this.model.getTrackBank ()).markDirty ();
                break;

            case TAG_ACTIVE:
                track.setInternalIsActivated (Double.parseDouble (value) > 0);
                ((TrackBankImpl) this.model.getTrackBank ()).markDirty ();
                break;

            case "type":
                track.setType (ChannelType.valueOf (value));
                break;

            case TAG_SELECT:
                final boolean isSelected = Double.parseDouble (value) > 0;
                track.setSelected (isSelected);
                ((TrackBankImpl) this.model.getCurrentTrackBank ()).handleBankTrackSelection (track, isSelected);
                if (isSelected)
                    this.updateNoteMapping ();
                break;

            case TAG_NUMBER:
                // Note: index is set in the tree (or flat) recalculation
                track.setPosition (Integer.parseInt (value));
                break;

            case TAG_NAME:
                track.setInternalName (value);
                break;

            case TAG_VOLUME:
                if (parts.isEmpty ())
                    track.setInternalVolume (Double.parseDouble (value));
                else if ("str".equals (parts.poll ()))
                    track.setVolumeStr (value);
                break;

            case "pan":
                if (parts.isEmpty ())
                    track.setInternalPan (Double.parseDouble (value));
                else if ("str".equals (parts.poll ()))
                    track.setPanStr (value);
                break;

            case "vu":
                track.setVu (Double.parseDouble (value));
                break;

            case "vuleft":
                track.setVuLeft (Double.parseDouble (value));
                break;

            case "vuright":
                track.setVuRight (Double.parseDouble (value));
                break;

            case "mute":
                track.setMuteState (Double.parseDouble (value) > 0);
                break;

            case "solo":
                track.setSoloState (Double.parseDouble (value) > 0);
                break;

            case "recarm":
                track.setRecArmState (Double.parseDouble (value) > 0);
                break;

            case "monitor":
                track.setMonitorState (Double.parseDouble (value) > 0);
                break;

            case "autoMonitor":
                track.setAutoMonitorState (Double.parseDouble (value) > 0);
                break;

            case "automode":
                switch ((int) Double.parseDouble (value))
                {
                    case 0:
                        track.setAutomation (AutomationMode.TRIM_READ);
                        break;
                    case 1:
                        track.setAutomation (AutomationMode.READ);
                        break;
                    case 2:
                        track.setAutomation (AutomationMode.TOUCH);
                        break;
                    case 3:
                        track.setAutomation (AutomationMode.WRITE);
                        break;
                    case 4:
                        track.setAutomation (AutomationMode.LATCH);
                        break;
                    case 5:
                        track.setAutomation (AutomationMode.LATCH_PREVIEW);
                        break;
                    default:
                        // Not used
                        break;
                }
                break;

            case "overdub":
                track.setOverdub (Double.parseDouble (value) > 0);
                break;

            case TAG_COLOR:
                final Optional<double []> color = ((ModelImpl) this.model).parseColor (value);
                if (color.isPresent ())
                    track.setColorState (color.get ());
                break;

            case "send":
                this.parseSend (track, parts, value);
                break;

            case "playingnotes":
                if (tb != null)
                    tb.handleNotes (track.getPosition (), Note.parseNotes (value));
                break;

            case "inQuantLengthEnabled":
                track.setRecordQuantizationNoteLengthState (Double.parseDouble (value) > 0);
                break;

            case "inQuantResolution":
                track.setRecordQuantizationGrid (Double.parseDouble (value));
                break;

            default:
                this.host.error ("Unhandled Track Parameter: " + command);
                break;
        }
    }


    private void parseSend (final TrackImpl track, final Queue<String> parts, final String value)
    {
        if (parts.isEmpty ())
            return;

        final String sendCmd = parts.poll ();
        final SendBankImpl sendBank = (SendBankImpl) track.getSendBank ();
        try
        {
            final int position = Integer.parseInt (sendCmd);
            this.parseSendValue (sendBank.getUnpagedItem (position), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            // The number of sends on the track
            if (TAG_COUNT.equals (sendCmd))
            {
                sendBank.setItemCount (Integer.parseInt (value));
                this.rebindKnobs ();
            }
            else
                this.host.error ("Unhandled Send command: " + sendBank);
        }
    }


    private void parseSendValue (final ISend send, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final SendImpl sendImpl = (SendImpl) send;
        switch (command)
        {
            case TAG_NAME:
                sendImpl.setInternalName (value);
                sendImpl.setExists (value != null && !value.isEmpty ());
                break;

            case TAG_VOLUME:
                if (parts.isEmpty ())
                    sendImpl.setInternalValue (Double.parseDouble (value));
                else if ("str".equals (parts.poll ()))
                    sendImpl.setValueStr (value);
                break;

            default:
                this.host.error ("Unhandled Send command: " + command);
                break;
        }
    }


    private void parseDevice (final SpecificDeviceImpl device, final String value, final Queue<String> parts)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case TAG_COUNT:
                if (device instanceof final CursorDeviceImpl cdi)
                    cdi.setDeviceCount (Integer.parseInt (value));
                break;

            case TAG_EXISTS:
                device.setExists (Integer.parseInt (value) > 0);
                break;

            case "position":
                device.setPosition (Integer.parseInt (value));
                break;

            case "bypass":
                device.setEnabled (Integer.parseInt (value) == 0);
                break;

            case TAG_NAME:
                device.setInternalName (value);
                break;

            case "window":
                device.setWindowOpen (Double.parseDouble (value) > 0);
                break;

            case "expand":
                device.setExpanded (Double.parseDouble (value) > 0);
                break;

            case "sibling":
                if (device instanceof final CursorDeviceImpl cdi)
                    this.parseSibling (cdi, command, parts, value);
                break;

            case TAG_PARAM:
                this.parseParameter (device, value, parts);
                break;

            case "band":
                if (device instanceof final EqualizerDeviceImpl edi)
                {
                    final String bandIndex = parts.poll ();
                    try
                    {
                        final int position = Integer.parseInt (bandIndex);
                        edi.setTypeInternal (position, value);
                    }
                    catch (final NumberFormatException ex)
                    {
                        this.host.error ("Unhandled equalizer command: " + bandIndex);
                    }
                }
                break;

            default:
                this.host.error ("Unhandled device parameter: " + command);
                break;
        }
    }


    private void parseParameter (final SpecificDeviceImpl device, final String value, final Queue<String> parts)
    {
        final String cmd = parts.poll ();
        try
        {
            final int paramNo = Integer.parseInt (cmd);
            final ParameterBankImpl parameterBank = (ParameterBankImpl) device.getParameterBank ();
            if (parameterBank != null)
                this.parseDeviceParamValue (paramNo, parameterBank.getUnpagedItem (paramNo), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            if (TAG_COUNT.equals (cmd))
            {
                device.setParameterCount (Integer.parseInt (value));
                this.rebindKnobs ();
            }
            else
                this.host.error ("Unhandled Device Param parameter: " + cmd);
        }
    }


    private void parseUserParameter (final String value, final Queue<String> parts)
    {
        final String type = parts.poll ();
        if (!type.equals (TAG_PARAM))
        {
            this.host.error ("Unhandled User parameter: " + type);
            return;
        }

        final String cmd = parts.poll ();
        final UserParameterBankImpl parameterBank = (UserParameterBankImpl) this.model.getUserParameterBank ();
        try
        {
            final int paramNo = Integer.parseInt (cmd);
            this.parseDeviceParamValue (paramNo, parameterBank.getUnpagedItem (paramNo), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            if (TAG_COUNT.equals (cmd))
            {
                parameterBank.setItemCount (Integer.parseInt (value));
                this.rebindKnobs ();
            }
            else
                this.host.error ("Unhandled User Param parameter: " + cmd);
        }
    }


    private void parseSibling (final CursorDeviceImpl device, final String command, final Queue<String> parts, final String value)
    {
        final String siblingCmd = parts.poll ();
        try
        {
            final int siblingNo = Integer.parseInt (siblingCmd) - 1;
            final IDeviceBank deviceBank = device.getDeviceBank ();
            if (siblingNo < deviceBank.getPageSize ())
            {
                final ItemImpl sibling = (ItemImpl) deviceBank.getItem (siblingNo);
                if (TAG_NAME.equals (parts.poll ()))
                {
                    sibling.setInternalName (value);
                    sibling.setExists (value != null && !value.isEmpty ());
                }
                else
                    this.host.error ("Unhandled device sibling parameter: " + command);
            }
        }
        catch (final NumberFormatException ex)
        {
            this.host.error ("Unhandled Device Sibling parameter: " + siblingCmd);
        }
    }


    private void parseDeviceParamValue (final int paramNo, final IParameter param, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final ParameterImpl p = (ParameterImpl) param;
        switch (command)
        {
            case TAG_NAME:
                p.setInternalName (value);
                p.setPosition (paramNo);
                p.setExists (value != null && !value.isEmpty ());
                break;

            case "value":
                if (parts.isEmpty ())
                    p.setInternalValue (Double.parseDouble (value));
                else if ("str".equals (parts.poll ()))
                    p.setValueStr (value);
                break;

            default:
                this.host.error ("Unhandled FX Param Value: " + command);
                break;
        }
    }


    private void parseBrowserValue (final Queue<String> parts, final String value)
    {
        if (this.browser == null)
            return;

        final String command = parts.poll ();
        switch (command)
        {
            case "result":
                final int resultNo = Integer.parseInt (parts.poll ()) - 1;
                if (TAG_NAME.equals (parts.poll ()))
                    ((BrowserImpl) this.browser).setPreset (resultNo, value == null || value.isEmpty () ? null : value);
                else
                    this.host.error ("Unhandled Browser Result: " + command);
                break;

            case "selected":
                switch (parts.poll ())
                {
                    case TAG_NAME:
                        // Not used
                        break;
                    case "index":
                        ((BrowserImpl) this.browser).setPresetSelected (Integer.parseInt (value));
                        break;
                    default:
                        this.host.error ("Unhandled Browser Parameter Selected: " + command);
                        break;
                }
                break;

            default:
                this.host.error ("Unhandled Browser Parameter: " + command);
                break;
        }
    }


    private void parseMarker (final Queue<String> parts, final String value)
    {
        final MarkerBankImpl markerBank = (MarkerBankImpl) this.model.getMarkerBank ();
        final String part = parts.poll ();
        try
        {
            final int position = Integer.parseInt (part);
            this.parseMarkerValue (markerBank.getUnpagedItem (position), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            // The number of tracks
            if (TAG_COUNT.equals (part))
                markerBank.setItemCount (Integer.parseInt (value));
            else
                this.host.error ("Unhandled Marker command: " + part);
        }
    }


    private void parseScene (final Queue<String> parts, final String value)
    {
        for (final ITrackBank tb: ((ModelImpl) this.model).getTrackBanks ())
        {
            final Queue<String> partsCopy = new LinkedBlockingDeque<> (parts);
            final SceneBankImpl sceneBank = (SceneBankImpl) tb.getSceneBank ();
            final String part = partsCopy.poll ();
            try
            {
                final int position = Integer.parseInt (part);
                this.parseSceneValue (sceneBank.getUnpagedItem (position), partsCopy, value);
            }
            catch (final NumberFormatException ex)
            {
                // The number of scenes
                if (TAG_COUNT.equals (part))
                    sceneBank.setItemCount (Integer.parseInt (value));
                else
                    this.host.error ("Unhandled Scene command: " + part);
            }
        }
    }


    private void parseMarkerValue (final IMarker marker, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final MarkerImpl markerImpl = (MarkerImpl) marker;
        switch (command)
        {
            case TAG_EXISTS:
                markerImpl.setExists (Double.parseDouble (value) > 0);
                break;

            case TAG_NUMBER:
                markerImpl.setPosition (Integer.parseInt (value));
                break;

            case TAG_NAME:
                markerImpl.setInternalName (value);
                break;

            case TAG_COLOR:
                final Optional<double []> color = ((ModelImpl) this.model).parseColor (value);
                if (color.isPresent ())
                    markerImpl.setColorState (color.get ());
                break;

            default:
                this.host.error ("Unhandled Marker Parameter: " + command);
                break;
        }
    }


    private void parseSceneValue (final IScene scene, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final SceneImpl sceneImpl = (SceneImpl) scene;
        switch (command)
        {
            case TAG_EXISTS:
                sceneImpl.setExists (Double.parseDouble (value) > 0);
                break;

            case TAG_NUMBER:
                sceneImpl.setPosition (Integer.parseInt (value));
                break;

            case TAG_NAME:
                sceneImpl.setInternalName (value);
                break;

            case TAG_COLOR:
                final Optional<double []> color = ((ModelImpl) this.model).parseColor (value);
                if (color.isPresent ())
                    sceneImpl.setColor (new ColorEx (color.get ()));
                break;

            default:
                this.host.error ("Unhandled Scene Parameter: " + command);
                break;
        }
    }


    private void parseClipValue (final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final ModelImpl modelImpl = (ModelImpl) this.model;
        switch (command)
        {
            case TAG_EXISTS:
                modelImpl.setCursorClipExists (Double.parseDouble (value) > 0);
                break;

            case "start":
                modelImpl.setCursorClipPlayStart (Double.parseDouble (value));
                break;

            case "end":
                modelImpl.setCursorClipPlayEnd (Double.parseDouble (value));
                break;

            case "playposition":
                modelImpl.setCursorClipPlayPosition (Double.parseDouble (value));
                break;

            case TAG_COLOR:
                final Optional<double []> color = modelImpl.parseColor (value);
                if (color.isPresent ())
                    modelImpl.setCursorClipColorValue (color.get ());
                break;

            case "loop":
                modelImpl.setCursorClipLoopIsEnabled (Double.parseDouble (value) > 0);
                break;

            case "notes":
                modelImpl.setCursorClipNotes (Note.parseNotes (value));
                break;

            case "all":
                modelImpl.setClips (value);
                break;

            default:
                this.host.error ("Unhandled Clip Parameter: " + command);
                break;
        }
    }


    private void parseNoteRepeat (final Queue<String> parts, final String value)
    {
        final IControlSurface<?> surface = this.controllerSetup.getSurface ();
        final IMidiInput input = surface.getMidiInput ();
        if (input == null)
            return;
        final NoteRepeatImpl noteRepeat = (NoteRepeatImpl) input.getDefaultNoteInput ().getNoteRepeat ();

        final String command = parts.poll ();
        switch (command)
        {
            case TAG_ACTIVE:
                final boolean isActive = Double.parseDouble (value) > 0;
                surface.getConfiguration ().setNoteRepeatActive (isActive);
                break;

            case "period":
                noteRepeat.setInternalPeriod (1.0 / Double.parseDouble (value));
                break;

            case "notelength":
                noteRepeat.setInternalNoteLength (Double.parseDouble (value));
                break;

            case "mode":
                noteRepeat.setInternalMode (Integer.parseInt (value));
                break;

            case "velocity":
                noteRepeat.setInternalUsePressure (Integer.parseInt (value) > 0);
                break;

            default:
                this.host.error ("Unhandled NoteRepeat Parameter: " + command);
                break;
        }
    }


    private void parseGroove (final Queue<String> parts, final String value)
    {
        final IGroove groove = this.model.getGroove ();

        final String command = parts.poll ();
        switch (command)
        {
            case TAG_ACTIVE:
                ((GrooveParameter) groove.getParameter (GrooveParameterID.ENABLED)).setInternalValue (Double.parseDouble (value));
                break;

            case "amount":
                ((GrooveParameter) groove.getParameter (GrooveParameterID.SHUFFLE_AMOUNT)).setInternalValue (Double.parseDouble (value));
                break;

            default:
                this.host.error ("Unhandled Groove Parameter: " + command);
                break;
        }
    }


    private static Queue<String> parsePath (final String osc)
    {
        final String [] parts = osc.split ("/");
        final Queue<String> oscParts = new ArrayBlockingQueue<> (parts.length);
        Collections.addAll (oscParts, parts);
        if (oscParts.size () <= 1)
        {
            oscParts.clear ();
            return oscParts;
        }
        // Remove first empty element
        oscParts.poll ();
        return oscParts;
    }


    private void updateNoteMapping ()
    {
        this.host.scheduleTask ( () -> this.controllerSetup.getSurface ().getViewManager ().getActive ().updateNoteMapping (), 1000);
    }


    /**
     * If the size of a bank changes (e.g. tracks or parameters) there might be the need to replace
     * EmptyXXX instances which are bound to knobs with real instances.
     */
    private void rebindKnobs ()
    {
        this.controllerSetup.getSurfaces ().forEach (surface -> {

            final IMode mode = surface.getModeManager ().getActive ();
            if (mode == null)
                return;

            // Force rebinding parameters to knobs
            mode.onActivate ();
        });
    }
}
