// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.IHwControl;
import de.mossgrabers.framework.graphics.IGraphicsContext;


/**
 * Additional methods necessary for implementing the hardware controls for Reaper.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IReaperHwControl extends IHwControl
{
    /**
     * Draw the control into the given context.
     *
     * @param gc The graphics context
     * @param scale The scale factor
     */
    void draw (IGraphicsContext gc, double scale);


    /**
     * Handle a mouse event from the simulator UI.
     *
     * @param mouseEvent The event
     * @param x The X position of the event
     * @param y The Y position of the event
     */
    void mouse (int mouseEvent, double x, double y);
}
