// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
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
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import java.awt.event.MouseEvent;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwAbsoluteKnobImpl extends AbstractHwContinuousControl implements IHwAbsoluteKnob, IReaperHwControl
{
    private final HwControlLayout layout;

    private MidiInputImpl         midiInput;
    private BindType              midiType;
    private int                   midiChannel;
    private int                   midiControl;

    private boolean               isPressed;
    private double                pressedX;
    private double                pressedY;
    private int                   currentValue = 0;


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
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.midiInput = (MidiInputImpl) input;
        this.midiType = type;
        this.midiChannel = channel;
        this.midiControl = control;

        input.bind (this, type, channel, control);
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

        gc.fillCircle (centerX, centerY, radius, ColorEx.BLACK);

        final int length = (int) Math.round (this.currentValue * 360.0 / 127.0);
        ((GraphicsContextImpl) gc).fillArc (centerX, centerY, radius, ColorEx.RED, 270 - length, length);

        gc.fillCircle (centerX, centerY, radius * 0.8, ColorEx.BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void mouse (final int mouseEvent, final double x, final double y, final double scale)
    {
        if (this.midiInput == null)
            return;

        try
        {
            final Bounds bounds = this.layout.getBounds ();
            if (bounds == null)
                return;

            final double scaleX = x / scale;
            final double scaleY = y / scale;

            if (mouseEvent == MouseEvent.MOUSE_PRESSED && bounds.contains (scaleX, scaleY))
            {
                this.isPressed = true;
                this.pressedX = scaleX;
                this.pressedY = scaleY;
                return;
            }

            if (!this.isPressed)
                return;

            if (mouseEvent == MouseEvent.MOUSE_RELEASED)
            {
                this.isPressed = false;
                return;
            }

            if (mouseEvent == MouseEvent.MOUSE_DRAGGED)
            {
                final double offset = Math.min (3, Math.max (-3, this.pressedX - scaleX + (this.pressedY - scaleY)));
                this.pressedX = scaleX;
                this.pressedY = scaleY;

                if (this.midiType == BindType.CC)
                {
                    this.currentValue = (int) Math.max (0, Math.min (127, this.currentValue + offset));
                    this.midiInput.handleMidiMessage (new ShortMessage (0xB0, this.midiChannel, this.midiControl, this.currentValue));
                }
            }
        }
        catch (final InvalidMidiDataException ex)
        {
            this.host.error ("Invalid MIDI message.", ex);
        }
    }
}
