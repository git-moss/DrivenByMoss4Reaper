// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.midimonitor;

import de.mossgrabers.controller.midimonitor.MidiMonitorDefinition;
import de.mossgrabers.controller.midimonitor.MidiMonitorSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.utils.LogModel;

import java.awt.Window;


/**
 * The Midi Monitor instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiMonitorInstance extends AbstractControllerInstance
{
    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param window The owner window for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public MidiMonitorInstance (final LogModel logModel, final Window window, final MessageSender sender, final IniFiles iniFiles)
    {
        super (new MidiMonitorDefinition (), logModel, window, sender, iniFiles);
    }


    /**
     * Create a controller setup instance.
     *
     * @param setupFactory The setup factory
     * @return The controller setup
     */
    @Override
    protected IControllerSetup createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new MidiMonitorSetup (this.host, setupFactory, this.settingsUI);
    }
}
