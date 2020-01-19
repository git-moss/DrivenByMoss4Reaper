// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.data.IDevice;
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
     * @param dataSetup Some configuration variables
     * @param numDevices The number of devices in the page of the bank
     */
    public DeviceBankImpl (final DataSetupEx dataSetup, final int numDevices)
    {
        super (dataSetup, numDevices);
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new DeviceImpl (this.dataSetup, i));
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