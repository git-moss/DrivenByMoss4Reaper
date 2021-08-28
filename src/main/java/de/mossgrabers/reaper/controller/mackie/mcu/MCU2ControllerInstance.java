// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.mackie.mcu;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.MCUControllerDefinition;
import de.mossgrabers.controller.mackie.mcu.MCUControllerSetup;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * MCU with 1 extender controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCU2ControllerInstance extends AbstractControllerInstance<MCUControlSurface, MCUConfiguration>
{
    /** The controller definition instance. */
    public static final MCUControllerDefinition CONTROLLER_DEFINITION = new MCUControllerDefinition (1);


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public MCU2ControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<MCUControlSurface, MCUConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new MCUControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, 2);
    }
}
