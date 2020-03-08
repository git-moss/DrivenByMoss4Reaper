// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.JFormattedTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;


/**
 * Reaper implementation of a double setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DoubleSettingImpl extends BaseSetting<JFormattedTextField, Double> implements IDoubleSetting
{
    private double value;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param properties Where to load from
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The value
     */
    public DoubleSettingImpl (final LogModel logModel, final PropertiesEx properties, final String label, final String category, final double initialValue)
    {
        super (logModel, label, category, new JFormattedTextField (NumberFormat.getNumberInstance ()));

        this.value = properties.getDouble (this.getID (), initialValue);

        this.field.setValue (Double.valueOf (initialValue));
        this.field.addKeyListener (new KeyAdapter ()
        {
            /** {@inheritDoc} */
            @Override
            public void keyTyped (final KeyEvent e)
            {
                SafeRunLater.execute (DoubleSettingImpl.this.logModel, () -> {
                    try
                    {
                        DoubleSettingImpl.this.set (Double.parseDouble (DoubleSettingImpl.this.field.getText ()));
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
    public void set (final Double value)
    {
        this.set (value.doubleValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void set (final double value)
    {
        this.value = value;
        this.flush ();

        SafeRunLater.execute (this.logModel, () -> {
            final String v = this.field.getText ();
            final String doubleStr = Double.toString (this.value);
            if (!v.equals (doubleStr))
                this.field.setText (doubleStr);
        });
    }


    /** {@inheritDoc} */
    @Override
    public Double get ()
    {
        return Double.valueOf (this.value);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.notifyObservers (Double.valueOf (this.value));
    }


    /** {@inheritDoc} */
    @Override
    public void store (final PropertiesEx properties)
    {
        properties.put (this.getID (), Double.toString (this.value));
    }
}
