// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.ui.utils.LogModel;

import com.nikhaldimann.inieditor.IniEditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Manages the information about all available devices.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceManager
{
    private static final Pattern         PATTERN_VST     = Pattern.compile (".+?,.+?,(.+?)(\\!\\!\\!VSTi)?");
    private static final Pattern         PATTERN_COMPANY = Pattern.compile ("(.*?)\\s*\\((.*?)\\)");
    private static final Pattern         PATTERN_JSFX    = Pattern.compile ("NAME\\s?((\")?.+?(\")?)\\s?\"(.+?)\"");

    private static final Set<String>     NON_CATEGORIES  = new HashSet<> ();

    private final List<Device>           devices         = new ArrayList<> ();
    private final Set<String>            categories      = new TreeSet<> ();
    private final Set<String>            vendors         = new TreeSet<> ();
    private final List<DeviceCollection> collections     = new ArrayList<> ();

    static
    {
        Collections.addAll (NON_CATEGORIES, "ix", "till", "loser", "liteon", "sstillwell", "teej", "schwa", "u-he", "remaincalm_org");
    }

    private static final DeviceManager INSTANCE = new DeviceManager ();


    /**
     * Private due to singleton.
     */
    private DeviceManager ()
    {
        // Intentionally empty
    }


    /**
     * Get the singleton.
     *
     * @return The singleton
     */
    public static DeviceManager get ()
    {
        return INSTANCE;
    }


    /**
     * Get the number of all devices.
     *
     * @return The number
     */
    public int getNumDevices ()
    {
        synchronized (this.devices)
        {
            return this.devices.size ();
        }
    }


    /**
     * Get all available devices.
     *
     * @return The devices
     */
    public List<Device> getAll ()
    {
        synchronized (this.devices)
        {
            return new ArrayList<> (this.devices);
        }
    }


    /**
     * Filter the devices by file type.
     *
     * @param deviceFileType Filter by device type (plugin format), may be null
     * @return The devices matching the filter criteria
     */
    public List<Device> filterByFileType (final DeviceFileType deviceFileType)
    {
        return this.filterBy (deviceFileType, null, null, null, null, null);
    }


    /**
     * Filter the devices by category.
     *
     * @param category Filter by device category, may be null
     * @return The devices matching the filter criteria
     */
    public List<Device> filterByCategory (final String category)
    {
        return this.filterBy (null, category, null, null, null, null);
    }


    /**
     * Filter the devices by a vendor.
     *
     * @param vendor Filter by device vendor, may be null
     * @return The devices matching the filter criteria
     */
    public List<Device> filterByVendor (final String vendor)
    {
        return this.filterBy (null, null, vendor, null, null, null);
    }


    /**
     * Filter the devices by a collection.
     *
     * @param collection Filter by device collection, may be null
     * @return The devices matching the filter criteria
     */
    public List<Device> filterByCollection (final DeviceCollection collection)
    {
        return this.filterBy (null, null, null, collection, null, null);
    }


    /**
     * Filter the devices by a location.
     *
     * @param location Filter by device location, may be null
     * @return The devices matching the filter criteria
     */
    public List<Device> filterByLocation (final DeviceLocation location)
    {
        return this.filterBy (null, null, null, null, location, null);
    }


    /**
     * Filter the devices by type.
     *
     * @param deviceType Filter by device type, may be null
     * @return The devices matching the filter criteria
     */
    public List<Device> filterByType (final DeviceType deviceType)
    {
        return this.filterBy (null, null, null, null, null, deviceType);
    }


    /**
     * Filter the devices by different criteria.
     *
     * @param fileType Filter by device type (plugin format), may be null
     * @param category Filter by device category, may be null
     * @param vendor Filter by device vendor, may be null
     * @param collection Filter by device collection, may be null
     * @param location Filter by device location, may be null
     * @param deviceType Filter by device type, may be null
     * @return The devices matching the filter criteria
     */
    public List<Device> filterBy (final DeviceFileType fileType, final String category, final String vendor, final DeviceCollection collection, final DeviceLocation location, final DeviceType deviceType)
    {
        final List<Device> results = new ArrayList<> ();
        synchronized (this.devices)
        {
            for (final Device d: this.devices)
            {
                if (fileType != null && d.getFileType () != fileType)
                    continue;

                if (category != null && !d.hasCategory (category))
                    continue;

                if (vendor != null && !vendor.equals (d.getVendor ()))
                    continue;

                if (location != null && d.getLocation () != location)
                    continue;

                if (deviceType != null && d.getType () != deviceType)
                    continue;

                results.add (d);
            }
        }
        return collection == null ? results : collection.filter (results);
    }


    /**
     * Load all information about available devices from different INI files
     * (reaper-vstplugins64.ini, reaper-fxtags.ini, reaper-jsfx.ini) in Reapers configuration path.
     *
     * @param iniFiles Access to the INI files
     * @param logModel For logging
     */
    public void parseINIFiles (final IniFiles iniFiles, final LogModel logModel)
    {
        synchronized (this.devices)
        {
            this.clearCache ();

            // Load all 64 bit devices
            if (iniFiles.isVstPresent ())
                this.parseDevicesFile (iniFiles.getIniVstPlugins64 ());

            // Load categories and vendor information
            if (iniFiles.isFxTagsPresent ())
                this.parseFXTagsFile (iniFiles.getIniFxTags ());

            // Load JS devices
            this.loadJSDevices (iniFiles.getIniPath () + File.separator + "reaper-jsfx.ini", logModel);

            // Load collection filters
            if (iniFiles.isFxFoldersPresent ())
                this.parseCollectionFilters (iniFiles.getIniFxFolders ());

            this.devices.sort ( (d1, d2) -> d1.getDisplayName ().compareToIgnoreCase (d2.getDisplayName ()));
        }
    }


    /**
     * Get all collected device categories.
     *
     * @return The set with all categories
     */
    public Set<String> getCategories ()
    {
        synchronized (this.devices)
        {
            return this.categories;
        }
    }


    /**
     * Get all collected device vendors.
     *
     * @return The set with all vendors
     */
    public Set<String> getVendors ()
    {
        synchronized (this.devices)
        {
            return this.vendors;
        }
    }


    /**
     * Get all configured collections.
     *
     * @return The collections
     */
    public List<DeviceCollection> getCollections ()
    {
        synchronized (this.devices)
        {
            return this.collections;
        }
    }


    /**
     * Get a collection by its' name.
     *
     * @param collectionName The name to look for
     * @return The collection or null if not found
     */
    public DeviceCollection getCollection (final String collectionName)
    {
        synchronized (this.devices)
        {
            for (final DeviceCollection collection: this.collections)
            {
                if (collection.getName ().equals (collectionName))
                    return collection;
            }
        }
        return null;
    }


    /**
     * Parses the devices file.
     *
     * @param iniFile The ini file from which to parse
     */
    private void parseDevicesFile (final IniEditor iniFile)
    {
        final Map<String, String> vstcacheSection = iniFile.getSectionMap ("vstcache");
        for (final Entry<String, String> entry: vstcacheSection.entrySet ())
        {
            final Device device = parseDevice (entry.getKey (), entry.getValue ());
            if (device != null)
                this.devices.add (device);
        }
    }


    /**
     * Parses the FX tags file.
     *
     * @param iniFile The ini file from which to parse
     */
    private void parseFXTagsFile (final IniEditor iniFile)
    {
        for (final Device d: this.devices)
        {
            final String categoriesStr = iniFile.get ("category", d.getModule ());
            if (categoriesStr != null)
            {
                final List<String> asList = Arrays.asList (categoriesStr.split ("\\|"));
                d.setCategories (asList);
                this.categories.addAll (asList);
            }

            final String vendor = iniFile.get ("developer", d.getModule ());
            if (vendor == null)
                continue;
            d.setVendor (vendor);
            this.vendors.add (vendor);
        }
    }


    /**
     * Parses the collection filter file.
     *
     * @param iniFile The ini file from which to parse
     */
    private void parseCollectionFilters (final IniEditor iniFile)
    {
        for (int i = 0; i < getInt (iniFile, "Folders", "NbFolders", 0); i++)
        {
            final int id = getInt (iniFile, "Folders", "Id" + i, -1);
            if (id < 0)
                continue;
            final String collectionName = iniFile.get ("Folders", "Name" + i);
            if (collectionName == null)
                continue;

            final DeviceCollection deviceCollection = new DeviceCollection (collectionName);
            this.collections.add (deviceCollection);

            final String collectionSection = "Folder" + id;
            for (int j = 0; j < getInt (iniFile, collectionSection, "Nb", 0); j++)
            {
                final String item = iniFile.get (collectionSection, "Item" + j);
                if (item == null)
                    continue;
                final int type = getInt (iniFile, collectionSection, "Type" + j, 0);
                deviceCollection.addItem (item, type);
            }
        }
    }


    /**
     * Get a value from the INI file and convert it to an integer.
     *
     * @param iniFile The INI file
     * @param category The category section
     * @param name The name of the entry
     * @param def The default value
     * @return The integer value
     */
    private static int getInt (final IniEditor iniFile, final String category, final String name, final int def)
    {
        final String value = iniFile.get ("Folders", "NbFolders");
        try
        {
            return value == null ? def : Integer.parseInt (value);
        }
        catch (final NumberFormatException ex)
        {
            return def;
        }
    }


    /**
     * Parse the information of a device.
     *
     * @param module The module name
     * @param nameAndCompany The name and company
     * @return The created device or null if the information cannot be parsed
     */
    private static Device parseDevice (final String module, final String nameAndCompany)
    {
        final Matcher matcher = PATTERN_VST.matcher (nameAndCompany);
        if (!matcher.matches ())
            return null;

        final String creationName = matcher.group (1);
        if ("<SHELL>".equals (creationName))
            return null;

        final Matcher companyMatcher = PATTERN_COMPANY.matcher (creationName);
        if (!companyMatcher.matches ())
            return null;

        final String name = companyMatcher.group (1);
        final String type = matcher.group (2);

        final DeviceFileType dt;
        if ("!!!VSTi".equals (type))
            dt = module.endsWith ("vst3") ? DeviceFileType.VST3I : DeviceFileType.VSTI;
        else
            dt = module.endsWith ("vst3") ? DeviceFileType.VST3 : DeviceFileType.VST;

        return new Device (creationName, name, module, dt);
    }


    /**
     * Clear all cached information.
     */
    private void clearCache ()
    {
        this.devices.clear ();
        this.categories.clear ();
        this.vendors.clear ();
        this.collections.clear ();
    }


    /**
     * Parses the FX tags file.
     *
     * @param filename The file from which to parse
     * @param logModel The host for logging
     */
    private void loadJSDevices (final String filename, final LogModel logModel)
    {
        final Path path = Paths.get (filename);
        try
        {
            if (path.toFile ().exists ())
                Files.readAllLines (path, Charset.forName ("UTF-8")).forEach (this::parseJSDevice);
            else
                logModel.info (filename + " not present, skipped loading.");
        }
        catch (final IOException ex)
        {
            logModel.error ("Could not load file: " + path, ex);
        }
    }


    /**
     * Parses the information about a JS plugin.
     *
     * @param line The line to parse
     */
    private void parseJSDevice (final String line)
    {
        final Matcher matcher = PATTERN_JSFX.matcher (line);
        if (!matcher.matches ())
            return;
        final String name = matcher.group (4).substring (4);
        final String module = matcher.group (1);
        final Device device = new Device (module, name, module, DeviceFileType.JS);
        this.devices.add (device);

        final String [] modulePath = module.split ("/");
        if (modulePath.length <= 1)
            return;
        final String o = modulePath[0].startsWith ("\"") ? modulePath[0].substring (1) : modulePath[0];
        if (NON_CATEGORIES.contains (o.toLowerCase ()))
        {
            device.setVendor (o);
            this.vendors.add (o);
        }
        else
        {
            device.setCategories (Collections.singleton (o));
            this.categories.add (o);
        }
    }
}
