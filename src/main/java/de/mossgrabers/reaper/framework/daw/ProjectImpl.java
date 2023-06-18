// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.daw.data.bank.ParameterBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.TrackBankImpl;


/**
 * Encapsulates the Project instance.
 *
 * @author Jürgen Moßgraber
 */
public class ProjectImpl extends BaseImpl implements IProject
{
    private final IModel            model;
    private final Configuration     configuration;
    private String                  name = "None";
    private boolean                 isDirtyFlag;
    private final ParameterBankImpl parameterBank;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param model The model
     * @param configuration The configuration
     * @param numParams The number of parameters
     */
    public ProjectImpl (final DataSetupEx dataSetup, final IModel model, final Configuration configuration, final int numParams)
    {
        super (dataSetup);

        this.model = model;
        this.configuration = configuration;
        this.parameterBank = numParams > 0 ? new ParameterBankImpl (dataSetup, Processor.MASTER, numParams, null) : null;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.sender.enableUpdates (Processor.PROJECT, enable);
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
        this.sendOSC ("createSceneFromPlayingLauncherClips");
    }


    /** {@inheritDoc} */
    @Override
    public void createScene ()
    {
        final int newClipLenghthInBeats = this.configuration.getNewClipLenghthInBeats (this.model.getTransport ().getQuartersPerMeasure ());
        this.sendOSC ("createScene", newClipLenghthInBeats);
    }


    /**
     * Set the project name.
     *
     * @param name The name of the project
     */
    public void setInternalName (final String name)
    {
        this.name = name;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDirty ()
    {
        return this.isDirtyFlag;
    }


    /**
     * Set the dirty flag.
     *
     * @param isDirty True to set the project dirty
     */
    public void setDirty (final boolean isDirty)
    {
        this.isDirtyFlag = isDirty;
    }


    /** {@inheritDoc} */
    @Override
    public void save ()
    {
        this.sender.invokeAction (Actions.PROJECT_SAVE);
    }


    /** {@inheritDoc} */
    @Override
    public void load ()
    {
        this.sender.invokeAction (Actions.PROJECT_LOAD);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getCueVolumeParameter ()
    {
        return EmptyParameter.INSTANCE;
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
    public IParameter getCueMixParameter ()
    {
        return EmptyParameter.INSTANCE;
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


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.PROJECT;
    }


    /** {@inheritDoc} */
    @Override
    public IParameterBank getParameterBank ()
    {
        return this.parameterBank;
    }
}
