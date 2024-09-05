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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private static final DeviceManager                INSTANCE                    = new DeviceManager ();

    private static final String                       SECTION_FOLDERS             = "Folders";
    private static final Pattern                      PATTERN_NAME                = Pattern.compile ("(?<type>[^:]+?)(?<instrument>i?):\\s*(?<name>([^)])+)(\\s*\\((?<company>[^)]+?)\\))?(\\s*\\((?<channels>[^)]+)\\))?");
    private static final Set<String>                  NON_CATEGORIES              = Set.of ("ix", "till", "loser", "liteon", "sstillwell", "teej", "schwa", "u-he", "remaincalm_org");

    private static final Map<Integer, DeviceFileType> TYPE_CODES                  = new HashMap<> (5);
    private static final Map<String, DeviceFileType>  DEVICE_FILE_TYPE_MAP        = new HashMap<> ();
    private static final Map<String, String>          JS_CATEGORY_MAP             = new HashMap<> ();
    private static final Map<String, List<String>>    KNOWN_PLUGIN_CATEGORIES_MAP = new HashMap<> ();
    static
    {
        TYPE_CODES.put (Integer.valueOf (1), DeviceFileType.VST3);
        TYPE_CODES.put (Integer.valueOf (2), DeviceFileType.VST2);
        TYPE_CODES.put (Integer.valueOf (3), DeviceFileType.AU);
        TYPE_CODES.put (Integer.valueOf (4), DeviceFileType.LV2);
        TYPE_CODES.put (Integer.valueOf (5), DeviceFileType.CLAP);

        DEVICE_FILE_TYPE_MAP.put ("VST3", DeviceFileType.VST3);
        DEVICE_FILE_TYPE_MAP.put ("VST", DeviceFileType.VST2);
        DEVICE_FILE_TYPE_MAP.put ("AU", DeviceFileType.AU);
        DEVICE_FILE_TYPE_MAP.put ("LV2", DeviceFileType.LV2);
        DEVICE_FILE_TYPE_MAP.put ("CLAP", DeviceFileType.CLAP);
        DEVICE_FILE_TYPE_MAP.put ("JS", DeviceFileType.JS);

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

        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Delay", List.of ("ColourCopy", "Delay", "MFM2", "ReaDelay", "Replika", "ValhallaDelay", "Ping Pong"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Distortion", List.of ("Bite", "Dirt", "COLDFIRE", "Dist", "Trash", "Saturation", "Vinyl", "Paranoia Mangler", "Tape MELLO-FI"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Dynamics", List.of ("Bus", "Comp", "Expander", "Exciter", "Nectar", "Neutron", "Ozone", "ReaLimit", "Satin", "Solid Bus Comp", "Solid Dynamics", "Supercharger", "Limiter", "Gate", "Transient", "Presswerk", "ReaComp", "ReaXcomp", "Vari Comp", "Enhancer", "Loud", "Satin", "Compciter", "Stereo Upmix", "Zero Crossing Maximizer", "Event Horizon Clipper", "Stereo Width", "Thunderkick", "Booster", "De-esser"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("EQ", List.of ("EQ", "Equalizer", "Kicker"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Filter", List.of ("Driver", "Filter", "ReaFir"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Granular", List.of ("FRAGMENTS", "REFRACT"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Guitar", List.of ("Pre", "VC"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("MIDI", List.of ("Captain", "SChord", "MIDI"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Misc", List.of ("RX", "SpectraLayers", "Lorenz Attractor", "WaveLab"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Modulation", List.of ("Choral", "Chorus", "MOTIONS", "Flair", "Flange", "Stutter", "Ring Modulator", "Waveshaper", "Phasis", "Modulator", "Phaser", "Rotary"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Multi", List.of ("Guitar Rig", "KAOSS", "Surge XT Effects"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Pitch", List.of ("Melodyne", "ReaPitch", "ReaTune"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Reverb", List.of ("Raum", "RC", "ReaVerb", "ReaVerbate", "INTENSITY", "ValhallaRoom", "ValhallaShimmer", "ValhallaSupermassive", "ValhallaVintageVerb", "Twangstrom", "Zebrify", "ZRev", "PLATE", "SPRING", "LX-24", "MDE-X"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Sampler", List.of ("ReaSamplOmatic5000", "sforzando", "Synclavier", "TAL Sampler", "TX16Wx", "Kontakt"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Synth", List.of ("Absynth", "ACE", "Acid", "Analog Lab", "ARP", "Augmented", "B-3", "Battery", "Bazille", "Easel", "Cardinal", "Clavinet", "CMI", "CP-70", "CS-80", "CZ", "DecentSampler", "Diva", "DX7", "Emulator", "Fabric", "Farfisa", "FM8", "Freak", "Hive", "Hype", "Massive", "Mellotron", "microKORG", "Microwave", "MiniBrute", "MiniFreak", "miniKORG", "Modular", "modwave", "MODX", "MonoPoly", "opsix", "Organ", "Padshop", "Piano", "Pigments", "Polysix", "Prophecy", "Prophet", "ReaSynth", "Retrologue", "SEM", "sfizz", "sforzando", "Synth1", "Synthi", "Solina", "SQ80", "Super 8", "TRITON", "VOX Continental", "VOX Super Continental", "wavestate", "Wurli", "Zebra", "Mini D", "Mini V", "ELECTRIBE", "EP-1", "Groove Agent", "HALion", "Jun-6", "Jup-8", "Komplete", "MS-20", "M1", "Maschine", "Matrix-12", "K1v", "Omnisphere", "OP-Xa", "OPx-4", "Reaktor", "ReaSynDr", "Repro", "Stage-73", "Surge XT", "Avenger", "WAVESTATION", "XO (18 out)"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Tools", List.of ("Tune", "ReaCast", "ReaControlMIDI", "ReaInsert", "ReaNINJAM", "ReaSurround", "ReaSurroundPan", "Stereo Mixer", "Polarity", "SwixMitch", "Generator", "Meter", "FFT", "Statistics", "Center Canceler", "Stereo Field Manipulator", "Time Difference Pan", "Goniometer", "Joiner", "Splitter", "Switcher", "ReaStream", "Pseudo-Stereo", "Non-Linear Processor", "Auto-Wideness"));
        KNOWN_PLUGIN_CATEGORIES_MAP.put ("Vocal", List.of ("ReaVocode", "ReaVoice", "VocalSynth", "Vocoder", "Speek"));
    }

    private final List<DeviceMetadataImpl>  devices            = new ArrayList<> ();
    private final List<DeviceMetadataImpl>  instruments        = new ArrayList<> ();
    private final List<DeviceMetadataImpl>  effects            = new ArrayList<> ();
    private final Set<String>               categories         = new TreeSet<> ();
    private final Set<String>               vendors            = new TreeSet<> ();
    private final List<DeviceCollection>    collections        = new ArrayList<> ();
    private final Set<DeviceFileType>       availableFileTypes = new TreeSet<> ();
    private final Map<String, ParameterMap> parameterMaps      = new HashMap<> ();
    private final List<DeviceFileType>      preferredTypes     = new ArrayList<> ();
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
        return this.filterBy (deviceFileType, null, null, null, null);
    }


    /**
     * Filter the devices by category.
     *
     * @param category Filter by device category, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByCategory (final String category)
    {
        return this.filterBy (null, category, null, null, null);
    }


    /**
     * Filter the devices by a vendor.
     *
     * @param vendor Filter by device vendor, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByVendor (final String vendor)
    {
        return this.filterBy (null, null, vendor, null, null);
    }


    /**
     * Filter the devices by a collection.
     *
     * @param collection Filter by device collection, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByCollection (final DeviceCollection collection)
    {
        return this.filterBy (null, null, null, collection, null);
    }


    /**
     * Filter the devices by type.
     *
     * @param deviceType Filter by device type, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterByType (final DeviceType deviceType)
    {
        return this.filterBy (null, null, null, null, deviceType);
    }


    /**
     * Filter the devices by different criteria.
     *
     * @param fileType Filter by device type (plugin format), may be null
     * @param category Filter by device category, may be null
     * @param vendor Filter by device vendor, may be null
     * @param collection Filter by device collection, may be null
     * @param deviceType Filter by device type, may be null
     * @return The devices matching the filter criteria
     */
    public List<DeviceMetadataImpl> filterBy (final DeviceFileType fileType, final String category, final String vendor, final DeviceCollection collection, final DeviceType deviceType)
    {
        List<DeviceMetadataImpl> results = new ArrayList<> ();
        synchronized (this.devices)
        {
            for (final DeviceMetadataImpl d: this.devices)
            {
                if (accept (d, fileType, category, vendor, deviceType))
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


    private static boolean accept (final DeviceMetadataImpl d, final DeviceFileType fileType, final String category, final String vendor, final DeviceType deviceType)
    {
        if (fileType != null && d.getFileType () != fileType || category != null && !d.hasCategory (category))
            return false;

        if (vendor != null && !vendor.equals (d.getVendor ()))
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
     * Add a device to the manager.
     *
     * @param description The description line of the device, e.g. "VSTi: My Plugin (Company X)"
     * @param module The module of the plugin
     */
    public void addDeviceInfo (final String description, final String module)
    {
        final Matcher matcher = PATTERN_NAME.matcher (description);
        if (!matcher.matches ())
            return;

        String deviceName = matcher.group ("name").trim ();
        if (deviceName == null)
            return;

        final String channels = matcher.group ("channels");
        if (channels != null)
            deviceName = deviceName + " (" + channels + ")";

        final String instrument = matcher.group ("instrument");
        final String type = matcher.group ("type");
        if (type == null)
            return;
        final DeviceFileType fileType = DEVICE_FILE_TYPE_MAP.get (type);
        if (fileType == null)
            return;
        final DeviceType deviceType = instrument != null && "i".equals (instrument) ? DeviceType.INSTRUMENT : DeviceType.AUDIO_EFFECT;

        String category = null;
        String company = matcher.group ("company");
        if (fileType == DeviceFileType.JS)
        {
            if (company != null && !"Cockos".equals (company))
            {
                // Some JS plugins have additional descriptions in brackets which are not a
                // company name
                deviceName = deviceName + " (" + company + ")";
                company = null;
            }

            // Parse category or company from first part of module
            final String [] modulePath = module.split ("/");
            if (modulePath.length <= 1)
                return;
            if (NON_CATEGORIES.contains (modulePath[0].toLowerCase (Locale.US)))
                company = modulePath[0];
            else
            {
                final String mappedCategory = JS_CATEGORY_MAP.get (modulePath[0]);
                category = mappedCategory == null ? modulePath[0] : mappedCategory;
            }
        }

        final DeviceMetadataImpl device = new DeviceMetadataImpl (deviceName, module, deviceType, fileType);
        if (company != null)
        {
            device.setVendor (company);
            this.vendors.add (company);
        }

        if (category != null)
        {
            device.addCategory (category);
            this.categories.add (category);
        }

        this.devices.add (device);
        if (deviceType == DeviceType.INSTRUMENT)
            this.instruments.add (device);
        else
            this.effects.add (device);

        this.availableFileTypes.add (fileType);
    }


    /**
     * Load all information from some INI files in Reapers' configuration path.
     *
     * @param iniFiles Access to the INI files
     * @param logModel For logging
     */
    public void applyDeviceInfo (final IniFiles iniFiles, final LogModel logModel)
    {
        this.iniFiles = iniFiles;

        synchronized (this.devices)
        {
            // Load categories and vendor information
            if (iniFiles.isFxTagsPresent ())
            {
                final Set<String> vendorsSet = new TreeSet<> ();
                final Set<String> categoriesSet = new TreeSet<> ();
                this.parseFXTagsFile (iniFiles.getIniFxTags (), categoriesSet, vendorsSet);
                this.categories.addAll (categoriesSet);
                this.vendors.addAll (vendorsSet);
            }

            // Load collection filters
            if (iniFiles.isFxFoldersPresent ())
                this.parseCollectionFilters (iniFiles.getIniFxFolders ());

            // Load device maps
            if (iniFiles.isParamMapsPresent ())
                this.parseParameterMaps (iniFiles.getIniParamMaps ());

            // Improve category assignment
            for (final DeviceMetadataImpl device: this.devices)
            {
                if (!device.isCategorized ())
                {
                    final String deviceName = device.name ();
                    final String category = findCategory (deviceName);
                    if (category != null)
                        device.addCategory (category);
                }
                else if (device.hasCategory ("MIDI"))
                    device.setType (DeviceType.MIDI_EFFECT);

                if (device.hasCategory ("Utility"))
                {
                    device.setCategory ("Tools");
                    this.categories.add ("Tools");
                }
                if (device.hasCategory ("Pitch Shift"))
                {
                    device.setCategory ("Pitch");
                    this.categories.add ("Pitch");
                }
            }

            this.categories.remove ("Utility");
            this.categories.remove ("Pitch");

            // Finally sort the devices by their display name
            this.devices.sort ( (d1, d2) -> d1.getDisplayName ().compareToIgnoreCase (d2.getDisplayName ()));
        }
    }


    private static String findCategory (final String deviceName)
    {
        for (final Map.Entry<String, List<String>> entry: KNOWN_PLUGIN_CATEGORIES_MAP.entrySet ())
        {
            for (String value: entry.getValue ())
            {
                if (deviceName.contains (value))
                    return entry.getKey ();
            }
        }
        return null;
    }


    /**
     * Get the file types present on this system.
     *
     * @return The file types
     */
    public List<DeviceFileType> getAvailableFileTypes ()
    {
        return new ArrayList<> (this.availableFileTypes);
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
            return new ArrayList<> (this.categories);
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
            return new ArrayList<> (this.vendors);
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
                    final DeviceType type = d.getType ();
                    if (type == DeviceType.INSTRUMENT)
                        cats.add ("Synth");
                    else if (type == DeviceType.MIDI_EFFECT)
                        cats.add ("MIDI");
                }
                d.addCategories (cats);
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
     * Get a value from the INI file and convert it to an integer.
     *
     * @param iniFile The INI file
     * @param category The category section
     * @param name The name of the entry
     * @param defaultValue The default value
     * @return The integer value
     */
    private static int getInt (final IniEditor iniFile, final String category, final String name, final int defaultValue)
    {
        final String value = iniFile.get (category, name);
        try
        {
            return value == null ? defaultValue : Integer.parseInt (value);
        }
        catch (final NumberFormatException ex)
        {
            return defaultValue;
        }
    }
}
