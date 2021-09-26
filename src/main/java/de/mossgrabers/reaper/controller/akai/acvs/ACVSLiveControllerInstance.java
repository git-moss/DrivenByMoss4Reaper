// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.akai.acvs;

import de.mossgrabers.controller.akai.acvs.ACVSConfiguration;
import de.mossgrabers.controller.akai.acvs.ACVSControllerDefinition;
import de.mossgrabers.controller.akai.acvs.ACVSControllerSetup;
import de.mossgrabers.controller.akai.acvs.controller.ACVSControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Controller instance for Akai devices supporting the ACVS protocol. Currently, the MPC Live I, II,
 * One, X and Force.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSLiveControllerInstance extends AbstractControllerInstance<ACVSControlSurface, ACVSConfiguration>
{
    /** The controller definition instance. */
    public static final ACVSControllerDefinition CONTROLLER_DEFINITION = new ACVSControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public ACVSLiveControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<ACVSControlSurface, ACVSConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new ACVSControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
