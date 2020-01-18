// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

/**
 * Layout information for drawing a hardware control in the simulator.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwControlLayout
{
    private final String id;
    private Bounds       bounds;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     */
    public HwControlLayout (final String id)
    {
        this.id = id;
    }


    /**
     * Get the ID of the control.
     *
     * @return The ID
     */
    public String getId ()
    {
        return this.id;
    }


    /**
     * Get the bounds.
     *
     * @return The bounds
     */
    public Bounds getBounds ()
    {
        return this.bounds;
    }


    /**
     * The physical bounds of this hardware element on the controller. The unit is scaled into the
     * GUI window.
     *
     * @param x The X position of the control
     * @param y The Y position of the control
     * @param width The width of the control
     * @param height The height of the control
     */
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.bounds = new Bounds (x, y, width, height);
    }
}
