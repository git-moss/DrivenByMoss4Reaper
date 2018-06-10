// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * A filter folder with either specificly assigned plugins or a search query.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceCollection
{
    private static final int           TYPE_JS    = 2;
    private static final int           TYPE_VST   = 3;
    private static final int           TYPE_QUERY = 1048576;

    private final String               name;
    private final Map<String, Integer> items      = new HashMap<> ();


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
        this.items.put (item, Integer.valueOf (type));
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
    public List<Device> filter (final List<Device> devices)
    {
        final List<Device> results = new ArrayList<> ();
        for (final Device d: devices)
        {
            if (this.testDevice (d))
                results.add (d);
        }
        return results;
    }


    /**
     * Test if the device matches this folder.
     *
     * @param d The device to test
     * @return True if matches
     */
    private boolean testDevice (final Device d)
    {
        for (final Entry<String, Integer> e: this.items.entrySet ())
        {
            final boolean isJS = d.getFileType ().equals (DeviceFileType.JS);
            final String key = e.getKey ();
            switch (e.getValue ().intValue ())
            {
                case TYPE_JS:
                    if (isJS && key.equals (d.getModule ()))
                        return true;
                    break;

                case TYPE_VST:
                    if (!isJS && compareModules (key, d.getModule ()))
                        return true;
                    break;

                case TYPE_QUERY:
                    if (compareQuery (key, d))
                        return true;
                    break;

                default:
                    // Unsupported folder type
                    break;
            }
        }
        return false;
    }


    /**
     * Parses the filter query and tests it against the given device.
     *
     * @param query The query to parse
     * @param d The device to test
     * @return True if matches
     */
    private static boolean compareQuery (final String query, final Device d)
    {
        // Parse the query
        final Set<String> mustMatch = new HashSet<> ();
        final Set<String> mustNotMatch = new HashSet<> ();

        for (final String part: query.split (" OR "))
        {
            final String [] notParts = part.split (" NOT ");
            mustMatch.add (notParts[0].toLowerCase ());
            for (int i = 1; i < notParts.length; i++)
                mustNotMatch.add (notParts[i].toLowerCase ());
        }

        // Compare all texts at once
        final String text = (d.getName () + " " + d.getVendor () + " " + d.getFileType ()).toLowerCase ();

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


    /**
     * Compares if the given path points to the module.
     *
     * @param modulePath The path
     * @param module The module to test
     * @return True if matches
     */
    private static boolean compareModules (final String modulePath, final String module)
    {
        final String [] split = modulePath.split ("[\\\\/]");
        if (split == null || split.length == 0)
            return false;
        final String filename = split[split.length - 1].replace (' ', '_').replace ('(', '_').replace (')', '_');
        return filename.equals (module);
    }
}
