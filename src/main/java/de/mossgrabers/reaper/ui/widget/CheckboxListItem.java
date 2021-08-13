// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import de.mossgrabers.reaper.controller.IControllerInstance;


/**
 * A check-box item to be used in a list box.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CheckboxListItem
{
    private final IControllerInstance item;


    /**
     * Constructor.
     *
     * @param item The item to encapsulate
     */
    public CheckboxListItem (final IControllerInstance item)
    {
        this.item = item;
    }


    /**
     * Get the selection state of the check-box.
     *
     * @return True if selected
     */
    public boolean isSelected ()
    {
        return this.item.isEnabled ();
    }


    /**
     * Set the selection state of the check-box.
     *
     * @param isSelected True to set
     */
    public void setSelected (final boolean isSelected)
    {
        this.item.setEnabled (isSelected);
    }


    /**
     * Get the encapsulated item.
     *
     * @return The item
     */
    public IControllerInstance getItem ()
    {
        return this.item;
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return this.item.toString ();
    }
}
