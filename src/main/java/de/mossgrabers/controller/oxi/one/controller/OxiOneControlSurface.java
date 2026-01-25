// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent;
import de.mossgrabers.framework.graphics.canvas.component.LabelComponent.LabelLayout;


/**
 * The OXI One control surface.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneControlSurface extends AbstractControlSurface<OxiOneConfiguration>
{
    /** Knob 1: the velocity knob. */
    public static final int                    KNOB1_VELOCITY          = 0;
    /** Knob 2: the octave knob. */
    public static final int                    KNOB2_OCTAVE            = 1;
    /** Knob 3: the filter knob touch. */
    public static final int                    KNOB3_GATE              = 2;
    /** Knob 4: the resonance knob. */
    public static final int                    KNOB4_MODULATION        = 3;

    /** The Arpeggiator button. */
    public static final int                    MK1_BUTTON_ARP          = 0;
    /** The 4th sequencer track button. */
    public static final int                    MK1_BUTTON_SEQUENCER4   = 1;
    /** The 16/left button. */
    public static final int                    MK1_BUTTON_16_LEFT      = 2;
    /** The End button. */
    public static final int                    MK1_BUTTON_END          = 3;

    /** The keyboard/preview button. */
    public static final int                    MK1_BUTTON_KEYBOARD     = 4;
    /** The 3rd sequencer track button. */
    public static final int                    MK1_BUTTON_SEQUENCER3   = 5;
    /** The 32/up button. */
    public static final int                    MK1_BUTTON_32_UP        = 6;
    /** The Init/x2 button. */
    public static final int                    MK1_BUTTON_INIT         = 7;

    /** The arranger button. */
    public static final int                    MK1_BUTTON_ARRANGER     = 8;
    /** The 2nd sequencer track button. */
    public static final int                    MK1_BUTTON_SEQUENCER2   = 9;
    /** The 48/down button. */
    public static final int                    MK1_BUTTON_48_DOWN      = 10;
    /** The Save button. */
    public static final int                    MK1_BUTTON_SAVE         = 11;

    /** The back button. */
    public static final int                    MK1_BUTTON_BACK         = 12;
    /** The 1st sequencer track button. */
    public static final int                    MK1_BUTTON_SEQUENCER1   = 13;
    /** The 64/right button. */
    public static final int                    MK1_BUTTON_64_RIGHT     = 14;
    /** The Load button. */
    public static final int                    MK1_BUTTON_LOAD         = 15;

    /** The encoder 1 button. */
    public static final int                    MK1_BUTTON_ENCODER1     = 16;
    /** The Shift button. */
    public static final int                    MK1_BUTTON_SHIFT        = 17;
    /** The MOD button. */
    public static final int                    MK1_BUTTON_MOD          = 18;
    /** The Copy button. */
    public static final int                    MK1_BUTTON_COPY         = 19;

    /** The encoder 2 button. */
    public static final int                    MK1_BUTTON_ENCODER2     = 20;
    /** The Stop button. */
    public static final int                    MK1_BUTTON_STOP         = 21;
    /** The Division button. */
    public static final int                    MK1_BUTTON_DIVISION     = 22;
    /** The Paste button. */
    public static final int                    MK1_BUTTON_PASTE        = 23;

    /** The encoder 3 button. */
    public static final int                    MK1_BUTTON_ENCODER3     = 24;
    /** The Play button. */
    public static final int                    MK1_BUTTON_PLAY         = 25;
    /** The LFO button. */
    public static final int                    MK1_BUTTON_LFO          = 26;
    /** The Undo button. */
    public static final int                    MK1_BUTTON_UNDO         = 27;

    /** The encoder 4 button. */
    public static final int                    MK1_BUTTON_ENCODER4     = 28;
    /** The Record button. */
    public static final int                    MK1_BUTTON_REC          = 29;
    /** The Step Chord button. */
    public static final int                    MK1_BUTTON_STEP_CHORD   = 30;
    /** The Random button. */
    public static final int                    MK1_BUTTON_RANDOM       = 31;

    /** The Mute button. */
    public static final int                    MK1_BUTTON_MUTE         = 32;

    /** The Arpeggiator button. */
    public static final int                    MK2_BUTTON_ARP          = 0;
    /** The flow button. */
    public static final int                    MK2_BUTTON_FLOW         = 1;
    /** The 16/left button. */
    public static final int                    MK2_BUTTON_16_LEFT      = 2;
    /** The End button. */
    public static final int                    MK2_BUTTON_END          = 3;

    /** The keyboard/preview button. */
    public static final int                    MK2_BUTTON_KEYBOARD     = 4;
    /** The Gen button. */
    public static final int                    MK2_BUTTON_GEN          = 5;
    /** The 32/up button. */
    public static final int                    MK2_BUTTON_32_UP        = 6;
    /** The Init/x2 button. */
    public static final int                    MK2_BUTTON_INIT         = 7;

    /** The arranger button. */
    public static final int                    MK2_BUTTON_ARRANGER     = 8;
    /** The MOD button. */
    public static final int                    MK2_BUTTON_MOD          = 9;
    /** The 48/down button. */
    public static final int                    MK2_BUTTON_48_DOWN      = 10;
    /** The Save button. */
    public static final int                    MK2_BUTTON_SAVE         = 11;

    /** The back button. */
    public static final int                    MK2_BUTTON_BACK         = 12;
    /** The Perf button. */
    public static final int                    MK2_BUTTON_PERF         = 13;
    /** The 64/right button. */
    public static final int                    MK2_BUTTON_64_RIGHT     = 14;
    /** The Load button. */
    public static final int                    MK2_BUTTON_LOAD         = 15;

    /** The encoder 1 button. */
    public static final int                    MK2_BUTTON_ENCODER1     = 16;
    /** The Shift button. */
    public static final int                    MK2_BUTTON_SHIFT        = 17;
    /** The page button. */
    public static final int                    MK2_BUTTON_PAGE         = 18;
    /** The Copy button. */
    public static final int                    MK2_BUTTON_COPY         = 19;

    /** The encoder 2 button. */
    public static final int                    MK2_BUTTON_ENCODER2     = 20;
    /** The Stop button. */
    public static final int                    MK2_BUTTON_STOP         = 21;
    /** The Division button. */
    public static final int                    MK2_BUTTON_TRACK        = 22;
    /** The Paste button. */
    public static final int                    MK2_BUTTON_PASTE        = 23;

    /** The encoder 3 button. */
    public static final int                    MK2_BUTTON_ENCODER3     = 24;
    /** The Play button. */
    public static final int                    MK2_BUTTON_PLAY         = 25;
    /** The LFO button. */
    public static final int                    MK2_BUTTON_LFO          = 26;
    /** The Undo button. */
    public static final int                    MK2_BUTTON_UNDO         = 27;

    /** The encoder 4 button. */
    public static final int                    MK2_BUTTON_ENCODER4     = 28;
    /** The Record button. */
    public static final int                    MK2_BUTTON_REC          = 29;
    /** The Step Chord button. */
    public static final int                    MK2_BUTTON_STEP_CHORD   = 30;
    /** The Random button. */
    public static final int                    MK2_BUTTON_RANDOM       = 31;

    /** The Mute button. */
    public static final int                    MK2_BUTTON_MUTE         = 32;

    /** The 1st sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER1   = 33;
    /** The 2nd sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER2   = 34;
    /** The 3rd sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER3   = 35;
    /** The 4th sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER4   = 36;
    /** The 5th sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER5   = 37;
    /** The 6th sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER6   = 38;
    /** The 7th sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER7   = 39;
    /** The 8th sequencer track button. */
    public static final int                    MK2_BUTTON_SEQUENCER8   = 40;

    private static final int                   MK1_LEDS_BACK           = 0;
    private static final int                   MK1_LEDS_CONFIG         = 1;
    private static final int                   MK1_LEDS_ARRANGER_SHOW  = 2;
    private static final int                   MK1_LEDS_ARRANGER_STATE = 3;
    private static final int                   MK1_LEDS_KEYBOARD       = 4;
    private static final int                   MK1_LEDS_PREVIEW        = 5;
    private static final int                   MK1_LEDS_ARP            = 6;
    private static final int                   MK1_LEDS_ARP_HOLD       = 7;
    private static final int                   MK1_LEDS_SHIFT          = 8;
    private static final int                   MK1_LEDS_STOP           = 9;
    private static final int                   MK1_LEDS_SEQ_1          = 10;
    private static final int                   MK1_LEDS_REC            = 11;
    private static final int                   MK1_LEDS_PLAY           = 12;
    private static final int                   MK1_LEDS_SEQ_3          = 13;
    private static final int                   MK1_LEDS_NUDGE          = 14;
    private static final int                   MK1_LEDS_SYNC           = 15;
    private static final int                   MK1_LEDS_SEQ_1_SEL      = 16;
    private static final int                   MK1_LEDS_SEQ_2          = 17;
    private static final int                   MK1_LEDS_SEQ_2_SEL      = 18;
    private static final int                   MK1_LEDS_MUTE           = 19;
    private static final int                   MK1_LEDS_LOAD           = 20;
    private static final int                   MK1_LEDS_SEQ_4          = 21;
    private static final int                   MK1_LEDS_SEQ_4_SEL      = 22;
    private static final int                   MK1_LEDS_SEQ_3_SEL      = 23;
    private static final int                   MK1_LEDS_SAVE           = 24;
    private static final int                   MK1_LEDS_CLEAR          = 25;
    private static final int                   MK1_LEDS_DUPLICATE      = 26;
    private static final int                   MK1_LEDS_PASTE          = 27;
    private static final int                   MK1_LEDS_COPY           = 28;
    private static final int                   MK1_LEDS_UNDO           = 29;
    private static final int                   MK1_LEDS_RANDOM         = 30;
    private static final int                   MK1_LEDS_REDO           = 31;
    private static final int                   MK1_LEDS_RANDOM2        = 32;
    private static final int                   MK1_LEDS_INIT           = 33;
    private static final int                   MK1_LEDS_X2             = 34;
    private static final int                   MK1_LEDS_END            = 35;
    private static final int                   MK1_LEDS_2              = 36;
    private static final int                   MK1_LEDS_MOD            = 37;
    private static final int                   MK1_LEDS_DIVISION       = 38;
    private static final int                   MK1_LEDS_FOLLOW         = 39;
    private static final int                   MK1_LEDS_LFO            = 40;
    private static final int                   MK1_LEDS_CONDENSE       = 41;
    private static final int                   MK1_LEDS_CVOUT          = 42;
    private static final int                   MK1_LEDS_STEP_CHORD     = 43;
    private static final int                   MK1_LEDS_EXPAND         = 44;
    private static final int                   MK1_LEDS_16             = 45;
    private static final int                   MK1_LEDS_LEFT           = 46;
    private static final int                   MK1_LEDS_32             = 47;
    private static final int                   MK1_LEDS_UP             = 48;
    private static final int                   MK1_LEDS_48             = 49;
    private static final int                   MK1_LEDS_RIGHT          = 50;
    private static final int                   MK1_LEDS_64             = 51;
    private static final int                   MK1_LEDS_DOWN           = 52;
    private static final int                   MK1_LEDS_NO_LED         = 53;
    private static final int                   MK1_LEDS_PLAY2          = 54;

    private static final int                   MK2_LEDS_BACK           = 0;
    private static final int                   MK2_LEDS_CONFIG         = 1;
    private static final int                   MK2_LEDS_ARRANGER_SHOW  = 2;
    private static final int                   MK2_LEDS_ARRANGER_STATE = 3;
    private static final int                   MK2_LEDS_KEYBOARD       = 4;
    private static final int                   MK2_LEDS_PREVIEW        = 5;
    private static final int                   MK2_LEDS_ARP            = 6;
    private static final int                   MK2_LEDS_ARP_HOLD       = 7;
    private static final int                   MK2_LEDS_SHIFT_2        = 8;
    private static final int                   MK2_LEDS_SHIFT          = 9;
    private static final int                   MK2_LEDS_STOP           = 10;
    private static final int                   MK2_LEDS_SYNC           = 11;
    private static final int                   MK2_LEDS_REC            = 12;
    private static final int                   MK2_LEDS_REC_2          = 13;
    private static final int                   MK2_LEDS_PLAY           = 14;
    private static final int                   MK2_LEDS_TAP            = 15;
    private static final int                   MK2_LEDS_MUTE           = 16;
    private static final int                   MK2_LEDS_NUDGE          = 17;
    private static final int                   MK2_LEDS_FLOW           = 18;
    private static final int                   MK2_LEDS_GROOVE         = 19;
    private static final int                   MK2_LEDS_GEN            = 20;
    private static final int                   MK2_LEDS_CVOUT          = 21;
    private static final int                   MK2_LEDS_MOD            = 22;
    private static final int                   MK2_LEDS_EXT_MOD        = 23;
    private static final int                   MK2_LEDS_PERF           = 24;
    private static final int                   MK2_LEDS_PROJ           = 25;
    private static final int                   MK2_LEDS_UNDO           = 26;
    private static final int                   MK2_LEDS_REDO           = 27;
    private static final int                   MK2_LEDS_COPY           = 28;
    private static final int                   MK2_LEDS_DUPLICATE      = 29;
    private static final int                   MK2_LEDS_LOAD           = 30;
    private static final int                   MK2_LEDS_LOAD_2         = 31;
    private static final int                   MK2_LEDS_SAVE           = 32;
    private static final int                   MK2_LEDS_SAVE_2         = 33;
    private static final int                   MK2_LEDS_PASTE          = 34;
    private static final int                   MK2_LEDS_CLEAR          = 35;
    private static final int                   MK2_LEDS_RANDOM         = 36;
    private static final int                   MK2_LEDS_RANDOM2        = 37;
    private static final int                   MK2_LEDS_LFO            = 38;
    private static final int                   MK2_LEDS_CONDENSE       = 39;
    private static final int                   MK2_LEDS_PAGE           = 40;
    private static final int                   MK2_LEDS_EXPAND         = 41;
    private static final int                   MK2_LEDS_INIT           = 42;
    private static final int                   MK2_LEDS_X2             = 43;
    private static final int                   MK2_LEDS_END            = 44;
    private static final int                   MK2_LEDS_2              = 45;
    private static final int                   MK2_LEDS_TRACK          = 46;
    private static final int                   MK2_LEDS_FOLLOW         = 47;
    private static final int                   MK2_LEDS_STEP           = 48;
    private static final int                   MK2_LEDS_CHORD          = 49;
    private static final int                   MK2_LEDS_64             = 50;
    private static final int                   MK2_LEDS_RIGHT          = 51;
    private static final int                   MK2_LEDS_48             = 52;
    private static final int                   MK2_LEDS_DOWN           = 53;
    private static final int                   MK2_LEDS_32             = 54;
    private static final int                   MK2_LEDS_UP             = 55;
    private static final int                   MK2_LEDS_16             = 56;
    private static final int                   MK2_LEDS_LEFT           = 57;
    private static final int                   MK2_LEDS_SEQ_1          = 58;
    private static final int                   MK2_LEDS_SEQ_2          = 59;
    private static final int                   MK2_LEDS_SEQ_3          = 60;
    private static final int                   MK2_LEDS_SEQ_4          = 61;
    private static final int                   MK2_LEDS_SEQ_5          = 62;
    private static final int                   MK2_LEDS_SEQ_6          = 63;
    private static final int                   MK2_LEDS_SEQ_7          = 64;
    private static final int                   MK2_LEDS_SEQ_8          = 65;
    private static final int                   MK2_LEDS_SEQ_1_SEL      = 66;
    private static final int                   MK2_LEDS_SEQ_2_SEL      = 67;
    private static final int                   MK2_LEDS_SEQ_3_SEL      = 68;
    private static final int                   MK2_LEDS_SEQ_4_SEL      = 69;
    private static final int                   MK2_LEDS_SEQ_5_SEL      = 70;
    private static final int                   MK2_LEDS_SEQ_6_SEL      = 71;
    private static final int                   MK2_LEDS_SEQ_7_SEL      = 72;
    private static final int                   MK2_LEDS_SEQ_8_SEL      = 73;

    private static final List<Integer>         MK1_NORMAL_LEDS         = new ArrayList<> ();
    private static final List<Integer>         MK2_NORMAL_LEDS         = new ArrayList<> ();
    private static final List<Integer>         MK1_SHIFTED_LEDS        = new ArrayList<> ();
    private static final List<Integer>         MK2_SHIFTED_LEDS        = new ArrayList<> ();
    private static final Map<Integer, Integer> MK1_BUTTON_LEDS         = new HashMap<> ();
    private static final Map<Integer, Integer> MK2_BUTTON_LEDS         = new HashMap<> ();
    private static final Map<Integer, Integer> MK1_SHIFTED_BUTTON_LEDS = new HashMap<> ();
    private static final Map<Integer, Integer> MK2_SHIFTED_BUTTON_LEDS = new HashMap<> ();

    static
    {
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_BACK));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_ARRANGER_SHOW));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_KEYBOARD));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_ARP_HOLD));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_STOP));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_1));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_2));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_3));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_4));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_PLAY));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_MUTE));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_PASTE));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_DUPLICATE));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_UNDO));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_RANDOM));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_INIT));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_END));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_MOD));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_DIVISION));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_LFO));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_STEP_CHORD));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_16));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_32));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_48));
        MK1_NORMAL_LEDS.add (Integer.valueOf (MK1_LEDS_64));

        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_CONFIG));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_ARRANGER_STATE));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_PREVIEW));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_ARP));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_SYNC));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_1_SEL));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_2_SEL));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_3_SEL));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_SEQ_4_SEL));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_PLAY2));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_NUDGE));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_CLEAR));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_COPY));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_REDO));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_RANDOM2));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_X2));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_2));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_FOLLOW));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_EXPAND));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_CVOUT));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_CONDENSE));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_LEFT));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_UP));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_RIGHT));
        MK1_SHIFTED_LEDS.add (Integer.valueOf (MK1_LEDS_DOWN));

        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_BACK));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_ARRANGER_SHOW));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_KEYBOARD));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_ARP));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SHIFT_2));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_STOP));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_REC));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_PLAY));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_MUTE));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_FLOW));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_GEN));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_MOD));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_PERF));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_UNDO));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_COPY));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_LOAD));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SAVE));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_PASTE));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_RANDOM));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_LFO));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_PAGE));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_INIT));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_END));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_TRACK));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_STEP));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_64));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_48));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_32));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_16));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_1));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_2));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_3));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_4));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_5));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_6));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_7));
        MK2_NORMAL_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_8));

        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_CONFIG));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_ARRANGER_STATE));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_PREVIEW));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_ARP_HOLD));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SHIFT));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SYNC));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_REC_2));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_TAP));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_NUDGE));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_GROOVE));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_CVOUT));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_EXT_MOD));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_PROJ));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_REDO));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_DUPLICATE));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_LOAD_2));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SAVE_2));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_CLEAR));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_RANDOM2));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_CONDENSE));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_EXPAND));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_X2));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_2));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_FOLLOW));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_CHORD));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_RIGHT));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_DOWN));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_UP));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_LEFT));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_1_SEL));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_2_SEL));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_3_SEL));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_4_SEL));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_5_SEL));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_6_SEL));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_7_SEL));
        MK2_SHIFTED_LEDS.add (Integer.valueOf (MK2_LEDS_SEQ_8_SEL));

        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SHIFT), Integer.valueOf (MK1_LEDS_SHIFT));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_BACK), Integer.valueOf (MK1_LEDS_BACK));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_STOP), Integer.valueOf (MK1_LEDS_STOP));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_REC), Integer.valueOf (MK1_LEDS_REC));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_PLAY), Integer.valueOf (MK1_LEDS_PLAY));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_UNDO), Integer.valueOf (MK1_LEDS_UNDO));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_LOAD), Integer.valueOf (MK1_LEDS_LOAD));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SAVE), Integer.valueOf (MK1_LEDS_SAVE));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_COPY), Integer.valueOf (MK1_LEDS_COPY));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_PASTE), Integer.valueOf (MK1_LEDS_PASTE));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_16_LEFT), Integer.valueOf (MK1_LEDS_16));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_32_UP), Integer.valueOf (MK1_LEDS_32));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_48_DOWN), Integer.valueOf (MK1_LEDS_48));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_64_RIGHT), Integer.valueOf (MK1_LEDS_64));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER1), Integer.valueOf (MK1_LEDS_SEQ_1));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER2), Integer.valueOf (MK1_LEDS_SEQ_2));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER3), Integer.valueOf (MK1_LEDS_SEQ_3));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER4), Integer.valueOf (MK1_LEDS_SEQ_4));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_MUTE), Integer.valueOf (MK1_LEDS_MUTE));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_ARRANGER), Integer.valueOf (MK1_LEDS_ARRANGER_SHOW));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_KEYBOARD), Integer.valueOf (MK1_LEDS_KEYBOARD));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_ARP), Integer.valueOf (MK1_LEDS_ARP));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_MOD), Integer.valueOf (MK1_LEDS_MOD));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_LFO), Integer.valueOf (MK1_LEDS_LFO));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_STEP_CHORD), Integer.valueOf (MK1_LEDS_STEP_CHORD));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_DIVISION), Integer.valueOf (MK1_LEDS_DIVISION));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_INIT), Integer.valueOf (MK1_LEDS_INIT));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_END), Integer.valueOf (MK1_LEDS_END));
        MK1_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_RANDOM), Integer.valueOf (MK1_LEDS_RANDOM));

        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SHIFT), Integer.valueOf (MK1_LEDS_SHIFT));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_STOP), Integer.valueOf (MK1_LEDS_SYNC));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_REC), Integer.valueOf (MK1_LEDS_NO_LED));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_PLAY), Integer.valueOf (MK1_LEDS_PLAY2));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_UNDO), Integer.valueOf (MK1_LEDS_REDO));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_COPY), Integer.valueOf (MK1_LEDS_DUPLICATE));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_16_LEFT), Integer.valueOf (MK1_LEDS_LEFT));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_32_UP), Integer.valueOf (MK1_LEDS_UP));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_48_DOWN), Integer.valueOf (MK1_LEDS_DOWN));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_64_RIGHT), Integer.valueOf (MK1_LEDS_RIGHT));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER1), Integer.valueOf (MK1_LEDS_SEQ_1_SEL));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER2), Integer.valueOf (MK1_LEDS_SEQ_2_SEL));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER3), Integer.valueOf (MK1_LEDS_SEQ_3_SEL));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_SEQUENCER4), Integer.valueOf (MK1_LEDS_SEQ_4_SEL));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_MUTE), Integer.valueOf (MK1_LEDS_NUDGE));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_KEYBOARD), Integer.valueOf (MK1_LEDS_PREVIEW));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_ARP), Integer.valueOf (MK1_LEDS_ARP_HOLD));
        MK1_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK1_BUTTON_RANDOM), Integer.valueOf (MK1_LEDS_RANDOM2));

        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SHIFT), Integer.valueOf (MK2_LEDS_SHIFT));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_BACK), Integer.valueOf (MK2_LEDS_BACK));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_STOP), Integer.valueOf (MK2_LEDS_STOP));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_REC), Integer.valueOf (MK2_LEDS_REC));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PLAY), Integer.valueOf (MK2_LEDS_PLAY));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_UNDO), Integer.valueOf (MK2_LEDS_UNDO));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_LOAD), Integer.valueOf (MK2_LEDS_LOAD));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SAVE), Integer.valueOf (MK2_LEDS_SAVE));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_COPY), Integer.valueOf (MK2_LEDS_COPY));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PASTE), Integer.valueOf (MK2_LEDS_PASTE));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_16_LEFT), Integer.valueOf (MK2_LEDS_16));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_32_UP), Integer.valueOf (MK2_LEDS_32));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_48_DOWN), Integer.valueOf (MK2_LEDS_48));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_64_RIGHT), Integer.valueOf (MK2_LEDS_64));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_MUTE), Integer.valueOf (MK2_LEDS_MUTE));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_ARRANGER), Integer.valueOf (MK2_LEDS_ARRANGER_SHOW));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_KEYBOARD), Integer.valueOf (MK2_LEDS_KEYBOARD));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_ARP), Integer.valueOf (MK2_LEDS_ARP));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_MOD), Integer.valueOf (MK2_LEDS_MOD));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_LFO), Integer.valueOf (MK2_LEDS_LFO));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_STEP_CHORD), Integer.valueOf (MK2_LEDS_STEP));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_INIT), Integer.valueOf (MK2_LEDS_INIT));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_END), Integer.valueOf (MK2_LEDS_END));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_RANDOM), Integer.valueOf (MK2_LEDS_RANDOM));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_FLOW), Integer.valueOf (MK2_LEDS_FLOW));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_GEN), Integer.valueOf (MK2_LEDS_GEN));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PERF), Integer.valueOf (MK2_LEDS_PERF));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PAGE), Integer.valueOf (MK2_LEDS_PAGE));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_TRACK), Integer.valueOf (MK2_LEDS_TRACK));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_MUTE), Integer.valueOf (MK2_LEDS_MUTE));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER1), Integer.valueOf (MK2_LEDS_SEQ_1));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER2), Integer.valueOf (MK2_LEDS_SEQ_2));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER3), Integer.valueOf (MK2_LEDS_SEQ_3));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER4), Integer.valueOf (MK2_LEDS_SEQ_4));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER5), Integer.valueOf (MK2_LEDS_SEQ_5));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER6), Integer.valueOf (MK2_LEDS_SEQ_6));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER7), Integer.valueOf (MK2_LEDS_SEQ_7));
        MK2_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER8), Integer.valueOf (MK2_LEDS_SEQ_8));

        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SHIFT), Integer.valueOf (MK2_LEDS_SHIFT_2));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_BACK), Integer.valueOf (MK2_LEDS_CONFIG));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_STOP), Integer.valueOf (MK2_LEDS_SYNC));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_REC), Integer.valueOf (MK2_LEDS_REC_2));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PLAY), Integer.valueOf (MK2_LEDS_TAP));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_UNDO), Integer.valueOf (MK2_LEDS_REDO));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_LOAD), Integer.valueOf (MK2_LEDS_LOAD_2));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SAVE), Integer.valueOf (MK2_LEDS_SAVE_2));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_COPY), Integer.valueOf (MK2_LEDS_DUPLICATE));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PASTE), Integer.valueOf (MK2_LEDS_CLEAR));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_16_LEFT), Integer.valueOf (MK2_LEDS_LEFT));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_32_UP), Integer.valueOf (MK2_LEDS_UP));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_48_DOWN), Integer.valueOf (MK2_LEDS_DOWN));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_64_RIGHT), Integer.valueOf (MK2_LEDS_RIGHT));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_MUTE), Integer.valueOf (MK2_LEDS_NUDGE));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_ARRANGER), Integer.valueOf (MK2_LEDS_ARRANGER_STATE));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_KEYBOARD), Integer.valueOf (MK2_LEDS_PREVIEW));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_ARP), Integer.valueOf (MK2_LEDS_ARP_HOLD));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_MOD), Integer.valueOf (MK2_LEDS_EXT_MOD));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_LFO), Integer.valueOf (MK2_LEDS_CONDENSE));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_STEP_CHORD), Integer.valueOf (MK2_LEDS_CHORD));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_INIT), Integer.valueOf (MK2_LEDS_X2));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_END), Integer.valueOf (MK2_LEDS_2));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_RANDOM), Integer.valueOf (MK2_LEDS_RANDOM2));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_FLOW), Integer.valueOf (MK2_LEDS_GROOVE));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_GEN), Integer.valueOf (MK2_LEDS_CVOUT));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PERF), Integer.valueOf (MK2_LEDS_PROJ));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_PAGE), Integer.valueOf (MK2_LEDS_EXPAND));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_TRACK), Integer.valueOf (MK2_LEDS_FOLLOW));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_MUTE), Integer.valueOf (MK2_LEDS_NUDGE));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER1), Integer.valueOf (MK2_LEDS_SEQ_1_SEL));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER2), Integer.valueOf (MK2_LEDS_SEQ_2_SEL));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER3), Integer.valueOf (MK2_LEDS_SEQ_3_SEL));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER4), Integer.valueOf (MK2_LEDS_SEQ_4_SEL));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER5), Integer.valueOf (MK2_LEDS_SEQ_5_SEL));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER6), Integer.valueOf (MK2_LEDS_SEQ_6_SEL));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER7), Integer.valueOf (MK2_LEDS_SEQ_7_SEL));
        MK2_SHIFTED_BUTTON_LEDS.put (Integer.valueOf (MK2_BUTTON_SEQUENCER8), Integer.valueOf (MK2_LEDS_SEQ_8_SEL));
    }

    private static final byte []        ENTER_REMOTE_MODE =
    {
        0x06,
        0x55,
        (byte) 0xF7
    };

    private static final byte []        EXIT_REMOTE_MODE  =
    {
        0x00,
        (byte) 0xF7
    };

    private final byte []               sysexHeader       =
    {
        (byte) 0xF0,
        0x00,
        0x21,
        0x5B,
        0x00,
        0x01,
    };

    private final byte []               ledUpdate         =
    {
        0x02,
        0x00,
        0x00,
        0x00,
        0x00,
        (byte) 0xF7
    };

    private String                      acknowledge       = "f000215b00010653f7";

    private final boolean []            buttonStates;

    private final List<Integer>         normalLEDS;
    private final List<Integer>         shiftedLEDS;
    private final Map<Integer, Integer> buttonLEDS;
    private final Map<Integer, Integer> shiftedButtonLEDS;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param isMk2 True if it is Mk2 otherwise Mk1
     */
    public OxiOneControlSurface (final IHost host, final ColorManager colorManager, final OxiOneConfiguration configuration, final IMidiOutput output, final IMidiInput input, final boolean isMk2)
    {
        super (host, configuration, colorManager, output, input, new OxiOnePadGrid (colorManager, output, configuration, isMk2), 290, 140);

        this.acknowledge = isMk2 ? "f000215b01010653f7" : "f000215b00010653f7";

        this.normalLEDS = isMk2 ? MK2_NORMAL_LEDS : MK1_NORMAL_LEDS;
        this.shiftedLEDS = isMk2 ? MK2_SHIFTED_LEDS : MK1_SHIFTED_LEDS;
        this.buttonLEDS = isMk2 ? MK2_BUTTON_LEDS : MK1_BUTTON_LEDS;
        this.shiftedButtonLEDS = isMk2 ? MK2_SHIFTED_BUTTON_LEDS : MK1_SHIFTED_BUTTON_LEDS;

        this.buttonStates = new boolean [isMk2 ? 74 : 55];

        this.sysexHeader[4] = (byte) (isMk2 ? 0x01 : 0x00);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /**
     * Enable the MIDI remote mode on the OXI One.
     */
    public void enterRemoteMode ()
    {
        this.output.sendSysex (this.sysexHeader, ENTER_REMOTE_MODE);
    }


    /**
     * Disable the MIDI remote mode on the OXI One.
     */
    public void exitRemoteMode ()
    {
        this.output.sendSysex (this.sysexHeader, EXIT_REMOTE_MODE);
    }


    /** {@inheritDoc} */
    @Override
    protected void flushHardware ()
    {
        super.flushHardware ();

        ((OxiOnePadGrid) this.padGrid).flush ();
    }


    /** {@inheritDoc} */
    @Override
    protected void internalShutdown ()
    {
        final IGraphicDisplay display = this.getGraphicsDisplay ();
        display.addElement (new LabelComponent ("Goodbye", null, ColorEx.BLACK, false, false, LabelLayout.PLAIN));
        display.send ();

        this.exitRemoteMode ();

        super.internalShutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public OxiOnePadGrid getPadGrid ()
    {
        return (OxiOnePadGrid) this.padGrid;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDeletePressed ()
    {
        // Use mode button as delete button for resetting knobs
        final boolean pressed = this.isPressed (ButtonID.BANK_RIGHT);
        if (pressed)
            this.setTriggerConsumed (ButtonID.BANK_RIGHT);
        return pressed;
    }


    /**
     * Update all buttons which have 2 states depending on the state of the shift button.
     */
    public void updateFunctionButtonLEDs ()
    {
        final boolean isShifted = this.isShiftPressed ();
        for (int i = 0; i < this.buttonStates.length; i++)
        {
            if (this.normalLEDS.contains (Integer.valueOf (i)) && this.buttonStates[i])
                this.updateLED (i, !isShifted);
            else if (this.shiftedLEDS.contains (Integer.valueOf (i)) && this.buttonStates[i])
                this.updateLED (i, isShifted);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        // There are different MIDI CCs for the LEDs depending on the state of the Shift button!
        final Integer integerCC = Integer.valueOf (cc);

        final Integer normalLedCC = this.buttonLEDS.get (integerCC);
        final Integer shiftedLedCC = this.shiftedButtonLEDS.get (integerCC);
        if (normalLedCC == null)
            return;

        // Store the state for updating the buttons with a double LED when the Shift button is used
        // 1st bit represents the state of the shifted LED (on/off)
        final int intNormalLedCCValue = normalLedCC.intValue ();
        this.buttonStates[intNormalLedCCValue] = (value & 1) > 0;
        // 2nd bit represents the state of the shifted LED (on/off)
        if (shiftedLedCC != null)
            this.buttonStates[shiftedLedCC.intValue ()] = (value & 2) > 0;

        if (this.isShiftPressed ())
        {
            if (shiftedLedCC != null)
                this.updateLED (shiftedLedCC.intValue (), this.buttonStates[shiftedLedCC.intValue ()]);
        }
        else
            this.updateLED (intNormalLedCCValue, this.buttonStates[intNormalLedCCValue]);
    }


    /**
     * Update an LED on the device.
     *
     * @param ledID The ID of the LED
     * @param enabled True to turn on the LED otherwise turn it off
     */
    private void updateLED (final int ledID, final boolean enabled)
    {
        this.ledUpdate[1] = (byte) (ledID / 16);
        this.ledUpdate[2] = (byte) (ledID % 16);
        this.ledUpdate[4] = (byte) (enabled ? 1 : 0);
        this.output.sendSysex (this.sysexHeader, this.ledUpdate);
    }


    /**
     * Handle incoming system exclusive messages.
     *
     * @param data The data of the system exclusive message
     */
    private void handleSysEx (final String data)
    {
        if (this.acknowledge.equalsIgnoreCase (data))
            this.scheduleTask (this::forceFlush, 3000);
    }
}