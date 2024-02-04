// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter.map;

import de.mossgrabers.framework.daw.data.IDevice;

import java.util.ArrayList;
import java.util.List;


/**
 * The mapping of the parameters of a device to pages of 8 parameters.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterMap
{
    private final List<ParameterMapPage> pages = new ArrayList<> ();
    private final String                 deviceName;


    /**
     * Constructor.
     *
     * @param device The device for which to map the parameters
     */
    public ParameterMap (final IDevice device)
    {
        this (device.getName ());
    }


    /**
     * Constructor.
     *
     * @param deviceName The name of the device to which the map belongs
     */
    public ParameterMap (final String deviceName)
    {
        this.deviceName = deviceName;
    }


    /**
     * Get the device name.
     *
     * @return The device name
     */
    public String getDeviceName ()
    {
        return this.deviceName;
    }


    /**
     * Add a page to the map.
     */
    public void addPage ()
    {
        this.pages.add (new ParameterMapPage ("Page " + this.pages.size () + 1));
    }


    /**
     * Get the pages.
     *
     * @return The pages
     */
    public List<ParameterMapPage> getPages ()
    {
        return this.pages;
    }
}
