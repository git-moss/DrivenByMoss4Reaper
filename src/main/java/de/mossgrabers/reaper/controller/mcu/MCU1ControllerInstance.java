// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.mcu;

import de.mossgrabers.controller.mcu.MCUControllerDefinition;
import de.mossgrabers.controller.mcu.MCUControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * MCU without extender controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCU1ControllerInstance extends AbstractControllerInstance
{
    /** The controller definition instance. */
    public static final MCUControllerDefinition CONTROLLER_DEFINITION = new MCUControllerDefinition (0);


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public MCU1ControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<?, ?> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new MCUControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, 1);
    }
}
