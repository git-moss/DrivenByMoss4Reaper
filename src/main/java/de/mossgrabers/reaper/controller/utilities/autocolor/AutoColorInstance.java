// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.utilities.autocolor;

import de.mossgrabers.controller.utilities.autocolor.AutoColorConfiguration;
import de.mossgrabers.controller.utilities.autocolor.AutoColorDefinition;
import de.mossgrabers.controller.utilities.autocolor.AutoColorSetup;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * The Auto Color instance.
 *
 * @author Jürgen Moßgraber
 */
public class AutoColorInstance extends AbstractControllerInstance<IControlSurface<AutoColorConfiguration>, AutoColorConfiguration>
{
    /** The controller definition instance. */
    public static final AutoColorDefinition CONTROLLER_DEFINITION = new AutoColorDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public AutoColorInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<IControlSurface<AutoColorConfiguration>, AutoColorConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new AutoColorSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
