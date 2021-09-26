// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.reaper.communication.Processor;
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
    public String getName ()
    {
        final String name = super.getName ();
        return this.doesExist () && name != null && name.isBlank () ? "Scene " + (this.getPosition () + 1) : name;
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
        this.sendPositionedItemOSC ("launch");
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        this.sendPositionedItemOSC ("remove");
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.sendPositionedItemOSC ("select");
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.sendPositionedItemOSC ("duplicate");
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.SCENE;
    }
}
