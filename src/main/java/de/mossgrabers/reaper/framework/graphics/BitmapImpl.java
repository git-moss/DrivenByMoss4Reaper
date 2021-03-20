// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.graphics;

import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IEncoder;
import de.mossgrabers.framework.graphics.IRenderer;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.dialog.BasicDialog;
import de.mossgrabers.reaper.ui.utils.FontCache;
import de.mossgrabers.reaper.ui.widget.BoxPanel;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;


/**
 * Implementation of a bitmap.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BitmapImpl implements IBitmap
{
    private final WindowManager windowManager;
    private final BufferedImage bufferedImage;
    private final ByteBuffer    imageBuffer;
    private final Object        windowLock  = new Object ();
    private BitmapWindow        window;
    private String              windowTitle = "";


    /**
     * Constructor.
     *
     * @param windowManager The window manager
     * @param width The width of the bitmap
     * @param height The height of the bitmap
     */
    public BitmapImpl (final WindowManager windowManager, final int width, final int height)
    {
        this.windowManager = windowManager;
        this.bufferedImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
        this.imageBuffer = ByteBuffer.allocateDirect (width * height * 4);
    }


    /** {@inheritDoc} */
    @Override
    public void encode (final IEncoder encoder)
    {
        synchronized (this.bufferedImage)
        {
            this.imageBuffer.clear ();

            final WritableRaster raster = this.bufferedImage.getRaster ();

            final int [] pixel = new int [4];
            final int h = this.bufferedImage.getHeight ();
            final int w = this.bufferedImage.getWidth ();
            for (int y = 0; y < h; y++)
            {
                for (int x = 0; x < w; x++)
                {
                    raster.getPixel (x, y, pixel);

                    this.imageBuffer.put ((byte) pixel[2]);
                    this.imageBuffer.put ((byte) pixel[1]);
                    this.imageBuffer.put ((byte) pixel[0]);
                    // Alpha not used
                    this.imageBuffer.put ((byte) 0);
                }
            }

            this.imageBuffer.rewind ();
            encoder.encode (this.imageBuffer, w, h);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setDisplayWindowTitle (final String title)
    {
        this.windowTitle = title;
    }


    /** {@inheritDoc} */
    @Override
    public void showDisplayWindow ()
    {
        synchronized (this.windowLock)
        {
            if (this.window == null)
                this.window = new BitmapWindow (this.windowManager.getMainFrame ());
        }
        this.window.setTitle (this.windowTitle);
        this.window.showWindow ();
    }


    /** {@inheritDoc} */
    @Override
    public void render (final boolean enableAntialias, final IRenderer renderer)
    {
        synchronized (this.bufferedImage)
        {
            renderer.render (new GraphicsContextImpl (this.bufferedImage.createGraphics (), FontCache.SANS_SERIF, enableAntialias));
        }
    }


    /**
     * Draw the buffered image in the given graphics context.
     *
     * @param gc The graphics context
     * @param x The x position
     * @param y The y position
     * @param width The width to scale the image to
     * @param height The height to scale the image to
     */
    public void drawScaledImage (final Graphics gc, final int x, final int y, final int width, final int height)
    {
        synchronized (this.bufferedImage)
        {
            gc.drawImage (this.bufferedImage, x, y, width, height, null);
        }
    }


    protected void drawImage (final Graphics gc)
    {
        synchronized (this.bufferedImage)
        {
            gc.drawImage (this.bufferedImage, 0, 0, this.bufferedImage.getWidth (), this.bufferedImage.getHeight (), null);
        }
    }


    private class BitmapWindow extends BasicDialog
    {
        private static final int  BITMAP_REDRAW_RATE = 200;
        private static final long serialVersionUID   = -6034592629355700876L;

        private final Timer       animationTimer;

        private final JPanel      canvas             = new JPanel ()
                                                     {
                                                         private static final long serialVersionUID = 971155807100338380L;


                                                         @Override
                                                         public void paintComponent (final Graphics gc)
                                                         {
                                                             // Let UI Delegate paint first, which
                                                             // includes background filling since
                                                             // this component is opaque.
                                                             super.paintComponent (gc);
                                                             BitmapImpl.this.drawImage (gc);
                                                         }
                                                     };


        public BitmapWindow (final JFrame owner)
        {
            super (owner, "", true, false);

            this.animationTimer = new Timer (BITMAP_REDRAW_RATE, event -> this.canvas.repaint ());
            this.canvas.setPreferredSize (new Dimension (BitmapImpl.this.bufferedImage.getWidth (), BitmapImpl.this.bufferedImage.getHeight ()));

            this.basicInit ();
        }


        /** {@inheritDoc} */
        @Override
        protected Container init () throws Exception
        {
            final JPanel contentPane = new JPanel (new BorderLayout ());

            contentPane.add (this.canvas, BorderLayout.CENTER);

            // Close button
            final BoxPanel buttons = new BoxPanel (BoxLayout.X_AXIS, true);
            buttons.createSpace (BoxPanel.GLUE);
            this.setButtons (null, buttons.createButton ("Close", null, BoxPanel.NONE));
            contentPane.add (buttons, BorderLayout.SOUTH);

            return contentPane;
        }


        public void showWindow ()
        {
            if (this.isShowing ())
                return;
            this.animationTimer.start ();
            // Since it is modal, the next function call blocks!
            this.setVisible (true);
            this.animationTimer.stop ();
        }
    }
}
