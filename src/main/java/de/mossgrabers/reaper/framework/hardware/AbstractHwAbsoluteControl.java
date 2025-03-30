// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteControl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.framework.midi.MidiInputImpl;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractHwAbsoluteControl extends AbstractHwContinuousControl implements IHwAbsoluteControl, IReaperHwControl
{
    protected final HwControlLayout layout;

    protected MidiInputImpl         inputImpl;
    protected int                   control;
    protected boolean               isHiRes;

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


    /**
     * Returns true if this is bound to 14-bit hi-res values (2 CCs).
     *
     * @return True if it is hi-res
     */
    public boolean isHiRes ()
    {
        return this.isHiRes;
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
        this.bind (input, type, channel, control, false);
    }


    /** {@inheritDoc} */
    @Override
    public void bindHiRes (final IMidiInput input, final int channel, final int control)
    {
        this.bind (input, BindType.CC, channel, control, true);
    }


    private void bind (final IMidiInput input, final BindType type, final int channel, final int control, final boolean isHiRes)
    {
        this.inputImpl = (MidiInputImpl) input;
        this.type = type;
        this.channel = channel;
        this.control = control;

        this.isHiRes = isHiRes;
        if (isHiRes)
            input.bindHiRes (this, channel, control);
        else
            input.bind (this, type, channel, control);
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
            this.input.bind (this, this.type, this.channel, this.control);
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
        // Take over is currently not supported with Reaper
    }


    /** {@inheritDoc} */
    @Override
    public void setIndexInGroup (final int index)
    {
        // Not supported
    }
}
