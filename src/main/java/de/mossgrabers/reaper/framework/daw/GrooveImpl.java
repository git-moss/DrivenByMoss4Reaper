// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.data.GrooveParameter;


/**
 * Implementation of the Groove object.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveImpl extends BaseImpl implements IGroove
{
    private final GrooveParameter [] parameters = new GrooveParameter [4];


    /**
     * Constructor
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param iniFiles The INI configuration files
     */
    public GrooveImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final IniFiles iniFiles)
    {
        super (host, sender);

        for (int i = 0; i < this.parameters.length; i++)
            this.parameters[i] = new GrooveParameter (host, sender, valueChanger, i, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter [] getParameters ()
    {
        return this.parameters;
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }
}
