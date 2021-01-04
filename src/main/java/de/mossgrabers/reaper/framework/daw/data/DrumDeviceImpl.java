// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Proxy to the Drum device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumDeviceImpl extends SpecificDeviceImpl implements IDrumDevice
{
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
    public DrumDeviceImpl (final DataSetupEx dataSetup, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (dataSetup, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
    }
}