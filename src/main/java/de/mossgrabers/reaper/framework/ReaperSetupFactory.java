// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework;

import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.reaper.framework.daw.ModelImpl;
import de.mossgrabers.reaper.framework.midi.MidiAccessImpl;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.util.LogModel;

import javax.sound.midi.MidiDevice;


/**
 * Factory for creating Reaper objects.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ReaperSetupFactory implements ISetupFactory
{
    private final MessageSender  sender;
    private final IHost          host;
    private final MidiAccessImpl midiAccess;
    private final IniFiles       iniFiles;


    /**
     * Constructor.
     *
     * @param iniFiles The INI configuration files
     * @param sender The OSC sender
     * @param host The DAW host
     * @param logModel The logging model
     * @param inputs The midi input devices
     * @param outputs The midi output devices
     */
    public ReaperSetupFactory (final IniFiles iniFiles, final MessageSender sender, final IHost host, final LogModel logModel, final MidiDevice [] inputs, final MidiDevice [] outputs)
    {
        this.iniFiles = iniFiles;
        this.sender = sender;
        this.host = host;
        this.midiAccess = new MidiAccessImpl (logModel, this.host, this.sender, inputs, outputs);
    }


    /**
     * Cleanup all midi connections.
     */
    public void cleanup ()
    {
        this.midiAccess.cleanup ();
    }


    /** {@inheritDoc} */
    @Override
    public IModel createModel (ColorManager colorManager, IValueChanger valueChanger, Scales scales, int numTracks, int numScenes, int numSends, int numFilterColumnEntries, int numResults, boolean hasFlatTrackList, int numParams, int numDevicesInBank, int numDeviceLayers, int numDrumPadLayers, int numMarkers)
    {
        return new ModelImpl (this.iniFiles, this.sender, this.host, colorManager, valueChanger, scales, numTracks, numScenes, numSends, numFilterColumnEntries, numResults, hasFlatTrackList, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers, numMarkers);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiAccess createMidiAccess ()
    {
        return this.midiAccess;
    }
}
