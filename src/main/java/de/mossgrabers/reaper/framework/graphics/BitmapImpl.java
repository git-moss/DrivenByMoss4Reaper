// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.graphics;

import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IRenderer;

import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;


/**
 * Implementation of a bitmap.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BitmapImpl extends Dialog<Void> implements IBitmap
{
    private final BufferedImage  bufferedImage;
    private final Canvas         canvas  = new Canvas ();
    private final AnimationTimer animationTimer;

    private WritableImage        fxImage = null;


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
        this.bufferedImage = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);

        this.initModality (Modality.NONE);
        this.initOwner (owner);

        final DialogPane dialogPane = this.getDialogPane ();
        dialogPane.getButtonTypes ().add (new ButtonType ("Close", ButtonData.CANCEL_CLOSE));

        this.canvas.widthProperty ().set (width);
        this.canvas.heightProperty ().set (height);

        final StackPane canvasContainer = new StackPane (this.canvas);
        canvasContainer.getStyleClass ().add ("display");

        dialogPane.setContent (canvasContainer);

        this.animationTimer = new AnimationTimer ()
        {
            @Override
            public void handle (final long now)
            {
                BitmapImpl.this.updateDisplay ();
            }
        };

        this.setOnHidden (event -> this.animationTimer.stop ());
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
        this.show ();
        this.animationTimer.start ();
    }


    /** {@inheritDoc} */
    @Override
    public void render (final IRenderer renderer)
    {
        renderer.render (new GraphicsContextImpl (this.bufferedImage.createGraphics ()));
    }


    /**
     * Update the virtual and real display.
     */
    void updateDisplay ()
    {
        final GraphicsContext gc = this.canvas.getGraphicsContext2D ();
        this.fxImage = SwingFXUtils.toFXImage (this.bufferedImage, this.fxImage);
        gc.drawImage (this.fxImage, 0, 0, this.canvas.getWidth (), this.canvas.getWidth () / 6);
    }
}
