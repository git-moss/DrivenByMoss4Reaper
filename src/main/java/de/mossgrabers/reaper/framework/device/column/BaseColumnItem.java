// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;


/**
 * Base class for an item in a filter column.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseColumnItem extends ItemImpl implements IBrowserColumnItem
{
    /**
     * Constructor.
     *
     * @param index The index of the item
     */
    protected BaseColumnItem (final int index)
    {
        super (null, null, index);
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return super.getIndex () + 1;
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
