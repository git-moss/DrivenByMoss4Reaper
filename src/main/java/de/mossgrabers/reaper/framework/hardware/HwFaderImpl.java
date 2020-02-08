// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import java.awt.event.MouseEvent;


/**
 * Implementation of a proxy to a fader on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwFaderImpl extends AbstractHwContinuousControl implements IHwFader, IReaperHwControl
{
    private final HwControlLayout layout;
    private final boolean         isVertical;

    private MidiInputImpl         midiInput;
    private BindType              midiType;
    private int                   midiChannel;
    private int                   midiControl;
    private boolean               isPressed;
    private int                   currentValue = 0;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param host The controller host
     * @param label The label of the fader
     * @param isVertical True if the fader is displayed vertical (otherwise horizontal)
     */
    public HwFaderImpl (final String id, final IHost host, final String label, final boolean isVertical)
    {
        super (host, label);

        this.isVertical = isVertical;
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
        this.touchCommand = command;
        input.bindTouch (this, type, 0, control);
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        if (this.command != null)
            this.command.execute ((int) Math.round (value * 127.0));
        else if (this.pitchbendCommand != null)
        {
            final double v = value * 16383.0;
            final int data1 = (int) Math.min (127, Math.round (v % 128.0));
            final int data2 = (int) Math.min (127, Math.round (v / 128.0));
            this.pitchbendCommand.onPitchbend (data1, data2);
        }
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

        final double left = bounds.getX () * scale;
        final double top = bounds.getY () * scale;
        final double width = bounds.getWidth () * scale;
        final double height = bounds.getHeight () * scale;

        gc.fillRectangle (left, top, width, height, ColorEx.BLACK);

        final double factor = this.midiType == BindType.CC ? 127.0 : 16383.0;

        if (this.isVertical)
        {
            final double scaledHeight = height / factor * this.currentValue;
            gc.fillRectangle (left, top + height - scaledHeight, width, scaledHeight, ColorEx.WHITE);
        }
        else
        {
            final double scaledWidth = width / factor * this.currentValue;
            gc.fillRectangle (left, top, scaledWidth, height, ColorEx.WHITE);
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
            final Bounds bounds = this.layout.getBounds ();
            if (bounds == null)
                return;

            final double scaleX = x / scale;
            final double scaleY = y / scale;

            if (mouseEvent == MouseEvent.MOUSE_PRESSED && bounds.contains (scaleX, scaleY))
            {
                this.isPressed = true;
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
                double value;
                if (this.isVertical)
                    value = 1 - (scaleY - bounds.getY ()) / bounds.getHeight ();
                else
                    value = (scaleX - bounds.getX ()) / bounds.getWidth ();
                value = Math.max (0, Math.min (1, value));

                if (this.midiType == BindType.CC)
                {
                    this.currentValue = (int) Math.max (0, Math.round (value * 127.0));
                    this.midiInput.handleMidiMessage (new ShortMessage (0xB0, this.midiChannel, this.midiControl, this.currentValue));
                }
                else if (this.midiType == BindType.PITCHBEND)
                {
                    this.currentValue = (int) Math.max (0, Math.round (value * 16383.0));
                    final int data1 = (int) Math.min (127, Math.round (this.currentValue % 128.0));
                    final int data2 = (int) Math.min (127, Math.round (this.currentValue / 128.0));
                    this.midiInput.handleMidiMessage (new ShortMessage (0xE0, this.midiChannel, data1, data2));
                }
            }
        }
        catch (final InvalidMidiDataException ex)
        {
            this.host.error ("Invalid MIDI message.", ex);
        }
    }
}
