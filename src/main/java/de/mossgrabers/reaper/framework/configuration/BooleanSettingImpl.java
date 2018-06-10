// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IBooleanSetting;
import de.mossgrabers.transformator.util.PropertiesEx;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;


/**
 * Reaper implementation of a integer setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BooleanSettingImpl extends BaseSetting<CheckBox, Boolean> implements IBooleanSetting
{
    private boolean value;


    /**
     * Constructor.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The initial value
     */
    public BooleanSettingImpl (final String label, final String category, final boolean initialValue)
    {
        super (label, category, new CheckBox ());
        this.value = initialValue;

        this.field.selectedProperty ().addListener ( (observable, oldValue, newValue) -> this.set (newValue));
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

        Platform.runLater ( () -> {
            final boolean v = this.field.selectedProperty ().get ();
            if (v != this.value)
                this.field.selectedProperty ().set (this.value);
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
