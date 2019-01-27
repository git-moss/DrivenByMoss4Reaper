// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.communication;

import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.reaper.framework.daw.ApplicationImpl;
import de.mossgrabers.reaper.framework.daw.BrowserImpl;
import de.mossgrabers.reaper.framework.daw.CursorDeviceImpl;
import de.mossgrabers.reaper.framework.daw.MarkerBankImpl;
import de.mossgrabers.reaper.framework.daw.ModelImpl;
import de.mossgrabers.reaper.framework.daw.ParameterBankImpl;
import de.mossgrabers.reaper.framework.daw.ProjectImpl;
import de.mossgrabers.reaper.framework.daw.SceneBankImpl;
import de.mossgrabers.reaper.framework.daw.TrackBankImpl;
import de.mossgrabers.reaper.framework.daw.TransportImpl;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;
import de.mossgrabers.reaper.framework.daw.data.MarkerImpl;
import de.mossgrabers.reaper.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.ParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.SceneImpl;
import de.mossgrabers.reaper.framework.daw.data.SendImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.util.Collections;
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
    private final IControllerSetup controllerSetup;

    private final IHost            host;
    private final IProject         project;
    private final ApplicationImpl  application;
    private final MasterTrackImpl  masterTrack;
    private final TransportImpl    transport;
    private final IValueChanger    valueChanger;
    private final CursorDeviceImpl cursorDevice;
    private final CursorDeviceImpl instrumentDevice;
    private final IBrowser         browser;
    private final IModel           model;


    /**
     * Constructor.
     *
     * @param controllerSetup The model
     */
    public MessageParser (final IControllerSetup controllerSetup)
    {
        this.controllerSetup = controllerSetup;
        this.model = controllerSetup.getModel ();
        this.host = this.model.getHost ();
        this.project = this.model.getProject ();
        this.application = (ApplicationImpl) this.model.getApplication ();
        this.transport = (TransportImpl) this.model.getTransport ();
        this.masterTrack = (MasterTrackImpl) this.model.getMasterTrack ();
        this.valueChanger = this.model.getValueChanger ();
        this.cursorDevice = (CursorDeviceImpl) this.model.getCursorDevice ();
        this.instrumentDevice = (CursorDeviceImpl) this.model.getInstrumentDevice ();
        this.browser = this.model.getBrowser ();
    }


    /**
     * Parse OSC coming from Reaper.
     *
     * @param osc The OSC string
     * @param value The OSC value
     */
    public void parseOSC (final String osc, final String value)
    {
        final Queue<String> parts = parsePath (osc);
        if (parts == null)
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
                    case "name":
                        ((ProjectImpl) this.project).setName (value);
                        break;
                    case "engine":
                        this.application.setInternalEngineActive (Integer.parseInt (value) > 0);
                        break;
                    default:
                        this.host.error ("Unhandled Project parameter: " + projectCmd);
                        break;
                }
                break;

            case "track":
                this.parseTrack (parts, value);
                break;

            case "master":
                this.parseTrackValue (this.masterTrack, parts, value);
                break;

            case "device":
                this.parseDevice (this.cursorDevice, value, parts);
                break;

            case "primary":
                this.parseDevice (this.instrumentDevice, value, parts);
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

            default:
                this.host.error ("Unhandled OSC address: " + osc + " " + value);
                return;
        }
    }


    private boolean parseTransport (final String command, final Queue<String> parts, final String value)
    {
        switch (command)
        {
            case "click":
                this.transport.setMetronomeState (Double.parseDouble (value) > 0);
                break;

            case "prerollClick":
                this.transport.setPrerollClick (Integer.parseInt (value) > 0);
                break;

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
                this.transport.setTempoState (Double.parseDouble (value));
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
            final int index = Integer.parseInt (part);
            this.parseTrackValue (tb.getTrack (index), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            switch (part)
            {
                // The number of tracks
                case "count":
                    tb.setTrackCount (Integer.parseInt (value));
                    ((TrackBankImpl) this.model.getTrackBank ()).markDirty ();
                    break;

                default:
                    this.host.error ("Unhandled Track command: " + part);
                    return;
            }
        }
    }


    private void parseTrackValue (final TrackImpl track, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case "exists":
                track.setExists (Double.parseDouble (value) > 0);
                break;

            case "depth":
                track.setDepth (Integer.parseInt (value));
                ((TrackBankImpl) this.model.getTrackBank ()).markDirty ();
                break;

            case "active":
                track.setInternalIsActivated (Double.parseDouble (value) > 0);
                break;

            case "type":
                track.setType (ChannelType.valueOf (value));
                break;

            case "select":
                final boolean isSelected = Double.parseDouble (value) > 0;
                track.setSelected (isSelected);
                ((TrackBankImpl) this.model.getCurrentTrackBank ()).handleBankTrackSelection (track, isSelected);
                break;

            case "number":
                // Note: index is set in the tree (or flat) recalculation
                track.setPosition (Integer.parseInt (value));
                break;

            case "name":
                track.setName (value);
                break;

            case "volume":
                if (parts.isEmpty ())
                    track.setInternalVolume (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                else if ("str".equals (parts.poll ()))
                    track.setVolumeStr (value);
                break;

            case "pan":
                if (parts.isEmpty ())
                    track.setInternalPan (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                else if ("str".equals (parts.poll ()))
                    track.setPanStr (value);
                break;

            case "vuleft":
                track.setVuLeft (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                break;

            case "vuright":
                track.setVuRight (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
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
                // (0=trim/off, 1=read, 2=touch, 3=write, 4=latch)
                switch ((int) Double.parseDouble (value))
                {
                    case 0:
                        track.setAutomation (TrackImpl.AUTOMATION_TRIM);
                        break;
                    case 1:
                        track.setAutomation (TrackImpl.AUTOMATION_READ);
                        break;
                    case 2:
                        track.setAutomation (TrackImpl.AUTOMATION_TOUCH);
                        break;
                    case 3:
                        track.setAutomation (TrackImpl.AUTOMATION_WRITE);
                        break;
                    case 4:
                        track.setAutomation (TrackImpl.AUTOMATION_LATCH);
                        break;
                    default:
                        // Not used
                        break;
                }
                break;

            case "color":
                final double [] color = ((ModelImpl) this.model).parseColor (value);
                if (color != null)
                    track.setColorState (color);
                break;

            case "send":
                if (!parts.isEmpty ())
                {
                    final int sendIndex = Integer.parseInt (parts.poll ()) - 1;
                    final ISendBank sendBank = track.getSendBank ();
                    if (sendIndex < sendBank.getPageSize ())
                        this.parseSendValue (sendBank.getItem (sendIndex), parts, value);
                }
                break;

            case "repeatActive":
                track.setInternalNoteRepeat (Double.parseDouble (value) > 0);
                break;

            case "noterepeatlength":
                track.setInternalNoteRepeatLength (Double.parseDouble (value));
                break;

            default:
                this.host.error ("Unhandled Track Parameter: " + command);
                break;
        }
    }


    private void parseSendValue (final ISend send, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final SendImpl sendImpl = (SendImpl) send;
        switch (command)
        {
            case "name":
                sendImpl.setName (value);
                sendImpl.setExists (value != null && !value.isEmpty ());
                break;

            case "volume":
                if (parts.isEmpty ())
                    sendImpl.setInternalValue (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                else if ("str".equals (parts.poll ()))
                    sendImpl.setValueStr (value);
                break;

            default:
                this.host.error ("Unhandled Send command: " + command);
                break;
        }
    }


    private void parseDevice (final CursorDeviceImpl device, final String value, final Queue<String> parts)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case "count":
                device.setDeviceCount (Integer.parseInt (value));
                break;

            case "exists":
                device.setExists (Integer.parseInt (value) > 0);
                break;

            case "position":
                device.setPosition (Integer.parseInt (value));
                break;

            case "bypass":
                device.setEnabled (Integer.parseInt (value) == 0);
                break;

            case "name":
                device.setName (value);
                break;

            case "window":
                device.setWindowOpen (Double.parseDouble (value) > 0);
                break;

            case "expand":
                device.setExpanded (Double.parseDouble (value) > 0);
                break;

            case "sibling":
                try
                {
                    final int siblingNo = Integer.parseInt (parts.poll ()) - 1;
                    final IDeviceBank deviceBank = device.getDeviceBank ();
                    if (siblingNo < deviceBank.getPageSize ())
                    {
                        final ItemImpl sibling = (ItemImpl) deviceBank.getItem (siblingNo);
                        switch (parts.poll ())
                        {
                            case "name":
                                sibling.setName (value);
                                sibling.setExists (value != null && !value.isEmpty ());
                                break;

                            default:
                                this.host.error ("Unhandled device sibling parameter: " + command);
                                return;
                        }
                    }
                }
                catch (final NumberFormatException ex)
                {
                    return;
                }
                break;

            case "param":
                final String cmd = parts.poll ();
                try
                {
                    final int paramNo = Integer.parseInt (cmd);
                    final ParameterBankImpl parameterBank = (ParameterBankImpl) device.getParameterBank ();
                    if (parameterBank != null)
                        this.parseDeviceParamValue (paramNo, parameterBank.getParameter (paramNo), parts, value);
                }
                catch (final NumberFormatException ex)
                {
                    switch (cmd)
                    {
                        case "count":
                            device.setParameterCount (Integer.parseInt (value));
                            break;

                        default:
                            this.host.error ("Unhandled Device Param parameter: " + cmd);
                            return;
                    }
                }
                break;

            default:
                this.host.error ("Unhandled device parameter: " + command);
                return;
        }
    }


    private void parseDeviceParamValue (final int paramNo, final IParameter param, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final ParameterImpl p = (ParameterImpl) param;
        switch (command)
        {
            case "name":
                p.setName (value);
                p.setPosition (paramNo);
                p.setExists (value != null && !value.isEmpty ());
                break;

            case "value":
                if (parts.isEmpty ())
                    p.setInternalValue (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
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
                switch (parts.poll ())
                {
                    case "name":
                        ((BrowserImpl) this.browser).setPreset (resultNo, value == null || value.isEmpty () ? null : value);
                        break;
                    default:
                        this.host.error ("Unhandled Browser Result: " + command);
                        break;
                }
                break;

            case "selected":
                switch (parts.poll ())
                {
                    case "name":
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
        final IMarkerBank markerBank = this.model.getMarkerBank ();
        final String part = parts.poll ();
        try
        {
            final int index = Integer.parseInt (part);
            this.parseMarkerValue (markerBank.getItem (index), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            switch (part)
            {
                // The number of tracks
                case "count":
                    ((MarkerBankImpl) markerBank).setMarkerCount (Integer.parseInt (value));
                    break;

                default:
                    this.host.error ("Unhandled Marker command: " + part);
                    return;
            }
        }
    }


    private void parseScene (final Queue<String> parts, final String value)
    {
        for (final ITrackBank tb: ((ModelImpl) this.model).getTrackBanks ())
        {
            final Queue<String> partsCopy = new LinkedBlockingDeque<> (parts);
            final ISceneBank sceneBank = tb.getSceneBank ();
            final String part = partsCopy.poll ();
            try
            {
                final int index = Integer.parseInt (part);
                this.parseSceneValue (sceneBank.getItem (index), partsCopy, value);
            }
            catch (final NumberFormatException ex)
            {
                switch (part)
                {
                    // The number of tracks
                    case "count":
                        ((SceneBankImpl) sceneBank).setSceneCount (Integer.parseInt (value));
                        break;

                    default:
                        this.host.error ("Unhandled Scene command: " + part);
                        return;
                }
            }
        }
    }


    private void parseMarkerValue (final IMarker marker, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        final MarkerImpl markerImpl = (MarkerImpl) marker;
        switch (command)
        {
            case "exists":
                markerImpl.setExists (Double.parseDouble (value) > 0);
                break;

            case "number":
                markerImpl.setPosition (Integer.parseInt (value));
                break;

            case "name":
                markerImpl.setName (value);
                break;

            case "color":
                final double [] color = ((ModelImpl) this.model).parseColor (value);
                if (color != null)
                    markerImpl.setColorState (color);
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
            case "exists":
                sceneImpl.setExists (Double.parseDouble (value) > 0);
                break;

            case "number":
                sceneImpl.setPosition (Integer.parseInt (value));
                break;

            case "name":
                sceneImpl.setName (value);
                break;

            case "color":
                final double [] color = ((ModelImpl) this.model).parseColor (value);
                if (color != null)
                    sceneImpl.setColor (color[0], color[1], color[2]);
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
            case "start":
                modelImpl.setCursorClipPlayStart (Double.parseDouble (value));
                break;

            case "end":
                modelImpl.setCursorClipPlayEnd (Double.parseDouble (value));
                break;

            case "playposition":
                modelImpl.setCursorClipPlayPosition (Double.parseDouble (value));
                break;

            case "color":
                final double [] color = modelImpl.parseColor (value);
                if (color != null)
                    modelImpl.setCursorClipColorValue (color);
                break;

            case "loop":
                modelImpl.setCursorClipLoopIsEnabled (Double.parseDouble (value) > 0);
                break;

            case "notes":
                modelImpl.setCursorClipNotes (value);
                break;

            case "all":
                modelImpl.setClips (value);
                break;

            default:
                this.host.error ("Unhandled Clip Parameter: " + command);
                break;
        }
    }


    private static Queue<String> parsePath (final String osc)
    {
        final String [] parts = osc.split ("/");
        final Queue<String> oscParts = new ArrayBlockingQueue<> (parts.length);
        Collections.addAll (oscParts, parts);
        if (oscParts.size () < 2)
            return null;
        // Remove first empty element
        oscParts.poll ();
        return oscParts;
    }
}
