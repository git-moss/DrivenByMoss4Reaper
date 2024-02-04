// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * Encapsulates the data of the master tracks' panorama parameter.
 *
 * @author Jürgen Moßgraber
 */
public class MasterPanoramaParameterImpl extends ParameterImpl
{
    private static final Object MASTER_PANORAMA_UPDATE_LOCK = new Object ();


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param defaultValue The default value for resetting parameters [0..1]
     */
    public MasterPanoramaParameterImpl (final DataSetupEx dataSetup, final double defaultValue)
    {
        super (dataSetup, 0, defaultValue);

        this.setInternalName ("Master Pan");
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        this.sender.processIntArg (Processor.MASTER, "pan/touch", isBeingTouched ? 1 : 0);

        super.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendValue ()
    {
        synchronized (MASTER_PANORAMA_UPDATE_LOCK)
        {
            if (this.isAutomationRecActive ())
                this.sender.delayUpdates (Processor.MASTER);
            this.sender.processDoubleArg (Processor.MASTER, "pan", this.value);
        }
    }
}
