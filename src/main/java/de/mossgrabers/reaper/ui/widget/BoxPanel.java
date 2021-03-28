// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Panel which makes life easier.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BoxPanel extends JPanel
{
    private static final long serialVersionUID = 4048792364380336179L;

    /** GLUE space. */
    public static final int   GLUE             = -1;
    /** No space. */
    public static final int   NONE             = 0;
    /** Small space. */
    public static final int   SMALL            = 4;
    /** Icon space. */
    public static final int   ICON_SPACE       = 7;
    /** Normal space. */
    public static final int   NORMAL           = 12;
    /** Large space. */
    public static final int   LARGE            = 17;

    protected boolean         isVert;
    protected float           fieldAlignment;


    /**
     * Creates a JPanel with a BoxLayout. The field alignment is set to LEFT or TOP depending on
     * alignment.
     *
     * @param alignment X or Y alignment, use BoxLayout.?_AXIS
     * @param addBorderSpace If true a border with space is added
     */
    public BoxPanel (final int alignment, final boolean addBorderSpace)
    {
        // The following line is not necessary because they contain the same value
        // (alignment == BoxLayout.Y_AXIS) ? Component.LEFT_ALIGNMENT :
        // Component.TOP_ALIGNMENT
        this (alignment, Component.LEFT_ALIGNMENT, addBorderSpace);
    }


    /**
     * Creates a JPanel with a BoxLayout.
     *
     * @param alignment X or Y alignment, use BoxLayout.?_AXIS
     * @param fieldAlignment The alignment of the elements, use Component.?_ALIGNMENT
     * @param addBorderSpace If true a border with space is added
     */
    public BoxPanel (final int alignment, final float fieldAlignment, final boolean addBorderSpace)
    {
        this.fieldAlignment = fieldAlignment;
        this.isVert = alignment == BoxLayout.Y_AXIS;
        this.setLayout (new BoxLayout (this, alignment));
        if (addBorderSpace)
            this.setBorder (BorderFactory.createEmptyBorder (NORMAL, NORMAL, NORMAL, NORMAL));
    }


    /**
     * Set the width or height (depending on the orientation of the panel) of all components in this
     * panel to those of the largest component.
     */
    public void sizeEqual ()
    {
        this.sizeEqual (this.isVert);
    }


    /**
     * Set the width or height (depending on the orientation of the panel) of all components in this
     * panel to those of the largest component.
     *
     * @param isVert If true equalizes the components vertically otherwise horizontally
     */
    public void sizeEqual (final boolean isVert)
    {
        final List<JComponent> children = this.getJComponents ();
        final Dimension max = calcMaxDimension (children);
        for (final JComponent comp: children)
        {
            final Dimension dim = comp.getPreferredSize ();
            if (isVert)
                dim.width = max.width;
            else
                dim.height = max.height;
            comp.setMinimumSize (dim);
            comp.setMaximumSize (dim);
            comp.setPreferredSize (dim);
        }
        this.invalidate ();
    }


    /**
     * Wraps the BoxPanel inside the NORTH part of a JPanel with BorderLayout. This is useful to
     * prevent the controls inside the box-panel to be sized vertically.
     *
     * @return The wrapper panel
     */
    public JPanel stickToTop ()
    {
        return this.stickTo (BorderLayout.NORTH);
    }


    /**
     * Wraps the BoxPanel inside the SOUTH part of a JPanel with BorderLayout. This is useful to
     * prevent the controls inside the box-panel to be sized vertically.
     *
     * @return The wrapper panel
     */
    public JPanel stickToBottom ()
    {
        return this.stickTo (BorderLayout.NORTH);
    }


    /**
     * Wraps the BoxPanel inside the LEFT part of a JPanel with BorderLayout. This is useful to
     * prevent the controls inside the box-panel to be sized horizontally.
     *
     * @return The wrapper panel
     */
    public JPanel stickToLeft ()
    {
        return this.stickTo (BorderLayout.WEST);
    }


    /**
     * Wraps the BoxPanel inside the RIGHT part of a JPanel with BorderLayout. This is useful to
     * prevent the controls inside the box-panel to be sized horizontally.
     *
     * @return The wrapper panel
     */
    public JPanel stickToRight ()
    {
        return this.stickTo (BorderLayout.EAST);
    }


    /**
     * Sticks the panel to a BorderLayout direction by wrapping the panl in to a BorderLayout-ed
     * JPanel.
     *
     * @param where The BorderLayout direction
     * @return The wrapper panel
     */
    protected JPanel stickTo (final String where)
    {
        final JPanel wrapper = new JPanel (new BorderLayout ());
        wrapper.add (this, where);
        return wrapper;
    }


    /**
     * Disable/Enables the panel with all its subcomponents.
     *
     * @param enable What to do
     */
    public void setAllEnabled (final boolean enable)
    {
        Functions.enableComponent (this, enable);
    }


    /**
     * Creates and adds a combo-box to the panel.
     *
     * @param <E> The type of the combobox's content
     * @param label The name of the label which is added to the combo-box
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param content The content of the combo-box
     * @return The created combo-box
     */
    public <E> JComboBoxX<E> createComboBox (final String label, final String mnemonic, final int space, final Collection<E> content)
    {
        return this.addComponent (new JComboBoxX<> (content), label, mnemonic, null, space);
    }


    /**
     * Creates and adds a list-box to the panel.
     *
     * @param <E> The type of the combobox's content
     * @param label The name of the label which is added to the listbox
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param content The content of the list-box
     * @return The created list box
     */
    public <E> JListX<E> createListBox (final String label, final String mnemonic, final int space, final Collection<E> content)
    {
        return this.createListBox (label, mnemonic, space, new JListX<> (content));
    }


    /**
     * Adds a list-box to the panel.
     *
     * @param <E> The type of the combobox's content
     * @param label The name of the label which is added to the list-box
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param list The list-box to add
     * @return The created list box
     */
    public <E> JListX<E> createListBox (final String label, final String mnemonic, final int space, final JListX<E> list)
    {
        return this.addComponent (list, label, mnemonic, null, space, true);
    }


    /**
     * Creates and adds a button to the panel.
     *
     * @param label The name of the label which is added to the button
     * @param mnemonic A shortcut for the button
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created button
     */
    public JButton createButton (final String label, final String mnemonic, final int space)
    {
        return (JButton) this.addButton (new JButton (), null, label, mnemonic, null, space);
    }


    /**
     * Creates and adds a button to the panel.
     *
     * @param icon An icon which is displayed on the button
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created button
     */
    public JButton createButton (final Icon icon, final int space)
    {
        return (JButton) this.addButton (new JButton (), icon, null, null, null, space);
    }


    /**
     * Creates and adds a button to the panel.
     *
     * @param icon An icon which is displayed on the button
     * @param label The name of the label which is displayed on the button
     * @param mnemonic A shortcut for the button
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created button
     */
    public JButton createButton (final Icon icon, final String label, final String mnemonic, final int space)
    {
        return (JButton) this.addButton (new JButton (), icon, label, mnemonic, null, space);
    }


    /**
     * Creates and adds a button to the panel.
     *
     * @param icon An icon which is displayed on the button
     * @param label The name of the label which is displayed on the button
     * @param mnemonic A shortcut for the button
     * @param tooltip Tooltip text
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created button
     */
    public JButton createButton (final Icon icon, final String label, final String mnemonic, final String tooltip, final int space)
    {
        return (JButton) this.addButton (new JButton (), icon, label, mnemonic, tooltip, space);
    }


    /**
     * Creates and adds a radio button to the panel.
     *
     * @param label The name of the label which is added to the button
     * @param mnemonic A shortcut for the button
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created radio button
     */
    public JRadioButton createRadioButton (final String label, final String mnemonic, final int space)
    {
        return this.createRadioButton (label, mnemonic, null, space);
    }


    /**
     * Creates and adds a radio button to the panel.
     *
     * @param label The name of the label which is added to the button
     * @param mnemonic A shortcut for the button
     * @param tooltip Tooltip text
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created radio button
     */
    public JRadioButton createRadioButton (final String label, final String mnemonic, final String tooltip, final int space)
    {
        return (JRadioButton) this.addButton (new JRadioButton (), null, label, mnemonic, tooltip, space);
    }


    /**
     * Creates and adds a check box to the panel.
     *
     * @param label The text that is displayed beneath the check box
     * @param mnemonic A shortcut for the check box
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created check box button
     */
    public JCheckBox createCheckBox (final String label, final String mnemonic, final int space)
    {
        return (JCheckBox) this.addButton (new JCheckBox (), null, label, mnemonic, null, space);
    }


    /**
     * Creates and adds a text field to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created text field
     */
    public JTextField createField (final String label, final String mnemonic, final int space)
    {
        return this.createField (label, mnemonic, space, -1);
    }


    /**
     * Creates and adds a text field to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @return The created text field
     */
    public JTextField createField (final String label, final String mnemonic, final int space, final int columns)
    {
        return this.createField (label, mnemonic, space, columns, -1);
    }


    /**
     * Creates and adds a text field to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @param maxChars If not -1 the characters are limited to this number
     * @return The created text field
     */
    public JTextField createField (final String label, final String mnemonic, final int space, final int columns, final int maxChars)
    {
        final JTextField field = this.addField (new JTextField (), label, mnemonic, space, columns);
        if (maxChars != -1)
            field.setDocument (new MaxLengthDocument (maxChars));
        return field;
    }


    /**
     * Creates and adds a password field to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @return The created password field
     */
    public JPasswordField createPasswordField (final String label, final String mnemonic, final int space, final int columns)
    {
        return this.createPasswordField (label, mnemonic, space, columns, -1);
    }


    /**
     * Creates and adds a password field to the panel.
     *
     * @param label The label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @return The created password field
     */
    public JPasswordField createPasswordField (final JLabel label, final String mnemonic, final int space, final int columns)
    {
        return this.createPasswordField (label, mnemonic, space, columns, -1);
    }


    /**
     * Creates and adds a password field to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @param maxChars If not -1 the characters are limited to this number
     * @return The created password field
     */
    public JPasswordField createPasswordField (final String label, final String mnemonic, final int space, final int columns, final int maxChars)
    {
        final JPasswordField field = new JPasswordField ();
        this.addField (field, label, mnemonic, space, columns);
        if (maxChars != -1)
            field.setDocument (new MaxLengthDocument (maxChars));
        return field;
    }


    /**
     * Creates and adds a password field to the panel.
     *
     * @param label The label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @param maxChars If not -1 the characters are limited to this number
     * @return The created password field
     */
    public JPasswordField createPasswordField (final JLabel label, final String mnemonic, final int space, final int columns, final int maxChars)
    {
        final JPasswordField field = new JPasswordField ();
        this.addField (field, label, mnemonic, space, columns);
        if (maxChars != -1)
            field.setDocument (new MaxLengthDocument (maxChars));
        return field;
    }


    /**
     * Creates and adds a text area to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created text area
     */
    public JTextArea createTextArea (final String label, final String mnemonic, final int space)
    {
        return this.createTextArea (label, mnemonic, space, 0, 0);
    }


    /**
     * Creates and adds a text area to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param rows Rows of the text area
     * @param columns Columns of the text area
     * @return The created text area
     */
    public JTextArea createTextArea (final String label, final String mnemonic, final int space, final int rows, final int columns)
    {
        return this.createTextArea (label, mnemonic, space, rows, columns, -1);
    }


    /**
     * Creates and adds a text area to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param rows Rows of the text area
     * @param columns Columns of the text area
     * @param maxChars If not -1 the characters are limited to this number
     * @return The created text area
     */
    public JTextArea createTextArea (final String label, final String mnemonic, final int space, final int rows, final int columns, final int maxChars)
    {
        final JTextArea area = this.addComponent (new JTextArea (rows, columns), label, mnemonic, null, space, true);
        if (maxChars != -1)
            area.setDocument (new MaxLengthDocument (maxChars));
        return area;
    }


    /**
     * Creates and adds a text pane to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created text pane
     */
    public JTextPane createTextPane (final String label, final String mnemonic, final int space)
    {
        return this.createTextPane (label, mnemonic, space, 0);
    }


    /**
     * Creates and adds a text pane to the panel.
     *
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param rows Rows of the text pane
     * @return The created text pane
     */
    public JTextPane createTextPane (final String label, final String mnemonic, final int space, final int rows)
    {
        final JTextPane textPane = this.addComponent (new JTextPane (), label, mnemonic, null, space, true);

        if (rows > 0)
        {
            final Dimension dim = textPane.getPreferredSize ();
            dim.height = rows * textPane.getFontMetrics (textPane.getFont ()).getHeight ();
            // The parents parent is the scroll pane
            final Container scrollPane = textPane.getParent ().getParent ();
            scrollPane.setPreferredSize (dim);
            if (dim.width > 10)
                dim.width = 10;
            scrollPane.setMinimumSize (dim);
        }

        return textPane;
    }


    /**
     * Creates and adds a separator to the panel.
     *
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created separator
     */
    public JSeparator createSeparator (final int space)
    {
        return this.addComponent (new JSeparator (this.isVert ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL), (JLabel) null, null, null, space);
    }


    /**
     * Creates and adds an icon to the panel.
     *
     * @param icon The icon that is displayed
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created icon (label)
     */
    public JLabel createIcon (final Icon icon, final int space)
    {
        return this.addComponent (new JLabel (icon), (JLabel) null, null, null, space);
    }


    /**
     * Creates and adds a label to the panel.
     *
     * @param label The name of the label
     * @param mnemonic A shortcut for the label
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created label
     */
    public JLabel createLabel (final String label, final String mnemonic, final int space)
    {
        return this.addLabel (new JLabel (Functions.getText (label)), mnemonic, space);
    }


    /**
     * Adds a label to the panel.
     *
     * @param label The label to add
     * @param mnemonic A shortcut for the label
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The created label
     */
    public JLabel addLabel (final JLabel label, final String mnemonic, final int space)
    {
        final JLabel aLabel = this.addComponent (label, "", null, null, space, false);
        if (mnemonic != null)
            aLabel.setDisplayedMnemonic (this.getMnemonicChar (mnemonic));
        return aLabel;
    }


    /**
     * Creates space and adds it to the panel.
     *
     * @param space The type of space (NONE, SMALL, NORMAL, LARGE, GLUE)
     */
    public void createSpace (final int space)
    {
        if (space == NONE)
            return;

        if (space == GLUE)
            this.add (this.isVert ? Box.createVerticalGlue () : Box.createHorizontalGlue ());
        else
            this.add (Box.createRigidArea (new Dimension (this.isVert ? 0 : space, this.isVert ? space : 0)));
    }


    /**
     * Adds a component to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final int space)
    {
        return this.addComponent (component, (JLabel) null, null, space);
    }


    /**
     * Adds a component with a label to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param label The name of the label which is added to the component
     * @param mnemonic A shortcut for the label
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final String label, final String mnemonic, final int space)
    {
        return this.addComponent (component, label, mnemonic, space, false);
    }


    /**
     * Adds a component with a label to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param label The label which is added to the component
     * @param mnemonic A shortcut for the label
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final JLabel label, final String mnemonic, final int space)
    {
        return this.addComponent (component, label, mnemonic, space, false);
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
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final String label, final String mnemonic, final String tooltip, final int space)
    {
        return this.addComponent (component, label, mnemonic, tooltip, space, false);
    }


    /**
     * Adds a component with a label to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param label The label which is added to the component
     * @param mnemonic A shortcut for the label
     * @param tooltip Tooltip text
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final JLabel label, final String mnemonic, final String tooltip, final int space)
    {
        return this.addComponent (component, label, mnemonic, tooltip, space, false);
    }


    /**
     * Adds a component with a label to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param label The name of the label which is added to the component
     * @param mnemonic A shortcut for the label
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param addScrollPane If true a the component is wrapped in a scrollpane
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final String label, final String mnemonic, final int space, final boolean addScrollPane)
    {
        return this.addComponent (component, label, mnemonic, null, space, addScrollPane);
    }


    /**
     * Adds a component with a label to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param label The label which is added to the component
     * @param mnemonic A shortcut for the label
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param addScrollPane If true a the component is wrapped in a scrollpane
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final JLabel label, final String mnemonic, final int space, final boolean addScrollPane)
    {
        return this.addComponent (component, label, mnemonic, null, space, addScrollPane);
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
     * @param addScrollPane If true a the component is wrapped in a scroll pane
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final String label, final String mnemonic, final String tooltip, final int space, final boolean addScrollPane)
    {
        final JLabel l = label != null && label.length () > 0 ? new JLabel (Functions.getText (label)) : null;
        return this.addComponent (component, l, mnemonic, tooltip, space, addScrollPane);
    }


    /**
     * Adds a component with a label to the panel.
     *
     * @param <T> The exact type
     * @param component The component to add
     * @param label The label which is added to the component
     * @param mnemonic A shortcut for the label
     * @param tooltip Tooltip text
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param addScrollPane If true a the component is wrapped in a scroll pane
     * @return The added component
     */
    public <T extends JComponent> T addComponent (final T component, final JLabel label, final String mnemonic, final String tooltip, final int space, final boolean addScrollPane)
    {
        if (label != null)
            this.addLabel (label, mnemonic, SMALL).setLabelFor (component);

        if (tooltip != null)
            component.setToolTipText (Functions.getText (tooltip));

        final JComponent c = addScrollPane ? new JScrollPane (component) : component;
        this.add (c);

        if (this.isVert)
            c.setAlignmentX (this.fieldAlignment);
        else
            c.setAlignmentY (this.fieldAlignment);

        this.createSpace (space);

        return component;
    }


    /**
     * Adds an abstract button to the panel.
     *
     * @param button The button to add
     * @param icon An icon which is displayed on the button
     * @param label The name of the label which is added to the button
     * @param mnemonic A shortcut for the button
     * @param tooltip Tooltip text
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @return The added button
     */
    protected AbstractButton addButton (final AbstractButton button, final Icon icon, final String label, final String mnemonic, final String tooltip, final int space)
    {
        if (icon != null)
        {
            button.setIcon (icon);
            button.setIconTextGap (ICON_SPACE);
        }
        button.setText (Functions.getText (label));
        button.setMnemonic (this.getMnemonicChar (mnemonic));
        this.addComponent (button, (JLabel) null, null, tooltip, space);
        return button;
    }


    /**
     * Adds a text field to the panel.
     *
     * @param <T> The exact type
     * @param field The field to add
     * @param label The name of the label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @return The text field
     */
    protected <T extends JTextField> T addField (final T field, final String label, final String mnemonic, final int space, final int columns)
    {
        if (columns != -1)
            field.setColumns (columns);
        return this.addComponent (field, label, mnemonic, space);
    }


    /**
     * Adds a text field to the panel.
     *
     * @param field The field to add
     * @param label The label which is added to the field
     * @param mnemonic A shortcut for the field
     * @param space Add space after the element (NONE, SMALL, NORMAL, LARGE, GLUE)
     * @param columns Columns of the text field
     * @return The text field
     */
    protected JTextField addField (final JTextField field, final JLabel label, final String mnemonic, final int space, final int columns)
    {
        if (columns != -1)
            field.setColumns (columns);
        return this.addComponent (field, label, mnemonic, space);
    }


    /**
     * Reads the text from the functions-resource bundle if text starts with '@'.
     *
     * @param text The text or a message id starting with '@'
     * @return The loaded char
     */
    protected char getMnemonicChar (final String text)
    {
        final String t = Functions.getText (text);
        return t.length () == 0 ? 0 : t.charAt (0);
    }


    /**
     * Calculate the maximum dimension over a number of components.
     *
     * @param children The children from which to calculate the maximum dimension.
     * @return The maximum width and height of the given elements
     */
    private static Dimension calcMaxDimension (final List<JComponent> children)
    {
        final Dimension max = new Dimension (0, 0);
        for (final JComponent comp: children)
        {
            final Dimension dim = comp.getMinimumSize ();
            if (max.width < dim.width)
                max.width = dim.width;
            if (max.height < dim.height)
                max.height = dim.height;
        }
        return max;
    }


    /**
     * Get all swing child components of the panel. Ignores Fillers.
     *
     * @return All Swing components except fillers
     */
    private List<JComponent> getJComponents ()
    {
        final List<JComponent> swingComponents = new ArrayList<> ();
        for (final Component component: this.getComponents ())
        {
            if (component instanceof JComponent jc && !(component instanceof javax.swing.Box.Filler))
                swingComponents.add (jc);
        }
        return swingComponents;
    }
}
