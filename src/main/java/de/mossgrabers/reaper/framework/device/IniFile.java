// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses a Windows INI file. A INI file contains key/value properties, which are divided into
 * sections. The same key can occur in multiple sections.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class IniFile
{
    private static final Pattern             PATTERN_SECTION = Pattern.compile ("\\s*\\[([^]]*)\\]\\s*");
    private static final Pattern             PATTERN_KEY     = Pattern.compile ("\\s*([^=]*)=(.*)");

    private Map<String, Map<String, String>> entries         = new HashMap<> ();


    /**
     * Load an ini file.
     *
     * @param path The full path of the ini file
     * @throws IOException Could not open the file
     */
    public void load (final String path) throws IOException
    {
        this.entries.clear ();

        try (final BufferedReader br = new BufferedReader (new FileReader (path)))
        {
            String line;
            String section = null;
            while ((line = br.readLine ()) != null)
            {
                Matcher m = PATTERN_SECTION.matcher (line);
                if (m.matches ())
                {
                    section = m.group (1).trim ();
                    continue;
                }

                if (section == null)
                    continue;

                m = PATTERN_KEY.matcher (line);
                if (m.matches ())
                    this.entries.computeIfAbsent (section, s -> new HashMap<> ()).put (m.group (1).trim (), m.group (2).trim ());
            }
        }
    }


    /**
     * Get all entries of a section.
     *
     * @param section The name of the section
     * @return All key/value pairs of the section
     */
    public Map<String, String> getSection (final String section)
    {
        return this.entries.get (section);
    }


    /**
     * Get the value of a key in a section as a string value.
     *
     * @param section The name of the section
     * @param key The key
     * @param defaultvalue The default value to return if the key is not present
     * @return The value
     */
    public String getString (final String section, final String key, final String defaultvalue)
    {
        final Map<String, String> kv = this.entries.get (section);
        return kv == null ? defaultvalue : kv.get (key);
    }


    /**
     * Get the value of a key in a section as an integer value.
     *
     * @param section The name of the section
     * @param key The key
     * @param defaultvalue The default value to return if the key is not present
     * @return The value
     */
    public int getInt (final String section, final String key, final int defaultvalue)
    {
        final Map<String, String> kv = this.entries.get (section);
        return kv == null ? defaultvalue : Integer.parseInt (kv.get (key));
    }


    /**
     * Get the value of a key in a section as a float value.
     *
     * @param section The name of the section
     * @param key The key
     * @param defaultvalue The default value to return if the key is not present
     * @return The value
     */
    public float getFloat (final String section, final String key, final float defaultvalue)
    {
        final Map<String, String> kv = this.entries.get (section);
        return kv == null ? defaultvalue : Float.parseFloat (kv.get (key));
    }


    /**
     * Get the value of a key in a section as a double value.
     *
     * @param section The name of the section
     * @param key The key
     * @param defaultvalue The default value to return if the key is not present
     * @return The value
     */
    public double getDouble (final String section, final String key, final double defaultvalue)
    {
        final Map<String, String> kv = this.entries.get (section);
        return kv == null ? defaultvalue : Double.parseDouble (kv.get (key));
    }
}