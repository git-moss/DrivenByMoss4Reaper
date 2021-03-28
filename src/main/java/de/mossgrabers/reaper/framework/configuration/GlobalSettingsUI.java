// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.midi.Midi;
import de.mossgrabers.reaper.framework.midi.MidiDeviceConverter;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;
import de.mossgrabers.reaper.ui.widget.JComboBoxX;

import javax.sound.midi.MidiDevice;

import java.util.ArrayList;
import java.util.List;


/**
 * The Reaper implementation to create user interface widgets for global settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GlobalSettingsUI extends AbstractSettingsUI
{
    private static final String                    TAG_IS_ENABLED  = "TAG_IS_ENABLED";
    private static final String                    TAG_MIDI_INPUT  = "MIDI_INPUT";
    private static final String                    TAG_MIDI_OUTPUT = "MIDI_OUTPUT";

    private final int                              numMidiInPorts;
    private final int                              numMidiOutPorts;
    private final List<Pair<String [], String []>> discoveryPairs;

    private boolean                                isEnabled       = true;

    private final MidiDevice []                    selectedMidiInputs;
    private final MidiDevice []                    selectedMidiOutputs;


    /**
     * Constructor.
     *
     * @param sender The sender
     * @param logModel The log model
     * @param properties Where to store to
     * @param numMidiInPorts The number of required MIDI input ports
     * @param numMidiOutPorts The number of required MIDI output ports
     * @param discoveryPairs Suggestions for automatically selecting the required in-/outputs
     */
    public GlobalSettingsUI (final MessageSender sender, final LogModel logModel, final PropertiesEx properties, final int numMidiInPorts, final int numMidiOutPorts, final List<Pair<String [], String []>> discoveryPairs)
    {
        super (logModel, properties, sender);

        this.numMidiInPorts = numMidiInPorts;
        this.numMidiOutPorts = numMidiOutPorts;
        this.discoveryPairs = discoveryPairs;

        this.selectedMidiInputs = new MidiDevice [this.numMidiInPorts];
        this.selectedMidiOutputs = new MidiDevice [this.numMidiOutPorts];
    }


    /**
     * Get if the controller is enabled.
     *
     * @return True if enabled
     */
    public boolean isEnabled ()
    {
        return this.isEnabled;
    }


    /**
     * Set if the controller is enabled.
     *
     * @param isEnabled True if enabled
     */
    public void setEnabled (final boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }


    /**
     * Get the selected MIDI input. A controller can require several MIDI inputs.
     *
     * @param index The index of the MIDI input
     * @return The MIDI device or null if none is selected
     */
    public MidiDevice getSelectedMidiInput (final int index)
    {
        return this.selectedMidiInputs[index];
    }


    /**
     * Get the selected MIDI output. A controller can require several MIDI outputs.
     *
     * @param index The index of the MIDI output
     * @return The MIDI device or null if none is selected
     */
    public MidiDevice getSelectedMidiOutput (final int index)
    {
        return this.selectedMidiOutputs[index];
    }


    /**
     * Get the selected MIDI inputs. A controller can require several MIDI inputs.
     *
     * @return The MIDI device or null if none is selected
     */
    public MidiDevice [] getSelectedMidiInputs ()
    {
        return this.selectedMidiInputs;
    }


    /**
     * Get the selected MIDI outputs. A controller can require several MIDI outputs.
     *
     * @return The MIDI device or null if none is selected
     */
    public MidiDevice [] getSelectedMidiOutputs ()
    {
        return this.selectedMidiOutputs;
    }


    /**
     * Create all selection widgets for the MIDI inputs.
     *
     * @return All widgets
     */
    public List<JComboBoxX<MidiDevice>> createMidiInputWidgets ()
    {
        final List<JComboBoxX<MidiDevice>> midiInputs = new ArrayList<> ();
        for (int i = 0; i < this.numMidiInPorts; i++)
        {
            final JComboBoxX<MidiDevice> midiInput = new JComboBoxX<> ();
            midiInput.setRenderer (new MidiDeviceConverter ());
            midiInputs.add (midiInput);
            final int index = i;
            midiInput.addItemListener (event -> SafeRunLater.execute (GlobalSettingsUI.this.logModel, () -> {
                final MidiDevice selectedItem = midiInput.getSelectedItem ();
                if (selectedItem != null)
                    this.selectedMidiInputs[index] = selectedItem;
            }));
        }
        return midiInputs;
    }


    /**
     * Create all selection widgets for the MIDI outputs.
     *
     * @return All widgets
     */
    public List<JComboBoxX<MidiDevice>> createMidiOutputWidgets ()
    {
        final List<JComboBoxX<MidiDevice>> midiOutputs = new ArrayList<> ();
        for (int i = 0; i < this.numMidiOutPorts; i++)
        {
            final JComboBoxX<MidiDevice> midiOutput = new JComboBoxX<> ();
            midiOutput.setRenderer (new MidiDeviceConverter ());
            midiOutputs.add (midiOutput);
            final int index = i;
            midiOutput.addActionListener (event -> SafeRunLater.execute (GlobalSettingsUI.this.logModel, () -> {
                final MidiDevice selectedItem = midiOutput.getSelectedItem ();
                if (selectedItem != null)
                    this.selectedMidiOutputs[index] = midiOutput.getSelectedItem ();
            }));
        }
        return midiOutputs;
    }


    /**
     * Load MIDI settings.
     */
    public void initMIDI ()
    {
        this.isEnabled = this.properties.getBoolean (TAG_IS_ENABLED, true);

        for (int i = 0; i < this.numMidiInPorts; i++)
        {
            this.selectedMidiInputs[i] = Midi.getInputDevice (this.properties.getString (TAG_MIDI_INPUT + i));
            if (this.selectedMidiInputs[i] != null)
                continue;
            for (final Pair<String [], String []> pair: this.discoveryPairs)
            {
                this.selectedMidiInputs[i] = Midi.getInputDevice (pair.getKey ()[i]);
                if (this.selectedMidiInputs[i] != null)
                    break;
            }
        }

        for (int i = 0; i < this.numMidiOutPorts; i++)
        {
            this.selectedMidiOutputs[i] = Midi.getOutputDevice (this.properties.getString (TAG_MIDI_OUTPUT + i));
            if (this.selectedMidiOutputs[i] != null)
                continue;
            for (final Pair<String [], String []> pair: this.discoveryPairs)
            {
                this.selectedMidiOutputs[i] = Midi.getOutputDevice (pair.getValue ()[i]);
                if (this.selectedMidiOutputs[i] != null)
                    break;
            }
        }
    }


    /**
     * Store all settings.
     *
     * @param properties Where to store to
     */
    public void store (final PropertiesEx properties)
    {
        properties.putBoolean (TAG_IS_ENABLED, this.isEnabled);

        for (int i = 0; i < this.numMidiInPorts; i++)
        {
            final MidiDevice midiDevice = this.getSelectedMidiInput (i);
            if (midiDevice == null)
                properties.remove (TAG_MIDI_INPUT + i);
            else
                properties.putString (TAG_MIDI_INPUT + i, midiDevice.getDeviceInfo ().getName ());
        }

        for (int i = 0; i < this.numMidiOutPorts; i++)
        {
            final MidiDevice midiDevice = this.getSelectedMidiOutput (i);
            if (midiDevice == null)
                properties.remove (TAG_MIDI_OUTPUT + i);
            else
                properties.putString (TAG_MIDI_OUTPUT + i, midiDevice.getDeviceInfo ().getName ());
        }

        this.settings.forEach (s -> s.store (properties));
    }


    /**
     * Check if any of the settings has changed and needs to be stored.
     *
     * @return True if dirty
     */
    public boolean isDirty ()
    {
        for (final IfxSetting setting: this.settings)
        {
            if (setting.isDirty ())
                return true;
        }
        return false;
    }
}
