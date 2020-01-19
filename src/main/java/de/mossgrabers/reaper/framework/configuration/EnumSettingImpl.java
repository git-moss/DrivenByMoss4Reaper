// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;


/**
 * Reaper implementation of an enum setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EnumSettingImpl extends BaseSetting<JComboBox<String>, String> implements IEnumSetting
{
    private String value;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param options The string array that defines the allowed options for the button group or
     *            chooser
     * @param initialValue The initial value
     */
    public EnumSettingImpl (final LogModel logModel, final String label, final String category, final String [] options, final String initialValue)
    {
        super (logModel, label, category, new JComboBox<> (new DefaultComboBoxModel<> (options)));
        this.value = initialValue;

        this.field.setSelectedItem (this.value);
        this.field.addItemListener (event -> SafeRunLater.execute (EnumSettingImpl.this.logModel, () -> this.set ((String) this.field.getSelectedItem ())));
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.setInternal (value);

        SafeRunLater.execute (this.logModel, () -> {
            if (this.value != null && !this.value.equals (this.field.getSelectedItem ()))
                this.field.setSelectedItem (this.value);
        });
    }


    private void setInternal (final String value)
    {
        this.value = value;
        this.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.notifyObservers (this.value);
    }


    /** {@inheritDoc} */
    @Override
    public void store (final PropertiesEx properties)
    {
        properties.put (this.getID (), this.value);
    }


    /** {@inheritDoc} */
    @Override
    public void load (final PropertiesEx properties)
    {
        this.set (properties.getString (this.getID (), this.value));
    }


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean enable)
    {
        this.field.setEnabled (enable);
    }
}
