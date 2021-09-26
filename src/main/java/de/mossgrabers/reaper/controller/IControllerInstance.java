// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.framework.configuration.DocumentSettingsUI;
import de.mossgrabers.reaper.framework.configuration.GlobalSettingsUI;


/**
 * Interface to an controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IControllerInstance
{
    /**
     * Get the controller description.
     *
     * @return The definition
     */
    IControllerDefinition getDefinition ();


    /**
     * Start the controller.
     */
    void start ();


    /**
     * Restart the controller.
     */
    void restart ();


    /**
     * Stop the controller.
     */
    void stop ();


    /**
     * Returns true if the controller instance has been successfully started.
     *
     * @return True if the controller instance has been successfully started.
     */
    boolean isRunning ();


    /**
     * Get if the controller is enabled.
     *
     * @return True if enabled
     */
    boolean isEnabled ();


    /**
     * Set if the controller is enabled.
     *
     * @param isEnabled True if enabled
     */
    void setEnabled (boolean isEnabled);


    /**
     * Flush out the settings to the controller device.
     */
    void flush ();


    /**
     * Parse an incoming DAW message into the model.
     *
     * @param address The message address
     * @param argument The argument
     */
    void parse (String address, String argument);


    /**
     * Edit the settings of the controller instance.
     */
    void edit ();


    /**
     * Store the controllers configuration settings.
     */
    void storeConfiguration ();


    /**
     * Get the global settings.
     *
     * @return The settings
     */
    GlobalSettingsUI getGlobalSettingsUI ();


    /**
     * Get the project settings.
     *
     * @return The settings
     */
    DocumentSettingsUI getDocumentSettingsUI ();


    /**
     * Simulate the user interface of the device in a window.
     */
    void simulateUI ();


    /**
     * Test the user interface.
     */
    void testUI ();
}