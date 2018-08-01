// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.AbstractBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * An abstract bank which provides some basic features.
 *
 * @param <T> The specific item type of the bank item
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractBankImpl<T extends IItem> extends AbstractBank<T>
{
    protected final IHost         host;
    protected final MessageSender sender;
    protected final IValueChanger valueChanger;

    protected int                 itemCount;


    /**
     * Constructor.
     * 
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param pageSize The number of elements in a page of the bank
     */
    public AbstractBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int pageSize)
    {
        super (pageSize);
        this.host = host;
        this.sender = sender;
        this.valueChanger = valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.itemCount;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        // TODO Reaper We need that for OSC, requires rewrite of bank management
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        // TODO Reaper We need that for OSC, requires rewrite of bank management
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.items.get (0).getPosition () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        final T sel = this.getSelectedItem ();
        return sel != null && sel.getPosition () < this.getItemCount () - 1;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        if (position < 0 || position >= this.getItemCount ())
            return;

        // TODO Reaper Possible? -> auf jeden Fall kann man die Page verschieben
        // final int pageSize = this.getPageSize ();
        // this.bank.scrollPosition ().set ((position / pageSize) * pageSize);
        // this.bank.scrollIntoView (position);
    }
}
