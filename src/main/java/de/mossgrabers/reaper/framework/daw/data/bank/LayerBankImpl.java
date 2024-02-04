// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.LayerImpl;


/**
 * Encapsulates the data of a layer bank. Reaper has no layer concept, therefore this is an always
 * empty bank.
 *
 * @author Jürgen Moßgraber
 */
public class LayerBankImpl extends AbstractBankImpl<ILayer> implements ILayerBank
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numDeviceLayers The number of layers
     */
    public LayerBankImpl (final DataSetupEx dataSetup, final int numDeviceLayers)
    {
        super (dataSetup, numDeviceLayers);

        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new LayerImpl (this.dataSetup, i, numDeviceLayers));
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        return DAWColor.COLOR_OFF.name ();
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


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        // Not supported
        return "";
    }
}