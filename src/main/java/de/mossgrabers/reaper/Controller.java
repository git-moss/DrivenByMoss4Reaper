// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper;

/**
 * Interface to be used from C++ code to control the application.
 *
 * @author Jürgen Moßgraber
 */
public class Controller
{
    private static MainApp app;


    /**
     * Private due to the fact that it only provides static functions.
     */
    private Controller ()
    {
        // Intentionally empty
    }


    /**
     * Startup the application window.
     *
     * @param iniPath Folder where the Reaper INI files are stored
     */
    public static void startup (final String iniPath)
    {
        app = new MainApp (iniPath);
    }


    /**
     * Add an available device.
     *
     * @param name The name of the device
     * @param identifier The ID of the device (can be the DLL path)
     */
    public static void addDevice (final String name, final String identifier)
    {
        if (app != null)
            app.addDevice (name, identifier);
    }


    /**
     * Startup the infrastructure and controller instances.
     */
    public static void startInfrastructure ()
    {
        if (app != null)
            app.startupInfrastructure ();
    }


    /**
     * Update the data model.
     *
     * @param data The data to update the model with
     */
    public static void updateModel (final String data)
    {
        if (app != null)
            app.updateModel (data);
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
            app.showStage ();
    }


    /**
     * Displays the project settings window.
     */
    public static void displayProjectWindow ()
    {
        if (app != null)
            app.showProjectWindow ();
    }


    /**
     * Displays the parameter mapping window.
     */
    public static void displayParameterWindow ()
    {
        if (app != null)
            app.showParameterWindow ();
    }


    /**
     * Restart all configured controllers.
     */
    public static void restartControllers ()
    {
        if (app != null)
            app.restartControllers ();
    }


    /**
     * Set the default initial settings for the document/project.
     */
    public static void setDefaultDocumentSettings ()
    {
        if (app != null)
            app.setDefaultDocumentSettings ();
    }


    /**
     * Get the formatted document settings formatted to be stored in the Reaper extension data
     *
     * @return The formatted document settings
     */
    public static String getFormattedDocumentSettings ()
    {
        return app == null ? "" : app.getFormattedDocumentSettings ();
    }


    /**
     * Set the formatted document settings formatted from the the Reaper extension data
     *
     * @param data The formatted document settings
     */
    public static void setFormattedDocumentSettings (final String data)
    {
        if (app != null)
            app.setFormattedDocumentSettings (data);
    }


    /**
     * Handle a MIDI message.
     *
     * @param deviceID The device (MIDI input port) which received the message
     * @param message The MIDI message
     */
    public static void onMIDIEvent (final int deviceID, final byte [] message)
    {
        if (app != null)
            app.onMIDIMessage (deviceID, message);
    }
}