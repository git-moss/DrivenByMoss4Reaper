// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator.ui.widgets;

import javax.swing.DefaultListSelectionModel;


/**
 * No selection please.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoSelectionModel extends DefaultListSelectionModel
{
    private static final long serialVersionUID = -7172874777243971922L;


    /** {@inheritDoc} */
    @Override
    public void addSelectionInterval (final int index0, final int index1)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void insertIndexInterval (final int index, final int length, final boolean before)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setAnchorSelectionIndex (final int anchorIndex)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setLeadSelectionIndex (final int leadIndex)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setSelectionInterval (final int index0, final int index1)
    {
        // Intentionally empty
    }
}