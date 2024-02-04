// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.Actions;


/**
 * Implementation of the Mixer object.
 *
 * @author Jürgen Moßgraber
 */
public class MixerImpl extends BaseImpl implements IMixer
{
    /**
     * Constructor
     *
     * @param dataSetup Some configuration variables
     */
    public MixerImpl (final DataSetupEx dataSetup)
    {
        super (dataSetup);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipLauncherSectionVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleClipLauncherSectionVisibility ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCrossFadeSectionVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCrossFadeSectionVisibility ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDeviceSectionVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDeviceSectionVisibility ()
    {
        this.invokeAction (Actions.TOGGLE_FX_INSERTS);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isIoSectionVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIoSectionVisibility ()
    {
        // No IO section in the mixer available but use for 'FX Parameters'
        this.invokeAction (Actions.TOGGLE_FX_PARAMETERS);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMeterSectionVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMeterSectionVisibility ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSendSectionVisible ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSendsSectionVisibility ()
    {
        this.invokeAction (Actions.TOGGLE_FX_SENDS);
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return Processor.ACTION;
    }
}