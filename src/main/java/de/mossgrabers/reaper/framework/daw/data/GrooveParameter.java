// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;


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
     * @param dataSetup Some configuration variables
     * @param index The index of the parameters
     * @param iniFiles The INI file where the values are stored
     */
    public GrooveParameter (final DataSetupEx dataSetup, final int index, final IniFiles iniFiles)
    {
        super (dataSetup, index);

        this.iniFiles = iniFiles;

        this.setExists (true);

        this.setName (PARAMETER_NAMES[index]);
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        int val = 0;
        switch (this.getIndex ())
        {
            case 0:
                val = this.iniFiles.getMainIniInteger ("fingers", "groove_strength", 100);
                break;

            case 1:
                val = this.iniFiles.getMainIniInteger ("fingers", "groove_velstrength", 100);
                break;

            case 2:
                val = this.iniFiles.getMainIniInteger ("fingers", "groove_target", 0);
                break;

            case 3:
                val = this.iniFiles.getMainIniInteger ("fingers", "groove_tolerance", 16);
                break;

            case 4:
                val = this.iniFiles.getMainIniInteger ("midiedit", "quantstrength", 100);
                break;

            default:
                // Not used
                break;
        }

        this.value = this.fromIniValue (val);
        return (int) this.value;
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return Integer.toString (this.toIniValue (this.getValue ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int val)
    {
        if (!this.doesExist ())
            return;

        final int limit = this.valueChanger.getUpperBound () - 1;
        switch (this.getIndex ())
        {
            case 0:
            case 1:
                this.value = val;
                break;

            case 2:
                if (val != this.value)
                    this.value = val < this.value ? 0 : limit;
                break;

            case 3:
                if (val == 0)
                    this.value = 0;
                else
                {
                    final int offset = (int) (limit / 3.0);
                    if (val > this.value)
                        this.value = Math.min (this.value + offset, limit);
                    else
                        this.value = Math.max (0, this.value - offset);
                }
                break;

            default:
                // Not used
                break;
        }

        final int scaledValue = this.toIniValue (this.value);
        this.sender.processIntArg ("inifile", "fingers/" + PARAMETER_COMMANDS[this.getIndex ()], scaledValue);
        this.iniFiles.updateMainIniInteger ("fingers", PARAMETER_COMMANDS[this.getIndex ()], scaledValue);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        if (this.getIndex () < 2)
            this.setValue (this.valueChanger.getUpperBound () - 1);
        else
            super.resetValue ();
    }


    private int toIniValue (final double val)
    {
        final int upper = this.valueChanger.getUpperBound () - 1;
        switch (this.getIndex ())
        {
            case 0:
            case 1:
                return (int) Math.round (val * 100.0 / upper);

            case 2:
                return val == 0 ? 0 : 1;

            case 3:
                final int scaledValue = (int) Math.round (val * 3.0 / upper);
                switch (scaledValue)
                {
                    case 0:
                        return 4;
                    case 1:
                        return 8;
                    case 2:
                        return 16;
                    case 3:
                    default:
                        return 32;
                }

            default:
                // Not used
                return 0;
        }
    }


    private int fromIniValue (final int val)
    {
        final int upper = this.valueChanger.getUpperBound () - 1;
        switch (this.getIndex ())
        {
            case 0:
            case 1:
                return val * upper / 100;

            case 2:
                return val == 0 ? 0 : upper;

            case 3:
                int v;
                switch (val)
                {
                    case 4:
                        v = 0;
                        break;
                    case 8:
                        v = 1;
                        break;
                    case 16:
                        v = 2;
                        break;
                    case 32:
                    default:
                        v = 3;
                        break;
                }
                return (int) Math.round (v * upper / 3.0);

            default:
                // Not used
                return 0;
        }
    }
}
