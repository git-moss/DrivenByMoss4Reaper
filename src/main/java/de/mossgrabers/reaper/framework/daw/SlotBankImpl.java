// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.reaper.framework.daw.data.SlotImpl;
import de.mossgrabers.transformator.communication.MessageSender;

import java.util.Collections;
import java.util.List;


/**
 * Encapsulates the data of a slot bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotBankImpl extends AbstractBankImpl<ISlot> implements ISlotBank
{
    private int trackIndex;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param trackIndex The track index to which the slot bank belongs
     * @param numSlots The number of slots in the page of the bank
     */
    public SlotBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int trackIndex, final int numSlots)
    {
        super (host, sender, valueChanger, numSlots);
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSelectedItem ()
    {
        // Not supported but provide one to support e.g. clip duplication
        return this.items.get (0);
    }


    /** {@inheritDoc} */
    @Override
    public List<ISlot> getSelectedItems ()
    {
        return Collections.singletonList (this.getSelectedItem ());
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getEmptySlot (final int startFrom)
    {
        // There are no slots in Reaper but to make it possible to create a midi item on a track
        // we return a fake slot.
        return this.items.get (0);
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new SlotImpl (this.host, this.sender, this.trackIndex, i));
    }
}