// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.parameter.AbstractParameterImpl;


/**
 * A parameter encapsulating the tracks crossfade setting.
 *
 * @author Jürgen Moßgraber
 */
public class CrossfadeParameter extends AbstractParameterImpl
{
    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param index The index of the crossfade parameter
     */
    public CrossfadeParameter (final IValueChanger valueChanger, final int index)
    {
        super (valueChanger, index);
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
    public String getDisplayedValue ()
    {
        return "AB";
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.valueChanger.getUpperBound () / 2;
    }
}
