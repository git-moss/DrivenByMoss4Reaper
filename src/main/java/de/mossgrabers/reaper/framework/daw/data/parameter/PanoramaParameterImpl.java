// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of a tracks' panorama parameter.
 *
 * @author Jürgen Moßgraber
 */
public class PanoramaParameterImpl extends ParameterImpl
{
    private static final Object PANORAMA_UPDATE_LOCK = new Object ();


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the send
     * @param defaultValue The default value for resetting parameters [0..1]
     */
    public PanoramaParameterImpl (final DataSetupEx dataSetup, final int index, final double defaultValue)
    {
        super (dataSetup, index, defaultValue);

        this.setInternalName ("Pan");
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        this.sender.processIntArg (Processor.TRACK, this.createCommand ("pan/touch"), isBeingTouched ? 1 : 0);

        super.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendValue ()
    {
        synchronized (PANORAMA_UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates (Processor.TRACK);
            this.sender.processDoubleArg (Processor.TRACK, this.createCommand ("pan"), this.value);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected String createCommand (final String command)
    {
        return this.getIndex () + "/" + command;
    }
}
