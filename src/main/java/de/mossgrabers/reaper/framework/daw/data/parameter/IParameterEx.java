// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.parameter.IParameter;


/**
 * Additional methods for parameters.
 *
 * @author Jürgen Moßgraber
 */
public interface IParameterEx extends IParameter
{
    /**
     * Get the normalized internal value. This is used to improve the update cycle between Reaper
     * and the Java side.
     *
     * @return The value in the range of [0..1]
     */
    double getInternalValue ();


    /**
     * Set the value.
     *
     * @param value The value normalized to 0..1
     */
    void setInternalValue (double value);


    /**
     * Set the value as text.
     *
     * @param valueStr The text
     */
    void setValueStr (String valueStr);


    /**
     * Set the name of the parameter.
     *
     * @param name The name
     */
    void setInternalName (String name);


    /**
     * Set the position of the track, among all tracks.
     *
     * @param position The position
     */
    void setPosition (int position);


    /**
     * Set the exists state.
     *
     * @param exists True if exists
     */
    void setExists (boolean exists);
}
