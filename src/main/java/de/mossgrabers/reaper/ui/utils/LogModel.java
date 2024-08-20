package de.mossgrabers.reaper.ui.utils;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Contains the data for the display content.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author Jürgen Moßgraber
 */
public class LogModel
{
    private Style               normalStyle;
    private Style               errorStyle;

    private final Object        updateLock = new Object ();
    private JTextPane           textPane;
    private final StringBuilder buffer     = new StringBuilder ();


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
     * @param loggingTextPane Where to output the logging messages
     */
    public void setTextArea (final JTextPane loggingTextPane)
    {
        synchronized (this.updateLock)
        {
            this.textPane = loggingTextPane;

            this.normalStyle = this.textPane.addStyle ("Normal Style", null);
            StyleConstants.setForeground (this.normalStyle, Color.BLACK);
            this.errorStyle = this.textPane.addStyle ("Error Style", null);
            StyleConstants.setForeground (this.errorStyle, Color.RED);

            if (this.buffer.length () > 0)
                this.info ("");
        }
    }


    /**
     * Adds a logging message.
     *
     * @param message The message to add
     * @param exception The exception to log
     */
    public void error (final String message, final Throwable exception)
    {
        this.log (message, true);
        if (exception == null)
            return;
        final StringWriter writer = new StringWriter ();
        exception.printStackTrace (new PrintWriter (writer));
        this.log (writer.toString (), true);
    }


    /**
     * Adds a logging message.
     *
     * @param message The message to add
     */
    public void info (final String message)
    {
        this.log (message, false);
    }


    /**
     * Adds a logging message.
     *
     * @param message The message to add
     * @param isError True if an error is logged
     */
    public void log (final String message, final boolean isError)
    {
        if (message.length () == 0 && this.buffer.isEmpty ())
            return;

        SafeRunLater.execute (null, () -> {

            synchronized (this.updateLock)
            {
                if (message.length () > 0)
                    this.buffer.append (message).append ("\n");

                if (this.textPane != null)
                {
                    try
                    {
                        final StyledDocument doc = this.textPane.getStyledDocument ();
                        doc.insertString (doc.getLength (), this.buffer.toString (), isError ? this.errorStyle : this.normalStyle);
                    }
                    catch (final BadLocationException ex)
                    {
                        // Ignore since we cannot do anything meaningful with this
                    }

                    this.buffer.setLength (0);
                    this.textPane.setCaretPosition (this.textPane.getDocument ().getLength ());
                }
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
            if (this.textPane == null)
                this.buffer.setLength (0);
            else
                this.textPane.setText ("");
        }
    }
}
