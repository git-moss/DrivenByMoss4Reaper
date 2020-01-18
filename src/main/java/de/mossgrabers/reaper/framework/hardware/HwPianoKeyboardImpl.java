// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.IHwPianoKeyboard;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.graphics.IGraphicsContext;


/**
 * Implementation of a proxy to a fader on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwPianoKeyboardImpl implements IHwPianoKeyboard, IReaperHwControl
{
    private final HwControlLayout layout;
    private IMidiInput            input;


    /**
     * Constructor.
     *
     * @param id The ID of the control
     * @param numKeys The number of keys to display
     * @param octave The octave of the first key
     * @param startKeyInOctave The start key
     */
    public HwPianoKeyboardImpl (final String id, final int numKeys, final int octave, final int startKeyInOctave)
    {
        this.layout = new HwControlLayout (id);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input)
    {
        this.input = input;
    }


    /** {@inheritDoc} */
    @Override
    public void update ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public String getLabel ()
    {
        return "Keyboard";
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.layout.setBounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double scale)
    {
        final Bounds bounds = this.layout.getBounds ();
        if (bounds == null)
            return;
        // TODO Draw the piano
        gc.fillRectangle (bounds.getX () * scale, bounds.getY () * scale, bounds.getWidth () * scale, bounds.getHeight () * scale, ColorEx.BLACK);
    }


    /**
     * Execute a MIDI note on/off command.
     *
     * @param isDown True to send a note on command
     * @param note The note
     * @param velocity The velocity of the note
     */
    public void sendNoteEvent (final boolean isDown, final int note, final int velocity)
    {
        if (this.input != null)
            this.input.sendRawMidiEvent (isDown ? 0x90 : 0x80, note, velocity);
    }
}
