// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;


/**
 * A static parameter which is not backed by a real parameter implementation in the DAW and
 * represents a value from 0% to 100%.
 *
 * @author Jürgen Moßgraber
 */
public class StaticPercentageParameter extends AbstractStaticParameter
{
    private final double defaultValue;
    private double       value;


    /**
     * Constructor.
     * 
     * @param name The name of the parameter
     * @param valueChanger The value changer
     * @param defaultValue The defaultValue in the range of [0..1]
     */
    public StaticPercentageParameter (final String name, final IValueChanger valueChanger, final double defaultValue)
    {
        super (name, valueChanger);

        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }


    /**
     * Get the raw value in the range of [0,1].
     * 
     * @return The value
     */
    public double getRawValue ()
    {
        return this.value;
    }


    /** {@inheritDoc} **/
    @Override
    public void inc (final double incrementValue)
    {
        this.value = Math.clamp (this.value + this.valueChanger.toNormalizedValue (incrementValue), 0, 1.0);
    }


    /** {@inheritDoc} **/
    @Override
    public String getDisplayedValue ()
    {
        return String.format ("%d%%", Integer.valueOf ((int) Math.round (this.value * 100.0)));
    }


    /** {@inheritDoc} **/
    @Override
    public int getValue ()
    {
        return (int) Math.round (this.value * this.valueChanger.getUpperBound ());
    }


    /** {@inheritDoc} **/
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.value = Math.clamp (this.valueChanger.toNormalizedValue (value), 0, 1.0);
    }


    /** {@inheritDoc} **/
    @Override
    public void setNormalizedValue (final double value)
    {
        this.value = Math.clamp (value, 0, 1.0);
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
        final int v = valueChanger.changeValue (control, this.valueChanger.fromNormalizedValue (this.value));
        this.value = Math.clamp (this.valueChanger.toNormalizedValue (v), 0, 1.0);
    }


    /** {@inheritDoc} **/
    @Override
    public void resetValue ()
    {
        this.value = this.defaultValue;
    }
}
