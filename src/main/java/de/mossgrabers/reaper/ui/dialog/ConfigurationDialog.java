// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.dialog;

import de.mossgrabers.reaper.framework.configuration.ActionSettingImpl;
import de.mossgrabers.reaper.framework.configuration.GlobalSettingsUI;
import de.mossgrabers.reaper.framework.configuration.IfxSetting;
import de.mossgrabers.reaper.framework.midi.Midi;
import de.mossgrabers.reaper.framework.midi.MissingMidiDevice;
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
import java.awt.Toolkit;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;


/**
 * Dialog for editing all settings of a controller configuration.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ConfigurationDialog extends BasicDialog
{
    private static final long                serialVersionUID = -495747813993365661L;

    private static final int                 MIN_HEIGHT       = 600;

    private List<JComboBoxX<MidiDevice>>     midiInputBoxes;
    private List<JComboBoxX<MidiDevice>>     midiOutputBoxes;
    private final transient LogModel         model;
    private final transient GlobalSettingsUI settings;

    /**
     * Constructor.
     *
     * @param model For displaying error
     * @param owner The owner of the dialog
     * @param settings The configuration settings
     */
    public ConfigurationDialog (final LogModel model, final Window owner, final GlobalSettingsUI settings)
    {
        super ((JFrame) owner, "Configuration", true, true);

        this.setMinimumSize (new Dimension (400, MIN_HEIGHT));
        this.setSize (400, MIN_HEIGHT);

        this.model = model;
        this.settings = settings;

        this.basicInit ();
    }


    /**
     * Set an action ID. Looks for an action setting waiting for input.
     *
     * @param actionID The action ID to set
     */
    public void setAction (final String actionID)
    {
        for (final IfxSetting s: this.settings.getSettings ())
        {
            if (s instanceof final ActionSettingImpl asi && asi.isSelectionActive ())
            {
                asi.set (actionID);
                this.toFront ();
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected Container init ()
    {
        final JPanel contentPane = new JPanel (new BorderLayout ());

        final TwoColsPanel mainColumn = new TwoColsPanel (true);
        final JPanel wrapper = new JPanel (new BorderLayout ());
        wrapper.add (mainColumn, BorderLayout.NORTH);

        final JScrollPane scrollPane = new JScrollPane (wrapper);
        scrollPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add (scrollPane, BorderLayout.CENTER);

        this.midiInputBoxes = this.settings.createMidiInputWidgets ();
        this.midiOutputBoxes = this.settings.createMidiOutputWidgets ();

        if (!(this.midiInputBoxes.isEmpty () && this.midiOutputBoxes.isEmpty ()))
        {
            mainColumn.addComponent (new TitledSeparator ("Midi Ports"), BoxPanel.NORMAL);

            for (int i = 0; i < this.midiInputBoxes.size (); i++)
                mainColumn.addComponent (this.midiInputBoxes.get (i), new JLabel ("Midi Input " + (i + 1)), null, BoxPanel.NORMAL);
            for (int i = 0; i < this.midiOutputBoxes.size (); i++)
                mainColumn.addComponent (this.midiOutputBoxes.get (i), new JLabel ("Midi Output " + (i + 1)), null, BoxPanel.NORMAL);

            final JButton rescanButton = new JButton ("Rescan Midi Devices");
            rescanButton.addActionListener (event -> this.updateMidiDevices ());
            mainColumn.addComponent (rescanButton, BoxPanel.NORMAL);

            this.fillAndSetMidiDevices ();
        }

        String category = null;
        for (final IfxSetting s: this.settings.getSettings ())
        {
            final String cat = s.getCategory ();
            if (category == null || !category.equals (cat))
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

        return contentPane;
    }


    /** {@inheritDoc}} */
    @Override
    public Dimension getPreferredSize ()
    {
        final Dimension dim = super.getPreferredSize ();
        final double maxHeight = Toolkit.getDefaultToolkit ().getScreenSize ().getHeight () - 200;
        if (dim.height > maxHeight)
            dim.height = (int) maxHeight;
        return dim;
    }


    /**
     * Rescans all available MIDI devices and fills the MIDI input and outputs combo boxes.
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
            // Reload the settings
            this.settings.initMIDI ();
            this.fillAndSetMidiDevices ();
        }
        catch (final MidiUnavailableException ex)
        {
            this.model.error ("Could not update MIDI devices.", ex);
        }
    }


    private void fillAndSetMidiDevices ()
    {
        final List<MidiDevice> inputDevices = Midi.getInputDevices ();
        for (int i = 0; i < this.midiInputBoxes.size (); i++)
        {
            final List<MidiDevice> devices = new ArrayList<> (inputDevices);
            final JComboBoxX<MidiDevice> deviceBox = this.midiInputBoxes.get (i);
            final MidiDevice selectedDevice = this.settings.getSelectedMidiInput (i);
            if (selectedDevice instanceof MissingMidiDevice)
                devices.add (0, selectedDevice);
            deviceBox.setAll (devices);
            deviceBox.setSelectedItem (selectedDevice);
        }

        final List<MidiDevice> outputDevices = Midi.getOutputDevices ();
        for (int i = 0; i < this.midiOutputBoxes.size (); i++)
        {
            final List<MidiDevice> devices = new ArrayList<> (outputDevices);
            final JComboBoxX<MidiDevice> deviceBox = this.midiOutputBoxes.get (i);
            final MidiDevice selectedDevice = this.settings.getSelectedMidiOutput (i);
            if (selectedDevice instanceof MissingMidiDevice)
                devices.add (0, selectedDevice);
            deviceBox.setAll (devices);
            deviceBox.setSelectedItem (selectedDevice);
        }
    }
}
