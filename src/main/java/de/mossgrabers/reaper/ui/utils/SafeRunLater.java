// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.utils;

import javax.swing.SwingUtilities;


/**
 * Helper class to catch exception happening inside a SwingUtilities.invokeLater call.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SafeRunLater
{
    /**
     * Constructor. Private due to helper class.
     */
    private SafeRunLater ()
    {
        // Intentionally empty
    }


    /**
     * Execute the runnable later.
     *
     * @param logModel For logging errors
     * @param runnable The runnable to execute
     */
    public static void execute (final LogModel logModel, final Runnable runnable)
    {
        SwingUtilities.invokeLater ( () -> {
            try
            {
                runnable.run ();
            }
            catch (final RuntimeException ex)
            {
                if (logModel != null)
                    logModel.error ("Error in executing SafeRunLater.", ex);
            }
        });
    }
}
