// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * Encapsulates the data of a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterImpl extends ItemImpl implements IParameter
{
    protected IValueChanger valueChanger;

    private String          valueStr          = "";
    private boolean         isBeingTouched;

    protected double        value;
    protected double        lastReceivedValue = -1;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param index The index of the parameters
     */
    public ParameterImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int index)
    {
        super (host, sender, index);
        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.setValue ((int) Math.max (0, Math.min (this.getValue () + increment, this.valueChanger.getUpperBound () - 1.0)));
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.valueStr;
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        final String displayedValue = this.getDisplayedValue ();
        final int length = displayedValue.length ();
        return length > limit ? displayedValue.substring (0, length) : displayedValue;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.valueChanger.fromNormalizedValue (Math.max (this.value, 0));
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int value)
    {
        this.setValue (this.valueChanger.changeValue (value, this.getValue ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        if (!this.doesExist ())
            return;
        this.value = this.valueChanger.toNormalizedValue (value);
        this.sender.processDoubleArg ("device", "param/" + this.getPosition () + "/value", this.value);
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


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setValue (0);
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        // Prevent updating of the value from the DAW when the user edits the value, otherwise the
        // value "jumps" due to roundtrip delays

        this.isBeingTouched = isBeingTouched;

        if (this.isBeingTouched || this.lastReceivedValue < 0)
            return;

        this.value = this.lastReceivedValue;
        this.lastReceivedValue = -1;
    }


    /**
     * Set the value.
     *
     * @param value The value normalized to 0..1
     */
    public void setInternalValue (final double value)
    {
        if (this.isBeingTouched)
            this.lastReceivedValue = value;
        else
            this.value = value;
    }


    /**
     * Set the value as text.
     *
     * @param valueStr The text
     */
    public void setValueStr (final String valueStr)
    {
        this.valueStr = valueStr == null ? "" : valueStr;
    }
}
