package de.mossgrabers.transformator.util;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;


/**
 * Contains the data for the display content.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LogModel
{
    private final SimpleStringProperty  logMessage     = new SimpleStringProperty ();
    private final SimpleBooleanProperty shutdownSignal = new SimpleBooleanProperty ();


    /**
     * Add a listener for a shutdown request.
     *
     * @param listener A listener
     */
    public void addShutdownListener (final ChangeListener<? super Boolean> listener)
    {
        this.shutdownSignal.addListener (listener);
    }


    /**
     * Get the log text message property.
     *
     * @return The property
     */
    public SimpleStringProperty getLogMessageProperty ()
    {
        return this.logMessage;
    }


    /**
     * Adds a logging message.
     *
     * @param message The message to add
     */
    public synchronized void addLogMessage (final String message)
    {
        SafeRunLater.execute ( () -> {
            final String text = this.logMessage.get ();
            final StringBuilder sb = new StringBuilder ();
            if (text != null)
                sb.append (text);
            String msg = sb.append (message).append ('\n').toString ();
            this.logMessage.set (msg);
            System.out.println (message);
        });
    }


    /**
     * Clear the messages.
     */
    public synchronized void clearLogMessage ()
    {
        this.logMessage.set ("");
    }


    /**
     * Signal shutdown.
     */
    public void shutdown ()
    {
        this.shutdownSignal.set (true);
    }
}
