// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.beatstep;

import de.mossgrabers.controller.beatstep.BeatstepControllerDefinition;
import de.mossgrabers.controller.beatstep.BeatstepControllerSetup;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.util.LogModel;

import javafx.stage.Window;


/**
 * Arturia Beatstep Pro controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepProControllerInstance extends AbstractControllerInstance
{
    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param window The owner window for the configuration dialog
     * @param sender The sender
     */
    public BeatstepProControllerInstance (final LogModel logModel, final Window window, final MessageSender sender)
    {
        super (new BeatstepControllerDefinition (true), logModel, window, sender);
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
        return new BeatstepControllerSetup (this.host, setupFactory, this.settingsUI, true);
    }
}
