// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.ableton.push;

import de.mossgrabers.controller.ableton.push.Push1ControllerDefinition;
import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushControllerSetup;
import de.mossgrabers.controller.ableton.push.PushVersion;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Push 1 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class Push1ControllerInstance extends AbstractControllerInstance<PushControlSurface, PushConfiguration>
{
    /** The controller definition instance. */
    public static final Push1ControllerDefinition CONTROLLER_DEFINITION = new Push1ControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public Push1ControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<PushControlSurface, PushConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new PushControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, PushVersion.VERSION_1);
    }
}
