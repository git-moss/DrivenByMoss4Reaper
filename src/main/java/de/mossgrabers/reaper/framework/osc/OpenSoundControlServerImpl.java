// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.osc;

import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of a OSC server connection.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OpenSoundControlServerImpl implements IOpenSoundControlServer
{
    private OSCPortOut connection;


    /**
     * Constructor.
     *
     * @param connection The OSC connection
     */
    public OpenSoundControlServerImpl (final OSCPortOut connection)
    {
        this.connection = connection;
    }


    /** {@inheritDoc} */
    @Override
    public void sendMessage (final IOpenSoundControlMessage message) throws IOException
    {
        if (this.connection == null)
            return;

        final String address = message.getAddress ();
        final Object [] values = message.getValues ();
        this.connection.send (new OSCMessage (address, Arrays.asList (values)));
    }


    /** {@inheritDoc} */
    @Override
    public void sendBundle (final List<IOpenSoundControlMessage> messages) throws IOException
    {
        int pos = 0;
        OSCBundle oscBundle = new OSCBundle ();
        for (final IOpenSoundControlMessage message: messages)
        {
            final String address = message.getAddress ();
            final Object [] values = message.getValues ();
            oscBundle.addPacket (new OSCMessage (address, Arrays.asList (values)));

            pos++;
            if (pos > 1000)
            {
                pos = 0;
                this.connection.send (oscBundle);
                oscBundle = new OSCBundle ();
            }
        }
        if (!oscBundle.getPackets ().isEmpty ())
            this.connection.send (oscBundle);
    }
}
