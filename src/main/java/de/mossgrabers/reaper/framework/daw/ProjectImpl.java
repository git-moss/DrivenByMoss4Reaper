// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.transformator.communication.MessageSender;


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
     * @param sender The OSC sender
     */
    public ProjectImpl (final MessageSender sender)
    {
        super (sender, null);
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
}
