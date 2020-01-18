// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwGraphicsDisplay;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IGraphicsContext;


/**
 * Implementation of a proxy to a graphics display on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwGraphicsDisplayImpl extends AbstractHwControl implements IHwGraphicsDisplay, IReaperHwControl
{
    private final HwControlLayout layout;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param bitmap The bitmap
     */
    public HwGraphicsDisplayImpl (final String id, final IBitmap bitmap)
    {
        super (null, null);

        this.layout = new HwControlLayout (id);
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.layout.setBounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double scale)
    {
        final Bounds bounds = this.layout.getBounds ();
        if (bounds == null)
            return;
        gc.fillRectangle (bounds.getX () * scale, bounds.getY () * scale, bounds.getWidth () * scale, bounds.getHeight () * scale, ColorEx.BLACK);

        // TODO Draw the display content

    }
}
