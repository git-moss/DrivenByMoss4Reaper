// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.novation.launchpad;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.LaunchpadControllerSetup;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.definition.LaunchpadProMk3ControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Launchpad Pro Mk3 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadProMk3ControllerInstance extends AbstractControllerInstance<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /** The controller definition instance. */
    public static final LaunchpadProMk3ControllerDefinition CONTROLLER_DEFINITION = new LaunchpadProMk3ControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public LaunchpadProMk3ControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchpadControlSurface, LaunchpadConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new LaunchpadControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, CONTROLLER_DEFINITION);
    }
}
