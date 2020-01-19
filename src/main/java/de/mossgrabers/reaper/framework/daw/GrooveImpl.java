// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.data.IParameter;
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
     * @param dataSetup Some configuration variables
     * @param iniFiles The INI configuration files
     */
    public GrooveImpl (final DataSetupEx dataSetup, final IniFiles iniFiles)
    {
        super (dataSetup);

        for (int i = 0; i < this.parameters.length; i++)
            this.parameters[i] = new GrooveParameter (dataSetup, i, iniFiles);
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
