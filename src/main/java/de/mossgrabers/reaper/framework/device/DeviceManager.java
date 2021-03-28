// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.ui.utils.LogModel;

import com.nikhaldimann.inieditor.IniEditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
    private static final String          SECTION_FOLDERS                = "Folders";
    private static final Pattern         PATTERN_VST                    = Pattern.compile (".+?,.+?,(.+?)(\\!\\!\\!VSTi)?");
    private static final Pattern         PATTERN_COMPANY                = Pattern.compile ("(.*?)\\s*\\((.*?)\\)");
    private static final Pattern         PATTERN_AU_COMPANY_DEVICE_NAME = Pattern.compile ("(.+?):\\s*(.+)");
    private static final Pattern         PATTERN_JSFX                   = Pattern.compile ("NAME\\s?((\")?.+?(\")?)\\s?\"(.+?)\"");
    private static final String          IS_INSTRUMENT_TAG              = "<inst>";

    private static final Set<String>     NON_CATEGORIES                 = Set.of ("ix", "till", "loser", "liteon", "sstillwell", "teej", "schwa", "u-he", "remaincalm_org");

    private final List<Device>           devices                        = new ArrayList<> ();
    private final List<Device>           instruments                    = new ArrayList<> ();
    private final List<Device>           effects                        = new ArrayList<> ();
    private final List<String>           categories                     = new ArrayList<> ();
    private final List<String>           vendors                        = new ArrayList<> ();
    private final List<DeviceCollection> collections                    = new ArrayList<> ();

    private static final DeviceManager   INSTANCE                       = new DeviceManager ();


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
     * Get all instrument devices.
     *
     * @return The instrument devices
     */
    public List<Device> getInstruments ()
    {
        synchronized (this.devices)
        {
            return new ArrayList<> (this.instruments);
        }
    }


    /**
     * Get all effect devices.
     *
     * @return The effect devices
     */
    public List<Device> getEffects ()
    {
        synchronized (this.devices)
        {
            return new ArrayList<> (this.effects);
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
                if (accept (d, fileType, category, vendor, location, deviceType))
                    results.add (d);
            }
        }
        return collection == null ? results : collection.filter (results);
    }


    private static boolean accept (final Device d, final DeviceFileType fileType, final String category, final String vendor, final DeviceLocation location, final DeviceType deviceType)
    {
        if (fileType != null && d.getFileType () != fileType)
            return false;

        if (category != null && !d.hasCategory (category))
            return false;

        if (vendor != null && !vendor.equals (d.getVendor ()))
            return false;

        if (location != null && d.getLocation () != location)
            return false;

        return deviceType == null || d.getType () == deviceType;
    }


    /**
     * Load all information about available devices from different INI files
     * (reaper-vstplugins64.ini, reaper-fxtags.ini, reaper-jsfx.ini) in Reapers' configuration path.
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
                this.parseVstDevicesFile (Device.Architecture.X64, iniFiles.getIniVstPlugins64 ());
            if (iniFiles.isVstARMPresent ())
                this.parseVstDevicesFile (Device.Architecture.ARM, iniFiles.getIniVstPluginsARM64 ());
            if (iniFiles.isAuPresent ())
                this.parseAuDevicesFile (Device.Architecture.X64, iniFiles.getIniAuPlugins64 ());
            if (iniFiles.isAuARMPresent ())
                this.parseAuDevicesFile (Device.Architecture.ARM, iniFiles.getIniAuPluginsARM64 ());

            final Set<String> categoriesSet = new TreeSet<> ();
            final Set<String> vendorsSet = new TreeSet<> ();

            // Load categories and vendor information
            if (iniFiles.isFxTagsPresent ())
                this.parseFXTagsFile (iniFiles.getIniFxTags (), categoriesSet, vendorsSet);

            // Load JS devices
            this.loadJSDevices (iniFiles.getIniPath () + File.separator + "reaper-jsfx.ini", logModel, categoriesSet, vendorsSet);

            this.categories.addAll (categoriesSet);
            this.vendors.addAll (vendorsSet);

            // Load collection filters
            if (iniFiles.isFxFoldersPresent ())
                this.parseCollectionFilters (iniFiles.getIniFxFolders ());

            this.devices.sort ( (d1, d2) -> d1.getDisplayName ().compareToIgnoreCase (d2.getDisplayName ()));

            this.devices.forEach (device -> {
                final DeviceType type = device.getType ();
                if (type == DeviceType.INSTRUMENT)
                    this.instruments.add (device);
                else if (type == DeviceType.AUDIO_EFFECT)
                    this.effects.add (device);
            });
        }
    }


    /**
     * Get all collected device categories.
     *
     * @return The set with all categories
     */
    public List<String> getCategories ()
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
    public List<String> getVendors ()
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
     * Parses the VST 64 devices file.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param iniFile The INI file from which to parse
     */
    private void parseVstDevicesFile (final Device.Architecture architecture, final IniEditor iniFile)
    {
        final Map<String, String> section = iniFile.getSectionMap ("vstcache");
        for (final Entry<String, String> entry: section.entrySet ())
        {
            final Device device = parseVstDevice (architecture, entry.getKey (), entry.getValue ());
            if (device != null)
                this.devices.add (device);
        }
    }


    /**
     * Parses the AU 64 devices file content.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param iniFileContent The content of the INI file from which to parse
     */
    private void parseAuDevicesFile (final Device.Architecture architecture, final String iniFileContent)
    {
        iniFileContent.lines ().forEach (line -> {

            if ("[auplugins]".equals (line))
                return;

            final String [] split = line.split ("=");
            if (split.length != 2)
                return;

            final Device device = parseAuDevice (architecture, split[0], split[1]);
            if (device != null)
                this.devices.add (device);

        });
    }


    /**
     * Parses the FX tags file.
     *
     * @param iniFile The ini file from which to parse
     * @param categoriesSet The categories set
     * @param vendorsSet The vendors set
     */
    private void parseFXTagsFile (final IniEditor iniFile, final Set<String> categoriesSet, final Set<String> vendorsSet)
    {
        for (final Device d: this.devices)
        {
            final String categoriesStr = iniFile.get ("category", d.getModule ());
            if (categoriesStr != null)
            {
                final List<String> asList = Arrays.asList (categoriesStr.split ("\\|"));
                d.setCategories (asList);
                categoriesSet.addAll (asList);
            }

            final String vendor = iniFile.get ("developer", d.getModule ());
            if (vendor == null)
                continue;
            d.setVendor (vendor);
            vendorsSet.add (vendor);
        }
    }


    /**
     * Parses the collection filter file.
     *
     * @param iniFile The ini file from which to parse
     */
    private void parseCollectionFilters (final IniEditor iniFile)
    {
        for (int i = 0; i < getInt (iniFile, SECTION_FOLDERS, "NbFolders", 0); i++)
        {
            final int id = getInt (iniFile, SECTION_FOLDERS, "Id" + i, -1);
            if (id >= 0)
            {
                final String collectionName = iniFile.get (SECTION_FOLDERS, "Name" + i);
                if (collectionName != null)
                {
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
        final String value = iniFile.get (category, name);
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
     * Parse the information of a VST device.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param module The module name
     * @param nameAndCompany The name and company
     * @return The created device or null if the information cannot be parsed
     */
    private static Device parseVstDevice (final Device.Architecture architecture, final String module, final String nameAndCompany)
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

        return new Device (creationName, name, module, dt, architecture);
    }


    /**
     * Parse the information of a AU device.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param module The module name
     * @param isInstrument The encoded instrument tag (&lt;!inst&gt; or &lt;!inst&gt;)
     * @return The created device or null if the information cannot be parsed
     */
    private static Device parseAuDevice (final Device.Architecture architecture, final String module, final String isInstrument)
    {
        final Matcher matcher = PATTERN_AU_COMPANY_DEVICE_NAME.matcher (module);
        if (!matcher.matches ())
            return null;

        final String company = matcher.group (1);
        final String deviceName = matcher.group (2);

        final DeviceFileType dt = IS_INSTRUMENT_TAG.equals (isInstrument) ? DeviceFileType.AUI : DeviceFileType.AU;
        final Device device = new Device (module, deviceName, module, dt, architecture);
        device.setVendor (company);
        return device;
    }


    /**
     * Clear all cached information.
     */
    private void clearCache ()
    {
        this.devices.clear ();
        this.instruments.clear ();
        this.effects.clear ();
        this.categories.clear ();
        this.vendors.clear ();
        this.collections.clear ();
    }


    /**
     * Parses the FX tags file.
     *
     * @param filename The file from which to parse
     * @param logModel The host for logging
     * @param categoriesSet The categories set
     * @param vendorsSet The vendors set
     */
    private void loadJSDevices (final String filename, final LogModel logModel, final Set<String> categoriesSet, final Set<String> vendorsSet)
    {
        final Path path = Paths.get (filename);
        try
        {
            if (path.toFile ().exists ())
                loadFile (path).forEach (line -> this.parseJSDevice (line, categoriesSet, vendorsSet));
            else
                logModel.info (filename + " not present, skipped loading.");
        }
        catch (final IOException ex)
        {
            logModel.error ("Could not load file: " + path, ex);
        }
    }


    private static List<String> loadFile (final Path path) throws IOException
    {
        try
        {
            return Files.readAllLines (path);
        }
        catch (final MalformedInputException ex)
        {
            return Files.readAllLines (path, StandardCharsets.ISO_8859_1);
        }
    }


    /**
     * Parses the information about a JS plugin.
     *
     * @param line The line to parse
     * @param categoriesSet The categories set
     * @param vendorsSet The vendors set
     */
    private void parseJSDevice (final String line, final Set<String> categoriesSet, final Set<String> vendorsSet)
    {
        final Matcher matcher = PATTERN_JSFX.matcher (line);
        if (!matcher.matches ())
            return;
        final String name = matcher.group (4).substring (4);
        final String module = matcher.group (1);
        final Device device = new Device (module, name, module, DeviceFileType.JS, Device.Architecture.SCRIPT);
        this.devices.add (device);

        final String [] modulePath = module.split ("/");
        if (modulePath.length <= 1)
            return;
        final String o = modulePath[0].startsWith ("\"") ? modulePath[0].substring (1) : modulePath[0];
        if (NON_CATEGORIES.contains (o.toLowerCase (Locale.US)))
        {
            device.setVendor (o);
            vendorsSet.add (o);
        }
        else
        {
            device.setCategories (Collections.singleton (o));
            categoriesSet.add (o);
        }
    }
}
