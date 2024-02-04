// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
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
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import java.awt.event.MouseEvent;


/**
 * Implementation of a proxy to a button on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public class HwButtonImpl extends AbstractHwButton implements IReaperHwControl
{
    private final HwControlLayout layout;

    private MidiInputImpl         inputImpl;
    private int                   control;
    private int                   value;

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
        this.bind (input, type, channel, control, -1);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control, final int value)
    {
        this.inputImpl = (MidiInputImpl) input;
        this.type = type;
        this.channel = channel;
        this.control = control;
        this.value = value;

        if (value < 0)
            input.bind (this, type, channel, control);
        else
            input.bind (this, type, channel, control, value);
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
        if (this.input == null)
            return;

        this.unbind ();

        if (this.value < 0)
            this.input.bind (this, this.type, this.channel, this.control);
        else
            this.input.bind (this, this.type, this.channel, this.control, this.value);
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
            if (bounds == null)
                return;
            final double width = bounds.width () * scale;
            final double height = bounds.height () * scale;
            final double fontSize = ((GraphicsContextImpl) gc).calculateFontSize (this.label, height, width, 8.0);
            gc.drawTextInBounds (this.label, bounds.x () * scale, bounds.y () * scale, width, height, Align.CENTER, textColor, fontSize);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void mouse (final int mouseEvent, final double x, final double y, final double scale)
    {
        // Check if click is in the bounds of the button
        final double scaleX = x / scale;
        final double scaleY = y / scale;
        final Bounds bounds = this.layout.getBounds ();
        if (bounds == null || !bounds.contains (scaleX, scaleY))
            return;

        // If MIDI is not attached simply execute the command
        if (this.inputImpl == null)
        {
            if (this.command == null)
                return;

            if (mouseEvent == MouseEvent.MOUSE_PRESSED)
                this.command.execute (ButtonEvent.DOWN, 127);
            else if (mouseEvent == MouseEvent.MOUSE_RELEASED)
                this.command.execute (ButtonEvent.UP, 0);
            return;
        }

        // Send MIDI event to indirectly trigger the command
        try
        {
            switch (mouseEvent)
            {
                case MouseEvent.MOUSE_RELEASED:
                    this.handleMouseUp ();
                    break;

                case MouseEvent.MOUSE_PRESSED:
                    this.handleMouseDown (scaleY, bounds);
                    break;

                case MouseEvent.MOUSE_DRAGGED:
                    if (this.type == BindType.NOTE && this.isPressed ())
                        this.handleMouseDragged (scaleY, bounds);
                    break;

                default:
                    // Not used
                    break;
            }
        }
        catch (final InvalidMidiDataException ex)
        {
            this.host.error ("Invalid MIDI message.", ex);
        }
    }


    protected void handleMouseDragged (final double scaleY, final Bounds bounds) throws InvalidMidiDataException
    {
        if (this.currentY < 0)
            this.currentY = bounds.y ();
        final double v = 1 - Math.abs (scaleY - this.currentY) / bounds.height ();
        this.inputImpl.handleMidiMessage (new ShortMessage (0xA0, this.channel, this.control, (int) Math.max (0, Math.round (v * 127.0))));
    }


    protected void handleMouseUp () throws InvalidMidiDataException
    {
        if (this.isPressed () && this.value < 0)
        {
            final int type = this.type == BindType.CC ? 0xB0 : 0x80;
            this.inputImpl.handleMidiMessage (new ShortMessage (type, this.channel, this.control, 0));
        }
    }


    protected void handleMouseDown (final double scaleY, final Bounds bounds) throws InvalidMidiDataException
    {
        final double v = 1 - Math.abs (scaleY - bounds.y ()) / bounds.height ();
        final int type = this.type == BindType.CC ? 0xB0 : 0x90;
        int midiValue;
        if (this.value < 0)
            midiValue = (int) Math.max (0, Math.round (v * 127.0));
        else
            midiValue = this.value;
        this.inputImpl.handleMidiMessage (new ShortMessage (type, this.channel, this.control, midiValue));
    }
}
