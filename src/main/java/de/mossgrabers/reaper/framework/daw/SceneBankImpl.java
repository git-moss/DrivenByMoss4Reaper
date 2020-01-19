// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.empty.EmptyScene;
import de.mossgrabers.reaper.framework.daw.data.SceneImpl;


/**
 * Encapsulates the data of a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneBankImpl extends AbstractPagedBankImpl<SceneImpl, IScene> implements ISceneBank
{
    private final AbstractTrackBankImpl trackBank;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param trackBank The track bank to which the scene bank belongs
     * @param numScenes The number of scenes in the page of the bank
     */
    public SceneBankImpl (final DataSetupEx dataSetup, final AbstractTrackBankImpl trackBank, final int numScenes)
    {
        super (dataSetup, numScenes, EmptyScene.INSTANCE);

        this.trackBank = trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates ("session", enable);
    }


    /** {@inheritDoc}} */
    @Override
    protected SceneImpl createItem (final int position)
    {
        return new SceneImpl (this.dataSetup, this.pageSize == 0 ? 0 : position % this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - 1);
        this.trackBank.updateSlotBanks (this.bankOffset);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        if (this.bankOffset + 1 < this.getItemCount ())
        {
            this.bankOffset += 1;
            this.trackBank.updateSlotBanks (this.bankOffset);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.bankOffset = Math.max (0, this.bankOffset - this.pageSize);
        this.trackBank.updateSlotBanks (this.bankOffset);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        if (this.bankOffset + this.pageSize < this.getItemCount ())
        {
            this.bankOffset += this.pageSize;
            this.trackBank.updateSlotBanks (this.bankOffset);
        }
    }
}