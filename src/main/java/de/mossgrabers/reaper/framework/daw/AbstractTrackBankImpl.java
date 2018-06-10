// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.AbstractChannelBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractChannelBank
{
    private MessageSender sender;
    private IHost         host;

    // Set the intial value pretty so navigation works even if TCP communication may be broken
    private int           trackCount = 1000;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractTrackBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        super (valueChanger, numTracks, numScenes, numSends);
        this.sender = sender;
        this.host = host;
    }


    /**
     * Initialise all observers.
     */
    public void init ()
    {
        this.tracks = this.createTracks (this.numTracks);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackCount ()
    {
        return this.trackCount;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollTracksUp ()
    {
        return this.tracks[0].getPosition () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollTracksDown ()
    {
        final ITrack sel = this.getSelectedTrack ();
        return sel != null && sel.getPosition () < this.getTrackCount () - 1;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksUp ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksDown ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksPageUp ()
    {
        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getIndex () + 1 + "/select", Integer.valueOf (0));
        this.sendTrackOSC ("bank/-", null);
        this.sendTrackOSC (this.getNumTracks () + "/select", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTracksPageDown ()
    {
        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getIndex () + 1 + "/select", Integer.valueOf (0));
        this.sendTrackOSC ("bank/+", null);
        this.sendTrackOSC ("1/select", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToChannel (final int channel)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToScene (final int position)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackPositionFirst ()
    {
        return this.getTrack (0).getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackPositionLast ()
    {
        for (int i = 7; i >= 0; i--)
        {
            final int pos = this.getTrack (i).getPosition ();
            if (pos >= 0)
                return pos;
        }
        return -1;
    }


    /**
     * Create all track data and setup observers.
     *
     * @param count The number of tracks of the track bank page
     * @return The created data
     */
    protected ITrack [] createTracks (final int count)
    {
        final ITrack [] trackData = new TrackImpl [count];
        for (int i = 0; i < count; i++)
            trackData[i] = new TrackImpl (this.host, this.sender, this.valueChanger, i, this.numSends, this.numScenes);
        return trackData;
    }


    /**
     * Sets the number of tracks.
     *
     * @param trackCount The number of tracks
     */
    public void setTrackCount (final int trackCount)
    {
        this.trackCount = trackCount;
    }


    protected void sendTrackOSC (final String command, final Object value)
    {
        this.sender.sendOSC ("/track/" + command + "/", value);
    }


    /**
     * Handles track changes. Notifies all track change observers.
     *
     * @param index The index of the newly de-/selected track
     * @param isSelected True if selected
     */
    public void handleBankTrackSelection (final int index, final boolean isSelected)
    {
        if (index < 0)
            return;
        this.getTrack (index).setSelected (isSelected);
        this.notifyTrackSelectionObservers (index, isSelected);
    }
}