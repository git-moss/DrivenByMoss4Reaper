// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.AppCallback;
import de.mossgrabers.reaper.controller.ControllerInstanceManager;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.ui.dialog.BrowserDialog;
import de.mossgrabers.reaper.ui.dialog.DebugDialog;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;
import de.mossgrabers.reaper.ui.widget.CheckboxListRenderer;
import de.mossgrabers.reaper.ui.widget.ControllerCheckboxListItem;
import de.mossgrabers.reaper.ui.widget.Functions;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


/**
 * The user interface of the extension.
 *
 * @author Jürgen Moßgraber
 */
public class MainFrame extends JFrame
{
    private static final long                                  serialVersionUID = 4251131641194938848L;
    private static final int                                   GAP              = 14;

    private final transient AppCallback                        callback;
    private final JTextPane                                    loggingTextArea  = new JTextPane ();

    private final JButton                                      removeButton;
    private final JButton                                      configButton;
    private final JButton                                      enableButton;
    private final JButton                                      detectButton;
    private final JButton                                      addButton;
    private final JButton                                      projectButton;
    private final JButton                                      parameterButton;
    private final JButton                                      debugButton;

    private final DefaultListModel<ControllerCheckboxListItem> listModel        = new DefaultListModel<> ();
    private final JList<ControllerCheckboxListItem>            controllerList   = new JList<> (this.listModel);

    private final DebugDialog                                  debugDialog;
    private final BrowserDialog                                browserDialog;
    private final LogModel                                     logModel;
    private ControllerInstanceManager                          instanceManager;


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
        this.logModel = logModel;

        final Class<? extends MainFrame> clazz = this.getClass ();
        final URL resource = clazz.getResource ("/images/AppIcon.gif");
        final Toolkit toolkit = Toolkit.getDefaultToolkit ();
        final Image image = toolkit.getImage (resource);
        if (image != null)
            this.setIconImage (image);

        this.setTitle ();

        this.debugDialog = new DebugDialog (this, callback);
        this.browserDialog = new BrowserDialog (this, this.getCallback ());

        // Top pane

        // Center pane with device configuration and logging

        // Configure button
        final ImageIcon configureIcon = Functions.getIcon ("Configure");
        this.configButton = new JButton ("Configuration");
        addIcon (this.configButton, configureIcon);
        this.configButton.addActionListener (event -> this.editController ());
        this.configButton.setToolTipText ("Open the configuration dialog of the selected controller.");

        // Add and Detect button
        final ImageIcon addIcon = Functions.getIcon ("Add");
        this.addButton = new JButton ("Add");
        addIcon (this.addButton, addIcon);
        this.addButton.setToolTipText ("Not all controllers can be detected automatically. Use the Add button and select the controller to add from the appearing menu.");
        this.configureAddButton (this.addButton, instanceManager.getDefinitions ());
        this.detectButton = new JButton ("Detect");
        addIcon (this.detectButton, addIcon);
        this.detectButton.setToolTipText ("Automatically adds connected controllers.");
        this.detectButton.addActionListener (event -> this.detectControllers ());

        // Remove button
        final ImageIcon removeIcon = Functions.getIcon ("Remove");
        this.removeButton = new JButton ("Remove");
        addIcon (this.removeButton, removeIcon);
        this.removeButton.addActionListener (event -> this.removeController ());
        this.removeButton.setToolTipText ("Removes the controller which is selected in the list.");

        // Project button
        this.projectButton = new JButton ("Project");
        addIcon (this.projectButton, configureIcon);
        this.projectButton.setToolTipText ("Configure the controller settings which are stored individually with each Reaper project, e.g. Scale settings.");
        this.projectButton.addActionListener (event -> this.projectSettings ());

        // Parameter button
        this.parameterButton = new JButton ("Parameters");
        addIcon (this.parameterButton, configureIcon);
        this.parameterButton.setToolTipText ("Arrange the parameters of the currently selected device into pages.");
        this.parameterButton.addActionListener (event -> this.parameterMapping ());

        // Disable/Enable controller button
        final ImageIcon enableIcon = Functions.getIcon ("OnOff");
        this.enableButton = new JButton ("Dis-/enable");
        addIcon (this.enableButton, enableIcon);
        this.enableButton.setToolTipText ("Disable a controller to save performance if you do not use it (or it is not connected).");
        this.enableButton.addActionListener (event -> this.toggleEnableController ());

        // Debug button
        final ImageIcon debugIcon = Functions.getIcon ("Debug");
        this.debugButton = new JButton ("Debug");
        addIcon (this.debugButton, debugIcon);
        this.configureDebugButton (this.debugButton);

