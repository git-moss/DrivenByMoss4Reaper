// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.electra.one;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.ElectraOneControllerDefinition;
import de.mossgrabers.controller.electra.one.ElectraOneControllerSetup;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Electra.One controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneControllerInstance extends AbstractControllerInstance<ElectraOneControlSurface, ElectraOneConfiguration>
{
    /** The controller definition instance. */
    public static final ElectraOneControllerDefinition CONTROLLER_DEFINITION = new ElectraOneControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public ElectraOneControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ElectraOneControlSurface, ElectraOneConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new ElectraOneControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
