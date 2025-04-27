// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.observer.IParameterValueObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterProxy;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMap;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPage;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPageParameter;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.RenamedParameter;
import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Encapsulates the data of a parameter bank.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterBankImpl extends AbstractPagedBankImpl<ParameterImpl, IParameter> implements IParameterBank
{
    private final Map<String, Integer>         selectedDevicePages = new HashMap<> ();

    private final Processor                    processor;
    private final IDevice                      device;
    private final IParameter []                mappedParameterCache;
    private int                                mappedParameterCount;
    private final ParameterPageBankImpl        parameterPageBank;
    private final Set<IParameterValueObserver> observers           = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numParams The number of parameters in the page of the bank
     * @param device The device for looking up the device parameter mapping
     */
    public ParameterBankImpl (final DataSetupEx dataSetup, final int numParams, final IDevice device)
    {
        this (dataSetup, Processor.DEVICE, numParams, device);
    }


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param processor The processor to use for sending parameter updates
     * @param numParams The number of parameters in the page of the bank
     * @param device The device for looking up the device parameter mapping
     */
    public ParameterBankImpl (final DataSetupEx dataSetup, final Processor processor, final int numParams, final IDevice device)
    {
        super (dataSetup, numParams, EmptyParameter.INSTANCE);

        this.parameterPageBank = new ParameterPageBankImpl (numParams, this, device);

        this.processor = processor;
        this.device = device;
        if (this.device != null)
        {
            this.device.addNameObserver (name -> {

                // Restore the offset when switching between devices
                this.setBankOffset (this.selectedDevicePages.getOrDefault (name.toLowerCase (), Integer.valueOf (0)).intValue ());

            });
        }

        this.mappedParameterCache = new IParameter [this.pageSize];
        this.clearParameterCache ();
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.mappedParameterCount < 0 ? this.itemCount : this.mappedParameterCount;
    }


    /**
     * Get the number of parameters of the device (without potential reduced mapping).
     *
     * @return The number of parameters
     */
    public int getUnpagedItemCount ()
    {
        return this.itemCount;
    }


    /** {@inheritDoc} */
    @Override
    protected void setBankOffset (final int bankOffset)
    {
        this.bankOffset = Math.max (0, Math.min (bankOffset, this.getItemCount () - 1));

        // Store the offset for switching between devices
        if (this.device != null)
            this.selectedDevicePages.put (this.device.getName ().toLowerCase (), Integer.valueOf (this.bankOffset));

        this.refreshParameterCache ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getItem (final int index)
    {
        return new ParameterProxy (this, index);
    }


    /**
     * Get the pure parameter.
     *
     * @param index The index of the parameter
     * @return The parameter
     */
    public IParameter getParameter (final int index)
    {
        return this.mappedParameterCount < 0 ? super.getItem (index) : this.mappedParameterCache[index];
    }


    /** {@inheritDoc}} */
    @Override
    protected ParameterImpl createItem (final int position)
    {
        return new ParameterImpl (this.dataSetup, this.processor, position % this.pageSize, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.scrollTo (this.bankOffset - this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.scrollTo (this.bankOffset + this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.scrollTo (this.bankOffset - this.pageSize * this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.scrollTo (this.bankOffset + this.pageSize * this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.scrollTo (position, true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        if (position >= 0 && position < this.getItemCount ())
            this.setBankOffset (adjustPage ? position / this.pageSize * this.pageSize : position);
    }


    /** {@inheritDoc} */
    @Override
    public IParameterPageBank getPageBank ()
    {
        return this.parameterPageBank;
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IParameterValueObserver observer)
    {
        this.observers.add (observer);
    }


    /**
     * Notify observers about a value change of a parameter.
     *
     * @param paramIndex The overall index of the parameter
     */
    public void notifyValueObservers (final int paramIndex)
    {
        final int pageSize = this.getPageSize ();
        final int page = paramIndex / pageSize;
        final int index = paramIndex % pageSize;
        for (final IParameterValueObserver observer: this.observers)
            observer.update (page, index);
    }


    /**
     * Updates the parameter mapping.
     */
    public void refreshParameterCache ()
    {
        this.updateParameterCache ();
        this.parameterPageBank.updatePageCache ();
        this.firePageObserver ();
    }


    private void updateParameterCache ()
    {
        // Is there a parameter map?
        ParameterMap parameterMap = null;
        if (this.device != null)
        {
            final String deviceName = this.device.getName ();
            parameterMap = DeviceManager.get ().getParameterMaps ().get (deviceName.toLowerCase ());
        }

        if (parameterMap == null)
        {
            this.clearParameterCache ();
            return;
        }

        // Get the selected mapping page, if any
        final List<ParameterMapPage> pages = parameterMap.getPages ();
        final int page = this.bankOffset / this.pageSize;
        final int numPages = pages.size ();
        if (numPages == 0)
            return;

        if (page >= numPages)
        {
            this.setBankOffset (0);
            return;
        }

        this.mappedParameterCount = numPages * this.pageSize;

        // Cache all parameters on the selected page
        final List<ParameterMapPageParameter> parameters = pages.get (page).getParameters ();
        for (int i = 0; i < 8; i++)
        {
            final ParameterMapPageParameter parameterMapPageParameter = parameters.get (i);
            final int destIndex = parameterMapPageParameter.getIndex ();
            this.mappedParameterCache[i] = destIndex < 0 ? EmptyParameter.INSTANCE : new RenamedParameter (this.getUnpagedItem (destIndex), parameterMapPageParameter.getName ());
        }

        for (int i = 8; i < this.pageSize; i++)
            this.mappedParameterCache[i] = EmptyParameter.INSTANCE;
    }


    private void clearParameterCache ()
    {
        this.mappedParameterCount = -1;
        Arrays.fill (this.mappedParameterCache, EmptyParameter.INSTANCE);
    }
}