// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a master track parameter (volume or panorama).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterTrackParameterImpl extends ParameterImpl
{
    private final String paramName;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param paramName The name of the parameter
     */
    public MasterTrackParameterImpl (final DataSetupEx dataSetup, final String paramName)
    {
        super (dataSetup, 0);

        this.paramName = paramName;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendValue ()
    {
        this.sender.processDoubleArg (Processor.MASTER, this.createCommand (this.paramName), this.value);
    }


    /** {@inheritDoc} */
    @Override
    protected String createCommand (final String command)
    {
        return command;
    }
}
