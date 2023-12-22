// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.empty.EmptyScene;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.SceneImpl;


/**
 * Encapsulates the data of a scene bank.
 *
 * @author Jürgen Moßgraber
 */
public class SceneBankImpl extends AbstractPagedBankImpl<SceneImpl, IScene> implements ISceneBank
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numScenes The number of scenes in the page of the bank
     */
    public SceneBankImpl (final DataSetupEx dataSetup, final int numScenes)
    {
        super (dataSetup, numScenes, EmptyScene.INSTANCE);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.SESSION, enable);
    }


    /** {@inheritDoc}} */
    @Override
    protected SceneImpl createItem (final int position)
    {
        return new SceneImpl (this.dataSetup, this.pageSize == 0 ? 0 : position % this.pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        // There is no selected scene, therefore use the bank offset
        return this.bankOffset > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        // There is no selected scene, therefore use the bank offset
        return this.bankOffset + 1 < this.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        if (position < this.getItemCount ())
            this.setBankOffset (position);
    }
}