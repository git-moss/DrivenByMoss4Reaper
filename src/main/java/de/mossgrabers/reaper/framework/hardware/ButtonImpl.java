// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractButton;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * Implementation of a proxy to a button on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ButtonImpl extends AbstractButton
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param label The label of the button
     */
    public ButtonImpl (final IHost host, final String label)
    {
        super (host, label);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int value)
    {
        input.bind (this, type, channel, value);
    }
}
