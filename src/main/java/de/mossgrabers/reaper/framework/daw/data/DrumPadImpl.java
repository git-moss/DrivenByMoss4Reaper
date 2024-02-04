// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.bank.DrumPadBankImpl;

import java.util.function.Supplier;


/**
 * The data of a drum pad.
 *
 * @author Jürgen Moßgraber
 */
public class DrumPadImpl extends ChannelImpl implements IDrumPad
{
    private final DrumPadBankImpl bank;

    private Supplier<ColorEx>     supplier;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param bank The bank for changing the selection
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public DrumPadImpl (final DataSetupEx dataSetup, final DrumPadBankImpl bank, final int index, final int numSends)
    {
        super (dataSetup, index, numSends);

        this.bank = bank;

        // Always existing
        this.setExists (true);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.bank.setSelectedDrumPad (this.getPosition ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDevices ()
    {
        // Drum pads are not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        return this.supplier == null ? super.getColor () : this.supplier.get ();
    }


    /**
     * Set a color supplier.
     *
     * @param supplier The color supplier
     */
    public void setColorSupplier (final Supplier<ColorEx> supplier)
    {
        this.supplier = supplier;
    }
}
