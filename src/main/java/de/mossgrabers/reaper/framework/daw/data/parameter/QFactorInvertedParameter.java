// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.observer.IValueObserver;


/**
 * Inverts the value when set absolute. Used to work around inverted Q factor in Reaper.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class QFactorInvertedParameter implements IParameter
{
    private final IParameter parameter;
    private final int        maxValue;


    /**
     * Constructor.
     *
     * @param parameter The parameter to wrap
     * @param maxValue The maximum possible value for inversion
     */
    public QFactorInvertedParameter (final IParameter parameter, final int maxValue)
    {
        this.parameter = parameter;
        this.maxValue = maxValue;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.parameter.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.parameter.getIndex ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.parameter.getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.parameter.isSelected ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        this.parameter.setSelected (isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.parameter.select ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.parameter.getName ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.parameter.getName (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.parameter.addNameObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        this.parameter.setName (name);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.parameter.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.parameter.inc (increment);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.parameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        return this.parameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.maxValue - this.parameter.getValue () - 1;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        this.parameter.setValue (this.maxValue - value - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        this.parameter.setNormalizedValue (1 - value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int value)
    {
        this.parameter.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.parameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        this.parameter.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        return this.parameter.getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        this.parameter.setIndication (enable);
    }
}
