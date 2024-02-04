// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.device.column;

import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;


/**
 * Base class for an item in a filter column.
 *
 * @author Jürgen Moßgraber
 */
public abstract class BaseColumnItem extends ItemImpl implements IBrowserColumnItem
{
    protected int hits = 0;


    /**
     * Constructor.
     *
     * @param index The index of the item
     */
    protected BaseColumnItem (final int index)
    {
        super (null, index);
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


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        final int idx = this.getIndex ();
        if (idx != this.position)
            this.updateCache (idx);
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public int getHitCount ()
    {
        final int idx = this.getIndex ();
        if (idx != this.position)
            this.updateCache (idx);
        return this.hits;
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        // Not used
        return null;
    }


    protected abstract String getCachedName ();


    protected abstract int getCachedHitCount ();


    private void updateCache (final int idx)
    {
        this.position = idx;
        this.name = this.getCachedName ();
        this.hits = this.getCachedHitCount ();
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return this.getCachedName () + " (" + this.getCachedHitCount () + ")";
    }
}
