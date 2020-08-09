// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.ItemSelectionObserver;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.bank.TrackBankImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * The master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterTrackImpl extends TrackImpl implements IMasterTrack
{
    private final List<ItemSelectionObserver> observers = new ArrayList<> ();
    private UserParameterImpl                 crossfaderParameter;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param trackBank The trackbank for calculating the index
     * @param numSends The number of sends on a page
     */
    public MasterTrackImpl (final DataSetupEx dataSetup, final TrackBankImpl trackBank, final int numSends)
    {
        super (dataSetup, trackBank, 0, 1, numSends, 0);

        this.crossfaderParameter = new UserParameterImpl (dataSetup, 0, null)
        {
            @Override
            protected void sendUpdate ()
            {
                MasterTrackImpl.this.sendTrackOSC ("user/param/0/value", this.value);
            }
        };

        // Master channel does always exist
        this.setExists (true);

        this.setName ("Master");
        this.setType (ChannelType.MASTER);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates ("mastertrack", enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return (this.trackBank.getItemCount () - 1) % this.trackBank.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        // Master track is no group
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final ItemSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        super.setSelected (isSelected);
        for (final ItemSelectionObserver observer: this.observers)
            observer.call (-1, isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setRecArm (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    protected String getProcessor ()
    {
        return "master";
    }


    /** {@inheritDoc} */
    @Override
    protected String createCommand (final String command)
    {
        return command;
    }


    /**
     * Get the first track FX parameter acting as the crossfader parameter.
     *
     * @return The parameter
     */
    public ParameterImpl getCrossfaderParameter ()
    {
        return this.crossfaderParameter;
    }
}