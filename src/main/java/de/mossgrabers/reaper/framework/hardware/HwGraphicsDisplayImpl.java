// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwGraphicsDisplay;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.reaper.framework.graphics.BitmapImpl;
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;


/**
 * Implementation of a proxy to a graphics display on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public class HwGraphicsDisplayImpl extends AbstractHwControl implements IHwGraphicsDisplay, IReaperHwControl
{
    private final HwControlLayout layout;
    private final IBitmap         bitmap;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param bitmap The bitmap
     */
    public HwGraphicsDisplayImpl (final String id, final IBitmap bitmap)
    {
        super (null, null);

        this.bitmap = bitmap;
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
        ((BitmapImpl) this.bitmap).drawScaledImage (((GraphicsContextImpl) gc).getGraphics (), (int) (bounds.x () * scale), (int) (bounds.y () * scale), (int) (bounds.width () * scale), (int) (bounds.height () * scale));
    }


    /** {@inheritDoc} */
    @Override
    public void mouse (final int mouseEvent, final double x, final double y, final double scale)
    {
        // No interaction with displays
    }
}
