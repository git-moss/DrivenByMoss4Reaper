// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;

import javax.swing.JTextField;


/**
 * Reaper implementation of a double setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DoubleSettingImpl extends BaseSetting<JTextField, Double> implements IDoubleSetting
{
    private double value;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The value
     */
    public DoubleSettingImpl (final LogModel logModel, final String label, final String category, final double initialValue)
    {
        super (logModel, label, category, new JTextField (Double.toString (initialValue)));

        this.value = initialValue;

        limitToNumbers (this.field, NUMBERS_AND_DOT);
        this.field.addActionListener (event -> {
            try
            {
                this.set (Double.parseDouble (this.field.getText ()));
            }
            catch (final NumberFormatException ex)
            {
                // Ignore
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


    /** {@inheritDoc} */
    @Override
    public void load (final PropertiesEx properties)
    {
        this.set (properties.getDouble (this.getID (), this.value));
    }
}
