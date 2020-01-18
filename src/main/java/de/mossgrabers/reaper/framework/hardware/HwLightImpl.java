// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.graphics.IGraphicsContext;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


/**
 * Implementation of a proxy to a light / LED on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwLightImpl extends AbstractHwControl implements IHwLight, IReaperHwControl
{
    private final HwControlLayout   layout;

    private final Supplier<ColorEx> colorSupplier;
    private final Consumer<ColorEx> colorSendValueConsumer;
    private final IntSupplier       intSupplier;
    private final IntConsumer       intSendValueConsumer;
    private IntFunction<ColorEx>    stateToColorFunction;

    private ColorEx                 colorState;
    private int                     intState;


    /**
     * Constructor.
     *
     * @param id The ID o the control
     * @param supplier Callback for getting the state of the light
     * @param sendValueConsumer Callback for sending the state to the controller device
     */
    public HwLightImpl (final String id, final Supplier<ColorEx> supplier, final Consumer<ColorEx> sendValueConsumer)
    {
        super (null, null);

        this.layout = new HwControlLayout (id);

        this.colorSupplier = supplier;
        this.colorSendValueConsumer = sendValueConsumer;
        this.intSupplier = null;
        this.intSendValueConsumer = null;
    }


    /**
     * Constructor.
     *
     * @param id The ID o the control
     * @param supplier Callback for getting the state of the light
     * @param sendValueConsumer Callback for sending the state to the controller device
     * @param stateToColorFunction Convert the state of the light to a color, which can be displayed
     *            in the simulated GUI
     */
    public HwLightImpl (final String id, final IntSupplier supplier, final IntConsumer sendValueConsumer, final IntFunction<ColorEx> stateToColorFunction)
    {
        super (null, null);

        this.layout = new HwControlLayout (id);

        this.colorSupplier = null;
        this.colorSendValueConsumer = null;
        this.intSupplier = supplier;
        this.intSendValueConsumer = sendValueConsumer;
        this.stateToColorFunction = stateToColorFunction;
    }


    /** {@inheritDoc} */
    @Override
    public void clearCache ()
    {
        this.turnOff ();
    }


    /** {@inheritDoc} */
    @Override
    public void turnOff ()
    {
        if (this.colorSendValueConsumer != null)
            this.colorSendValueConsumer.accept (ColorEx.BLACK);
        else
            this.intSendValueConsumer.accept (0);
    }


    /** {@inheritDoc} */
    @Override
    public void update ()
    {
        if (this.colorSupplier != null)
        {
            final ColorEx newColorState = this.colorSupplier.get ();
            if (this.colorState != null && this.colorState.equals (newColorState))
                return;
            this.colorState = newColorState;
            this.colorSendValueConsumer.accept (this.colorState);
        }
        else
        {
            final int newColorState = this.intSupplier.getAsInt ();
            if (this.intState == newColorState)
                return;
            this.intState = newColorState;
            this.intSendValueConsumer.accept (this.intState);
            this.colorState = this.stateToColorFunction.apply (this.intState);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.layout.setBounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double scale)
    {
        final Bounds bounds = this.layout.getBounds ();
        if (bounds != null)
            gc.fillRectangle (bounds.getX () * scale, bounds.getY () * scale, bounds.getWidth () * scale, bounds.getHeight () * scale, this.colorState == null ? ColorEx.BLACK : this.colorState);
    }


    /**
     * Get the current color state.
     *
     * @return The color state
     */
    public ColorEx getColorState ()
    {
        return this.colorState;
    }
}
