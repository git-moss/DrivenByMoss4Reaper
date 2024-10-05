// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.dialog;

import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.reaper.AppCallback;
import de.mossgrabers.reaper.framework.daw.BrowserContentType;
import de.mossgrabers.reaper.framework.daw.BrowserImpl;
import de.mossgrabers.reaper.framework.device.DeviceMetadataImpl;
import de.mossgrabers.reaper.framework.device.column.BaseColumn;
import de.mossgrabers.reaper.ui.widget.BoxPanel;
import de.mossgrabers.reaper.ui.widget.Functions;
import de.mossgrabers.reaper.ui.widget.JListX;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A window to access the browser.
 *
 * @author Jürgen Moßgraber
 */
public class BrowserDialog extends BasicDialog
{
    private static final long           serialVersionUID   = -4991119574575580454L;

    private static final int            MAX_FILTER_COLUMNS = 7;

    private final transient AppCallback callback;
    private final List<JListX<String>>  filterListBox      = new ArrayList<> ();
    private final List<BoxPanel>        filterPanels       = new ArrayList<> ();
    private final List<JLabel>          filterColumnLabels = new ArrayList<> ();
    private final JLabel                infoTextLabel      = new JLabel ();
    private JListX<String>              resultListBox;
    private transient BrowserImpl       browser;
    private final transient Object      browserLock        = new Object ();
    private JMenu                       displayMenu        = null;


