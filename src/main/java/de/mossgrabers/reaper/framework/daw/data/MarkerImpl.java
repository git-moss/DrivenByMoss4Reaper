// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a marker.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerImpl extends ItemImpl implements IMarker
{
    private ColorEx color = new ColorEx (0.2, 0.2, 0.2);


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the marker
     */
    public MarkerImpl (final DataSetupEx dataSetup, final int index)
    {
        super (dataSetup, index);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        return this.color;
    }


    /**
     * Set the color.
     *
     * @param color The color
     */
    public void setColorState (final double [] color)
    {
        this.color = new ColorEx (color);
    }


    /** {@inheritDoc} */
    @Override
    public void launch (final boolean quantized)
    {
        this.sendMarkerOSC ("launch");
    }


    /** {@inheritDoc} */
    @Override
    public void removeMarker ()
    {
        this.sendMarkerOSC ("remove");
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.sendMarkerOSC ("select");
    }


    protected void sendMarkerOSC (final String command)
    {
        this.sender.processNoArg ("marker", this.getPosition () + "/" + command);
    }
}
