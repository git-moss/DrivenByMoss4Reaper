// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator.communication;

/**
 * Send an OSC message to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface MessageSender
{
    /**
     * Sends an OSC message.
     *
     * @param command The command to send
     * @param value The value to send, may be null
     */
    void sendOSC (final String command, final Object value);


    /**
     * Returns true if the sender is connected to the receiver.
     *
     * @return True if connected
     */
    boolean isConnected ();


    /**
     * Invokes the action for the given action identifier.
     *
     * @param id the action identifier, must not be null
     */
    void invokeAction (final String id);


    /**
     * Invokes the action for the given action identifier.
     *
     * @param id The action identifier, must not be null
     */
    void invokeAction (final int id);
}
