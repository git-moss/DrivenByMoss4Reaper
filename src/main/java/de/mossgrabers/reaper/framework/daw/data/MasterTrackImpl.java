// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IItemSelectionObserver;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.bank.TrackBankImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.MasterPanningParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.MasterVolumeParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.UserParameterImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * The master track.
 *
 * @author Jürgen Moßgraber
 */
public class MasterTrackImpl extends TrackImpl implements IMasterTrack
{
    private final List<IItemSelectionObserver> observers = new ArrayList<> ();
    private final UserParameterImpl            crossfaderParameter;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param trackBank The track bank for calculating the index
     * @param numSends The number of sends on a page
     * @param numParams The number of parameters
     */
    public MasterTrackImpl (final DataSetupEx dataSetup, final TrackBankImpl trackBank, final int numSends, final int numParams)
    {
        super (dataSetup, trackBank, 0, 1, numSends, 0, numParams, new MasterVolumeParameterImpl (dataSetup, 0.716), new MasterPanningParameterImpl (dataSetup, 0.5));

        this.crossfaderParameter = new UserParameterImpl (dataSetup, 0, null)
        {
            @Override
            protected void sendValue ()
            {
                MasterTrackImpl.this.sendPositionedItemOSC ("user/param/0/value", this.value);
            }
        };

        // Master channel does always exist
        this.setExists (true);

        this.setInternalName ("Master");
        this.setType (ChannelType.MASTER);
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        if (this.trackBank instanceof final TrackBankImpl tb && tb.hasFullFlatTrackList ())
            return this.trackBank.getItemCount () - 1;

        return super.getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.MASTER, enable);
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
    public void addSelectionObserver (final IItemSelectionObserver observer)
    {
        this.observers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        super.setSelected (isSelected);
        for (final IItemSelectionObserver observer: this.observers)
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
    protected Processor getProcessor ()
    {
        return Processor.MASTER;
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