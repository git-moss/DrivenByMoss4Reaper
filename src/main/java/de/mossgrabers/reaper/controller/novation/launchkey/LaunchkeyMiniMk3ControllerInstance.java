// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.novation.launchkey;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3ControllerDefinition;
import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3ControllerSetup;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Launchkey Mini Mk3 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMiniMk3ControllerInstance extends AbstractControllerInstance<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    /** The controller definition instance. */
    public static final LaunchkeyMiniMk3ControllerDefinition CONTROLLER_DEFINITION = new LaunchkeyMiniMk3ControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public LaunchkeyMiniMk3ControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new LaunchkeyMiniMk3ControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
