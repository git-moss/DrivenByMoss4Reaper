// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.observer.NoteObserver;


/**
 * A track bank of all effect tracks. Not used in Reaper.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EffectTrackBankImpl extends AbstractTrackBankImpl
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numTracks The number of track of a bank page
     * @param numScenes The number of scenes of a bank page
     */
    public EffectTrackBankImpl (final DataSetupEx dataSetup, final int numTracks, final int numScenes)
    {
        super (dataSetup, numTracks, numScenes, 0);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public void addNoteObserver (final NoteObserver observer)
    {
        // No notes on FX tracks
    }
}