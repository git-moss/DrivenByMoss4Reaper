// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.AbstractParameterImpl;


/**
 * A parameter encapsulating the tracks crossfade setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CrossfadeParameter extends AbstractParameterImpl
{
    private final IValueChanger valueChanger;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param index The index of the crossfade parameter
     */
    public CrossfadeParameter (final IValueChanger valueChanger, final int index)
    {
        super (index);

        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Crossfade";
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
        return "AB";
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.valueChanger.getUpperBound () / 2;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        // Not supported
    }
}
