// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.akai.apc;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.APCControllerDefinition;
import de.mossgrabers.controller.akai.apc.APCControllerSetup;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * APC40 mkII controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class APC40mkIIControllerInstance extends AbstractControllerInstance<APCControlSurface, APCConfiguration>
{
    /** The controller definition instance. */
    public static final APCControllerDefinition CONTROLLER_DEFINITION = new APCControllerDefinition (true);


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public APC40mkIIControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<APCControlSurface, APCConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new APCControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, true);
    }
}
