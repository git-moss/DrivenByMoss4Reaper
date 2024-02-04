// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.yaeltex.turn;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnControllerDefinition;
import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnControllerSetup;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Yaeltex Turn controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class YaeltexTurnControllerInstance extends AbstractControllerInstance<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    /** The controller definition instance. */
    public static final YaeltexTurnControllerDefinition CONTROLLER_DEFINITION = new YaeltexTurnControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public YaeltexTurnControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<YaeltexTurnControlSurface, YaeltexTurnConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new YaeltexTurnControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
