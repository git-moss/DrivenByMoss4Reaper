// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;


/**
 * Encapsulates the data of a send.
 *
 * @author Jürgen Moßgraber
 */
public class SendImpl extends ParameterImpl implements ISend
{
    private static final Object UPDATE_LOCK = new Object ();

    private final IChannel      channel;
    private ColorEx             color;
    private boolean             isEnabled   = false;


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
    public boolean isEnabled ()
    {
        return this.isEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabled ()
    {
        final StringBuilder command = new StringBuilder ().append (this.channel.getPosition ()).append ("/send/").append (this.getPosition ()).append ("/active");
        this.sender.processDoubleArg (Processor.TRACK, command.toString (), this.value);
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


    /**
     * Set the enabled state.
     *
     * @param isEnabled True to enable
     */
    public void setInternalEnabled (final boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }
}
