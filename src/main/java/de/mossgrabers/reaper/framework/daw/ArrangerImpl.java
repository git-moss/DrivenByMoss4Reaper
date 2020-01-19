// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IArranger;


/**
 * Encapsulates the Arranger instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ArrangerImpl implements IArranger
{
    /**
     * Constructor
     */
    public ArrangerImpl ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public boolean areCueMarkersVisible ()
    {
        // Not used
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCueMarkerVisibility ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaybackFollowEnabled ()
    {
        // Not used
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePlaybackFollow ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDoubleRowTrackHeight ()
    {
        // Not used
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleTrackRowHeight ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipLauncherVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleClipLauncher ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTimelineVisible ()
    {
        // Not used
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleTimeLine ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public boolean isIoSectionVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIoSection ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean areEffectTracksVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEffectTracks ()
    {
        // Not supported
    }
}