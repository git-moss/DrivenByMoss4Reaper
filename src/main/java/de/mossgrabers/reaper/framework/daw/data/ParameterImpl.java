// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterImpl extends ItemImpl implements IParameter
{
    private String   valueStr          = "";
    private boolean  isBeingTouched;

    protected double value;
    protected double lastReceivedValue = -1;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the parameter
     */
    public ParameterImpl (final DataSetupEx dataSetup, final int index)
    {
        super (dataSetup, index);
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


    /**
     * Get the normalized internal value.
     *
     * @return The value in the range of [0..1]
     */
    public double getInternalValue ()
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
    public void setValue (final int value)
    {
        if (!this.doesExist ())
            return;
        this.value = this.valueChanger.toNormalizedValue (value);
        this.sendValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        // Always immediatly with Reaper
        this.setValue (value);
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


    /**
     * Send the changed value to Reaper.
     */
    protected void sendValue ()
    {
        this.sendPositionedItemOSC ("value", this.value);
    }


    /** {@inheritDoc} */
    @Override
    protected String createCommand (final String command)
    {
        return "param/" + super.createCommand (command);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.DEVICE;
    }
}
