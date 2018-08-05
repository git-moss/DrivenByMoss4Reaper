// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.reaper.framework.daw.data.SceneImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Encapsulates the data of a scene bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneBankImpl extends AbstractBankImpl<IScene> implements ISceneBank
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param numScenes The number of scenes in the page of the bank
     */
    public SceneBankImpl (final IHost host, final MessageSender sender, final int numScenes)
    {
        super (null, null, null, numScenes);
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new SceneImpl (this.host, this.sender, i));
    }
}