// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.utilities;

import de.mossgrabers.controller.utilities.UtilitiesDefinition;
import de.mossgrabers.controller.utilities.UtilitiesSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.utils.LogModel;

import java.awt.Window;


/**
 * Utilities instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UtilitiesInstance extends AbstractControllerInstance
{
    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param window The owner window for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public UtilitiesInstance (final LogModel logModel, final Window window, final MessageSender sender, final IniFiles iniFiles)
    {
        super (new UtilitiesDefinition (), logModel, window, sender, iniFiles);
    }


    /**
     * Create a controller setup instance.
     *
     * @param setupFactory The setup factory
     * @return The controller setup
     */
    @Override
    protected IControllerSetup createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new UtilitiesSetup (this.host, setupFactory, this.settingsUI);
    }
}
