// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.parameter.AbstractParameterImpl;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;

import java.text.DecimalFormat;


/**
 * Encapsulates the tempo.
 *
 * @author Jürgen Moßgraber
 */
public class TempoParameterImpl extends AbstractParameterImpl implements IParameterEx
{
    private static final double DEFAULT_TEMPO = 120.0;

    private final MessageSender sender;
    private double              tempo         = DEFAULT_TEMPO;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    public TempoParameterImpl (final DataSetupEx dataSetup)
    {
        super (dataSetup.getValueChanger (), -1);

        this.sender = dataSetup.getSender ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.setInternalValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int control)
    {
        final boolean increase = valueChanger.isIncrease (control);
        final boolean slow = Math.abs (valueChanger.calcKnobChange (control)) > 1;

        final String dir;
        if (increase)
            dir = slow ? "+" : "++";
        else
            dir = slow ? "-" : "--";
        this.sender.processNoArg (Processor.TEMPO, dir);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setInternalValue (DEFAULT_TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return new DecimalFormat ("#.00").format (this.tempo);
    }


    /** {@inheritDoc} */
    @Override
    public void setInternalValue (final double value)
    {
        this.tempo = value;
    }


    /** {@inheritDoc} */
    @Override
    public double getInternalValue ()
    {
        return this.tempo;
    }


    /** {@inheritDoc} */
    @Override
    public void setValueStr (final String valueStr)
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void setInternalName (final String value)
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void setPosition (final int paramNo)
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void setExists (final boolean exists)
    {
        // Not used
    }
}
