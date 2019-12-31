// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwAbsoluteKnobImpl extends AbstractHwContinuousControl implements IHwAbsoluteKnob, IReaperHwControl
{
    private final String id;
    private Bounds       bounds;


    /**
     * Constructor.
     * 
     * @param id The ID of the control
     * @param host The controller host
     * @param label The label of the knob
     */
    public HwAbsoluteKnobImpl (final String id, final IHost host, final String label)
    {
        super (host, label);

        this.id = id;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int value)
    {
        input.bind (this, type, channel, value);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final TriggerCommand command, final IMidiInput input, final BindType type, final int control)
    {
        // No touch on absolute knob
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        this.command.execute ((int) Math.round (value * 127.0));
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.bounds = new Bounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public String getId ()
    {
        return this.id;
    }


    /** {@inheritDoc} */
    @Override
    public Bounds getBounds ()
    {
        return this.bounds;
    }
}
