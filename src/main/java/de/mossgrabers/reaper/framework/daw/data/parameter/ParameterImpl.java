// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.ItemImpl;


/**
 * Encapsulates the data of a parameter.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterImpl extends ItemImpl implements IParameterEx
{
    private String          valueStr          = "";
    private boolean         isBeingTouched;

    protected double        value;
    protected double        lastReceivedValue = -1;

    private final int       defaultValue;
    private final Processor processor;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param index The index of the parameter
     * @param defaultValue The default value for resetting parameters [0..1]
     */
    public ParameterImpl (final DataSetupEx dataSetup, final int index, final double defaultValue)
    {
        this (dataSetup, Processor.DEVICE, index, defaultValue);
    }


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param processor The processor to use for sending parameter updates
     * @param index The index of the parameter
     * @param defaultValue The default value for resetting parameters [0..1]
     */
    public ParameterImpl (final DataSetupEx dataSetup, final Processor processor, final int index, final double defaultValue)
    {
        super (dataSetup, index);

        this.processor = processor;
        this.defaultValue = this.valueChanger.fromNormalizedValue (defaultValue);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.setValue ((int) Math.max (0, Math.min (this.getValue () + increment, this.valueChanger.getUpperBound () - 1.0)));
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.valueStr;
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue (final int limit)
    {
        final String displayedValue = this.getDisplayedValue ();
        final int length = displayedValue.length ();
        return length > limit ? displayedValue.substring (0, length) : displayedValue;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.valueChanger.fromNormalizedValue (Math.max (this.value, 0));
    }


    /** {@inheritDoc} */
    @Override
    public double getInternalValue ()
    {
        return Math.max (this.value, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final int control)
    {
        this.changeValue (this.valueChanger, control);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int control)
    {
        // Stay in the normalized domain for better resolution!
        double offset = valueChanger.calcKnobChange (control);
        final boolean isNegative = offset < 0;
        offset = valueChanger.toNormalizedValue (Math.abs (offset));
        if (isNegative)
            offset = -offset;
        this.setNormalizedValue (Math.min (1, Math.max (0, this.getInternalValue () + offset)));
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int value)
    {
        this.setNormalizedValue (this.valueChanger.toNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.setNormalizedValue (valueChanger.toNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        if (!this.doesExist ())
            return;
        this.value = value;
        this.sendValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        // Always immediately with Reaper
        this.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        // Not supported
        return this.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setValue (this.defaultValue);
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        // Prevent updating of the value from the DAW when the user edits the value, otherwise the
        // value "jumps" due to round-trip delays

        this.isBeingTouched = isBeingTouched;

        if (this.isBeingTouched || this.lastReceivedValue < 0)
            return;

        this.value = this.lastReceivedValue;
        this.lastReceivedValue = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setInternalValue (final double value)
    {
        if (this.isBeingTouched)
            this.lastReceivedValue = value;
        else
            this.value = value;
    }


    /** {@inheritDoc} */
    @Override
    public void setValueStr (final String valueStr)
    {
        this.valueStr = valueStr == null ? "" : valueStr;
    }


    /**
     * Send the changed value to Reaper.
     */
    protected void sendValue ()
    {
        this.sendPositionedItemOSC ("value", this.value);
    }


    /** {@inheritDoc} */
    @Override
    protected String createCommand (final String command)
    {
        String cmd = "param/" + super.createCommand (command);

        if (this.processor == Processor.TRACK)
        {
            final ITrack pinnedOrSelectedTrack = this.dataSetup.getCursorTrack ().getPinnedOrSelectedTrack ();
            if (pinnedOrSelectedTrack != null)
                cmd = pinnedOrSelectedTrack.getPosition () + "/" + cmd;
        }

        return cmd;
    }


    /** {@inheritDoc} */
    @Override
    protected Processor getProcessor ()
    {
        return this.processor;
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return this.getPosition () + ": " + this.getName ();
    }


    /**
     * Helper function only to be used to replicate the data of the 1st master FX parameter into the
     * fake crossfader parameter.
     *
     * @param destParam The parameter into which to write the values of this parameter
     */
    public void copyValues (final ParameterImpl destParam)
    {
        destParam.setInternalName (this.name);
        destParam.setExists (this.exists);
        destParam.setInternalValue (this.value);
        destParam.setValueStr (this.valueStr);
    }
}
