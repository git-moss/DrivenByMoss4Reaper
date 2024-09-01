// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
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
 * @author Jürgen Moßgraber
 */
public abstract class ItemImpl extends BaseImpl implements IItem
{
    protected int                             index;
    protected int                             position;
    protected boolean                         exists        = false;
    protected String                          name          = null;
    private boolean                           selected;
    private final Set<IValueObserver<String>> nameObservers = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index
     */
    protected ItemImpl (final DataSetupEx dataSetup, final int index)
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
        return this.exists && this.name != null ? this.name : "";
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


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        // Overwrite to support
    }


    /**
     * Set the name of the parameter.
     *
     * @param name The name
     */
    public void setInternalName (final String name)
    {
        if (this.name != null && this.name.equals (name))
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


    /** {@inheritDoc} */
    @Override
    public void toggleMultiSelect ()
    {
        // Intentionally empty
    }


    protected void sendPositionedItemOSC (final String command)
    {
        this.sender.processNoArg (this.getProcessor (), this.createCommand (command));
    }


    protected void sendPositionedItemOSC (final String command, final int value)
    {
        this.sender.processIntArg (this.getProcessor (), this.createCommand (command), value);
    }


    protected void sendPositionedItemOSC (final String command, final boolean value)
    {
        this.sender.processIntArg (this.getProcessor (), this.createCommand (command), value ? 1 : 0);
    }


    /**
     * Send an item command with a position.
     *
     * @param command The command
     * @param value The value
     */
    public void sendPositionedItemOSC (final String command, final double value)
    {
        this.sender.processDoubleArg (this.getProcessor (), this.createCommand (command), value);
    }


    protected void sendPositionedItemOSC (final String command, final String value)
    {
        this.sender.processStringArg (this.getProcessor (), this.createCommand (command), value);
    }


    protected String createCommand (final String command)
    {
        return this.getPosition () + "/" + command;
    }
}
