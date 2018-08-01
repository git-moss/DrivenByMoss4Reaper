// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Encapsulates the data of a send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendImpl extends ParameterImpl implements ISend
{
    private int trackIndex;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param trackIndex The index of the track to which this send belongs
     * @param index The index of the send
     */
    public SendImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int trackIndex, final int index)
    {
        super (host, sender, valueChanger, index);

        this.trackIndex = trackIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.setValue (this.getValue () + increment);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final double value)
    {
        if (!this.doesExist ())
            return;
        this.value = (int) value;
        final Double doubleValue = Double.valueOf (this.valueChanger.toNormalizedValue (this.getValue ()));
        final StringBuilder command = new StringBuilder ("/track/").append (this.trackIndex + 1).append ("/send/").append (this.getIndex () + 1).append ("/volume");
        this.sender.sendOSC (command.toString (), doubleValue);
    }
}
