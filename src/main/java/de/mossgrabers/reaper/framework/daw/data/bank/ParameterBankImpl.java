// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;


/**
 * Encapsulates the data of a parameter bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterBankImpl extends AbstractPagedBankImpl<ParameterImpl, IParameter> implements IParameterBank
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numParams The number of parameters in the page of the bank
     */
    public ParameterBankImpl (final DataSetupEx dataSetup, final int numParams)
    {
        super (dataSetup, numParams, EmptyParameter.INSTANCE);
    }


    /** {@inheritDoc}} */
    @Override
    protected ParameterImpl createItem (final int position)
    {
        return new ParameterImpl (this.dataSetup, position % this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.scrollTo (this.bankOffset - this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.scrollTo (this.bankOffset + this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.scrollTo (this.bankOffset - this.pageSize * this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.scrollTo (this.bankOffset + this.pageSize * this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.scrollTo (position, true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        if (position < 0 || position >= this.getItemCount ())
            return;
        final int pageSize = this.getPageSize ();
        this.bankOffset = Math.min (Math.max (0, adjustPage ? position / pageSize * pageSize : position), this.getItemCount () - 1);
        this.firePageObserver ();
    }
}