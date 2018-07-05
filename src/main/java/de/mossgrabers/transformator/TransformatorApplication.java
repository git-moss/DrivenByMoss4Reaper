// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.controller.ControllerInstanceManager;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.graphics.SVGImage;
import de.mossgrabers.transformator.communication.DataModelUpdateExecutor;
import de.mossgrabers.transformator.communication.DataModelUpdater;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.midi.Midi;
import de.mossgrabers.transformator.midi.MidiConnection;
import de.mossgrabers.transformator.util.LogModel;

import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiUnavailableException;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ResourceBundle;


/**
 * Main window which provides the user interface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransformatorApplication extends Application implements MessageSender, DataModelUpdater
{
    protected final SimpleStringProperty        title             = new SimpleStringProperty ();
    protected final LogModel                    logModel          = new LogModel ();

    protected final MainConfiguration           mainConfiguration = new MainConfiguration ();

    protected Stage                             stage;
    private final ListView<IControllerInstance> controllerList    = new ListView<> ();

    private ControllerInstanceManager           instanceManager;
    private AnimationTimer                      animationTimer;
    private final DataModelUpdateExecutor       modelUpdater      = new DataModelUpdateExecutor (this);
    private IniFiles                            iniFiles          = new IniFiles ();

    private TrayIcon                            trayIcon;
    private SystemTray                          tray;


    /** {@inheritDoc} */
    @Override
    public void start (final Stage stage)
    {
        this.stage = stage;
        this.instanceManager = new ControllerInstanceManager (this.logModel, stage, this);

        // Run the application as a tray icon if supported
        if (SystemTray.isSupported ())
        {
            this.stage.initStyle (StageStyle.UTILITY);

            // Instructs JavaFX not to exit implicitly when the last application window is closed
            Platform.setImplicitExit (false);
            // Sets up the tray icon (using awt code)
            Platform.runLater (this::addAppToTray);
        }

        this.setTitle ();
        this.loadConfig ();
        final Scene scene = this.createUI ();
        this.logModel.addShutdownListener ((ChangeListener<Boolean>) (observable, oldValue, newValue) -> this.exit ());
        this.showStage (stage, scene);
        this.initUSB ();

        Platform.runLater (this::startupInfrastructure);
    }


    /**
     * Initialise USB.
     */
    protected void initUSB ()
    {
        try
        {
            final int result = LibUsb.init (null);
            if (result != LibUsb.SUCCESS)
                throw new LibUsbException ("Unable to initialize libusb.", result);

            // Print LibUsb errors and warnings
            LibUsb.setDebug (null, LibUsb.LOG_LEVEL_WARNING);

        }
        catch (final LibUsbException ex)
        {
            this.logModel.addLogMessage (ex.getLocalizedMessage ());
        }
    }


    protected Scene createUI ()
    {
        // Top pane with options

        // Top pane
        final Button refreshButton = new Button ("Refresh");
        refreshButton.setMaxWidth (Double.MAX_VALUE);
        refreshButton.setOnAction (event -> this.sendRefreshCommand ());

        // Center pane with device configuration and logging
        final Button configButton = new Button ("Configuration");
        configButton.setOnAction (event -> this.editController ());
        configButton.setMaxWidth (Double.MAX_VALUE);
        final MenuButton addButton = new MenuButton ("Add");
        this.configureAddButton (addButton);

        addButton.setMaxWidth (Double.MAX_VALUE);
        final Button removeButton = new Button ("Remove");
        removeButton.setOnAction (event -> this.removeController ());
        removeButton.setMaxWidth (Double.MAX_VALUE);
        final VBox deviceButtonContainer = new VBox (addButton, removeButton, configButton, refreshButton);
        deviceButtonContainer.getStyleClass ().add ("configurationButtons");

        this.controllerList.setMinWidth (200);
        this.controllerList.setMinHeight (200);
        this.controllerList.setMaxHeight (200);
        final BorderPane controllerConfigurationPane = new BorderPane (this.controllerList, new Label ("Controller:"), deviceButtonContainer, null, null);
        controllerConfigurationPane.getStyleClass ().add ("configuration");

        final TextArea loggingTextArea = new TextArea ();
        loggingTextArea.textProperty ().bind (this.logModel.getLogMessageProperty ());
        final Label loggingAreaLabel = new Label ("Logging:");
        this.createDefaultMenuItems (loggingTextArea);
        final BorderPane loggingPane = new BorderPane (loggingTextArea, loggingAreaLabel, null, null, null);
        loggingPane.getStyleClass ().add ("logging");

        final BorderPane root = new BorderPane (loggingPane, controllerConfigurationPane, null, null, null);
        final Scene scene = new Scene (root, javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets ().add ("css/DefaultStyles.css");
        return scene;
    }


    /** {@inheritDoc} */
    @Override
    public void stop () throws Exception
    {
        this.logModel.addLogMessage ("Shutting down...");

        this.modelUpdater.stopUpdater ();

        if (this.animationTimer != null)
        {
            this.logModel.addLogMessage ("Stopping flush timer...");
            this.animationTimer.stop ();
        }

        this.instanceManager.stopAll ();

        SVGImage.clearCache ();

        this.logModel.addLogMessage ("Storing configuration...");
        this.instanceManager.save (this.mainConfiguration);
        this.saveConfig ();

        MidiConnection.cleanupUnusedDevices ();

        this.logModel.addLogMessage ("Shutting down USB...");
        LibUsb.exit (null);

        // Hardcore! No idea which thread is hanging, could be a JavaFX bug...
        System.exit (0);
    }


    /**
     * Exits the application.
     */
    public void exit ()
    {
        this.logModel.addLogMessage ("Exiting platform...");
        Platform.exit ();
        if (this.tray != null && this.trayIcon != null)
            this.tray.remove (this.trayIcon);
    }


    protected void setTitle ()
    {
        final StringBuilder sb = new StringBuilder ("DrivenByMoss 4 Reaper");

        final Package p = Package.getPackage ("transformator");
        if (p != null)
        {
            final String implementationVersion = p.getImplementationVersion ();
            if (implementationVersion != null)
                sb.append (" v").append (implementationVersion);
        }
        this.title.set (sb.toString ());
    }


    /**
     * Configures and shows the stage.
     *
     * @param stage The stage to start
     * @param scene The scene to set
     */
    protected void showStage (final Stage stage, final Scene scene)
    {
        stage.minWidthProperty ().set (600);
        stage.minHeightProperty ().set (500);

        stage.titleProperty ().bind (this.title);

        final InputStream rs = ClassLoader.getSystemResourceAsStream ("images/AppIcon.gif");
        if (rs != null)
            stage.getIcons ().add (new Image (rs));

        stage.setScene (scene);

        if (!SystemTray.isSupported ())
            stage.show ();
    }


    /**
     * Load the settings from the config file.
     */
    protected void loadConfig ()
    {
        try
        {
            this.mainConfiguration.load ();
        }
        catch (final IOException ex)
        {
            this.logModel.addLogMessage ("Could not load main configuration: " + ex.getLocalizedMessage ());
        }

        this.mainConfiguration.restoreStagePlacement (this.stage);

        SVGImage.clearCache ();
    }


    /**
     * Save the settings from the config file.
     */
    protected void saveConfig ()
    {
        try
        {
            this.mainConfiguration.storeStagePlacement (this.stage);
            this.mainConfiguration.save ();
        }
        catch (final IOException ex)
        {
            final String message = new StringBuilder ("Could not store configuration file: ").append (ex.getLocalizedMessage ()).toString ();
            this.logModel.addLogMessage (message);
            this.message (message);
        }
    }


    /**
     * Shows a message dialog. If the message starts with a '@' the message is interpreted as a
     * identifier for a string located in the resource file.
     *
     * @param message The message to display or a resource key
     * @see ResourceBundle#getString
     */
    private void message (final String message)
    {
        final Alert alert = new Alert (AlertType.INFORMATION);
        alert.setTitle (null);
        alert.setHeaderText (null);
        alert.setContentText (message);
        alert.initOwner (this.stage);
        alert.showAndWait ();
    }


    /**
     * Start scripting engine, open osc and midi ports.
     */
    private void startupInfrastructure ()
    {
        this.startFlushTimer ();

        try
        {
            Midi.readDeviceMetadata ();
        }
        catch (final MidiUnavailableException ex)
        {
            this.logModel.addLogMessage (ex.getLocalizedMessage ());
        }

        this.instanceManager.load (this.mainConfiguration);
        final ObservableList<IControllerInstance> items = this.controllerList.getItems ();
        items.setAll (this.instanceManager.getInstances ());
        if (!items.isEmpty ())
            this.controllerList.getSelectionModel ().select (0);

        // Start the loop to read data from Reaper
        this.modelUpdater.execute ();

        this.startControllers ();
    }


    /**
     * Start all configured controllers.
     */
    private void startControllers ()
    {
        this.instanceManager.startAll ();
        this.sendRefreshCommand ();
    }


    /**
     * Restart all configured controllers.
     */
    private void restartControllers ()
    {
        this.instanceManager.stopAll ();
        this.startControllers ();
    }


    /**
     * Send a controller flush message to the script.
     */
    void flushToController ()
    {
        this.instanceManager.flushAll ();
    }


    /**
     * Starts the controller flush loop for display updates.
     */
    private void startFlushTimer ()
    {
        // Update & render loop
        this.animationTimer = new AnimationTimer ()
        {
            @Override
            public void handle (final long now)
            {
                TransformatorApplication.this.flushToController ();
            }
        };
        this.animationTimer.start ();
    }


    /**
     * Closes the given socket if it is not null.
     *
     * @param socket The socket to close
     */
    public void closeSocket (final Socket socket)
    {
        if (socket == null)
            return;
        try
        {
            socket.close ();
        }
        catch (final IOException ex)
        {
            this.logModel.addLogMessage ("Could not close socket: " + ex.getLocalizedMessage ());
        }
    }


    /**
     * Parse an incoming DAW message into all configured controllers.
     *
     * @param address The message address
     * @param argument The argument
     */
    private void handleReceiveOSC (final String address, final String argument)
    {
        if (address.contains ("inipath"))
            this.loadINIFiles (argument);
        else
            this.instanceManager.parseAll (address, argument);
    }


    /** {@inheritDoc} */
    @Override
    public void sendOSC (final String command, final Object value)
    {
        if (value == null)
            this.processNoArg (command);
        else if (value instanceof String)
            this.processStringArg (command, (String) value);
        else if (value instanceof Integer)
            this.processIntArg (command, ((Integer) value).intValue ());
        else if (value instanceof Double)
        {
            final Double doubleValue = (Double) value;
            if (value.toString ().endsWith (".0"))
                this.processIntArg (command, doubleValue.intValue ());
            else
                this.processDoubleArg (command, doubleValue.doubleValue ());
        }
        else if (value instanceof Boolean)
            this.processIntArg (command, ((Boolean) value).booleanValue () ? 1 : 0);
        else
            this.logModel.addLogMessage ("Unsupported type: " + value.getClass ().toString ());
    }


    /**
     * Retrieve data from Reaper via the DLL.
     * 
     * @param dump Resend all data, ignore cache
     * @return The data formatted in an OSC style with line separators
     */
    public native String receiveModelData (final boolean dump);


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     */
    public native void processNoArg (final String command);


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     * @param value A string value
     */
    public native void processStringArg (final String command, final String value);


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     * @param value An integer value
     */
    public native void processIntArg (final String command, final int value);


    /**
     * Call Reaper command in DLL.
     *
     * @param command The OSC path command
     * @param value A double value
     */
    public native void processDoubleArg (final String command, final double value);


    /** {@inheritDoc} */
    @Override
    public void invokeAction (final String id)
    {
        if ("slice_to_multi_sampler_track".equals (id) || "slice_to_drum_track".equals (id))
            this.invokeAction (Actions.DYNAMIC_SPLIT);
        else
            this.sendOSC ("/action_ex", id);
    }


    /** {@inheritDoc} */
    @Override
    public void invokeAction (final int id)
    {
        this.sendOSC ("/action", Integer.valueOf (id));
    }


    private void removeController ()
    {
        final int selectedIndex = this.controllerList.getSelectionModel ().getSelectedIndex ();
        if (selectedIndex < 0)
            return;
        this.controllerList.getItems ().remove (selectedIndex);
        this.instanceManager.remove (selectedIndex);
    }


    private void editController ()
    {
        final int selectedIndex = this.controllerList.getSelectionModel ().getSelectedIndex ();
        if (selectedIndex < 0)
            return;

        this.instanceManager.edit (selectedIndex);
        this.restartControllers ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDataModel (final boolean dump)
    {
        final String data = this.receiveModelData (dump);
        if (data.isEmpty ())
            return;
        for (final String command: data.split ("\n"))
        {
            final String [] split = command.split (" ");
            final String params = split.length == 1 ? null : command.substring (split[0].length () + 1);
            try
            {
                this.handleReceiveOSC (split[0], params);
            }
            catch (final IllegalArgumentException ex)
            {
                final StringWriter sw = new StringWriter ();
                ex.printStackTrace (new PrintWriter (sw));
                this.logModel.addLogMessage (sw.toString ());
            }
        }
    }


    private void sendRefreshCommand ()
    {
        this.modelUpdater.executeDump ();
    }


    private void configureAddButton (final MenuButton addButton)
    {
        final ObservableList<MenuItem> items = addButton.getItems ();
        final IControllerDefinition [] definitions = this.instanceManager.getDefinitions ();
        for (int i = 0; i < definitions.length; i++)
        {
            final MenuItem item = new MenuItem (definitions[i].toString ());
            final int index = i;
            item.setOnAction (event -> {
                if (this.instanceManager.isInstantiated (index))
                {
                    this.logModel.addLogMessage ("Only one instance of a controller type is supported!");
                    return;
                }
                final IControllerInstance inst = this.instanceManager.instantiate (index);
                this.controllerList.getItems ().add (inst);
                this.controllerList.getSelectionModel ().select (inst);
                inst.start ();
                this.sendRefreshCommand ();
            });
            items.add (item);
        }
    }


    private void createDefaultMenuItems (final TextInputControl t)
    {
        final MenuItem selectAll = new MenuItem ("Select All");
        selectAll.setOnAction (e -> t.selectAll ());
        final MenuItem copy = new MenuItem ("Copy");
        copy.setOnAction (e -> t.copy ());
        final MenuItem clear = new MenuItem ("Clear");
        clear.setOnAction (e -> this.logModel.clearLogMessage ());

        final BooleanBinding emptySelection = Bindings.createBooleanBinding ( () -> Boolean.valueOf (t.getSelection ().getLength () == 0), t.selectionProperty ());
        copy.disableProperty ().bind (emptySelection);

        t.setContextMenu (new ContextMenu (copy, clear, new SeparatorMenuItem (), selectAll));
    }


    /**
     * Load and parse all Reaper INI files, which contain information about the available devices.
     *
     * @param path The path which contains the INI files
     */
    private final void loadINIFiles (final String path)
    {
        this.logModel.addLogMessage ("Loading device INI files from " + path + " ...");
        this.iniFiles.init (path, this.logModel);
        DeviceManager.get ().parseINIFiles (this.iniFiles, this.logModel);
    }


    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray ()
    {
        // set up a system tray icon.
        this.tray = SystemTray.getSystemTray ();
        final InputStream rs = ClassLoader.getSystemResourceAsStream ("images/AppIcon.gif");
        if (rs == null)
            return;

        try
        {
            final java.awt.Image image = ImageIO.read (rs);
            this.trayIcon = new TrayIcon (image);
            this.trayIcon.setImageAutoSize (true);

            // If the user double-clicks on the tray icon, show the main app stage.
            this.trayIcon.addActionListener (event -> Platform.runLater (this::showStage));

            // If the user selects the default menu item (which includes the app name),
            // show the main app stage.
            final java.awt.MenuItem openItem = new java.awt.MenuItem ("Open");
            openItem.addActionListener (event -> Platform.runLater (this::showStage));

            // The convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            final java.awt.Font defaultFont = java.awt.Font.decode (null);
            openItem.setFont (defaultFont.deriveFont (java.awt.Font.BOLD));

            // To really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            final java.awt.MenuItem exitItem = new java.awt.MenuItem ("Exit");
            exitItem.addActionListener (event -> this.exit ());

            // Setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu ();
            popup.add (openItem);
            popup.addSeparator ();
            popup.add (exitItem);
            this.trayIcon.setPopupMenu (popup);

            // Add the application tray icon to the system tray.
            this.tray.add (this.trayIcon);
        }
        catch (final AWTException | IOException ex)
        {
            this.logModel.addLogMessage ("Unable to init system tray");
        }
    }


    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage ()
    {
        if (this.stage == null)
            return;
        this.stage.show ();
        this.stage.toFront ();
    }
}
