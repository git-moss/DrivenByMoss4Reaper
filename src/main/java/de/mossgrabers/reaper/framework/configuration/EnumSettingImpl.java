// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.transformator.util.PropertiesEx;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;


/**
 * Reaper implementation of an enum setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EnumSettingImpl extends BaseSetting<ComboBox<String>, String> implements IEnumSetting
{
    private String value;


    /**
     * Constructor.
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param options The string array that defines the allowed options for the button group or
     *            chooser
     * @param initialValue The initial value
     */
    public EnumSettingImpl (final String label, final String category, final String [] options, final String initialValue)
    {
        super (label, category, new ComboBox<> (FXCollections.observableArrayList (options)));
        this.value = initialValue;

        final SingleSelectionModel<String> selectionModel = this.field.getSelectionModel ();
        selectionModel.select (this.value);
        selectionModel.selectedItemProperty ().addListener ( (observable, oldValue, newValue) -> this.set (newValue));
        this.field.setMaxWidth (Double.MAX_VALUE);
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.value = value;
        this.flush ();

        Platform.runLater ( () -> {
            final SingleSelectionModel<String> selectionModel = this.field.getSelectionModel ();
            final ReadOnlyObjectProperty<String> selectedItemProperty = selectionModel.selectedItemProperty ();
            if (this.value != null && !this.value.equals (selectedItemProperty.get ()))
                selectionModel.select (this.value);
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


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean enable)
    {
        this.field.disableProperty ().set (!enable);
    }
}
