package de.mossgrabers.controller.oxi.one.command.trigger;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to handle the play button. Shift executes tap tempo.
 *
 * @author Jürgen Moßgraber
 */
public class OxiPlayCommand extends PlayCommand<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public OxiPlayCommand (final IModel model, final OxiOneControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeShifted ()
    {
        this.transport.tapTempo ();
    }
}
