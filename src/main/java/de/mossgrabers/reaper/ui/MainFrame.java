// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.AppCallback;
import de.mossgrabers.reaper.controller.ControllerInstanceManager;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.ui.dialog.DebugDialog;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.widget.CheckboxListItem;
import de.mossgrabers.reaper.ui.widget.CheckboxListRenderer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


/**
 * The user interface of the extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MainFrame extends JFrame
{
    private static final long                        serialVersionUID = 4251131641194938848L;
    private static final int                         GAP              = 14;

    private final transient AppCallback              callback;
    private final JTextArea                          loggingTextArea  = new JTextArea ();
    private final JButton                            removeButton     = new JButton ("Remove");
    private final JButton                            configButton     = new JButton ("Configuration");
    private final DefaultListModel<CheckboxListItem> listModel        = new DefaultListModel<> ();
    private final JList<CheckboxListItem>            controllerList   = new JList<> (this.listModel);

    private DebugDialog                              debugDialog;


    /**
     * Constructor.
     *
     * @param callback The callback from the user interface
     * @param instanceManager The available controller definitions
     * @param logModel The logging model
     */
    public MainFrame (final AppCallback callback, final ControllerInstanceManager instanceManager, final LogModel logModel)
    {
        this.callback = callback;

        logModel.setTextArea (this.loggingTextArea);

        this.setType (Type.UTILITY);

        this.setTitle ();

        this.debugDialog = new DebugDialog (this, callback);

        // Top pane

        final JButton refreshButton = new JButton ("Refresh");
        refreshButton.addActionListener (event -> this.callback.sendRefreshCommand ());

        // Center pane with device configuration and logging
        this.configButton.addActionListener (event -> this.editController ());
        this.configButton.setBackground (Color.YELLOW);
        final JButton addButton = new JButton ("Add");
        addButton.setBackground (Color.GREEN);
        this.configureAddButton (addButton, instanceManager.getDefinitions ());

        final JButton detectButton = new JButton ("Detect");
        detectButton.setBackground (Color.GREEN);
        detectButton.addActionListener (event -> this.detectControllers ());

        this.removeButton.addActionListener (event -> this.removeController ());
        this.removeButton.setBackground (Color.RED);

        final JButton enableButton = new JButton ("Dis-/enable");
        enableButton.addActionListener (event -> this.toggleEnableController ());

        final JButton debugButton = new JButton ("Debug");
        debugButton.addActionListener (event -> this.displayDebugDialog ());

        final JPanel deviceButtonContainer = new JPanel ();
        deviceButtonContainer.setBorder (new EmptyBorder (0, GAP, 0, 0));
        deviceButtonContainer.setLayout (new GridLayout (7, 1, 0, GAP));

        deviceButtonContainer.add (detectButton);
        deviceButtonContainer.add (addButton);
        deviceButtonContainer.add (this.removeButton);
        deviceButtonContainer.add (this.configButton);
        deviceButtonContainer.add (enableButton);
        deviceButtonContainer.add (refreshButton);
        deviceButtonContainer.add (debugButton);

        this.controllerList.setMinimumSize (new Dimension (300, 200));
        this.controllerList.setCellRenderer (new CheckboxListRenderer ());
        final JScrollPane controllerListPane = new JScrollPane (this.controllerList);

        final JPanel controllerConfigurationPane = new JPanel (new BorderLayout ());
        controllerConfigurationPane.add (controllerListPane, BorderLayout.CENTER);
        controllerConfigurationPane.add (new JLabel ("Controller:"), BorderLayout.NORTH);
        controllerConfigurationPane.add (deviceButtonContainer, BorderLayout.EAST);

        final JScrollPane loggingTextAreaPane = new JScrollPane (this.loggingTextArea);
        final JLabel loggingAreaLabel = new JLabel ("Logging:");
        this.createDefaultMenuItems (this.loggingTextArea);
        final JPanel loggingPane = new JPanel (new BorderLayout ());
        loggingPane.setBorder (new EmptyBorder (GAP, 0, 0, 0));
        loggingPane.add (loggingTextAreaPane, BorderLayout.CENTER);
        loggingPane.add (loggingAreaLabel, BorderLayout.NORTH);

        final JPanel root = new JPanel (new BorderLayout ());
        root.setBorder (new EmptyBorder (GAP, GAP, GAP, GAP));
        root.add (loggingPane, BorderLayout.CENTER);
        root.add (controllerConfigurationPane, BorderLayout.NORTH);

        this.add (root);
        this.pack ();

        this.getRootPane ().registerKeyboardAction (e -> this.setVisible (false), KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.configureFrame (this);

        for (final IControllerInstance instance: instanceManager.getInstances ())
            this.listModel.addElement (new CheckboxListItem (instance));

        this.updateWidgetStates ();

    }


    /**
     * Detect connected controllers, if any.
     */
    private void detectControllers ()
    {
        final List<IControllerInstance> detectedControllers = this.callback.detectControllers ();
        if (detectedControllers.isEmpty ())
            return;

        for (final IControllerInstance controllerInstance: detectedControllers)
        {
            final CheckboxListItem inst = new CheckboxListItem (controllerInstance);
            this.listModel.addElement (inst);
        }

        this.controllerList.setSelectedValue (detectedControllers.get (0), true);
        this.updateWidgetStates ();

    }


    /**
     * Edit the currently selected controller, if any.
     */
    private void editController ()
    {
        final int selectedIndex = this.controllerList.getSelectionModel ().getLeadSelectionIndex ();
        if (selectedIndex >= 0)
            this.callback.editController (selectedIndex);
    }


    /**
     * Remove the currently selected controller, if any.
     */
    private void removeController ()
    {
        final int selectedIndex = this.controllerList.getSelectionModel ().getLeadSelectionIndex ();
        if (selectedIndex < 0)
            return;
        this.listModel.remove (selectedIndex);
        this.callback.removeController (selectedIndex);
        this.updateWidgetStates ();
    }


    /**
     * Dis-/enable the currently selected controller, if any.
     */
    private void toggleEnableController ()
    {
        final int selectedIndex = this.controllerList.getSelectionModel ().getLeadSelectionIndex ();
        if (selectedIndex < 0)
            return;
        final CheckboxListItem item = this.listModel.getElementAt (selectedIndex);
        item.setSelected (!item.isSelected ());
        this.callback.toggleEnableController (selectedIndex);
        // Force a redraw
        this.listModel.setElementAt (item, selectedIndex);
    }


    protected void setTitle ()
    {
        final StringBuilder sb = new StringBuilder ("DrivenByMoss 4 Reaper");
        final Package p = this.getClass ().getClassLoader ().getDefinedPackage ("de.mossgrabers.reaper");
        if (p != null)
        {
            final String implementationVersion = p.getImplementationVersion ();
            if (implementationVersion != null)
                sb.append (" v").append (implementationVersion);
        }

        this.setTitle (sb.toString ());
    }


    /**
     * Configures and shows the stage.
     *
     * @param frame The main frame
     */
    protected void configureFrame (final JFrame frame)
    {
        frame.setMinimumSize (new Dimension (840, 500));

        final URL url = ClassLoader.getSystemResource ("images/AppIcon.gif");
        if (url != null)
        {
            final ImageIcon imageIcon = new ImageIcon (url);
            this.setIconImage (imageIcon.getImage ());
        }
    }


    private void createDefaultMenuItems (final JTextArea t)
    {
        final JPopupMenu popup = new JPopupMenu ();

        final JMenuItem copy = new JMenuItem ("Copy");
        copy.addActionListener (e -> t.copy ());
        popup.add (copy);
        final JMenuItem clear = new JMenuItem ("Clear");
        clear.addActionListener (e -> this.callback.clearLogMessage ());
        popup.add (clear);

        popup.addSeparator ();

        final JMenuItem selectAll = new JMenuItem ("Select All");
        selectAll.addActionListener (e -> t.selectAll ());
        popup.add (selectAll);

        t.setComponentPopupMenu (popup);
    }


    private void configureAddButton (final JButton addButton, final Set<IControllerDefinition> definitions)
    {
        final Map<String, Map<String, JMenuItem>> menus = new TreeMap<> ();
        for (final IControllerDefinition definition: definitions)
        {
            final String vendor = definition.getHardwareVendor ();
            final Map<String, JMenuItem> menuItems = menus.computeIfAbsent (vendor, v -> new TreeMap<> ());
            final String hardwareModel = definition.getHardwareModel ();
            final JMenuItem item = new JMenuItem (hardwareModel);
            item.addActionListener (event -> this.addController (definition));
            menuItems.put (hardwareModel, item);
        }

        final JPopupMenu popup = new JPopupMenu ();
        for (final Entry<String, Map<String, JMenuItem>> menuItems: menus.entrySet ())
        {
            final JMenu menu = new JMenu (menuItems.getKey ());
            for (final JMenuItem subMenus: menuItems.getValue ().values ())
                menu.add (subMenus);
            popup.add (menu);
        }

        addButton.addActionListener (event -> popup.show (addButton, 0, addButton.getHeight ()));
    }


    private void addController (final IControllerDefinition definition)
    {
        final IControllerInstance controllerInstance = this.callback.addController (definition);
        if (controllerInstance == null)
            return;
        final CheckboxListItem inst = new CheckboxListItem (controllerInstance);
        this.listModel.addElement (inst);
        this.controllerList.setSelectedValue (inst, true);
        this.updateWidgetStates ();
    }


    private void displayDebugDialog ()
    {
        this.debugDialog.setVisible (true);
    }


    private void updateWidgetStates ()
    {
        final boolean isEmpty = this.controllerList.getModel ().getSize () == 0;
        boolean hasSelection = this.controllerList.getSelectedIndex () != -1;
        if (!hasSelection && !isEmpty)
        {
            this.controllerList.setSelectedIndex (0);
            hasSelection = true;
        }

        this.configButton.setEnabled (hasSelection);
        this.removeButton.setEnabled (hasSelection);
    }
}
