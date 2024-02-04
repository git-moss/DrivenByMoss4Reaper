// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.observer.INoteObserver;
import de.mossgrabers.reaper.framework.daw.ApplicationImpl;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * A track bank of all effect tracks. Not used in Reaper.
 *
 * @author Jürgen Moßgraber
 */
public class EffectTrackBankImpl extends AbstractTrackBankImpl
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param application The application
     * @param numTracks The number of track of a bank page
     * @param sceneBank The scene bank
     * @param numScenes The number of scenes of a bank page
     * @param numParams The number of parameters
     */
    public EffectTrackBankImpl (final DataSetupEx dataSetup, final ApplicationImpl application, final int numTracks, final ISceneBank sceneBank, final int numScenes, final int numParams)
    {
        super (dataSetup, application, numTracks, sceneBank, numScenes, 0, numParams);
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
    public void addNoteObserver (final INoteObserver observer)
    {
        // No notes on FX tracks
    }
}