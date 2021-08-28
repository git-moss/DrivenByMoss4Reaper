// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.novation.sl;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.SLControllerDefinition;
import de.mossgrabers.controller.novation.sl.SLControllerSetup;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Novation Remote SL mkI controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIControllerInstance extends AbstractControllerInstance<SLControlSurface, SLConfiguration>
{
    /** The controller definition instance. */
    public static final SLControllerDefinition CONTROLLER_DEFINITION = new SLControllerDefinition (true);


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public SLMkIIControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<SLControlSurface, SLConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new SLControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, true);
    }
}
