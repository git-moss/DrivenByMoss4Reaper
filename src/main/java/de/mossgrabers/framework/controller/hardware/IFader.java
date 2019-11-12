// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * Interface for a proxy to a fader on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IFader extends IControl
{
    /**
     * Assign a command to a fader, which it should control.
     *
     * @param command The command to assign
     * @return The fader for fluent interface
     */
    IFader bind (ContinuousCommand command);


    /**
     * Bind a midi command coming from an MIDI input to the fader.
     *
     * @param input The MIDI input
     * @param type How to bind
     * @param value The MIDI CC or note to bind
     */
    void bind (IMidiInput input, BindType type, int value);


    /**
     * Bind a midi CC coming from an MIDI input to the fader.
     *
     * @param input The MIDI input
     * @param channel The MIDI channel
     * @param type How to bind
     * @param value The MIDI CC or note to bind
     */
    void bind (IMidiInput input, BindType type, int channel, int value);


    /**
     * Get the trigger command,
     *
     * @return The command or null if not bound
     */
    ContinuousCommand getCommand ();


    /**
     * Handle a value update. Only for internal updates.
     *
     * @param value The new value
     */
    void handleValue (double value);
}
