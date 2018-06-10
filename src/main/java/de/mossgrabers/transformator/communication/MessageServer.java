package de.mossgrabers.transformator.communication;

import de.mossgrabers.transformator.util.LogModel;

import com.illposed.osc.OSCMessage;

import javafx.beans.property.SimpleBooleanProperty;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A TCP server for receiving additional OSC messages from Reaper. This does not implement the OSC
 * protocol instead simple strings are sent who "look like" an OSC message.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MessageServer
{
    private static final int            SEND_QUEUE_LIMIT  = 1000;

    private final LogModel              model;
    private final String                name;
    private final MessageHandler        oscHandler;
    private Socket                      socket;
    private ServerSocket                server;
    private SocketServer                socketServer;
    private PrintWriter                 out;

    private final Object                socketLock        = new Object ();
    private final AtomicBoolean         stopSenderSignal  = new AtomicBoolean (false);

    private final SimpleBooleanProperty isClientConnected = new SimpleBooleanProperty (false);
    private final SimpleBooleanProperty isServerRunning   = new SimpleBooleanProperty (false);

    final List<String>                  sendQueue         = Collections.synchronizedList (new LinkedList<String> ()
                                                          {
                                                              private static final long serialVersionUID = -474936912885881812L;


                                                              /** {@inheritDoc} */
                                                              @Override
                                                              public boolean add (final String message)
                                                              {
                                                                  // Limit the queue to a maximum to
                                                                  // prevent out of memory
                                                                  if (this.size () > SEND_QUEUE_LIMIT)
                                                                      this.remove (0);
                                                                  return super.add (message);
                                                              }
                                                          });


    /**
     * Constructor.
     *
     * @param name A name for the server
     * @param model Where to log errors and info comments
     * @param oscHandler Where to send received OSC messages
     */
    public MessageServer (final String name, final LogModel model, final MessageHandler oscHandler)
    {
        this.name = name;
        this.model = model;
        this.oscHandler = oscHandler;
    }


    /**
     * Start the server and bind to the given IP and port.
     *
     * @param ip The IP to bind to; set to 0.0.0.0 for local bind
     * @param port The port to bind to
     */
    public void connect (final String ip, final int port)
    {
        this.stop ();

        try
        {
            final InetAddress bindAddr = ip == null ? null : InetAddress.getByName (ip);
            this.model.addLogMessage ("Starting " + this.name + " at port: " + port);

            synchronized (this.socketLock)
            {
                this.server = new ServerSocket (port, 1, bindAddr);
                this.socketServer = SocketServer.handle (this, this.server);
                this.isServerRunning.set (true);
            }

            this.startSender ();
        }
        catch (final IOException ex)
        {
            this.model.addLogMessage ("Could not start " + this.name + ": " + ex.getLocalizedMessage ());
            this.isServerRunning.set (false);
            return;
        }
    }


    /**
     * Start the thread who asynchronously sends the collected OSC messages to Reaper.
     */
    private void startSender ()
    {
        this.stopSenderSignal.set (false);

        new Thread ( () -> {
            while (true)
            {
                this.internalSendMessages ();

                if (this.stopSenderSignal.get ())
                    break;

                try
                {
                    Thread.sleep (100);
                }
                catch (final InterruptedException ex)
                {
                    continue;
                }
            }
        }).start ();
    }


    /**
     * Stop the sender thread.
     */
    private void stopSender ()
    {
        this.stopSenderSignal.set (true);
    }


    /**
     * Close all clients.
     */
    public void stop ()
    {
        synchronized (this.socketLock)
        {
            if (this.server == null)
                return;

            this.stopSender ();
            this.socketServer.setDisconnected (true);
            this.socketServer.setStop (true);
            this.server = null;
            this.isServerRunning.set (false);
        }
        this.model.addLogMessage ("Stopped " + this.name + '.');
    }


    /**
     * Send a message to the current client.
     *
     * @param message The message to send
     */
    public void sendMessage (final String message)
    {
        this.sendQueue.add (message);
    }


    /**
     * Send all collected OSC messages if a client is currently connected.
     */
    void internalSendMessages ()
    {
        synchronized (this.socketLock)
        {
            if (this.socket == null)
            {
                // TCP socket not yet available. No client connected.
                return;
            }

            if (this.sendQueue.isEmpty ())
                return;

            try
            {
                if (this.out == null)
                    this.out = new PrintWriter (new BufferedWriter (new OutputStreamWriter (this.socket.getOutputStream ())), true);
                while (!this.sendQueue.isEmpty ())
                {
                    final String message = this.sendQueue.remove (0);
                    this.out.print (message + '\n');
                }
                this.out.flush ();
            }
            catch (final IOException ex)
            {
                this.model.addLogMessage ("Could not send message via " + this.name + ": " + ex.getLocalizedMessage ());
                this.socketServer.setDisconnected (true);
            }
        }
    }


    /**
     * Log a message
     *
     * @param message The message to log
     */
    public void log (final String message)
    {
        this.model.addLogMessage (message);
    }


    /**
     * Store the current socket of the connected client.
     *
     * @param socket A socket
     */
    void setClientSocket (final Socket socket)
    {
        synchronized (this.socketLock)
        {
            if (socket == null)
            {
                this.out = null;
                this.socket = null;
            }
            else
                this.socket = socket;
            this.isClientConnected.set (this.socket != null);
        }
    }


    /**
     * Handle a received OSC command. Splits up multiple commands and sends them to the registered
     * handler.
     *
     * @param line The line of commands to parse
     */
    public void handleCommand (final String line)
    {
        for (final String command: line.split ("\n"))
        {
            final String [] split = command.split (" ");
            final String params = split.length == 1 ? null : command.substring (split[0].length () + 1);
            try
            {
                this.oscHandler.handle (new OSCMessage (split[0], Collections.singleton (params)));
            }
            catch (final IllegalArgumentException ex)
            {
                final StringWriter sw = new StringWriter ();
                ex.printStackTrace (new PrintWriter (sw));
                this.log (sw.toString ());
            }
        }
    }


    /**
     * Get the property, which notifies if the client is connected.
     *
     * @return The property
     */
    public SimpleBooleanProperty getIsClientConnectedProperty ()
    {
        return this.isClientConnected;
    }


    /**
     * Get the property, which notifies if the server is running.
     *
     * @return The property
     */
    public SimpleBooleanProperty getIsServerRunningProperty ()
    {
        return this.isServerRunning;
    }
}