        // Button panel
        final JPanel deviceButtonContainer = new JPanel ();
        deviceButtonContainer.setBorder (new EmptyBorder (0, GAP, 0, 0));
        deviceButtonContainer.setLayout (new GridLayout (8, 1, 0, GAP));
        deviceButtonContainer.add (this.detectButton);
        deviceButtonContainer.add (this.addButton);
        deviceButtonContainer.add (this.removeButton);
        deviceButtonContainer.add (this.configButton);
        deviceButtonContainer.add (this.projectButton);
        deviceButtonContainer.add (this.parameterButton);
        deviceButtonContainer.add (this.enableButton);
        deviceButtonContainer.add (this.debugButton);

        this.controllerList.setMinimumSize (new Dimension (300, 200));
        this.controllerList.setCellRenderer (new CheckboxListRenderer ());
        this.controllerList.addMouseListener (new MouseAdapter ()
        {
            @Override
            public void mouseClicked (final MouseEvent e)
            {
                if (e.getClickCount () != 2)
                    return;
                final int index = MainFrame.this.controllerList.locationToIndex (e.getPoint ());
                if (index < 0)
                    return;
                final ControllerCheckboxListItem item = MainFrame.this.listModel.getElementAt (index);
                item.setSelected (true);
                MainFrame.this.editController ();
            }
        });
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

