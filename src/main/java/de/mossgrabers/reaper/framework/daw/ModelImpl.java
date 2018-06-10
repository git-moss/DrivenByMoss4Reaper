// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractModel;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.reaper.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelImpl extends AbstractModel
{
    private MessageSender sender;


    /**
     * Constructor.
     *
     * @param sender The OSC sender
     * @param host The DAW host
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales object
     * @param numTracks The number of track to monitor (per track bank)
     * @param numScenes The number of scenes to monitor (per scene bank)
     * @param numSends The number of sends to monitor
     * @param numFilterColumnEntries The number of entries in one filter column to monitor
     * @param numResults The number of search results in the browser to monitor
     * @param hasFlatTrackList Don"t navigate groups, all tracks are flat
     * @param numParams The number of parameter of a device to monitor
     * @param numDevicesInBank The number of devices to monitor
     * @param numDeviceLayers The number of device layers to monitor
     * @param numDrumPadLayers The number of drum pad layers to monitor
     */
    public ModelImpl (final MessageSender sender, final IHost host, final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final int numTracks, final int numScenes, final int numSends, final int numFilterColumnEntries, final int numResults, final boolean hasFlatTrackList, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (colorManager, valueChanger, scales, numTracks, numScenes, numSends, numFilterColumnEntries, numResults, hasFlatTrackList, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

        this.sender = sender;
        this.host = host;

        this.trackBank = new TrackBankImpl (host, sender, valueChanger, this.numTracks, this.numScenes, this.numSends, this.hasFlatTrackList);
        this.effectTrackBank = null;
        this.masterTrack = new MasterTrackImpl (host, sender, valueChanger);

        this.primaryDevice = new CursorDeviceImpl (sender, host, valueChanger, this.numSends, this.numParams, this.numDevicesInBank, this.numDeviceLayers, this.numDrumPadLayers);
        this.cursorDevice = new CursorDeviceImpl (sender, host, valueChanger, this.numSends, this.numParams, this.numDevicesInBank, this.numDeviceLayers, this.numDrumPadLayers);
        if (this.numDrumPadLayers > 0)
            this.drumDevice64 = new CursorDeviceImpl (sender, host, valueChanger, 0, 0, 0, 64, 64);
        if (this.numResults > 0)
            this.browser = new BrowserImpl (sender, this.cursorDevice, this.numFilterColumnEntries, this.numResults);

        this.application = new ApplicationImpl (sender, host);
        this.arranger = new ArrangerImpl ();
        this.mixer = new MixerImpl (sender, host);
        this.project = new ProjectImpl (sender);
        this.transport = new TransportImpl (sender, host, this.trackBank, valueChanger);

        this.groove = new GrooveImpl (sender, host, valueChanger);

        this.currentTrackBank = this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank createSceneViewTrackBank (final int numTracks, final int numScenes)
    {
        // Not supported
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorClip getCursorClip (final int cols, final int rows)
    {
        return this.cursorClips.computeIfAbsent (cols + "-" + rows, k -> new CursorClipImpl (this.transport, this.sender, this.valueChanger, cols, rows));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorTrackPinned ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCursorTrackPinned ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorDeviceOnMasterTrack ()
    {
        return this.getMasterTrack ().isSelected ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canConvertClip ()
    {
        return true;
    }

}