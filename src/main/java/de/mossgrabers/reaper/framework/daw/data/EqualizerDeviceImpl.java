// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.parameter.QFactorInvertedParameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Encapsulates the data of a Reaper equalizer ReaEQ device. Parameters are frequency, gain,
 * q-factor for each active band (3 * N), followed by bypass and wet parameters (2). The type is
 * encoded in the parameter names
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EqualizerDeviceImpl extends SpecificDeviceImpl implements IEqualizerDevice
{
    private static final String              BANDOFF             = "off";

    private static final Map<String, String> EQ_TYPE_INDICES     = new HashMap<> ();
    private static final Map<String, String> EQ_TYPE_INDICES_INV = new HashMap<> ();

    static
    {
        EQ_TYPE_INDICES.put (BANDOFF, "-1");
        EQ_TYPE_INDICES.put ("lowcut", "4");
        EQ_TYPE_INDICES.put ("lowshelf", "0");
        EQ_TYPE_INDICES.put ("bell", "8");
        EQ_TYPE_INDICES.put ("highcut", "3");
        EQ_TYPE_INDICES.put ("highshelf", "1");
        EQ_TYPE_INDICES.put ("notch", "6");

        EQ_TYPE_INDICES_INV.put ("-1", BANDOFF);
        EQ_TYPE_INDICES_INV.put ("4", "lowcut");
        EQ_TYPE_INDICES_INV.put ("0", "lowshelf");
        EQ_TYPE_INDICES_INV.put ("8", "bell");
        // Band (alt)
        EQ_TYPE_INDICES_INV.put ("9", "bell");
        // Band (alt2)
        EQ_TYPE_INDICES_INV.put ("2", "bell");
        EQ_TYPE_INDICES_INV.put ("3", "highcut");
        EQ_TYPE_INDICES_INV.put ("1", "highshelf");
        EQ_TYPE_INDICES_INV.put ("6", "notch");
        // 5 == Allpass, which is not supported
        EQ_TYPE_INDICES_INV.put ("5", "bell");
        // 7 == Bandpass, which is not supported
        EQ_TYPE_INDICES_INV.put ("7", "bell");
    }

    private static final int NUMBER_OF_BANDS = 8;

    private final String []  bandTypes       = new String [8];


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    public EqualizerDeviceImpl (final DataSetupEx dataSetup)
    {
        super (dataSetup, 0, 3 * NUMBER_OF_BANDS + 2, 0, 0, 0);

        Arrays.fill (this.bandTypes, BANDOFF);
    }


    /** {@inheritDoc} */
    @Override
    public int getBandCount ()
    {
        return NUMBER_OF_BANDS;
    }


    /** {@inheritDoc} */
    @Override
    public String getType (final int index)
    {
        return this.doesExist () ? this.bandTypes[index] : BANDOFF;
    }


    /** {@inheritDoc} */
    @Override
    public void setType (final int index, final String type)
    {
        final String typeIndex = EQ_TYPE_INDICES.get (type);
        if (typeIndex != null)
            this.sender.processStringArg (Processor.DEVICE, "eq-band/" + index, typeIndex);
    }


    /**
     * Set the band type.
     *
     * @param index The index, 0-7
     * @param typeIndex The indexed type as a string
     */
    public void setTypeInternal (final int index, final String typeIndex)
    {
        final String type = EQ_TYPE_INDICES_INV.get (typeIndex);
        this.bandTypes[index] = type == null ? BANDOFF : type;
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
        return new QFactorInvertedParameter (this.getParameterBank ().getItem (3 * index + 2), this.valueChanger.getUpperBound ());
    }
}
