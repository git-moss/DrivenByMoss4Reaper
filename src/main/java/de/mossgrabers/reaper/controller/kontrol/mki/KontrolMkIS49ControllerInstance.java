// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.kontrol.mki;

import de.mossgrabers.controller.kontrol.mki.Kontrol1ControllerDefinition;
import de.mossgrabers.controller.kontrol.mki.Kontrol1ControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.utils.LogModel;

import java.awt.Window;


/**
 * Komplete Kontrol S49 controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIS49ControllerInstance extends AbstractControllerInstance
{
    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param window The owner window for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public KontrolMkIS49ControllerInstance (final LogModel logModel, final Window window, final MessageSender sender, final IniFiles iniFiles)
    {
        super (new Kontrol1ControllerDefinition (1), logModel, window, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<?, ?> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new Kontrol1ControllerSetup (1, this.host, setupFactory, this.settingsUI, this.settingsUI);
    }
}
