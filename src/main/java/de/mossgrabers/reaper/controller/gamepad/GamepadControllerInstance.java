// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.gamepad;

import de.mossgrabers.controller.gamepad.GamepadConfiguration;
import de.mossgrabers.controller.gamepad.GamepadControllerDefinition;
import de.mossgrabers.controller.gamepad.GamepadControllerSetup;
import de.mossgrabers.controller.gamepad.controller.GamepadControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * The Gamepad controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class GamepadControllerInstance extends AbstractControllerInstance<GamepadControlSurface, GamepadConfiguration>
{
    /** The controller definition instance. */
    public static final GamepadControllerDefinition CONTROLLER_DEFINITION = new GamepadControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public GamepadControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<GamepadControlSurface, GamepadConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new GamepadControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
