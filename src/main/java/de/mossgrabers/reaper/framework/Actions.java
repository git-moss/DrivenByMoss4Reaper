// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework;

import java.util.HashSet;
import java.util.Set;


/**
 * Reaper action IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public final class Actions
{
    /** Previous project tab. */
    public static final int           PROJECT_TAB_PREVIOUS            = 40862;
    /** Next project tab. */
    public static final int           PROJECT_TAB_NEXT                = 40861;
    /** Save project. */
    public static final int           PROJECT_SAVE                    = 40026;
    /** Open dialog to load a project. */
    public static final int           PROJECT_LOAD                    = 40025;

    /** Transport: Tap tempo. */
    public static final int           TRANSPORT_TAP_TEMPO             = 1134;

    /** Item: Remove items. */
    public static final int           REMOVE_ITEMS                    = 40006;

    /** Record: Set record mode to normal. */
    public static final int           RECORD_MODE_NORMAL              = 40252;
    /** Record: Set record mode to selected item auto-punch. */
    public static final int           RECORD_MODE_PUNCH_ITEMS         = 40253;
    /** Record: Set record mode to time selection auto-punch. */
    public static final int           RECORD_MODE_AUTO_PUNCH          = 40076;

    /** Pre-roll: Toggle pre-roll on record. */
    public static final int           RECORD_PREROLL                  = 41819;
    /** Options: Toggle metronome. */
    public static final int           TOGGLE_METRONOME                = 40364;
    /** Options: Enable metronome. */
    public static final int           ENABLE_METRONOME                = 41745;
    /** Options: Disable metronome. */
    public static final int           DISABLE_METRONOME               = 41746;

    /** Track: Insert track from template.... */
    public static final int           INSERT_NEW_TRACK_FROM_TEMPLATE  = 46000;
    /** Track: Duplicate tracks. */
    public static final int           DUPLICATE_TRACKS                = 40062;

    /** Load the 1st window set. */
    public static final int           LOAD_WINDOW_SET_1               = 40454;
    /** Load the 2nd window set. */
    public static final int           LOAD_WINDOW_SET_2               = 40455;
    /** Load the 3rd window set. */
    public static final int           LOAD_WINDOW_SET_3               = 40456;

    /** Envelope: Toggle show all active envelopes. */
    public static final int           SHOW_ALL_ACTIVE_ENVELOPES       = 40926;
    /** View: Toggle mixer visible. */
    public static final int           TOGGLE_MIXER_VISIBLE            = 40078;
    /** View: Toggle show MIDI editor windows. */
    public static final int           TOGGLE_SHOW_MIDI_EDITOR_WINDOWS = 40716;
    /** View: Show track manager window. */
    public static final int           SHOW_TRACK_MANAGER_WINDOW       = 40906;
    /** Toggle full-screen. */
    public static final int           TOGGLE_FULLSCREEN               = 40346;
    /** Media explorer: Show/hide media explorer. */
    public static final int           TOGGLE_MEDIA_EXPLORER           = 50124;

    /** Mixer: Toggle show FX inserts if space available. */
    public static final int           TOGGLE_FX_INSERTS               = 40549;
    /** Mixer: Toggle show sends if space available. */
    public static final int           TOGGLE_FX_SENDS                 = 40557;
    /** Mixer: Toggle show FX parameters if space available. */
    public static final int           TOGGLE_FX_PARAMETERS            = 40910;

    /** Item: Dynamic split items... */
    public static final int           DYNAMIC_SPLIT                   = 40760;

    /** Track: Go to next track. */
    public static final int           GO_TO_NEXT_TRACK                = 40285;
    /** Track: Go to previous track. */
    public static final int           GO_TO_PREV_TRACK                = 40286;

    /** View: Zoom out horizontal. */
    public static final int           ZOOM_OUT_HORIZ                  = 1011;
    /** View: Zoom in horizontal. */
    public static final int           ZOOM_IN_HORIZ                   = 1012;
    /** View: Zoom out vertical. */
    public static final int           ZOOM_OUT_VERT                   = 40112;
    /** View: Zoom in vertical. */
    public static final int           ZOOM_IN_VERT                    = 40111;

    /** View: Toggle auto-view-scroll during playback */
    public static final int           TOGGLE_FOLLOW_PLAYBACK          = 40036;

    private static final Set<Integer> IGNORE_IF_TEST_IS_ACTIVE        = new HashSet<> ();
    static
    {
        IGNORE_IF_TEST_IS_ACTIVE.add (Integer.valueOf (INSERT_NEW_TRACK_FROM_TEMPLATE));
        IGNORE_IF_TEST_IS_ACTIVE.add (Integer.valueOf (PROJECT_SAVE));
        IGNORE_IF_TEST_IS_ACTIVE.add (Integer.valueOf (DYNAMIC_SPLIT));
    }

    private static boolean isTesting = false;


    /**
     * Constructor. Private due to constant class.
     */
    private Actions ()
    {
        // Intentionally empty
    }


    /**
     * Dis-/enable testing.
     *
     * @param enableTesting True to enable testing
     */
    public static void setTesting (final boolean enableTesting)
    {
        isTesting = enableTesting;
    }


    /**
     * Test if the action is blocked.
     *
     * @param actionID The ID of the action to test
     * @return True if testing is active and the action is not suitable for testing (opens dialogs)
     */
    public static boolean isBlocked (final int actionID)
    {
        return isTesting && IGNORE_IF_TEST_IS_ACTIVE.contains (Integer.valueOf (actionID));
    }
}
