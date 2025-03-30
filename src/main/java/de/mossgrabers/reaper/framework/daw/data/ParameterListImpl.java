// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.IParameterList;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.framework.daw.data.bank.ParameterBankImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMap;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPage;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPageParameter;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.RenamedParameter;
import de.mossgrabers.reaper.framework.device.DeviceManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of a parameter list.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterListImpl implements IParameterList
{
    private final int               numMonitoredPages;
    private final int               maxNumberOfParameters;
    private final ParameterBankImpl parameterBank;
    private final List<IParameter>  parameters     = new ArrayList<> ();
    private final IDevice           device;
    private String                  deviceName     = null;
    private int                     devicePosition = -1;


    /**
     * Constructor.
     *
     * @param numMonitoredPages The number of pages to monitor. Each page has 8 parameters.
     * @param parameterBank The parameter bank of the device
     * @param device The device for looking up the device parameter mapping
     */
    public ParameterListImpl (final int numMonitoredPages, final ParameterBankImpl parameterBank, final IDevice device)
    {
        this.numMonitoredPages = numMonitoredPages;
        this.maxNumberOfParameters = numMonitoredPages * 8;
        this.parameterBank = parameterBank;
        this.device = device;
    }


    /** {@inheritDoc} */
    @Override
    public int getMaxNumberOfParameters ()
    {
        return this.maxNumberOfParameters;
    }


    /** {@inheritDoc} */
    @Override
    public List<IParameter> getParameters ()
    {
        synchronized (this.parameters)
        {
            // Update all parameters if a device has changed
            if (this.deviceName == null || !this.deviceName.equals (this.device.getName ()) || this.devicePosition != this.device.getPosition ())
            {
                this.deviceName = this.device.getName ();
                this.devicePosition = this.device.getPosition ();
                this.refreshParameterCache ();
            }

            return this.parameters;
        }
    }


    /**
     * Refresh the parameter cache.
     */
    public void refreshParameterCache ()
    {
        synchronized (this.parameters)
        {
            this.parameters.clear ();

            if (this.parameterBank == null)
                return;

            // Is there a parameter map?
            ParameterMap parameterMap = null;
            if (this.device != null)
            {
                final String deviceName = this.device.getName ();
                parameterMap = DeviceManager.get ().getParameterMaps ().get (deviceName.toLowerCase ());
            }

            if (parameterMap == null || parameterMap.getPages ().size () == 0)
            {
                final int maxParameters = Math.min (this.maxNumberOfParameters, this.parameterBank.getItemCount ());
                for (int i = 0; i < maxParameters; i++)
                    this.parameters.add (this.parameterBank.getUnpagedItem (i));
                return;
            }

            // Get the selected mapping page, if any
            final List<ParameterMapPage> pages = parameterMap.getPages ();

            final int maxPage = Math.min (this.numMonitoredPages, pages.size ());
            for (int i = 0; i < maxPage; i++)
            {
                final ParameterMapPage parameterMapPage = pages.get (i);
                for (final ParameterMapPageParameter mappedParameters: parameterMapPage.getParameters ())
                {
                    final int destIndex = mappedParameters.getIndex ();
                    this.parameters.add (destIndex < 0 ? EmptyParameter.INSTANCE : new RenamedParameter (this.parameterBank.getUnpagedItem (destIndex), mappedParameters.getName ()));
                }
            }
        }
    }
}
