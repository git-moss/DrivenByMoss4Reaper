// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.dialog;

import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.reaper.framework.configuration.IfxSetting;
import de.mossgrabers.reaper.framework.configuration.SettingsUI;
import de.mossgrabers.reaper.framework.midi.Midi;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.widget.BoxPanel;
import de.mossgrabers.reaper.ui.widget.JComboBoxX;
import de.mossgrabers.reaper.ui.widget.TitledSeparator;
import de.mossgrabers.reaper.ui.widget.TwoColsPanel;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;


/**
 * Dialog for editing all settings of a controller configuration.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ConfigurationDialog extends BasicDialog
{
    private static final long            serialVersionUID = -495747813993365661L;

    private List<JComboBoxX<MidiDevice>> midiInputs;
    private List<JComboBoxX<MidiDevice>> midiOutputs;
    private final transient LogModel     model;
    private final transient ISettingsUI  settings;


    /**
     * Constructor.
     *
     * @param model For displaying error
     * @param owner The owner of the dialog
     * @param settings The configuration settings
     */
    public ConfigurationDialog (final LogModel model, final Window owner, final ISettingsUI settings)
    {
        super ((JFrame) owner, "Configuration", true, true);

        this.setMinimumSize (new Dimension (400, 600));

        this.model = model;
        this.settings = settings;

        this.basicInit ();
    }


    /** {@inheritDoc} */
    @Override
    protected Container init () throws Exception
    {
        final JPanel contentPane = new JPanel (new BorderLayout ());

        final TwoColsPanel mainColumn = new TwoColsPanel (true);
        final JScrollPane scrollPane = new JScrollPane (mainColumn);
        scrollPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        final SettingsUI settingsImpl = (SettingsUI) this.settings;
        this.midiInputs = settingsImpl.createMidiInputWidgets ();
        this.midiOutputs = settingsImpl.createMidiOutputWidgets ();

        final boolean hasMidiPorts = !this.midiInputs.isEmpty () && !this.midiOutputs.isEmpty ();
        if (hasMidiPorts)
            mainColumn.addComponent (new TitledSeparator ("Midi Ports"), BoxPanel.NORMAL);

        contentPane.add (scrollPane, BorderLayout.CENTER);

        int index = 1;
        final List<MidiDevice> inputDevices = Midi.getInputDevices ();
        for (final JComboBoxX<MidiDevice> device: this.midiInputs)
        {
            mainColumn.addComponent (device, new JLabel ("Midi Input " + index), null, BoxPanel.NORMAL);
            device.setAll (inputDevices);
            final MidiDevice selectedDevice = settingsImpl.getSelectedMidiInput (index - 1);
            if (selectedDevice != null)
                device.setSelectedItem (selectedDevice);
            index++;
        }

        index = 1;
        final List<MidiDevice> outputDevices = Midi.getOutputDevices ();
        for (final JComboBoxX<MidiDevice> device: this.midiOutputs)
        {
            mainColumn.addComponent (device, new JLabel ("Midi Output " + index), null, BoxPanel.NORMAL);
            device.setAll (outputDevices);
            final MidiDevice selectedDevice = settingsImpl.getSelectedMidiOutput (index - 1);
            if (selectedDevice != null)
                device.setSelectedItem (selectedDevice);
            index++;
        }

        if (hasMidiPorts)
        {
            final JButton rescanButton = new JButton ("Rescan Midi Devices");
            rescanButton.addActionListener (event -> this.updateMidiDevices ());
            mainColumn.addComponent (rescanButton, BoxPanel.NORMAL);
        }

        String category = null;
        for (final IfxSetting<?> s: settingsImpl.getSettings ())
        {
            if (category != s.getCategory ())
            {
                category = s.getCategory ();
                mainColumn.addComponent (new TitledSeparator (category), BoxPanel.NORMAL);
            }

            final JLabel label = s.getLabelWidget ();
            final JComponent widget = s.getWidget ();

            mainColumn.addComponent (widget, label, null, BoxPanel.NORMAL);
        }

        // Close button
        final BoxPanel buttons = new BoxPanel (BoxLayout.X_AXIS, true);
        buttons.createSpace (BoxPanel.GLUE);
        this.setButtons (null, buttons.createButton ("Close", null, BoxPanel.NONE));
        contentPane.add (buttons, BorderLayout.SOUTH);

        this.addComponentListener (new ComponentAdapter ()
        {
            @Override
            public void componentResized (final ComponentEvent event)
            {
                final Rectangle b = ConfigurationDialog.this.getBounds ();
                final Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
                final int maxHeight = (int) screenSize.getHeight () - 200;
                if (b.height > maxHeight)
                {
                    b.height = maxHeight;
                    ConfigurationDialog.this.setBounds (b);
                }
                super.componentResized (event);
            }
        });

        return contentPane;
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
            this.fillMidiDevices ();
        }
        catch (final MidiUnavailableException ex)
        {
            this.model.error ("Could not update MIDI devices.", ex);
        }
    }


    private void fillMidiDevices ()
    {
        final List<MidiDevice> inputDevices = Midi.getInputDevices ();
        final List<MidiDevice> outputDevices = Midi.getOutputDevices ();

        for (final JComboBoxX<MidiDevice> in: this.midiInputs)
            in.setAll (inputDevices);
        for (final JComboBoxX<MidiDevice> out: this.midiOutputs)
            out.setAll (outputDevices);
    }
}
