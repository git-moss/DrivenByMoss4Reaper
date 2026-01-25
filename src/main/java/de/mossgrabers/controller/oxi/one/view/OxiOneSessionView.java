// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import java.util.Optional;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneColorManager;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Session view (only Mk2).
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneSessionView extends AbstractSessionView<OxiOneControlSurface, OxiOneConfiguration> implements IScrollableView
{
    protected boolean                 isShowTemporarily;
    private final OxiOneConfiguration configuration;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneSessionView (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 8, 16, true);

        this.ignoreClipColorForPlayAndRecord = true;

        this.configuration = this.surface.getConfiguration ();

        final int redLo = OxiOneColorManager.OXI_ONE_COLOR_DARKER_RED;
        final int redHi = OxiOneColorManager.OXI_ONE_COLOR_RED;
        final int black = OxiOneColorManager.OXI_ONE_COLOR_BLACK;
        final int white = OxiOneColorManager.OXI_ONE_COLOR_WHITE;
        final int green = OxiOneColorManager.OXI_ONE_COLOR_GREEN;
        final int amber = OxiOneColorManager.OXI_ONE_COLOR_ORANGE;
        final int gray = OxiOneColorManager.OXI_ONE_COLOR_GRAY;
        final LightInfo isRecording = new LightInfo (redHi, -1, false);
        final LightInfo isRecordingQueued = new LightInfo (redHi, black, true);
        final LightInfo isPlaying = new LightInfo (green, -1, false);
        final LightInfo isPlayingQueued = new LightInfo (green, green, true);
        final LightInfo isStopQueued = new LightInfo (green, green, true);
        final LightInfo hasContent = new LightInfo (amber, white, false);
        final LightInfo noContent = new LightInfo (black, -1, false);
        final LightInfo recArmed = new LightInfo (redLo, -1, false);
        final LightInfo isMuted = new LightInfo (gray, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, isStopQueued, hasContent, noContent, recArmed, isMuted);

        this.birdColorHasContent = hasContent;
        this.birdColorSelected = new LightInfo (OxiOneColorManager.OXI_ONE_COLOR_GREEN, -1, false);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (index >= 0 || index < 8)
        {
            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final ISceneBank sceneBank = tb.getSceneBank ();
            final IScene s = sceneBank.getItem (index);

            if (s.doesExist ())
                return DAWColor.getColorID (s.getColor ());
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        final boolean result = super.handleButtonCombinations (track, slot);

        if (this.isButtonCombination (ButtonID.DELETE) && this.configuration.isDeleteModeActive ())
            this.configuration.toggleDeleteModeActive ();
        else if (this.isButtonCombination (ButtonID.DUPLICATE) && this.configuration.isDuplicateModeActive () && (!slot.doesExist () || !slot.hasContent ()))
            this.configuration.toggleDuplicateModeActive ();

        return result;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isButtonCombination (final ButtonID buttonID)
    {
        if (super.isButtonCombination (buttonID) || buttonID == ButtonID.DELETE && this.configuration.isDeleteModeActive ())
            return true;

        return buttonID == ButtonID.DUPLICATE && this.configuration.isDuplicateModeActive ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> sel = tb.getSelectedItem ();
        final int selIndex = sel.isPresent () ? sel.get ().getIndex () : -1;
        final ISceneBank sceneBank = tb.getSceneBank ();
        scrollStates.setCanScrollLeft (selIndex > 0 || tb.canScrollPageBackwards ());
        scrollStates.setCanScrollRight (selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollPageForwards ());
        scrollStates.setCanScrollUp (sceneBank.canScrollPageBackwards ());
        scrollStates.setCanScrollDown (sceneBank.canScrollPageForwards ());
    }
}
