// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Encapsulates the data of a device.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceImpl extends ItemImpl implements IDevice
{
    private static final Pattern PATTERN_DEVICE_NAME = Pattern.compile ("(.+:)?\\s*(.+?)\\s*(\\(.+\\))?");

    private boolean              isEnabled           = false;


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
    public boolean isEnabled ()
    {
        return this.isEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabledState ()
    {
        this.sendOSC (this.getIndex () + 1 + "/bypass", this.isEnabled);
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


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        super.setInternalName (removeTypeAndManufacturer (name));
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.sendOSC ("selected", this.getIndex () + 1);
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

        final Matcher matcher = PATTERN_DEVICE_NAME.matcher (name);
        if (!matcher.matches ())
            return name;

        // Group 1 is type, Group 3 is company
        return matcher.group (2);
    }


    protected void sendDeviceOSC (final String command)
    {
        this.sendOSC (this.getIndex () + 1 + "/" + command);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.DEVICE;
    }
}
