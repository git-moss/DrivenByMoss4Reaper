package de.mossgrabers.reaper.framework.graphics;

import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING;
import static java.awt.RenderingHints.KEY_DITHERING;
import static java.awt.RenderingHints.KEY_FRACTIONALMETRICS;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_DITHER_DISABLE;
import static java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_STROKE_PURE;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

import de.mossgrabers.framework.graphics.IImage;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGElementException;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.animation.AnimationElement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A Scalable Vector Graphic (SVG) image file. xxxxxxx
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SVGImage implements IImage
{
    private static final Map<Object, Object>               RENDERING_HINTS = Map.of (KEY_ANTIALIASING, VALUE_ANTIALIAS_ON, KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY, KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY, KEY_DITHERING, VALUE_DITHER_DISABLE, KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON, KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC, KEY_RENDERING, VALUE_RENDER_QUALITY, KEY_STROKE_CONTROL, VALUE_STROKE_PURE, KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
    private static final SVGUniverse                       RENDERER        = new SVGUniverse ();

    private static final Map<String, Map<Color, SVGImage>> CACHE           = new HashMap<> ();
    private static final Object                            CACHE_LOCK      = new Object ();
    private static final Set<String>                       STROKE_ELEMENTS = new HashSet<> (4);

    private final BufferedImage                            bufferedImage;
    private final String                                   imageName;

    static
    {
        Collections.addAll (STROKE_ELEMENTS, "polygon", "circle", "path", "rect");
    }


    /**
     * Get a SVG image as a buffered image. The image is expected to be monochrome: 1 color and the
     * a transparent background. The given color replaces the color of the image. The images are
     * cached by name and color.
     *
     * @param imageName The name (absolute path) of the image
     * @param color The color for replacement
     * @return The buffered image
     * @throws IOException Could not load the image
     */
    public static SVGImage getSVGImage (final String imageName, final Color color) throws IOException
    {
        SVGImage svgImage;
        synchronized (CACHE_LOCK)
        {
            final Map<Color, SVGImage> images = CACHE.computeIfAbsent (imageName, in -> new HashMap<> ());
            svgImage = images.get (color);
            if (svgImage == null)
            {
                svgImage = new SVGImage (imageName, color);
                images.put (color, svgImage);
            }
        }
        return svgImage;
    }


    /**
     * Clears the image cache.
     */
    public static void clearCache ()
    {
        synchronized (CACHE_LOCK)
        {
            CACHE.clear ();
        }
    }


    /**
     * Constructor.
     *
     * @param imageName The name of the image (absolute path) to load
     * @param color The replacement color
     * @throws IOException Could not load the image
     */
    public SVGImage (final String imageName, final Color color) throws IOException
    {
        this.imageName = imageName;

        try
        {
            final SVGDiagram diagram = RENDERER.getDiagram (RENDERER.loadSVG (this.getClass ().getResource (imageName)));
            changeColorOfElement (toText (color), diagram.getRoot ());

            this.bufferedImage = new BufferedImage ((int) diagram.getWidth (), (int) diagram.getHeight (), BufferedImage.TYPE_INT_ARGB);
            final Graphics2D graphics = (Graphics2D) this.bufferedImage.getGraphics ();
            graphics.setRenderingHints (RENDERING_HINTS);
            diagram.render (graphics);
        }
        catch (final SVGException ex)
        {
            throw new IOException (ex);
        }
    }


    /**
     * Get the buffered image.
     *
     * @return The buffered image
     */
    public BufferedImage getImage ()
    {
        return this.bufferedImage;
    }


    /**
     * Get the name of the image.
     *
     * @return The name
     */
    public String getImageName ()
    {
        return this.imageName;
    }


    /**
     * Adds a new fill color to all given elements.
     *
     * @param color The replacement color
     * @param node The node in which to replace the color
     * @throws SVGElementException
     */
    private static void changeColorOfElement (final String color, final SVGElement node) throws SVGElementException
    {
        if (STROKE_ELEMENTS.contains (node.getTagName ()))
        {
            if (node.hasAttribute ("fill", AnimationElement.AT_XML))
                node.setAttribute ("fill", AnimationElement.AT_XML, color);
            else
                node.addAttribute ("fill", AnimationElement.AT_XML, color);
        }

        for (int i = 0; i < node.getNumChildren (); i++)
            changeColorOfElement (color, node.getChild (i));
    }


    /**
     * Formats the given color as CSS rgb string.
     *
     * @param color The color to format
     * @return The formatted color string
     */
    private static final String toText (final Color color)
    {
        return new StringBuilder (20).append ("rgb(").append (color.getRed ()).append (',').append (color.getGreen ()).append (',').append (color.getBlue ()).append (')').toString ();
    }


    /** {@inheritDoc} */
    @Override
    public double getWidth ()
    {
        return this.getImage ().getWidth ();
    }


    /** {@inheritDoc} */
    @Override
    public int getHeight ()
    {
        return this.getImage ().getHeight ();
    }
}
