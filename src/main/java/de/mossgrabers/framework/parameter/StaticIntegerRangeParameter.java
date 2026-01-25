// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;


/**
 * A static parameter which is not backed by a real parameter implementation in the DAW and
 * represents an integer value from [minValue,maxValue] stepped by 1.
 *
 * @author Jürgen Moßgraber
 */
public class StaticIntegerRangeParameter extends AbstractStaticParameter
{
    private final int minValue;
    private final int maxValue;
    private final int defaultValue;
    private int       value;


    /**
     * Constructor.
     * 
     * @param name The name of the parameter
     * @param valueChanger The value changer
     * @param minValue The minimum value
     * @param maxValue The maximum value
     * @param defaultValue The defaultValue in the range of [minValue,maxValue]
     */
    public StaticIntegerRangeParameter (final String name, final IValueChanger valueChanger, final int minValue, final int maxValue, final int defaultValue)
    {
        super (name, valueChanger);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }


    /**
     * Get the raw value inside of the range.
     * 
     * @return The value
     */
    public int getRawValue ()
    {
        return this.value;
    }


    /** {@inheritDoc} **/
    @Override
    public void inc (final double incrementValue)
    {
        this.value = Math.clamp (this.value + (incrementValue > 0 ? 1 : -1), this.minValue, this.maxValue);
    }


    /** {@inheritDoc} **/
    @Override
    public String getDisplayedValue ()
    {
        return Integer.toString (this.value);
    }


    /** {@inheritDoc} **/
    @Override
    public int getValue ()
    {
        final double range = this.maxValue - this.minValue;
        final double v = this.value - this.minValue;
        return (int) Math.round (v / range * this.valueChanger.getUpperBound ());
    }


    /** {@inheritDoc} **/
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.value = Math.clamp (value, this.minValue, this.maxValue);
    }


    /** {@inheritDoc} **/
    @Override
    public void setNormalizedValue (final double value)
    {
        this.value = Math.clamp (this.valueChanger.fromNormalizedValue (value), this.minValue, this.maxValue);
    }


    /** {@inheritDoc} **/
    @Override
    public void setValueImmediatly (final int value)
    {
        this.setValue (this.valueChanger, value);
    }


    /** {@inheritDoc} **/
    @Override
    public void changeValue (final IValueChanger valueChanger, final int control)
    {
        this.value = Math.clamp (this.value + (valueChanger.isIncrease (control) ? 1 : -1), this.minValue, this.maxValue);
    }


    /** {@inheritDoc} **/
    @Override
    public void resetValue ()
    {
        this.value = this.defaultValue;
    }
}
