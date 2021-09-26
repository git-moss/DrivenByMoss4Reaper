// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.AbstractParameterImpl;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;

import java.text.DecimalFormat;


/**
 * Encapsulates the tempo.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TempoParameterImpl extends AbstractParameterImpl
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
        this.setInternalTempo (value);
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
        this.setInternalTempo (DEFAULT_TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return new DecimalFormat ("#.00").format (this.tempo);
    }


    /**
     * Set the internal tempo value.
     *
     * @param tempo The value
     */
    public void setInternalTempo (final double tempo)
    {
        this.tempo = tempo;
    }


    /**
     * Get the internal tempo value.
     *
     * @return The value
     */
    public double getInternalValue ()
    {
        return this.tempo;
    }
}
