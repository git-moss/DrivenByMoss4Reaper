// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.widget;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;


/**
 * A panel for a action setting.
 *
 * @author Jürgen Moßgraber
 */
public class ActionPanel extends JPanel
{
    private static final long serialVersionUID = 5685236453528547582L;

    private final JTextField  textField        = new JTextField ();
    private final JButton     selectButton     = new JButton ("Select");


    /**
     * Constructor.
     */
    public ActionPanel ()
    {
        super (new BorderLayout ());

        this.add (this.textField, BorderLayout.CENTER);
        this.add (this.selectButton, BorderLayout.EAST);
    }


    /**
     * Get the text field for the action ID.
     *
     * @return The field
     */
    public JTextField getTextField ()
    {
        return this.textField;
    }


    /**
     * Get the button for action selection.
     *
     * @return The button
     */
    public JButton getSelectButton ()
    {
        return this.selectButton;
    }
}
