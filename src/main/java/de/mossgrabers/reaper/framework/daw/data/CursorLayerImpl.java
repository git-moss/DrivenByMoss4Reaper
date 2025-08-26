// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.ICursorLayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.empty.EmptyDeviceBank;
import de.mossgrabers.framework.daw.data.empty.EmptyLayerBank;

import java.util.Optional;


/**
 * Dummy implementation of a cursor layer. Fully empty since there are no device layers in Reaper.
 *
 * @author Jürgen Moßgraber
 */
public class CursorLayerImpl implements ICursorLayer
{
    private final IDeviceBank deviceBank;
    private final ILayerBank  layerBank;


    /**
     * Constructor.
     *
     * @param numDevices The number of devices of a device bank page
     * @param numLayers The number of layers of a layer bank page
     */
    public CursorLayerImpl (final int numDevices, final int numLayers)
    {
        this.deviceBank = EmptyDeviceBank.getInstance (numDevices);
        this.layerBank = EmptyLayerBank.getInstance (numLayers);
    }


    /** {@inheritDoc} */
    @Override
    public IDeviceBank getDeviceBank ()
    {
        return this.deviceBank;
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISpecificDevice> getSelectedDevice ()
    {
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public ILayerBank getLayerBank ()
    {
        return this.layerBank;
    }
}
