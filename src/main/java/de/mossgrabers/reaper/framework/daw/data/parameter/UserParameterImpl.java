// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.ChannelImpl;


/**
 * Encapsulates the data of a user parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserParameterImpl extends ParameterImpl
{
    private final IModel model;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the parameters
     * @param model The model for getting the selected track
     */
    public UserParameterImpl (final DataSetupEx dataSetup, final int index, final IModel model)
    {
        super (dataSetup, index, 0);

        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendValue ()
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (cursorTrack instanceof ChannelImpl)
            ((ChannelImpl) cursorTrack).sendPositionedItemOSC ("user/" + this.createCommand ("value"), this.value);
    }
}
