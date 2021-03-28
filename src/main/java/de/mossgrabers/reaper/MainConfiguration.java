// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper;

import de.mossgrabers.reaper.ui.utils.PropertiesEx;

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
    private static final long   serialVersionUID = 2863435764890303358L;

    private static final String CONFIG_FILENAME  = "DrivenByMoss4Reaper.config";


    /**
     * Load the settings from the configuration file.
     *
     * @param path The path where the configuration file is stored
     * @throws IOException Could not load configuration file
     */
    public void load (final String path) throws IOException
    {
        final File configFile = new File (path, CONFIG_FILENAME);
        if (!configFile.exists ())
            return;

        try (final FileReader reader = new FileReader (configFile))
        {
            this.load (reader);
        }
    }


    /**
     * Store the settings to the configuration file.
     *
     * @param path The path where the configuration file is stored
     * @throws IOException Could not store
     */
    public void save (final String path) throws IOException
    {
        final File configFile = new File (path, CONFIG_FILENAME);
        try (final FileWriter writer = new FileWriter (configFile))
        {
            this.store (writer, "");
        }
    }
}
