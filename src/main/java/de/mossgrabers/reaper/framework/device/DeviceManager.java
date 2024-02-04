// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device;

import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMap;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPage;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPageParameter;
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Manages the information about all available devices.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceManager
{
    private static final String                       SECTION_FOLDERS                = "Folders";
    private static final Pattern                      PATTERN_VST                    = Pattern.compile (".+?,.+?,(.+?)(\\!\\!\\!VSTi)?");
    private static final Pattern                      PATTERN_COMPANY                = Pattern.compile ("(.*?)\\s*\\((.*?)\\)");
    private static final Pattern                      PATTERN_AU_COMPANY_DEVICE_NAME = Pattern.compile ("(.+?):\\s*(.+)");
    private static final Pattern                      PATTERN_JSFX                   = Pattern.compile ("NAME\\s?((\")?.+?(\")?)\\s?\"(.+?)\"");
    private static final Pattern                      PATTERN_CLAP                   = Pattern.compile ("(.*?)\\|(.*?)\\s*\\((.*?)\\)");
    private static final String                       IS_INSTRUMENT_TAG              = "<inst>";
    private static final Map<String, String>          JS_CATEGORY_MAP                = new HashMap<> ();

    private static final DeviceManager                INSTANCE                       = new DeviceManager ();

    private static final Set<String>                  NON_CATEGORIES                 = Set.of ("ix", "till", "loser", "liteon", "sstillwell", "teej", "schwa", "u-he", "remaincalm_org");

    private static final Map<Integer, DeviceFileType> TYPE_CODES                     = new HashMap<> (5);
    static
    {
        TYPE_CODES.put (Integer.valueOf (1), DeviceFileType.VST3);
        TYPE_CODES.put (Integer.valueOf (2), DeviceFileType.VST2);
        TYPE_CODES.put (Integer.valueOf (3), DeviceFileType.AU);
        // Not used since Reaper does not store this in a file!
        // TYPE_CODES.put (Integer.valueOf (4), DeviceFileType.LV2);
        TYPE_CODES.put (Integer.valueOf (5), DeviceFileType.CLAP);

        JS_CATEGORY_MAP.put ("analysis", "Analysis");
        JS_CATEGORY_MAP.put ("delay", "Delay");
        JS_CATEGORY_MAP.put ("dynamics", "Dynamics");
        JS_CATEGORY_MAP.put ("filters", "Filter");
        JS_CATEGORY_MAP.put ("guitar", "Guitar");
        JS_CATEGORY_MAP.put ("loopsamplers", "Sampler");
        JS_CATEGORY_MAP.put ("meters", "Analysis");
        JS_CATEGORY_MAP.put ("midi", "MIDI");
        JS_CATEGORY_MAP.put ("misc", "Misc");
        JS_CATEGORY_MAP.put ("old_unsupported", "Misc");
        JS_CATEGORY_MAP.put ("pitch", "Pitch");
        JS_CATEGORY_MAP.put ("synthesis", "Synth");
        JS_CATEGORY_MAP.put ("utility", "Tools");
        JS_CATEGORY_MAP.put ("waveshapers", "Modulation");
    }

    private final List<DeviceMetadataImpl>  devices                = new ArrayList<> ();
    private final List<DeviceMetadataImpl>  instruments            = new ArrayList<> ();
    private final List<DeviceMetadataImpl>  effects                = new ArrayList<> ();
    private final List<String>              categories             = new ArrayList<> ();
    private final List<String>              vendors                = new ArrayList<> ();
    private final List<DeviceCollection>    collections            = new ArrayList<> ();
    private final List<DeviceFileType>      availableFileTypes     = new ArrayList<> ();
    private final Set<DeviceArchitecture>   availableArchitectures = new TreeSet<> ();
    private final Map<String, ParameterMap> parameterMaps          = new HashMap<> ();
    private final List<DeviceFileType>      preferredTypes         = new ArrayList<> ();
    private IniFiles                        iniFiles;


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
    public List<DeviceMetadataImpl> getAll ()
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
    public List<DeviceMetadataImpl> getInstruments ()
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
    public List<DeviceMetadataImpl> getEffects ()
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
    public List<DeviceMetadataImpl> filterByFileType (final DeviceFileType deviceFileType)
    {
        return this.filterBy (deviceFileType, null, null, null, null, null);
    }


    /**
     * Filter the devices by category.
     *
     * @param category Filter by device category, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByCategory (final String category)
    {
        return this.filterBy (null, category, null, null, null, null);
    }


    /**
     * Filter the devices by a vendor.
     *
     * @param vendor Filter by device vendor, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByVendor (final String vendor)
    {
        return this.filterBy (null, null, vendor, null, null, null);
    }


    /**
     * Filter the devices by a collection.
     *
     * @param collection Filter by device collection, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByCollection (final DeviceCollection collection)
    {
        return this.filterBy (null, null, null, collection, null, null);
    }


    /**
     * Filter the devices by a location.
     *
     * @param location Filter by device location, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByLocation (final DeviceArchitecture location)
    {
        return this.filterBy (null, null, null, null, location, null);
    }


    /**
     * Filter the devices by type.
     *
     * @param deviceType Filter by device type, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByType (final DeviceType deviceType)
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
    public List<DeviceMetadataImpl> filterBy (final DeviceFileType fileType, final String category, final String vendor, final DeviceCollection collection, final DeviceArchitecture location, final DeviceType deviceType)
    {
        List<DeviceMetadataImpl> results = new ArrayList<> ();
        synchronized (this.devices)
        {
            for (final DeviceMetadataImpl d: this.devices)
            {
                if (accept (d, fileType, category, vendor, location, deviceType))
                    results.add (d);
            }
        }

        if (collection != null)
            results = collection.filter (results);
        return this.filterPreferredFileTypes (results);
    }


    /**
     * Filter by preferred types.
     *
     * @param results The devices to filter
     * @return The filtered list
     */
    private List<DeviceMetadataImpl> filterPreferredFileTypes (final List<DeviceMetadataImpl> results)
    {
        final List<DeviceFileType> prefTypes = this.getPreferredTypes ();
        if (prefTypes.isEmpty () || results.size () <= 1)
            return results;

        // Group identical devices (= with the same name) into a map
        final Map<String, Map<DeviceFileType, DeviceMetadataImpl>> identicalDevices = new TreeMap<> ();
        for (final DeviceMetadataImpl device: results)
        {
            final String deviceName = device.name ();
            identicalDevices.computeIfAbsent (deviceName, name -> new EnumMap<> (DeviceFileType.class)).put (device.getFileType (), device);
        }

        // Choose the preferred devices by their file type
        final List<DeviceMetadataImpl> resultsFiltered = new ArrayList<> ();
        for (final Map<DeviceFileType, DeviceMetadataImpl> deviceOptions: identicalDevices.values ())
        {
            // Find the device of the preferred type
            DeviceMetadataImpl deviceMetadata = null;
            for (final DeviceFileType prefType: prefTypes)
            {
                deviceMetadata = deviceOptions.get (prefType);
                if (deviceMetadata != null)
                    break;
            }

            // If no preferred type was found, add all
            if (deviceMetadata == null)
                resultsFiltered.addAll (deviceOptions.values ());
            else
                resultsFiltered.add (deviceMetadata);
        }
        return resultsFiltered;
    }


    private static boolean accept (final DeviceMetadataImpl d, final DeviceFileType fileType, final String category, final String vendor, final DeviceArchitecture architecture, final DeviceType deviceType)
    {
        if (fileType != null && d.getFileType () != fileType || category != null && !d.hasCategory (category))
            return false;

        if (vendor != null && !vendor.equals (d.getVendor ()) || architecture != null && d.getArchitecture () != architecture)
            return false;

        return deviceType == null || d.getType () == deviceType;
    }


    private List<DeviceFileType> getPreferredTypes ()
    {
        if (this.iniFiles == null)
            return Collections.emptyList ();

        final int pattern = this.iniFiles.getMainIniInteger ("REAPER-fxadd", "dupefilter", 0);

        // The pattern is a 16 bit integer consisting of four 4-bit values providing the potential
        // four preferred plugin types. Priority 1 is LSB. The values for the plugin types are:
        // Off: 0, VST3: 1, VST2: 2, AU: 3, LV2: 4, CLAP: 5

        this.preferredTypes.clear ();
        for (int i = 0; i < 4; i++)
        {
            final int code = pattern >> i * 4 & 15;
            final DeviceFileType type = TYPE_CODES.get (Integer.valueOf (code));
            if (type != null)
                this.preferredTypes.add (type);
        }

        return this.preferredTypes;
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
        this.iniFiles = iniFiles;

        synchronized (this.devices)
        {
            this.clearCache ();

            this.parseVST (iniFiles);
            this.parseCLAP (iniFiles);
            this.parseAU (iniFiles);

            // Load categories and vendor information
            final Set<String> categoriesSet = new TreeSet<> ();
            final Set<String> vendorsSet = new TreeSet<> ();
            if (iniFiles.isFxTagsPresent ())
                this.parseFXTagsFile (iniFiles.getIniFxTags (), categoriesSet, vendorsSet);

            this.parseJS (iniFiles, logModel, categoriesSet, vendorsSet);

            this.categories.addAll (categoriesSet);
            this.vendors.addAll (vendorsSet);

            // Load collection filters
            if (iniFiles.isFxFoldersPresent ())
                this.parseCollectionFilters (iniFiles.getIniFxFolders ());

            // Load device maps
            if (iniFiles.isParamMapsPresent ())
                this.parseParameterMaps (iniFiles.getIniParamMaps ());

            // Set MIDI device type
            for (final DeviceMetadataImpl device: this.devices)
            {
                if (device.hasCategory ("MIDI"))
                    device.setType (DeviceType.MIDI_EFFECT);
            }

            // Finally sort the devices by their display name
            this.devices.sort ( (d1, d2) -> d1.getDisplayName ().compareToIgnoreCase (d2.getDisplayName ()));

            // Split them into instruments and effects
            this.devices.forEach (device -> {
                final DeviceType type = device.getType ();
                if (type == DeviceType.INSTRUMENT)
                    this.instruments.add (device);
                else if (type == DeviceType.AUDIO_EFFECT)
                    this.effects.add (device);
            });
        }
    }


    private void parseVST (final IniFiles iniFiles)
    {
        final boolean isVstPresent = iniFiles.isVstPresent ();
        final boolean isVstARMPresent = iniFiles.isVstARMPresent ();
        if (!isVstPresent && !isVstARMPresent)
            return;
        if (isVstPresent)
        {
            this.parseVstDevicesFile (DeviceArchitecture.X64, iniFiles.getIniVstPlugins64 ());
            this.availableArchitectures.add (DeviceArchitecture.X64);
        }
        if (isVstARMPresent)
        {
            this.parseVstDevicesFile (DeviceArchitecture.ARM, iniFiles.getIniVstPluginsARM64 ());
            this.availableArchitectures.add (DeviceArchitecture.ARM);
        }
        this.availableFileTypes.addAll (List.of (DeviceFileType.VST2, DeviceFileType.VST3));
    }


    private void parseCLAP (final IniFiles iniFiles)
    {
        final boolean isClapPresent = iniFiles.isClapPresent ();
        final boolean isClapARMPresent = iniFiles.isClapARMPresent ();
        if (!isClapPresent && !isClapARMPresent)
            return;
        if (isClapPresent)
        {
            this.parseCLAPDevicesFile (DeviceArchitecture.X64, iniFiles.getIniClapPlugins64 ());
            this.availableArchitectures.add (DeviceArchitecture.X64);
        }
        if (isClapARMPresent)
        {
            this.parseCLAPDevicesFile (DeviceArchitecture.ARM, iniFiles.getIniClapPluginsARM64 ());
            this.availableArchitectures.add (DeviceArchitecture.ARM);
        }
        this.availableFileTypes.addAll (List.of (DeviceFileType.CLAP));
    }


    private void parseAU (final IniFiles iniFiles)
    {
        final boolean isAuPresent = iniFiles.isAuPresent ();
        final boolean isAuARMPresent = iniFiles.isAuARMPresent ();
        if (!isAuPresent && !isAuARMPresent)
            return;
        if (isAuPresent)
        {
            this.parseAuDevicesFile (DeviceArchitecture.X64, iniFiles.getIniAuPlugins64 ());
            this.availableArchitectures.add (DeviceArchitecture.X64);
        }
        if (isAuARMPresent)
        {
            this.parseAuDevicesFile (DeviceArchitecture.ARM, iniFiles.getIniAuPluginsARM64 ());
            this.availableArchitectures.add (DeviceArchitecture.ARM);
        }
        this.availableFileTypes.addAll (List.of (DeviceFileType.AU));
    }


    private void parseJS (final IniFiles iniFiles, final LogModel logModel, final Set<String> categoriesSet, final Set<String> vendorsSet)
    {
        this.availableFileTypes.add (DeviceFileType.JS);
        this.availableArchitectures.add (DeviceArchitecture.SCRIPT);
        this.loadJSDevices (iniFiles.getIniPath () + File.separator + "reaper-jsfx.ini", logModel, categoriesSet, vendorsSet);
    }


    /**
     * Get the file types present on this system.
     *
     * @return The file types
     */
    public List<DeviceFileType> getAvailableFileTypes ()
    {
        return this.availableFileTypes;
    }


    /**
     * Get the locations present on this system.
     *
     * @return The file types
     */
    public List<DeviceArchitecture> getAvailableLocations ()
    {
        return new ArrayList<> (this.availableArchitectures);
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
     * Get the parameter mappings map. Will be modified outside of class, which is intentional.
     *
     * @return The map
     */
    public Map<String, ParameterMap> getParameterMaps ()
    {
        return this.parameterMaps;
    }


    /**
     * Parses the AU 64 devices file content.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param iniFileContent The content of the INI file from which to parse
     */
    private void parseAuDevicesFile (final DeviceArchitecture architecture, final String iniFileContent)
    {
        iniFileContent.lines ().forEach (line -> {

            if ("[auplugins]".equals (line))
                return;

            final String [] split = line.split ("=");
            if (split.length != 2)
                return;

            final DeviceMetadataImpl device = parseAuDevice (architecture, split[0], split[1]);
            if (device != null)
                this.devices.add (device);

        });
    }


    /**
     * Parses the CLAP 64 devices file.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param iniFile The INI file from which to parse
     */
    private void parseCLAPDevicesFile (final DeviceArchitecture architecture, final IniEditor iniFile)
    {
        for (final String fileName: iniFile.sectionNames ())
        {
            final Map<String, String> sectionMap = iniFile.getSectionMap (fileName);
            final DeviceMetadataImpl device = parseCLAPDevice (architecture, sectionMap);
            if (device != null)
                this.devices.add (device);
        }
    }


    /**
     * Parses the VST 64 devices file.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param iniFile The INI file from which to parse
     */
    private void parseVstDevicesFile (final DeviceArchitecture architecture, final IniEditor iniFile)
    {
        final Map<String, String> section = iniFile.getSectionMap ("vstcache");
        for (final Entry<String, String> entry: section.entrySet ())
        {
            final DeviceMetadataImpl device = parseVstDevice (architecture, entry.getKey (), entry.getValue ());
            if (device != null)
                this.devices.add (device);
        }
    }


    /**
     * Parses the FX tags file.
     *
     * @param iniFile The INI file from which to parse
     * @param categoriesSet The categories set
     * @param vendorsSet The vendors set
     */
    private void parseFXTagsFile (final IniEditor iniFile, final Set<String> categoriesSet, final Set<String> vendorsSet)
    {
        for (final DeviceMetadataImpl d: this.devices)
        {
            final String categoriesStr = iniFile.get ("category", d.getModule ());
            if (categoriesStr != null)
            {
                final Set<String> cats = new HashSet<> ();
                for (final String category: categoriesStr.split ("\\|"))
                {
                    if (!NON_CATEGORIES.contains (category))
                        cats.add (category);
                }
                if (cats.isEmpty ())
                {
                    if (d.getType () == DeviceType.INSTRUMENT)
                        cats.add ("Synth");
                    else if (d.getType () == DeviceType.MIDI_EFFECT)
                        cats.add ("MIDI");
                }
                d.setCategories (cats);
                categoriesSet.addAll (cats);
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
     * @param iniFile The INI file from which to parse
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
     * Parses the parameter maps configuration file.
     *
     * @param iniFile The INI file from which to parse
     */
    private void parseParameterMaps (final IniEditor iniFile)
    {
        for (final String name: iniFile.sectionNames ())
        {
            final Map<String, String> sectionMap = iniFile.getSectionMap (name);
            final ParameterMap parameterMap = new ParameterMap (name);
            final List<ParameterMapPage> pages = parameterMap.getPages ();

            int i = 0;
            String pageName;
            while ((pageName = sectionMap.get ("page" + i)) != null)
            {
                final ParameterMapPage page = new ParameterMapPage (pageName);

                final String params = sectionMap.get ("params" + i);
                if (params == null)
                    break;

                final String [] parts = params.split (",");
                if (parts.length != 16)
                    break;

                final List<ParameterMapPageParameter> parameters = page.getParameters ();
                for (int p = 0; p < 16; p += 2)
                    parameters.get (p / 2).assign (Integer.parseInt (parts[p]), parts[p + 1]);

                pages.add (page);

                i++;
            }

            this.parameterMaps.put (name, parameterMap);
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
    private static DeviceMetadataImpl parseVstDevice (final DeviceArchitecture architecture, final String module, final String nameAndCompany)
    {
        final Matcher matcher = PATTERN_VST.matcher (nameAndCompany);
        if (!matcher.matches ())
            return null;

        final String creationName = matcher.group (1);
        if ("<SHELL>".equals (creationName))
            return null;

        final String type = matcher.group (2);

        final Matcher companyMatcher = PATTERN_COMPANY.matcher (creationName);
        final boolean hasCompany = companyMatcher.matches ();
        final String name = hasCompany ? companyMatcher.group (1) : creationName;

        final DeviceType deviceType = "!!!VSTi".equals (type) ? DeviceType.INSTRUMENT : DeviceType.AUDIO_EFFECT;
        final DeviceFileType fileType = module.endsWith ("vst3") ? DeviceFileType.VST3 : DeviceFileType.VST2;
        return new DeviceMetadataImpl (creationName, name, module, deviceType, fileType, architecture);
    }


    /**
     * Parse the information of a CLAP device.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param sectionMap The INI section which contains the info about a CLAP plugin
     * @return The created device or null if the information cannot be parsed
     */
    private static DeviceMetadataImpl parseCLAPDevice (final DeviceArchitecture architecture, final Map<String, String> sectionMap)
    {
        if (sectionMap.size () != 2)
            return null;

        String uuid = null;
        String module = null;
        String deviceType = null;
        String deviceName = null;
        String company = null;

        for (final Entry<String, String> entry: sectionMap.entrySet ())
        {
            final String key = entry.getKey ();

            if ("_".equals (key))
                uuid = entry.getValue ();
            else
            {
                module = key;
                final Matcher clapMatcher = PATTERN_CLAP.matcher (entry.getValue ());
                if (!clapMatcher.matches ())
                    return null;

                deviceType = clapMatcher.group (1);
                deviceName = clapMatcher.group (2);
                company = clapMatcher.group (3);
            }
        }

        if (uuid == null || module == null || deviceType == null || deviceName == null || company == null)
            return null;

        final DeviceType dt = "0".equals (deviceType) ? DeviceType.AUDIO_EFFECT : DeviceType.INSTRUMENT;
        final DeviceMetadataImpl device = new DeviceMetadataImpl (module, deviceName, module, dt, DeviceFileType.CLAP, architecture);
        device.setVendor (company);
        return device;
    }


    /**
     * Parse the information of a AU device.
     *
     * @param architecture The processor architecture for which the device is compiled
     * @param module The module name
     * @param isInstrument The encoded instrument tag (&lt;!inst&gt; or &lt;!inst&gt;)
     * @return The created device or null if the information cannot be parsed
     */
    private static DeviceMetadataImpl parseAuDevice (final DeviceArchitecture architecture, final String module, final String isInstrument)
    {
        final Matcher matcher = PATTERN_AU_COMPANY_DEVICE_NAME.matcher (module);
        if (!matcher.matches ())
            return null;

        final String company = matcher.group (1);
        final String deviceName = matcher.group (2);

        final DeviceType dt = IS_INSTRUMENT_TAG.equals (isInstrument) ? DeviceType.INSTRUMENT : DeviceType.AUDIO_EFFECT;
        final DeviceMetadataImpl device = new DeviceMetadataImpl (module, deviceName, module, dt, DeviceFileType.AU, architecture);
        device.setVendor (company);
        return device;
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
        final DeviceMetadataImpl device = new DeviceMetadataImpl (module, name, module, DeviceType.AUDIO_EFFECT, DeviceFileType.JS, DeviceArchitecture.SCRIPT);
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
            final String mapped = JS_CATEGORY_MAP.get (o);
            if (mapped == null)
            {
                device.setCategories (Collections.singleton (o));
                categoriesSet.add (o);
            }
            else
                device.setCategories (Collections.singleton (mapped));
        }
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
     * Load and parse the JS file.
     *
     * @param filename The name of the file from which to parse
     * @param logModel For logging
     * @param categoriesSet The categories
     * @param vendorsSet The vendors
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
}
