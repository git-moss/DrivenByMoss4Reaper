// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.osc;

import de.mossgrabers.framework.osc.IOpenSoundControlCallback;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.transport.udp.OSCPortIn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of an OSC server connection.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OpenSoundControlServerImpl implements IOpenSoundControlServer
{
    private final IOpenSoundControlCallback callback;
    private final Object                    receiverLock = new Object ();
    private final LogModel                  logModel;
    private OSCPortIn                       oscReceiver;


    /**
     * Constructor.
     *
     * @param callback The OSC callback
     * @param logModel For logging
     */
    public OpenSoundControlServerImpl (final IOpenSoundControlCallback callback, final LogModel logModel)
    {
        this.callback = callback;
        this.logModel = logModel;
    }


    /** {@inheritDoc} */
    @Override
    public void start (final int port) throws IOException
    {
        synchronized (this.receiverLock)
        {
            this.close ();
            this.oscReceiver = new OSCPortIn (port);
            this.oscReceiver.addPacketListener (new PacketListener ());
            this.oscReceiver.startListening ();
        }
    }


    /**
     * Close the server.
     */
    public void close ()
    {
        synchronized (this.receiverLock)
        {
            if (this.oscReceiver == null)
                return;

            this.oscReceiver.stopListening ();
            try
            {
                this.oscReceiver.close ();
            }
            catch (final IOException ex)
            {
                this.logModel.error ("Could not close OSC receiver.", ex);
            }
        }
    }


    private class PacketListener implements OSCPacketListener
    {
        /** {@inheritDoc} */
        @Override
        public void handlePacket (final OSCPacketEvent event)
        {
            final List<OSCMessage> messages = new ArrayList<> ();
            this.collectMessages (messages, event.getPacket ());

            SafeRunLater.execute (OpenSoundControlServerImpl.this.logModel, () -> {
                for (final OSCMessage message: messages)
                    OpenSoundControlServerImpl.this.callback.handle (new OpenSoundControlMessageImpl (message));
            });
        }


        /** {@inheritDoc} */
        @Override
        public void handleBadData (final OSCBadDataEvent event)
        {
            OpenSoundControlServerImpl.this.logModel.error ("Could not parse message.", event.getException ());
        }


        private void collectMessages (final List<OSCMessage> messages, final OSCPacket packet)
        {
            if (packet instanceof OSCMessage)
                messages.add ((OSCMessage) packet);
            else if (packet instanceof OSCBundle)
            {
                for (final OSCPacket op: ((OSCBundle) packet).getPackets ())
                    this.collectMessages (messages, op);
            }
        }
    }
}
