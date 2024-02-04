// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


/**
 * A titled separator. This is a text followed by a horizontal separator line.
 *
 * @author Jürgen Moßgraber
 */
public class TitledSeparator extends JPanel
{
    private static final long serialVersionUID = 3546082445448654905L;

    protected JLabel          label;
    protected JSeparator      sep;


    /**
     * Constructor.
     *
     * @param text The text to display as a title
     */
    public TitledSeparator (final String text)
    {
        super (null);

        this.sep = new JSeparator ();
        this.add (this.sep);

        this.label = new JLabel (Functions.getText (text));
        this.add (this.label);

        final Font f = this.label.getFont ();
        this.label.setFont (new Font (f.getFontName (), Font.BOLD, f.getSize ()));
    }


    /**
     * Set the component this is labelling. Can be null if this does not label a Component. If the
     * displayedMnemonic property is set and the labelFor property is also set, the label will call
     * the requestFocus method of the component specified by the labelFor property when the mnemonic
     * is activated.
     *
     * @param c The Component this label is for, or null if the label is not the label for a
     *            component
     */
    public void setLabelFor (final Component c)
    {
        this.label.setLabelFor (c);
    }


    /**
     * Paints the component.
     *
     * @param g The graphics display
     */
    @Override
    protected void paintComponent (final Graphics g)
    {
        final Graphics2D g2 = (Graphics2D) g;
        final Font f = this.label.getFont ();
        final Rectangle2D bounds = f.getStringBounds (this.label.getText (), g2.getFontRenderContext ());

        final Dimension size = this.getSize ();
        final int y = size.height / 2;
        final int x = (int) bounds.getWidth () + 2;
        this.sep.setBounds (x, y, size.width - x, size.height);
        this.label.setBounds (0, 0, x, size.height);

        super.paintComponent (g);
    }


    /**
     * Returns the minimum size of the label as the minimum size of this control.
     *
     * @return The minimum size
     */
    @Override
    public Dimension getMinimumSize ()
    {
        return this.label.getMinimumSize ();
    }


    /**
     * Returns the minimum size of the label as the preferred size of this control.
     *
     * @return The preferred size
     */
    @Override
    public Dimension getPreferredSize ()
    {
        return this.getMinimumSize ();
    }


    /**
     * Enables or disables this component, depending on the value of the parameter <code>b</code>.
     *
     * @param b If <code>true</code>, this component is enabled; otherwise this component is
     *            disabled
     */
    @Override
    public void setEnabled (final boolean b)
    {
        super.setEnabled (b);
        this.label.setEnabled (b);
    }
}
