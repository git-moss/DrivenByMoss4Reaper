// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.JFormattedTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;


/**
 * Reaper implementation of a integer setting.
 *
 * @author Jürgen Moßgraber
 */
public class IntegerSettingImpl extends BaseValueSetting<JFormattedTextField, Integer> implements IIntegerSetting
{
    private final int minValue;
    private final int maxValue;
    private final int initialValue;
    private int       value;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param properties Where to load from
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The value
     * @param minValue The minimum accepted value
     * @param maxValue The maximum accepted value
     */
    public IntegerSettingImpl (final LogModel logModel, final PropertiesEx properties, final String label, final String category, final int initialValue, final int minValue, final int maxValue)
    {
        super (logModel, label, category, new JFormattedTextField (NumberFormat.getIntegerInstance ()));

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initialValue = initialValue;
        this.load (properties);

        this.field.setValue (Integer.valueOf (initialValue));
        this.field.addKeyListener (new KeyAdapter ()
        {
            /** {@inheritDoc} */
            @Override
            public void keyTyped (final KeyEvent e)
            {
                SafeRunLater.execute (IntegerSettingImpl.this.logModel, () -> {
                    try
                    {
                        IntegerSettingImpl.this.set (Integer.parseInt (IntegerSettingImpl.this.field.getText ()));
                    }
                    catch (final NumberFormatException ex)
                    {
                        // Ignore
                    }
                });
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    public void set (final Integer value)
    {
        this.set (value.intValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void set (final int value)
    {
        if (value < this.minValue || value > this.maxValue)
            return;
        this.value = value;
        this.setDirty ();
        this.flush ();

        SafeRunLater.execute (this.logModel, () -> {
            final String v = this.field.getText ();
            if (!v.equals (Integer.toString (this.value)))
                this.field.setText (Integer.toString (this.value));
        });
    }


    /** {@inheritDoc} */
    @Override
    public Integer get ()
    {
        return Integer.valueOf (this.value);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.notifyObservers (Integer.valueOf (this.value));
    }


    /** {@inheritDoc} */
    @Override
    public void store (final PropertiesEx properties)
    {
        properties.put (this.getID (), Integer.toString (this.value));
    }


    /** {@inheritDoc} */
    @Override
    public void load (final PropertiesEx properties)
    {
        this.set (properties.getInt (this.getID (), this.initialValue));
    }


    /** {@inheritDoc} */
    @Override
    public void reset ()
    {
        this.set (this.initialValue);
    }
}
