// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.akai.apcmini;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.APCminiControllerSetup;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.akai.apcmini.definition.APCminiMk1ControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * APCmini Mk1 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiMk1ControllerInstance extends AbstractControllerInstance<APCminiControlSurface, APCminiConfiguration>
{
    /** The controller definition instance. */
    public static final APCminiMk1ControllerDefinition CONTROLLER_DEFINITION = new APCminiMk1ControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public APCminiMk1ControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<APCminiControlSurface, APCminiConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new APCminiControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, CONTROLLER_DEFINITION);
    }
}
