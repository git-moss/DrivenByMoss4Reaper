// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.data.ParameterImpl;


/**
 * Encapsulates the data of a parameter bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterBankImpl extends AbstractBankImpl<IParameter> implements IParameterBank
{
    protected final IParameter emptyParameter;
    protected int              bankOffset = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numParams The number of parameters in the page of the bank
     */
    public ParameterBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numParams)
    {
        super (host, sender, valueChanger, numParams);

        this.initItems ();

        this.emptyParameter = new ParameterImpl (host, sender, valueChanger, -1);
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        // Items are added on the fly in getItem
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return this.bankOffset - this.pageSize >= 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return this.bankOffset + this.pageSize < this.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        if (this.bankOffset + this.pageSize < this.getItemCount ())
            this.bankOffset += this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        final int offset = this.pageSize * this.pageSize;
        this.bankOffset = Math.max (0, this.bankOffset - offset);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        final int offset = this.pageSize * this.pageSize;
        if (this.bankOffset + offset < this.getItemCount ())
            this.bankOffset += offset;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.bankOffset = position;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getItem (final int index)
    {
        final int id = this.bankOffset + index;
        return id >= 0 && id < this.getItemCount () ? this.getParameter (id) : this.emptyParameter;
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.bankOffset;
    }


    /**
     * Get a track from the track list. No paging is applied.
     *
     * @param position The position of the track
     * @return The track
     */
    public ParameterImpl getParameter (final int position)
    {
        synchronized (this.items)
        {
            final int size = this.items.size ();
            final int diff = position - size + 1;
            if (diff > 0)
            {
                for (int i = 0; i < diff; i++)
                    this.items.add (new ParameterImpl (this.host, this.sender, this.valueChanger, (size + i) % this.pageSize));
            }
            return (ParameterImpl) this.items.get (position);
        }
    }


    /**
     * Set the number of parameters.
     *
     * @param count The number of parameters
     */
    public void setItemCount (final int count)
    {
        this.itemCount = count;
        this.bankOffset = 0;
    }
}