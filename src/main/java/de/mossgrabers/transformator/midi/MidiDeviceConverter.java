package de.mossgrabers.transformator.midi;

import javafx.util.StringConverter;

import javax.sound.midi.MidiDevice;


/**
 * Converter for MidiDevice objects to a string representation. Use with ListView or ComboBox.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiDeviceConverter extends StringConverter<MidiDevice>
{
    /** {@inheritDoc} */
    @Override
    public String toString (final MidiDevice device)
    {
        return device == null ? null : device.getDeviceInfo ().getName ();
    }


    /** {@inheritDoc} */
    @Override
    public MidiDevice fromString (final String string)
    {
        // Not used
        return null;
    }
}
