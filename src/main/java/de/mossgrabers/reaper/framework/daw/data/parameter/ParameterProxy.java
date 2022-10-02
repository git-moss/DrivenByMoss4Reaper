package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.framework.daw.data.bank.ParameterBankImpl;


/**
 * Allows to store specific parameters from a parameter bank (e.g. for parameter binding) but can
 * internally be replaced.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterProxy implements IParameter
{
    private final ParameterBankImpl bank;
    private final int               index;


    /**
     * Constructor.
     * 
     * @param bank The parameter bank
     * @param index The index of the parameter in the bank to proxy
     */
    public ParameterProxy (final ParameterBankImpl bank, final int index)
    {
        this.bank = bank;
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.getParam ().doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.getParam ().getIndex ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.getParam ().getPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.getParam ().isSelected ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (boolean isSelected)
    {
        this.getParam ().setSelected (isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.getParam ().select ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.getParam ().getName ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (int limit)
    {
        return this.getParam ().getName (limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (IValueObserver<String> observer)
    {
        this.getParam ().addNameObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public void setName (String name)
    {
        this.getParam ().setName (name);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (boolean enable)
    {
        this.getParam ().enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (double increment)
    {
        this.getParam ().inc (increment);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.getParam ().getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (int limit)
    {
        return this.getParam ().getDisplayedValue (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.getParam ().getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (int value)
    {
        this.getParam ().setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (IValueChanger valueChanger, int value)
    {
        this.getParam ().setValue (valueChanger, value);
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (double value)
    {
        this.getParam ().setNormalizedValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (int value)
    {
        this.getParam ().setValueImmediatly (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (int value)
    {
        this.getParam ().changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (IValueChanger valueChanger, int value)
    {
        this.getParam ().changeValue (valueChanger, value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.getParam ().resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (boolean isBeingTouched)
    {
        this.getParam ().touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        return this.getParam ().getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (boolean enable)
    {
        this.getParam ().setIndication (enable);
    }


    private IParameter getParam ()
    {
        return this.bank.getParameter (this.index);
    }
}
