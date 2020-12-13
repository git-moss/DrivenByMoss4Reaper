// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.communication;

/**
 * The available message processors.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum Processor
{
    /** The transport. */
    TRANSPORT,
    /** The project. */
    PROJECT,
    /** The track. */
    TRACK,
    /** The master track. */
    MASTER,
    /** The device. */
    DEVICE,
    /** The browser. */
    BROWSER,
    /** The marker. */
    MARKER,
    /** The clip. */
    CLIP,
    /** The session. */
    SESSION,
    /** The scene. */
    SCENE,
    /** The note repeat. */
    NOTEREPEAT,
    /** The playing notes. */
    PLAYINGNOTES,
    /** The groove. */
    GROOVE,

    /** Single commands - play. */
    PLAY,
    /** Single commands - stop. */
    STOP,
    /** Single commands - record. */
    RECORD,
    /** Single commands - repeat. */
    REPEAT,
    /** Single commands - time. */
    TIME,
    /** Single commands - tempo. */
    TEMPO,
    /** Single commands - action. */
    ACTION,
    /** Single commands - quantize. */
    QUANTIZE,
    /** Single commands - metro volume. */
    METRO_VOL,
    /** Single commands - undo. */
    UNDO,
    /** Single commands - redo. */
    REDO,
    /** Single commands - cursor. */
    CURSOR,
    /** Single commands - refresh. */
    REFRESH,
    /** Single commands - inifile. */
    INIFILE;
}
