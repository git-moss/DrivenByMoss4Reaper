// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Adds some helper functions to a JComboBox.
 *
 * @param <E> the type of the elements of this combo box
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class JComboBoxX<E> extends JComboBox<E>
{
    private static final long serialVersionUID = 4796892976317717447L;


    /**
     * Default Constructor.
     */
    public JComboBoxX ()
    {
        // Empty by intention
    }


    /**
     * Constructor.
     *
     * @param items The items to add to the combo box
     */
    public JComboBoxX (final Collection<E> items)
    {
        this.addAll (items);
    }


    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public E getSelectedItem ()
    {
        return (E) super.getSelectedItem ();
    }


    /**
     * Get all items from the list.
     *
     * @param <T> The type into which to cast the items (for convenience)
     * @param clazz To give the T type
     * @return All items from the list
     */
    public <T> Collection<T> getAll (final Class<T> clazz)
    {
        final DefaultComboBoxModel<E> model = this.getModel ();
        final int size = model.getSize ();
        final Collection<T> items = new ArrayList<> (size);
        for (int i = 0; i < size; i++)
            items.add (clazz.cast (model.getElementAt (i)));
        return items;
    }


    /**
     * Clears the list and adds all given items to the list. Selects the first added item in the
     * collection. Disables update changes and keeps the selection.
     *
     * @param items The items to set
     */
    public void setAll (final Collection<E> items)
    {
        final ActionListener [] actionListeners = this.getActionListeners ();
        for (final ActionListener actionListener: actionListeners)
            this.removeActionListener (actionListener);
        final E selectedItem = this.getSelectedItem ();

        this.getModel ().removeAllElements ();
        this.addAll (items);

        if (selectedItem != null)
            this.setSelectedItem (selectedItem);
        for (final ActionListener actionListener: actionListeners)
            this.addActionListener (actionListener);

    }


    /**
     * Adds all given items to the list. Selects the first added item in the list. If the items
     * collection is empty the selection is not changed.
     *
     * @param items The items to add
     */
    public final void addAll (final Collection<E> items)
    {
        if (items.isEmpty ())
            return;
        final DefaultComboBoxModel<E> model = this.getModel ();
        final int insertPos = model.getSize ();
        for (final E o: items)
            model.addElement (o);
        this.setSelectedIndex (insertPos);
    }


    /**
     * Get the DefaultComboBoxModel if this list has set one otherwise an exception is thrown.
     *
     * @return The casted model
     */
    @Override
    public DefaultComboBoxModel<E> getModel ()
    {
        return (DefaultComboBoxModel<E>) super.getModel ();
    }
}
