// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.kontrol.mki;

import de.mossgrabers.controller.kontrol.mki.Kontrol1ControllerDefinition;
import de.mossgrabers.controller.kontrol.mki.Kontrol1ControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Komplete Kontrol S61 controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIS61ControllerInstance extends AbstractControllerInstance
{
    /** The controller definition instance. */
    public static final Kontrol1ControllerDefinition CONTROLLER_DEFINITION = new Kontrol1ControllerDefinition (2);


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public KontrolMkIS61ControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<?, ?> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new Kontrol1ControllerSetup (2, this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
