// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.observer.ObserverManagement;
import de.mossgrabers.reaper.communication.MessageSender;


/**
 * Base class for all Reaper proxies.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseImpl implements ObserverManagement
{
    protected final DataSetupEx   dataSetup;
    protected final IHost         host;
    protected final MessageSender sender;
    protected final IValueChanger valueChanger;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    public BaseImpl (final DataSetupEx dataSetup)
    {
        this.dataSetup = dataSetup;
        this.host = dataSetup != null ? dataSetup.getHost () : null;
        this.sender = dataSetup != null ? dataSetup.getSender () : null;
        this.valueChanger = dataSetup != null ? dataSetup.getValueChanger () : null;
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


    /**
     * Check if automation recording is enable and currently recording.
     *
     * @return True if automation recording is enable and currently recording
     */
    public boolean isAutomationRecActive ()
    {
        final ITransport transport = this.dataSetup.getTransport ();
        return transport != null && transport.isWritingArrangerAutomation ();
    }
}
