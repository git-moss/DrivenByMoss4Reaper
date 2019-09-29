// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadImpl extends ChannelImpl implements IDrumPad
{
    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public DrumPadImpl (final DataSetupEx dataSetup, final int index, final int numSends)
    {
        super (dataSetup, index, numSends);
    }
}
