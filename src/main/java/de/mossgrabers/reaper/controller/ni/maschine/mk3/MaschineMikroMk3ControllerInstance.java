// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.ni.maschine.mk3;

import de.mossgrabers.controller.ni.maschine.Maschine;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineControllerSetup;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineMikroMk3ControllerDefinition;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Maschine Mikro Mk3 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineMikroMk3ControllerInstance extends AbstractControllerInstance<MaschineControlSurface, MaschineConfiguration>
{
    /** The controller definition instance. */
    public static final MaschineMikroMk3ControllerDefinition CONTROLLER_DEFINITION = new MaschineMikroMk3ControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public MaschineMikroMk3ControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<MaschineControlSurface, MaschineConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new MaschineControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, Maschine.MIKRO_MK3);
    }
}
