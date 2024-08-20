// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.framework.daw.BrowserContentType;

import java.util.List;


/**
 * Interface for callback from the user interface.
 *
 * @author Jürgen Moßgraber
 */
public interface AppCallback
{
    /**
     * Returns true if the full infrastructure has been started.
     *
     * @return True if everything is up and running
     */
    boolean isFullyInitialised ();


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
     * @return The created controller instance or null if an error occurred
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
     * Disable/enable a controller.
     *
     * @param controllerIndex The index of the controller
     */
    void toggleEnableController (int controllerIndex);


    /**
     * Edit the project settings.
     */
    void projectSettings ();


    /**
     * Edit the parameter mappings for the currently selected device, if any.
     *
     * @param controllerIndex The index of the controller from which to get the currently selected
     *            device
     */
    void parameterSettings (int controllerIndex);


    /**
     * Request a rescan of all MIDI ports and update the configuration settings.
     */
    void sendMIDIPortRefreshCommand ();


    /**
     * Request a full update of the data model.
     */
    void sendRefreshCommand ();


    /**
     * Clear the logging messages.
     */
    void clearLogMessage ();


    /**
     * Disable/enable an update processor for performance improvements.
     *
     * @param processor The processor to The processor to disable/enable
     * @param enable True to enable processor updates, false to disable
     */
    void enableUpdates (Processor processor, boolean enable);


    /**
     * Get the visibility configuration for the given browser content type.
     *
     * @param browserContentType The content type
     * @return The visibility configuration
     */
    boolean [] getBrowserColumnsVisibility (BrowserContentType browserContentType);


    /**
     * Set the visibility configuration for the given browser content type.
     *
     * @param browserContentType The content type
     * @param visibilities The visibility configuration
     */
    void setBrowserColumnsVisibility (BrowserContentType browserContentType, boolean [] visibilities);


    /**
     * Get the disabled / enabled state the popup window for notifications.
     *
     * @return True if enabled
     */
    boolean getPopupWindowNotification ();


    /**
     * Disable / enable the popup window for notifications.
     *
     * @param enabled True to enable
     */
    void setPopupWindowNotification (final boolean enabled);
}
