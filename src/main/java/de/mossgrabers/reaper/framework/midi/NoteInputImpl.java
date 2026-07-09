// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.AbstractNoteInput;
import de.mossgrabers.reaper.communication.BackendExchange;

import javax.sound.midi.MidiDevice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * Implementation of a note input.
 *
 * @author Jürgen Moßgraber
 */
public class NoteInputImpl extends AbstractNoteInput
{
    private final BackendExchange sender;
    private final MidiDevice      device;
    private final int             noteInputIndex;


    /**
     * Constructor.
     *
     * @param device The MIDI input port for which to create this note input
     * @param noteInputIndex The index of the note input of the given device
     * @param sender Interface to the C++ backend
     * @param filters a filter string formatted as hexadecimal value with '?' as wildcard. For
     *            example '80????' would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     */
    public NoteInputImpl (final MidiDevice device, final int noteInputIndex, final BackendExchange sender, final String... filters)
    {
        this.sender = sender;
        this.device = device;
        this.noteInputIndex = noteInputIndex;

        final Set<String> backendFilters = new HashSet<> ();
        if (filters.length == 0)
        {
            backendFilters.add ("90");
            backendFilters.add ("80");
            backendFilters.add ("B001");
            backendFilters.add ("B00B");
            backendFilters.add ("B040");
            backendFilters.add ("E0");
            backendFilters.add ("D0");
            backendFilters.add ("A0");
        }
        else
        {
            // Remove question marks for faster comparison
            for (final String filter: filters)
            {
                if (filter.length () != 6)
                    throw new IllegalArgumentException ("Filter must be 6 characters long!");

                final String status = filter.substring (0, 2).replace ('?', ' ').trim ();
                final String data1 = filter.substring (2, 4).replace ('?', ' ').trim ();
                final String data2 = filter.substring (4, 6).replace ('?', ' ').trim ();
                if (status.isEmpty ())
                    throw new IllegalArgumentException ("Filter has missing status!");
                if (data1.isEmpty () && !data2.isEmpty ())
                    throw new IllegalArgumentException ("First data byte filter cannot be empty if second is set!");
                if (data1.isEmpty () || data2.length () == 1)
                    throw new IllegalArgumentException ("Can only handle 2 byte data byte filters!");

                // Add MIDI channels if necessary
                final List<String> results = new ArrayList<> ();
                if (status.length () == 1)
                {
                    for (int i = 0; i <= 0xF; i++)
                        results.add (status + Integer.toHexString (i).toUpperCase (Locale.US));
                }
                else
                    results.add (status);

                // Add data byte filters
                for (final String result: results)
                {
                    String finalFilter = result;
                    if (!data1.isEmpty ())
                    {
                        finalFilter += data1;
                        if (!data2.isEmpty ())
                            finalFilter += data2;
                    }
                    backendFilters.add (finalFilter);
                }
            }
        }

        this.noteRepeat = new NoteRepeatImpl (sender);
        if (device instanceof final ReaperMidiDevice reaperMidiDevice)
            sender.setNoteInputFilters (reaperMidiDevice.getDeviceID (), this.noteInputIndex, backendFilters.toArray (new String [backendFilters.size ()]));
    }


    /** {@inheritDoc} */
    @Override
    public void setKeyTranslationTable (final int [] table)
    {
        if (this.device instanceof final ReaperMidiDevice reaperMidiDevice)
            this.sender.setNoteInputKeyTranslationTable (reaperMidiDevice.getDeviceID (), this.noteInputIndex, table);
    }


    /** {@inheritDoc} */
    @Override
    public void setVelocityTranslationTable (final int [] table)
    {
        if (this.device instanceof final ReaperMidiDevice reaperMidiDevice)
            this.sender.setNoteInputVelocityTranslationTable (reaperMidiDevice.getDeviceID (), this.noteInputIndex, table);
    }


    /** {@inheritDoc} */
    @Override
    public void enableMPE (final boolean enable)
    {
        this.isMPEEnabled = enable;
    }


    /** {@inheritDoc} */
    @Override
    public void setMPEPitchBendSensitivity (final int pitchBendRange)
    {
        this.mpePitchBendSensitivity = pitchBendRange;
    }
}
