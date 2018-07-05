// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework;

import de.mossgrabers.transformator.util.LogModel;

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
    private static final String REAPER_MAIN     = "REAPER.ini";
    private static final String VST_PLUGINS_64  = "reaper-vstplugins64.ini";
    private static final String FX_TAGS         = "reaper-fxtags.ini";
    private static final String FX_FOLDERS      = "reaper-fxfolders.ini";

    private final IniEditor     iniReaperMain   = new IniEditor ();
    private final IniEditor     iniVstPlugins64 = new IniEditor ();
    private final IniEditor     iniFxTags       = new IniEditor ();
    private final IniEditor     iniFxFolders    = new IniEditor ();

    private String              iniPath;

    private boolean             isMainPresent;
    private boolean             isVstPresent;
    private boolean             isFxTagsPresent;
    private boolean             isFxFoldersPresent;


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

        this.isMainPresent = loadINIFile (iniPath + File.separator + REAPER_MAIN, this.iniReaperMain, logModel);
        this.isVstPresent = loadINIFile (iniPath + File.separator + VST_PLUGINS_64, this.iniVstPlugins64, logModel);
        this.isFxTagsPresent = loadINIFile (iniPath + File.separator + FX_TAGS, this.iniFxTags, logModel);
        this.isFxFoldersPresent = loadINIFile (iniPath + File.separator + FX_FOLDERS, this.iniFxFolders, logModel);
    }


    /**
     * Get the main Reaper config file.
     *
     * @return The file
     */
    public IniEditor getIniReaperMain ()
    {
        return this.iniReaperMain;
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
            logModel.addLogMessage (filename + " not present, skipped loading.");
        }
        catch (final IOException ex)
        {
            logModel.addLogMessage ("Could not load file: " + filename);
            logModel.addLogMessage (ex.getClass () + ":" + ex.getMessage ());

        }
        return false;
    }
}
