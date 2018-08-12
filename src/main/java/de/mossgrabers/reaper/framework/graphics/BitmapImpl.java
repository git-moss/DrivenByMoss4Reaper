// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.graphics;

import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IRenderer;
import de.mossgrabers.transformator.BasicDialog;
import de.mossgrabers.transformator.ui.BoxPanel;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;


/**
 * Implementation of a bitmap.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BitmapImpl extends BasicDialog implements IBitmap
{
    private static final long serialVersionUID = -6034592629355700876L;

    final BufferedImage       bufferedImage;
    private final JPanel      canvas           = new JPanel ()
                                               {
                                                   private static final long serialVersionUID = 971155807100338380L;


                                                   @Override
                                                   public void paintComponent (final Graphics gc)
                                                   {
                                                       // Let UI Delegate paint first, which
                                                       // includes background filling since
                                                       // this component is opaque.

                                                       super.paintComponent (gc);
                                                       gc.drawImage (BitmapImpl.this.bufferedImage, 0, 0, this.getWidth (), this.getWidth () / 6, null);
                                                   }
                                               };
    private final Timer       animationTimer;


    /**
     * Constructor.
     *
     * @param owner
     *
     * @param width The width of the bitmap
     * @param height The height of the bitmap
     */
    public BitmapImpl (final Window owner, final int width, final int height)
    {
        super ((JFrame) owner, "", true, true);

        this.bufferedImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);

        final Dimension dim = new Dimension (width, height);
        this.setMinimumSize (dim);
        this.canvas.setMinimumSize (dim);
        this.canvas.setMaximumSize (dim);
        this.canvas.setSize (dim);

        this.animationTimer = new Timer (60, evemt -> {
            BitmapImpl.this.canvas.validate ();
            BitmapImpl.this.canvas.repaint ();
        });

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


    /** {@inheritDoc} */
    @Override
    protected void set () throws Exception
    {
        this.animationTimer.start ();
    }


    /** {@inheritDoc} */
    @Override
    public void fillTransferBuffer (final ByteBuffer buffer)
    {
        final int [] pixels = ((DataBufferInt) this.bufferedImage.getRaster ().getDataBuffer ()).getData ();
        final int height = this.bufferedImage.getHeight ();
        final int width = this.bufferedImage.getWidth ();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int pixel = pixels[x + y * width];
                final int red = ((pixel & 0x00FF0000) >> 16) * 31 / 255;
                final int green = ((pixel & 0x0000FF00) >> 8) * 63 / 255;
                final int blue = (pixel & 0x000000FF) * 31 / 255;

                // 3b(low) green - 5b red / 5b blue - 3b (high) green, e.g. gggRRRRR BBBBBGGG
                buffer.put ((byte) ((green & 0x07) << 5 | red & 0x1F));
                buffer.put ((byte) ((blue & 0x1F) << 3 | (green & 0x38) >> 3));
            }
            for (int x = 0; x < 128; x++)
                buffer.put ((byte) 0x00);
        }

        buffer.rewind ();
    }


    /** {@inheritDoc} */
    @Override
    public void setDisplayWindowTitle (final String title)
    {
        this.setTitle (title);
    }


    /** {@inheritDoc} */
    @Override
    public void showDisplayWindow ()
    {
        if (this.isShowing ())
            return;
        this.setVisible (true);
        this.animationTimer.start ();
    }


    /** {@inheritDoc} */
    @Override
    public void render (final IRenderer renderer)
    {
        renderer.render (new GraphicsContextImpl (this.bufferedImage.createGraphics ()));
    }


    /** {@inheritDoc} */
    @Override
    protected boolean onCancel ()
    {
        this.animationTimer.stop ();
        return true;
    }
}
