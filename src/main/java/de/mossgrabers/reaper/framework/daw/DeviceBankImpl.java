// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.data.DeviceImpl;


/**
 * Encapsulates the data of a device bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceBankImpl extends AbstractBankImpl<IDevice> implements IDeviceBank
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numDevices The number of devices in the page of the bank
     */
    public DeviceBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numDevices)
    {
        super (host, sender, valueChanger, numDevices);
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new DeviceImpl (this.host, this.sender, i));
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.sender.processNoArg ("device", "page/-");
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.sender.processNoArg ("device", "page/+");
    }
}