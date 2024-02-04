// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.ISetting;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;

import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * Interface to editing a setting with a Java FX control widget.
 *
 * @author Jürgen Moßgraber
 */
public interface IfxSetting extends ISetting
{
    /**
     * Set and fire the initial or loaded value, if present.
     */
    void init ();


    /**
     * Flushing out the value of the control to all observers.
     */
    void flush ();


    /**
     * Create an ID for the setting.
     *
     * @return The ID
     */
    String getID ();


    /**
     * Get the label.
     *
     * @return The label
     */
    String getLabel ();


    /**
     * Get the category.
     *
     * @return The category
     */
    String getCategory ();


    /**
     * Create a label widget for the setting.
     *
     * @return The label
     */
    JLabel getLabelWidget ();


    /**
     * Get the widget to edit the value of this setting.
     *
     * @return The widget
     */
    JComponent getWidget ();


    /**
     * Store the setting.
     *
     * @param properties Where to store to
     */
    void store (PropertiesEx properties);


    /**
     * Load the setting.
     *
     * @param properties Where to load from
     */
    void load (PropertiesEx properties);


    /**
     * Set the default value.
     */
    void reset ();


    /**
     * Check if the value has been changed since loaded from the settings.
     *
     * @return True if dirty
     */
    boolean isDirty ();
}
