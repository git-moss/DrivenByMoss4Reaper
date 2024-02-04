// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui;

/**
 * Interface to get access to the lazy instantiated main frame window.
 *
 * @author Jürgen Moßgraber
 */
public interface WindowManager
{
    /**
     * Get the main window, create it if necessary.
     *
     * @return The main window
     */
    MainFrame getMainFrame ();
}
