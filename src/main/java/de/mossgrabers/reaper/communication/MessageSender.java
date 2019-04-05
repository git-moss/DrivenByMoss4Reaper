// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.communication;

/**
 * Interface to communicate with the C++ DLL.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface MessageSender
{
    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     */
    default void processNoArg (final String processor)
    {
        this.processNoArg (processor, null);
    }


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     */
    void processNoArg (final String processor, final String command);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value A string value
     */
    void processStringArg (final String processor, final String command, final String value);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param value An integer value
     */
    default void processIntArg (final String processor, final int value)
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
    void processIntArg (final String processor, final String command, final int value);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param value A double value
     */
    default void processDoubleArg (final String processor, final double value)
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
    void processDoubleArg (final String processor, final String command, final double value);


    /**
     * Call Reaper command in DLL.
     *
     * @param processor The processor ID
     * @param command The command ID
     * @param value A boolean value
     */
    default void processBooleanArg (final String processor, final String command, final boolean value)
    {
        this.processIntArg (processor, command, value ? 1 : 0);
    }


    /**
     * Call Reaper MIDI command in DLL.
     *
     * @param status MIDI status byte
     * @param data1 MIDI data byte 1
     * @param data2 MIDI data byte 2
     */
    void processMidiArg (int status, int data1, int data2);


    /**
     * Invokes the action for the given action identifier.
     *
     * @param id The action identifier, must not be null
     */
    default void invokeAction (final int id)
    {
        this.processIntArg ("action", "", id);
    }
}
