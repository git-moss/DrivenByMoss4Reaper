// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Implementation of a Groove parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveParameter extends ParameterImpl
{
    private static final String [] PARAMETER_NAMES    = new String []
    {
        "Strgth Position",
        "Strgth Velocity",
        "Target",
        "Sensitivity"
    };

    private static final String [] PARAMETER_COMMANDS = new String []
    {
        "strength",
        "velocity",
        "target",
        "tolerance"
    };


    /**
     * Constructor.
     *
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param index The index of the parameters
     */
    public GrooveParameter (final MessageSender sender, final IValueChanger valueChanger, final int index)
    {
        super (sender, valueChanger, index);

        this.setName (PARAMETER_NAMES[index]);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final double value)
    {
        if (!this.doesExist ())
            return;

        int scaledValue = 0;
        switch (this.index)
        {
            case 0:
            case 1:
                this.value = (int) value;
                scaledValue = (int) Math.round (value * 100.0 / (this.valueChanger.getUpperBound () - 1));
                break;
            case 2:
                if (value != this.value)
                    this.value = value < this.value ? 0 : this.valueChanger.getUpperBound () - 1;
                scaledValue = this.value == 0 ? 0 : 1;
                break;
            case 3:
                this.value = (int) value;
                scaledValue = (int) Math.round (value * 3.0 / (this.valueChanger.getUpperBound () - 1));
                switch (scaledValue)
                {
                    case 0:
                        scaledValue = 4;
                        break;
                    case 1:
                        scaledValue = 8;
                        break;
                    case 2:
                        scaledValue = 16;
                        break;
                    case 3:
                    default:
                        scaledValue = 32;
                        break;
                }
                break;
            default:
                // Not used
                break;
        }
        this.sender.sendOSC ("/groove/" + PARAMETER_COMMANDS[this.index], Integer.valueOf (scaledValue));

        // TODO Write to ini file instead
        // if (!APIExists("BR_Win32_WritePrivateProfileString")) && get_ini_file(inipath) ?
        // (
        // part = strcpy(#, parsePart(line, '/', 2));
        // strcmp(part, "strength") == 0 ? (
        // extension_api("BR_Win32_WritePrivateProfileString", "fingers", "groove_strength", value,
        // inipath);

        // ) : (strcmp(part, "velocity") == 0 ? (
        // extension_api("BR_Win32_WritePrivateProfileString", "fingers", "groove_velstrength",
        // value, inipath);

        // ) : (strcmp(part, "target") == 0 ? (
        // extension_api("BR_Win32_WritePrivateProfileString", "fingers", "groove_target", value,
        // inipath);

        // ) : (strcmp(part, "tolerance") == 0 ? (
        // extension_api("BR_Win32_WritePrivateProfileString", "fingers", "groove_tolerance", value,
        // inipath);

    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        if (this.index < 2)
            this.setValue (this.valueChanger.getUpperBound () - 1.0);
        else
            super.resetValue ();
    }
}
