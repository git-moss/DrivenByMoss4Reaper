// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;


/**
 * The Groove configuration mode.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneGrooveMode extends AbstractParameterMode<OxiOneControlSurface, OxiOneConfiguration, IParameter> implements IOxiModeReset
{
    private static final String [] MENU          =
    {
        "Enbl",
        "",
        "ShfA",
        "ShfR"
    };

    private static final String [] SHIFTED_MENU  =
    {
        "",
        "AccP",
        "AccA",
        "AccR",
    };

    private final IGroove          groove;
    private final IParameter []    params        = new IParameter [8];
    private int                    selectedIndex = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneGrooveMode (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Groove", surface, model, false);

        this.groove = this.model.getGroove ();

        this.params[0] = this.groove.getParameter (GrooveParameterID.ENABLED);
        this.params[1] = EmptyParameter.INSTANCE;
        this.params[2] = this.groove.getParameter (GrooveParameterID.SHUFFLE_AMOUNT);
        this.params[3] = this.groove.getParameter (GrooveParameterID.SHUFFLE_RATE);
        this.params[4] = EmptyParameter.INSTANCE;
        this.params[5] = this.groove.getParameter (GrooveParameterID.ACCENT_PHASE);
        this.params[6] = this.groove.getParameter (GrooveParameterID.ACCENT_AMOUNT);
        this.params[7] = this.groove.getParameter (GrooveParameterID.ACCENT_RATE);

        this.setControls (ContinuousID.createSequentialList (ContinuousID.KNOB1, 4));
        this.setParameterProvider (new FourKnobProvider<> (surface, new FixedParameterProvider (this.params), ButtonID.SHIFT));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateSelectedIndex ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        IParameter p = this.params[this.selectedIndex];
        String label = p.doesExist () ? p.getName (12) + ": " + p.getDisplayedValue (8) : "";
        int value = p.doesExist () ? p.getValue () : -1;

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
}
