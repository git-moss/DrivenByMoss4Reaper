// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.controller.IControllerInstance;

import java.util.List;


/**
 * Interface for callback from the user interface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface AppCallback
{
    /**
     * Detect controllers.
     *
     * @return The detected instances
     */
    List<IControllerInstance> detectControllers ();


    /**
     * Add and create a controller from a controller definition.
     *
     * @param definition The controller definition
     * @return The created controller instance or null if an error occured
     */
    IControllerInstance addController (IControllerDefinition definition);


    /**
     * Edit a controller.
     *
     * @param controllerIndex The index of the controller
     */
    void editController (int controllerIndex);


    /**
     * Remove a controller.
     *
     * @param controllerIndex The index of the controller
     */
    void removeController (int controllerIndex);


    /**
     * Dis-/enable a controller.
     *
     * @param controllerIndex The index of the controller
     */
    void toggleEnableController (int controllerIndex);


    /**
     * Edit the project settings.
     */
    void projectSettings ();


    /**
     * Request a full update of the data model.
     */
    void sendRefreshCommand ();


    /**
     * Clear the logging messages.
     */
    void clearLogMessage ();


    /**
     * Dis-/enable an update processor for performance improvements.
     *
     * @param processor The processor to The processor to dis-/enable
     * @param enable True to enable processor updates, false to disable
     */
    void enableUpdates (final Processor processor, final boolean enable);
}
