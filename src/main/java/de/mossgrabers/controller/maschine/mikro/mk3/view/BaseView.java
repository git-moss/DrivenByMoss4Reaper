// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractView;


/**
 * Base class for views views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseView extends AbstractView<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration> implements PadButtons
{
    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The controller
     * @param model The model
     */
    public BaseView (final String name, final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super (name, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // LEDs cannot be controlled
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        int padIndex = note - 36;

        // TODO not working since there is no pressed state
        if (this.surface.isPressed (MaschineMikroMk3ControlSurface.MIKRO_3_NOTE_REPEAT))
        {
            this.surface.setTriggerConsumed (MaschineMikroMk3ControlSurface.MIKRO_3_NOTE_REPEAT);

            final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            final INoteRepeat noteRepeat = this.surface.getInput ().getDefaultNoteInput ().getNoteRepeat ();
            if (padIndex < 8)
            {
                noteRepeat.setPeriod (selectedTrack, Resolution.getValueAt (padIndex));
                this.model.getHost ().showNotification ("Repeat Period: " + Resolution.getNameAt (padIndex));
            }
            else
            {
                noteRepeat.setNoteLength (selectedTrack, Resolution.getValueAt (padIndex % 8));
                this.model.getHost ().showNotification ("Note Length: " + Resolution.getNameAt (padIndex % 8));
            }
            return;
        }

        if (velocity > 0)
            this.executeFunction (padIndex);
    }


    /**
     * Implement to execute whatever function the view has.
     *
     * @param padIndex The index of the pressed pad (0-15)
     */
    protected abstract void executeFunction (int padIndex);


    /** {@inheritDoc} */
    @Override
    public void onButton (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (index)
        {
            case 0:
                this.model.getCurrentTrackBank ().selectPreviousItem ();
                break;
            case 1:
                this.model.getCurrentTrackBank ().selectNextItem ();
                break;
            case 2:
                this.model.getCurrentTrackBank ().selectPreviousPage ();
                break;
            case 3:
                this.model.getCurrentTrackBank ().selectNextPage ();
                break;
            default:
                // Not used
                break;
        }
    }
}