// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework;

import de.mossgrabers.reaper.ui.utils.LogModel;

import com.nikhaldimann.inieditor.IniEditor;

import java.io.File;
import java.io.IOException;


/**
 * Manages access to the different Reaper INI files.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class IniFiles
{
    private static final String OPTION_FORMAT_NO_SPACES = "%s%s%s";
    private static final String REAPER_MAIN             = "REAPER.ini";
    private static final String VST_PLUGINS_64          = "reaper-vstplugins64.ini";
    private static final String FX_TAGS                 = "reaper-fxtags.ini";
    private static final String FX_FOLDERS              = "reaper-fxfolders.ini";

    private final IniEditor     iniReaperMain           = new IniEditor ();
    private final IniEditor     iniVstPlugins64         = new IniEditor ();
    private final IniEditor     iniFxTags               = new IniEditor ();
    private final IniEditor     iniFxFolders            = new IniEditor ();

    private String              iniPath;

    private boolean             isMainPresent;
    private boolean             isVstPresent;
    private boolean             isFxTagsPresent;
    private boolean             isFxFoldersPresent;
    private LogModel            logModel;


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
        this.logModel = logModel;

        synchronized (this.iniReaperMain)
        {
            this.isMainPresent = loadINIFile (iniPath + File.separator + REAPER_MAIN, this.iniReaperMain, logModel);
        }

        this.isVstPresent = loadINIFile (iniPath + File.separator + VST_PLUGINS_64, this.iniVstPlugins64, logModel);
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
     * Is the main Reaper config file present?
     *
     * @return True if successfully loaded
     */
    public boolean isMainPresent ()
    {
        return this.isMainPresent;
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
     * @param filename The absolute filename
     * @param iniFile The ini file
     * @param logModel For logging
     * @return True if successfully loaded
     */
    private static boolean loadINIFile (final String filename, final IniEditor iniFile, final LogModel logModel)
    {
        try
        {
            final File file = new File (filename);
            if (file.exists ())
            {
                iniFile.load (file.getAbsolutePath ());
                return true;
            }
            logModel.info (filename + " not present, skipped loading.");
        }
        catch (final IOException ex)
        {
            logModel.error ("Could not load file: " + filename, ex);
        }
        return false;
    }


    /**
     * Save the main INI configuration file.
     */
    public void saveMainFile ()
    {
        final String filename = this.iniPath + File.separator + REAPER_MAIN;
        try
        {
            synchronized (this.iniReaperMain)
            {
                this.iniReaperMain.save (filename);
            }
        }
        catch (final IOException ex)
        {
            this.logModel.error ("Could not store main configuration: " + filename, ex);
        }
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
     * Set an integer option value in the main INI file.
     *
     * @param section The section in the INI file
     * @param option The option name
     * @param value The value to set
     */
    public void setMainIniInteger (final String section, final String option, final int value)
    {
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


    /**
     * Set a double option value in the main INI file.
     *
     * @param section The section in the INI file
     * @param option The option name
     * @param value The value to set
     */
    public void setMainIniDouble (final String section, final String option, final double value)
    {
        synchronized (this.iniReaperMain)
        {
            if (!this.iniReaperMain.hasSection (section))
                this.iniReaperMain.addSection (section);
            this.iniReaperMain.set (section, option, Double.toString (value));
        }
    }
}
