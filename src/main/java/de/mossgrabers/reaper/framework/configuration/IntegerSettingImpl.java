// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.transformator.util.PropertiesEx;

import javafx.application.Platform;
import javafx.scene.control.TextField;


/**
 * Reaper implementation of a integer setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class IntegerSettingImpl extends BaseSetting<TextField, Integer> implements IIntegerSetting
{
    private final int minValue;
    private final int maxValue;
    private int       value;


    /**
     * Constructor.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The value
     * @param minValue The minimum accepted value
     * @param maxValue The maximum accepted value
     */
    public IntegerSettingImpl (final String label, final String category, final int initialValue, final int minValue, final int maxValue)
    {
        super (label, category, new TextField (Integer.toString (initialValue)));
        this.value = initialValue;
        this.minValue = minValue;
        this.maxValue = maxValue;

        limitToNumbers (this.field, NUMBERS);
        this.field.textProperty ().addListener ( (observable, oldValue, newValue) -> {
            try
            {
                this.set (Integer.parseInt (newValue));
            }
            catch (final NumberFormatException ex)
            {
                // Ignore
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
        this.flush ();

        Platform.runLater ( () -> {
            final String v = this.field.textProperty ().get ();
            if (!v.equals (Integer.toString (this.value)))
                this.field.textProperty ().set (Integer.toString (this.value));
        });
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.notifyObservers (Integer.valueOf (this.value - 1));
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
        this.set (properties.getInt (this.getID (), this.value));
    }
}
