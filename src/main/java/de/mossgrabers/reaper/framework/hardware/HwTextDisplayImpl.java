// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;


/**
 * Implementation of a proxy to a text display on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwTextDisplayImpl extends AbstractHwControl implements IHwTextDisplay, IReaperHwControl
{
    private final HwControlLayout layout;
    private final String []       lines;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param numLines The number of lines that the display can show
     */
    public HwTextDisplayImpl (final String id, final int numLines)
    {
        super (null, null);

        this.layout = new HwControlLayout (id);
        this.lines = new String [numLines];
    }


    /** {@inheritDoc}} */
    @Override
    public void setLine (final int line, final String text)
    {
        this.lines[line] = text;
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
        if (bounds == null || this.lines[0] == null)
            return;

        double x = bounds.getX () * scale;
        double y = bounds.getY () * scale;

        double width = bounds.getWidth () * scale;
        double height = (bounds.getHeight () * scale) / this.lines.length;

        final double fontSize = ((GraphicsContextImpl) gc).calculateFontSize (this.lines[0], height, width, 6.0);

        for (int i = 0; i < this.lines.length; i++)
        {
            if (this.lines[i] != null)
                gc.drawTextInBounds (this.lines[i], x, y + i * height, width, height, Align.CENTER, ColorEx.WHITE, fontSize);
        }
    }
}
