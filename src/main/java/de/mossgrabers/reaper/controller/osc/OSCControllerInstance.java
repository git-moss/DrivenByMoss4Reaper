// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.osc;

import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.OSCControllerDefinition;
import de.mossgrabers.controller.osc.OSCControllerSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Open Sound Control (OSC) instance.
 *
 * @author Jürgen Moßgraber
 */
public class OSCControllerInstance extends AbstractControllerInstance<IControlSurface<OSCConfiguration>, OSCConfiguration>
{
    /** The controller definition instance. */
    public static final OSCControllerDefinition CONTROLLER_DEFINITION = new OSCControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public OSCControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<IControlSurface<OSCConfiguration>, OSCConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new OSCControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
