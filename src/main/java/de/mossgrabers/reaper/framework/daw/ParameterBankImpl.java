// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.framework.daw.data.ParameterImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Encapsulates the data of a parameter bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterBankImpl extends AbstractBankImpl<IParameter> implements IParameterBank
{
    private IParameterPageBank pageBank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param pageBank The page bank
     * @param numParams The number of parameters in the page of the bank
     */
    public ParameterBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final IParameterPageBank pageBank, final int numParams)
    {
        super (host, sender, valueChanger, numParams);
        this.pageBank = pageBank;

        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new ParameterImpl (this.host, this.sender, this.valueChanger, i));
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.pageBank.getItemCount () * this.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.pageBank.canScrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.pageBank.canScrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.pageBank.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.pageBank.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        this.pageBank.scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        this.pageBank.scrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.pageBank.scrollTo (position);
    }
}