// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui;

import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.reaper.framework.daw.BrowserImpl;
import de.mossgrabers.reaper.ui.dialog.BasicDialog;
import de.mossgrabers.reaper.ui.widget.BoxPanel;
import de.mossgrabers.reaper.ui.widget.JListX;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A window to access the browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserDialog extends BasicDialog
{
    private static final long          serialVersionUID   = -4991119574575580454L;

    private final List<JListX<String>> filterListBox      = new ArrayList<> ();
    private final List<BoxPanel>       filterPanels       = new ArrayList<> ();
    private final List<JLabel>         filterColumnLabels = new ArrayList<> ();
    private JListX<String>             resultListBox;
    private BrowserImpl                browser;
    private final Object               browserLock        = new Object ();


    /**
     * Constructor.
     * 
     * @param owner The owner of the dialog
     */
    public BrowserDialog (final JFrame owner)
    {
        super (owner, "Browser", false, false);

        final URL resource = this.getClass ().getResource ("/images/AppIcon.gif");
        final Image image = Toolkit.getDefaultToolkit ().getImage (resource);
        if (image != null)
            this.setIconImage (image);

        this.getRootPane ().registerKeyboardAction (e -> this.close (false), KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        final Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
        this.setMinimumSize (new Dimension (800, 600));
        this.setMaximumSize (new Dimension (screenSize.width - 100, screenSize.height - 100));

        this.basicInit ();
    }


    /** {@inheritDoc} */
    @Override
    protected Container init ()
    {
        final JPanel contentPane = new JPanel (new BorderLayout ());

        // Filter and result columns
        final BoxPanel columnWidgets = new BoxPanel (BoxLayout.X_AXIS, true);
        for (int i = 0; i < 8; i++)
        {
            final BoxPanel columnPanel = new BoxPanel (BoxLayout.Y_AXIS, true);
            this.filterPanels.add (columnPanel);
            this.filterListBox.add (columnPanel.createListBox ("-", null, BoxPanel.NORMAL, Collections.emptyList ()));
            columnWidgets.add (columnPanel);
            this.filterColumnLabels.add ((JLabel) columnPanel.getComponent (0));
        }
        final BoxPanel columnPanel = new BoxPanel (BoxLayout.Y_AXIS, false);
        this.resultListBox = columnPanel.createListBox ("Results:", null, BoxPanel.NONE, Collections.emptyList ());
        columnWidgets.add (columnPanel);

        contentPane.add (columnWidgets, BorderLayout.CENTER);

        // Cancel and OK buttons
        final BoxPanel buttons = new BoxPanel (BoxLayout.X_AXIS, true);
        buttons.createSpace (BoxPanel.GLUE);
        final JButton cancelButton = buttons.createButton ("Cancel", null, BoxPanel.NONE);
        cancelButton.addActionListener (e -> this.close (false));
        buttons.createSpace (BoxPanel.NORMAL);
        final JButton okButton = buttons.createButton ("OK", null, BoxPanel.NONE);
        okButton.addActionListener (e -> this.close (true));
        this.getRootPane ().setDefaultButton (okButton);
        contentPane.add (buttons, BorderLayout.SOUTH);

        return contentPane;
    }


    /** {@inheritDoc} */
    @Override
    protected void processWindowEvent (WindowEvent e)
    {
        if (e.getID () == WindowEvent.WINDOW_CLOSING)
            this.close (false);

        super.processWindowEvent (e);
    }


    /**
     * Open the browser window.
     *
     * @param browser The browser from which to get the data
     */
    public void open (final BrowserImpl browser)
    {
        synchronized (this.browserLock)
        {
            if (this.browser != null)
                this.close (false);

            this.browser = browser;

            final int filterColumnCount = this.browser.getFilterColumnCount ();

            for (int i = 0; i < 8; i++)
            {
                final boolean columnExists = i < filterColumnCount;
                if (columnExists)
                {
                    final JListX<String> list = this.filterListBox.get (i);
                    final DefaultListModel<String> model = list.getModel ();
                    model.clear ();

                    final IBrowserColumn filterColumn = this.browser.getFilterColumn (i);
                    for (final IBrowserColumnItem item: filterColumn.getItems ())
                    {
                        model.addElement (item.getName ());
                    }

                    this.filterColumnLabels.get (i).setText (filterColumn.getName () + ":");
                }

                this.filterPanels.get (i).setVisible (columnExists);
            }

            this.pack ();
            this.setVisible (true);
            this.toFront ();
        }
    }


    /**
     * Close the browser window.
     *
     * @param commit True to commit otherwise discard
     */
    public void close (final boolean commit)
    {
        synchronized (this.browserLock)
        {
            this.setVisible (false);

            // Necessary to prevent endless loop
            final BrowserImpl b = this.browser;
            this.browser = null;

            if (b != null)
                b.stopBrowsing (commit);
        }
    }
}
