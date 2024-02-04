// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.novation.launchcontrol;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLControllerDefinition;
import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLControllerSetup;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * LaunchControl XL controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchControlXLControllerInstance extends AbstractControllerInstance<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    /** The controller definition instance. */
    public static final LaunchControlXLControllerDefinition CONTROLLER_DEFINITION = new LaunchControlXLControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public LaunchControlXLControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<LaunchControlXLControlSurface, LaunchControlXLConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new LaunchControlXLControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
