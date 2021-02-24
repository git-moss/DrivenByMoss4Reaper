// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework;

import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.reaper.ui.utils.LogModel;

import com.nikhaldimann.inieditor.IniEditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;


/**
 * Manages access to the different Reaper INI files.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class IniFiles
{
    private static final String OPTION_FORMAT_NO_SPACES = "%s%s%s";
    private static final String REAPER_MAIN             = "REAPER.ini";
    private static final String REAPER_MAIN2            = "reaper.ini";
    private static final String VST_PLUGINS_64          = "reaper-vstplugins64.ini";
    private static final String VST_PLUGINS_ARM64       = "reaper-vstplugins_arm64.ini";
    private static final String AU_PLUGINS_64           = "reaper-auplugins64.ini";
    private static final String AU_PLUGINS_ARM64        = "reaper-auplugins_arm64.ini";
    private static final String FX_TAGS                 = "reaper-fxtags.ini";
    private static final String FX_FOLDERS              = "reaper-fxfolders.ini";

    private final IniEditor     iniReaperMain           = new IniEditor ();
    private final IniEditor     iniVstPlugins64         = new IniEditor ();
    private final IniEditor     iniVstPluginsARM64      = new IniEditor ();
    private final IniEditor     iniFxTags               = new IniEditor ();
    private final IniEditor     iniFxFolders            = new IniEditor ();
    private String              iniAuPlugins64Content;
    private String              iniAuPluginsARM64Content;

    private String              iniPath;

    private boolean             isVstPresent;
    private boolean             isVstARMPresent;
    private boolean             isAuPresent             = false;
    private boolean             isAuARMPresent          = false;
    private boolean             isFxTagsPresent;
    private boolean             isFxFoldersPresent;

    /**
     * Constructor.
     */
    public IniFiles ()
    {
        this.iniReaperMain.setOptionFormatString (OPTION_FORMAT_NO_SPACES);
        this.iniVstPlugins64.setOptionFormatString (OPTION_FORMAT_NO_SPACES);
        this.iniFxTags.setOptionFormatString (OPTION_FORMAT_NO_SPACES);
        this.iniFxFolders.setOptionFormatString (OPTION_FORMAT_NO_SPACES);
    }


    /**
     * Get the INI path.
     *
     * @return The INI path
     */
    public String getIniPath ()
    {
        return this.iniPath;
    }


    /**
     * Load all INI files.
     *
     * @param iniPath The path to the INI files
     * @param logModel Where to log errors
     */
    public void init (final String iniPath, final LogModel logModel)
    {
        this.iniPath = iniPath;

        synchronized (this.iniReaperMain)
        {
            loadINIFile (new String []
            {
                iniPath + File.separator + REAPER_MAIN,
                iniPath + File.separator + REAPER_MAIN2
            }, this.iniReaperMain, logModel);
        }

        this.isVstPresent = loadINIFile (iniPath + File.separator + VST_PLUGINS_64, this.iniVstPlugins64, logModel);

        final OperatingSystem os = OperatingSystem.get ();
        if (os == OperatingSystem.MAC || os == OperatingSystem.MAC_ARM)
        {
            final File iniAuPlugins64 = new File (iniPath + File.separator + AU_PLUGINS_64);
            if (iniAuPlugins64.exists ())
            {
                try
                {
                    this.iniAuPlugins64Content = Files.readString (iniAuPlugins64.toPath (), Charset.defaultCharset ());
                    this.isAuPresent = true;
                }
                catch (final IOException ex)
                {
                    logModel.error ("Could not load AU configuration file.", ex);
                }
            }

            if (os == OperatingSystem.MAC_ARM)
            {
                this.isVstARMPresent = loadINIFile (iniPath + File.separator + VST_PLUGINS_ARM64, this.iniVstPluginsARM64, logModel);

                final File iniAuPluginsARM64 = new File (iniPath + File.separator + AU_PLUGINS_ARM64);
                if (iniAuPluginsARM64.exists ())
                {
                    try
                    {
                        this.iniAuPluginsARM64Content = Files.readString (iniAuPluginsARM64.toPath (), Charset.defaultCharset ());
                        this.isAuARMPresent = true;
                    }
                    catch (final IOException ex)
                    {
                        logModel.error ("Could not load AU configuration file.", ex);
                    }
                }
            }
        }

        this.isFxTagsPresent = loadINIFile (iniPath + File.separator + FX_TAGS, this.iniFxTags, logModel);
        this.isFxFoldersPresent = loadINIFile (iniPath + File.separator + FX_FOLDERS, this.iniFxFolders, logModel);
    }


    /**
     * Get the VST plugins config file.
     *
     * @return The file
     */
    public IniEditor getIniVstPlugins64 ()
    {
        return this.iniVstPlugins64;
    }


    /**
     * Get the VST ARM plugins config file.
     *
     * @return The file
     */
    public IniEditor getIniVstPluginsARM64 ()
    {
        return this.iniVstPluginsARM64;
    }


    /**
     * Get the content of the AU plugins config file.
     *
     * @return The file content
     */
    public String getIniAuPlugins64 ()
    {
        return this.iniAuPlugins64Content;
    }


    /**
     * Get the content of the AU ARM plugins config file.
     *
     * @return The file content
     */
    public String getIniAuPluginsARM64 ()
    {
        return this.iniAuPluginsARM64Content;
    }


    /**
     * Get the FX tags config file.
     *
     * @return The file
     */
    public IniEditor getIniFxTags ()
    {
        return this.iniFxTags;
    }


    /**
     * Get the FX folders config file.
     *
     * @return The file
     */
    public IniEditor getIniFxFolders ()
    {
        return this.iniFxFolders;
    }


    /**
     * Is the VST plugins config file present?
     *
     * @return True if successfully loaded
     */
    public boolean isVstPresent ()
    {
        return this.isVstPresent;
    }


    /**
     * Is the VST ARM plugins config file present?
     *
     * @return True if successfully loaded
     */
    public boolean isVstARMPresent ()
    {
        return this.isVstARMPresent;
    }


    /**
     * Is the AU plugins config file present?
     *
     * @return True if successfully loaded
     */
    public boolean isAuPresent ()
    {
        return this.isAuPresent;
    }


    /**
     * Is the AU ARM plugins config file present?
     *
     * @return True if successfully loaded
     */
    public boolean isAuARMPresent ()
    {
        return this.isAuARMPresent;
    }


    /**
     * Is the FX tags config file present?
     *
     * @return True if successfully loaded
     */
    public boolean isFxTagsPresent ()
    {
        return this.isFxTagsPresent;
    }


    /**
     * Is the FX folders config file present?
     *
     * @return True if successfully loaded
     */
    public boolean isFxFoldersPresent ()
    {
        return this.isFxFoldersPresent;
    }


    /**
     * Load an INI file.
     *
     * @param filename The absolute filename to try
     * @param iniFile The ini file
     * @param logModel For logging
     * @return True if successfully loaded
     */
    private static boolean loadINIFile (final String filename, final IniEditor iniFile, final LogModel logModel)
    {
        return loadINIFile (new String []
        {
            filename
        }, iniFile, logModel);
    }


    /**
     * Load an INI file.
     *
     * @param filenames The absolute filenames to try
     * @param iniFile The ini file
     * @param logModel For logging
     * @return True if successfully loaded
     */
    private static boolean loadINIFile (final String [] filenames, final IniEditor iniFile, final LogModel logModel)
    {
        for (final String filename: filenames)
        {
            try
            {
                final File file = new File (filename);
                if (file.exists ())
                {
                    iniFile.load (file.getAbsolutePath ());
                    return true;
                }
            }
            catch (final IOException ex)
            {
                logModel.error ("Could not load file: " + filename, ex);
                return false;
            }
        }

        logModel.info (filenames[0] + " not present, skipped loading.");
        return false;
    }


    /**
     * Get an option value from the main INI file as an integer.
     *
     * @param section The section in the INI file
     * @param option The option name
     * @param defaultValue The default value to return if the value could not be read
     * @return The value
     */
    public int getMainIniInteger (final String section, final String option, final int defaultValue)
    {
        String value;
        synchronized (this.iniReaperMain)
        {
            try
            {
                value = this.iniReaperMain.get (section, option);
            }
            catch (final com.nikhaldimann.inieditor.IniEditor.NoSuchSectionException ex)
            {
                return defaultValue;
            }
        }
        if (value == null)
            return defaultValue;
        try
        {
            return Integer.parseInt (value);
        }
        catch (final NumberFormatException ex)
        {
            return defaultValue;
        }
    }


    /**
     * Set an integer option value in the main INI file. Does not write to the file, only updates
     * the cached value!
     *
     * @param section The section in the INI file
     * @param option The option name
     * @param value The value to set
     */
    public void updateMainIniInteger (final String section, final String option, final int value)
    {
        // Updated the cached values as well
        synchronized (this.iniReaperMain)
        {
            if (!this.iniReaperMain.hasSection (section))
                this.iniReaperMain.addSection (section);
            this.iniReaperMain.set (section, option, Integer.toString (value));
        }
    }


    /**
     * Get an option value from the main INI file as a double.
     *
     * @param section The section in the INI file
     * @param option The option name
     * @param defaultValue The default value to return if the value could not be read
     * @return The value
     */
    public double getMainIniDouble (final String section, final String option, final double defaultValue)
    {
        String value;
        synchronized (this.iniReaperMain)
        {
            try
            {
                value = this.iniReaperMain.get (section, option);
            }
            catch (final com.nikhaldimann.inieditor.IniEditor.NoSuchSectionException ex)
            {
                return defaultValue;
            }
        }
        if (value == null)
            return defaultValue;
        try
        {
            return Double.parseDouble (value);
        }
        catch (final NumberFormatException ex)
        {
            return defaultValue;
        }
    }
}
