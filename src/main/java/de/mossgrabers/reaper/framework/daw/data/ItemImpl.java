// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.reaper.framework.daw.BaseImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Base implementation for an item.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class ItemImpl extends BaseImpl implements IItem
{
    private int     index;
    private int     position;
    private boolean exists;
    private String  name = "";
    private boolean selected;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param index The index
     */
    public ItemImpl (final IHost host, final MessageSender sender, final int index)
    {
        super (host, sender);

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


    /**
     * Set the name of the parameter.
     *
     * @param name The name
     */
    public void setName (final String name)
    {
        this.name = name == null ? "" : name;
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
        // Not supported
    }
}
