// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
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
import de.mossgrabers.transformator.midi.Midi;
import de.mossgrabers.transformator.midi.MidiDeviceConverter;
import de.mossgrabers.transformator.util.PropertiesEx;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;

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
    private static final String                    TAG_MIDI_INPUT  = "MIDI_INPUT";
    private static final String                    TAG_MIDI_OUTPUT = "MIDI_OUTPUT";

    private final List<IfxSetting<?>>              settings        = new ArrayList<> ();

    private final int                              numMidiInPorts;
    private final int                              numMidiOutPorts;
    private final List<Pair<String [], String []>> discoveryPairs;

    private final List<ComboBox<MidiDevice>>       midiInputs      = new ArrayList<> ();
    private final List<ComboBox<MidiDevice>>       midiOutputs     = new ArrayList<> ();

    private final MidiDevice []                    selectedMidiInputs;
    private final MidiDevice []                    selectedMidiOutputs;


    /**
     * Constructor.
     *
     * @param numMidiInPorts The number of required midi input ports
     * @param numMidiOutPorts The number of required midi output ports
     * @param discoveryPairs Suggestions for automatically selecting the required in-/outputs
     */
    public SettingsUI (final int numMidiInPorts, final int numMidiOutPorts, final List<Pair<String [], String []>> discoveryPairs)
    {
        this.numMidiInPorts = numMidiInPorts;
        this.numMidiOutPorts = numMidiOutPorts;
        this.discoveryPairs = discoveryPairs;

        this.selectedMidiInputs = new MidiDevice [this.numMidiInPorts];
        this.selectedMidiOutputs = new MidiDevice [this.numMidiOutPorts];
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
    public List<ComboBox<MidiDevice>> createMidiInputWidgets ()
    {
        this.midiInputs.clear ();
        for (int i = 0; i < this.numMidiInPorts; i++)
        {
            final ComboBox<MidiDevice> midiInput = new ComboBox<> ();
            midiInput.setConverter (new MidiDeviceConverter ());
            midiInput.setMaxWidth (Double.MAX_VALUE);
            this.midiInputs.add (midiInput);
            final int index = i;
            midiInput.getSelectionModel ().selectedItemProperty ().addListener ((ChangeListener<MidiDevice>) (observable, oldValue, newValue) -> this.selectedMidiInputs[index] = newValue);
        }
        return this.midiInputs;
    }


    /**
     * Create all selection widgets for the midi outputs.
     *
     * @return All widgets
     */
    public List<ComboBox<MidiDevice>> createMidiOutputWidgets ()
    {
        this.midiOutputs.clear ();
        for (int i = 0; i < this.numMidiOutPorts; i++)
        {
            final ComboBox<MidiDevice> midiOutput = new ComboBox<> ();
            midiOutput.setConverter (new MidiDeviceConverter ());
            midiOutput.setMaxWidth (Double.MAX_VALUE);
            this.midiOutputs.add (midiOutput);
            final int index = i;
            midiOutput.getSelectionModel ().selectedItemProperty ().addListener ((ChangeListener<MidiDevice>) (observable, oldValue, newValue) -> this.selectedMidiOutputs[index] = newValue);
        }
        return this.midiOutputs;
    }


    /**
     * Flushes the values of all settings.
     */
    public void flush ()
    {
        this.settings.forEach (s -> s.flush ());
    }


    /**
     * Load all settings.
     *
     * @param properties Where to store to
     */
    public void load (final PropertiesEx properties)
    {
        for (int i = 0; i < this.numMidiInPorts; i++)
        {
            this.selectedMidiInputs[i] = Midi.getInputDevice (properties.getString (TAG_MIDI_INPUT + i));
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
            this.selectedMidiOutputs[i] = Midi.getOutputDevice (properties.getString (TAG_MIDI_OUTPUT + i));
            if (this.selectedMidiOutputs[i] != null)
                continue;
            for (final Pair<String [], String []> pair: this.discoveryPairs)
            {
                this.selectedMidiOutputs[i] = Midi.getOutputDevice (pair.getKey ()[i]);
                if (this.selectedMidiOutputs[i] != null)
                    break;
            }
        }

        this.settings.forEach (s -> s.load (properties));
    }


    /**
     * Store all settings.
     *
     * @param properties Where to store to
     */
    public void store (final PropertiesEx properties)
    {
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
        final EnumSettingImpl setting = new EnumSettingImpl (label, category, options, initialValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IStringSetting getStringSetting (final String label, final String category, final int numChars, final String initialText)
    {
        final StringSettingImpl setting = new StringSettingImpl (label, category, initialText);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IDoubleSetting getNumberSetting (final String label, final String category, final double minValue, final double maxValue, final double stepResolution, final String unit, final double initialValue)
    {
        final DoubleSettingImpl setting = new DoubleSettingImpl (label, category, initialValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IIntegerSetting getRangeSetting (final String label, final String category, final int minValue, final int maxValue, final int stepResolution, final String unit, final int initialValue)
    {
        final IntegerSettingImpl setting = new IntegerSettingImpl (label, category, initialValue, minValue, maxValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public ISignalSetting getSignalSetting (final String label, final String category, final String title)
    {
        final SignalSettingImpl setting = new SignalSettingImpl (label, category, title);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IColorSetting getColorSetting (final String label, final String category, final ColorEx defaultColor)
    {
        final ColorSettingImpl setting = new ColorSettingImpl (label, category, defaultColor);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IBooleanSetting getBooleanSetting (final String label, final String category, final boolean initialValue)
    {
        final BooleanSettingImpl setting = new BooleanSettingImpl (label, category, initialValue);
        this.settings.add (setting);
        return setting;
    }
}
