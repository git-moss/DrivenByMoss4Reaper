// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptySend;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.SendImpl;


/**
 * Encapsulates the data of a send bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendBankImpl extends AbstractPagedBankImpl<SendImpl, ISend> implements ISendBank
{
    private final IChannel channel;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param channel The track to which the send bank belongs
     * @param numSends The number of sends in the page of the bank
     */
    public SendBankImpl (final DataSetupEx dataSetup, final IChannel channel, final int numSends)
    {
        super (dataSetup, numSends, EmptySend.INSTANCE);

        this.channel = channel;
    }


    /** {@inheritDoc}} */
    @Override
    protected SendImpl createItem (final int position)
    {
        final SendImpl send = new SendImpl (this.dataSetup, this.channel, position % this.pageSize);
        send.setPosition (position);
        return send;
    }
}