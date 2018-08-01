// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator.communication;

import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.reaper.framework.daw.AbstractTrackBankImpl;
import de.mossgrabers.reaper.framework.daw.ApplicationImpl;
import de.mossgrabers.reaper.framework.daw.BrowserImpl;
import de.mossgrabers.reaper.framework.daw.CursorDeviceImpl;
import de.mossgrabers.reaper.framework.daw.MarkerBankImpl;
import de.mossgrabers.reaper.framework.daw.ProjectImpl;
import de.mossgrabers.reaper.framework.daw.TransportImpl;
import de.mossgrabers.reaper.framework.daw.data.ChannelImpl;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;
import de.mossgrabers.reaper.framework.daw.data.MarkerImpl;
import de.mossgrabers.reaper.framework.daw.data.ParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.SendImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


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
    private final IMasterTrack     masterTrack;
    private final TransportImpl    transport;
    private final IValueChanger    valueChanger;
    private final CursorDeviceImpl cursorDevice;
    private final IBrowser         browser;
    private IModel                 model;


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
        this.masterTrack = this.model.getMasterTrack ();
        this.valueChanger = this.model.getValueChanger ();
        this.cursorDevice = (CursorDeviceImpl) this.model.getCursorDevice ();
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
                this.parseDevice (value, parts);
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
        final ITrackBank tb = this.model.getTrackBank ();
        final String part = parts.poll ();
        try
        {
            this.parseTrackValue (tb.getItem (Integer.parseInt (part) - 1), parts, value);
        }
        catch (final NumberFormatException ex)
        {
            switch (part)
            {
                // The number of tracks
                case "count":
                    ((AbstractTrackBankImpl) tb).setTrackCount (Integer.parseInt (value));
                    break;

                default:
                    this.host.error ("Unhandled Track command: " + part);
                    return;
            }
        }
    }


    private void parseTrackValue (final ITrack track, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case "exists":
                ((TrackImpl) track).setExists (Double.parseDouble (value) > 0);
                break;

            case "active":
                ((TrackImpl) track).setInternalIsActivated (Double.parseDouble (value) > 0);
                break;

            case "type":
                ((TrackImpl) track).setType (ChannelType.valueOf (value));
                break;

            case "select":
                final int index = track.getIndex ();
                // Is it the master track?
                if (index == -1)
                    track.setSelected (Double.parseDouble (value) > 0);
                else
                    ((AbstractTrackBankImpl) this.model.getCurrentTrackBank ()).handleBankTrackSelection (index, Double.parseDouble (value) > 0);
                break;

            case "number":
                ((TrackImpl) track).setPosition (Integer.parseInt (value));
                break;

            case "name":
                ((ChannelImpl) track).setName (value);
                break;

            case "volume":
                if (parts.isEmpty ())
                    ((ChannelImpl) track).setInternalVolume (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                else if ("str".equals (parts.poll ()))
                    ((ChannelImpl) track).setVolumeStr (value);
                break;

            case "pan":
                if (parts.isEmpty ())
                    ((ChannelImpl) track).setInternalPan (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                else if ("str".equals (parts.poll ()))
                    ((ChannelImpl) track).setPanStr (value);
                break;

            case "vuleft":
                ((ChannelImpl) track).setVuLeft (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                break;

            case "vuright":
                ((ChannelImpl) track).setVuRight (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                break;

            case "mute":
                ((ChannelImpl) track).setMuteState (Double.parseDouble (value) > 0);
                break;

            case "solo":
                ((ChannelImpl) track).setSoloState (Double.parseDouble (value) > 0);
                break;

            case "recarm":
                ((TrackImpl) track).setRecArmState (Double.parseDouble (value) > 0);
                break;

            case "monitor":
                ((TrackImpl) track).setMonitorState (Double.parseDouble (value) > 0);
                break;

            case "autoMonitor":
                ((TrackImpl) track).setAutoMonitorState (Double.parseDouble (value) > 0);
                break;

            case "automode":
                // (0=trim/off, 1=read, 2=touch, 3=write, 4=latch)
                switch ((int) Double.parseDouble (value))
                {
                    case 0:
                        ((TrackImpl) track).setAutomation (TrackImpl.AUTOMATION_TRIM);
                        break;
                    case 1:
                        ((TrackImpl) track).setAutomation (TrackImpl.AUTOMATION_READ);
                        break;
                    case 2:
                        ((TrackImpl) track).setAutomation (TrackImpl.AUTOMATION_TOUCH);
                        break;
                    case 3:
                        ((TrackImpl) track).setAutomation (TrackImpl.AUTOMATION_WRITE);
                        break;
                    case 4:
                        ((TrackImpl) track).setAutomation (TrackImpl.AUTOMATION_LATCH);
                        break;
                }
                break;

            case "color":
                final String [] values = value.split (" ");
                if (values.length != 3)
                {
                    this.host.error ("Color: Wrong number of arguments: " + values.length);
                    final StringBuilder str = new StringBuilder ();
                    for (final String value2: values)
                        str.append (value2).append (':');
                    this.host.error (str.toString ());
                    return;
                }
                ((TrackImpl) track).setColorState (new double []
                {
                    Double.parseDouble (values[0]) / 255.0,
                    Double.parseDouble (values[1]) / 255.0,
                    Double.parseDouble (values[2]) / 255.0
                });
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
                ((TrackImpl) track).setInternalRepeat (Double.parseDouble (value) > 0);
                break;

            case "noterepeatlength":
                ((TrackImpl) track).setInternalRepeatNoteLength (Integer.parseInt (value));
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


    private void parseDevice (final String value, final Queue<String> parts)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case "count":
                this.cursorDevice.setDeviceCount (Integer.parseInt (value));
                break;

            case "exists":
                this.cursorDevice.setExists (Integer.parseInt (value) > 0);
                break;

            case "position":
                this.cursorDevice.setPosition (Integer.parseInt (value));
                break;

            case "bypass":
                this.cursorDevice.setEnabled (Integer.parseInt (value) == 0);
                break;

            case "name":
                this.cursorDevice.setName (value);
                break;

            case "window":
                this.cursorDevice.setWindowOpen (Double.parseDouble (value) > 0);
                break;

            case "expand":
                this.cursorDevice.setExpanded (Double.parseDouble (value) > 0);
                break;

            case "sibling":
                try
                {
                    final int siblingNo = Integer.parseInt (parts.poll ()) - 1;
                    switch (parts.poll ())
                    {
                        case "name":
                            ((ItemImpl) this.cursorDevice.getDeviceBank ().getItem (siblingNo)).setName (value);
                            break;

                        default:
                            this.host.error ("Unhandled device sibling parameter: " + command);
                            return;
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
                    final int paramNo = Integer.parseInt (cmd) - 1;
                    this.parseDeviceParamValue (this.cursorDevice.getParameterBank ().getItem (paramNo), parts, value);
                }
                catch (final NumberFormatException ex)
                {
                    switch (cmd)
                    {
                        case "count":
                            this.cursorDevice.setParameterCount (Integer.parseInt (value));
                            break;

                        case "bank":
                            if (parts.isEmpty ())
                            {
                                this.host.error ("Missing Device Param Bank parameter.");
                                return;
                            }
                            final String bankCmd = parts.poll ();
                            if ("selected".equals (bankCmd))
                                this.cursorDevice.getParameterPageBank ().selectPage (Integer.parseInt (value) - 1);
                            else
                                this.host.error ("Unhandled Device Param Bank parameter: " + cmd);
                            break;

                        default:
                            this.host.error ("Unhandled Device Param parameter: " + cmd);
                            return;
                    }
                    return;
                }
                break;

            default:
                this.host.error ("Unhandled device parameter: " + command);
                return;
        }
    }


    private void parseDeviceParamValue (final IParameter param, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case "name":
                ((ParameterImpl) param).setName (value);
                ((ParameterImpl) param).setExists (value != null && !value.isEmpty ());
                break;

            case "value":
                if (parts.isEmpty ())
                    ((ParameterImpl) param).setInternalValue (this.valueChanger.fromNormalizedValue (Double.parseDouble (value)));
                else if ("str".equals (parts.poll ()))
                    ((ParameterImpl) param).setValueStr (value);
                break;

            default:
                this.host.error ("Unhandled FX Param Value: " + command);
                break;
        }
    }


    private void parseBrowserValue (final Queue<String> parts, final String value)
    {
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
            this.parseMarkerValue (markerBank.getItem (Integer.parseInt (part) - 1), parts, value);
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


    private void parseMarkerValue (final IMarker marker, final Queue<String> parts, final String value)
    {
        final String command = parts.poll ();
        switch (command)
        {
            case "exists":
                ((MarkerImpl) marker).setExists (Double.parseDouble (value) > 0);
                break;

            case "number":
                ((MarkerImpl) marker).setPosition (Integer.parseInt (value));
                break;

            case "name":
                ((MarkerImpl) marker).setName (value);
                break;

            case "color":
                final String [] values = value.split (" ");
                if (values.length != 3)
                {
                    this.host.error ("Color: Wrong number of arguments: " + values.length);
                    final StringBuilder str = new StringBuilder ();
                    for (final String value2: values)
                        str.append (value2).append (':');
                    this.host.error (str.toString ());
                    return;
                }
                ((MarkerImpl) marker).setColorState (new double []
                {
                    Double.parseDouble (values[0]) / 255.0,
                    Double.parseDouble (values[1]) / 255.0,
                    Double.parseDouble (values[2]) / 255.0
                });
                break;

            default:
                this.host.error ("Unhandled Marker Parameter: " + command);
                break;
        }
    }


    private void parseClipValue (final Queue<String> parts, final String value)
    {
        final ICursorClip clip = this.model.getCursorClip ();
        final String command = parts.poll ();
        switch (command)
        {
            case "start":
                clip.setPlayStart (Double.parseDouble (value));
                break;

            case "end":
                clip.setPlayEnd (Double.parseDouble (value));
                break;

            case "loopStart":
                // Not used - this is the repeat loop start
                break;

            case "loopEnd":
                // Not used - this is the repeat loop end
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
