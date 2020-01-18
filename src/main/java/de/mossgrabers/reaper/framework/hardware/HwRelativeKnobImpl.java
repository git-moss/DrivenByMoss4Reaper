// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.controller.valuechanger.Relative2ValueChanger;
import de.mossgrabers.framework.controller.valuechanger.Relative3ValueChanger;
import de.mossgrabers.framework.controller.valuechanger.Relative4ValueChanger;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.graphics.IGraphicsContext;

import java.util.EnumMap;
import java.util.Map;


/**
 * Implementation of a proxy to a relative knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwRelativeKnobImpl extends AbstractHwContinuousControl implements IHwRelativeKnob, IReaperHwControl
{
    private final static Map<RelativeEncoding, IValueChanger> VALUE_CHANGERS = new EnumMap<> (RelativeEncoding.class);

    static
    {
        VALUE_CHANGERS.put (RelativeEncoding.TWOS_COMPLEMENT, new DefaultValueChanger (127, 1, 1));
        VALUE_CHANGERS.put (RelativeEncoding.OFFSET_BINARY, new Relative3ValueChanger (127, 1, 1));
        VALUE_CHANGERS.put (RelativeEncoding.SIGNED_BIT, new Relative2ValueChanger (127, 1, 1));
        VALUE_CHANGERS.put (RelativeEncoding.SIGNED_BIT2, new Relative4ValueChanger (127, 1, 1));
    }

    private final HwControlLayout  layout;
    private final RelativeEncoding encoding;


    /**
     * Constructor. Uses Two's complement as the default relative encoding.
     *
     * @param id
     *
     * @param host The controller host
     * @param label The label of the knob
     */
    public HwRelativeKnobImpl (final String id, final IHost host, final String label)
    {
        this (id, host, label, RelativeEncoding.TWOS_COMPLEMENT);
    }


    /**
     * Constructor.
     *
     * @param id The controller host
     * @param host The label of the knob
     * @param label The encoding of the relative value
     * @param encoding The relative encoding
     */
    public HwRelativeKnobImpl (final String id, final IHost host, final String label, final RelativeEncoding encoding)
    {
        super (host, label);

        this.layout = new HwControlLayout (id);
        this.encoding = encoding;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        input.bind (this, type, channel, control, this.encoding);
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
        this.touchCommand = command;
        input.bindTouch (this, type, 0, control);
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        final int intValue = (int) Math.round (value * 127);
        final int speed = (int) VALUE_CHANGERS.get (this.encoding).calcKnobSpeed (intValue);
        this.command.execute (speed);
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
        gc.fillCircle (centerX, centerY, radius * scale, ColorEx.BLACK);
    }
}
