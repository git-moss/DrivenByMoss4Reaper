// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.hardware.ILight;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;


/**
 * Implementation of a proxy to a light / LED on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LightImpl implements ILight
{
    private final IntSupplier supplier;
    private final IntConsumer sendValueConsumer;

    private int               colorState;


    /**
     * Constructor.
     *
     * @param supplier Callback for getting the state of the light
     * @param sendValueConsumer Callback for sending the state to the controller device
     */
    public LightImpl (final IntSupplier supplier, final IntConsumer sendValueConsumer)
    {
        this.supplier = supplier;
        this.sendValueConsumer = sendValueConsumer;
    }


    /** {@inheritDoc} */
    @Override
    public void turnOff ()
    {
        this.sendValueConsumer.accept (0);
    }


    /** {@inheritDoc} */
    @Override
    public void update ()
    {
        final int newColorState = this.supplier.getAsInt ();
        if (this.colorState == newColorState)
            return;
        this.colorState = newColorState;
        this.sendValueConsumer.accept (-1);
    }
}
