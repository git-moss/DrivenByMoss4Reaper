// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.usb;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.IUsbEndpoint;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.usb.UsbMatcher.EndpointMatcher;

import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Implementation for an USB device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UsbDeviceImpl implements IUsbDevice
{
    private IHost                 host;
    private DeviceHandle          handle;
    private UsbMatcher            usbMatcher;
    private List<Byte>            interfaces    = new ArrayList<> ();
    private List<UsbEndpointImpl> endpointCache = new ArrayList<> ();
    private HidDeviceImpl         hidDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param usbMatcher The device info
     */
    public UsbDeviceImpl (final IHost host, final UsbMatcher usbMatcher)
    {
        this.host = host;
        this.usbMatcher = usbMatcher;

        if (usbMatcher.isEnableHID ())
            this.hidDevice = new HidDeviceImpl (this.usbMatcher.getVendor (), this.usbMatcher.getProductID ());

        this.handle = openDeviceWithVidPid (usbMatcher.getVendor (), usbMatcher.getProductID ());
        if (this.handle == null)
            host.error ("USB Device not found.", new LibUsbException (LibUsb.ERROR_NO_DEVICE));
    }


    /** {@inheritDoc} */
    @Override
    public void release ()
    {
        try
        {
            if (this.hidDevice != null)
                this.hidDevice.close ();
        }
        finally
        {
            if (this.handle == null)
                return;

            // Prevent further sending
            final DeviceHandle h = this.handle;
            this.handle = null;

            this.endpointCache.forEach (UsbEndpointImpl::clear);

            for (final Byte interf: this.interfaces)
            {
                final int result = LibUsb.releaseInterface (h, interf.byteValue ());
                if (result != LibUsb.SUCCESS)
                    this.host.error ("Unable to release interface", new LibUsbException (result));
            }

            LibUsb.close (h);
        }
    }


    /**
     * Find the device with the given vendor and product ID and open it.
     *
     * @param vendorId The vendor ID to look for
     * @param productId The product ID to look for
     * @return The device handle of the device or null if not found
     */
    private static DeviceHandle openDeviceWithVidPid (final short vendorId, final short productId)
    {
        final DeviceList list = new DeviceList ();
        int result = LibUsb.getDeviceList (null, list);
        if (result < LibUsb.SUCCESS)
            throw new LibUsbException ("Unable to get device list.", result);

        try
        {
            final Iterator<Device> iterator = list.iterator ();
            LibUsbException ex = null;
            while (iterator.hasNext ())
            {
                final Device device = iterator.next ();
                final DeviceDescriptor descriptor = new DeviceDescriptor ();
                result = LibUsb.getDeviceDescriptor (device, descriptor);
                if (result != LibUsb.SUCCESS)
                {
                    ex = new LibUsbException ("Unable to read device descriptor.", result);
                    // Continue, maybe there is a working device
                    continue;
                }
                if (descriptor.idVendor () == vendorId && descriptor.idProduct () == productId)
                {
                    final DeviceHandle handle = new DeviceHandle ();
                    result = LibUsb.open (device, handle);
                    if (result != LibUsb.SUCCESS)
                    {
                        ex = new LibUsbException ("Unable to read device descriptor.", result);
                        // Continue, maybe there is a working device
                        continue;
                    }
                    return handle;
                }
            }

            if (ex != null)
                throw ex;
        }
        finally
        {
            LibUsb.freeDeviceList (list, true);
        }

        return null;
    }


    /** {@inheritDoc} */
    @Override
    public IUsbEndpoint getEndpoint (final int interfaceIndex, final int endpointIndex)
    {
        if (this.handle == null)
            return null;

        final List<EndpointMatcher> endpoints = this.usbMatcher.getEndpoints ();
        final EndpointMatcher endpointMatcher = endpoints.get (interfaceIndex);
        final byte interfaceNumber = endpointMatcher.getInterfaceNumber ();

        // Only claim interface once
        if (!this.interfaces.contains (Byte.valueOf (interfaceNumber)))
        {
            int result = LibUsb.setAutoDetachKernelDriver (this.handle, true);
            if (result != LibUsb.SUCCESS && result != LibUsb.ERROR_NOT_SUPPORTED)
                throw new LibUsbException ("Unable enable auto kernel driver attach.", result);

            result = LibUsb.claimInterface (this.handle, interfaceNumber);
            if (result != LibUsb.SUCCESS)
            {
                this.host.error ("Unable to claim interface.", new LibUsbException (result));
                return null;
            }
            this.interfaces.add (Byte.valueOf (interfaceNumber));
        }

        final UsbEndpointImpl endpoint = new UsbEndpointImpl (this.host, this.handle, endpointMatcher.getEndpointAddresses ()[endpointIndex], endpointMatcher.getEndpointIsBulk ()[endpointIndex]);
        this.endpointCache.add (endpoint);
        return endpoint;
    }


    /** {@inheritDoc} */
    @Override
    public IHidDevice getHidDevice ()
    {
        return this.hidDevice;
    }
}
