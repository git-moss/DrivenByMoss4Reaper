package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to display record options on long-press otherwise has the normal record command
 * behaviour.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushRecordCommand extends RecordCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PushRecordCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
        {
            this.surface.setTriggerConsumed (PushControlSurface.PUSH_BUTTON_RECORD);
            this.surface.getModeManager ().setActiveMode (Modes.REC_ARM);
            return;
        }

        super.executeNormal (event);
    }
}
