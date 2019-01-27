// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.dialog;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;


/**
 * An abstract dialog which has some common basic functionality. Every subclass must call
 * basicInit() in its constructor!<br/>
 * Additional features:
 * <ul>
 * <li>Centers the dialog relative to the main window</li>
 * <li>Pressing the "DELETE" key closes or hides the dialog</li>
 * <li>Pressing "ENTER" in a dialog when the focus is in a JTextField activates the default
 * key.</li>
 * </ul>
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BasicDialog extends JDialog
{
    private static final long serialVersionUID = -1982748939630328892L;

    protected JFrame          frame;
    protected boolean         confirmed;
    protected JButton         ok;
    protected JButton         cancel;


    /**
     * Constructor.
     *
     * @param frame Frame to which this dialog belongs
     * @param title The title of the dialog
     * @param isModal Should the dialog be modal ?
     * @param disposeOnClose Should the dialog be hidden or disposed on close
     */
    public BasicDialog (final JFrame frame, final String title, final boolean isModal, final boolean disposeOnClose)
    {
        super (frame, title, isModal);
        this.frame = frame;
        this.setDefaultCloseOperation (disposeOnClose ? WindowConstants.DISPOSE_ON_CLOSE : WindowConstants.HIDE_ON_CLOSE);
    }


    /**
     * Sets the ok button.
     *
     * @param ok The OK button
     */
    public void setButtons (final JButton ok)
    {
        this.setButtons (ok, null);
    }


    /**
     * Sets the ok and cancel button.
     *
     * @param ok The OK button
     * @param cancel The cancel button
     */
    public void setButtons (final JButton ok, final JButton cancel)
    {
        if (ok != null)
        {
            this.ok = ok;
            ok.addActionListener (new OkButtonActionListener ());
            this.getRootPane ().setDefaultButton (ok);
        }

        if (cancel != null)
        {
            this.cancel = cancel;
            cancel.addActionListener (new CancelButtonActionListener ());
            if (ok == null)
                this.getRootPane ().setDefaultButton (ok);
        }
    }


    /**
     * Show the wait cursor
     *
     * @param busy True to show busy cursor
     */
    public void setBusy (final boolean busy)
    {
        this.setCursor (Cursor.getPredefinedCursor (busy ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
    }


    /**
     * Returns true if the ok button was clicked to exit the dialog.
     *
     * @return True if the ok button was clicked to exit the dialog.
     */
    public boolean isConfirmed ()
    {
        return this.confirmed;
    }


    /**
     * Start the initialisation for the dialog. Every subclass must call this function in the
     * constructor!
     */
    protected void basicInit ()
    {
        this.frame.setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));

        try
        {
            this.setEnabled (true);

            this.setContentPane (this.init ());

            this.getRootPane ().registerKeyboardAction (event -> this.cancel (), KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            this.set ();

            this.pack ();
            this.setLocationRelativeTo (this.frame);
        }
        catch (final Exception ex)
        {
            ex.printStackTrace ();
        }

        this.frame.setCursor (Cursor.getPredefinedCursor (Cursor.DEFAULT_CURSOR));
    }


    /**
     * Overwrite this function to create and add the widgets of the dialog.
     *
     * @return The panel that should be set as the content-pane
     * @exception java.lang.Exception Oops, something crashed...
     */
    protected abstract Container init () throws Exception;


    /**
     * Overwrite this function to set the widgets of the dialog to the correct values.
     *
     * @exception java.lang.Exception Oops, something crashed...
     */
    protected void set () throws Exception
    {
        // Intentionally empty
    }


    /**
     * Overwrite this function to read the data from the widgets.
     *
     * @return If true the dialog is closed
     */
    protected boolean onOk ()
    {
        return true;
    }


    /**
     * Overwrite this function to do additional things if dialog is aborted.
     *
     * @return If true the dialog is closed
     */
    protected boolean onCancel ()
    {
        return true;
    }


    /**
     * Executes the defaultCloseOperation if onOk returns true.
     */
    public void ok ()
    {
        this.setBusy (true);
        this.confirmed = true;
        this.processWindowEvent (this.createClosingEvent ());
        this.setBusy (false);
    }


    /**
     * Executes the defaultCloseOperation if onCancel returns true.
     */
    public void cancel ()
    {
        this.setBusy (true);
        this.confirmed = false;
        this.processWindowEvent (this.createClosingEvent ());
        this.setBusy (false);
    }


    /**
     * Executes the defaultCloseOperation if onCancel/onOk returns true.
     */
    @Override
    protected void processWindowEvent (final WindowEvent event)
    {
        switch (event.getID ())
        {
            case WindowEvent.WINDOW_CLOSING:
                if (this.confirmed)
                {
                    if (!this.onOk ())
                        return;
                }
                else
                {
                    if (!this.onCancel ())
                        return;
                }
                break;

            case WindowEvent.WINDOW_OPENED:
                this.setFocusOn ();
                break;

            default:
                // Not used
                break;
        }

        super.processWindowEvent (event);
    }


    /**
     * Can be overwritten to set the focus on a widget. Sets the focus on the ok button (if set) as
     * a default.
     */
    protected void setFocusOn ()
    {
        if (this.ok != null)
            this.ok.requestFocus ();
    }


    /**
     * Creates a closing event for this dialog.
     *
     * @return The event
     */
    private WindowEvent createClosingEvent ()
    {
        return new WindowEvent (this, WindowEvent.WINDOW_CLOSING);
    }

    /**
     * Calls the ok () function.
     */
    class OkButtonActionListener implements ActionListener
    {
        @Override
        public void actionPerformed (final ActionEvent e)
        {
            BasicDialog.this.ok ();
        }
    }

    /**
     * Calls the cancel () function.
     */
    class CancelButtonActionListener implements ActionListener
    {
        @Override
        public void actionPerformed (final ActionEvent e)
        {
            BasicDialog.this.cancel ();
        }
    }
}
