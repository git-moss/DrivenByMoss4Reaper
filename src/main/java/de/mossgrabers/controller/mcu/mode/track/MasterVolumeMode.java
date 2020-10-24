// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.track;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.parameterprovider.FixedParameterProvider;

import java.util.Collections;


/**
 * Mode for changing the master volume and metronome volume.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterVolumeMode extends AbstractMode<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MasterVolumeMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Master Volume", surface, model, true, null, Collections.singletonList (ContinuousID.FADER_MASTER));

        this.setParameters (new FixedParameterProvider (this.model.getMasterTrack ().getVolumeParameter ()));
        this.setParameters (ButtonID.SHIFT, new FixedParameterProvider (this.model.getTransport ().getMetronomeVolumeParameter ()));
    }
}
