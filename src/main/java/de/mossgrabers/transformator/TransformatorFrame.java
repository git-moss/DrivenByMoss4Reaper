package de.mossgrabers.transformator;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public class TransformatorFrame extends JFrame
{
    private JLabel swingLabel;
    private String iniPath;


    public TransformatorFrame (String iniPath)
    {
        // Setup Swing-Fenster, -Button und -Label
        super ("Swing Frame");

        this.iniPath = iniPath;

        JFXPanel jfxPanel = new JFXPanel ();

        this.add (jfxPanel);

        this.setVisible (true);

        Platform.runLater ( () -> setupJavaFXScene (jfxPanel));
    }


    private void setupJavaFXScene (JFXPanel jfxPanel)
    {
        Button btJavaFX = new Button ("JavaFX Button");

        TransformatorApplication app = new TransformatorApplication ();
        Scene scene = app.start (this, this.iniPath);

        jfxPanel.setScene (scene);

        SwingUtilities.invokeLater (this::showSwingWindow);
    }


    private void showSwingWindow ()
    {
        pack ();
        setVisible (true);
    }

    // public static void main (String [] args)
    // {
    // SwingUtilities.invokeLater (TransformatorFrame::new);
    // }
}