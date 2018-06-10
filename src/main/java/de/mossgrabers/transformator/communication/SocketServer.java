package de.mossgrabers.transformator.communication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Encapsulate a tcp server with 1 client.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SocketServer extends Thread
{
    private static SocketServer socketServer = null;
    private Socket              socket       = null;
    private ServerSocket        serverSocket = null;
    private MessageServer       tcpServer;
    private BufferedInputStream in;
    private boolean             disonnected  = false;
    private boolean             stop         = false;


    /**
     * Constructor.
     *
     * @param tcpServer The wrapping tcp server instance for feedback
     * @param socket The server socket
     */
    private SocketServer (final MessageServer tcpServer, final ServerSocket socket)
    {
        super ("SocketServer");
        this.tcpServer = tcpServer;
        this.serverSocket = socket;
        this.setStop (false);
        this.setDisconnected (false);
        this.start ();
    }


    /**
     * Disconnect client.
     *
     * @param doDisconnect Disconnects if true
     */
    public synchronized void setDisconnected (final boolean doDisconnect)
    {
        if (this.socket != null && doDisconnect)
        {
            try
            {
                this.socket.close ();
            }
            catch (final IOException ex)
            {
                this.tcpServer.log ("Error closing client: " + ex.getLocalizedMessage ());
            }
        }
        this.disonnected = doDisconnect;
    }


    /**
     * Stop server.
     *
     * @param doStop Stops the server if true
     */
    public synchronized void setStop (final boolean doStop)
    {
        this.stop = doStop;
        if (this.serverSocket != null && doStop)
        {
            try
            {
                this.serverSocket.close ();
            }
            catch (final IOException ex)
            {
                this.tcpServer.log ("Error closing server: " + ex.getLocalizedMessage ());
            }
        }
    }


    /**
     * Creates a singleton instance of the socket server.
     *
     * @param tcpServer Where to log messages to and send the received data
     * @param serverSocket The socket of the server
     * @return The instance
     */
    public static synchronized SocketServer handle (final MessageServer tcpServer, final ServerSocket serverSocket)
    {
        if (socketServer == null)
            socketServer = new SocketServer (tcpServer, serverSocket);
        else
        {
            if (socketServer.serverSocket != null)
            {
                try
                {
                    socketServer.setDisconnected (true);
                    socketServer.setStop (true);
                    if (socketServer.socket != null)
                        socketServer.socket.close ();
                    if (socketServer.serverSocket != null)
                        socketServer.serverSocket.close ();
                }
                catch (final IOException ex)
                {
                    tcpServer.log (ex.getLocalizedMessage ());
                }
            }
            socketServer.serverSocket = null;
            socketServer.socket = null;
            socketServer = new SocketServer (tcpServer, serverSocket);
        }
        return socketServer;
    }


    /** {@inheritDoc} */
    @Override
    public void run ()
    {
        while (!this.stop)
        {
            try
            {
                this.socket = this.serverSocket.accept ();
            }
            catch (final Exception ex)
            {
                if (!this.stop)
                {
                    this.tcpServer.log ("Error acception connection: " + ex.getLocalizedMessage ());
                    this.stop = true;
                }
                continue;
            }
            this.startServer ();
            if (this.socket != null)
            {
                try
                {
                    this.socket.close ();
                }
                catch (final IOException ex)
                {
                    this.tcpServer.log ("Error closing client socket: " + ex.getLocalizedMessage ());
                }
                this.socket = null;
                this.tcpServer.setClientSocket (this.socket);
            }
        }
    }


    /**
     * Start the TCP server receiption loop.
     */
    private void startServer ()
    {
        this.tcpServer.setClientSocket (this.socket);
        InputStream is = null;
        // Signaled via isConnected property
        // this.tcpServer.log ("New Client: " + this.socket.getInetAddress ().getHostAddress ());

        try
        {
            is = this.socket.getInputStream ();
            this.in = new BufferedInputStream (is);
        }
        catch (final IOException ex)
        {
            this.tcpServer.log ("Cound't open input stream on client " + ex.getLocalizedMessage ());
            this.setDisconnected (true);
            return;
        }

        while (true)
        {
            final String rec;
            try
            {
                rec = readInputStream (this.in);
            }
            catch (final IOException ex)
            {
                this.setDisconnected (true);
                if (this.disonnected)
                {
                    this.tcpServer.log ("Server closed client connection: " + ex.getLocalizedMessage ());
                }
                else
                    this.tcpServer.log ("Lost client connection: " + ex.getLocalizedMessage ());
                break;
            }

            if (rec == null)
            {
                this.setDisconnected (true);
                // Signaled via isConnected property
                // this.tcpServer.log ("Client closed connection.");
                break;
            }

            this.tcpServer.handleCommand (rec);
        }
    }


    /**
     * Read the available data from the input stream as a string.
     *
     * @param inputStream THe stream to read from
     * @return The read string
     * @throws IOException Error reading
     */
    private static String readInputStream (final BufferedInputStream inputStream) throws IOException
    {
        final int s = inputStream.read ();
        if (s == -1)
            return null;
        final int len = inputStream.available ();
        final StringBuilder data = new StringBuilder (len + 1).append ((char) s);
        if (len > 0)
        {
            final byte [] byteData = new byte [len];
            inputStream.read (byteData);
            data.append (new String (byteData));
        }
        return data.toString ();
    }
}
