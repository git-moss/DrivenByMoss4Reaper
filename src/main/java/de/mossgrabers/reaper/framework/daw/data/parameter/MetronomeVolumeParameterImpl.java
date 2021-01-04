// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the metronome volume.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MetronomeVolumeParameterImpl extends EmptyParameter
{
    private final DataSetupEx dataSetup;

    private double            metronomeVolume    = 0.5;
    private String            metronomeVolumeStr = "0.0";


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    public MetronomeVolumeParameterImpl (final DataSetupEx dataSetup)
    {
        this.dataSetup = dataSetup;
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.dataSetup.getSender ().processNoArg (Processor.METRO_VOL, increment > 0 ? "+" : "-");
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        this.dataSetup.getSender ().processIntArg (Processor.METRO_VOL, this.dataSetup.getValueChanger ().toMidiValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        this.setValue (this.dataSetup.getValueChanger ().fromNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.dataSetup.getValueChanger ().fromNormalizedValue (Math.max (this.metronomeVolume, 0));
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


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.metronomeVolumeStr;
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
