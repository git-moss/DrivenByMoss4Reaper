// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
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
    void invokeAction (final int id);
}
