package de.mossgrabers.transformator.midi;

import javax.sound.midi.MidiMessage;


/**
 * Callback interface for receiving midi messages.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface MidiMessageHandler
{
    /**
     * Handles a midi message.
     *
     * @param message The midi message to handle
     * @param timeStamp The timestamp of the message
     */
    void handleMidiMessage (MidiMessage message, long timeStamp);
}
