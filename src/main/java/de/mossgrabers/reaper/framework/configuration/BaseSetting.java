// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IValueObserver;
import de.mossgrabers.framework.utils.StringUtils;

import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

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
public abstract class BaseSetting<C extends Control, T> implements IfxSetting<T>
{
    protected static final String        NUMBERS                = "0123456789";
    protected static final String        SIGNED_NUMBERS         = "-0123456789";
    protected static final String        NUMBERS_AND_DOT        = ".0123456789";
    protected static final String        SIGNED_NUMBERS_AND_DOT = ".-0123456789";

    protected final C                    field;

    private final Label                  labelWidget;
    private final Set<IValueObserver<T>> observers              = new HashSet<> ();
    private final String                 label;
    private final String                 category;


    /**
     * Constructor
     *
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     * @param field The widget for editing the setting
     */
    public BaseSetting (final String label, final String category, final C field)
    {
        this.label = label;
        this.category = category;
        this.field = field;
        this.labelWidget = new Label (this.label);
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
    public Label getLabelWidget ()
    {
        return this.labelWidget;
    }


    /** {@inheritDoc} */
    @Override
    public Control getWidget ()
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
        this.labelWidget.disableProperty ().set (!enable);
        this.field.disableProperty ().set (!enable);
    }


    /** {@inheritDoc} */
    @Override
    public void setVisible (final boolean visible)
    {
        this.labelWidget.setVisible (visible);
        this.labelWidget.setManaged (visible);
        this.field.setVisible (visible);
        this.field.setManaged (visible);
    }


    /**
     * Limits the textfield to the given characters.
     *
     * @param field The field to limit
     * @param characters The characters to limit to
     */
    protected static void limitToNumbers (final TextField field, final String characters)
    {
        field.addEventFilter (KeyEvent.KEY_TYPED, keyEvent -> {
            if (!characters.contains (keyEvent.getCharacter ()))
                keyEvent.consume ();
        });
    }
}
