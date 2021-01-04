// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;


/**
 * A titled border which draws a background with a gradient color effect.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GradientTitledBorder extends TitledBorder
{
    private static final long serialVersionUID = 3257565118169625144L;

    Color                     gradientColor1;
    Color                     gradientColor2;


    /**
     * Constructor. Uses LAF colors.
     *
     * @param title The text of the title
     */
    public GradientTitledBorder (final String title)
    {
        this (title, UIManager.getColor ("textHighlightText"), UIManager.getColor ("controlShadow"), UIManager.getColor ("control"));
    }


    /**
     * Constructor.
     *
     * @param title The text of the title
     * @param titleColor The color of the title
     * @param gradientColor1 The starting color of the gradient on the left
     * @param gradientColor2 The ending color of the gradient on the right
     */
    public GradientTitledBorder (final String title, final Color titleColor, final Color gradientColor1, final Color gradientColor2)
    {
        super (null, Functions.getText (title));
        this.setTitleColor (titleColor);

        this.gradientColor1 = gradientColor1;
        this.gradientColor2 = gradientColor2;

        Font f = this.getTitleFont ();
        if (f == null)
        {
            f = UIManager.getFont ("Label.font");
            if (f == null)
                f = new Font (Font.SANS_SERIF, Font.BOLD, 12);
        }

        this.setTitleFont (f);
    }


    /**
     * Paints the gradient titled border.
     *
     * @param c The component for which this border is being painted
     * @param g The paint graphics
     * @param x The x position of the painted border
     * @param y The y position of the painted border
     * @param width The width of the painted border
     * @param height The height of the painted border
     */
    @Override
    public void paintBorder (final Component c, final Graphics g, final int x, final int y, final int width, final int height)
    {
        if (!(g instanceof Graphics2D))
            throw new ClassCastException ("Parameter g must be an instance of Graphics2D but is " + g.getClass () + ".");
        final Graphics2D g2 = (Graphics2D) g;
        g2.setPaint (new GradientPaint (x, y, this.gradientColor1, (float) x + width, y, this.gradientColor2));
        final int h = this.getBorderInsets (c).top;
        g2.fillRect (x, y, width, y + h);
        final Font f = this.getTitleFont ();
        final Rectangle2D bounds = f.getStringBounds (this.getTitle (), g2.getFontRenderContext ());
        super.paintBorder (c, g2, x, y + (int) (h - bounds.getHeight ()) / 2, width, height);
    }


    /**
     * Gets the insets of the border.
     *
     * @param c The component for which to get the insets of the border
     * @return The insets of the border
     */
    @Override
    public Insets getBorderInsets (final Component c)
    {
        return new Insets (7 + this.getTitleFont ().getSize () + 7, 0, 0, 0);
    }


    /**
     * No cascaded border possible, always returns null.
     *
     * @return null
     */
    @Override
    public Border getBorder ()
    {
        return null;
    }
}