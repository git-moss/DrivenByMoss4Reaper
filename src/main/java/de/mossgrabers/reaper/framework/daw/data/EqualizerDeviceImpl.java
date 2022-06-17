// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.EqualizerBandType;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.parameter.EqBandTypeParameterImpl;


/**
 * Encapsulates the data of a Reaper equalizer ReaEQ device. Parameters are frequency, gain,
 * q-factor for each active band (3 * N), followed by bypass and wet parameters (2). The type is
 * encoded in the parameter names
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EqualizerDeviceImpl extends SpecificDeviceImpl implements IEqualizerDevice
{
    private static final int                 NUMBER_OF_BANDS      = 8;

    private final EqBandTypeParameterImpl [] eqBandTypeParameters = new EqBandTypeParameterImpl [8];


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param numParams The number of parameters
     */
    public EqualizerDeviceImpl (final DataSetupEx dataSetup, final int numParams)
    {
        super (dataSetup, Processor.EQ, 0, numParams, 0, 0, 0);

        for (int i = 0; i < this.eqBandTypeParameters.length; i++)
            this.eqBandTypeParameters[i] = new EqBandTypeParameterImpl (dataSetup, i);
    }


    /** {@inheritDoc} */
    @Override
    public int getBandCount ()
    {
        return NUMBER_OF_BANDS;
    }


    /** {@inheritDoc} */
    @Override
    public EqualizerBandType getTypeID (final int index)
    {
        return this.doesExist () ? this.eqBandTypeParameters[index].getTypeInternal () : EqualizerBandType.OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void setType (final int index, final EqualizerBandType type)
    {
        this.eqBandTypeParameters[index].setType (type);
    }


    /**
     * Set the band type.
     *
     * @param index The index [0-7]
     * @param typeIndex The indexed type [-1,9]
     */
    public void setTypeInternal (final int index, final int typeIndex)
    {
        this.eqBandTypeParameters[index].setTypeInternal (Integer.valueOf (typeIndex));
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getType (final int index)
    {
        return this.eqBandTypeParameters[index];
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getFrequency (final int index)
    {
        return this.getParameterBank ().getItem (3 * index);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getGain (final int index)
    {
        return this.getParameterBank ().getItem (3 * index + 1);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getQ (final int index)
    {
        return this.getParameterBank ().getItem (3 * index + 2);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.EQ;
    }
}
