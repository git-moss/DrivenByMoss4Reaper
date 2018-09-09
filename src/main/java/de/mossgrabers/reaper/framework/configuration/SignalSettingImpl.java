// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;

import javax.swing.JButton;


/**
 * Reaper implementation of a string setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SignalSettingImpl extends BaseSetting<JButton, Void> implements ISignalSetting
{
    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param title The title of the button
     */
    public SignalSettingImpl (final LogModel logModel, final String label, final String category, final String title)
    {
        super (logModel, label, category, new JButton (title));

        this.field.addActionListener (event -> this.notifyObservers (null));
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
