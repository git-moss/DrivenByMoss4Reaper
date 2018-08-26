package de.mossgrabers.reaper;

import de.mossgrabers.framework.usb.UsbException;
import de.mossgrabers.framework.utils.StringUtils;

import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;


public class TestKontrol2USB
{
    final static int interfaceID = 0x00;


    public static void main (String [] args)
    {
        int result;
        try
        {
            result = LibUsb.init (null);
            if (result != LibUsb.SUCCESS)
                throw new LibUsbException ("Unable to initialize libusb.", result);

            // Print LibUsb errors and warnings
            LibUsb.setDebug (null, LibUsb.LOG_LEVEL_WARNING);

        }
        catch (final LibUsbException ex)
        {
            ex.printStackTrace ();
            return;
        }

        final DeviceHandle handle;
        try
        {
            handle = openDeviceWithVidPid ((short) 0x17cc, (short) 0x1610);
            if (handle == null)
                return;
        }
        catch (final UsbException ex)
        {
            ex.printStackTrace ();
            return;
        }

        result = LibUsb.setAutoDetachKernelDriver (handle, true);
        if (result != LibUsb.SUCCESS && result != LibUsb.ERROR_NOT_SUPPORTED)
            new LibUsbException (result).printStackTrace ();

        result = LibUsb.claimInterface (handle, interfaceID);
        if (result != LibUsb.SUCCESS)
        {
            new LibUsbException (result).printStackTrace ();
            return;
        }

        sendImage (handle);

        result = LibUsb.releaseInterface (handle, interfaceID);
        if (result != LibUsb.SUCCESS)
            new LibUsbException (result).printStackTrace ();

        LibUsb.close (handle);
        LibUsb.exit (null);
    }


    private static void sendImage (DeviceHandle handle)
    {
        // File imageFile = new File ("/home/mos/Schreibtisch/bitmaptest/red64.png");
        // BufferedImage image;
        // try
        // {
        // image = ImageIO.read (imageFile);
        // }
        // catch (IOException ex)
        // {
        // // TODO Auto-generated catch block
        // ex.printStackTrace ();
        // return;
        // }
        //
        // int width = image.getWidth ();
        // int height = image.getHeight ();128
        
        ///////////////////////////////
        // Display is 480 x 270

        int x = 448;            // 448 = letztes
        int y = 269;            // 256 = letztes
        int width = 32;         // 32 = letztes
        int height = 1;        // 16 = letztes

        ByteBuffer buffer = ByteBuffer.allocateDirect (9724*100);

        buffer.put ((byte) 0x84);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x01); // Screen

        buffer.put ((byte) 0x60);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.putShort ((short) x);
        buffer.putShort ((short) y);

        buffer.putShort ((short) width);
        buffer.putShort ((short) height);

//        final WritableRaster raster = image.getRaster ();
//        final int [] data = raster.getPixels (0, 0, width, height, (int []) null);
        final int [] data = new int[0];

        boolean finished = false;
        int i = 0;
        int length = width * height;

        while (!finished)
        {
            if (length - i != 0)
                buffer.put (StringUtils.fromHexStr ("02000000000000"));

            if (length - i >= 22)
            {
                buffer.put (StringUtils.fromHexStr ("0b"));
                for (int j = 0; j < 22; j++)
                    convertPixel (buffer, data, i);
                i += 22;
            }
            else if (length - i >= 12)
            {
                buffer.put (StringUtils.fromHexStr ("06"));
                for (int j = 0; j < 12; j++)
                    convertPixel (buffer, data, i);
                i += 12;
            }
            else if (length - i >= 10)
            {
                buffer.put (StringUtils.fromHexStr ("05"));
                for (int j = 0; j < 10; j++)
                    convertPixel (buffer, data, i);
                i += 10;
            }
            else if (length - i >= 8)
            {
                buffer.put (StringUtils.fromHexStr ("04"));
                for (int j = 0; j < 8; j++)
                    convertPixel (buffer, data, i);
                i += 8;
            }
            else if (length - i >= 6)
            {
                buffer.put (StringUtils.fromHexStr ("03"));
                for (int j = 0; j < 6; j++)
                    convertPixel (buffer, data, i);
                i += 6;
            }
            else if (length - i >= 2)
            {
                buffer.put (StringUtils.fromHexStr ("01"));
                for (int j = 0; j < 2; j++)
                    convertPixel (buffer, data, i);
                i += 2;
            }
            else if (length - i == 1)
            {
                buffer.put (StringUtils.fromHexStr ("01"));
                convertPixel (buffer, data, i);
                buffer.put (StringUtils.fromHexStr ("0000"));
                i += 1;
            }
            else
            {
                buffer.put (StringUtils.fromHexStr ("02000000030000"));
                buffer.put (StringUtils.fromHexStr ("0040000000"));
                finished = true;
            }

        }

        final IntBuffer transfered = IntBuffer.allocate (1);

        LibUsb.bulkTransfer (handle, (byte) 0x03, buffer, transfered, 1000);
    }


    private static void convertPixel (ByteBuffer buffer, int [] data, int i)
    {
        int pos = 3 * i;
//        int red = data[pos];
//        int green = data[pos + 1];
//        int blue = data[pos + 2];
        int red = 255;
        int green = 0;
        int blue = 0;

        int pixel = ((red * 0x1F / 0xFF) << 11) + ((green * 0x3F / 0xFF) << 5) + (blue * 0x1F / 0xFF);

        // Bytes need to be swapped
        buffer.put ((byte) ((pixel & 0xFF00) >> 8));
        buffer.put ((byte) (pixel & 0x00FF));
    }


    private static DeviceHandle openDeviceWithVidPid (final short vendorId, final short productId) throws UsbException
    {
        final DeviceList list = new DeviceList ();
        int result = LibUsb.getDeviceList (null, list);
        if (result < LibUsb.SUCCESS)
            throw new UsbException ("Unable to get device list.", new LibUsbException (result));

        try
        {
            final Iterator<Device> iterator = list.iterator ();
            while (iterator.hasNext ())
            {
                final Device device = iterator.next ();
                final DeviceDescriptor descriptor = new DeviceDescriptor ();
                result = LibUsb.getDeviceDescriptor (device, descriptor);
                if (result != LibUsb.SUCCESS)
                {
                    new LibUsbException (result).printStackTrace ();
                    // Continue, maybe there is a working device
                    continue;
                }
                if (descriptor.idVendor () == vendorId && descriptor.idProduct () == productId)
                {
                    DeviceHandle handle = new DeviceHandle ();
                    try
                    {
                        result = LibUsb.open (device, handle);
                        if (result == LibUsb.SUCCESS)
                            return handle;
                        new LibUsbException (result).printStackTrace ();
                    }
                    catch (org.usb4java.LibUsbException ex)
                    {
                        ex.printStackTrace ();
                    }

                    // Continue, maybe there is a working device
                }
            }
        }
        finally
        {
            LibUsb.freeDeviceList (list, true);
        }

        return null;
    }
}
