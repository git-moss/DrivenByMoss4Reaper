// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.AbstractParameterImpl;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the metronome volume.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MetronomeVolumeParameterImpl extends AbstractParameterImpl
{
    private final DataSetupEx   dataSetup;

    // -6dB
    private static final double DEFAULT_VOLUME     = 0.592449;

    private double              metronomeVolume    = DEFAULT_VOLUME;
    private String              metronomeVolumeStr = "0.0";


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    public MetronomeVolumeParameterImpl (final DataSetupEx dataSetup)
    {
        super (dataSetup.getValueChanger (), -1);

        this.dataSetup = dataSetup;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.metronomeVolume = valueChanger.toNormalizedValue (value);
        this.dataSetup.getSender ().processIntArg (Processor.METRO_VOL, valueChanger.toMidiValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.valueChanger.fromNormalizedValue (Math.max (this.metronomeVolume, 0));
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.setValue (valueChanger.changeValue (value, this.getValue ()));
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setNormalizedValue (DEFAULT_VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.dataSetup.getSender ().processNoArg (Processor.METRO_VOL, increment > 0 ? "+" : "-");
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.metronomeVolumeStr;
    }


    /**
     * Set the value.
     *
     * @param metronomeVolume The value normalized to 0..1
     */
    public void setInternalMetronomeVolume (final double metronomeVolume)
    {
        this.metronomeVolume = metronomeVolume;
    }


    /**
     * Set the metronome volume text.
     *
     * @param metronomeVolumeStr The metronome volume text
     */
    public void setMetronomeVolumeStr (final String metronomeVolumeStr)
    {
        this.metronomeVolumeStr = metronomeVolumeStr;
    }
}
