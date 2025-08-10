// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.observer.IObserverManagement;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.communication.Processor;


/**
 * Base class for all Reaper proxies.
 *
 * @author Jürgen Moßgraber
 */
public abstract class BaseImpl implements IObserverManagement
{
    protected final DataSetupEx   dataSetup;
    protected final IHost         host;
    protected final BackendExchange sender;
    protected final IValueChanger valueChanger;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     */
    protected BaseImpl (final DataSetupEx dataSetup)
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
     * Check if automation recording is enabled and currently recording.
     *
     * @return True if automation recording is enable and currently recording
     */
    public boolean isAutomationRecActive ()
    {
        final ITransport transport = this.dataSetup.getTransport ();
        return transport != null && transport.isWritingArrangerAutomation ();
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


    protected void sendOSC (final String command)
    {
        this.sender.processNoArg (this.getProcessor (), command);
    }


    protected void sendOSC (final String command, final int value)
    {
        this.sender.processIntArg (this.getProcessor (), command, value);
    }


    protected void sendOSC (final String command, final double value)
    {
        this.sender.processDoubleArg (this.getProcessor (), command, value);
    }


    protected void sendOSC (final String command, final boolean value)
    {
        this.sender.processBooleanArg (this.getProcessor (), command, value);
    }


    protected void sendOSC (final String command, final String value)
    {
        this.sender.processStringArg (this.getProcessor (), command, value);
    }


    protected abstract Processor getProcessor ();
}
