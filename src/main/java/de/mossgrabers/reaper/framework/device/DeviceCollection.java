// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * A filter folder with either specifically assigned plugins or a search query.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceCollection
{
    private static final int                                  TYPE_JS                 = 2;
    private static final int                                  TYPE_VST                = 3;
    private static final int                                  TYPE_QUERY              = 1048576;

    private static final char []                              MODULE_CHARS_TO_REPLACE =
    {
        ' ',
        '(',
        ')',
        '-',
        '+'
    };

    private final String                                      name;
    private final Set<String>                                 jsItems                 = new HashSet<> ();
    private final Set<String>                                 vstItems                = new HashSet<> ();
    private final Map<String, Pair<Set<String>, Set<String>>> queryItems              = new HashMap<> ();


    /**
     * Constructor.
     *
     * @param name The name of the folder
     */
    public DeviceCollection (final String name)
    {
        this.name = name;
    }


    /**
     * Add an item to the folder.
     *
     * @param item Either a specific device module name or a search query
     * @param type The type of the folder
     */
    public void addItem (final String item, final int type)
    {
        switch (type)
        {
            case TYPE_JS:
                this.jsItems.add (item.startsWith ("\"") && item.endsWith ("\"") ? item.substring (1, item.length () - 2) : item);
                break;

            case TYPE_VST:
                final String [] split = item.split ("[\\\\/]");
                if (split != null && split.length > 0)
                    this.vstItems.add (cleanupFilename (split[split.length - 1]));
                break;

            case TYPE_QUERY:
                // Parse the query
                final Set<String> mustMatch = new HashSet<> ();
                final Set<String> mustNotMatch = new HashSet<> ();

                for (final String part: item.split (" OR "))
                {
                    final String [] notParts = part.split (" NOT ");
                    mustMatch.add (notParts[0].trim ().toLowerCase (Locale.US));
                    for (int i = 1; i < notParts.length; i++)
                    {
                        String notStr = notParts[i].trim ().toLowerCase (Locale.US);
                        final int length = notStr.length ();
                        if (length > 2 && notStr.charAt (0) == '(' && notStr.charAt (length - 1) == ')')
                            notStr = notStr.substring (1, length - 2).trim ();
                        mustNotMatch.add (notStr);
                    }
                }
                this.queryItems.put (item, new Pair<> (mustMatch, mustNotMatch));
                break;

            default:
                // Unsupported folder type
                break;
        }
    }


    private static String cleanupFilename (final String filename)
    {
        String fn = filename;
        for (final char c: MODULE_CHARS_TO_REPLACE)
            fn = fn.replace (c, '_');
        return fn.toLowerCase (Locale.US);
    }


    /**
     * Get the name of the folder.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Filter all given devices by the folder.
     *
     * @param devices The devices to filter
     * @return All devices contained in the folder (or match the search query)
     */
    public List<DeviceMetadataImpl> filter (final List<DeviceMetadataImpl> devices)
    {
        final List<DeviceMetadataImpl> results = new ArrayList<> ();
        for (final DeviceMetadataImpl d: devices)
        {
            if (this.testDevice (d))
                results.add (d);
        }
        return results;
    }


    /**
     * Test if the device matches this folder.
     *
     * @param device The device to test
     * @return True if matches
     */
    private boolean testDevice (final DeviceMetadataImpl device)
    {
        final boolean isJS = device.getFileType ().equals (DeviceFileType.JS);
        final String module = device.getModule ();

        if (isJS)
        {
            if (this.jsItems.contains (module.startsWith ("\"") && module.endsWith ("\"") ? module.substring (1, module.length () - 1) : module))
                return true;
        }
        else
        {
            if (this.vstItems.contains (module))
                return true;
        }

        for (final Pair<Set<String>, Set<String>> query: this.queryItems.values ())
        {
            if (compareQuery (query.getKey (), query.getValue (), device))
                return true;
        }

        return false;
    }


    /**
     * Parses the filter query and tests it against the given device.
     *
     * @param mustMatch The terms to match
     * @param mustNotMatch The terms to not match
     * @param device The device to test
     * @return True if matches
     */
    private static boolean compareQuery (final Set<String> mustMatch, final Set<String> mustNotMatch, final DeviceMetadataImpl device)
    {
        // Compare all texts at once
        final String text = (device.name () + " " + device.getVendor () + " " + device.getFileType ()).toLowerCase (Locale.US);

        boolean success = mustMatch.isEmpty ();
        for (final String match: mustMatch)
        {
            if (text.contains (match))
            {
                success = true;
                break;
            }
        }
        if (!success)
            return false;

        for (final String notMatch: mustNotMatch)
        {
            if (text.contains (notMatch))
                return false;
        }
        return true;
    }
}
