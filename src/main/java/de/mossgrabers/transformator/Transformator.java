package de.mossgrabers.transformator;

import javafx.application.Application;


/**
 * The main application.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Transformator
{
    /**
     * Main function.
     *
     * @param args The first entry must contain the folder where the Reaper INI files are stored
     */
    public static void main (final String [] args)
    {
        // Start in separate thread to allow the method to return to C++
        try
        {
            final Thread t = new Thread ( () -> {
                try
                {
                    Application.launch (TransformatorApplication.class, args);
                }
                catch (final Throwable ex)
                {
                    ex.printStackTrace ();
                }
            });

            t.start ();
        }
        catch (final Throwable ex)
        {
            ex.printStackTrace ();
        }
    }


    /**
     * Shutdown the application.
     */
    public static void shutdown ()
    {
        final TransformatorApplication app = TransformatorApplication.get ();
        if (app != null)
            app.exit ();
    }
}
