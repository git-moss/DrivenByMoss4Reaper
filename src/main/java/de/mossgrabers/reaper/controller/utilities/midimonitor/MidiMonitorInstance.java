// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.utilities.midimonitor;

import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorConfiguration;
import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorDefinition;
import de.mossgrabers.controller.utilities.midimonitor.MidiMonitorSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * The Midi Monitor instance.
 *
 * @author Jürgen Moßgraber
 */
public class MidiMonitorInstance extends AbstractControllerInstance<IControlSurface<MidiMonitorConfiguration>, MidiMonitorConfiguration>
{
    /** The controller definition instance. */
    public static final MidiMonitorDefinition CONTROLLER_DEFINITION = new MidiMonitorDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public MidiMonitorInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<IControlSurface<MidiMonitorConfiguration>, MidiMonitorConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new MidiMonitorSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
