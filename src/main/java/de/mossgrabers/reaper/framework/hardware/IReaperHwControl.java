// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.IHwControl;


/**
 * Additional methods necessary for implementing the hardware controls for Reaper.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IReaperHwControl extends IHwControl
{
    /**
     * Get the ID of the control.
     *
     * @return The ID
     */
    String getId ();


    /**
     * Get the bounds.
     *
     * @return The bounds
     */
    Bounds getBounds ();
}
