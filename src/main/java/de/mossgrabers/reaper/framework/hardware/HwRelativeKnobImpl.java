// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.controller.valuechanger.RelativeValueChangers;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.framework.daw.data.parameter.IParameterEx;
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import java.awt.event.MouseEvent;


/**
 * Implementation of a proxy to a relative knob on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public class HwRelativeKnobImpl extends AbstractHwContinuousControl implements IHwRelativeKnob, IReaperHwControl
{
    private final HwControlLayout  layout;
    private final RelativeEncoding encoding;

    private MidiInputImpl          inputImpl;
    private int                    control;
    // Alternative binding to the command
    private IParameter             parameter;

    private boolean                isPressed;
    private double                 pressedX;
    private double                 pressedY;
    private boolean                shouldAdaptSensitivity = true;


    /**
     * Constructor. Uses Two's complement as the default relative encoding.
     *
     * @param id The ID of the control
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
    public void bind (final IParameter parameter)
    {
        this.parameter = parameter;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isBound ()
    {
        return this.parameter != null || super.isBound ();
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.inputImpl = (MidiInputImpl) input;
        this.type = type;
        this.channel = channel;
        this.control = control;

        input.bind (this, type, channel, control, this.encoding);
    }


    /** {@inheritDoc} */
    @Override
    public void unbind ()
    {
        if (this.input != null)
            this.input.unbind (this);
    }


    /** {@inheritDoc} */
    @Override
    public void rebind ()
    {
        if (this.input != null)
            this.input.bind (this, this.type, this.channel, this.control, this.encoding);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final TriggerCommand command, final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.touchCommand = command;
        input.bindTouch (this, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        // value is scaled to [0..1] but still encoded

        // scale back to [0..127]
        final int intValue = (int) Math.round (value * 127);

        if (this.parameter != null)
        {
            // Decode with the hardware encoding and re-encode with default encoding, which is used
            // for the direct binding
            final int cv = RelativeValueChangers.getDefault ().encode (RelativeValueChangers.get (this.encoding).decode (intValue));

            this.parameter.changeValue (cv);
        }
        else if (this.command != null)
            this.command.execute (intValue);
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

        final double radius = Math.min (bounds.width (), bounds.height ()) / 2.0;
        final double centerX = (bounds.x () + radius) * scale;
        final double centerY = (bounds.y () + radius) * scale;
        gc.fillCircle (centerX, centerY, radius * scale, ColorEx.BLACK);

        if (this.parameter instanceof final IParameterEx pi)
        {
            final double paramValue = pi.getInternalValue ();
            final int l = (int) Math.round (paramValue * 360.0);
            ((GraphicsContextImpl) gc).fillArc (centerX, centerY, radius * scale, ColorEx.RED, 270 - l - 4, 4);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void mouse (final int mouseEvent, final double x, final double y, final double scale)
    {
        final Bounds bounds = this.layout.getBounds ();
        if (bounds == null)
            return;

        final double scaleX = x / scale;
        final double scaleY = y / scale;

        switch (mouseEvent)
        {
            case MouseEvent.MOUSE_PRESSED:
                if (bounds.contains (scaleX, scaleY))
                {
                    this.isPressed = true;
                    this.pressedX = scaleX;
                    this.pressedY = scaleY;
                }
                break;

            case MouseEvent.MOUSE_RELEASED:
                this.isPressed = false;
                break;

            case MouseEvent.MOUSE_DRAGGED:
                try
                {
                    if (!this.isPressed)
                        return;

                    final int speed = (int) Math.min (3, Math.max (-3, Math.round (this.pressedX - scaleX + (this.pressedY - scaleY))));
                    if (speed == 0)
                        return;
                    this.pressedX = scaleX;
                    this.pressedY = scaleY;

                    final int value = RelativeValueChangers.get (this.encoding).encode (speed);

                    if (this.inputImpl == null)
                    {
                        this.command.execute (value);
                        return;
                    }

                    if (this.type == BindType.CC)
                        this.inputImpl.handleMidiMessage (new ShortMessage (0xB0, this.channel, this.control, value));
                }
                catch (final InvalidMidiDataException ex)
                {
                    this.host.error ("Invalid MIDI message.", ex);
                }
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setSensitivity (final double sensitivity)
    {
        RelativeValueChangers.setSensitivity (sensitivity);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndexInGroup (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean shouldAdaptSensitivity ()
    {
        return this.shouldAdaptSensitivity;
    }


    /** {@inheritDoc} */
    @Override
    public void setShouldAdaptSensitivity (final boolean shouldAdaptSensitivity)
    {
        this.shouldAdaptSensitivity = shouldAdaptSensitivity;
    }
}
