// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.Views;


/**
 * The play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<PushControlSurface, PushConfiguration> implements SceneView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final PushControlSurface surface, final IModel model)
    {
        this (Views.VIEW_NAME_PLAY, surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final String name, final PushControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int sceneIndex, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IScene scene = this.model.getCurrentTrackBank ().getSceneBank ().getItem (sceneIndex);

        if (this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (this.surface.getTriggerId (ButtonID.DELETE));
            scene.remove ();
            return;
        }

        scene.select ();
        scene.launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButton (final int scene)
    {
        // TODO REmove
    }


    /** {@inheritDoc} */
    @Override
    public String getSceneButtonColor (final int scene)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        final IScene s = sceneBank.getItem (7 - scene);
        if (s.doesExist ())
            return s.isSelected () ? AbstractSessionView.COLOR_SELECTED_SCENE : AbstractSessionView.COLOR_SCENE;
        return AbstractSessionView.COLOR_SCENE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (this.surface.getTriggerId (ButtonID.DELETE));
            final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
            this.model.getNoteClip (8, 128).clearRow (editMidiChannel, this.keyManager.map (note));
            return;
        }
        super.onGridNote (note, velocity);
    }
}