// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A parameter page in a device parameter mapping.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterMapPage
{
    /** The number of parameters of a page. */
    public static final int                       PARAM_COUNT = 8;

    private String                                pageName;
    private final List<ParameterMapPageParameter> parameters  = new ArrayList<> (PARAM_COUNT);


    /**
     * Constructor.
     *
     * @param pageName The name of the parameter page
     */
    public ParameterMapPage (final String pageName)
    {
        this.pageName = pageName;

        for (int i = 0; i < PARAM_COUNT; i++)
            this.parameters.add (new ParameterMapPageParameter ());
    }


    /**
     * Get the name of the page.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.pageName;
    }


    /**
     * Set the name of the page.
     *
     * @param pageName The name to set
     */
    public void setName (final String pageName)
    {
        this.pageName = pageName;
    }


    /**
     * Get the parameters of the page.
     *
     * @return The parameters
     */
    public List<ParameterMapPageParameter> getParameters ()
    {
        return this.parameters;
    }


    /**
     * Swap the positions of the two parameters.
     *
     * @param sourceIndex The first parameter
     * @param destinationIndex The second parameter
     */
    public void swapParameters (final int sourceIndex, final int destinationIndex)
    {
        Collections.swap (this.parameters, sourceIndex, destinationIndex);
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return this.pageName;
    }
}
