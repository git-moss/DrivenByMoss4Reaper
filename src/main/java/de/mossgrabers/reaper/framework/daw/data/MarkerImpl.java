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
        // TODO Reaper Do markers have a color in Reaper? If not return gray
        // final ColorValue color = this.marker.getColor ();
        return new double []
        {
            0,
            0,
            0
        };
    }


    /** {@inheritDoc} */
    @Override
    public void launch (final boolean quantized)
    {
        // TODO Reaper - select and launch from the marker
    }


    /** {@inheritDoc} */
    @Override
    public void removeMarker ()
    {
        // TODO Reaper - remove the marker
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // TODO Reaper - Select the marker
    }
}
