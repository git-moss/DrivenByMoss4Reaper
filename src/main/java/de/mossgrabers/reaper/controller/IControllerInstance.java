// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller;

/**
 * Interface to an controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IControllerInstance
{
    /**
     * Start the controller.
     */
    void start ();


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
}