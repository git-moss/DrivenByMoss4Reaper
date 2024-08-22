// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.ZoomParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.device.DeviceMetadataImpl;
import de.mossgrabers.reaper.ui.utils.RobotUtil;

import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Proxy to the Application object.
 *
 * @author Jürgen Moßgraber
 */
public class ApplicationImpl extends BaseImpl implements IApplication
{
    private static final String [] PANEL_LAYOUTS = new String []
    {
        PANEL_LAYOUT_ARRANGE,
        PANEL_LAYOUT_MIX,
        PANEL_LAYOUT_EDIT,
        PANEL_LAYOUT_PLAY
    };

    private String                 panelLayout   = IApplication.PANEL_LAYOUT_ARRANGE;
    private boolean                engineActive  = true;
    private int                    windowLayout  = 0;
    private boolean                canUndoState  = true;
    private boolean                canRedoState  = true;
    private final ZoomParameter    horizontalZoomParameter;
    private final ZoomParameter    verticalZoomParameter;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    public ApplicationImpl (final DataSetupEx dataSetup)
    {
        super (dataSetup);

        this.horizontalZoomParameter = new ZoomParameter (this.valueChanger, this, true);
        this.verticalZoomParameter = new ZoomParameter (this.valueChanger, this, true);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEngineActive ()
    {
        return this.engineActive;
    }


    /** {@inheritDoc} */
    @Override
    public void setEngineActive (final boolean active)
    {
        this.sender.processBooleanArg (Processor.PROJECT, "engine", active);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEngineActive ()
    {
        this.setEngineActive (!this.engineActive);
    }


    /** {@inheritDoc} */
    @Override
    public void setPanelLayout (final String panelLayout)
    {
        this.panelLayout = panelLayout;
        switch (panelLayout)
        {
            case PANEL_LAYOUT_ARRANGE:
                this.windowLayout = 0;
                this.sender.invokeAction (Actions.LOAD_WINDOW_SET_1);
                break;
            case PANEL_LAYOUT_MIX:
                this.windowLayout = 1;
                this.sender.invokeAction (Actions.LOAD_WINDOW_SET_2);
                break;
            case PANEL_LAYOUT_EDIT:
                this.windowLayout = 2;
                this.sender.invokeAction (Actions.LOAD_WINDOW_SET_3);
                break;
            case PANEL_LAYOUT_PLAY:
                this.windowLayout = 3;
                this.sender.invokeAction (Actions.LOAD_WINDOW_SET_4);
                break;
            default:
                this.host.println ("Not a supported layout: " + panelLayout);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getPanelLayout ()
    {
        return this.panelLayout;
    }


    /** {@inheritDoc} */
    @Override
    public void previousPanelLayout ()
    {
        this.setPanelLayout (this.windowLayout - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void nextPanelLayout ()
    {
        this.setPanelLayout (this.windowLayout + 1);
    }


    private void setPanelLayout (final int index)
    {
        this.windowLayout = index % 4;
        this.panelLayout = PANEL_LAYOUTS[this.windowLayout];
        this.sender.invokeAction (Actions.LOAD_WINDOW_SET_1 + this.windowLayout);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isArrangeLayout ()
    {
        return PANEL_LAYOUT_ARRANGE.equals (this.panelLayout);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMixerLayout ()
    {
        return PANEL_LAYOUT_MIX.equals (this.panelLayout);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEditLayout ()
    {
        return PANEL_LAYOUT_EDIT.equals (this.panelLayout);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlayLayout ()
    {
        return PANEL_LAYOUT_PLAY.equals (this.panelLayout);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleNoteEditor ()
    {
        this.sender.invokeAction (Actions.TOGGLE_SHOW_MIDI_EDITOR_WINDOWS);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutomationEditor ()
    {
        this.sender.invokeAction (Actions.SHOW_ALL_ACTIVE_ENVELOPES);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDevices ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleInspector ()
    {
        this.sender.invokeAction (Actions.SHOW_TRACK_MANAGER_WINDOW);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMixer ()
    {
        this.sender.invokeAction (Actions.TOGGLE_MIXER_VISIBLE);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleFullScreen ()
    {
        this.sender.invokeAction (Actions.TOGGLE_FULLSCREEN);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleBrowserVisibility ()
    {
        this.sender.invokeAction (Actions.TOGGLE_MEDIA_EXPLORER);
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void deleteSelection ()
    {
        this.sender.invokeAction (Actions.REMOVE_ITEMS);
    }


    /** {@inheritDoc} */
    @Override
    public void undo ()
    {
        this.sender.processNoArg (Processor.UNDO);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canUndo ()
    {
        return this.canUndoState;
    }


    /**
     * Set the state of canUndo.
     *
     * @param canUndoState The state
     */
    public void setCanUndoState (final boolean canUndoState)
    {
        this.canUndoState = canUndoState;
    }


    /** {@inheritDoc} */
    @Override
    public void redo ()
    {
        this.sender.processNoArg (Processor.REDO);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canRedo ()
    {
        return this.canRedoState;
    }


    /**
     * Set the state of canRedo.
     *
     * @param canRedoState The state
     */
    public void setCanRedoState (final boolean canRedoState)
    {
        this.canRedoState = canRedoState;
    }


    /** {@inheritDoc} */
    @Override
    public void addAudioTrack ()
    {
        this.addChannel (ChannelType.AUDIO, null, null, Collections.emptyList ());
    }


    /** {@inheritDoc} */
    @Override
    public void addEffectTrack ()
    {
        this.addChannel (ChannelType.EFFECT, null, null, Collections.emptyList ());
    }


    /** {@inheritDoc} */
    @Override
    public void addInstrumentTrack ()
    {
        this.addChannel (ChannelType.INSTRUMENT, null, null, Collections.emptyList ());
    }


    /**
     * Create a new channel.
     *
     * @param type The type of the channel
     * @param name The name of the channel, might be null
     * @param color The color of the channel, might be null
     * @param devices An optional list of devices to add to the channel, must not be null
     */
    public void addChannel (final ChannelType type, final String name, final ColorEx color, final List<IDeviceMetadata> devices)
    {
        final List<String> params = new ArrayList<> ();

        final String typeStr;
        switch (type)
        {
            case HYBRID:
            case INSTRUMENT:
                typeStr = "INSTRUMENT";
                break;

            case EFFECT:
                typeStr = "EFFECT";
                break;

            default:
            case AUDIO:
                typeStr = "AUDIO";
                break;
        }

        params.add (typeStr);
        params.add (name == null ? "" : name);

        // Add the color, if any
        final String colorTxt;
        if (color == null)
            colorTxt = "";
        else
        {
            final int [] rgb = color.toIntRGB255 ();
            colorTxt = "RGB(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")";
        }
        params.add (colorTxt);

        for (final IDeviceMetadata device: devices)
            params.add (((DeviceMetadataImpl) device).getCreationName ());

        this.sender.processStringArgs (Processor.TRACK, "addTrack", params.toArray (new String [params.size ()]));
    }


    /** {@inheritDoc} */
    @Override
    public void arrowKeyLeft ()
    {
        // Cursor keys cannot be triggered, therefore let's navigate tracks
        this.sender.invokeAction (Actions.GO_TO_PREV_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void arrowKeyUp ()
    {
        // Cursor keys cannot be triggered, therefore let's navigate tracks
        this.sender.invokeAction (Actions.GO_TO_PREV_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void arrowKeyRight ()
    {
        // Cursor keys cannot be triggered, therefore let's navigate tracks
        this.sender.invokeAction (Actions.GO_TO_NEXT_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void arrowKeyDown ()
    {
        // Cursor keys cannot be triggered, therefore let's navigate tracks
        this.sender.invokeAction (Actions.GO_TO_NEXT_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void zoomOut ()
    {
        this.sender.invokeAction (Actions.ZOOM_OUT_HORIZ);
    }


    /** {@inheritDoc} */
    @Override
    public void zoomIn ()
    {
        this.sender.invokeAction (Actions.ZOOM_IN_HORIZ);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getZoomParameter ()
    {
        return this.horizontalZoomParameter;
    }


    /** {@inheritDoc} */
    @Override
    public void decTrackHeight ()
    {
        this.sender.invokeAction (Actions.ZOOM_OUT_VERT);
    }


    /** {@inheritDoc} */
    @Override
    public void incTrackHeight ()
    {
        this.sender.invokeAction (Actions.ZOOM_IN_VERT);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getTrackHeightParameter ()
    {
        return this.verticalZoomParameter;
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        this.sendKey (KeyEvent.VK_ENTER);
    }


    /** {@inheritDoc} */
    @Override
    public void escape ()
    {
        this.sendKey (KeyEvent.VK_ESCAPE);
    }


    /** {@inheritDoc} */
    @Override
    public void sliceToSampler ()
    {
        this.invokeAction (Actions.DYNAMIC_SPLIT);
    }


    /** {@inheritDoc} */
    @Override
    public void sliceToDrumMachine ()
    {
        this.invokeAction (Actions.DYNAMIC_SPLIT);
    }


    /** {@inheritDoc} */
    @Override
    public void invokeAction (final String id)
    {
        this.sendOSC ("", id);
    }


    /** {@inheritDoc} */
    @Override
    public void showHelp ()
    {
        final Desktop desktop = Desktop.getDesktop ();
        if (desktop.isSupported (Desktop.Action.OPEN))
        {
            final File file = new File ("./docs/DrivenByMoss-Manual.pdf");
            if (file.exists ())
            {
                try
                {
                    desktop.open (file);
                }
                catch (final IOException ex)
                {
                    // Ignore
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.ACTION;
    }


    /**
     * Set the audio engine state.
     *
     * @param active Active or off
     */
    public void setInternalEngineActive (final boolean active)
    {
        this.engineActive = active;
    }


    private void sendKey (final int key)
    {
        if (RobotUtil.exists ())
            RobotUtil.sendKey (key);
        else
            this.host.println ("Sending key presses not supported on this platform.");
    }
}