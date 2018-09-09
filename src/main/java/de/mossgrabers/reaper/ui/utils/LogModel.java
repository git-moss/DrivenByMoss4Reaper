package de.mossgrabers.reaper.ui.utils;

import javax.swing.JTextArea;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Contains the data for the display content.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LogModel
{
    private final JTextArea logMessage;


    /**
     * Constructor.
     *
     * @param loggingTextArea Where to output the logging messages
     */
    public LogModel (final JTextArea loggingTextArea)
    {
        this.logMessage = loggingTextArea;
    }


    /**
     * Adds a logging message.
     *
     * @param message The message to add
     * @param ex The exception to log
     */
    public void error (final String message, final Throwable ex)
    {
        this.info (message);
        final StringWriter writer = new StringWriter ();
        ex.printStackTrace (new PrintWriter (writer));
        this.info (writer.toString ());
    }


    /**
     * Adds a logging message.
     *
     * @param message The message to add
     */
    public void info (final String message)
    {
        SafeRunLater.execute ( () -> {
            synchronized (this.logMessage)
            {
                this.logMessage.append (message);
                this.logMessage.append ("\n");
                System.out.println (message);
            }
        });
    }


    /**
     * Clear the messages.
     */
    public void clearLogMessage ()
    {
        synchronized (this.logMessage)
        {
            this.logMessage.setText ("");
        }
    }
}
