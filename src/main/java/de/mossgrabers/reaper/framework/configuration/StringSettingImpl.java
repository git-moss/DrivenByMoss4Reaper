// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.transformator.util.PropertiesEx;
import de.mossgrabers.transformator.util.SafeRunLater;

import javafx.scene.control.TextField;


/**
 * Reaper implementation of a string setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StringSettingImpl extends BaseSetting<TextField, String> implements IStringSetting
{
    private String value;


    /**
     * Constructor.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The initial value
     */
    public StringSettingImpl (final String label, final String category, final String initialValue)
    {
        super (label, category, new TextField (initialValue));
        this.value = initialValue;

        this.field.textProperty ().addListener ( (observable, oldValue, newValue) -> this.set (newValue));
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.value = value;
        this.flush ();

        SafeRunLater.execute ( () -> {
            final String v = this.field.textProperty ().get ();
            if (v == null || !v.equals (this.value))
                this.field.textProperty ().set (this.value);
        });
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
}
