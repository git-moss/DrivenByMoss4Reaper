// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.awt.Component;


/**
 * Renders a checkbox item in a list box.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<CheckboxListItem>
{
    private static final long serialVersionUID = -6249149996532470179L;


    /** {@inheritDoc} */
    @Override
    public Component getListCellRendererComponent (final JList<? extends CheckboxListItem> list, final CheckboxListItem value, final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        this.setEnabled (list.isEnabled ());
        this.setSelected (value.isSelected ());
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