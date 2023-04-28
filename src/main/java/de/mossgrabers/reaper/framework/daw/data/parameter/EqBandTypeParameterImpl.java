// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.EqualizerBandType;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


/**
 * Encapsulates the equalizer band type.
 *
 * @author Jürgen Moßgraber
 */
public class EqBandTypeParameterImpl extends ParameterImpl
{
    private static final String                          BAND                = "band/";

    private static final Map<EqualizerBandType, Integer> EQ_TYPE_INDICES     = new EnumMap<> (EqualizerBandType.class);
    private static final Map<Integer, EqualizerBandType> EQ_TYPE_INDICES_INV = new HashMap<> ();

    static
    {
        EQ_TYPE_INDICES.put (EqualizerBandType.OFF, Integer.valueOf (-1));
        EQ_TYPE_INDICES.put (EqualizerBandType.LOWCUT, Integer.valueOf (4));
        EQ_TYPE_INDICES.put (EqualizerBandType.LOWSHELF, Integer.valueOf (0));
        EQ_TYPE_INDICES.put (EqualizerBandType.BELL, Integer.valueOf (8));
        EQ_TYPE_INDICES.put (EqualizerBandType.HIGHCUT, Integer.valueOf (3));
        EQ_TYPE_INDICES.put (EqualizerBandType.HIGHSHELF, Integer.valueOf (1));
        EQ_TYPE_INDICES.put (EqualizerBandType.NOTCH, Integer.valueOf (6));

        EQ_TYPE_INDICES_INV.put (Integer.valueOf (-1), EqualizerBandType.OFF);
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (4), EqualizerBandType.LOWCUT);
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (0), EqualizerBandType.LOWSHELF);
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (8), EqualizerBandType.BELL);
        // Band (alt)
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (9), EqualizerBandType.BELL);
        // Band (alt2)
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (2), EqualizerBandType.BELL);
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (3), EqualizerBandType.HIGHCUT);
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (1), EqualizerBandType.HIGHSHELF);
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (6), EqualizerBandType.NOTCH);
        // 5 == Allpass, which is not supported
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (5), EqualizerBandType.BELL);
        // 7 == Bandpass, which is not supported
        EQ_TYPE_INDICES_INV.put (Integer.valueOf (7), EqualizerBandType.BELL);
    }

    private final int         bandIndex;
    private EqualizerBandType bandType = EqualizerBandType.OFF;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param bandIndex The index of the band 0-7
     */
    public EqBandTypeParameterImpl (final DataSetupEx dataSetup, final int bandIndex)
    {
        super (dataSetup, Processor.EQ, -1, 0);

        this.bandIndex = bandIndex;
    }


    /**
     * Set the band type.
     *
     * @param typeIndex The indexed type [-1,9]
     */
    public void setTypeInternal (final Integer typeIndex)
    {
        final EqualizerBandType type = EQ_TYPE_INDICES_INV.get (typeIndex);
        this.bandType = type == null ? EqualizerBandType.OFF : type;
    }


    /**
     * Set the band type.
     *
     * @param type The type
     */
    public void setType (final EqualizerBandType type)
    {
        this.dataSetup.getSender ().processStringArg (Processor.EQ, BAND + this.bandIndex, EQ_TYPE_INDICES.get (type).toString ());
    }


    /**
     * Get the band type.
     *
     * @return The type
     */
    public EqualizerBandType getTypeInternal ()
    {
        return this.bandType;
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        final EqualizerBandType [] types = EqualizerBandType.values ();
        final int typeIndex = (int) Math.round (value * (types.length - 1));
        this.setType (types[typeIndex]);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.setNormalizedValue (valueChanger.toNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return (int) Math.round (this.bandType.ordinal () * (127.0 / (EqualizerBandType.values ().length - 1)));
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.inc (valueChanger.calcKnobChange (value) > 0 ? 1 : -1);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.sendOSC (BAND + this.index, -1);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        final int idValue = Math.min (Math.max (0, (int) (this.bandType.ordinal () + increment)), 6);
        final Integer id = EQ_TYPE_INDICES.get (EqualizerBandType.values ()[idValue]);
        this.sendOSC (BAND + this.bandIndex, id.toString ());
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return "Band Type " + (this.bandIndex + 1);
    }
}
