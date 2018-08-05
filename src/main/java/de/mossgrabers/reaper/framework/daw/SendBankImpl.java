// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.reaper.framework.daw.data.SendImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Encapsulates the data of a send bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendBankImpl extends AbstractBankImpl<ISend> implements ISendBank
{
    private final int trackIndex;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param trackIndex The index of the track to which the send bank belongs
     * @param numSends The number of sends in the page of the bank
     */
    public SendBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int trackIndex, final int numSends)
    {
        super (host, sender, valueChanger, numSends);
        this.trackIndex = trackIndex;
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new SendImpl (this.host, this.sender, this.valueChanger, this.trackIndex, i));
    }
}