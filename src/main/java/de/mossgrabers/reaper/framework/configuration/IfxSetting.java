// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.ISetting;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;

import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * Interface to editing a setting with a Java FX control widget.
 *
 * @param <T> The type of the settings value
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IfxSetting<T> extends ISetting<T>
{
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
}
