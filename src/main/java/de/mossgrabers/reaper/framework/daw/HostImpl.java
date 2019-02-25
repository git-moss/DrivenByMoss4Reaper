// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IImage;
import de.mossgrabers.framework.osc.IOpenSoundControlCallback;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.UsbException;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.reaper.framework.graphics.BitmapImpl;
import de.mossgrabers.reaper.framework.graphics.SVGImage;
import de.mossgrabers.reaper.framework.osc.OpenSoundControlMessageImpl;
import de.mossgrabers.reaper.framework.osc.OpenSoundControlServerImpl;
import de.mossgrabers.reaper.framework.usb.UsbDeviceImpl;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.transport.udp.OSCPortIn;

import java.awt.Color;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Encapsulates the ControllerHost instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HostImpl implements IHost
{
    private final Window                           owner;
    private final LogModel                         logModel;
    private final ScheduledExecutorService         executor       = Executors.newScheduledThreadPool (10);
    private final List<UsbMatcher>                 usbDeviceInfos = new ArrayList<> ();
    private final List<IUsbDevice>                 usbDevices     = new ArrayList<> ();
    private final List<OpenSoundControlServerImpl> oscSenders     = new ArrayList<> ();
    private OSCPortIn                              oscReceiver;
    private final NotificationWindow               notificationWindow;


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param owner The owner window for the bitmap display window
     */
    public HostImpl (final LogModel logModel, final Window owner)
    {
        this.logModel = logModel;
        this.owner = owner;

        this.notificationWindow = new NotificationWindow (logModel);
    }


    /**
     * Shuts down the scheduled delay executor.
     */
    public void shutdown ()
    {
        this.executor.shutdown ();
        this.notificationWindow.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Reaper";
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasClips ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPinning ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasCrossfader ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumDevice ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRepeat ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditMarkers ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void scheduleTask (final Runnable task, final long delay)
    {
        try
        {
            this.executor.schedule (task, delay, TimeUnit.MILLISECONDS);
        }
        catch (final java.util.concurrent.RejectedExecutionException ex)
        {
            this.error ("Could not delay thread.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text)
    {
        this.logModel.info (text);
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text, final Throwable ex)
    {
        this.logModel.error (text, ex);
    }


    /** {@inheritDoc} */
    @Override
    public void println (final String text)
    {
        this.logModel.info (text);
    }


    /** {@inheritDoc} */
    @Override
    public void showNotification (final String message)
    {
        SafeRunLater.execute (this.logModel, () -> this.notificationWindow.displayMessage (message));
    }


    /** {@inheritDoc} */
    @Override
    public IImage loadSVG (final String imageName, final int scale)
    {
        final String filename = "/images/" + imageName;
        try
        {
            return SVGImage.getSVGImage (filename, Color.BLACK);
        }
        catch (final IOException ex)
        {
            this.error ("Could not load SVG image: " + filename, ex);
            return null;
        }
    }


    /** {@inheritDoc} */
    @Override
    public IBitmap createBitmap (final int width, final int height)
    {
        return new BitmapImpl (this.owner, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public IMemoryBlock createMemoryBlock (final int size)
    {
        return new MemoryBlockImpl (size);
    }


    /** {@inheritDoc} */
    @Override
    public IUsbDevice getUsbDevice (final int index) throws UsbException
    {
        final UsbDeviceImpl usbDevice = new UsbDeviceImpl (this, this.usbDeviceInfos.get (index));
        this.usbDevices.add (usbDevice);
        return usbDevice;
    }


    /** {@inheritDoc} */
    @Override
    public void releaseUsbDevices ()
    {
        for (final IUsbDevice usbDevice: this.usbDevices)
            usbDevice.release ();
    }


    /**
     * Add an USB device info.
     *
     * @param matcher The info
     */
    public void addUSBDeviceInfo (final UsbMatcher matcher)
    {
        this.usbDeviceInfos.add (matcher);
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlServer connectToOSCServer (final String serverAddress, final int serverPort)
    {
        final OpenSoundControlServerImpl server = new OpenSoundControlServerImpl (this, serverAddress, serverPort);
        this.oscSenders.add (server);
        return server;
    }


    /** {@inheritDoc} */
    @Override
    public void createOSCServer (final IOpenSoundControlCallback callback, final int port)
    {
        try
        {
            if (this.oscReceiver != null)
            {
                this.oscReceiver.stopListening ();
                this.oscReceiver.close ();
            }
            this.oscReceiver = new OSCPortIn (port);
        }
        catch (final IOException ex)
        {
            this.error ("Could not create OSC server.", ex);
            return;
        }

        this.oscReceiver.addPacketListener (new PacketListener (callback));
        this.oscReceiver.startListening ();
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlMessage createOSCMessage (final String address, final List<Object> values)
    {
        return new OpenSoundControlMessageImpl (address, values);
    }


    /** {@inheritDoc} */
    @Override
    public void releaseOSC ()
    {
        if (this.oscReceiver != null)
        {
            this.oscReceiver.stopListening ();
            try
            {
                this.oscReceiver.close ();
            }
            catch (final IOException ex)
            {
                this.error ("Could not close OSC receiver.", ex);
                return;
            }
        }
        for (final OpenSoundControlServerImpl sender: this.oscSenders)
            sender.close ();
    }

    private class PacketListener implements OSCPacketListener
    {
        private final IOpenSoundControlCallback callback;


        public PacketListener (final IOpenSoundControlCallback callback)
        {
            this.callback = callback;
        }


        /** {@inheritDoc} */
        @Override
        public void handlePacket (final OSCPacketEvent event)
        {
            final List<OSCMessage> messages = new ArrayList<> ();
            this.collectMessages (messages, event.getPacket ());

            SafeRunLater.execute (HostImpl.this.logModel, () -> {
                for (final OSCMessage message: messages)
                    this.callback.handle (new OpenSoundControlMessageImpl (message));
            });
        }


        /** {@inheritDoc} */
        @Override
        public void handleBadData (final OSCBadDataEvent event)
        {
            HostImpl.this.logModel.error ("Could not parse message.", event.getException ());
        }


        private void collectMessages (final List<OSCMessage> messages, final OSCPacket packet)
        {
            if (packet instanceof OSCMessage)
                messages.add ((OSCMessage) packet);
            else if (packet instanceof OSCBundle)
            {
                for (final OSCPacket op: ((OSCBundle) packet).getPackets ())
                    collectMessages (messages, op);
            }
        }
    }
}
