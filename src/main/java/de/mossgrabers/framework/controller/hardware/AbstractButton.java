// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract implementation of a proxy to a button on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractButton extends ControlImpl implements IButton
{
    private static final int BUTTON_STATE_INTERVAL = 400;

    protected final IHost    host;
    private final String     label;

    protected TriggerCommand command;
    protected ILight         light;

    private ButtonEvent      state;
    private boolean          isConsumed;


    /**
     * Constructor.
     *
     * @param host The host
     * @param label The label of the button
     */
    public AbstractButton (final IHost host, final String label)
    {
        this.host = host;
        this.label = label;
    }


    /** {@inheritDoc} */
    @Override
    public IButton bind (final TriggerCommand command)
    {
        this.command = command;
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int value)
    {
        this.bind (input, type, 0, value);
    }


    /**
     * Handle a button press.
     */
    protected void handleButtonPressed ()
    {
        this.state = ButtonEvent.DOWN;
        this.isConsumed = false;
        this.host.scheduleTask (this::checkButtonState, BUTTON_STATE_INTERVAL);
        this.command.execute (ButtonEvent.DOWN);
    }


    /**
     * Handle a button release.
     */
    protected void handleButtonRelease ()
    {
        this.state = ButtonEvent.UP;
        if (!this.isConsumed)
            this.command.execute (ButtonEvent.UP);
    }


    /** {@inheritDoc} */
    @Override
    public void addLight (final ILight light)
    {
        this.light = light;
    }


    /** {@inheritDoc} */
    @Override
    public ILight getLight ()
    {
        return this.light;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed ()
    {
        return this.state == ButtonEvent.DOWN || this.state == ButtonEvent.LONG;
    }


    /** {@inheritDoc} */
    @Override
    public void setConsumed ()
    {
        this.isConsumed = true;
    }


    /** {@inheritDoc} */
    @Override
    public void trigger (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.handleButtonPressed ();
        else if (event == ButtonEvent.UP)
            this.handleButtonRelease ();
    }


    /**
     * Get the label.
     *
     * @return The label
     */
    public String getLabel ()
    {
        return this.label;
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommand getCommand ()
    {
        return this.command;
    }


    /**
     * If the state of the given button is still down, the state is set to long and an event gets
     * fired.
     */
    private void checkButtonState ()
    {
        if (!this.isPressed ())
            return;
        this.state = ButtonEvent.LONG;
        this.command.execute (ButtonEvent.LONG);
    }
}
