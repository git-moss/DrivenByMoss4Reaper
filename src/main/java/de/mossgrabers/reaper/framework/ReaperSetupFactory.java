// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.ModelImpl;
import de.mossgrabers.reaper.framework.midi.MidiAccessImpl;
import de.mossgrabers.reaper.framework.midi.NoteRepeatImpl;
import de.mossgrabers.reaper.ui.utils.LogModel;

import javax.sound.midi.MidiDevice;

import java.util.List;


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
     * @param inputs The MIDI input devices
     * @param outputs The MIDI output devices
     */
    public ReaperSetupFactory (final IniFiles iniFiles, final MessageSender sender, final IHost host, final LogModel logModel, final MidiDevice [] inputs, final MidiDevice [] outputs)
    {
        this.iniFiles = iniFiles;
        this.sender = sender;
        this.host = host;
        this.midiAccess = new MidiAccessImpl (logModel, this.host, this.sender, inputs, outputs);
    }


    /**
     * Cleanup all MIDI connections.
     */
    public void cleanup ()
    {
        this.midiAccess.cleanup ();
    }


    /** {@inheritDoc} */
    @Override
    public IModel createModel (final Configuration configuration, final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final ModelSetup modelSetup)
    {
        final DataSetupEx dataSetup = new DataSetupEx (this.host, valueChanger, colorManager, this.sender);
        return new ModelImpl (configuration, modelSetup, dataSetup, scales, this.iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    public IMidiAccess createMidiAccess ()
    {
        return this.midiAccess;
    }


    /** {@inheritDoc} */
    @Override
    public List<ArpeggiatorMode> getArpeggiatorModes ()
    {
        return NoteRepeatImpl.ARP_MODES;
    }

}
