// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.awt.Component;


/**
 * Renders a check-box item in a list box.
 *
 * @author Jürgen Moßgraber
 */
public class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<ControllerCheckboxListItem>
{
    private static final long serialVersionUID = -6249149996532470179L;

    private final ImageIcon   iconOff;
    private final ImageIcon   iconEnabled;
    private final ImageIcon   iconRunning;


    /**
     * Constructor.
     */
    public CheckboxListRenderer ()
    {
        this.iconOff = Functions.getIcon ("ControllerOff", 16);
        this.iconEnabled = Functions.getIcon ("ControllerEnabled", 16);
        this.iconRunning = Functions.getIcon ("ControllerRunning", 16);

        this.setIconTextGap (10);
    }


    /** {@inheritDoc} */
    @Override
    public Component getListCellRendererComponent (final JList<? extends ControllerCheckboxListItem> list, final ControllerCheckboxListItem value, final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        this.setEnabled (list.isEnabled ());

        if (value.isSelected ())
        {
            this.setIcon (value.isRunning () ? this.iconRunning : this.iconEnabled);
            this.setToolTipText (value.isRunning () ? "Controller is running" : "Controller is enabled but not connected");
        }
        else
        {
            this.setIcon (this.iconOff);
            this.setToolTipText ("Controller is off");
        }

        this.setFont (list.getFont ());
        if (isSelected)
        {
            this.setBackground (list.getSelectionBackground ());
            this.setForeground (list.getSelectionForeground ());
        }
        else
        {
            this.setBackground (list.getBackground ());
            this.setForeground (list.getForeground ());
        }
        this.setText (value.toString ());
        return this;
    }
}