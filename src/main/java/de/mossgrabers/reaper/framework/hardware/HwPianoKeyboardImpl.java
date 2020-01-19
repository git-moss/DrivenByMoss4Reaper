// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
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

    private static final int []   KEYS_WHITE =
    {
        0,
        2,
        4,
        5,
        7,
        9,
        11
    };

    private static final int []   KEYS_BLACK =
    {
        1,
        3,
        -1,
        6,
        8,
        10,
        -1
    };

    private final int             numKeys;
    private final HwControlLayout layout;

    private IMidiInput            input;

    private int                   activeKey  = -1;
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

        // Draw the white keys
        for (int i = 0; i < this.steps; i++)
        {
            final ColorEx color = KEYS_WHITE[i % 7] + 12 * (i / 7) == this.activeKey ? ColorEx.BLUE : ColorEx.WHITE;
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
            final ColorEx color = KEYS_BLACK[scalePos] + 12 * (i / 7) == this.activeKey ? ColorEx.BLUE : ColorEx.BLACK;
            gc.fillRectangle (x + i * this.keyWidthWhite + this.offset, y, this.keyWidthBlack, this.keyHeightBlack, color);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void mouse (final int mouseEvent, final double x, final double y)
    {
        // TODO
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


    /**
     * Calc the key from the mouse position.
     */
    private int getKey (final int x, final int y)
    {
        final int [] whiteKeys =
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

        // Calc white key
        final int num = (int) (x / this.keyWidthWhite);
        final int pos = num % 7 + 1;
        final int whiteKey = 12 * (num / 7) + whiteKeys[pos];

        if (y > this.keyHeightBlack) // A white key
            return whiteKey;

        // A white or black key
        // Move val to 1. key
        final int transNumX = (int) (x - num * this.keyWidthWhite);

        if (transNumX <= this.keyWidthBlack / 2)
        { // Black key left of white key
          // Is there a black key ?
            if (whiteKeys[pos] - whiteKeys[pos - 1] == 1)
                return whiteKey; // No
            // Yes
            return 12 * (num / 7) + whiteKeys[pos] - 1;
        }

        // Black key to the right of white key
        if (transNumX >= this.keyWidthWhite - this.keyWidthBlack / 2)
        {
            // Is there a black key ?
            if (whiteKeys[pos + 1] - whiteKeys[pos] == 1)
                return whiteKey; // No

            // Yes
            return 12 * (num / 7) + whiteKeys[pos] + 1;
        }

        // White key
        return whiteKey;
    }
}
