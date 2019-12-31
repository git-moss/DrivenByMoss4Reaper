// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;


/**
 * Implementation of a proxy to a text display on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwTextDisplayImpl extends AbstractHwControl implements IHwTextDisplay, IReaperHwControl
{
    private final String    id;
    private final String [] lines;
    private Bounds          bounds;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param numLines The number of lines that the display can show
     */
    public HwTextDisplayImpl (final String id, final int numLines)
    {
        super (null, null);

        this.id = id;
        this.lines = new String [numLines];
    }


    /** {@inheritDoc}} */
    @Override
    public void setLine (final int line, final String text)
    {
        this.lines[line] = text;
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.bounds = new Bounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public String getId ()
    {
        return this.id;
    }


    /** {@inheritDoc} */
    @Override
    public Bounds getBounds ()
    {
        return this.bounds;
    }
}
