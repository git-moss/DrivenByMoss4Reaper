// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.UserParameterImpl;


/**
 * Encapsulates the data of a user parameter bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserParameterBankImpl extends AbstractPagedBankImpl<ParameterImpl, IParameter> implements IParameterBank
{
    private final IModel model;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numUserPageSize The number of parameters in the page of the bank
     * @param model The model for getting the selected track
     */
    public UserParameterBankImpl (final DataSetupEx dataSetup, final int numUserPageSize, final IModel model)
    {
        super (dataSetup, numUserPageSize, EmptyParameter.INSTANCE);

        this.model = model;
    }


    /** {@index} */
    @Override
    protected IParameter createItem (final int position)
    {
        return new UserParameterImpl (this.dataSetup, position % this.pageSize, this.model);
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
        final int pageSize = this.getPageSize ();
        this.bankOffset = Math.min (Math.max (0, adjustPage ? position / pageSize * pageSize : position), (this.getItemCount () - 1) / pageSize * pageSize);
        this.firePageObserver ();
    }
}