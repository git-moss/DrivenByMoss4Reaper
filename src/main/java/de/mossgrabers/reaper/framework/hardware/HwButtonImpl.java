// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
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
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import java.awt.event.MouseEvent;


/**
 * Implementation of a proxy to a button on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwButtonImpl extends AbstractHwButton implements IReaperHwControl
{
    private final HwControlLayout layout;

    private MidiInputImpl         midiInput;
    private BindType              midiType;
    private int                   midiChannel;
    private int                   midiControl;
    private int                   midiValue;

    private double                currentY = -1;


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
    public void bind (final TriggerCommand command)
    {
        this.command = command;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.midiInput = (MidiInputImpl) input;
        this.midiType = type;
        this.midiChannel = channel;
        this.midiControl = control;
        this.midiValue = -1;

        input.bind (this, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control, final int value)
    {
        this.midiInput = (MidiInputImpl) input;
        this.midiType = type;
        this.midiChannel = channel;
        this.midiControl = control;
        this.midiValue = value;

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


    /** {@inheritDoc} */
    @Override
    public void mouse (final int mouseEvent, final double x, final double y, final double scale)
    {
        if (this.midiInput == null)
            return;

        try
        {
            if (mouseEvent == MouseEvent.MOUSE_RELEASED)
            {
                if (this.isPressed () && this.midiValue < 0)
                {
                    final int type = this.midiType == BindType.CC ? 0xB0 : 0x80;
                    this.midiInput.handleMidiMessage (new ShortMessage (type, this.midiChannel, this.midiControl, 0));
                }
                return;
            }

            final double scaleX = x / scale;
            final double scaleY = y / scale;

            final Bounds bounds = this.layout.getBounds ();
            if (!bounds.contains (scaleX, scaleY))
                return;

            if (mouseEvent == MouseEvent.MOUSE_PRESSED)
            {
                final double value = 1 - Math.abs (scaleY - bounds.getY ()) / bounds.getHeight ();
                final int type = this.midiType == BindType.CC ? 0xB0 : 0x90;
                this.midiInput.handleMidiMessage (new ShortMessage (type, this.midiChannel, this.midiControl, (int) Math.max (0, Math.round (value * 127.0))));
                return;
            }

            if (mouseEvent == MouseEvent.MOUSE_DRAGGED && this.midiType == BindType.NOTE && this.isPressed ())
            {
                if (this.currentY < 0)
                    this.currentY = bounds.getY ();
                final double value = 1 - Math.abs (scaleY - this.currentY) / bounds.getHeight ();
                this.midiInput.handleMidiMessage (new ShortMessage (0xA0, this.midiChannel, this.midiControl, (int) Math.max (0, Math.round (value * 127.0))));
            }
        }
        catch (final InvalidMidiDataException ex)
        {
            this.host.error ("Invalid MIDI message.", ex);
        }
    }
}
