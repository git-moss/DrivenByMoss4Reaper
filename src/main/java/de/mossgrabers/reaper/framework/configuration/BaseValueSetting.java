// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IValueSetting;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.reaper.ui.utils.LogModel;

import javax.swing.JComponent;


/**
 * Base class for settings.
 *
 * @param <C> The type of the widget to edit the setting, e.g. a text field
 * @param <T> The type of the settings value
 *
 * @author Jürgen Moßgraber
 */
public abstract class BaseValueSetting<C extends JComponent, T> extends BaseSetting<C, T> implements IValueSetting<T>
{
    boolean isDirty = false;


    /**
     * Constructor
     *
     * @param logModel The log model
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param field The widget for editing the setting
     */
    protected BaseValueSetting (final LogModel logModel, final String label, final String category, final C field)
    {
        super (logModel, label, category, field);
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        this.set (this.get ());
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<T> observer)
    {
        this.observers.add (observer);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDirty ()
    {
        return this.isDirty;
    }


    /**
     * Set to dirty if edited.
     */
    protected void setDirty ()
    {
        this.isDirty = true;
    }
}
