// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.reaper.framework.Actions;


/**
 * Encapsulates the Project instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProjectImpl extends BaseImpl implements IProject
{
    private String name = "None";


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    public ProjectImpl (final DataSetupEx dataSetup)
    {
        super (dataSetup);
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
}
