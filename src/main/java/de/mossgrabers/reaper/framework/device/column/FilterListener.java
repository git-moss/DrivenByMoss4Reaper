// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

/**
 * Callback interface for selection changes of a filter.
 *
 * @author Jürgen Moßgraber
 */
public interface FilterListener
{
    /**
     * Called when the filter has changed.
     */
    void hasChanged ();
}
