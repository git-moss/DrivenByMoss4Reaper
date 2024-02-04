// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameter.AbstractParameterImpl;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Encapsulates the data of a solo parameter.
 *
 * @author Jürgen Moßgraber
 */
public class SoloParameterImpl extends AbstractParameterImpl
{
    private final ChannelImpl channel;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param channel The channel to solo
     * @param index The index of the item in the page
     */
    public SoloParameterImpl (final IValueChanger valueChanger, final ChannelImpl channel, final int index)
    {
        super (valueChanger, index);

        this.channel = channel;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.channel.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Solo";
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.limit (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.valueChanger.fromNormalizedValue (this.channel.isSolo () ? 1 : 0);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.channel.setSolo (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        this.channel.setSolo (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.channel.setSolo (valueChanger.isIncrease (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.channel.setSolo (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.channel.setSolo (increment > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.channel.setSolo (false);
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        // Not supported
        return this.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }
}
