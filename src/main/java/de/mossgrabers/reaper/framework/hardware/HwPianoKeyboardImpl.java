// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.IHwPianoKeyboard;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.graphics.IGraphicsContext;

import java.awt.event.MouseEvent;


/**
 * Implementation of a proxy to a fader on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwPianoKeyboardImpl implements IHwPianoKeyboard, IReaperHwControl
{
    private static final int      NOTE_START        = 36;

    private static final int []   KEYS_WHITE        =
    {
        0,
        2,
        4,
        5,
        7,
        9,
        11
    };

    private static final int []   KEYS_BLACK        =
    {
        1,
        3,
        -1,
        6,
        8,
        10,
        -1
    };

    private static final int []   WHITE_KEYS_DETECT =
    {
        -1,
        0,
        2,
        4,
        5,
        7,
        9,
        11,
        12
    };

    private final int             numKeys;
    private final HwControlLayout layout;

    private IMidiInput            midiInput;
    private boolean               isPressed;
    private double                pressedX;
    private double                pressedY;
    private int                   pressedKey        = -1;
    private int                   currentValue      = 0;

    private double                keyHeightWhite;
    private double                keyWidthWhite;
    private double                keyHeightBlack;
    private double                keyWidthBlack;
    private double                offset;
    private int                   steps;
    private double                oldWidth;
    private double                oldHeight;


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
        this.numKeys = numKeys;
        this.layout = new HwControlLayout (id);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input)
    {
        this.midiInput = input;
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

        final double width = bounds.getWidth () * scale;
        final double height = bounds.getHeight () * scale;

        if (width != this.oldWidth || height != this.oldHeight)
        {
            this.oldWidth = width;
            this.oldHeight = height;

            // Note: Formula only works for keybeds from Cx to Cx
            this.steps = this.numKeys / 12 * 7 + 1;

            this.keyHeightWhite = height;
            this.keyWidthWhite = width / this.steps;
            this.keyHeightBlack = this.keyHeightWhite / 1.6;
            this.keyWidthBlack = this.keyWidthWhite / 1.6;
            this.offset = this.keyWidthWhite - this.keyWidthBlack / 2;
        }

        final double x = bounds.getX () * scale;
        final double y = bounds.getY () * scale;

        final int activeKey = this.pressedKey - NOTE_START;

        // Draw the white keys
        for (int i = 0; i < this.steps; i++)
        {
            final ColorEx color = KEYS_WHITE[i % 7] + 12 * (i / 7) == activeKey ? ColorEx.BLUE : ColorEx.WHITE;
            final double left = x + i * this.keyWidthWhite;
            gc.fillRectangle (left, y, this.keyWidthWhite, this.keyHeightWhite, color);
            gc.strokeRectangle (left, y, this.keyWidthWhite, this.keyHeightWhite, ColorEx.BLACK);
        }

        // Draw the black keys
        for (int i = 0; i < this.steps - 1; i++)
        {
            final int scalePos = i % 7;
            if (KEYS_BLACK[scalePos] == -1)
                continue;
            final ColorEx color = KEYS_BLACK[scalePos] + 12 * (i / 7) == activeKey ? ColorEx.BLUE : ColorEx.BLACK;
            gc.fillRectangle (x + i * this.keyWidthWhite + this.offset, y, this.keyWidthBlack, this.keyHeightBlack, color);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void mouse (final int mouseEvent, final double x, final double y, final double scale)
    {
        if (this.midiInput == null)
            return;

        final Bounds bounds = this.layout.getBounds ();
        if (bounds == null)
            return;

        final double scaleX = x / scale;
        final double scaleY = y / scale;

        if (mouseEvent == MouseEvent.MOUSE_PRESSED && bounds.contains (scaleX, scaleY))
        {
            this.isPressed = true;
            this.pressedX = scaleX - bounds.getX ();
            this.pressedY = scaleY - bounds.getY ();
            this.pressedKey = NOTE_START + this.getKey (this.pressedX, this.pressedY, scale);
            this.currentValue = 0;
            this.midiInput.sendRawMidiEvent (0x90, this.pressedKey, 127);
            return;
        }

        if (!this.isPressed)
            return;

        if (mouseEvent == MouseEvent.MOUSE_RELEASED)
        {
            this.midiInput.sendRawMidiEvent (0x80, this.pressedKey, 0);
            this.isPressed = false;
            this.pressedKey = -1;
            return;
        }

        if (mouseEvent == MouseEvent.MOUSE_DRAGGED)
        {
            final double change = Math.min (3, Math.max (-3, this.pressedX - scaleX + (this.pressedY - scaleY)));
            this.pressedX = scaleX;
            this.pressedY = scaleY;

            // Simulate Aftertouch with mouse movement
            this.currentValue = (int) Math.max (0, Math.min (127, this.currentValue + change));
            this.midiInput.sendRawMidiEvent (0xA0, this.pressedKey, this.currentValue);
        }
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
        if (this.midiInput != null)
            this.midiInput.sendRawMidiEvent (isDown ? 0x90 : 0x80, note, velocity);
    }


    /**
     * Calc the key from the mouse position.
     *
     * @param x The x position relative to the widget
     * @param y The y position relative to the widget
     * @param scale The scale factor
     * @return The pressed key
     */
    private int getKey (final double x, final double y, final double scale)
    {
        final double kww = this.keyWidthWhite / scale;
        final double kwb = this.keyWidthBlack / scale;
        final double khb = this.keyHeightBlack / scale;

        // Calc white key
        final int num = (int) (x / kww);
        final int pos = num % 7 + 1;
        final int whiteKey = 12 * (num / 7) + WHITE_KEYS_DETECT[pos];

        if (y > khb) // A white key
            return whiteKey;

        // A white or black key
        // Move val to 1. key
        final int transNumX = (int) (x - num * kww);

        if (transNumX <= kwb / 2)
        { // Black key left of white key
          // Is there a black key ?
            if (WHITE_KEYS_DETECT[pos] - WHITE_KEYS_DETECT[pos - 1] == 1)
                return whiteKey; // No
            // Yes
            return 12 * (num / 7) + WHITE_KEYS_DETECT[pos] - 1;
        }

        // Black key to the right of white key
        if (transNumX >= kww - kwb / 2)
        {
            // Is there a black key ?
            if (WHITE_KEYS_DETECT[pos + 1] - WHITE_KEYS_DETECT[pos] == 1)
                return whiteKey; // No

            // Yes
            return 12 * (num / 7) + WHITE_KEYS_DETECT[pos] + 1;
        }

        // White key
        return whiteKey;
    }
}
