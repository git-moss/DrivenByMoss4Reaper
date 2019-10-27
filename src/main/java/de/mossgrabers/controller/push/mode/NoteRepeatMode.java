// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.EditCapability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.daw.midi.NoteRepeatModes;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Editing the length of note repeat notes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatMode extends BaseMode
{
    private final IHost       host;
    private final INoteRepeat noteRepeat;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteRepeatMode (final PushControlSurface surface, final IModel model)
    {
        super ("Note Repeat", surface, model);

        this.isTemporary = true;
        this.host = this.model.getHost ();

        final INoteInput defaultNoteInput = surface.getInput ().getDefaultNoteInput ();
        this.noteRepeat = defaultNoteInput == null ? null : defaultNoteInput.getNoteRepeat ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.model.getGroove ().enableObservers (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.model.getGroove ().enableObservers (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();

        switch (index)
        {
            case 0:
            case 1:
                final int sel = Resolution.change (Resolution.getMatch (this.noteRepeat.getPeriod (selectedTrack)), this.model.getValueChanger ().calcKnobSpeed (value) > 0);
                this.noteRepeat.setPeriod (selectedTrack, Resolution.getValueAt (sel));
                break;

            case 2:
            case 3:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (this.noteRepeat.getNoteLength (selectedTrack)), this.model.getValueChanger ().calcKnobSpeed (value) > 0);
                    this.noteRepeat.setNoteLength (selectedTrack, Resolution.getValueAt (sel2));
                }
                break;

            case 5:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_MODE))
                {
                    final int sel2 = NoteRepeatModes.change (NoteRepeatModes.getIndex (this.noteRepeat.getMode (selectedTrack)), this.model.getValueChanger ().calcKnobSpeed (value) > 0);
                    this.noteRepeat.setMode (selectedTrack, NoteRepeatModes.getValueAt (sel2));
                }
                break;

            case 6:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_OCTAVES))
                    this.noteRepeat.setOctaves (selectedTrack, this.noteRepeat.getOctaves (selectedTrack) + (this.model.getValueChanger ().calcKnobSpeed (value) > 0 ? 1 : -1));
                break;

            case 7:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_SWING))
                    this.model.getGroove ().getParameters ()[1].changeValue (value);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (this.surface.getTriggerId (ButtonID.DELETE));

            switch (index)
            {
                case 5:
                    if (this.host.canEdit (EditCapability.NOTE_REPEAT_MODE))
                        this.noteRepeat.setMode (selectedTrack, "up");
                    break;

                case 6:
                    if (this.host.canEdit (EditCapability.NOTE_REPEAT_OCTAVES))
                        this.noteRepeat.setOctaves (selectedTrack, 1);
                    break;

                case 7:
                    if (this.host.canEdit (EditCapability.NOTE_REPEAT_SWING))
                        this.model.getGroove ().getParameters ()[1].resetValue ();
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || this.noteRepeat == null)
            return;

        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        switch (index)
        {
            case 0:
            case 1:
                final int sel = Resolution.change (Resolution.getMatch (this.noteRepeat.getPeriod (selectedTrack)), index == 1);
                this.noteRepeat.setPeriod (selectedTrack, Resolution.getValueAt (sel));
                break;

            case 2:
            case 3:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (this.noteRepeat.getNoteLength (selectedTrack)), index == 3);
                    this.noteRepeat.setNoteLength (selectedTrack, Resolution.getValueAt (sel2));
                }
                break;

            case 5:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_IS_FREE_RUNNING))
                    this.noteRepeat.toggleIsFreeRunning (selectedTrack);
                break;

            case 6:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY))
                    this.noteRepeat.toggleUsePressure (selectedTrack);
                break;

            case 7:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_SWING))
                    this.noteRepeat.toggleShuffle (selectedTrack);
                break;

            default:
                // Unused
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || this.noteRepeat == null)
            return;

        switch (index)
        {
            case 7:
                if (this.host.canEdit (EditCapability.NOTE_REPEAT_SWING))
                {
                    final IParameter grooveEnabled = this.model.getGroove ().getParameters ()[0];
                    grooveEnabled.setValue (grooveEnabled.getValue () == 0 ? this.model.getValueChanger ().getUpperBound () : 0);
                }
                break;

            default:
                // Unused
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        this.surface.updateTrigger (20, AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateTrigger (21, AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateTrigger (22, this.host.canEdit (EditCapability.NOTE_REPEAT_LENGTH) ? AbstractMode.BUTTON_COLOR_ON : AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateTrigger (23, this.host.canEdit (EditCapability.NOTE_REPEAT_LENGTH) ? AbstractMode.BUTTON_COLOR_ON : AbstractMode.BUTTON_COLOR_OFF);

        this.surface.updateTrigger (24, AbstractMode.BUTTON_COLOR_OFF);

        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_IS_FREE_RUNNING))
            this.surface.updateTrigger (25, !this.noteRepeat.isFreeRunning (selectedTrack) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        else
            this.surface.updateTrigger (25, AbstractMode.BUTTON_COLOR_OFF);

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY))
            this.surface.updateTrigger (26, this.noteRepeat.usePressure (selectedTrack) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        else
            this.surface.updateTrigger (26, AbstractMode.BUTTON_COLOR_OFF);

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_SWING))
            this.surface.updateTrigger (27, this.noteRepeat.isShuffle (selectedTrack) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        else
            this.surface.updateTrigger (27, AbstractMode.BUTTON_COLOR_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        for (int i = 0; i < 7; i++)
            this.surface.updateTrigger (102 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));

        this.surface.updateTrigger (109, this.model.getGroove ().getParameters ()[0].getValue () > 0 ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();

        display.setCell (0, 0, "Period:");
        final int selPeriodIndex = this.getSelectedPeriodIndex (selectedTrack);
        int pos = 0;
        for (final Pair<String, Boolean> p: Push1Display.createMenuList (4, Resolution.getNames (), selPeriodIndex))
        {
            display.setCell (pos, 1, (p.getValue ().booleanValue () ? Push1Display.SELECT_ARROW : " ") + p.getKey ());
            pos++;
        }

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_LENGTH))
        {
            display.setCell (0, 2, "Length:");
            final int selLengthIndex = this.getSelectedNoteLengthIndex (selectedTrack);
            pos = 0;
            for (final Pair<String, Boolean> p: Push1Display.createMenuList (4, Resolution.getNames (), selLengthIndex))
            {
                display.setCell (pos, 3, (p.getValue ().booleanValue () ? Push1Display.SELECT_ARROW : " ") + p.getKey ());
                pos++;
            }
        }

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        if (this.host.canEdit (EditCapability.NOTE_REPEAT_MODE))
        {
            final String bottomMenu = this.host.canEdit (EditCapability.NOTE_REPEAT_IS_FREE_RUNNING) ? "Sync" : "";
            final String mode = this.noteRepeat.getMode (selectedTrack);
            final int value = NoteRepeatModes.getIndex (mode) * upperBound / (NoteRepeatModes.values ().length - 1);
            display.setCell (0, 5, "Mode");
            display.setCell (1, 5, StringUtils.optimizeName (NoteRepeatModes.getName (mode), 8));
            display.setCell (2, 5, value, Format.FORMAT_VALUE);
            display.setCell (3, 5, bottomMenu);
        }

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_OCTAVES))
        {
            final int octaves = this.noteRepeat.getOctaves (selectedTrack);
            final String bottomMenu = this.host.canEdit (EditCapability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY) ? "Use Pressure" : "";
            final int value = octaves * upperBound / 8;
            display.setCell (0, 6, "Octaves");
            display.setCell (1, 6, Integer.toString (octaves));
            display.setCell (2, 6, value, Format.FORMAT_VALUE);
            display.setCell (3, 6, bottomMenu);
        }

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_SWING))
        {
            final IParameter shuffleParam = this.model.getGroove ().getParameters ()[1];
            display.setCell (0, 7, shuffleParam.getName (10));
            display.setCell (1, 7, shuffleParam.getDisplayedValue (8));
            display.setCell (2, 7, shuffleParam.getValue (), Format.FORMAT_VALUE);
            display.setCell (3, 7, "Shuffle");
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        if (this.noteRepeat == null)
            return;

        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();

        display.addOptionElement ("Period", "", false, "", "", false, false);
        final int selPeriodIndex = this.getSelectedPeriodIndex (selectedTrack);
        display.addListElement (6, Resolution.getNames (), selPeriodIndex);

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_LENGTH))
        {
            display.addOptionElement ("  Length", "", false, "", "", false, false);
            final int selLengthIndex = this.getSelectedNoteLengthIndex (selectedTrack);
            display.addListElement (6, Resolution.getNames (), selLengthIndex);
        }
        else
        {
            display.addEmptyElement ();
            display.addEmptyElement ();
        }

        display.addEmptyElement ();

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        if (this.host.canEdit (EditCapability.NOTE_REPEAT_MODE))
        {
            final String bottomMenu = this.host.canEdit (EditCapability.NOTE_REPEAT_IS_FREE_RUNNING) ? "Sync" : "";
            final boolean isBottomMenuEnabled = !this.noteRepeat.isFreeRunning (selectedTrack);
            final String mode = this.noteRepeat.getMode (selectedTrack);
            final int value = NoteRepeatModes.getIndex (mode) * upperBound / (NoteRepeatModes.values ().length - 1);
            display.addParameterElementWithPlainMenu ("", false, bottomMenu, null, isBottomMenuEnabled, "Mode", value, StringUtils.optimizeName (NoteRepeatModes.getName (mode), 8), this.isKnobTouched[5], -1);
        }
        else
            display.addEmptyElement ();

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_OCTAVES))
        {
            final int octaves = this.noteRepeat.getOctaves (selectedTrack);
            final String bottomMenu = this.host.canEdit (EditCapability.NOTE_REPEAT_USE_PRESSURE_TO_VELOCITY) ? "Use Pressure" : "";
            final boolean isBottomMenuEnabled = this.noteRepeat.usePressure (selectedTrack);
            final int value = octaves * upperBound / 8;
            display.addParameterElementWithPlainMenu ("", false, bottomMenu, null, isBottomMenuEnabled, "Octaves", value, Integer.toString (octaves), this.isKnobTouched[6], -1);
        }
        else
            display.addEmptyElement ();

        if (this.host.canEdit (EditCapability.NOTE_REPEAT_SWING))
        {
            final IParameter [] grooveParameters = this.model.getGroove ().getParameters ();
            final IParameter shuffleParam = grooveParameters[1];
            final int value = grooveParameters[0].getValue ();
            display.addParameterElementWithPlainMenu ("Groove " + grooveParameters[0].getDisplayedValue (8), value != 0, "Shuffle", null, this.noteRepeat.isShuffle (selectedTrack), shuffleParam.getName (10), shuffleParam.getValue (), shuffleParam.getDisplayedValue (8), this.isKnobTouched[7], -1);
        }
        else
            display.addEmptyElement ();
    }


    /**
     * Get the index of the selected period.
     *
     * @param selectedTrack The currently selected track
     * @return The selected period index
     */
    private int getSelectedPeriodIndex (final ITrack selectedTrack)
    {
        return this.noteRepeat == null ? -1 : Resolution.getMatch (this.noteRepeat.getPeriod (selectedTrack));
    }


    /**
     * Get the index of the selected length.
     *
     * @param selectedTrack The currently selected track
     * @return The selected lenth index
     */
    private int getSelectedNoteLengthIndex (final ITrack selectedTrack)
    {
        return this.noteRepeat == null ? -1 : Resolution.getMatch (this.noteRepeat.getNoteLength (selectedTrack));
    }
}
