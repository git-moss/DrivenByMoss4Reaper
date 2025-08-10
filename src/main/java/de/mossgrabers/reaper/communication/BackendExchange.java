// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.communication;

import java.util.Map;


/**
 * Interface to communicate with the C++ DLL.
 *
 * @author Jürgen Moßgraber
 */
public interface BackendExchange
{
    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     */
    default void processNoArg (final Processor processor)
    {
        this.processNoArg (processor, null);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     */
    void processNoArg (final Processor processor, final String command);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value A string value
     */
    void processStringArg (final Processor processor, final String command, final String value);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param values Several string values
     */
    void processStringArgs (final Processor processor, final String command, final String [] values);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param value An integer value
     */
    default void processIntArg (final Processor processor, final int value)
    {
        this.processIntArg (processor, null, value);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value An integer value
     */
    void processIntArg (final Processor processor, final String command, final int value);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param value A double value
     */
    default void processDoubleArg (final Processor processor, final double value)
    {
        this.processDoubleArg (processor, null, value);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value A double value
     */
    void processDoubleArg (final Processor processor, final String command, final double value);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value A boolean value
     */
    default void processBooleanArg (final Processor processor, final String command, final boolean value)
    {
        this.processIntArg (processor, command, value ? 1 : 0);
    }


    /**
     * Delay updates for a specific processor. Use to prevents that Reaper sends old values before
     * the latest ones are applied.
     *
     * @param processor The processor to delay
     */
    void delayUpdates (final Processor processor);


    /**
     * Disable/enable an update processor for performance improvements.
     *
     * @param processor The processor to The processor to disable/enable
     * @param enable True to enable processor updates, false to disable
     */
    void enableUpdates (final Processor processor, final boolean enable);


    /**
     * Insert a MIDI message into the Reaper MIDI input port queue.
     *
     * @param deviceID The device (MIDI input port) which 'received' the message
     * @param status MIDI status byte
     * @param data1 MIDI data byte 1
     * @param data2 MIDI data byte 2
     */
    void processMidiArg (int deviceID, int status, int data1, int data2);


    /**
     * Invokes the action for the given action identifier.
     *
     * @param id The action identifier, must not be null
     */
    void invokeAction (final int id);


    /**
     * Get all MIDI inputs.
     *
     * @return Returns key/value pairs where the key is the deviceID and the value the name
     */
    Map<Integer, String> getMidiInputs ();


    /**
     * Get all MIDI outputs.
     *
     * @return Returns key/value pairs where the key is the deviceID and the value the name
     */
    Map<Integer, String> getMidiOutputs ();


    /**
     * Opens a MIDI input.
     *
     * @param deviceID The ID (index) of the MIDI input
     * @return True if the port could be successfully opened
     */
    boolean openMidiInput (int deviceID);


    /**
     * Opens a MIDI output.
     *
     * @param deviceID The ID (index) of the MIDI input
     * @return True if the port could be successfully opened
     */
    boolean openMidiOutput (int deviceID);


    /**
     * Closes a MIDI input.
     *
     * @param deviceID The ID (index) of the MIDI input
     */
    void closeMidiInput (int deviceID);


    /**
     * Closes a MIDI output.
     *
     * @param deviceID The ID (index) of the MIDI input
     */
    void closeMidiOutput (int deviceID);


    /**
     * Sends a MIDI message to the output port.
     *
     * @param deviceID The ID (index) of the MIDI input
     * @param data The MIDI message
     */
    void sendMidiData (int deviceID, byte [] data);


    /**
     * Set the MIDI filters to apply to an MIDI input port. MIDI messages which match a filter will
     * be directly forwarded to Reaper.
     * 
     * @param deviceID The ID of the MIDI input port
     * @param noteInputIndex The index of the note input of the given device
     * @param backendFilters The filters
     */
    void setNoteInputFilters (int deviceID, int noteInputIndex, String [] backendFilters);


    /**
     * Set a key translation table.
     *
     * @param deviceID The ID of the MIDI input port
     * @param noteInputIndex The index of the note input of the given device
     * @param table The table
     */
    void setNoteInputKeyTranslationTable (int deviceID, int noteInputIndex, int [] table);


    /**
     * Set a velocity translation table.
     *
     * @param deviceID The ID of the MIDI input port
     * @param noteInputIndex The index of the note input of the given device
     * @param table The table
     */
    void setNoteInputVelocityTranslationTable (int deviceID, int noteInputIndex, int [] table);
}
