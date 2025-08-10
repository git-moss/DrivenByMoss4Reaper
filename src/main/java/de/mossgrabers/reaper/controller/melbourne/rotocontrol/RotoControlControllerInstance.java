// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.melbourne.rotocontrol;

import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlControllerDefinition;
import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlControllerSetup;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Melbourne Instruments ROTO CONTROL controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlControllerInstance extends AbstractControllerInstance<RotoControlControlSurface, RotoControlConfiguration>
{
    /** The controller definition instance. */
    public static final RotoControlControllerDefinition CONTROLLER_DEFINITION = new RotoControlControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public RotoControlControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<RotoControlControlSurface, RotoControlConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new RotoControlControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
