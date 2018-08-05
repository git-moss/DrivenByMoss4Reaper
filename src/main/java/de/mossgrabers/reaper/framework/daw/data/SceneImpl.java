// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Encapsulates the data of a scene.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneImpl extends ItemImpl implements IScene
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param index The index of the slot
     */
    public SceneImpl (final IHost host, final MessageSender sender, final int index)
    {
        super (host, sender, index);
        this.setExists (false);
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Scene " + this.getIndex ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return new double []
        {
            0,
            0,
            0
        };
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void launch ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        // Not supported
    }
}
