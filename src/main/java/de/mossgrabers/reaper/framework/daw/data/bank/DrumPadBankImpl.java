// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.empty.EmptyDrumPad;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.DrumPadImpl;

import java.util.Optional;


/**
 * Encapsulates the data of a drum pad bank.
 *
 * @author Jürgen Moßgraber
 */
public class DrumPadBankImpl extends AbstractPagedBankImpl<DrumPadImpl, ILayer> implements IDrumPadBank
{
    private final int numSends;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numLayers The number of layers in the page of the bank
     * @param numSends The number of sends
     */
    public DrumPadBankImpl (final DataSetupEx dataSetup, final int numLayers, final int numSends)
    {
        super (dataSetup, numLayers, EmptyDrumPad.getInstance (numSends));

        this.numSends = numSends;

        if (numLayers == 0)
            return;

        // Fake 128 drum pads which covers the whole MIDI range
        this.setItemCount (128);

        for (int i = 0; i < 128; i++)
        {
            final DrumPadImpl drumPad = this.getUnpagedItem (i);
            drumPad.setPosition (i);
            drumPad.setIndex (numLayers > 0 ? i % numLayers : i);
            drumPad.setInternalName ("Drum " + i);
        }

        this.setSelectedDrumPad (36);
        this.scrollTo (36);
    }


    /** {@inheritDoc} */
    @Override
    public IDrumPad getItem (final int index)
    {
        return (IDrumPad) super.getItem (index);
    }


    /** {@inheritDoc} */
    @Override
    protected IDrumPad createItem (final int position)
    {
        return new DrumPadImpl (this.dataSetup, this, position, this.numSends);
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
        final Optional<ILayer> sel = this.getSelectedItem ();
        if (sel.isEmpty ())
            return DAWColor.COLOR_OFF.name ();
        final ColorEx color = sel.get ().getColor ();
        return DAWColor.getColorID (color);
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


    /** {@inheritDoc} */
    @Override
    public boolean hasMutedPads ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        this.setBankOffset (position / this.pageSize * this.pageSize);
    }


    /**
     * Change the selection to the drum pad at the given position.
     *
     * @param position The drum pads position
     */
    public void setSelectedDrumPad (final int position)
    {
        for (int i = 0; i < this.itemCount; i++)
            this.getUnpagedItem (i).setSelected (i == position);
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