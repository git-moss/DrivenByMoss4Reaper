package de.mossgrabers.reaper.framework.daw;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Toolkit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Can display a notification window for a certain amount of time.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NotificationWindow
{
    /** Display the message for about 4 seconds. */
    private static final int               TIMEOUT  = 4;

    private final Frame                    frame    = new Frame ();
    private final Label                    label    = new Label ();
    private final AtomicInteger            counter  = new AtomicInteger ();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ();


    /**
     * Constructor. Starts the count down timer.
     */
    public NotificationWindow ()
    {
        this.frame.add (this.label, BorderLayout.CENTER);
        this.frame.setUndecorated (true);
        this.label.setAlignment (Label.CENTER);

        this.executor.scheduleAtFixedRate ( () -> {
            if (this.counter.get () > 0 && this.counter.decrementAndGet () == 0)
                this.frame.setVisible (false);
        }, 1, 1, TimeUnit.SECONDS);
    }


    /**
     * Shutdown the count down process.
     */
    public void shutdown ()
    {
        this.executor.shutdown ();
    }


    /**
     * Displays the notification window and resets the count down to hide it.
     *
     * @param message The message to display
     */
    public void displayMessage (final String message)
    {
        this.counter.set (TIMEOUT);
        this.label.setText (message);
        if (!this.frame.isVisible ())
        {
            this.centerFrame ();
            this.frame.setVisible (true);
        }
    }


    /**
     * Center the notification window on the screen.
     */
    private void centerFrame ()
    {
        final int width = 600;
        final int height = 100;

        this.frame.setSize (width, height);
        final Dimension dim = Toolkit.getDefaultToolkit ().getScreenSize ();
        final int x = (dim.width - width) / 2;
        final int y = (dim.height - height) / 2;
        this.frame.setLocation (x, y);
    }
}
