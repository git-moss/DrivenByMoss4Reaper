// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;
import de.mossgrabers.reaper.ui.widget.ColoredButton;

import javax.swing.JColorChooser;

import java.awt.Color;


/**
 * Reaper implementation of a ColorEx setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorSettingImpl extends BaseSetting<ColoredButton, double []> implements IColorSetting
{
    private ColorEx value;


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param initialValue The value
     */
    public ColorSettingImpl (final LogModel logModel, final String label, final String category, final ColorEx initialValue)
    {
        super (logModel, label, category, new ColoredButton ());
        this.value = initialValue;

        this.field.setBackground (new Color ((float) initialValue.getRed (), (float) initialValue.getGreen (), (float) initialValue.getBlue ()));

        this.field.addActionListener (event -> {
            final Color color = JColorChooser.showDialog (this.field, "Pick color", this.field.getBackground ());
            this.set (color.getRed () / 255.0, color.getGreen () / 255.0, color.getBlue () / 255.0);
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

        SafeRunLater.execute (this.logModel, () -> {
            this.field.setBackground (new Color ((float) this.value.getRed (), (float) this.value.getGreen (), (float) this.value.getBlue ()));
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
