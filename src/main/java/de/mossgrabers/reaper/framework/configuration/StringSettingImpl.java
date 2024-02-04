// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Reaper implementation of a string setting.
 *
 * @author Jürgen Moßgraber
 */
public class StringSettingImpl extends BaseValueSetting<JTextField, String> implements IStringSetting
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
     * @param initialValue The initial value
     */
    public StringSettingImpl (final LogModel logModel, final PropertiesEx properties, final String label, final String category, final String initialValue)
    {
        super (logModel, label, category, new JTextField (initialValue));

        this.initialValue = initialValue;
        this.load (properties);

        this.field.addKeyListener (new KeyAdapter ()
        {
            /** {@inheritDoc} */
            @Override
            public void keyTyped (final KeyEvent e)
            {
                SafeRunLater.execute (StringSettingImpl.this.logModel, () -> StringSettingImpl.this.set (StringSettingImpl.this.field.getText ()));
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.value = value;
        this.setDirty ();
        this.flush ();

        SafeRunLater.execute (this.logModel, () -> {
            final String v = this.field.getText ();
            if (v == null || !v.equals (this.value))
                this.field.setText (this.value);
        });
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
        this.notifyObservers (this.value == null ? "" : this.value);
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
    public void reset ()
    {
        this.set (this.initialValue);
    }
}
