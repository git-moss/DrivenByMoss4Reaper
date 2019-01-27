// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.observer.ObserverManagement;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * Base class for all Reaper proxies.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseImpl implements ObserverManagement
{
    protected final MessageSender sender;
    protected final IHost         host;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     */
    public BaseImpl (final IHost host, final MessageSender sender)
    {
        this.sender = sender;
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not supported
    }


    /**
     * Invokes the action for the given action identifier.
     *
     * @param id The action identifier, must not be null
     */
    public void invokeAction (final int id)
    {
        this.sender.invokeAction (id);
    }
}
