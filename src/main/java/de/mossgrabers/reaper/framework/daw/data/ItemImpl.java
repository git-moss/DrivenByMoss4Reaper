// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.framework.daw.BaseImpl;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;

import java.util.HashSet;
import java.util.Set;


/**
 * Base implementation for an item.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class ItemImpl extends BaseImpl implements IItem
{
    protected int                             index;
    private int                               position;
    private boolean                           exists        = false;
    private String                            name          = "";
    private boolean                           selected;
    private final Set<IValueObserver<String>> nameObservers = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index
     */
    public ItemImpl (final DataSetupEx dataSetup, final int index)
    {
        super (dataSetup);

        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.exists;
    }


    /**
     * Set the exists state.
     *
     * @param exists True if exists
     */
    public void setExists (final boolean exists)
    {
        this.exists = exists;
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index;
    }


    /**
     * Update the index.
     *
     * @param index The new index
     */
    public void setIndex (final int index)
    {
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.position;
    }


    /**
     * Set the position of the track, among all tracks.
     *
     * @param position The position
     */
    public void setPosition (final int position)
    {
        if (position >= 0)
            this.position = position;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.exists ? this.name : "";
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.optimizeName (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        this.nameObservers.add (observer);
    }


    /**
     * Set the name of the parameter.
     *
     * @param name The name
     */
    public void setName (final String name)
    {
        if (this.name.equals (name))
            return;

        this.name = name == null ? "" : name;
        this.nameObservers.forEach (observer -> observer.update (this.name));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.doesExist () && this.selected;
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        this.selected = isSelected;
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Intentionally empty
    }
}
