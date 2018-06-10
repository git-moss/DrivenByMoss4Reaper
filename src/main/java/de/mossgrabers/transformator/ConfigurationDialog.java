// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator;

import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.reaper.framework.configuration.IfxSetting;
import de.mossgrabers.reaper.framework.configuration.SettingsUI;
import de.mossgrabers.reaper.framework.configuration.TitledSeparator;
import de.mossgrabers.transformator.midi.Midi;
import de.mossgrabers.transformator.util.LogModel;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import java.awt.Toolkit;
import java.util.List;


/**
 * Dialog for editing all settings of a controller configuration.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ConfigurationDialog extends Dialog<Void>
{
    private List<ComboBox<MidiDevice>> midiInputs;
    private List<ComboBox<MidiDevice>> midiOutputs;
    private final LogModel             model;


    /**
     * Constructor.
     *
     * @param model For displaying error
     * @param owner The owner of the dialog
     * @param settings The configuration settings
     */
    public ConfigurationDialog (final LogModel model, final Window owner, final ISettingsUI settings)
    {
        this.initModality (Modality.APPLICATION_MODAL);
        this.setTitle ("Configuration");
        this.initOwner (owner);

        this.model = model;

        final DialogPane dialogPane = this.getDialogPane ();
        dialogPane.getButtonTypes ().add (new ButtonType ("Close", ButtonData.CANCEL_CLOSE));

        final GridPane grid = new GridPane ();
        grid.getStyleClass ().add ("grid");
        final ScrollPane scrollPane = new ScrollPane (grid);
        scrollPane.setFitToWidth (true);
        scrollPane.setFitToHeight (true);
        scrollPane.setHbarPolicy (ScrollBarPolicy.NEVER);
        scrollPane.setMaxHeight (Math.min (800, Toolkit.getDefaultToolkit ().getScreenSize ().getHeight ()));
        dialogPane.setContent (new BorderPane (scrollPane));

        int count = 2;

        grid.add (new TitledSeparator ("Midi Ports"), 1, 1, 2, 1);
        final SettingsUI settingsImpl = (SettingsUI) settings;

        int index = 1;
        final List<MidiDevice> inputDevices = Midi.getInputDevices ();
        this.midiInputs = settingsImpl.createMidiInputWidgets ();
        for (final ComboBox<MidiDevice> device: this.midiInputs)
        {
            grid.add (new Label ("Midi Input " + index), 1, count);
            grid.add (device, 2, count);
            device.getItems ().setAll (inputDevices);
            final MidiDevice selectedDevice = settingsImpl.getSelectedMidiInput (index - 1);
            if (selectedDevice != null)
                device.getSelectionModel ().select (selectedDevice);
            index++;
            count++;
        }
        index = 1;
        final List<MidiDevice> outputDevices = Midi.getOutputDevices ();
        this.midiOutputs = settingsImpl.createMidiOutputWidgets ();
        for (final ComboBox<MidiDevice> device: this.midiOutputs)
        {
            grid.add (new Label ("Midi Output " + index), 1, count);
            grid.add (device, 2, count);
            device.getItems ().setAll (outputDevices);
            final MidiDevice selectedDevice = settingsImpl.getSelectedMidiOutput (index - 1);
            if (selectedDevice != null)
                device.getSelectionModel ().select (selectedDevice);
            index++;
            count++;
        }
        this.updateMidiDevices ();

        final Button rescanButton = new Button ("Rescan Midi Devices");
        rescanButton.setOnAction (event -> this.updateMidiDevices ());
        rescanButton.setMaxWidth (Double.MAX_VALUE);

        grid.add (rescanButton, 2, count);
        count++;

        String category = null;
        for (final IfxSetting<?> s: settingsImpl.getSettings ())
        {
            if (category != s.getCategory ())
            {
                category = s.getCategory ();
                grid.add (new TitledSeparator (category), 1, count, 2, 1);
                count++;
            }

            final Label label = s.getLabelWidget ();
            final Node widget = s.getWidget ();
            label.setLabelFor (widget);
            grid.add (label, 1, count);
            grid.add (widget, 2, count);
            count++;
        }
    }


    /**
     * Rescans all available midi devices and fills the midi input and outputs combo boxes.
     */
    void updateMidiDevices ()
    {
        // Workaround for hang with coremidi4j
        new Thread (this::delayedUpdateMidiDevices).start ();
    }


    void delayedUpdateMidiDevices ()
    {
        try
        {
            Midi.readDeviceMetadata ();
            final List<MidiDevice> inputDevices = Midi.getInputDevices ();
            final List<MidiDevice> outputDevices = Midi.getOutputDevices ();

            for (final ComboBox<MidiDevice> in: this.midiInputs)
                in.getItems ().setAll (inputDevices);
            for (final ComboBox<MidiDevice> out: this.midiOutputs)
                out.getItems ().setAll (outputDevices);
        }
        catch (final MidiUnavailableException ex)
        {
            this.model.addLogMessage (ex.getLocalizedMessage ());
        }
    }
}
