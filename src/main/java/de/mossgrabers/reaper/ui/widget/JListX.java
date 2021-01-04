// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Adds some helper functions to a JList.
 *
 * @param <E> the type of the elements of this list
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class JListX<E> extends JList<E>
{
    private static final long serialVersionUID = 4796892976317717447L;


    /**
     * Default Constructor.
     */
    public JListX ()
    {
        super (new DefaultListModel<> ());
    }


    /**
     * Constructor.
     *
     * @param items The items to add to the list
     */
    public JListX (final Collection<E> items)
    {
        this ();
        this.addAll (items);
    }


    /**
     * Removes the possibility to select items.
     */
    public void disableSelection ()
    {
        this.setSelectionModel (new NoSelectionModel ());
    }


    /**
     * Returns true if the list box has a selected item.
     *
     * @return True if the list box has a selected item
     */
    public boolean hasSelection ()
    {
        return this.getSelectedIndex () != -1;
    }


    /**
     * Selects all items in the list.
     */
    public void selectAll ()
    {
        this.setSelectionInterval (0, this.getModel ().getSize () - 1);
    }


    /**
     * Selects the given items in the list.
     *
     * @param items The items to select
     */
    public void setSelectItems (final Collection<E> items)
    {
        final int [] sels = new int [items.size ()];
        int count = 0;
        for (final E o: items)
            sels[count++] = this.indexOf (o);
        this.setSelectedIndices (sels);
    }


    /**
     * Looks up the index of an item in a list.
     *
     * @param item The item to look up
     * @return The index of the item in the list or -1 if not found
     */
    public int indexOf (final Object item)
    {
        return this.getModel ().indexOf (item);
    }


    /**
     * Removes all items from the list.
     */
    public void clear ()
    {
        this.getModel ().clear ();
    }


    /**
     * Get all items from the list.
     *
     * @return All items from the list
     */
    public Collection<E> getAll ()
    {
        final ListModel<E> model = this.getModel ();
        final int size = model.getSize ();
        final Collection<E> items = new ArrayList<> (size);
        for (int i = 0; i < size; i++)
            items.add (model.getElementAt (i));
        return items;
    }


    /**
     * Clears the list and adds all given items to the list. Selects the first added item in the
     * list.
     *
     * @param items The items to set
     */
    public void setAll (final Collection<E> items)
    {
        this.getModel ().clear ();
        this.addAll (items);
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
        final DefaultListModel<E> model = this.getModel ();
        final int insertPos = model.getSize ();
        for (final E o: items)
            model.addElement (o);
        this.setSelectedIndex (insertPos);
    }


    /**
     * Adds all given items to the list if they are not already present. Selects the first added
     * item in the list. If the items collection is empty the selection is not changed.
     *
     * @param items The items to add
     */
    public void addAllIfNotPresent (final Collection<E> items)
    {
        final DefaultListModel<E> model = this.getModel ();
        final int insertPos = model.getSize ();
        for (final E o: items)
        {
            if (!model.contains (o))
                model.addElement (o);
        }
        if (insertPos != model.getSize ())
            this.setSelectedIndex (insertPos);
    }


    /**
     * Adds an item to the list.
     *
     * @param item The item to add
     */
    public void addItem (final E item)
    {
        final DefaultListModel<E> model = this.getModel ();
        model.addElement (item);
        this.setSelectedIndex (model.getSize () - 1);
    }


    /**
     * Adds an item to the list.
     *
     * @param index Index at which the specified item is to be inserted, if set to -1 the item is
     *            added to the end of the list
     * @param item The item to add
     */
    public void addItem (final int index, final E item)
    {
        if (index == -1)
            this.addItem (item);
        else
        {
            this.getModel ().add (index, item);
            this.setSelectedIndex (index);
        }
    }


    /**
     * Removes the item at the given index from the list.
     *
     * @param index The index of an item
     */
    public void removeItem (final int index)
    {
        final DefaultListModel<E> model = this.getModel ();
        model.remove (index);
        if (!model.isEmpty ())
            this.setSelectedIndex (Math.max (0, index - 1));
    }


    /**
     * Removes the current selection(s). Ensures that an item is selected afterwards.
     */
    public void removeSelectedItems ()
    {
        final DefaultListModel<E> model = this.getModel ();
        final int [] sels = this.getSelectedIndices ();
        if (sels.length == 0)
            return;
        for (int i = sels.length - 1; i >= 0; i--)
            model.removeElementAt (sels[i]);
        if (!model.isEmpty ())
            this.setSelectedIndex (Math.max (0, sels[0] - 1));
    }


    /**
     * Replaces the item at the given index with the new item. Selects the item afterwards.
     *
     * @param index The index at which to replace an item
     * @param item The new item
     */
    public void replaceItem (final int index, final E item)
    {
        final DefaultListModel<E> model = this.getModel ();
        model.removeElementAt (index);
        model.insertElementAt (item, index);
        this.setSelectedIndex (index);
    }


    /**
     * Moves the item at the currently selected index upwards or downwards in the list.
     *
     * @param up Move it upwards if true otherwise downwards
     */
    public void moveItem (final boolean up)
    {
        if (up)
            this.moveItemUp ();
        else
            this.moveItemDown ();
    }


    /**
     * Moves the item at the currently selected index upwards in the list. Ensures that it is still
     * selected. If none is selected or the item is already the first item in the list nothing
     * happens.
     */
    public void moveItemUp ()
    {
        final int index = this.getSelectedIndex ();
        if (index < 1)
            return;
        final DefaultListModel<E> model = this.getModel ();
        final E entry = model.remove (index);
        model.add (index - 1, entry);
        this.setSelectedIndex (index - 1);
    }


    /**
     * Moves the item at the currently selected index downwards in the list. Ensures that it is
     * still selected. If none is selected or the item is already the last item in the list nothing
     * happens.
     */
    public void moveItemDown ()
    {
        final int index = this.getSelectedIndex ();
        final DefaultListModel<E> model = this.getModel ();
        if (index == -1 || index >= model.getSize () - 1)
            return;
        final E entry = model.remove (index);
        model.add (index + 1, entry);
        this.setSelectedIndex (index + 1);
    }


    /**
     * Get the DefaultListModel if this list has set one otherwise an exception is thrown.
     *
     * @return The casted model
     */
    @Override
    public DefaultListModel<E> getModel ()
    {
        return (DefaultListModel<E>) super.getModel ();
    }


    /**
     * {@inheritDoc}
     *
     * Ensures that the newly selected index is visible.
     */
    @Override
    public void setSelectedIndex (final int index)
    {
        super.setSelectedIndex (index);
        this.ensureIndexIsVisible (index);
    }
}
