// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.mackie.hui;

import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.HUIControllerDefinition;
import de.mossgrabers.controller.mackie.hui.HUIControllerSetup;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * HUI with 2 extenders.
 *
 * @author Jürgen Moßgraber
 */
public class HUI3ControllerInstance extends AbstractControllerInstance<HUIControlSurface, HUIConfiguration>
{
    /** The controller definition instance. */
    public static final HUIControllerDefinition CONTROLLER_DEFINITION = new HUIControllerDefinition (2);


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public HUI3ControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<HUIControlSurface, HUIConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new HUIControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, 3);
    }
}
