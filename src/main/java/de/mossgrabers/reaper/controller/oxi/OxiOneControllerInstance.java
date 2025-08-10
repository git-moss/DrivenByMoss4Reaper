// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.oxi;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.OxiOneControllerDefinition;
import de.mossgrabers.controller.oxi.one.OxiOneControllerSetup;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * OXI One controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneControllerInstance extends AbstractControllerInstance<OxiOneControlSurface, OxiOneConfiguration>
{
    /** The controller definition instance. */
    public static final OxiOneControllerDefinition CONTROLLER_DEFINITION = new OxiOneControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public OxiOneControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<OxiOneControlSurface, OxiOneConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new OxiOneControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
