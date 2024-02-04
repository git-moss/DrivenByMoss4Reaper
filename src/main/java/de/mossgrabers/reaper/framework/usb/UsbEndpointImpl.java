// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.usb;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.usb.IUsbCallback;
import de.mossgrabers.framework.usb.IUsbEndpoint;

import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.Transfer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Implementation for an USB end-point.
 *
 * @author Jürgen Moßgraber
 */
public class UsbEndpointImpl implements IUsbEndpoint
{
    private final IHost          host;
    private final DeviceHandle   handle;
    private final byte           endpointAddress;
    private final boolean        isBulk;
    private Transfer             activeTransfer;
    private final Object         transferLock = new Object ();
    private final CountDownLatch clearLatch   = new CountDownLatch (1);


    /**
     * Constructor.
     *
     * @param host The host
     * @param handle The libusb device handle
     * @param endpointAddress The end-point address
     * @param isBulk True if bulk transfer should be used otherwise interrupt
     */
    public UsbEndpointImpl (final IHost host, final DeviceHandle handle, final byte endpointAddress, final boolean isBulk)
    {
        this.host = host;
        this.handle = handle;
        this.endpointAddress = endpointAddress;
        this.isBulk = isBulk;
    }


    /** {@inheritDoc} */
    @Override
    public void send (final IMemoryBlock memoryBlock, final int timeout)
    {
        final IntBuffer transfered = IntBuffer.allocate (1);
        final ByteBuffer buffer = memoryBlock.createByteBuffer ();
        int result;
        if (this.isBulk)
            result = LibUsb.bulkTransfer (this.handle, this.endpointAddress, buffer, transfered, timeout);
        else
            result = LibUsb.interruptTransfer (this.handle, this.endpointAddress, buffer, transfered, timeout);
        if (result == LibUsb.SUCCESS)
            return;

        // Retry once
        if (this.isBulk)
            result = LibUsb.bulkTransfer (this.handle, this.endpointAddress, buffer, transfered, timeout);
        else
            result = LibUsb.interruptTransfer (this.handle, this.endpointAddress, buffer, transfered, timeout);
        if (result != LibUsb.SUCCESS)
            this.host.error ("USB transmission error: " + result);
    }


    /** {@inheritDoc} */
    @Override
    public void sendAsync (final IMemoryBlock memoryBlock, final IUsbCallback callback, final int timeout)
    {
        synchronized (this.transferLock)
        {
            // Only handle one transfer at a time
            if (this.activeTransfer != null)
                return;
            this.activeTransfer = LibUsb.allocTransfer ();
        }

        if (this.isBulk)
            LibUsb.fillBulkTransfer (this.activeTransfer, this.handle, this.endpointAddress, memoryBlock.createByteBuffer (), result -> this.handleAsyncResult (callback, result), null, timeout);
        else
            LibUsb.fillInterruptTransfer (this.activeTransfer, this.handle, this.endpointAddress, memoryBlock.createByteBuffer (), result -> this.handleAsyncResult (callback, result), null, timeout);
        final int result = LibUsb.submitTransfer (this.activeTransfer);
        if (result != LibUsb.SUCCESS)
            this.host.error ("Unable to submit USB async transfer: " + result);
    }


    /**
     * Cancel pending transfers. Can only be executed once!
     */
    public void clear ()
    {
        synchronized (this.transferLock)
        {
            if (this.activeTransfer == null)
                return;
            final int result = LibUsb.cancelTransfer (this.activeTransfer);
            if (result != LibUsb.SUCCESS)
                return;
        }
        try
        {
            final boolean await = this.clearLatch.await (10L, TimeUnit.SECONDS);
            if (!await)
                this.host.error ("Timed out waiting for LibUsb transfer cancelation.");
        }
        catch (final InterruptedException ex)
        {
            this.host.error ("Thread was interrupted while waiting for LibUsb transfer cancelation.", ex);
            Thread.currentThread ().interrupt ();

        }
    }


    private void handleAsyncResult (final IUsbCallback callback, final Transfer result)
    {
        final int status = result.status ();
        if (status == LibUsb.TRANSFER_CANCELLED)
        {
            this.clearLatch.countDown ();
            return;
        }
        if (status != LibUsb.TRANSFER_COMPLETED)
            this.host.error ("USB receive error: " + status);
        callback.process (status == LibUsb.TRANSFER_COMPLETED ? result.actualLength () : -1);
        LibUsb.freeTransfer (this.activeTransfer);

        synchronized (this.transferLock)
        {
            this.activeTransfer = null;
        }
    }
}
