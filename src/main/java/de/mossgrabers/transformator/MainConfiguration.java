// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator;

import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.transformator.util.PropertiesEx;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * The main configuration file.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MainConfiguration extends PropertiesEx
{
    private static final long   serialVersionUID            = 2863435764890303358L;

    private static final String CONFIG_FILENAME             = "DrivenByMoss4Reaper.config";

    private static final String TAG_PREVIEW                 = "PREVIEW";
    private static final String TAG_DAW_COMMAND             = "DAW_COMMAND";
    private static final String TAG_RUN_AUTOMATICALLY       = "RUN_AUTOMATICALLY";
    private static final String TAG_TCP_PORT                = "TCP_PORT";

    private static final String DEFAULT_REAPER_PATH_WINDOWS = "C:\\Program Files\\REAPER (x64)\\reaper.exe";
    private static final String DEFAULT_REAPER_PATH_MAC     = "/Applications/REAPER64.app";
    private static final String DEFAULT_REAPER_PATH_LINUX   = "";


    /**
     * Load the settings from the config file.
     *
     * @throws IOException Could not load configuration file
     */
    public void load () throws IOException
    {
        final File configFile = new File (CONFIG_FILENAME);
        if (!configFile.exists ())
            return;

        try (final FileReader reader = new FileReader (configFile))
        {
            this.load (reader);
        }
    }


    /**
     * Store the settings to the config file.
     *
     * @throws IOException Could not store
     */
    public void save () throws IOException
    {
        final File configFile = new File (CONFIG_FILENAME);
        try (final FileWriter writer = new FileWriter (configFile))
        {
            this.store (writer, "");
        }
    }


    /**
     * Returns true if display preview is enabled.
     *
     * @return True if display preview is enabled
     */
    public boolean isPreviewEnabled ()
    {
        return this.getBoolean (TAG_PREVIEW, true);
    }


    /**
     * Set if display preview is enabled.
     *
     * @param enablePreview True to enable display preview
     */
    public void setEnablePreview (final boolean enablePreview)
    {
        this.putBoolean (TAG_PREVIEW, enablePreview);
    }


    /**
     * Get the full path to the DAW application.
     *
     * @return The full path
     */
    public String getApplicationCommand ()
    {
        return this.getString (TAG_DAW_COMMAND, this.getDefaultApplicationPath ());
    }


    /**
     * Set the full path to the DAW application.
     *
     * @param applicationCommand The full path
     */
    public void setApplicationCommand (final String applicationCommand)
    {
        this.putString (TAG_DAW_COMMAND, applicationCommand);
    }


    /**
     * Returns true if the automatic start of the DAW is enabled.
     *
     * @return True if the automatic start of the DAW is enabled
     */
    public boolean isRunAutomatically ()
    {
        return this.getBoolean (TAG_RUN_AUTOMATICALLY, true);
    }


    /**
     * Set if the automatic start of the DAW is enabled.
     *
     * @param runAutomatically True to enable the automatic start of the DAW
     */
    public void setRunAutomatically (final boolean runAutomatically)
    {
        this.putBoolean (TAG_RUN_AUTOMATICALLY, runAutomatically);
    }


    /**
     * Get the TCP port for the communication with the DAW.
     *
     * @return The TCP port
     */
    public int getTcpPort ()
    {
        return this.getInt (TAG_TCP_PORT, 1200);
    }


    /**
     * Set the TCP port for the communication with the DAW.
     *
     * @param tcpPort The TCP port
     */
    public void setTcpPort (final int tcpPort)
    {
        this.putInt (TAG_TCP_PORT, tcpPort);
    }


    protected String getDefaultApplicationPath ()
    {
        switch (OperatingSystem.get ())
        {
            case WINDOWS:
                return DEFAULT_REAPER_PATH_WINDOWS;
            case MAC:
                return DEFAULT_REAPER_PATH_MAC;
            case LINUX:
                return DEFAULT_REAPER_PATH_LINUX;
            default:
                // No support for other OS
                return "";
        }
    }
}
