// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
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

    protected int           value;
    protected int           lastReceivedValue = -1;


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
        // Not supported
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
        final String displayedValue = getDisplayedValue ();
        final int length = displayedValue.length ();
        return length > limit ? displayedValue.substring (0, length) : displayedValue;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return Math.max (this.value, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int value)
    {
        this.setValue (this.valueChanger.changeValue (value, this.getValue ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final double value)
    {
        if (!this.doesExist ())
            return;
        this.value = (int) value;
        this.sender.sendOSC ("/device/param/" + (this.getIndex () + 1) + "/value", Double.valueOf (this.valueChanger.toNormalizedValue (this.getValue ())));
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
        this.setValue (0.0);
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        // Prevent updating of the value from the DAW when the user edits the value, otherwise the
        // value "jumps" due to roundtrip delays

        this.isBeingTouched = isBeingTouched;

        if (this.isBeingTouched || this.lastReceivedValue == -1)
            return;

        this.value = this.lastReceivedValue;
        this.lastReceivedValue = -1;
    }


    /**
     * Set the value.
     *
     * @param value The value
     */
    public void setInternalValue (final int value)
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
