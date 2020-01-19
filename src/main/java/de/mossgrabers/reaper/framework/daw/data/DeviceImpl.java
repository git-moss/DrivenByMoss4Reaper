// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceImpl extends ItemImpl implements IDevice
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the device
     */
    public DeviceImpl (final DataSetupEx dataSetup, final int index)
    {
        super (dataSetup, index);
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        super.setName (removeTypeAndManufacturer (name));
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.sender.processIntArg ("device", "selected", this.getIndex () + 1);
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        this.sendDeviceOSC ("remove");
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.sendDeviceOSC ("duplicate");
    }


    private static String removeTypeAndManufacturer (final String name)
    {
        if (name == null)
            return "";
        if (name.startsWith ("VSTi: "))
            return removeManufacturer (name.substring (6));
        if (name.startsWith ("VST: "))
            return removeManufacturer (name.substring (5));
        if (name.startsWith ("VST3i: "))
            return removeManufacturer (name.substring (7));
        if (name.startsWith ("VST3: "))
            return removeManufacturer (name.substring (6));
        if (name.startsWith ("JS: "))
            return removeManufacturer (name.substring (4));
        return name;
    }


    private static String removeManufacturer (final String name)
    {
        final int index = name.indexOf ('(');
        return index > 1 ? name.substring (0, index).trim () : name;
    }


    protected void sendDeviceOSC (final String command)
    {
        this.sender.processNoArg ("device", this.getIndex () + 1 + "/" + command);
    }
}
