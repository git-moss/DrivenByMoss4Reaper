// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.daw.data.MarkerImpl;


/**
 * A marker bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerBankImpl extends AbstractBankImpl<IMarker> implements IMarkerBank
{
    protected final IMarker emptyMarker;
    protected int           bankOffset = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numMarkers The number of tracks of a bank page
     */
    public MarkerBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numMarkers)
    {
        super (host, sender, valueChanger, numMarkers);
        this.initItems ();

        this.emptyMarker = new MarkerImpl (host, sender, -1);
    }


    /** {@inheritDoc} */
    @Override
    public void addMarker ()
    {
        this.sender.processNoArg ("marker", "add");
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
    public void selectPreviousPage ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        if (this.bankOffset + this.pageSize < this.getItemCount ())
            this.bankOffset += this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public IMarker getItem (final int index)
    {
        final int id = this.bankOffset + index;
        return id >= 0 && id < this.getItemCount () ? this.getMarker (id) : this.emptyMarker;
    }


    /**
     * Get a marker from the marker list. No paging is applied.
     *
     * @param position The position of the marker
     * @return The marker
     */
    public MarkerImpl getMarker (final int position)
    {
        synchronized (this.items)
        {
            final int size = this.items.size ();
            final int diff = position - size + 1;
            if (diff > 0)
            {
                for (int i = 0; i < diff; i++)
                    this.items.add (new MarkerImpl (this.host, this.sender, this.pageSize == 0 ? 0 : (size + i) % this.pageSize));
            }
            return (MarkerImpl) this.items.get (position);
        }
    }


    /**
     * Sets the number of markers.
     *
     * @param markerCount The number of markers
     */
    public void setMarkerCount (final int markerCount)
    {
        this.itemCount = markerCount;
    }
}
