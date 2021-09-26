// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.hardware.IHwSurfaceFactory;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IImage;
import de.mossgrabers.framework.osc.IOpenSoundControlCallback;
import de.mossgrabers.framework.osc.IOpenSoundControlClient;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.UsbException;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.graphics.BitmapImpl;
import de.mossgrabers.reaper.framework.graphics.SVGImage;
import de.mossgrabers.reaper.framework.hardware.HwSurfaceFactoryImpl;
import de.mossgrabers.reaper.framework.osc.OpenSoundControlClientImpl;
import de.mossgrabers.reaper.framework.osc.OpenSoundControlMessageImpl;
import de.mossgrabers.reaper.framework.osc.OpenSoundControlServerImpl;
import de.mossgrabers.reaper.framework.usb.UsbDeviceImpl;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import java.awt.Color;
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
    private final WindowManager                    windowManager;
    private final LogModel                         logModel;
    private final ScheduledExecutorService         executor       = Executors.newScheduledThreadPool (10);
    private final List<UsbMatcher>                 usbDeviceInfos = new ArrayList<> ();
    private final List<IUsbDevice>                 usbDevices     = new ArrayList<> ();
    private final List<OpenSoundControlClientImpl> oscSenders     = new ArrayList<> ();
    private final List<OpenSoundControlServerImpl> oscReceivers   = new ArrayList<> ();
    private final NotificationWindow               notificationWindow;
    private final IControllerInstance              controllerInstance;


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the bitmap display window
     * @param controllerInstance The controller instance for restarting it
     */
    public HostImpl (final LogModel logModel, final WindowManager windowManager, final IControllerInstance controllerInstance)
    {
        this.logModel = logModel;
        this.windowManager = windowManager;
        this.controllerInstance = controllerInstance;

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
    public void restart ()
    {
        this.controllerInstance.restart ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean supports (final Capability capability)
    {
        switch (capability)
        {
            case MARKERS:
                return true;

            case NOTE_REPEAT_LENGTH, NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY, NOTE_REPEAT_MODE:
                return true;

            case NOTE_REPEAT_SWING, NOTE_REPEAT_OCTAVES, NOTE_REPEAT_IS_FREE_RUNNING:
                return false;

            case NOTE_EDIT_MUTE:
                return true;

            case NOTE_EDIT_RELEASE_VELOCITY, NOTE_EDIT_CHANCE, NOTE_EDIT_EXPRESSIONS, NOTE_EDIT_OCCURRENCE, NOTE_EDIT_RECCURRENCE, NOTE_EDIT_REPEAT, NOTE_EDIT_VELOCITY_SPREAD:
                return false;

            case QUANTIZE_INPUT_NOTE_LENGTH, QUANTIZE_AMOUNT:
                return false;

            case CUE_VOLUME:
                return false;

            case HAS_CROSSFADER, HAS_DRUM_DEVICE, HAS_EFFECT_BANK, HAS_SLOT_CHAINS:
                return false;

            case HAS_PINNING:
                return true;

            default:
                return false;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void scheduleTask (final Runnable task, final long delay)
    {
        try
        {
            if (!this.executor.isShutdown ())
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
        return SVGImage.getSVGImage (filename, Color.BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public IBitmap createBitmap (final int width, final int height)
    {
        return new BitmapImpl (this.windowManager, width, height);
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
    public IOpenSoundControlClient connectToOSCServer (final String serverAddress, final int serverPort)
    {
        final OpenSoundControlClientImpl client = new OpenSoundControlClientImpl (this, serverAddress, serverPort);
        this.oscSenders.add (client);
        return client;
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlServer createOSCServer (final IOpenSoundControlCallback callback)
    {
        final OpenSoundControlServerImpl server = new OpenSoundControlServerImpl (callback, this.logModel);
        this.oscReceivers.add (server);
        return server;
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlMessage createOSCMessage (final String address, final List<?> values)
    {
        return new OpenSoundControlMessageImpl (address, values);
    }


    /** {@inheritDoc} */
    @Override
    public void releaseOSC ()
    {
        for (final OpenSoundControlServerImpl receiver: this.oscReceivers)
            receiver.close ();
        for (final OpenSoundControlClientImpl sender: this.oscSenders)
            sender.close ();
    }


    /** {@inheritDoc} */
    @Override
    public IHwSurfaceFactory createSurfaceFactory (final double width, final double height)
    {
        final HwSurfaceFactoryImpl surfaceFactory = new HwSurfaceFactoryImpl (this);
        surfaceFactory.setDimension (width, height);
        return surfaceFactory;
    }


    /** {@inheritDoc} */
    @Override
    public List<IDeviceMetadata> getInstrumentMetadata ()
    {
        return new ArrayList<> (DeviceManager.get ().getInstruments ());
    }


    /** {@inheritDoc} */
    @Override
    public List<IDeviceMetadata> getAudioEffectMetadata ()
    {
        return new ArrayList<> (DeviceManager.get ().getEffects ());
    }


    /**
     * Get the window manager.
     *
     * @return The window manager
     */
    public WindowManager getWindowManager ()
    {
        return this.windowManager;
    }
}
