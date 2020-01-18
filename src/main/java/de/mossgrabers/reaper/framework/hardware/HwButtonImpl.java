// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwButton;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;


/**
 * Implementation of a proxy to a button on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwButtonImpl extends AbstractHwButton implements IReaperHwControl
{
    private final HwControlLayout layout;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param host The host
     * @param label The label of the button
     */
    public HwButtonImpl (final String id, final IHost host, final String label)
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
    public void bind (final TriggerCommand command)
    {
        this.command = command;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control, final int value)
    {
        input.bind (this, type, channel, control, value);
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.layout.setBounds (x, y, width, height);
        if (this.light != null)
            this.light.setBounds (x, y, width, height);
    }


    /** {@index} */
    @Override
    public void update ()
    {
        if (this.light != null)
            this.light.update ();
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double scale)
    {
        ColorEx textColor = ColorEx.WHITE;
        if (this.light != null)
        {
            final HwLightImpl hwLightImpl = (HwLightImpl) this.light;
            hwLightImpl.draw (gc, scale);
            final ColorEx colorState = hwLightImpl.getColorState ();
            if (colorState != null)
                textColor = ColorEx.calcContrastColor (colorState);
        }

        if (this.label != null)
        {
            final Bounds bounds = this.layout.getBounds ();
            final double width = bounds.getWidth () * scale;
            final double height = bounds.getHeight () * scale;
            final double fontSize = ((GraphicsContextImpl) gc).calculateFontSize (this.label, height, width, 6.0);
            gc.drawTextInBounds (this.label, bounds.getX () * scale, bounds.getY () * scale, width, height, Align.CENTER, textColor, fontSize);
        }
    }
}
