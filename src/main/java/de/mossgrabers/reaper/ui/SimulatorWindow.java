// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui;

import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.reaper.framework.graphics.GraphicsContextImpl;
import de.mossgrabers.reaper.framework.hardware.HwSurfaceFactoryImpl;
import de.mossgrabers.reaper.ui.utils.FontCache;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;


/**
 * The device UI simulator window.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SimulatorWindow extends JFrame
{
    private static final long          serialVersionUID = 683494997839264599L;

    private final HwSurfaceFactoryImpl surfaceFactory;
    private final IControlSurface<?>   surface;
    private double                     scaleFactor      = -1;


    /**
     * Constructor.
     *
     * @param surface The surface to simulate
     * @param title The window title
     */
    public SimulatorWindow (final IControlSurface<?> surface, final String title)
    {
        super (title);

        final URL resource = this.getClass ().getResource ("/images/AppIcon.gif");
        final Image image = Toolkit.getDefaultToolkit ().getImage (resource);
        if (image != null)
            this.setIconImage (image);

        this.surface = surface;
        this.surfaceFactory = (HwSurfaceFactoryImpl) this.surface.getSurfaceFactory ();

        final JPanel canvas = new JPanel ()
        {
            private static final long serialVersionUID = 6138483938641840923L;


            /** {@inheritDoc} */
            @Override
            public void paintComponent (final Graphics g)
            {
                super.paintComponent (g);
                SimulatorWindow.this.render (new GraphicsContextImpl ((Graphics2D) g, FontCache.MONOSPACED));
            }
        };

        this.getContentPane ().add (canvas);
        this.pack ();

        this.setSize (this.getSimulatorSize ());

        this.getRootPane ().addComponentListener (new ComponentAdapter ()
        {
            /** {@inheritDoc} */
            @Override
            public void componentResized (final ComponentEvent e)
            {
                SimulatorWindow.this.scaleFactor = -1;
                SimulatorWindow.this.repaint ();
            }
        });

        final MouseAdapter mouseListener = new MouseAdapter ()
        {
            /** {@inheritDoc} */
            @Override
            public void mousePressed (final MouseEvent e)
            {
                SimulatorWindow.this.handleMouseEvent (e);
            }


            /** {@inheritDoc} */
            @Override
            public void mouseReleased (final MouseEvent e)
            {
                SimulatorWindow.this.handleMouseEvent (e);
            }


            /** {@inheritDoc} */
            @Override
            public void mouseDragged (final MouseEvent e)
            {
                SimulatorWindow.this.handleMouseEvent (e);
            }
        };
        canvas.addMouseListener (mouseListener);
        canvas.addMouseMotionListener (mouseListener);
    }


    /**
     * Handle the mouse events of the UI.
     *
     * @param event The mouse event
     */
    private void handleMouseEvent (final MouseEvent event)
    {
        this.surfaceFactory.getControls ().forEach (control -> control.mouse (event.getID (), event.getX (), event.getY (), this.scaleFactor));
    }


    /**
     * Render the simulator.
     *
     * @param gc The graphics context in which to render
     */
    private void render (final IGraphicsContext gc)
    {
        final Dimension innerSize = this.getInnerSize ();
        if (this.scaleFactor < 0)
            this.scaleFactor = Math.min (innerSize.width / this.surfaceFactory.getWidth (), innerSize.height / this.surfaceFactory.getHeight ());
        gc.fillRectangle (0, 0, innerSize.width, innerSize.height, ColorEx.GRAY);

        this.surfaceFactory.getControls ().forEach (control -> control.draw (gc, this.scaleFactor));
    }


    /**
     * Calculate the inner size of the window which can be used for the simualator UI.
     *
     * @return The dimensions
     */
    private Dimension getInnerSize ()
    {
        final Dimension size = this.getSize ();
        final Insets insets = this.getInsets ();
        if (insets != null)
        {
            size.height -= insets.top + insets.bottom;
            size.width -= insets.left + insets.right;
        }
        return size;
    }


    /**
     * Calculate the size for the simulator window which is 80% of the available screen.
     *
     * @return The dimensions of the window
     */
    private Dimension getSimulatorSize ()
    {
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment ().getDefaultScreenDevice ();
        final DisplayMode displayMode = gd.getDisplayMode ();
        final int screenHeight = displayMode.getHeight ();
        final int screenWidth = displayMode.getWidth ();

        final double ratio = this.surfaceFactory.getWidth () / this.surfaceFactory.getHeight ();
        int height = (int) Math.round (screenHeight * 0.8);
        int width = (int) Math.round (height * ratio);
        if (width > screenWidth)
        {
            width = (int) Math.round (screenWidth * 0.8);
            height = (int) Math.round (width / ratio);
        }
        return new Dimension (width, height);
    }
}