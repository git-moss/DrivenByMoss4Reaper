// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.generic;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.GenericFlexiControllerDefinition;
import de.mossgrabers.controller.generic.GenericFlexiControllerSetup;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * The Generic Flexi controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class GenericFlexiControllerInstance extends AbstractControllerInstance<GenericFlexiControlSurface, GenericFlexiConfiguration>
{
    /** The controller definition instance. */
    public static final GenericFlexiControllerDefinition CONTROLLER_DEFINITION = new GenericFlexiControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public GenericFlexiControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<GenericFlexiControlSurface, GenericFlexiConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new GenericFlexiControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
