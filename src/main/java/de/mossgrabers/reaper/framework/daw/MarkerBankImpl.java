// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.reaper.framework.daw.data.MarkerImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * A marker bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerBankImpl extends AbstractBankImpl<IMarker> implements IMarkerBank
{
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
    }


    /** {@inheritDoc} */
    @Override
    public void addMarker ()
    {
        this.sender.sendOSC ("/marker/add", null);
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new MarkerImpl (this.host, this.sender, i));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        this.sender.sendOSC ("/marker/bank/-", null);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        this.sender.sendOSC ("/marker/bank/+", null);
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
