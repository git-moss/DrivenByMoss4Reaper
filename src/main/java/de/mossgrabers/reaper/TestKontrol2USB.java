package de.mossgrabers.reaper;

import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.usb.UsbException;

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
        File imageFile = new File ("/home/mos/Schreibtisch/bitmaptest/64.png");
        BufferedImage image;
        try
        {
            image = ImageIO.read (imageFile);
        }
        catch (IOException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace ();
        }
        int width = image.getWidth ();
        int height = image.getHeight ();
        
        ByteBuffer buffer = ByteBuffer.allocate (10000);

        buffer.put ((byte) 0x84);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00); // Screen
        
        buffer.put ((byte) 0x60);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        buffer.put ((byte) 0x00);
        
        buffer.putShort ((short) width);
        buffer.putShort ((short) height);
        
        final WritableRaster raster = image.getRaster ();
        final int [] pixel = new int [4];

        
        boolean finished = false;
        int i=0;
        int length = width * height;    // TODO Correct?

        while(!finished)
        {
            if(length-i != 0)
                tux.append(QByteArray::fromHex("02000000000000"));
            
            if(length-i >= 22)
                {
                tux.append(QByteArray::fromHex("0b"));
                for(unsigned int j=0;j<22;j++)
                    tux.append(QByteArray::fromHex(QByteArray::number(swappedData[i+j],16).rightJustified(4,'0')));
                i+=22;
                }
            else if(length-i >= 12)
                {
                tux.append(QByteArray::fromHex("06"));
                for(unsigned int j=0;j<12;j++)
                    tux.append(QByteArray::fromHex(QByteArray::number(swappedData[i+j],16).rightJustified(4,'0')));
                i+=12;
                }
            else if(length-i >= 10)
                {
                tux.append(QByteArray::fromHex("05"));
                for(unsigned int j=0;j<10;j++)
                    tux.append(QByteArray::fromHex(QByteArray::number(swappedData[i+j],16).rightJustified(4,'0')));
                i+=10;
                }
            else if(length-i >= 8)
                {
                tux.append(QByteArray::fromHex("04"));
                for(unsigned int j=0;j<8;j++)
                    tux.append(QByteArray::fromHex(QByteArray::number(swappedData[i+j],16).rightJustified(4,'0')));
                i+=8;
                }
            else if(length-i >= 6)
                {
                tux.append(QByteArray::fromHex("03"));
                for(unsigned int j=0;j<6;j++)
                    tux.append(QByteArray::fromHex(QByteArray::number(swappedData[i+j],16).rightJustified(4,'0')));
                i+=6;
                }
            else if(length-i >= 2)
                {
                tux.append(QByteArray::fromHex("01"));
                for(unsigned int j=0;j<2;j++)
                    tux.append(QByteArray::fromHex(QByteArray::number(swappedData[i+j],16).rightJustified(4,'0')));
                i+=2;
                }
            else if(length-i == 1)
                {
                tux.append(QByteArray::fromHex("01"));
                tux.append(QByteArray::fromHex(QByteArray::number(swappedData[i],16).rightJustified(4,'0')));
                tux.append(QByteArray::fromHex("0000"));
                i+=1;
                }
            else
                {
                tux.append(QByteArray::fromHex("02000000030000"));
                tux.append(QByteArray::fromHex("0040000000"));
                finished = true;
                }

        }

        
        for (int i = 0; i < capacity; i += 4)
        {

            final byte red = imageBuffer.get ();
            final byte green = imageBuffer.get ();
            final byte blue = imageBuffer.get ();
            imageBuffer.get (); // Drop transparency

            int color = (red / 7) | ((green / 3) * 64) | ((blue / 7) * 2048);
            b.putShort ((short) color);

            // host.println (i + " - 0:" + buffer0.position () +" - 1:" + buffer1.position ());
        }

        final IntBuffer transfered = IntBuffer.allocate (1);

        LibUsb.bulkTransfer (handle, (byte) 0x03, buffer, transfered, 1000);
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

                    // handle = LibUsb.openDeviceWithVidPid (null, vendorId, productId);

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
