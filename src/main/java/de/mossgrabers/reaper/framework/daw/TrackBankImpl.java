// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * A track bank of all instrument and audio tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackBankImpl extends AbstractTrackBankImpl
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numTracks The number of tracks in a bank page
     * @param numScenes The number of scenes in a bank page
     * @param numSends The number of sends in a bank page
     * @param hasFlatTrackList True if group navigation should not be supported, instead all tracks
     *            are flat
     */
    public TrackBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends, final boolean hasFlatTrackList)
    {
        super (host, sender, valueChanger, numTracks, numScenes, numSends);
    }


    /**
     * Handles track changes. Notifies all track change observers.
     * 
     * @param track The de-/selected track
     * @param isSelected True if selected
     */
    public void handleBankTrackSelection (final ITrack track, final boolean isSelected)
    {
        final int position = track.getPosition ();

        if (isSelected)
        {
            // Is track on current page? If not adjust the page
            if (position < this.bankOffset || position >= this.bankOffset + this.pageSize)
                this.bankOffset = (position / this.pageSize) * this.pageSize;
        }

        this.notifySelectionObservers (track.getIndex (), isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        for (int i = 0; i < this.getPageSize (); i++)
        {
            final ISend send = this.getItem (i).getSendBank ().getItem (sendIndex);
            if (send.doesExist ())
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return this.canEditSend (sendIndex) ? "Send " + (sendIndex + 1) : "";
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        return false;
    }
}