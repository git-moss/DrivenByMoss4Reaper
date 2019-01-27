// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.ui.utils.LogModel;

import javax.swing.JComponent;
import javax.swing.JLabel;

import java.util.HashSet;
import java.util.Set;


/**
 * Base class for settings.
 *
 * @param <C> The type of the widget to edit the setting, e.g. a text field
 * @param <T> The type of the settings value
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseSetting<C extends JComponent, T> implements IfxSetting<T>
{
    protected final LogModel             logModel;
    protected final C                    field;

    private final JLabel                 labelWidget;
    private final Set<IValueObserver<T>> observers = new HashSet<> ();
    private final String                 label;
    private final String                 category;


    /**
     * Constructor
     *
     * @param logModel The log model
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param field The widget for editing the setting
     */
    public BaseSetting (final LogModel logModel, final String label, final String category, final C field)
    {
        this.logModel = logModel;
        this.label = label;
        this.category = category;
        this.field = field;
        this.labelWidget = new JLabel (this.label);
    }


    /** {@inheritDoc} */
    @Override
    public String getLabel ()
    {
        return this.label;
    }


    /** {@inheritDoc} */
    @Override
    public String getCategory ()
    {
        return this.category;
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        return StringUtils.fixASCII (new StringBuilder (this.category).append ('_').append (this.label).toString ().toUpperCase ()).replace (' ', '_').replace ('?', '_');
    }


    /** {@inheritDoc} */
    @Override
    public JLabel getLabelWidget ()
    {
        return this.labelWidget;
    }


    /** {@inheritDoc} */
    @Override
    public JComponent getWidget ()
    {
        return this.field;
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<T> observer)
    {
        this.observers.add (observer);
    }


    /**
     * Notify all registered observers.
     *
     * @param value The new value
     */
    protected void notifyObservers (final T value)
    {
        for (final IValueObserver<T> observer: this.observers)
            observer.update (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean enable)
    {
        this.labelWidget.setEnabled (enable);
        this.field.setEnabled (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void setVisible (final boolean visible)
    {
        this.labelWidget.setVisible (visible);
        this.field.setVisible (visible);
    }
}
