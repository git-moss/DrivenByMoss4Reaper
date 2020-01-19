// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a scene.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneImpl extends ItemImpl implements IScene
{
    private ColorEx color = new ColorEx (0.2, 0.2, 0.2);


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the slot
     */
    public SceneImpl (final DataSetupEx dataSetup, final int index)
    {
        super (dataSetup, index);
        this.setExists (false);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        return this.color;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        this.color = color;
    }


    /** {@inheritDoc} */
    @Override
    public void launch ()
    {
        this.sendSceneOSC ("launch");
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        this.sendSceneOSC ("remove");
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.sendSceneOSC ("select");
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        // Not supported
    }


    protected void sendSceneOSC (final String command)
    {
        this.sender.processNoArg ("scene", this.getPosition () + "/" + command);
    }
}
