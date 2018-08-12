// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ILayerBank;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.reaper.framework.daw.data.DeviceImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Proxy to the Reaper Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorDeviceImpl extends DeviceImpl implements ICursorDevice
{
    private boolean                     isEnabled   = false;
    private boolean                     isWindowOpen;
    private boolean                     isExpanded;
    private int                         deviceCount = 0;

    private final IDeviceBank           deviceBank;
    private final ParameterPageBankImpl parameterPageBank;
    private final IParameterBank        parameterBank;
    private final ILayerBank            layerBank;
    private final IDrumPadBank          drumPadBank;


    /**
     * Constructor.
     *
     * @param host The host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numSends The number of sends
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public CursorDeviceImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (host, sender, -1);

        final int checkedNumParams = numParams >= 0 ? numParams : 8;
        final int checkedNumDevices = numDevicesInBank >= 0 ? numDevicesInBank : 8;
        final int checkedNumDeviceLayers = numDeviceLayers >= 0 ? numDeviceLayers : 8;
        final int checkedNumDrumPadLayers = numDrumPadLayers >= 0 ? numDrumPadLayers : 16;

        this.deviceBank = new DeviceBankImpl (host, sender, valueChanger, checkedNumDevices);

        if (checkedNumParams > 0)
        {
            this.parameterPageBank = new ParameterPageBankImpl (host, sender, valueChanger, checkedNumParams);
            this.parameterBank = new ParameterBankImpl (host, sender, valueChanger, this.parameterPageBank, checkedNumParams);
        }
        else
        {
            this.parameterPageBank = null;
            this.parameterBank = null;
        }

        // Always empty
        this.layerBank = new LayerBankImpl (checkedNumDeviceLayers);

        // Monitor the drum pad layers of a container device (if any)
        this.drumPadBank = new DrumPadBankImpl (host, sender, valueChanger, checkedNumDrumPadLayers, numSends);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.getPosition () % this.deviceBank.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.isEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        String n = this.getName ();
        if (n.length () < limit)
            return n;

        final String [] split = n.split (": ");
        if (split.length == 2)
            n = split[1];

        if (n.length () < limit)
            return n;

        return n.substring (0, limit);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlugin ()
    {
        // Not supported
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectPreviousFX ()
    {
        return this.getPosition () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectNextFX ()
    {
        return this.getPosition () < this.deviceCount - 1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isExpanded ()
    {
        return this.isExpanded;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isParameterPageSectionVisible ()
    {
        // Not supported
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWindowOpen ()
    {
        return this.isWindowOpen;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNested ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumPads ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasLayers ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSlots ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPinned ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabledState ()
    {
        this.sender.sendOSC ("/device/bypass", Integer.valueOf (this.isEnabled ? 1 : 0));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWindowOpen ()
    {
        this.sender.sendOSC ("/device/window", Integer.valueOf (this.isWindowOpen ? 0 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public void selectPrevious ()
    {
        // To support displaying the newly selected device quickly
        final int index = this.getIndex ();
        if (index > 0)
            this.setName (this.deviceBank.getItem (index - 1).getName ());
        this.sender.sendOSC ("/device/-", null);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNext ()
    {
        // To support displaying the newly selected device quickly
        final int index = this.getIndex ();
        if (index < this.deviceBank.getPageSize () - 1)
            this.setName (this.deviceBank.getItem (index + 1).getName ());
        this.sender.sendOSC ("/device/+", null);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleExpanded ()
    {
        this.isExpanded = !this.isExpanded;
        this.sender.sendOSC ("/device/expand", Integer.valueOf (this.isExpanded ? 1 : 0));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleParameterPageSectionVisible ()
    {
        // Not supported
    }


    /**
     * Set if the device is enabled.
     *
     * @param isEnabled The enabled state
     */
    public void setEnabled (final boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }


    /**
     * Set if the UI window displays the fx chain.
     *
     * @param isExpanded True if expanded
     */
    public void setExpanded (final boolean isExpanded)
    {
        this.isExpanded = isExpanded;
    }


    /**
     * Set if the UI window of the device is open.
     *
     * @param isWindowOpen True if open
     */
    public void setWindowOpen (final boolean isWindowOpen)
    {
        this.isWindowOpen = isWindowOpen;
    }


    /**
     * Set the number of devices of the channel.
     *
     * @param deviceCount The number of devices
     */
    public void setDeviceCount (final int deviceCount)
    {
        this.deviceCount = deviceCount;
    }


    /**
     * Set the overall number of parameters of the selected device.
     *
     * @param count The number of parameters
     */
    public void setParameterCount (final int count)
    {
        final int pageSize = this.deviceBank.getPageSize ();
        final int numOfPages = count / pageSize + (count % pageSize > 0 ? 1 : 0);

        final String [] parameterPageNames = new String [numOfPages];
        for (int i = 0; i < numOfPages; i++)
            parameterPageNames[i] = "Page " + (i + 1);
        this.parameterPageBank.setPageNames (parameterPageNames);
    }


    /** {@inheritDoc} */
    @Override
    public IDeviceBank getDeviceBank ()
    {
        return this.deviceBank;
    }


    /** {@inheritDoc} */
    @Override
    public IParameterPageBank getParameterPageBank ()
    {
        return this.parameterPageBank;
    }


    /** {@inheritDoc} */
    @Override
    public IParameterBank getParameterBank ()
    {
        return this.parameterBank;
    }


    /** {@inheritDoc} */
    @Override
    public ILayerBank getLayerBank ()
    {
        return this.layerBank;
    }


    /** {@inheritDoc} */
    @Override
    public IDrumPadBank getDrumPadBank ()
    {
        return this.drumPadBank;
    }


    /** {@inheritDoc} */
    @Override
    public IChannelBank<?> getLayerOrDrumPadBank ()
    {
        return this.hasDrumPads () ? this.drumPadBank : this.layerBank;
    }


    /** {@inheritDoc} */
    @Override
    public void browseToReplaceDevice ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertBeforeDevice ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAfterDevice ()
    {
        // Not used
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
}