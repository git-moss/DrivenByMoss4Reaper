// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.intuitiveinstruments.exquis;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisControllerDefinition;
import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisControllerSetup;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Intuitive Instruments Exquis controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisControllerInstance extends AbstractControllerInstance<ExquisControlSurface, ExquisConfiguration>
{
    /** The controller definition instance. */
    public static final ExquisControllerDefinition CONTROLLER_DEFINITION = new ExquisControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public ExquisControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ExquisControlSurface, ExquisConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new ExquisControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
