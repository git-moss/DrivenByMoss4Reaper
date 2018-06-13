// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.reaper.controller.ControllerInstanceManager;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.reaper.framework.device.DeviceManager;
import de.mossgrabers.reaper.framework.graphics.SVGImage;
import de.mossgrabers.transformator.communication.MessageHandler;
import de.mossgrabers.transformator.communication.MessageSender;
import de.mossgrabers.transformator.communication.MessageServer;
import de.mossgrabers.transformator.midi.Midi;
import de.mossgrabers.transformator.midi.MidiConnection;
import de.mossgrabers.transformator.util.LogModel;
import de.mossgrabers.transformator.util.TextInputValidator;

import com.illposed.osc.OSCMessage;

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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiUnavailableException;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Main window which provides the user interface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransformatorApplication extends Application implements MessageHandler, MessageSender
{
    protected final SimpleStringProperty        title              = new SimpleStringProperty ();
    protected final LogModel                    logModel           = new LogModel ();

    protected final MainConfiguration           mainConfiguration  = new MainConfiguration ();

    protected Stage                             stage;
    private final TextField                     applicationCommand = new TextField ();
    private final CheckBox                      runAutomatically   = new CheckBox ("Auto-run");
    private final CheckBox                      enablePreviewBox   = new CheckBox ();
    private final TextField                     tcpPortField       = new TextField ();
    private final ListView<IControllerInstance> controllerList     = new ListView<> ();

    private ControllerInstanceManager           instanceManager;

    private TrayIcon                            trayIcon;
    private SystemTray                          tray;
    private AnimationTimer                      animationTimer;
    private final ScheduledExecutorService      executor           = Executors.newSingleThreadScheduledExecutor ();

    private final MessageServer                 oscServer          = new MessageServer ("Reaper communication server", this.logModel, this);


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

        final GridPane topPane = new GridPane ();
        topPane.getStyleClass ().addAll ("grid", "padding");

        // Top pane - row 1
        final Label dawPathLabel = new Label ("DAW Path:");
        dawPathLabel.setAlignment (Pos.CENTER_RIGHT);
        dawPathLabel.setLabelFor (this.applicationCommand);
        GridPane.setHgrow (this.applicationCommand, Priority.ALWAYS);

        this.runAutomatically.setTooltip (new Tooltip ("If the DAW path is configured correctly Reaper is automatically started. Furthermore, the app starts minimized."));
        this.runAutomatically.setOnAction (e -> this.mainConfiguration.setRunAutomatically (this.runAutomatically.isSelected ()));

        final Button selectFileButton = new Button ("...");
        selectFileButton.setMaxWidth (Double.MAX_VALUE);
        selectFileButton.setOnAction (e -> this.selectDAWExecutable ());
        final Button runButton = new Button ("Run");
        runButton.setMaxWidth (Double.MAX_VALUE);
        runButton.setOnAction (e -> this.runDAW ());

        topPane.add (dawPathLabel, 0, 0);
        topPane.add (this.applicationCommand, 1, 0);
        topPane.add (selectFileButton, 2, 0);
        topPane.add (runButton, 3, 0);
        topPane.add (this.runAutomatically, 4, 0);

        // Top pane - row 2
        final Label displayPortLabel = new Label ("Communication Port:");
        displayPortLabel.setAlignment (Pos.CENTER_RIGHT);
        displayPortLabel.setLabelFor (this.tcpPortField);
        GridPane.setHgrow (this.tcpPortField, Priority.ALWAYS);
        TextInputValidator.limitToNumbers (this.tcpPortField);
        final Button applyButton = new Button ("Apply");
        applyButton.setMaxWidth (Double.MAX_VALUE);
        applyButton.setOnAction (e -> this.applyTCPPort ());

        final Button refreshButton = new Button ("Refresh");
        refreshButton.setMaxWidth (Double.MAX_VALUE);
        refreshButton.setOnAction (event -> this.sendRefreshCommand ());
        final CheckBox tcpConnectionBox = new CheckBox ("TCP connected");
        tcpConnectionBox.setDisable (true);
        tcpConnectionBox.selectedProperty ().bind (this.oscServer.getIsClientConnectedProperty ());

        topPane.add (displayPortLabel, 0, 1);
        topPane.add (this.tcpPortField, 1, 1);
        topPane.add (applyButton, 2, 1);
        topPane.add (refreshButton, 3, 1);
        topPane.add (tcpConnectionBox, 4, 1);

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
        final VBox deviceButtonContainer = new VBox (addButton, removeButton, configButton);
        deviceButtonContainer.getStyleClass ().add ("configurationButtons");

        this.controllerList.setMinWidth (300);
        final BorderPane controllerConfigurationPane = new BorderPane (this.controllerList, new Label ("Controller:"), deviceButtonContainer, null, null);
        controllerConfigurationPane.getStyleClass ().add ("configuration");

        final TextArea loggingTextArea = new TextArea ();
        loggingTextArea.textProperty ().bind (this.logModel.getLogMessageProperty ());
        final Label loggingAreaLabel = new Label ("Logging:");
        this.createDefaultMenuItems (loggingTextArea);
        final BorderPane loggingPane = new BorderPane (loggingTextArea, loggingAreaLabel, null, null, null);
        loggingPane.getStyleClass ().add ("logging");

        final BorderPane centerPane = new BorderPane (loggingPane, null, null, null, controllerConfigurationPane);

        final BorderPane root = new BorderPane (centerPane, topPane, null, null, null);
        final Scene scene = new Scene (root, javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets ().add ("css/DefaultStyles.css");
        return scene;
    }


    /** {@inheritDoc} */
    @Override
    public void stop () throws Exception
    {
        this.logModel.addLogMessage ("Shutting down...");

        if (this.animationTimer != null)
        {
            this.logModel.addLogMessage ("Stopping flush timer...");
            this.animationTimer.stop ();
        }

        this.executor.shutdown ();

        this.instanceManager.stopAll ();

        SVGImage.clearCache ();

        this.logModel.addLogMessage ("Storing configuration...");
        this.instanceManager.save (this.mainConfiguration);
        this.saveConfig ();

        this.logModel.addLogMessage ("Stopping OSC...");
        this.oscServer.stop ();

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
        stage.minWidthProperty ().set (820);
        stage.minHeightProperty ().set (300);

        stage.titleProperty ().bind (this.title);

        final InputStream rs = ClassLoader.getSystemResourceAsStream ("images/AppIcon.gif");
        if (rs != null)
            stage.getIcons ().add (new Image (rs));

        stage.setScene (scene);

        if (!SystemTray.isSupported () || !this.mainConfiguration.isRunAutomatically ())
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

        this.enablePreviewBox.setSelected (this.mainConfiguration.isPreviewEnabled ());
        this.applicationCommand.setText (this.mainConfiguration.getApplicationCommand ());
        this.runAutomatically.setSelected (this.mainConfiguration.isRunAutomatically ());
        this.tcpPortField.setText (Integer.toString (this.mainConfiguration.getTcpPort ()));

        SVGImage.clearCache ();

        if (this.runAutomatically.isSelected ())
            this.runDAW ();
    }


    /**
     * Save the settings from the config file.
     */
    protected void saveConfig ()
    {
        try
        {
            this.mainConfiguration.setApplicationCommand (this.applicationCommand.getText ());
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
     * Select the Bitwig executable.
     */
    private void selectDAWExecutable ()
    {
        final FileChooser chooser = new FileChooser ();
        chooser.setTitle ("Select the DAW executable file");

        if (OperatingSystem.get () == OperatingSystem.WINDOWS)
        {
            final ExtensionFilter filter = new ExtensionFilter ("Executable", "*.exe");
            chooser.getExtensionFilters ().add (filter);
        }

        final File file = chooser.showOpenDialog (this.stage);
        if (file != null)
            this.applicationCommand.setText (file.getAbsolutePath ());
    }


    /**
     * Start the DAW.
     */
    private void runDAW ()
    {
        Platform.runLater ( () -> {
            try
            {
                final String text = this.applicationCommand.getText ();
                if (OperatingSystem.get () == OperatingSystem.MAC)
                {
                    final String [] cmd = new String []
                    {
                        "open",
                        text
                    };
                    new ProcessBuilder (cmd).start ();
                }
                else
                    new ProcessBuilder (text).start ();
            }
            catch (final IOException ex)
            {
                this.logModel.addLogMessage (ex.getLocalizedMessage ());
            }
        });
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
        this.startOSCCommunication ();
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

        // Only start controllers if connection to Reaper is established
        if (this.oscServer.getIsServerRunningProperty ().get ())
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
     * Starts the receiption and opens the send port via TCP for extended communication not
     * available in the standard Reaper OSC. This has to use TCP since UDP is not available in
     * Reaper EEL.
     */
    private void startOSCCommunication ()
    {
        this.oscServer.connect (null, this.mainConfiguration.getTcpPort ());
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


    /** {@inheritDoc} */
    @Override
    public void handle (final OSCMessage message)
    {
        final String address = message.getAddress ();
        final List<Object> arguments = message.getArguments ();
        String argument = null;
        if (arguments != null && !arguments.isEmpty ())
        {
            final Object val = arguments.get (0);
            if (val != null)
                argument = val.toString ();
        }

        if (address.contains ("inipath"))
            this.loadINIFiles (argument);
        else
            this.instanceManager.parseAll (address, argument);
    }


    /** {@inheritDoc} */
    @Override
    public void sendOSC (final String command, final Object value)
    {
        final OSCMessage message = this.createOSCMessage (command, value);
        final StringBuilder msg = new StringBuilder ();
        msg.append (message.getAddress ());
        for (final Object arg: message.getArguments ())
            msg.append (' ').append (arg.toString ());
        this.oscServer.sendMessage (msg.toString ());
    }


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


    /** {@inheritDoc} */
    @Override
    public boolean isConnected ()
    {
        return this.oscServer.getIsClientConnectedProperty ().get ();
    }


    /**
     * Creates an OSC message.
     *
     * @param command The command
     * @param value The value(s)
     * @return The created message
     */
    private OSCMessage createOSCMessage (final String command, final Object value)
    {
        final List<Object> values = new ArrayList<> ();
        if (value != null)
        {
            if (value instanceof Double)
            {
                final Double doubleValue = (Double) value;
                if (value.toString ().endsWith (".0"))
                    values.add (Integer.valueOf (doubleValue.intValue ()));
                else
                    values.add (Float.valueOf (doubleValue.floatValue ()));
            }
            else if (value instanceof Integer)
                values.add (value);
            else if (value instanceof Boolean)
                values.add (((Boolean) value).booleanValue () ? Integer.valueOf (1) : Integer.valueOf (0));
            else if (value instanceof String)
                values.add (value);
            else
                this.logModel.addLogMessage ("Unsupported OSC type: " + value.getClass ().toString ());
        }
        return new OSCMessage (command, values);
    }


    private void applyTCPPort ()
    {
        final int newPort = Integer.parseInt (this.tcpPortField.getText ());
        this.mainConfiguration.setTcpPort (newPort);
        this.startOSCCommunication ();

        // Only start controllers if connection to Reaper is established
        if (this.oscServer.getIsServerRunningProperty ().get ())
            this.restartControllers ();
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


    private void sendRefreshCommand ()
    {
        if (this.isConnected ())
            this.sendOSC ("/refresh", null);
        else
            this.executor.schedule (this::sendRefreshCommand, 1000, TimeUnit.MILLISECONDS);
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
        DeviceManager.get ().loadINIFiles (path, this.logModel);
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
