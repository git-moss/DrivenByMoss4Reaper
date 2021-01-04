// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

/**
 * A bounding box with x/y position and a width and height.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Bounds
{
    private final double x;
    private final double y;
    private final double width;
    private final double height;


    /**
     * Contructor.
     *
     * @param x The x position
     * @param y The y position
     * @param width The width position
     * @param height The height position
     */
    public Bounds (final double x, final double y, final double width, final double height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    /**
     * Get the x position.
     *
     * @return The x position
     */
    public double getX ()
    {
        return this.x;
    }


    /**
     * Get the y position.
     *
     * @return The y position
     */
    public double getY ()
    {
        return this.y;
    }


    /**
     * Get the width.
     *
     * @return The width
     */
    public double getWidth ()
    {
        return this.width;
    }


    /**
     * Get the height.
     *
     * @return The height
     */
    public double getHeight ()
    {
        return this.height;
    }


    /**
     * Test if the given point is inside of these bounds.
     *
     * @param x The x position of the point
     * @param y The y position of the point
     * @return True if the point is inside of the bounds
     */
    public boolean contains (final double x, final double y)
    {
        return this.containsX (x) && this.containsY (y);
    }


    /**
     * Test if the given point is inside of the x bounds.
     *
     * @param x The x position of the point
     * @return True if the point is inside of the bounds
     */
    public boolean containsX (final double x)
    {
        return this.x <= x && x <= this.x + this.width;
    }


    /**
     * Test if the given point is inside of the y bounds.
     *
     * @param y The y position of the point
     * @return True if the point is inside of the bounds
     */
    public boolean containsY (final double y)
    {
        return this.y <= y && y <= this.y + this.height;
    }
}
