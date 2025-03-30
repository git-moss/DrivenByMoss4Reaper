// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter.map;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * A renamed parameter. Delegates everything to the wrapped parameter, except a changed name.
 *
 * @author Jürgen Moßgraber
 */
public class RenamedParameter implements IParameter
{
    private final IParameter parameter;
    private final String     newName;


    /**
     * Constructor.
     *
     * @param parameter The wrapped parameter
     * @param newName The text to replace the parameters' name
     */
    public RenamedParameter (final IParameter parameter, final String newName)
    {
        this.parameter = parameter;
        this.newName = newName;
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
    public void toggleMultiSelect ()
    {
        this.parameter.toggleMultiSelect ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.newName == null || this.newName.isBlank () ? this.parameter.getName () : this.newName;
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        // Name does never change
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        // Name does never change
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
        return this.parameter.getDisplayedValue (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.parameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        this.parameter.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.parameter.setValue (valueChanger, value);
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        this.parameter.setNormalizedValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.parameter.setValueImmediatly (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int value)
    {
        this.parameter.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.parameter.changeValue (valueChanger, value);
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