    /**
     * Constructor.
     *
     * @param owner The owner of the dialog
     * @param callback Callback to set/get browser settings
     */
    public BrowserDialog (final JFrame owner, final AppCallback callback)
    {
        super (owner, "Browser", false, false);

        this.callback = callback;

        // Replace the default Java logo
        final URL resource = this.getClass ().getResource ("/images/AppIcon.gif");
        final Image image = Toolkit.getDefaultToolkit ().getImage (resource);
        if (image != null)
            this.setIconImage (image);

        // Handle closing the window with the escape key
        this.getRootPane ().registerKeyboardAction (e -> this.cancel (), KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setMinimumSize (new Dimension (800, 600));
        this.basicInit ();
        this.createMenu ();
        this.pack ();
        this.setLocationRelativeTo (null);
    }


    /** {@inheritDoc} */
    @Override
    protected Container init ()
    {
        final JPanel contentPane = new JPanel (new BorderLayout ());

        // Filter and result columns
        final BoxPanel columnWidgets = new BoxPanel (BoxLayout.X_AXIS, true);
        for (int i = 0; i < MAX_FILTER_COLUMNS; i++)
        {
            final BoxPanel columnPanel = new BoxPanel (BoxLayout.Y_AXIS, false);
            this.filterPanels.add (columnPanel);
            final JListX<String> listBox = columnPanel.createListBox ("-", null, BoxPanel.NONE, Collections.emptyList ());
            listBox.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
            listBox.setPrototypeCellValue ("01234567890123456789012");
            this.filterListBox.add (listBox);
            columnWidgets.addComponent (columnPanel, BoxPanel.NORMAL);
            this.filterColumnLabels.add ((JLabel) columnPanel.getComponent (0));

            final int index = i;
            listBox.addListSelectionListener (e -> this.filterChanges (index, e));
        }
        final BoxPanel columnPanel = new BoxPanel (BoxLayout.Y_AXIS, false);
        this.resultListBox = columnPanel.createListBox ("Results:", null, BoxPanel.NONE, Collections.emptyList ());
        this.resultListBox.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        this.resultListBox.setPrototypeCellValue ("012345678901234567890123456");
        this.resultListBox.addListSelectionListener (this::filterResultChanges);
        this.resultListBox.addMouseListener (new MouseAdapter ()
        {
            /** {@inheritDoc} */
            @Override
            public void mouseClicked (final MouseEvent evt)
            {
                if (evt.getClickCount () == 2)
                    BrowserDialog.this.ok ();
            }
        });

        columnWidgets.addComponent (columnPanel, BoxPanel.NONE);

        final BoxPanel infoPane = new BoxPanel (BoxLayout.X_AXIS, true);
        infoPane.add (this.infoTextLabel);

        final Font f = this.infoTextLabel.getFont ();
        this.infoTextLabel.setFont (f.deriveFont (f.getStyle () | Font.BOLD));
        contentPane.add (infoPane, BorderLayout.NORTH);
        contentPane.add (columnWidgets, BorderLayout.CENTER);

        // Cancel and OK buttons
        final BoxPanel buttons = new BoxPanel (BoxLayout.X_AXIS, true);
        buttons.createSpace (BoxPanel.GLUE);
        final JButton cancelButton = buttons.createButton (Functions.getIcon ("Remove"), "Cancel", null, BoxPanel.NORMAL);
        final JButton okButton = buttons.createButton (Functions.getIcon ("Confirm"), "OK", null, BoxPanel.NONE);
        this.setButtons (okButton, cancelButton);
        Functions.asWidthAs (this.cancel, this.ok);

        contentPane.add (buttons, BorderLayout.SOUTH);

        return contentPane;
    }


    private void createMenu ()
    {
        final JMenuBar menuBar = new JMenuBar ();
        this.displayMenu = new JMenu ("View");
        menuBar.add (this.displayMenu);
        this.setJMenuBar (menuBar);
        for (int i = 0; i < MAX_FILTER_COLUMNS; i++)
        {
            final JMenuItem menuItem = new JCheckBoxMenuItem ("FILTER_COLUMN " + i);
            final int col = i;
            menuItem.addActionListener (e -> this.toggleFilterColumnVisibility (col));
            this.displayMenu.add (menuItem);
        }
        this.displayMenu.addSeparator ();

        final JMenuItem allMenuItem = new JMenuItem ("All");
        allMenuItem.addActionListener (e -> this.toggleFilterColumnVisibilityAll (true));
        this.displayMenu.add (allMenuItem);
        final JMenuItem noneMenuItem = new JMenuItem ("None");
        noneMenuItem.addActionListener (e -> this.toggleFilterColumnVisibilityAll (false));
        this.displayMenu.add (noneMenuItem);
    }


    private void updateMenu ()
    {
        final boolean [] browserColumnsVisibility = this.callback.getBrowserColumnsVisibility (this.browser.getContentType ());

        final int filterColumnCount = this.browser.getFilterColumnCount ();
        for (int col = 0; col < MAX_FILTER_COLUMNS; col++)
        {
            final JMenuItem menuItem = this.displayMenu.getItem (col);
            if (col < filterColumnCount)
            {
                final String name = this.browser.getFilterColumn (col).getName ();
                menuItem.setText (name.isBlank () ? "No filter" : name);
                this.setFilterColumnVisible (col, browserColumnsVisibility[col]);
                menuItem.setSelected (browserColumnsVisibility[col]);
                this.setFilterColumnVisible (col, browserColumnsVisibility[col]);
            }
            menuItem.setVisible (col < filterColumnCount);
        }
    }


    private void toggleFilterColumnVisibilityAll (final boolean all)
    {
        for (int col = 0; col < this.browser.getFilterColumnCount (); col++)
        {
            this.setFilterColumnVisible (col, all);
            this.updateColumnSettings (col, all);
            ((JCheckBoxMenuItem) this.displayMenu.getMenuComponent (col)).setSelected (all);
        }
    }


    private void toggleFilterColumnVisibility (final int column)
    {
        final BoxPanel filterCol = this.filterPanels.get (column);
        final boolean show = !filterCol.isVisible ();
        this.setFilterColumnVisible (column, show);
        this.updateColumnSettings (column, show);
    }


    private void setFilterColumnVisible (final int column, final boolean show)
    {
        final BoxPanel filterCol = this.filterPanels.get (column);
        filterCol.setVisible (show);
        filterCol.getParent ().getComponent (column * 2 + 1).setVisible (show);
    }


    protected void updateColumnSettings (final int column, final boolean show)
    {
        // Update settings
        final BrowserContentType contentType = this.browser.getContentType ();
        final boolean [] browserColumnsVisibility = this.callback.getBrowserColumnsVisibility (contentType);
        browserColumnsVisibility[column] = show;
        this.callback.setBrowserColumnsVisibility (contentType, browserColumnsVisibility);
    }


    /** {@inheritDoc} */
    @Override
    protected void processWindowEvent (final WindowEvent e)
    {
        if (e.getID () == WindowEvent.WINDOW_CLOSING)
            this.cancel ();

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
                this.close (false, false);

            this.browser = browser;

            this.infoTextLabel.setText (this.browser.getInfoText ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void ok ()
    {
        this.close (true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void cancel ()
    {
        this.close (false, true);
    }


    /**
     * Close the browser window.
     *
     * @param commit True to commit otherwise discard
     * @param notifyBrowser If true also notify the browser about the close
     */
    public void close (final boolean commit, final boolean notifyBrowser)
    {
        synchronized (this.browserLock)
        {
            this.setVisible (false);

            // Necessary to prevent endless loop
            final BrowserImpl b = this.browser;
            this.browser = null;

            if (b != null && notifyBrowser)
                b.stopBrowsing (commit);
        }
    }


    /**
     * Update the content of the results column.
     *
     * @param selectedIndex The currently selected index in the column
     */
    public void updateResults (final int selectedIndex)
    {
        synchronized (this.browserLock)
        {
            if (this.browser == null)
                return;

            this.updateFilterSelections ();

            final DefaultListModel<String> model;
            if (this.browser.isPresetContentType ())
                model = this.browser.getPresets ();
            else
            {
                model = new DefaultListModel<> ();
                for (final DeviceMetadataImpl device: this.browser.getFilteredDevices ())
                    model.addElement (device.fullName ());
            }
            this.resultListBox.setModel (model);

            this.updateResultSelection (selectedIndex);
        }
    }


    /**
     * Update the content of all filter columns.
     */
    public void updateFilters ()
    {
        synchronized (this.browserLock)
        {
            if (this.browser == null)
                return;

            this.setTitle ("Browser - " + this.browser.getSelectedContentType ());

            final int filterColumnCount = this.browser.getFilterColumnCount ();

            for (int i = 0; i < MAX_FILTER_COLUMNS; i++)
            {
                final JListX<String> list = this.filterListBox.get (i);
                final DefaultListModel<String> model = list.getModel ();
                model.clear ();

                if (i < filterColumnCount)
                {
                    final IBrowserColumn filterColumn = this.browser.getFilterColumn (i);
                    if (filterColumn instanceof final BaseColumn baseFilter)
                    {
                        model.addElement (BaseColumn.WILDCARD);
                        for (final String item: baseFilter.getAllItems ())
                            model.addElement (item);
                    }

                    final String name = filterColumn.getName ();
                    this.filterColumnLabels.get (i).setText (name.isBlank () ? "No filter:" : name + ":");
                }
            }

            this.updateMenu ();
        }
    }


    /**
     * Update the selections in the filter columns.
     */
    public void updateFilterSelections ()
    {
        synchronized (this.browserLock)
        {
            if (this.browser == null)
                return;

            final int filterColumnCount = this.browser.getFilterColumnCount ();

            for (int i = 0; i < MAX_FILTER_COLUMNS; i++)
            {
                if (i < filterColumnCount)
                {
                    final JListX<String> list = this.filterListBox.get (i);
                    list.setSelectedIndex (this.browser.getFilterColumn (i).getCursorIndex ());
                }
            }
        }
    }


    /**
     * Update the selected result.
     *
     * @param selectedIndex The index of the item to select
     */
    public void updateResultSelection (final int selectedIndex)
    {
        if (selectedIndex >= 0)
            this.resultListBox.setSelectedIndex (selectedIndex);
    }


    /**
     * A different filter item was selected.
     *
     * @param filterIndex The index of the filter column
     * @param event The change event
     */
    private void filterChanges (final int filterIndex, final ListSelectionEvent event)
    {
        if (event.getValueIsAdjusting ())
            return;

        synchronized (this.browserLock)
        {
            if (this.browser == null)
                return;

            final int selectedIndex = this.filterListBox.get (filterIndex).getSelectedIndex ();
            if (selectedIndex >= 0)
                this.browser.setSelectedFilterItemIndex (filterIndex, selectedIndex);
        }
    }


    /**
     * A different result item was selected.
     *
     * @param event The change event
     */
    private void filterResultChanges (final ListSelectionEvent event)
    {
        if (event.getValueIsAdjusting ())
            return;

        synchronized (this.browserLock)
        {
            if (this.browser == null)
                return;

            final int selectedIndex = this.resultListBox.getSelectedIndex ();
            if (selectedIndex >= 0)
                this.browser.setSelectedResult (selectedIndex);
        }
    }
}
