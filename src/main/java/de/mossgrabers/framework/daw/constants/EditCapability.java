// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

/**
 * Edit capabilities of the hosts API.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum EditCapability
{
    /** The API provides support for adding and deleting markers. */
    MARKERS,

    /** The API provides support for editing note repeat note lengths. */
    NOTE_REPEAT_LENGTH,
    /** The API provides support for editing note repeat swing factor. */
    NOTE_REPEAT_SWING,
    /** The API provides support for editing note repeat velocity ramp. */
    NOTE_REPEAT_VELOCITY_RAMP,

    /** The API provides support for editing note release velocity. */
    NOTE_EDIT_RELEASE_VELOCITY,
    /** The API provides support for editing note pressure. */
    NOTE_EDIT_PRESSURE,
    /** The API provides support for editing note timbre. */
    NOTE_EDIT_TIMBRE,
    /** The API provides support for editing note panorama. */
    NOTE_EDIT_PANORAMA,
    /** The API provides support for editing note transpose. */
    NOTE_EDIT_TRANSPOSE,

    /** The API provides support quantizing the note lengths of MIDI input. */
    QUANTIZE_INPUT_NOTE_LENGTH,
    /** The API provides support quantizing notes by an amount percentage. */
    QUANTIZE_AMOUNT
}
