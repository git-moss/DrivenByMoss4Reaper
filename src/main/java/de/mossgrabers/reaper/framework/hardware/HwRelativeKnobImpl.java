// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
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
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;


/**
 * Implementation of a proxy to a relative knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwRelativeKnobImpl extends AbstractHwContinuousControl implements IHwRelativeKnob, IReaperHwControl
{
    private final static Map<RelativeEncoding, IValueChanger> VALUE_CHANGERS        = new EnumMap<> (RelativeEncoding.class);
    private final static DefaultValueChanger                  DEFAULT_VALUE_CHANGER = new DefaultValueChanger (127, 1);

    static
    {
        VALUE_CHANGERS.put (RelativeEncoding.TWOS_COMPLEMENT, DEFAULT_VALUE_CHANGER);
        VALUE_CHANGERS.put (RelativeEncoding.OFFSET_BINARY, new Relative3ValueChanger (127, 1));
        VALUE_CHANGERS.put (RelativeEncoding.SIGNED_BIT, new Relative2ValueChanger (127, 1));
        VALUE_CHANGERS.put (RelativeEncoding.SIGNED_BIT2, new Relative4ValueChanger (127, 1));
    }

    private final HwControlLayout  layout;
    private final RelativeEncoding encoding;

    private MidiInputImpl          midiInput;
    private BindType               midiType;
    private int                    midiChannel;
    private int                    midiControl;
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
    public boolean isBound ()
    {
        return this.parameter != null || super.isBound ();
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.midiInput = (MidiInputImpl) input;
        this.midiType = type;
        this.midiChannel = channel;
        this.midiControl = control;

        input.bind (this, type, channel, control, this.encoding);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IParameter parameter)
    {
        this.parameter = parameter;
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

        // Decode with the hardware encoding and re-encode with default encoding, which is used for
        // the direct binding
        final int cv = DEFAULT_VALUE_CHANGER.encode (VALUE_CHANGERS.get (this.encoding).decode (intValue));

        if (this.parameter != null)
            this.parameter.changeValue (cv);
        else if (this.command != null)
            this.command.execute (cv);
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

        if (this.parameter instanceof ParameterImpl)
        {
            final double paramValue = ((ParameterImpl) this.parameter).getInternalValue ();
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

        try
        {
            if (mouseEvent == MouseEvent.MOUSE_DRAGGED)
            {
                final int speed = (int) Math.min (3, Math.max (-3, Math.round (this.pressedX - scaleX + (this.pressedY - scaleY))));
                if (speed == 0)
                    return;
                this.pressedX = scaleX;
                this.pressedY = scaleY;

                final int value = VALUE_CHANGERS.get (this.encoding).encode (speed);

                if (this.midiInput == null)
                {
                    this.command.execute (value);
                    return;
                }

                if (this.midiType == BindType.CC)
                    this.midiInput.handleMidiMessage (new ShortMessage (0xB0, this.midiChannel, this.midiControl, value));
            }
        }
        catch (final InvalidMidiDataException ex)
        {
            this.host.error ("Invalid MIDI message.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setSensitivity (final double sensitivity)
    {
        VALUE_CHANGERS.forEach ( (encoding, valueChanger) -> valueChanger.setSensitivity (sensitivity));
    }


    /** {@inheritDoc} */
    @Override
    public void setIndexInGroup (final int index)
    {
        // Not suported
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
