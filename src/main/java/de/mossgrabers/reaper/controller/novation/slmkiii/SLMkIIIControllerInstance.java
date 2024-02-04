// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.novation.slmkiii;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.SLMkIIIControllerDefinition;
import de.mossgrabers.controller.novation.slmkiii.SLMkIIIControllerSetup;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
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
 * @author Jürgen Moßgraber
 */
public class SLMkIIIControllerInstance extends AbstractControllerInstance<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    /** The controller definition instance. */
    public static final SLMkIIIControllerDefinition CONTROLLER_DEFINITION = new SLMkIIIControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public SLMkIIIControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<SLMkIIIControlSurface, SLMkIIIConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new SLMkIIIControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
