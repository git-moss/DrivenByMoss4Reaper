// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator.util;

import javafx.application.Platform;


/**
 * Helper class to catch exception happening inside a Platforem.runLater call.
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
     * @param runnable The runnable to execute
     */
    public static void execute (final Runnable runnable)
    {
        Platform.runLater ( () -> {
            try
            {
                runnable.run ();
            }
            catch (final RuntimeException ex)
            {
                ex.printStackTrace ();
            }
        });
    }
}
