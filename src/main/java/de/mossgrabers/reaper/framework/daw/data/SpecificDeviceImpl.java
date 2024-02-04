// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.bank.DrumPadBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.LayerBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.ParameterBankImpl;


/**
 * Proxy to the Reaper Cursor device.
 *
 * @author Jürgen Moßgraber
 */
public class SpecificDeviceImpl extends DeviceImpl implements ISpecificDevice
{
    private boolean                 isWindowOpen;
    private boolean                 isExpanded;
    private final ParameterBankImpl parameterBank;
    private final ILayerBank        layerBank;
    private final IDrumPadBank      drumPadBank;


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
    public SpecificDeviceImpl (final DataSetupEx dataSetup, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        this (dataSetup, Processor.DEVICE, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
    }


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param processor The processor to use for sending parameter updates
     * @param numSends The number of sends
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public SpecificDeviceImpl (final DataSetupEx dataSetup, final Processor processor, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (dataSetup, -1);

        final int checkedNumParams = numParams >= 0 ? numParams : 8;
        final int checkedNumDeviceLayers = numDeviceLayers >= 0 ? numDeviceLayers : 8;
        final int checkedNumDrumPadLayers = numDrumPadLayers >= 0 ? numDrumPadLayers : 16;

        if (checkedNumParams > 0)
            this.parameterBank = new ParameterBankImpl (dataSetup, processor, checkedNumParams, this);
        else
            this.parameterBank = null;

        // Always empty
        this.layerBank = new LayerBankImpl (dataSetup, checkedNumDeviceLayers);

        // Monitor the drum pad layers of a container device (if any)
        this.drumPadBank = new DrumPadBankImpl (dataSetup, checkedNumDrumPadLayers, numSends);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.DEVICE, enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        if (this.parameterBank == null || this.parameterBank.getItemCount () == 0)
            return "";
        final String name = this.parameterBank.getUnpagedItem (0).getName ();
        return name == null ? "" : name;
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
        // All are plugins (no native)
        return true;
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
        // Always true to support drum sequencers
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void addHasDrumPadsObserver (final IValueObserver<Boolean> observer)
    {
        // No drum devices, therefore never fires
    }


    /** {@inheritDoc} */
    @Override
    public void removeHasDrumPadsObserver (final IValueObserver<Boolean> observer)
    {
        // No drum devices, therefore never fires
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
    public void toggleWindowOpen ()
    {
        this.sender.processBooleanArg (Processor.DEVICE, "window", !this.isWindowOpen);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleExpanded ()
    {
        this.isExpanded = !this.isExpanded;
        this.sender.processBooleanArg (Processor.DEVICE, "expand", this.isExpanded);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleParameterPageSectionVisible ()
    {
        // Not supported
    }


    /**
     * Set if the UI window displays the FX chain.
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
     * Set the overall number of parameters of the selected device.
     *
     * @param count The number of parameters
     */
    public void setParameterCount (final int count)
    {
        if (this.parameterBank != null)
            this.parameterBank.setItemCount (count);
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
}