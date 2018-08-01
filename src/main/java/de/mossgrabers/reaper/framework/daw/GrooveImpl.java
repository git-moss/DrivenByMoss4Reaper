// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.data.GrooveParameter;
import de.mossgrabers.reaper.framework.daw.data.ParameterImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Implementation of the Groove object.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveImpl extends BaseImpl implements IGroove
{
    private final ParameterImpl [] parameters = new ParameterImpl [4];

    private IValueChanger          valueChanger;


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

        this.valueChanger = valueChanger;

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


    /**
     * Set a parameter value.
     *
     * @param index The index of the parameter
     * @param value The new value
     */
    public void setParameter (final int index, final int value)
    {
        // TODO Reaper - Is this necessary? Needs to be moved to setValue?!

        switch (index)
        {
            case 0:
            case 1:
                this.parameters[index].setInternalValue (value * (this.valueChanger.getUpperBound () - 1) / 100);
                this.parameters[index].setValueStr (value + "%");
                break;
            case 2:
                this.parameters[index].setInternalValue (value == 0 ? 0 : this.valueChanger.getUpperBound () - 1);
                this.parameters[index].setValueStr (value == 0 ? "Items" : "Notes");
                break;
            case 3:
                switch (value)
                {
                    case 4:
                        this.parameters[index].setInternalValue (0);
                        this.parameters[index].setValueStr ("4th");
                        break;
                    case 8:
                        this.parameters[index].setInternalValue ((this.valueChanger.getUpperBound () - 1) / 3);
                        this.parameters[index].setValueStr ("8th");
                        break;
                    case 16:
                        this.parameters[index].setInternalValue (2 * (this.valueChanger.getUpperBound () - 1) / 3);
                        this.parameters[index].setValueStr ("16th");
                        break;
                    case 32:
                    default:
                        this.parameters[index].setInternalValue (this.valueChanger.getUpperBound () - 1);
                        this.parameters[index].setValueStr ("32nd");
                        break;
                }
                break;
            default:
                // Not used
                break;
        }
    }
}
