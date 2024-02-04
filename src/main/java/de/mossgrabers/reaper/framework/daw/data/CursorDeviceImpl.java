// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.bank.DeviceBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.ParameterBankImpl;


/**
 * Proxy to the Reaper Cursor device.
 *
 * @author Jürgen Moßgraber
 */
public class CursorDeviceImpl extends SpecificDeviceImpl implements ICursorDevice
{
    private int               deviceCount = 0;
    private final IDeviceBank deviceBank;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numSends The number of sends
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public CursorDeviceImpl (final DataSetupEx dataSetup, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (dataSetup, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

        final int checkedNumDevices = numDevicesInBank >= 0 ? numDevicesInBank : 8;
        this.deviceBank = new DeviceBankImpl (dataSetup, checkedNumDevices);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.getPosition () % this.deviceBank.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public IDeviceBank getDeviceBank ()
    {
        return this.deviceBank;
    }


    /**
     * Set the number of devices of the channel.
     *
     * @param deviceCount The number of devices
     */
    public void setDeviceCount (final int deviceCount)
    {
        this.deviceCount = deviceCount;
        ((DeviceBankImpl) this.getDeviceBank ()).setItemCount (deviceCount);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectPrevious ()
    {
        return this.getPosition () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectNext ()
    {
        return this.getPosition () < this.deviceCount - 1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPinned ()
    {
        // There is no selected device state in Reaper, therefore this is always pinned
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setPinned (final boolean isPinned)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void selectPrevious ()
    {
        if (!this.canSelectPrevious ())
            return;

        // To support displaying the newly selected device quickly
        final int index = this.getIndex ();
        if (index > 0)
            this.setName (this.deviceBank.getItem (index - 1).getName ());
        this.sender.processNoArg (Processor.DEVICE, "-");
    }


    /** {@inheritDoc} */
    @Override
    public void selectNext ()
    {
        if (!this.canSelectNext ())
            return;

        // To support displaying the newly selected device quickly
        final int index = this.getIndex ();
        if (index < this.deviceBank.getPageSize () - 1)
        {
            final IDevice item = this.deviceBank.getItem (index + 1);
            this.setName (item.getName ());
        }
        this.sender.processNoArg (Processor.DEVICE, "+");
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void selectChannel ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void swapWithPrevious ()
    {
        this.sendDeviceOSC ("movePrev");
        this.deviceBank.getItem (Math.max (0, this.getIndex () - 1)).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void swapWithNext ()
    {
        this.sendDeviceOSC ("moveNext");
        this.deviceBank.getItem (Math.min (this.deviceBank.getPageSize () - 1, this.getIndex () + 1)).select ();
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSlotChains ()
    {
        // Not supported
        return new String [0];
    }


    /** {@inheritDoc} */
    @Override
    public void selectSlotChain (final String slotChainName)
    {
        // Not supported
    }


    /**
     * Refresh the parameter mapping.
     */
    public void refreshParameterMapping ()
    {
        ((ParameterBankImpl) this.getParameterBank ()).refreshParameterCache ();
    }
}