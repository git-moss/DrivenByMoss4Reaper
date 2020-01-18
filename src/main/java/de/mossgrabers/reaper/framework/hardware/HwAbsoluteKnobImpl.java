// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.graphics.IGraphicsContext;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwAbsoluteKnobImpl extends AbstractHwContinuousControl implements IHwAbsoluteKnob, IReaperHwControl
{
    private final HwControlLayout layout;


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

        this.layout = new HwControlLayout (id);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int value)
    {
        input.bind (this, type, channel, value);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IParameter parameter)
    {
        // So far only used for user mode, which is not supported for Reaper
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
        this.layout.setBounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double scale)
    {
        final Bounds bounds = this.layout.getBounds ();
        if (bounds == null)
            return;

        final double radius = Math.min (bounds.getWidth (), bounds.getHeight ()) / 2.0;
        final double centerX = (bounds.getX () + radius) * scale;
        final double centerY = (bounds.getY () + radius) * scale;

        // TODO Draw according to value
        gc.fillCircle (centerX, centerY, radius, ColorEx.RED);
        gc.fillCircle (centerX, centerY, radius * 0.9, ColorEx.BLACK);
    }
}
