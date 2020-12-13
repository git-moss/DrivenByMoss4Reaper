// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.data.parameter.GrooveParameter;

import java.util.EnumMap;
import java.util.Map;


/**
 * Implementation of the Groove object.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveImpl extends BaseImpl implements IGroove
{
    private Map<GrooveParameterID, IParameter> parameters = new EnumMap<> (GrooveParameterID.class);


    /**
     * Constructor
     *
     * @param dataSetup Some configuration variables
     */
    public GrooveImpl (final DataSetupEx dataSetup)
    {
        super (dataSetup);

        this.parameters.put (GrooveParameterID.ENABLED, new GrooveParameter (dataSetup, GrooveParameterID.ENABLED));
        this.parameters.put (GrooveParameterID.SHUFFLE_AMOUNT, new GrooveParameter (dataSetup, GrooveParameterID.SHUFFLE_AMOUNT));
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getParameter (final GrooveParameterID id)
    {
        final IParameter parameter = this.parameters.get (id);
        return parameter == null ? EmptyParameter.INSTANCE : parameter;
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        // Not used
        return null;
    }
}
