// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.osc;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.transport.udp.OSCPortOut;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of an OSC server connection.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OpenSoundControlServerImpl implements IOpenSoundControlServer
{
    private final IHost host;
    private OSCPortOut  connection;
    private boolean     isClosed = true;


    /**
     * Constructor.
     * 
     * @param host The host
     * @param serverAddress The address of the server to connect to
     * @param serverPort The port of the server to connect to
     */
    public OpenSoundControlServerImpl (final IHost host, final String serverAddress, final int serverPort)
    {
        this.host = host;

        try
        {
            this.connection = new OSCPortOut (InetAddress.getByName (serverAddress), serverPort);
            this.isClosed = false;
        }
        catch (final IOException ex)
        {
            this.connection = null;
            host.error ("Could not connect to OSC server.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void sendMessage (final IOpenSoundControlMessage message) throws IOException
    {
        if (this.isClosed)
            return;

        try
        {
            final String address = message.getAddress ();
            final Object [] values = message.getValues ();
            this.connection.send (new OSCMessage (address, Arrays.asList (values)));
        }
        catch (final OSCSerializeException ex)
        {
            throw new IOException (ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void sendBundle (final List<IOpenSoundControlMessage> messages) throws IOException
    {
        if (this.isClosed)
            return;

        try
        {
            int pos = 0;
            OSCBundle oscBundle = new OSCBundle ();
            for (final IOpenSoundControlMessage message: messages)
            {
                final String address = message.getAddress ();
                final Object [] values = message.getValues ();
                oscBundle.addPacket (new OSCMessage (address, Arrays.asList (values)));

                pos++;
                // We cannot get the exact size of the message due to the API, so let's try to stay
                // below 64K, which is the maximum of an UDP message
                if (pos > 100)
                {
                    pos = 0;
                    this.connection.send (oscBundle);
                    oscBundle = new OSCBundle ();
                }
            }
            if (!oscBundle.getPackets ().isEmpty ())
                this.connection.send (oscBundle);
        }
        catch (final OSCSerializeException ex)
        {
            throw new IOException (ex);
        }
    }


    /**
     * Close the wrapped osc client and free resources.
     */
    public void close ()
    {
        this.isClosed = true;

        if (this.connection == null)
            return;
        try
        {
            this.connection.close ();
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not close connection to OSC server.", ex);
        }
    }
}
