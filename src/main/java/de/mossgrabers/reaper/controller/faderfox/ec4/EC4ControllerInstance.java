// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.faderfox.ec4;

import de.mossgrabers.controller.faderfox.ec4.EC4Configuration;
import de.mossgrabers.controller.faderfox.ec4.EC4ControllerDefinition;
import de.mossgrabers.controller.faderfox.ec4.EC4ControllerSetup;
import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Faderfox EC4 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class EC4ControllerInstance extends AbstractControllerInstance<EC4ControlSurface, EC4Configuration>
{
    /** The controller definition instance. */
    public static final EC4ControllerDefinition CONTROLLER_DEFINITION = new EC4ControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public EC4ControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<EC4ControlSurface, EC4Configuration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new EC4ControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
