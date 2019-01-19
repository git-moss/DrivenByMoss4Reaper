// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.usb;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.IUsbEndpoint;
import de.mossgrabers.framework.usb.UsbException;
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
    private final IHost                 host;
    private final UsbMatcher            usbMatcher;
    private final List<Byte>            interfaces    = new ArrayList<> ();
    private final List<UsbEndpointImpl> endpointCache = new ArrayList<> ();
    private DeviceHandle                handle;
    private HidDeviceImpl               hidDevice;


    /**
     * Constructor.
     *
     * @param host The host
     * @param usbMatcher The device info
     * @throws UsbException Could not lookup or open the device
     */
    public UsbDeviceImpl (final IHost host, final UsbMatcher usbMatcher) throws UsbException
    {
        this.host = host;
        this.usbMatcher = usbMatcher;

        // Only attempt to open the device if endpoints are configured
        if (usbMatcher.getEndpoints ().isEmpty ())
            return;

        this.handle = openDeviceWithVidPid (usbMatcher.getVendor (), usbMatcher.getProductID ());
        if (this.handle == null)
            throw new UsbException ("USB Device not found.", new LibUsbException (LibUsb.ERROR_NO_DEVICE));
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
            if (this.handle != null)
            {
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
    }


    /**
     * Find the device with the given vendor and product ID and open it.
     *
     * @param vendorId The vendor ID to look for
     * @param productId The product ID to look for
     * @return The device handle of the device or null if not found
     * @throws UsbException Error reading device descriptor
     */
    private static DeviceHandle openDeviceWithVidPid (final short vendorId, final short productId) throws UsbException
    {
        final DeviceList list = new DeviceList ();
        int result = LibUsb.getDeviceList (null, list);
        if (result < LibUsb.SUCCESS)
            throw new UsbException ("Unable to get device list.", new LibUsbException (result));

        try
        {
            final Iterator<Device> iterator = list.iterator ();
            UsbException ex = null;
            while (iterator.hasNext ())
            {
                try
                {
                    final DeviceHandle handle = testAndOpenDevice (iterator.next (), vendorId, productId);
                    if (handle != null)
                        return handle;
                }
                catch (final UsbException exception)
                {
                    ex = exception;
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


    /**
     * Test if the given device matches the vendor and product id. If yes, open it and return the
     * handle.
     *
     * @param device The device to test
     * @param vendorId The vendor ID
     * @param productId The product ID
     * @return The handle or null if it does not match
     * @throws UsbException Error opening the matching device
     */
    private static DeviceHandle testAndOpenDevice (final Device device, final short vendorId, final short productId) throws UsbException
    {
        final DeviceDescriptor descriptor = new DeviceDescriptor ();
        int result = LibUsb.getDeviceDescriptor (device, descriptor);
        if (result != LibUsb.SUCCESS)
            throw new UsbException ("Unable to read device descriptor.", new LibUsbException (result));

        if (descriptor.idVendor () != vendorId || descriptor.idProduct () != productId)
            return null;

        final DeviceHandle handle = new DeviceHandle ();
        result = LibUsb.open (device, handle);
        if (result != LibUsb.SUCCESS)
            throw new UsbException ("Unable to read device descriptor.", new LibUsbException (result));

        return handle;
    }


    /** {@inheritDoc} */
    @Override
    public IUsbEndpoint getEndpoint (final int interfaceIndex, final int endpointIndex) throws UsbException
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
                throw new UsbException ("Unable enable auto kernel driver attach.", new LibUsbException (result));

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
    public IHidDevice getHidDevice () throws UsbException
    {
        if (this.hidDevice == null)
            this.hidDevice = new HidDeviceImpl (this.usbMatcher.getVendor (), this.usbMatcher.getProductID ());
        return this.hidDevice;
    }
}
