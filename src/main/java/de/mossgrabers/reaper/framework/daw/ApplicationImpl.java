// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Proxy to the Application object.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ApplicationImpl extends BaseImpl implements IApplication
{
    private String  panelLayout  = IApplication.PANEL_LAYOUT_ARRANGE;
    private boolean engineActive = true;


    /**
     * Constructor.
     *
     * @param sender The OSC sender
     * @param host The DAW host
     */
    public ApplicationImpl (final MessageSender sender, final IHost host)
    {
        super (sender, host);
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
        this.sender.sendOSC ("/project/engine", Integer.valueOf (active ? 1 : 0));
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
            case "ARRANGE":
                this.sender.invokeAction (Actions.LOAD_WINDOW_SET_1);
                break;
            case "MIX":
                this.sender.invokeAction (Actions.LOAD_WINDOW_SET_2);
                break;
            case "EDIT":
                this.sender.invokeAction (Actions.LOAD_WINDOW_SET_3);
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
    public boolean isArrangeLayout ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMixerLayout ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEditLayout ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlayLayout ()
    {
        return false;
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
        this.sender.invokeAction (Actions.SHOW_FX_CHAIN_WINDOWS);
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
    public void redo ()
    {
        this.sender.sendOSC ("/redo", null);
    }


    /** {@inheritDoc} */
    @Override
    public void undo ()
    {
        this.sender.sendOSC ("/undo", null);
    }


    /** {@inheritDoc} */
    @Override
    public void addAudioTrack ()
    {
        this.sender.invokeAction (Actions.INSERT_NEW_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void addEffectTrack ()
    {
        this.sender.invokeAction (Actions.INSERT_NEW_TRACK_AT_END);
    }


    /** {@inheritDoc} */
    @Override
    public void addInstrumentTrack ()
    {
        this.sender.invokeAction (Actions.INSERT_NEW_TRACK_FROM_TEMPLATE);
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
    public void enter ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void escape ()
    {
        // Not supported
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
}