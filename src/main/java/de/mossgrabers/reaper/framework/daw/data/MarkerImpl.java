// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Encapsulates the data of a marker.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerImpl extends ItemImpl implements IMarker
{
    private int       position;
    private double [] color;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param index The index of the marker
     */
    public MarkerImpl (final IHost host, final MessageSender sender, final int index)
    {
        super (host, sender, index);
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return this.color == null ? new double []
        {
            0.2,
            0.2,
            0.2
        } : this.color;
    }


    /**
     * Set the color.
     *
     * @param color The color
     */
    public void setColorState (final double [] color)
    {
        this.color = color;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.position;
    }


    /**
     * Set the position of the track, among all tracks.
     *
     * @param position The position
     */
    public void setPosition (final int position)
    {
        this.position = position;
    }


    /** {@inheritDoc} */
    @Override
    public void launch (final boolean quantized)
    {
        this.sendMarkerOSC ("launch", null);
    }


    /** {@inheritDoc} */
    @Override
    public void removeMarker ()
    {
        this.sendMarkerOSC ("remove", null);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.sendMarkerOSC ("select", null);
    }


    protected void sendMarkerOSC (final String command, final Object value)
    {
        this.sender.sendOSC ("/marker/" + (this.getIndex () + 1) + "/" + command, value);
    }
}
