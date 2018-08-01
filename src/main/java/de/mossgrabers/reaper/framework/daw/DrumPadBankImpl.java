// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.reaper.framework.daw.data.DrumPadImpl;
import de.mossgrabers.transformator.communication.MessageSender;


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
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numLayers The number of layers in the page of the bank
     * @param numSends The number of sends
     */
    public DrumPadBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numLayers, final int numSends)
    {
        super (host, sender, valueChanger, numLayers);
        this.numSends = numSends;
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new DrumPadImpl (this.host, this.sender, this.valueChanger, i, this.numSends));
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
            return DAWColors.COLOR_OFF;
        final double [] color = sel.getColor ();
        return DAWColors.getColorIndex (color[0], color[1], color[2]);
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
    public void scrollPageBackwards ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        // Not supported
    }
}