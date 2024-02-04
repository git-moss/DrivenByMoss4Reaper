// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import de.mossgrabers.reaper.controller.IControllerInstance;


/**
 * A check-box item containing a controller to be used in a list box.
 *
 * @author Jürgen Moßgraber
 *
 * @param item The item to encapsulate
 */
public record ControllerCheckboxListItem (IControllerInstance item)
{
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
     * Get the running state of the check-box.
     *
     * @return True if selected
     */
    public boolean isRunning ()
    {
        return this.item.isRunning ();
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


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return this.item.toString ();
    }
}
