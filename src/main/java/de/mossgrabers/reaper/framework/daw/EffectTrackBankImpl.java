// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * A track bank of all effect tracks. Not used in Reaper.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EffectTrackBankImpl extends AbstractTrackBankImpl
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numTracks The number of track of a bank page
     * @param numScenes The number of scenes of a bank page
     */
    public EffectTrackBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numTracks, final int numScenes)
    {
        super (host, sender, valueChanger, numTracks, numScenes, 0);
        this.init ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return "";
    }
}