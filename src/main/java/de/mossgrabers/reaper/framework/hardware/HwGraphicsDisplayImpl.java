// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwGraphicsDisplay;
import de.mossgrabers.framework.graphics.IBitmap;


/**
 * Implementation of a proxy to a graphics display on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwGraphicsDisplayImpl extends AbstractHwControl implements IHwGraphicsDisplay, IReaperHwControl
{
    private final String id;
    private Bounds       bounds;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param bitmap The bitmap
     */
    public HwGraphicsDisplayImpl (final String id, final IBitmap bitmap)
    {
        super (null, null);

        this.id = id;
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.bounds = new Bounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public String getId ()
    {
        return this.id;
    }


    /** {@inheritDoc} */
    @Override
    public Bounds getBounds ()
    {
        return this.bounds;
    }
}
