package de.mossgrabers.transformator;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * The main application.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Transformator
{
    private static TransformatorApplication app;


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
            SwingUtilities.invokeLater ( () -> {
                try
                {
                    try
                    {
                        UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
                    }
                    catch (final UnsupportedLookAndFeelException ex)
                    {
                        // Ignore
                    }

                    app = new TransformatorApplication (args[0]);
                }
                catch (final Throwable ex)
                {
                    ex.printStackTrace ();
                }
            });
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
        if (app != null)
            app.exit ();
    }
}
