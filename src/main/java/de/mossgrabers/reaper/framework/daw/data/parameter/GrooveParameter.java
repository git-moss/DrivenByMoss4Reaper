// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;

import java.util.EnumMap;
import java.util.Map;


/**
 * Implementation of a Groove parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveParameter extends ParameterImpl
{
    private static final Map<GrooveParameterID, String> NAMES = new EnumMap<> (GrooveParameterID.class);

    static
    {
        NAMES.put (GrooveParameterID.ENABLED, "Enabled");
        NAMES.put (GrooveParameterID.SHUFFLE_AMOUNT, "Shuffle Amount");
    }

    private final GrooveParameterID grooveParameterID;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param grooveParameterID The ID of the groove parameter
     */
    public GrooveParameter (final DataSetupEx dataSetup, final GrooveParameterID grooveParameterID)
    {
        super (dataSetup, grooveParameterID.ordinal (), 0);

        this.grooveParameterID = grooveParameterID;

        this.setExists (true);

        this.setInternalName (NAMES.get (grooveParameterID));
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        if (this.grooveParameterID == GrooveParameterID.ENABLED)
            return Integer.toString ((int) this.value);
        return String.format ("%.1f %%", Double.valueOf ((this.value * 2.0 - 1.0) * 100.0));
    }


    /** {@inheritDoc} */
    @Override
    public void setInternalValue (final double value)
    {
        final double v;
        if (this.grooveParameterID == GrooveParameterID.ENABLED)
            v = value > 0 ? 1.0 : 0;
        else
            v = (value + 1.0) / 2.0;
        super.setInternalValue (v);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendValue ()
    {
        if (this.grooveParameterID == GrooveParameterID.ENABLED)
            this.sendOSC ("active", this.value);
        else
            this.sendOSC ("amount", this.value * 2.0 - 1.0);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.GROOVE;
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        if (this.grooveParameterID == GrooveParameterID.ENABLED)
            super.resetValue ();
        else
            this.setNormalizedValue (0.5);
    }
}
