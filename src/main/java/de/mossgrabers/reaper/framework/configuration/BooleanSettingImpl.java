// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IBooleanSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.JCheckBox;


/**
 * Reaper implementation of a integer setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BooleanSettingImpl extends BaseSetting<JCheckBox, Boolean> implements IBooleanSetting
{
    private boolean value;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The initial value
     */
    public BooleanSettingImpl (final LogModel logModel, final String label, final String category, final boolean initialValue)
    {
        super (logModel, label, category, new JCheckBox ());

        this.value = initialValue;

        this.field.addActionListener (event -> SafeRunLater.execute (BooleanSettingImpl.this.logModel, () -> this.set (BooleanSettingImpl.this.field.isSelected ())));
    }


    /** {@inheritDoc} */
    @Override
    public void set (final Boolean value)
    {
        this.set (value.booleanValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void set (final boolean value)
    {
        this.value = value;
        this.flush ();

        SafeRunLater.execute (this.logModel, () -> {
            final boolean v = this.field.isSelected ();
            if (v != this.value)
                this.field.setSelected (this.value);
        });
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.notifyObservers (Boolean.valueOf (this.value));
    }


    /** {@inheritDoc} */
    @Override
    public void store (final PropertiesEx properties)
    {
        properties.put (this.getID (), Boolean.toString (this.value));
    }


    /** {@inheritDoc} */
    @Override
    public void load (final PropertiesEx properties)
    {
        this.set (properties.getBoolean (this.getID (), this.value));
    }
}
