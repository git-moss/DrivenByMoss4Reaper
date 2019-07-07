// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.midi;

import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;


/**
 * Implementation of a note input.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteInputImpl implements INoteInput
{
    private Integer []  keyTranslationTable;
    private Integer []  velocityTranslationTable;
    private Set<String> filters = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param filters a filter string formatted as hexadecimal value with `?` as wildcard. For
     *            example `80????` would match note-off on channel 1 (0). When this parameter is
     *            {@null}, a standard filter will be used to forward note-related messages on
     *            channel 1 (0).
     */
    public NoteInputImpl (final String... filters)
    {
        if (filters.length == 0)
        {
            this.filters.add ("90");
            this.filters.add ("80");
        }
        else
        {
            // Remove questionmarks for faster comparison
            for (final String filter: filters)
                this.filters.add (filter.replace ('?', ' ').trim ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setKeyTranslationTable (final Integer [] table)
    {
        this.keyTranslationTable = table;
    }


    /** {@inheritDoc} */
    @Override
    public void setVelocityTranslationTable (final Integer [] table)
    {
        this.velocityTranslationTable = table;
    }


    /**
     * Test if one of the configured note intput filters accept the given midi message.
     *
     * @param status The status code of the midi message
     * @param data1 The first data byte of the midi message
     * @return True if accepted
     */
    public boolean acceptFilter (final int status, final int data1)
    {
        final String statusHex = StringUtils.toHexStr (status);
        return this.filters.contains (statusHex) || this.filters.contains (statusHex + StringUtils.toHexStr (data1));
    }


    /**
     * Translate the given key by using the currently set translation table. If no table is set the
     * given key is returned without modification.
     *
     * @param key The key to translate
     * @return The translated key
     */
    public int translateKey (final int key)
    {
        return this.keyTranslationTable == null ? key : this.keyTranslationTable[key].intValue ();
    }


    /**
     * Translate the given velocity by using the currently set translation table. If no table is set
     * the given velocity is returned without modification.
     *
     * @param velocity The velocity to translate
     * @return The translated velocity
     */
    public int translateVelocity (final int velocity)
    {
        return this.velocityTranslationTable == null ? velocity : this.velocityTranslationTable[velocity].intValue ();
    }
}
