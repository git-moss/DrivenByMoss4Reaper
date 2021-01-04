// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.configuration.IBooleanSetting;
import de.mossgrabers.framework.configuration.IColorSetting;
import de.mossgrabers.framework.configuration.IDoubleSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;

import java.util.ArrayList;
import java.util.List;


/**
 * The Reaper implementation to create user interface widgets for settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractSettingsUI implements ISettingsUI
{
    protected final MessageSender    sender;
    protected final PropertiesEx     properties;
    protected final LogModel         logModel;
    protected final List<IfxSetting> settings = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param logModel The log model
     * @param properties Where to store to
     * @param sender The sender
     */
    public AbstractSettingsUI (final LogModel logModel, final PropertiesEx properties, final MessageSender sender)
    {
        this.logModel = logModel;
        this.properties = properties;
        this.sender = sender;
    }


    /**
     * Flushes the values of all settings.
     */
    public void flush ()
    {
        this.settings.forEach (s -> {
            try
            {
                s.flush ();
            }
            catch (final RuntimeException ex)
            {
                this.logModel.error ("Could not flush setting.", ex);
            }
        });
    }


    /**
     * Init all settings.
     */
    public void init ()
    {
        this.settings.forEach (IfxSetting::init);
    }


    /**
     * Get all settings.
     *
     * @return The settings
     */
    public List<IfxSetting> getSettings ()
    {
        return this.settings;
    }


    /** {@inheritDoc} */
    @Override
    public IEnumSetting getEnumSetting (final String label, final String category, final String [] options, final String initialValue)
    {
        final EnumSettingImpl setting = new EnumSettingImpl (this.logModel, this.properties, label, category, options, initialValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IStringSetting getStringSetting (final String label, final String category, final int numChars, final String initialText)
    {
        final StringSettingImpl setting = new StringSettingImpl (this.logModel, this.properties, label, category, initialText);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IDoubleSetting getNumberSetting (final String label, final String category, final double minValue, final double maxValue, final double stepResolution, final String unit, final double initialValue)
    {
        final DoubleSettingImpl setting = new DoubleSettingImpl (this.logModel, this.properties, label, category, initialValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IIntegerSetting getRangeSetting (final String label, final String category, final int minValue, final int maxValue, final int stepResolution, final String unit, final int initialValue)
    {
        final IntegerSettingImpl setting = new IntegerSettingImpl (this.logModel, this.properties, label, category, initialValue, minValue, maxValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public ISignalSetting getSignalSetting (final String label, final String category, final String title)
    {
        final SignalSettingImpl setting = new SignalSettingImpl (this.logModel, this.properties, label, category, title);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IColorSetting getColorSetting (final String label, final String category, final ColorEx defaultColor)
    {
        final ColorSettingImpl setting = new ColorSettingImpl (this.logModel, this.properties, label, category, defaultColor);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IBooleanSetting getBooleanSetting (final String label, final String category, final boolean initialValue)
    {
        final BooleanSettingImpl setting = new BooleanSettingImpl (this.logModel, this.properties, label, category, initialValue);
        this.settings.add (setting);
        return setting;
    }


    /** {@inheritDoc} */
    @Override
    public IActionSetting getActionSetting (final String label, final String category)
    {
        final ActionSettingImpl setting = new ActionSettingImpl (this.sender, this.logModel, this.properties, label, category);
        this.settings.add (setting);
        return setting;
    }
}
