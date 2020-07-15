// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * The Reaper implementation to create user interface widgets for document (project) specific
 * settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DocumentSettingsUI extends AbstractSettingsUI
{
    /**
     * Constructor.
     *
     * @param logModel The log model
     */
    public DocumentSettingsUI (final LogModel logModel)
    {
        super (logModel, new PropertiesEx (), null);
    }


    /**
     * Store all settings to the properties.
     *
     * @return The properties
     */
    public PropertiesEx store ()
    {
        this.settings.forEach (s -> s.store (this.properties));
        return this.properties;
    }


    /**
     * Parse the settings into the properties.
     *
     * @param propertiesText The properties text
     */
    public void parse (final String propertiesText)
    {
        final StringReader reader = new StringReader (propertiesText);
        try
        {
            this.properties.load (reader);
        }
        catch (final IOException ex)
        {
            final StringWriter sw = new StringWriter ();
            ex.printStackTrace (new PrintWriter (sw));
            this.logModel.info (sw.toString ());
        }

        this.settings.forEach (s -> s.load (this.properties));
    }
}
