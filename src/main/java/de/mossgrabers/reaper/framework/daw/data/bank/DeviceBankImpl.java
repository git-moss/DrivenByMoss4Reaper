// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.DeviceImpl;


/**
 * Encapsulates the data of a device bank.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceBankImpl extends AbstractBankImpl<IDevice> implements IDeviceBank
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numDevices The number of devices in the page of the bank
     */
    public DeviceBankImpl (final DataSetupEx dataSetup, final int numDevices)
    {
        super (dataSetup, numDevices);

        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new DeviceImpl (this.dataSetup, i));
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.sender.processNoArg (Processor.DEVICE, "page/-");
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.sender.processNoArg (Processor.DEVICE, "page/+");
    }


    /**
     * Sets the number of items.
     *
     * @param itemCount The number of items
     */
    public void setItemCount (final int itemCount)
    {
        this.itemCount = itemCount;
    }
}