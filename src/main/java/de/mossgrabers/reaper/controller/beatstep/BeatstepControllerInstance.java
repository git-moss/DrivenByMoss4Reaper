// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.beatstep;

import de.mossgrabers.controller.beatstep.BeatstepControllerDefinition;
import de.mossgrabers.controller.beatstep.BeatstepControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Arturia Beatstep controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepControllerInstance extends AbstractControllerInstance
{
    /** The controller definition instance. */
    public static final BeatstepControllerDefinition CONTROLLER_DEFINITION = new BeatstepControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public BeatstepControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<?, ?> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new BeatstepControllerSetup (this.host, setupFactory, this.settingsUI, this.settingsUI);
    }
}
