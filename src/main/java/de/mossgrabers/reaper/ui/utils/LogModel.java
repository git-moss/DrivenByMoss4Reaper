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
    private final Object  updateLock = new Object ();
    private JTextArea     logMessage;
    private StringBuilder buffer     = new StringBuilder ();


    /**
     * Constructor.
     */
    public LogModel ()
    {
        // Intentionally empty
    }


    /**
     * Set the logging text area.
     *
     * @param loggingTextArea Where to output the logging messages
     */
    public void setTextArea (final JTextArea loggingTextArea)
    {
        synchronized (this.updateLock)
        {
            this.logMessage = loggingTextArea;
            this.logMessage.append (this.buffer.toString ());
            this.buffer.setLength (0);
        }
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
        if (ex != null)
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
        SafeRunLater.execute (null, () -> {
            synchronized (this.updateLock)
            {
                if (this.logMessage == null)
                    this.buffer.append (message).append ("\n");
                else
                {
                    this.logMessage.append (message);
                    this.logMessage.append ("\n");
                }
                System.out.println (message);
            }
        });
    }


    /**
     * Clear the messages.
     */
    public void clearLogMessage ()
    {
        synchronized (this.updateLock)
        {
            if (this.logMessage == null)
                this.buffer.setLength (0);
            else
                this.logMessage.setText ("");
        }
    }
}
