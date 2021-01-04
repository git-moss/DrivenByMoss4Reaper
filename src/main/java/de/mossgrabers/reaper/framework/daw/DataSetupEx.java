// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DataSetup;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * Configuration parameters for DAW objects.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DataSetupEx extends DataSetup
{
    private final MessageSender sender;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param colorManager The color manager
     * @param sender The OSC sender
     */
    public DataSetupEx (final IHost host, final IValueChanger valueChanger, final ColorManager colorManager, final MessageSender sender)
    {
        super (host, valueChanger, colorManager);

        this.sender = sender;
    }


    /**
     * Get the sender.
     *
     * @return The sender
     */
    public MessageSender getSender ()
    {
        return this.sender;
    }
}
