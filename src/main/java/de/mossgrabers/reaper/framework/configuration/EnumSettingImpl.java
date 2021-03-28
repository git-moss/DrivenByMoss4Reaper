// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;


/**
 * Reaper implementation of an enumeration setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EnumSettingImpl extends BaseValueSetting<JComboBox<String>, String> implements IEnumSetting
{
    private final String initialValue;
    private String       value;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param properties Where to load from
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param options The string array that defines the allowed options for the button group or
     *            chooser
     * @param initialValue The initial value
     */
    public EnumSettingImpl (final LogModel logModel, final PropertiesEx properties, final String label, final String category, final String [] options, final String initialValue)
    {
        super (logModel, label, category, new JComboBox<> (new DefaultComboBoxModel<> (options)));

        this.initialValue = initialValue;
        this.load (properties);

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
        this.setDirty ();
        this.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public String get ()
    {
        return this.value;
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
        this.set (properties.getString (this.getID (), this.initialValue));
    }


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean enable)
    {
        this.field.setEnabled (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void reset ()
    {
        this.set (this.initialValue);
    }
}
