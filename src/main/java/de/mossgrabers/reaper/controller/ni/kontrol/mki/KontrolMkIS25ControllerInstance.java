// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.ni.kontrol.mki;

import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1ControllerDefinition;
import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1ControllerSetup;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Komplete Kontrol S25 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolMkIS25ControllerInstance extends AbstractControllerInstance<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /** The controller definition instance. */
    public static final Kontrol1ControllerDefinition CONTROLLER_DEFINITION = new Kontrol1ControllerDefinition (0);


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public KontrolMkIS25ControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<Kontrol1ControlSurface, Kontrol1Configuration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new Kontrol1ControllerSetup (0, this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
