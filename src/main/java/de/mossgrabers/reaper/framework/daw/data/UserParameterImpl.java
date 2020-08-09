// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


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
        super (dataSetup, index);

        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        if (!this.doesExist ())
            return;
        this.value = this.valueChanger.toNormalizedValue (value);
        this.sendUpdate ();
    }


    protected void sendUpdate ()
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack instanceof ChannelImpl)
            ((ChannelImpl) selectedTrack).sendTrackOSC ("user/param/" + this.getPosition () + "/value", this.value);
    }
}
