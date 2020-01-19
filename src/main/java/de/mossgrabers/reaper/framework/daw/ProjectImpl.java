// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.reaper.framework.Actions;


/**
 * Encapsulates the Project instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProjectImpl extends BaseImpl implements IProject
{
    private final IModel model;
    private String       name = "None";


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param model The model
     */
    public ProjectImpl (final DataSetupEx dataSetup, final IModel model)
    {
        super (dataSetup);

        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates ("project", enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name != null && this.name.length () > 0 ? this.name : "None";
    }


    /** {@inheritDoc} */
    @Override
    public void previous ()
    {
        this.invokeAction (Actions.PROJECT_TAB_PREVIOUS);
    }


    /** {@inheritDoc} */
    @Override
    public void next ()
    {
        this.invokeAction (Actions.PROJECT_TAB_NEXT);
    }


    /** {@inheritDoc} */
    @Override
    public void createSceneFromPlayingLauncherClips ()
    {
        // Not supported
    }


    /**
     * Set the project name.
     *
     * @param name The name of the project
     */
    public void setName (final String name)
    {
        this.name = name;
    }


    /** {@inheritDoc} */
    @Override
    public void save ()
    {
        this.sender.invokeAction (Actions.PROJECT_SAVE);
    }


    /** {@inheritDoc} */
    @Override
    public String getCueVolumeStr ()
    {
        // Not supported
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getCueVolumeStr (final int limit)
    {
        // Not supported
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getCueVolume ()
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void changeCueVolume (final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setCueVolume (final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetCueVolume ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchCueVolume (final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public String getCueMixStr ()
    {
        // Not supported
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getCueMixStr (final int limit)
    {
        // Not supported
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getCueMix ()
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void changeCueMix (final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setCueMix (final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetCueMix ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchCueMix (final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSolo ()
    {
        return ((TrackBankImpl) this.model.getTrackBank ()).hasSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasMute ()
    {
        return ((TrackBankImpl) this.model.getTrackBank ()).hasMute ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearSolo ()
    {
        ((TrackBankImpl) this.model.getTrackBank ()).clearSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearMute ()
    {
        ((TrackBankImpl) this.model.getTrackBank ()).clearMute ();
    }
}
