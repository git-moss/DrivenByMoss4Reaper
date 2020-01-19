// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.ILayerBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.ILayer;


/**
 * Encapsulates the data of a layer bank. Reaper has no layer concept, therefore this is an always
 * empty bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerBankImpl extends AbstractBankImpl<ILayer> implements ILayerBank
{
    /**
     * Constructor.
     *
     * @param numDeviceLayers The number of layers
     */
    public LayerBankImpl (final int numDeviceLayers)
    {
        super (null, 0);
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasZeroLayers ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        return DAWColor.COLOR_OFF.name ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // No clips in layers.
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        // No clips in layers.
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }
}