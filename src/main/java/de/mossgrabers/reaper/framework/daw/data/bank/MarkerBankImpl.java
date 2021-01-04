// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;
import de.mossgrabers.framework.daw.data.empty.EmptyMarker;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.MarkerImpl;


/**
 * A marker bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerBankImpl extends AbstractPagedBankImpl<MarkerImpl, IMarker> implements IMarkerBank
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numMarkers The number of tracks of a bank page
     */
    public MarkerBankImpl (final DataSetupEx dataSetup, final int numMarkers)
    {
        super (dataSetup, numMarkers, EmptyMarker.INSTANCE);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.MARKER, enable);
    }


    /** {@inheritDoc}} */
    @Override
    protected MarkerImpl createItem (final int position)
    {
        return new MarkerImpl (this.dataSetup, this.pageSize == 0 ? 0 : position % this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void addMarker ()
    {
        this.sender.processNoArg (Processor.MARKER, "add");
    }
}
