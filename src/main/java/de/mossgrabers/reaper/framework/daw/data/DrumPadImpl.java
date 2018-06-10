// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadImpl extends ChannelImpl implements IDrumPad
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public DrumPadImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int index, final int numSends)
    {
        super (host, sender, valueChanger, index, numSends);
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsert ()
    {
        // Intentionally empty
    }
}
