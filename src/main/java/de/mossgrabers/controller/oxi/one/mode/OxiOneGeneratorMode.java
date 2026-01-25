// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.StaticIntegerRangeParameter;
import de.mossgrabers.framework.parameter.StaticPercentageParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * The Generator configuration mode.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneGeneratorMode extends AbstractParameterMode<OxiOneControlSurface, OxiOneConfiguration, IParameter> implements IOxiModeReset
{
    private static final String []            MENU          =
    {
        "Puls",
        "Lgth",
        "Rota",
        "Dens"
    };

    private static final String []            SHIFTED_MENU  =
    {
        "",
        "",
        "",
        "",
    };

    private final IParameter []               params        = new IParameter [8];
    private int                               selectedIndex = 0;

    private final StaticIntegerRangeParameter pulseValue;
    private final StaticIntegerRangeParameter lengthValue;
    private final StaticIntegerRangeParameter rotationValue;
    private final StaticPercentageParameter   densityValue;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneGeneratorMode (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Generator", surface, model, false);

        final IValueChanger valueChanger = this.model.getValueChanger ();
        this.pulseValue = new StaticIntegerRangeParameter ("Pulse", valueChanger, 0, 16, 16);
        this.lengthValue = new StaticIntegerRangeParameter ("Length", valueChanger, 1, 16, 16);
        this.rotationValue = new StaticIntegerRangeParameter ("Rotation", valueChanger, -15, 15, 0);
        this.densityValue = new StaticPercentageParameter ("Density", valueChanger, 1.0);
        this.params[0] = this.pulseValue;
        this.params[1] = this.lengthValue;
        this.params[2] = this.rotationValue;
        this.params[3] = this.densityValue;
        this.params[4] = EmptyParameter.INSTANCE;
        this.params[5] = EmptyParameter.INSTANCE;
        this.params[6] = EmptyParameter.INSTANCE;
        this.params[7] = EmptyParameter.INSTANCE;

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));
        this.setParameterProvider (new FourKnobProvider<> (surface, new FixedParameterProvider (this.params), ButtonID.SHIFT));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateSelectedIndex ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final IParameter p = this.params[this.selectedIndex];
        final String label = p.doesExist () ? p.getName (12) + ": " + p.getDisplayedValue (8) : "";
        final int value = p.doesExist () ? p.getValue () : -1;

        display.addElement (new TitleValueMenuComponent ("", label, this.surface.isShiftPressed () ? SHIFTED_MENU : MENU, value, 0, 0, false));
        display.send ();
    }


    private void updateSelectedIndex ()
    {
        if (this.surface.isPressed (ButtonID.SHIFT))
        {
            if (this.selectedIndex < 4)
                this.selectedIndex += 4;
        }
        else
        {
            if (this.selectedIndex >= 4)
                this.selectedIndex -= 4;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.selectedIndex = this.surface.isPressed (ButtonID.SHIFT) ? index + 4 : index;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.params[this.selectedIndex].changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue (final int index)
    {
        this.params[this.selectedIndex].resetValue ();
    }


    /**
     * Get the pulse parameter.
     *
     * @return The parameter value
     */
    public int getPulse ()
    {
        return this.pulseValue.getRawValue ();
    }


    /**
     * Get the rotation parameter.
     *
     * @return The parameter value
     */
    public int getRotation ()
    {
        return this.rotationValue.getRawValue ();
    }


    /**
     * Get the density parameter.
     *
     * @return The parameter in the range of [0,1]
     */
    public double getDensity ()
    {
        return this.densityValue.getRawValue ();
    }


    /**
     * Get the length parameter.
     *
     * @return The parameter value
     */
    public int getLength ()
    {
        return this.lengthValue.getRawValue ();
    }
}
