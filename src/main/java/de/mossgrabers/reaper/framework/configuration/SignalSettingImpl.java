// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.transformator.util.PropertiesEx;

import javafx.scene.control.Button;


/**
 * Reaper implementation of a string setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SignalSettingImpl extends BaseSetting<Button, Void> implements ISignalSetting
{
    /**
     * Constructor.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param title The title of the button
     */
    public SignalSettingImpl (final String label, final String category, final String title)
    {
        super (label, category, new Button (title));

        this.field.setMaxWidth (Double.MAX_VALUE);
        this.field.setOnAction (event -> this.notifyObservers (null));
    }


    /** {@inheritDoc} */
    @Override
    public void set (final Void value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void store (final PropertiesEx properties)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void load (final PropertiesEx properties)
    {
        // Intentionally empty
    }
}
