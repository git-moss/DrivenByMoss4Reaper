// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IButton;
import de.mossgrabers.framework.controller.hardware.IFader;


/**
 * Interface to a MIDI input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMidiInput
{
    /**
     * Registers a callback for receiving short (normal) MIDI messages on this MIDI input port.
     *
     * @param callback A callback function that receives three MIDI message parameters
     */
    void setMidiCallback (MidiShortCallback callback);


    /**
     * Set a callback for midi system exclusive messages coming from this input.
     *
     * @param callback The callback
     */
    void setSysexCallback (MidiSysExCallback callback);


    /**
     * Create a note input.
     *
     * @param name the name of the note input as it appears in the track input choosers in the DAW
     * @param filters a filter string formatted as hexadecimal value with `?` as wildcard. For
     *            example `80????` would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     * @return The note input
     */
    INoteInput createNoteInput (final String name, final String... filters);


    /**
     * Get the default note input.
     *
     * @return The input or null if none exists
     */
    INoteInput getDefaultNoteInput ();


    /**
     * Sends a midi short message to the DAW.
     *
     * @param status The MIDI status byte
     * @param data1 The MIDI data byte 1
     * @param data2 The MIDI data byte 2
     */
    void sendRawMidiEvent (int status, int data1, int data2);


    /**
     * Bind the given button to a MIDI command received on this midi input.
     *
     * @param button The button to bind
     * @param type THe MIDI binding type
     * @param channel The MIDI channel
     * @param value The MIDI command (CC, Note, ...)
     */
    void bind (IButton button, BindType type, int channel, int value);


    /**
     * Bind the given fader to a MIDI command received on this midi input.
     *
     * @param fader The fader to bind
     * @param type THe MIDI binding type
     * @param channel The MIDI channel
     * @param value The MIDI command (CC, Note, ...)
     */
    void bind (IFader fader, BindType type, int channel, int value);
}