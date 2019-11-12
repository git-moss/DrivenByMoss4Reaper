// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * A control on a controller surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IControl
{
    /**
     * Update the state of the control (e.g. light, fader position).
     */
    void update ();
}
