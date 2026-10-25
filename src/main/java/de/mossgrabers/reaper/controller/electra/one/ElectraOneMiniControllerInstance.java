// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.electra.one;

import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniConfiguration;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniControllerDefinition;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniControllerSetup;
import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Electra.One Mini controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniControllerInstance extends AbstractControllerInstance<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration>
{
    /** The controller definition instance. */
    public static final ElectraOneMiniControllerDefinition CONTROLLER_DEFINITION = new ElectraOneMiniControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public ElectraOneMiniControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new ElectraOneMiniControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
