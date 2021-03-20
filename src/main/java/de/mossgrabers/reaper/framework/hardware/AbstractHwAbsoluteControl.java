// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteControl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractHwAbsoluteControl extends AbstractHwContinuousControl implements IHwAbsoluteControl, IReaperHwControl
{
    protected final HwControlLayout layout;

    protected MidiInputImpl         midiInput;
    protected BindType              midiType;
    protected int                   midiChannel;
    protected int                   midiControl;
    // Alternative binding to the command
    protected IParameter            parameter;

    protected boolean               isPressed;
    protected int                   currentValue = 0;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param host The host
     * @param label The label of the control
     */
    protected AbstractHwAbsoluteControl (final String id, final IHost host, final String label)
    {
        super (host, label);

        this.layout = new HwControlLayout (id);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isBound ()
    {
        return this.parameter != null || super.isBound ();
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IParameter parameter)
    {
        this.parameter = parameter;
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
    public void handleValue (final double value)
    {
        if (this.parameter != null)
        {
            this.parameter.setNormalizedValue (value);
        }
        else if (this.command != null)
        {
            this.command.execute ((int) Math.round (value * 127.0));
        }
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
    public void disableTakeOver ()
    {
        // Takeover is currently not supported with Reaper
    }


    /** {@inheritDoc} */
    @Override
    public void setIndexInGroup (final int index)
    {
        // Not suported
    }
}
