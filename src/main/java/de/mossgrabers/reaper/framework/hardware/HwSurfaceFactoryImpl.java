// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwControl;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwGraphicsDisplay;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.controller.hardware.IHwPianoKeyboard;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.hardware.IHwSurfaceFactory;
import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.graphics.IBitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


/**
 * Factory for creating hardware elements proxies of a hardware controller device.
 *
 * @author Jürgen Moßgraber
 */
public class HwSurfaceFactoryImpl implements IHwSurfaceFactory
{
    private final IHost                  host;
    private final List<IReaperHwControl> controls     = new ArrayList<> ();

    private double                       width;
    private double                       height;
    private int                          lightCounter = 0;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public HwSurfaceFactoryImpl (final IHost host)
    {
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public IHwButton createButton (final int surfaceID, final ButtonID buttonID, final String label)
    {
        final HwButtonImpl button = new HwButtonImpl (buttonID.name (), this.host, label);
        this.controls.add (button);
        return button;
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight createLight (final int surfaceID, final OutputID outputID, final Supplier<ColorEx> supplier, final Consumer<ColorEx> sendValueConsumer)
    {
        this.lightCounter++;
        final String id = createID (surfaceID, outputID == null ? "LIGHT" + this.lightCounter : outputID.name ());

        final HwLightImpl light = new HwLightImpl (id, supplier, sendValueConsumer);
        this.controls.add (light);
        return light;
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight createLight (final int surfaceID, final OutputID outputID, final IntSupplier supplier, final IntConsumer sendValueConsumer, final IntFunction<ColorEx> stateToColorFunction, final IHwButton button)
    {
        this.lightCounter++;
        final String id = createID (surfaceID, outputID == null ? "LIGHT" + this.lightCounter : outputID.name ());

        final HwLightImpl light = new HwLightImpl (id, supplier, sendValueConsumer, stateToColorFunction);
        if (button == null)
            this.controls.add (light);
        else
            button.addLight (light);
        return light;
    }


    /** {@inheritDoc} */
    @Override
    public IHwFader createFader (final int surfaceID, final ContinuousID faderID, final String label, final boolean isVertical)
    {
        final HwFaderImpl fader = new HwFaderImpl (faderID.name (), this.host, label, isVertical);
        this.controls.add (fader);
        return fader;
    }


    /** {@inheritDoc} */
    @Override
    public IHwAbsoluteKnob createAbsoluteKnob (final int surfaceID, final ContinuousID knobID, final String label)
    {
        final String id = createID (surfaceID, knobID.name ());
        final HwAbsoluteKnobImpl knob = new HwAbsoluteKnobImpl (id, this.host, label);
        this.controls.add (knob);
        return knob;
    }


    /** {@inheritDoc} */
    @Override
    public IHwRelativeKnob createRelativeKnob (final int surfaceID, final ContinuousID knobID, final String label)
    {
        final String id = createID (surfaceID, knobID.name ());
        final HwRelativeKnobImpl knob = new HwRelativeKnobImpl (id, this.host, label);
        this.controls.add (knob);
        return knob;
    }


    /** {@inheritDoc} */
    @Override
    public IHwRelativeKnob createRelativeKnob (final int surfaceID, final ContinuousID knobID, final String label, final RelativeEncoding encoding)
    {
        final String id = createID (surfaceID, knobID.name ());
        final HwRelativeKnobImpl knob = new HwRelativeKnobImpl (id, this.host, label, encoding);
        this.controls.add (knob);
        return knob;
    }


    /** {@inheritDoc} */
    @Override
    public IHwTextDisplay createTextDisplay (final int surfaceID, final OutputID outputID, final int numLines)
    {
        final String id = createID (surfaceID, outputID.name ());
        final HwTextDisplayImpl display = new HwTextDisplayImpl (id, numLines);
        this.controls.add (display);
        return display;
    }


    /** {@inheritDoc} */
    @Override
    public IHwGraphicsDisplay createGraphicsDisplay (final int surfaceID, final OutputID outputID, final IBitmap bitmap)
    {
        final String id = createID (surfaceID, outputID.name ());
        final HwGraphicsDisplayImpl display = new HwGraphicsDisplayImpl (id, bitmap);
        this.controls.add (display);
        return display;
    }


    /** {@inheritDoc} */
    @Override
    public IHwPianoKeyboard createPianoKeyboard (final int surfaceID, final int numKeys)
    {
        final int octave = 0;
        final int startKeyInOctave = 0;

        final String id = createID (surfaceID, "KEYBOARD");
        final HwPianoKeyboardImpl piano = new HwPianoKeyboardImpl (id, numKeys, octave, startKeyInOctave);
        this.controls.add (piano);
        return piano;
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.controls.forEach (IHwControl::update);
    }


    /** {@inheritDoc} */
    @Override
    public void clearCache ()
    {
        this.controls.forEach (control -> {
            if (control instanceof final IHwLight light)
                light.forceFlush ();
        });
    }


    /**
     * Get the width of the surface.
     *
     * @return The width of the surface
     */
    public double getWidth ()
    {
        return this.width;
    }


    /**
     * Get the height of the surface.
     *
     * @return The height of the surface
     */
    public double getHeight ()
    {
        return this.height;
    }


    /**
     * Get the controls.
     *
     * @return The controls
     */
    public List<IReaperHwControl> getControls ()
    {
        return this.controls;
    }


    private static String createID (final int surfaceID, final String name)
    {
        return surfaceID + 1 + "_" + name;
    }


    /**
     * Set the window dimension.
     *
     * @param width The width of the controller device
     * @param height The height of the controller device
     */
    public void setDimension (final double width, final double height)
    {
        this.width = width;
        this.height = height;
    }
}
