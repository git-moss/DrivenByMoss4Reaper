// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.reaper.framework.IniFiles;
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
        "groove_strength",
        "groove_velstrength",
        "groove_target",
        "groove_tolerance"
    };

    private IniFiles               iniFiles;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param index The index of the parameters
     * @param iniFiles The INI file where the values are stored
     */
    public GrooveParameter (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int index, final IniFiles iniFiles)
    {
        super (host, sender, valueChanger, index);

        this.iniFiles = iniFiles;

        this.setName (PARAMETER_NAMES[index]);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        switch (this.getIndex ())
        {
            case 0:
                this.value = this.iniFiles.getMainIniInteger ("fingers", "groove_strength", 100);
                break;

            case 1:
                this.value = this.iniFiles.getMainIniInteger ("fingers", "groove_velstrength", 100);
                break;

            case 2:
                this.value = this.iniFiles.getMainIniInteger ("fingers", "groove_target", 0);
                break;

            case 3:
                this.value = this.iniFiles.getMainIniInteger ("fingers", "groove_tolerance", 16);
                break;

            case 4:
                this.value = this.iniFiles.getMainIniInteger ("midiedit", "quantstrength", 100);
                break;

            default:
                // Not used
                break;
        }

        return super.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final double value)
    {
        if (!this.doesExist ())
            return;

        int scaledValue = 0;
        switch (this.getIndex ())
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
        this.iniFiles.setMainIniInteger ("fingers", PARAMETER_COMMANDS[this.getIndex ()], scaledValue);
        this.iniFiles.saveMainFile ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        if (this.getIndex () < 2)
            this.setValue (this.valueChanger.getUpperBound () - 1.0);
        else
            super.resetValue ();
    }
}
