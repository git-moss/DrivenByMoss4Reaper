// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


/**
 * JPanel with two columns. The left column contains the labels and the right column the controls.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TwoColsPanel extends BoxPanel
{
    private static final long serialVersionUID = 3618981170092914480L;

    protected BoxPanel        left             = new BoxPanel (BoxLayout.Y_AXIS, Component.TOP_ALIGNMENT, false);
    protected BoxPanel        right            = new BoxPanel (BoxLayout.Y_AXIS, Component.TOP_ALIGNMENT, false);


    /**
     * Creates a JPanel with two columns.
     *
     * @param addBorderSpace If true a border with space is added
     */
    public TwoColsPanel (final boolean addBorderSpace)
    {
        super (BoxLayout.Y_AXIS, addBorderSpace);

        this.setLayout (new BorderLayout (NORMAL, NONE));
        this.add (this.left, BorderLayout.WEST);
        this.add (this.right, BorderLayout.CENTER);
    }


    /**
     * Get the left column which contains the labels.
     *
     * @return The left column which contains the labels
     */
    public BoxPanel getLeft ()
    {
        return this.left;
    }


    /**
     * Get the right column which contains the controls.
     *
     * @return The right column which contains the controls
     */
    public BoxPanel getRight ()
    {
        return this.right;
    }


    /** {@inheritDoc} */
    @Override
    public <T extends JComponent> T addComponent (final T component, final JLabel label, final String mnemonic, final String tooltip, final int space, final boolean addScrollPane)
    {
        return this.addComponent (component, label, mnemonic, tooltip, space, addScrollPane, true);
    }


    /**
     * Adds a component with a label to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param label The name of the label which is added to the component
     * @param mnemonic A shortcut for the label
     * @param tooltip Tooltip text
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param addScrollPane If true a the component is wrapped in a scrollpane
     * @param adjustSize If true size changes of the component are reflected in the label
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final JLabel label, final String mnemonic, final String tooltip, final int space, final boolean addScrollPane, final boolean adjustSize)
    {
        final JLabel l = label == null ? new JLabel ("") : label;
        l.setLabelFor (component);
        if (mnemonic != null)
            l.setDisplayedMnemonic (this.getMnemonicChar (mnemonic));
        Functions.asHighAs (component, l);

        final JComponent c = addScrollPane ? new JScrollPane (component) : component;

        this.left.addComponent (l, (JLabel) null, mnemonic, tooltip, space, false);
        this.right.addComponent (c, (JLabel) null, null, tooltip, space, false);

        if (adjustSize)
            c.addComponentListener (new SizeAdjuster (l, c));

        l.setVerticalAlignment (SwingConstants.TOP);

        return component;
    }


    /**
     * Set the width or height (depending on the orientation of the panel) of all components in this
     * panel to those of the largest component.
     *
     * @param isVert If true equalizes the components vertically otherwise horizontally
     */
    @Override
    public void sizeEqual (final boolean isVert)
    {
        this.right.sizeEqual (isVert);
    }


    /**
     * Adjusts the height of the labels to the height of the matching control.
     */
    class SizeAdjuster extends ComponentAdapter
    {
        JLabel     label;
        JComponent component;


        /**
         * Constructor.
         *
         * @param label The label to adjust
         * @param component The component to adjust
         */
        public SizeAdjuster (final JLabel label, final JComponent component)
        {
            this.label = label;
            this.component = component;
        }


        /** {@inheritDoc} */
        @Override
        public void componentResized (final ComponentEvent e)
        {
            final Dimension size = this.label.getSize ();
            size.height = this.component.getSize ().height;
            this.label.setMinimumSize (size);
            this.label.setPreferredSize (size);
            this.label.setMaximumSize (size);
            this.label.invalidate ();
            TwoColsPanel.this.validate ();
        }
    }
}
