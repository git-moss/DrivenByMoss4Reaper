package de.mossgrabers.transformator.communication;

import com.illposed.osc.OSCMessage;


/**
 * Handler interface for received OSC messages.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface MessageHandler
{
    /**
     * Handles a received OSC message.
     *
     * @param message The received message
     */
    public void handle (final OSCMessage message);
}
