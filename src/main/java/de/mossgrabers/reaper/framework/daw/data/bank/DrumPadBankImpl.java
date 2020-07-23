// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.DrumPadImpl;


/**
 * Encapsulates the data of a drumpad bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadBankImpl extends AbstractBankImpl<IDrumPad> implements IDrumPadBank
{
    private int numSends;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numLayers The number of layers in the page of the bank
     * @param numSends The number of sends
     */
    public DrumPadBankImpl (final DataSetupEx dataSetup, final int numLayers, final int numSends)
    {
        super (dataSetup, numLayers);

        this.numSends = numSends;
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new DrumPadImpl (this.dataSetup, i, this.numSends));
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean shouldIndicate)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final ILayer sel = this.getSelectedItem ();
        if (sel == null)
            return DAWColor.COLOR_OFF.name ();
        final ColorEx color = sel.getColor ();
        return DAWColor.getColorIndex (color);
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
        // Not supported
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void clearMute ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void clearSolo ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSoloedPads ()
    {
        // Not supported
        return false;
    }
}