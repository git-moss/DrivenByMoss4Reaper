// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.graphics;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IImage;
import de.mossgrabers.transformator.util.FontCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;


/**
 * Implementation for the graphics context.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GraphicsContextImpl implements IGraphicsContext
{
    private static final FontCache FONT_CACHE = new FontCache ();

    private Graphics2D             gc;


    /**
     * Constructor.
     *
     * @param gc The Bitwig graphics context
     */
    public GraphicsContextImpl (final Graphics2D gc)
    {
        configureGraphics (gc);
        this.gc = gc;
    }


    /** {@inheritDoc} */
    @Override
    public void drawLine (final double x1, final double y1, final double x2, final double y2, final ColorEx color)
    {
        this.setColor (color);
        this.gc.drawLine ((int) x1, (int) y1, (int) x2, (int) y2);
    }


    /** {@inheritDoc} */
    @Override
    public void fillRectangle (final double x, final double y, final double width, final double height, final ColorEx color)
    {
        this.setColor (color);
        this.gc.fillRect ((int) x, (int) y, (int) width, (int) height);
    }


    /** {@inheritDoc} */
    @Override
    public void strokeRectangle (final double left, final double top, final double width, final double height, final ColorEx color)
    {
        this.strokeRectangle (left, top, width, height, color, 1);
    }


    /** {@inheritDoc} */
    @Override
    public void strokeRectangle (final double left, final double top, final double width, final double height, final ColorEx color, final double lineWidth)
    {
        final Stroke oldStroke = this.gc.getStroke ();
        this.setColor (color);
        this.gc.setStroke (new BasicStroke ((float) lineWidth));
        this.gc.drawRect ((int) left, (int) top, (int) width, (int) height);
        this.gc.setStroke (oldStroke);
    }


    /** {@inheritDoc} */
    @Override
    public void fillRoundedRectangle (final double left, final double top, final double width, final double height, final double radius, final ColorEx fillColor)
    {
        this.setColor (fillColor);
        final int arcHeight = (int) (radius * 2.5);
        this.gc.fillRoundRect ((int) left, (int) top, (int) width, (int) height, arcHeight, arcHeight);
    }


    /** {@inheritDoc} */
    @Override
    public void fillGradientRoundedRectangle (final double left, final double top, final double width, final double height, final double radius, final ColorEx color1, final ColorEx color2)
    {
        final Paint oldPaint = this.gc.getPaint ();
        this.gc.setPaint (new GradientPaint ((int) left, (int) (top + 1), convertColor (color1), (int) left, (int) (top + height), convertColor (color2)));
        final int arcHeight = (int) (radius * 2.5);
        this.gc.fillRoundRect ((int) left, (int) top, (int) width, (int) height, arcHeight, arcHeight);
        this.gc.setPaint (oldPaint);
    }


    /** {@inheritDoc} */
    @Override
    public void fillTriangle (final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final ColorEx lineColor)
    {
        this.setColor (lineColor);

        this.gc.fillPolygon (new int []
        {
            (int) x1,
            (int) x2,
            (int) x3
        }, new int []
        {
            (int) y1,
            (int) y2,
            (int) y3
        }, 3);
    }


    /** {@inheritDoc} */
    @Override
    public void fillCircle (double x, double y, double radius, ColorEx fillColor)
    {
        this.setColor (fillColor);
        final int size = (int) (2 * radius);
        this.gc.fillOval ((int) (x - radius), (int) (y - radius), size, size);
    }


    /** {@inheritDoc} */
    @Override
    public void drawTextInBounds (final String text, final double x, final double y, final double width, final double height, final Align alignment, final ColorEx color, final double fontSize)
    {
        this.drawTextInBounds (text, x, y, width, height, alignment, color, null, fontSize);
    }


    /** {@inheritDoc} */
    @Override
    public void drawTextInBounds (final String text, final double x, final double y, final double width, final double height, final Align alignment, final ColorEx color, final ColorEx backgroundColor, final double fontSize)
    {
        if (text == null || text.length () == 0)
            return;

        this.setColor (color);
        this.gc.setFont (FONT_CACHE.getFont ((int) fontSize));

        final Dimension dim = this.getTextDims (text);
        this.gc.clipRect ((int) x, (int) y, (int) width, (int) height);
        final int posX;
        switch (alignment)
        {
            case LEFT:
                posX = (int) x;
                break;

            case CENTER:
            default:
                posX = (int) (x + (width - dim.width) / 2);
                break;
        }

        final double textDescent = this.getTextDescent (text);
        final int posY = (int) (y + height - (height - dim.height) / 2 - textDescent);

        if (backgroundColor != null)
        {
            final double inset = 12.0;
            this.fillRoundedRectangle (posX - inset, posY - dim.height - inset, dim.width + 2 * inset, dim.height + 2 * inset, inset, backgroundColor);
        }

        this.gc.drawString (text, posX, posY);
        this.gc.setClip (null);
    }


    /** {@inheritDoc} */
    @Override
    public void drawTextInHeight (final String text, final double x, final double y, final double height, final ColorEx color, final double fontSize)
    {
        if (text == null || text.length () == 0)
            return;

        this.setColor (color);
        this.gc.setFont (FONT_CACHE.getFont ((int) fontSize));
        final Dimension dim = this.getTextDims (text);
        this.gc.drawString (text, (int) x, (int) (y + height - (height - dim.height) / 2 - this.getTextDescent ("Hg")));
    }


    /** {@inheritDoc} */
    @Override
    public void drawImage (final IImage image, final double x, final double y)
    {
        this.gc.drawImage (((SVGImage) image).getImage (), (int) x, (int) y, null);

    }


    /** {@inheritDoc} */
    @Override
    public void maskImage (final IImage image, final double x, final double y, final ColorEx maskColor)
    {
        SVGImage svgImage = (SVGImage) image;
        try
        {
            svgImage = SVGImage.getSVGImage (svgImage.getImageName (), convertColor (maskColor));
        }
        catch (final IOException ex)
        {
            // Ignore
        }

        this.gc.drawImage (svgImage.getImage (), (int) x, (int) y, null);
    }


    /** {@inheritDoc} */
    @Override
    public double calculateFontSize (final double maxHeight, final double maxWidth)
    {
        final String maxString = "G#5";
        final double minSize = 12.0;

        double size = minSize;
        double fittingSize = -1;
        while (size < maxHeight)
        {
            this.gc.setFont (FONT_CACHE.getFont ((int) size));
            final double width = this.getTextDims (maxString).getWidth ();
            if (width > maxWidth)
                break;
            fittingSize = size;
            size += 1.0;
        }
        return fittingSize;
    }


    /**
     * Makes several graphic settings on the graphics context.
     *
     * @param gc The graphics context
     */
    private static void configureGraphics (final Graphics2D gc)
    {
        gc.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gc.setRenderingHint (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gc.setRenderingHint (RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }


    /**
     * Get the width and the height of a text string.
     *
     * @param text The text to draw
     * @return The dimension of the text string
     */
    public Dimension getTextDims (final String text)
    {
        final FontMetrics fm = this.gc.getFontMetrics ();
        final Rectangle2D bounds = fm.getStringBounds (text, this.gc);
        final LineMetrics lm = fm.getFont ().getLineMetrics (text, this.gc.getFontRenderContext ());
        final double width = bounds.getWidth ();
        bounds.setRect (bounds.getX (), bounds.getY (), width, lm.getHeight ());
        return new Dimension ((int) Math.round (width), (int) Math.round (bounds.getHeight ()));
    }


    /**
     * Get the distance from the text's baseline to its bottom edge.
     *
     * @param text The text to draw
     * @return The distance from the text's baseline to its bottom edge
     */
    public int getTextDescent (final String text)
    {
        return Math.round (this.gc.getFont ().getLineMetrics (text, this.gc.getFontRenderContext ()).getDescent ());
    }


    protected void setColor (final ColorEx color)
    {
        this.gc.setColor (convertColor (color));
    }


    private static Color convertColor (final ColorEx color)
    {
        return new Color ((float) color.getRed (), (float) color.getGreen (), (float) color.getBlue ());
    }
}
