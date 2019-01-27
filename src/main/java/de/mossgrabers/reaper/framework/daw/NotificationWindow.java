package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
    private static final int               TIMEOUT    = 2;

    private final AtomicInteger            counter    = new AtomicInteger ();
    private final ScheduledExecutorService executor   = Executors.newSingleThreadScheduledExecutor ();

    private final JFrame                   popupStage = new JFrame ();
    private final JLabel                   label      = new JLabel ("");


    /**
     * Constructor. Starts the count down timer.
     * 
     * @param logModel Where to log to
     */
    public NotificationWindow (final LogModel logModel)
    {
        this.popupStage.setTitle ("Notification");
        this.popupStage.setAlwaysOnTop (true);
        this.popupStage.setUndecorated (true);
        this.popupStage.setResizable (false);
        this.popupStage.setMinimumSize (new Dimension (800, 80));

        this.label.setFont (this.label.getFont ().deriveFont ((float) 40.0));

        final JPanel root = new JPanel (new BorderLayout ());
        root.setBorder (new EmptyBorder (14, 14, 14, 14));
        root.add (this.label, BorderLayout.CENTER);

        this.popupStage.setContentPane (root);

        this.executor.scheduleAtFixedRate ( () -> {
            final int c = this.counter.get ();
            if (c <= 0)
                return;
            if (this.counter.decrementAndGet () == 0)
            {
                // Needs to be run on the Swing tread
                SafeRunLater.execute (logModel, () -> this.popupStage.setVisible (false));
            }
        }, 1, 1, TimeUnit.SECONDS);
    }


    /**
     * Shutdown the count down process.
     */
    public void shutdown ()
    {
        this.executor.shutdown ();
        if (this.popupStage.isShowing ())
            this.popupStage.setVisible (false);
    }


    /**
     * Displays the notification window and resets the count down to hide it.
     *
     * @param message The message to display
     */
    public void displayMessage (final String message)
    {
        if (this.executor.isShutdown ())
            return;

        this.counter.set (TIMEOUT);

        this.label.setText (message);

        final Dimension preferredSize = this.label.getPreferredSize ();
        preferredSize.width = preferredSize.width + 28;
        preferredSize.height = preferredSize.height + 28;
        this.popupStage.setMinimumSize (preferredSize);
        this.popupStage.setMaximumSize (preferredSize);
        this.popupStage.setPreferredSize (preferredSize);
        this.popupStage.setSize (preferredSize);
        this.popupStage.validate ();
        this.popupStage.revalidate ();

        if (this.popupStage.isShowing ())
            return;

        final Dimension dim = Toolkit.getDefaultToolkit ().getScreenSize ();
        this.popupStage.setLocation (dim.width / 2 - preferredSize.width / 2, dim.height / 2 - preferredSize.height / 2);
        this.popupStage.setVisible (true);
    }
}
