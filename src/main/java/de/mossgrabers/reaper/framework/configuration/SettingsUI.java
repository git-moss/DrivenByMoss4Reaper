// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IBooleanSetting;
import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.utils.Pair;
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
 * The Reaper implementation to create user interface widgets for settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SettingsUI implements ISettingsUI
{
    private static final String                    TAG_IS_ENABLED  = "TAG_IS_ENABLED";
    private static final String                    TAG_MIDI_INPUT  = "MIDI_INPUT";
    private static final String                    TAG_MIDI_OUTPUT = "MIDI_OUTPUT";

    private final LogModel                         logModel;
    private final PropertiesEx                     properties;
    private final List<IfxSetting<?>>              settings        = new ArrayList<> ();

    private final int                              numMidiInPorts;
    private final int                              numMidiOutPorts;
    private final List<Pair<String [], String []>> discoveryPairs;

    private boolean                                isEnabled       = true;

    private final MidiDevice []                    selectedMidiInputs;
    private final MidiDevice []                    selectedMidiOutputs;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param properties Where to store to
     * @param numMidiInPorts The number of required midi input ports
     * @param numMidiOutPorts The number of required midi output ports
     * @param discoveryPairs Suggestions for automatically selecting the required in-/outputs
     */
    public SettingsUI (final LogModel logModel, final PropertiesEx properties, final int numMidiInPorts, final int numMidiOutPorts, final List<Pair<String [], String []>> discoveryPairs)
    {
        this.logModel = logModel;
        this.properties = properties;

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
     * Get the selected midi input. A controller can require several midi inputs.
     *
     * @param index The index of the midi input
     * @return The midi device or null if none is selected
     */
    public MidiDevice getSelectedMidiInput (final int index)
    {
        return this.selectedMidiInputs[index];
    }


    /**
     * Get the selected midi output. A controller can require several midi outputs.
     *
     * @param index The index of the midi output
     * @return The midi device or null if none is selected
     */
    public MidiDevice getSelectedMidiOutput (final int index)
    {
        return this.selectedMidiOutputs[index];
    }


    /**
     * Get the selected midi inputs. A controller can require several midi inputs.
     *
     * @return The midi device or null if none is selected
     */
    public MidiDevice [] getSelectedMidiInputs ()
    {
        return this.selectedMidiInputs;
    }


    /**
     * Get the selected midi outputs. A controller can require several midi outputs.
     *
     * @return The midi device or null if none is selected
     */
    public MidiDevice [] getSelectedMidiOutputs ()
    {
        return this.selectedMidiOutputs;
    }


    /**
     * Create all selection widgets for the midi inputs.
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
            midiInput.addItemListener (event -> SafeRunLater.execute (SettingsUI.this.logModel, () -> {
                final MidiDevice selectedItem = midiInput.getSelectedItem ();
                if (selectedItem != null)
                    this.selectedMidiInputs[index] = selectedItem;
            }));
        }
        return midiInputs;
    }


    /**
     * Create all selection widgets for the midi outputs.
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
            midiOutput.addActionListener (event -> SafeRunLater.execute (SettingsUI.this.logModel, () -> {
                final MidiDevice selectedItem = midiOutput.getSelectedItem ();
                if (selectedItem != null)
                    this.selectedMidiOutputs[index] = midiOutput.getSelectedItem ();
            }));
        }
        return midiOutputs;
    }


    /**
     * Flushes the values of all settings.
     */
    public void flush ()
    {
        this.settings.forEach (s -> {
            try
            {
                s.flush ();
            }
            catch (final RuntimeException ex)
            {
                this.logModel.error ("Could not flush setting.", ex);
            }
        });
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
     * Init all settings.
     */
    public void init ()
    {
        this.settings.forEach (IfxSetting::init);
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
     * Get all settings.
     *
     * @return The settings
     */
    public List<IfxSetting<?>> getSettings ()
    {
        return this.settings;
    }


    /** {@inheritDoc} */
    @Override
    public IEnumSetting getEnumSetting (final String label, final String category, final String [] options, final String initialValue)
    {
        final EnumSettingImpl setting = new EnumSettingImpl (this.logModel, this.properties, label, category, options, initialValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IStringSetting getStringSetting (final String label, final String category, final int numChars, final String initialText)
    {
        final StringSettingImpl setting = new StringSettingImpl (this.logModel, this.properties, label, category, initialText);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IDoubleSetting getNumberSetting (final String label, final String category, final double minValue, final double maxValue, final double stepResolution, final String unit, final double initialValue)
    {
        final DoubleSettingImpl setting = new DoubleSettingImpl (this.logModel, this.properties, label, category, initialValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IIntegerSetting getRangeSetting (final String label, final String category, final int minValue, final int maxValue, final int stepResolution, final String unit, final int initialValue)
    {
        final IntegerSettingImpl setting = new IntegerSettingImpl (this.logModel, this.properties, label, category, initialValue, minValue, maxValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public ISignalSetting getSignalSetting (final String label, final String category, final String title)
    {
        final SignalSettingImpl setting = new SignalSettingImpl (this.logModel, this.properties, label, category, title);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IColorSetting getColorSetting (final String label, final String category, final ColorEx defaultColor)
    {
        final ColorSettingImpl setting = new ColorSettingImpl (this.logModel, this.properties, label, category, defaultColor);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IBooleanSetting getBooleanSetting (final String label, final String category, final boolean initialValue)
    {
        final BooleanSettingImpl setting = new BooleanSettingImpl (this.logModel, this.properties, label, category, initialValue);
        this.settings.add (setting);
        return setting;
    }
}
