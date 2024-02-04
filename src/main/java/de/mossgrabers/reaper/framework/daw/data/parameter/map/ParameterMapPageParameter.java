// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter.map;

/**
 * A parameter page in a device parameter mapping.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterMapPageParameter
{
    private int    index;
    private String name;


    /**
     * Constructor.
     */
    public ParameterMapPageParameter ()
    {
        this (-1, "Not assigned");
    }


    /**
     * Constructor.
     *
     * @param index The index of the parameter
     * @param name The name of the parameter
     */
    public ParameterMapPageParameter (final int index, final String name)
    {
        this.index = index;
        this.name = name;
    }


    /**
     * The index of the assigned parameter.
     *
     * @return The index
     */
    public int getIndex ()
    {
        return this.index;
    }


    /**
     * The (re-)name of the assigned parameter.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Rename the assigned parameter.
     *
     * @param name The name
     */
    public void setName (final String name)
    {
        this.name = name;
    }


    /**
     * Test if the parameter is assigned.
     *
     * @return True if assigned
     */
    public boolean isAssigned ()
    {
        return this.index >= 0;
    }


    /**
     * Set the index and name of the parameter to assign.
     *
     * @param index The index
     * @param name The name
     */
    public void assign (final int index, final String name)
    {
        this.index = index;
        this.name = name;
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        if (this.index < 0)
            return "Not assigned";
        final String n = this.name == null || this.name.isBlank () ? "[Keep Name]" : this.name;
        return n + " (#" + this.index + ")";
    }
}
