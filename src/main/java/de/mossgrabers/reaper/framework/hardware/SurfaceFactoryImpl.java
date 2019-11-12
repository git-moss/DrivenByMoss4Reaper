// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.hardware;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.hardware.IButton;
import de.mossgrabers.framework.controller.hardware.IControl;
import de.mossgrabers.framework.controller.hardware.IFader;
import de.mossgrabers.framework.controller.hardware.ILight;
import de.mossgrabers.framework.controller.hardware.ISurfaceFactory;
import de.mossgrabers.framework.daw.IHost;

import java.util.HashSet;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;


/**
 * Factory for creating hardware elements proxies of a hardware controller device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SurfaceFactoryImpl implements ISurfaceFactory
{
    private final IHost         host;
    private final Set<IControl> controls = new HashSet<> ();


    /**
     * Constructor.
     *
     * @param host The host
     */
    public SurfaceFactoryImpl (final IHost host)
    {
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public IButton createButton (final ButtonID buttonID, final String label)
    {
        return new ButtonImpl (this.host, label);
    }


    /** {@inheritDoc} */
    @Override
    public ILight createLight (final IntSupplier supplier, final IntConsumer sendValueConsumer)
    {
        return new LightImpl (supplier, sendValueConsumer);
    }


    /** {@inheritDoc} */
    @Override
    public IFader createFader (final String label)
    {
        return new FaderImpl (this.host, label);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.controls.forEach (IControl::update);
    }
}
