package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.transformator.util.SafeRunLater;

import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private static final int               TIMEOUT      = 2;
    private static final double            FRAME_WIDTH  = 600;
    private static final double            FRAME_HEIGHT = 100;

    private final AtomicInteger            counter      = new AtomicInteger ();
    private final ScheduledExecutorService executor     = Executors.newSingleThreadScheduledExecutor ();

    private final Stage                    popupStage   = new Stage ();
    private final Text                     label        = new Text (0, 0, "");


    /**
     * Constructor. Starts the count down timer.
     */
    public NotificationWindow ()
    {
        this.popupStage.initModality (Modality.NONE);
        this.popupStage.setTitle ("Title of popup");
        this.popupStage.setAlwaysOnTop (true);
        this.popupStage.initStyle (StageStyle.UNDECORATED);

        final BorderPane root = new BorderPane (this.label);
        final Scene scene = new Scene (root, FRAME_WIDTH, FRAME_HEIGHT);
        this.popupStage.setScene (scene);
        this.popupStage.sizeToScene ();
        this.popupStage.setResizable (false);
        this.popupStage.centerOnScreen ();

        this.executor.scheduleAtFixedRate ( () -> {
            final int c = this.counter.get ();
            if (c <= 0)
                return;
            if (this.counter.decrementAndGet () == 0)
            {
                // Needs to be run on the JavaFX tread
                SafeRunLater.execute (this.popupStage::hide);
            }
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
        this.fitFontSize ();

        if (!this.popupStage.isShowing ())
        {
            this.popupStage.show ();
        }
    }


    /**
     * Fit the font size to the current label text.
     */
    private void fitFontSize ()
    {
        final Bounds bounds = this.label.getBoundsInLocal ();
        final double scale = Math.min (FRAME_WIDTH / bounds.getWidth (), FRAME_HEIGHT / bounds.getHeight ());
        this.label.setScaleX (scale);
        this.label.setScaleY (scale);
    }
}
