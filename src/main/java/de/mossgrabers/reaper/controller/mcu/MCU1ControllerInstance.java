// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.mcu;

import de.mossgrabers.controller.mcu.MCUControllerDefinition;
import de.mossgrabers.controller.mcu.MCUControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.util.LogModel;

import javafx.stage.Window;


/**
 * MCU without extender controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCU1ControllerInstance extends AbstractControllerInstance
{
    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param window The owner window for the configuration dialog
     * @param sender The sender
     */
    public MCU1ControllerInstance (final LogModel logModel, final Window window, final MessageSender sender)
    {
        super (new MCUControllerDefinition (0), logModel, window, sender);
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
        return new MCUControllerSetup (this.host, setupFactory, this.settingsUI, 1);
    }
}