        this.instanceManager = instanceManager;
        this.fillControllerList ();
    }


    /**
     * Populate the controller list from the instance manager.
     */
    public void fillControllerList ()
    {
        this.listModel.clear ();
        for (final IControllerInstance instance: this.instanceManager.getInstances ())
            this.listModel.addElement (new ControllerCheckboxListItem (instance));

        this.updateWidgetStates ();
    }


    /**
     * Detect connected controllers, if any.
     */
    private void detectControllers ()
    {
        final List<IControllerInstance> detectedControllers = this.getCallback ().detectControllers ();
        if (detectedControllers.isEmpty ())
            return;

        for (final IControllerInstance controllerInstance: detectedControllers)
            this.listModel.addElement (new ControllerCheckboxListItem (controllerInstance));

        this.controllerList.setSelectedValue (detectedControllers.get (0), true);
        this.updateWidgetStates ();
    }


    /**
     * Edit the currently selected controller, if any.
     */
    private void editController ()
    {
        final int selectedIndex = this.getSelectedController ();
        if (selectedIndex >= 0)
        {
            this.getCallback ().editController (selectedIndex);
            this.forceRedraw (selectedIndex);
        }
    }


    /**
     * Force a redraw of the controller list.
     */
    public void forceRedrawControllerList ()
    {
        final int selIndex = this.getSelectedController ();
        final DefaultListModel<ControllerCheckboxListItem> newListModel = new DefaultListModel<> ();

        for (int i = 0; i < this.listModel.getSize (); i++)
        {
            newListModel.addElement (this.listModel.getElementAt (i));
            this.controllerList.setModel (newListModel);
        }

        if (selIndex >= 0)
            this.controllerList.setSelectedIndex (selIndex);
    }


    /**
     * Force a redraw of the given index in the controller list.
     *
     * @param selectedIndex The index of the item to redraw
     */
    void forceRedraw (final int selectedIndex)
    {
        final ControllerCheckboxListItem item = this.listModel.getElementAt (selectedIndex);
        this.listModel.setElementAt (item, selectedIndex);
    }


    /**
     * Remove the currently selected controller, if any.
     */
    private void removeController ()
    {
        final int selectedIndex = this.getSelectedController ();
        if (selectedIndex < 0)
            return;
        this.listModel.remove (selectedIndex);
        this.getCallback ().removeController (selectedIndex);
        this.updateWidgetStates ();
    }


    /**
     * Open the project settings dialog.
     */
    public void projectSettings ()
    {
        this.getCallback ().projectSettings ();
    }


    /**
     * Open the parameter mapping dialog.
     */
    public void parameterMapping ()
    {
        final int selectedIndex = this.getSelectedController ();
        if (selectedIndex >= 0)
            this.getCallback ().parameterSettings (selectedIndex);
    }


    /**
     * Get the browser dialog.
     *
     * @return The browser dialog
     */
    public BrowserDialog getBrowserDialog ()
    {
        return this.browserDialog;
    }


    /**
     * Disable/enable the currently selected controller, if any.
     */
    private void toggleEnableController ()
    {
        final int selectedIndex = this.getSelectedController ();
        if (selectedIndex < 0)
            return;
        final ControllerCheckboxListItem item = this.listModel.getElementAt (selectedIndex);
        item.setSelected (!item.isSelected ());
        this.getCallback ().toggleEnableController (selectedIndex);
        this.forceRedraw (selectedIndex);
    }


    /**
     * Set the window title and add the version number.
     */
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


    private void createDefaultMenuItems (final JTextPane t)
    {
        final JPopupMenu popup = new JPopupMenu ();

        final JMenuItem copy = new JMenuItem ("Copy");
        copy.addActionListener (e -> t.copy ());
        popup.add (copy);
        final JMenuItem clear = new JMenuItem ("Clear");
        clear.addActionListener (e -> this.getCallback ().clearLogMessage ());
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


    private void configureDebugButton (final JButton debugButton)
    {
        final JPopupMenu popup = new JPopupMenu ();

        final JCheckBoxMenuItem popupNotificationsItem = new JCheckBoxMenuItem ("Display popup notifications");
        popupNotificationsItem.addActionListener (event -> this.getCallback ().setPopupWindowNotification (popupNotificationsItem.isSelected ()));
        popup.add (popupNotificationsItem);
        popupNotificationsItem.setSelected (this.getCallback ().getPopupWindowNotification ());

        final JMenuItem refreshMIDIPortsItem = new JMenuItem ("Refresh MIDI Ports");
        refreshMIDIPortsItem.addActionListener (event -> this.getCallback ().sendMIDIPortRefreshCommand ());
        popup.add (refreshMIDIPortsItem);

        final JMenuItem refreshItem = new JMenuItem ("Data Refresh");
        refreshItem.addActionListener (event -> this.getCallback ().sendRefreshCommand ());
        popup.add (refreshItem);

        final JMenuItem dataItem = new JMenuItem ("Data Updates");
        dataItem.addActionListener (event -> this.displayDebugDialog ());
        popup.add (dataItem);

        final JMenuItem simItem = new JMenuItem ("Device Simulator");
        simItem.addActionListener (event -> this.displaySimulatorWindow ());
        popup.add (simItem);

        final JMenuItem testItem = new JMenuItem ("Test all views/modes");
        testItem.addActionListener (event -> this.testAllControllers ());
        popup.add (testItem);

        debugButton.addActionListener (event -> popup.show (debugButton, 0, debugButton.getHeight ()));
    }


    private void displaySimulatorWindow ()
    {
        final int selectedIndex = this.getSelectedController ();
        if (selectedIndex < 0)
            return;
        final ControllerCheckboxListItem checkboxListItem = this.listModel.get (selectedIndex);
        if (checkboxListItem == null)
            return;
        final IControllerInstance controllerInstance = checkboxListItem.item ();
        if (controllerInstance != null)
            controllerInstance.simulateUI ();
    }


    private void testAllControllers ()
    {
        final int selectedIndex = this.getSelectedController ();
        if (selectedIndex < 0)
            return;
        final ControllerCheckboxListItem checkboxListItem = this.listModel.get (selectedIndex);
        if (checkboxListItem == null)
            return;
        final IControllerInstance controllerInstance = checkboxListItem.item ();
        if (controllerInstance != null)
            controllerInstance.testUI ();
    }


    private void addController (final IControllerDefinition definition)
    {
        final IControllerInstance controllerInstance = this.getCallback ().addController (definition);
        if (controllerInstance == null)
            return;
        final ControllerCheckboxListItem inst = new ControllerCheckboxListItem (controllerInstance);
        this.listModel.addElement (inst);
        this.controllerList.setSelectedValue (inst, true);
        this.updateWidgetStates ();

        // Required to redraw the icon of the list item!
        SafeRunLater.execute (this.logModel, this.controllerList::repaint);
    }


    private void displayDebugDialog ()
    {
        this.debugDialog.setVisible (true);
    }


    /**
     * Updates all widget states.
     */
    public void updateWidgetStates ()
    {
        final boolean isEmpty = this.controllerList.getModel ().getSize () == 0;
        boolean hasSelection = this.getSelectedController () != -1;
        if (!hasSelection && !isEmpty)
        {
            this.controllerList.setSelectedIndex (0);
            hasSelection = true;
        }

        final boolean fullyInitialised = this.callback.isFullyInitialised ();
        if (!fullyInitialised)
        {
            // First trigger buffered info messages
            this.logModel.info ("");
            this.logModel.error ("Close all Reaper configuration dialogs to enable the DrivenByMoss window!", null);
        }

        final boolean isEnabled = hasSelection && fullyInitialised;
        this.configButton.setEnabled (isEnabled);
        this.removeButton.setEnabled (isEnabled);
        this.enableButton.setEnabled (isEnabled);
        this.projectButton.setEnabled (isEnabled);
        this.parameterButton.setEnabled (isEnabled);

        this.detectButton.setEnabled (fullyInitialised);
        this.addButton.setEnabled (fullyInitialised);
        this.debugButton.setEnabled (fullyInitialised);
    }


    private static void addIcon (final JButton button, final ImageIcon icon)
    {
        button.setIcon (icon);
        button.setHorizontalAlignment (SwingConstants.LEFT);
        button.setIconTextGap (10);
    }


    /**
     * Get the callback.
     *
     * @return The callback
     */
    public AppCallback getCallback ()
    {
        return this.callback;
    }


    private int getSelectedController ()
    {
        return this.controllerList.getSelectedIndex ();
    }
}
