// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Base class for an item in a filter column.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseColumnItem implements IBrowserColumnItem
{
    protected final int index;


    /**
     * Constructor.
     *
     * @param index The index of the item
     */
    protected BaseColumnItem (final int index)
    {
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index + 1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }
}
