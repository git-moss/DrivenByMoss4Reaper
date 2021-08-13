// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;


/**
 * Encapsulates the data of a send.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendImpl extends ParameterImpl implements ISend
{
    private static final Object UPDATE_LOCK = new Object ();

    private final IChannel      channel;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param channel The index of the track to which this send belongs
     * @param index The index of the send
     * @param defaultValue The default value for resetting parameters [0..1]
     */
    public SendImpl (final DataSetupEx dataSetup, final IChannel channel, final int index, final double defaultValue)
    {
        super (dataSetup, index, defaultValue);

        this.channel = channel;
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.setValue ((int) (this.getValue () + increment));
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        if (!this.doesExist ())
            return;
        synchronized (UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates (Processor.TRACK);
            this.value = this.valueChanger.toNormalizedValue (value);
            this.sendValue ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendValue ()
    {
        final StringBuilder command = new StringBuilder ().append (this.channel.getPosition ()).append ("/send/").append (this.getPosition ()).append ("/volume");
        this.sender.processDoubleArg (Processor.TRACK, command.toString (), this.value);
    }
}
