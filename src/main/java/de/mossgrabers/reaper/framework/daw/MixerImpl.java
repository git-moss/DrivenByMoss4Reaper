// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Implementation of the Mixer object.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixerImpl extends BaseImpl implements IMixer
{
    /**
     * Constructor
     *
     * @param host The DAW host
     * @param sender The OSC sender
     */
    public MixerImpl (final IHost host, final MessageSender sender)
    {
        super (host, sender);
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
}