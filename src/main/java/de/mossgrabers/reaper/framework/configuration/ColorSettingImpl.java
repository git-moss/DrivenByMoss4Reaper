// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.transformator.util.PropertiesEx;

import javafx.application.Platform;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;


/**
 * Reaper implementation of a ColorEx setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorSettingImpl extends BaseSetting<ColorPicker, double []> implements IColorSetting
{
    private ColorEx value;


    /**
     * Constructor.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The value
     */
    public ColorSettingImpl (final String label, final String category, final ColorEx initialValue)
    {
        super (label, category, new ColorPicker (Color.color (initialValue.getRed (), initialValue.getGreen (), initialValue.getBlue ())));
        this.value = initialValue;

        this.field.setMaxWidth (Double.MAX_VALUE);
        this.field.setMinHeight (30);
        this.field.setOnAction (event -> {
            final Color color = this.field.getValue ();
            this.set (color.getRed (), color.getGreen (), color.getBlue ());
        });
    }


    /** {@inheritDoc} */
    @Override
    public void set (final double [] value)
    {
        this.set (value[0], value[1], value[2]);
    }


    /** {@inheritDoc} */
    @Override
    public void set (final double red, final double green, final double blue)
    {
        this.set (new ColorEx (red, green, blue));
    }


    /** {@inheritDoc} */
    @Override
    public void set (final ColorEx value)
    {
        this.value = value;
        this.flush ();

        Platform.runLater ( () -> {
            final Color c = this.field.getValue ();
            if (this.value.getRed () != c.getRed () || this.value.getGreen () != c.getGreen () || this.value.getBlue () != c.getBlue ())
                this.field.setValue (Color.color (this.value.getRed (), this.value.getGreen (), this.value.getBlue ()));
        });
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.notifyObservers (new double []
        {
            this.value.getRed (),
            this.value.getGreen (),
            this.value.getBlue ()
        });
    }


    /** {@inheritDoc} */
    @Override
    public void store (final PropertiesEx properties)
    {
        properties.put (this.getID (), this.value.getRed () + "," + this.value.getGreen () + "," + this.value.getBlue ());
    }


    /** {@inheritDoc} */
    @Override
    public void load (final PropertiesEx properties)
    {
        final String color = properties.getString (this.getID ());
        if (color == null)
            return;
        final String [] parts = color.split (",");
        if (parts.length == 3)
            this.set (Double.parseDouble (parts[0]), Double.parseDouble (parts[1]), Double.parseDouble (parts[2]));
    }
}
