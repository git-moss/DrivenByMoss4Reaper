// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.configuration;

import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.ui.utils.LogModel;
import de.mossgrabers.reaper.ui.utils.PropertiesEx;
import de.mossgrabers.reaper.ui.utils.SafeRunLater;
import de.mossgrabers.reaper.ui.widget.ActionPanel;

import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Reaper implementation of an action setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ActionSettingImpl extends BaseValueSetting<ActionPanel, String> implements IActionSetting
{
    private String  value;
    private boolean isSelectionActive;


    /**
     * Constructor.
     *
     * @param sender The sender
     * @param logModel The log model
     * @param properties Where to load from
     * @param label The name of the setting, must not be null
     * @param category The name of the category, may not be null
     */
    public ActionSettingImpl (final MessageSender sender, final LogModel logModel, final PropertiesEx properties, final String label, final String category)
    {
        super (logModel, label, category, new ActionPanel ());

        this.load (properties);

        this.field.addKeyListener (new KeyAdapter ()
        {
            /** {@inheritDoc} */
            @Override
            public void keyTyped (final KeyEvent e)
            {
                SafeRunLater.execute (ActionSettingImpl.this.logModel, () -> ActionSettingImpl.this.set (ActionSettingImpl.this.field.getTextField ().getText ()));
            }
        });

        this.field.getSelectButton ().addActionListener (e -> {
            sender.processNoArg (Processor.ACTION, "select");
            this.isSelectionActive = true;
        });
    }


    /**
     * Check if the action setting is waiting for a actionID selection in Reaper.
     *
     * @return True if the setting is waiting for input
     */
    public boolean isSelectionActive ()
    {
        return this.isSelectionActive;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.value = value;
        this.setDirty ();
        this.flush ();

        this.isSelectionActive = false;

        SafeRunLater.execute (this.logModel, () -> {
            final JTextField textField = this.field.getTextField ();
            final String v = textField.getText ();
            if (v == null || !v.equals (this.value))
                textField.setText (this.value);
        });
    }


    /** {@inheritDoc} */
    @Override
    public String get ()
    {
        return this.value;
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.notifyObservers (this.value == null ? "" : this.value);
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
        this.set (properties.getString (this.getID (), ""));
    }


    /** {@inheritDoc} */
    @Override
    public void reset ()
    {
        this.set ("");
    }
}
