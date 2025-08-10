// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.akai.fire;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.FireControllerDefinition;
import de.mossgrabers.controller.akai.fire.FireControllerSetup;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Akai Fire controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class FireControllerInstance extends AbstractControllerInstance<FireControlSurface, FireConfiguration>
{
    /** The controller definition instance. */
    public static final FireControllerDefinition CONTROLLER_DEFINITION = new FireControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public FireControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<FireControlSurface, FireConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new FireControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
