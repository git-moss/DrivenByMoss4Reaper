// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IFader;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * Implementation of a proxy to a fader on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderImpl implements IFader
{
    private final IHost       host;
    private final String      label;
    private ContinuousCommand command;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param label The label of the fader
     */
    public FaderImpl (final IHost host, final String label)
    {
        this.host = host;
        this.label = label;
    }


    /** {@inheritDoc} */
    @Override
    public IFader bind (final ContinuousCommand command)
    {
        this.command = command;
        // TODO Add a description text
        // this.hardwareFader.addBinding (this.host.createAction (this::handleValue, () -> "TODO"));
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int value)
    {
        this.bind (input, type, 0, value);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int value)
    {
        input.bind (this, type, channel, value);
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        // TODO Support pitchbend
        this.command.execute ((int) Math.round (value * 127.0));
    }


    /** {@inheritDoc} */
    @Override
    public ContinuousCommand getCommand ()
    {
        return this.command;
    }


    /** {@inheritDoc} */
    @Override
    public void update ()
    {
        // TODO Send feedback to motor fader
    }
}
