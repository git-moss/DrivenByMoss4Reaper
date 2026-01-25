// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;


/**
 * Base class for static parameters which are not backed by a real parameter implementation in the
 * DAW.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractStaticParameter extends AbstractParameterImpl
{
    private String name;


    /**
     * Constructor.
     * 
     * @param name The name of the parameter
     * @param valueChanger The value changer
     */
    protected AbstractStaticParameter (final String name, final IValueChanger valueChanger)
    {
        super (valueChanger, 0);

        this.name = name;
    }


    /** {@inheritDoc} **/
    @Override
    public boolean doesExist ()
    {
        return true;
    }


    /** {@inheritDoc} **/
    @Override
    public String getName ()
    {
        return this.name;
    }


    /** {@inheritDoc} **/
    @Override
    public void setName (final String name)
    {
        this.name = name;
    }


    /** {@inheritDoc} **/
    @Override
    public abstract void inc (final double increment);


    /** {@inheritDoc} **/
    @Override
    public abstract String getDisplayedValue ();


    /** {@inheritDoc} **/
    @Override
    public abstract int getValue ();


    /** {@inheritDoc} **/
    @Override
    public abstract void setValue (final IValueChanger valueChanger, final int value);


    /** {@inheritDoc} **/
    @Override
    public abstract void setNormalizedValue (final double value);


    /** {@inheritDoc} **/
    @Override
    public abstract void setValueImmediatly (final int value);


    /** {@inheritDoc} **/
    @Override
    public abstract void changeValue (final IValueChanger valueChanger, final int value);


    /** {@inheritDoc} **/
    @Override
    public abstract void resetValue ();
}
