// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import javax.sound.midi.MidiDevice;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import java.awt.Component;


/**
 * Converter for MidiDevice objects to a string representation. Use with ListView or ComboBox.
 *
 * @author Jürgen Moßgraber
 */
public class MidiDeviceConverter extends DefaultListCellRenderer
{
    private static final long serialVersionUID = -8262443193380541011L;


    /** {@inheritDoc} */
    @Override
    public Component getListCellRendererComponent (final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        final Object v = value instanceof final MidiDevice md ? md.getDeviceInfo ().getName () : value;
        return super.getListCellRendererComponent (list, v, index, isSelected, cellHasFocus);
    }
}