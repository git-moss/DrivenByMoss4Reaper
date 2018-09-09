// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper;

import de.mossgrabers.reaper.ui.MainFrame;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * Interface to be used from C++ code to control the application.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Controller
{
    private static MainFrame app;


    /**
     * Private due to the fact that it only provides static functions.
     */
    private Controller ()
    {
        // Intentionally empty
    }


    /**
     * Startup the application window and infrastructure.
     *
     * @param iniPath Folder where the Reaper INI files are stored
     */
    public static void startup (final String iniPath)
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

                    app = new MainFrame (iniPath);
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
     * Update the data model.
     * 
     * @param data The data to update the model with
     */
    public static void updateModel (final String data)
    {
        if (app != null)
            SafeRunLater.execute ( () -> app.updateModel (data));
    }


    /**
     * Shutdown the application.
     */
    public static void shutdown ()
    {
        if (app != null)
            app.exit ();
    }


    /**
     * Displays the application window.
     */
    public static void displayWindow ()
    {
        if (app != null)
            SafeRunLater.execute (app::showStage);
    }
}
