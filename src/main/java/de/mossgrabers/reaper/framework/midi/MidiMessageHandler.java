// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import javax.sound.midi.MidiMessage;


/**
 * Callback interface for receiving MIDI messages.
 *
 * @author Jürgen Moßgraber
 */
public interface MidiMessageHandler
{
    /**
     * Handles a MIDI message.
     *
     * @param message The MIDI message to handle
     * @param timeStamp The timestamp of the message
     */
    void handleMidiMessage (MidiMessage message, long timeStamp);
}
