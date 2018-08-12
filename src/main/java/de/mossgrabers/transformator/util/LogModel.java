package de.mossgrabers.transformator.util;

import javax.swing.JTextArea;


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
     */
    public synchronized void addLogMessage (final String message)
    {
        SafeRunLater.execute ( () -> {

            this.logMessage.append (message);
            this.logMessage.append ("\n");
            System.out.println (message);
        });
    }


    /**
     * Clear the messages.
     */
    public synchronized void clearLogMessage ()
    {
        this.logMessage.setText ("");
    }
}
