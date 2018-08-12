// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator.ui.widgets;

import javax.swing.JTextField;
import javax.swing.text.Document;


/**
 * Adds some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class JTextFieldX extends JTextField
{
    private static final long serialVersionUID = -7699003940145959050L;


    /**
     * Constructs a new <code>TextField</code>. A default model is created, the initial string is
     * <code>null</code>, and the number of columns is set to 0.
     */
    public JTextFieldX ()
    {
        // Intentionally empty
    }


    /**
     * Constructs a new <code>TextField</code> initialized with the specified text. A default model
     * is created and the number of columns is 0.
     *
     * @param text the text to be displayed, or <code>null</code>
     */
    public JTextFieldX (final String text)
    {
        super (text);
    }


    /**
     * Constructs a new empty <code>TextField</code> with the specified number of columns. A default
     * model is created and the initial string is set to <code>null</code>.
     *
     * @param columns the number of columns to use to calculate the preferred width; if columns is
     *            set to zero, the preferred width will be whatever naturally results from the
     *            component implementation
     */
    public JTextFieldX (final int columns)
    {
        super (columns);
    }


    /**
     * Constructs a new <code>TextField</code> initialized with the specified text and columns. A
     * default model is created.
     *
     * @param text the text to be displayed, or <code>null</code>
     * @param columns the number of columns to use to calculate the preferred width; if columns is
     *            set to zero, the preferred width will be whatever naturally results from the
     *            component implementation
     */
    public JTextFieldX (final String text, final int columns)
    {
        super (text, columns);
    }


    /**
     * Constructs a new <code>JTextField</code> that uses the given text storage model and the given
     * number of columns. This is the constructor through which the other constructors feed. If the
     * document is <code>null</code>, a default model is created.
     *
     * @param doc the text storage to use; if this is <code>null</code>, a default will be provided
     *            by calling the <code>createDefaultModel</code> method
     * @param text the initial string to display, or <code>null</code>
     * @param columns the number of columns to use to calculate the preferred width >= 0; if
     *            <code>columns</code> is set to zero, the preferred width will be whatever
     *            naturally results from the component implementation
     */
    public JTextFieldX (final Document doc, final String text, final int columns)
    {
        super (doc, text, columns);
    }


    /**
     * Parses an integer from the text. If an NumberFormatException occurs null is returned.
     *
     * @return The integer or null
     */
    public Integer getIntegerFromText ()
    {
        try
        {
            final String text = this.getText ();
            return text.length () == 0 ? null : Integer.valueOf (text);
        }
        catch (final NumberFormatException ex)
        {
            return null;
        }
    }


    /**
     * Parses an integer from the text. If an NumberFormatException occurs -1 or no text is present
     * null is returned.
     *
     * @return The integer or -1
     */
    public int getIntFromText ()
    {
        final Integer i = this.getIntegerFromText ();
        return i == null ? -1 : i.intValue ();
    }


    /**
     * Sets an integer as the text value. If the value is null an empty string is set.
     *
     * @param value The integer to set
     */
    public void setIntegerAsText (final Integer value)
    {
        this.setText (value == null ? "" : value.toString ());
    }


    /**
     * Sets an integer as the text value.
     *
     * @param value The integer to set
     */
    public void setIntAsText (final int value)
    {
        this.setText (Integer.toString (value));
    }


    /**
     * Sets an integer as the text value. If the value is less than 0 an empty string is set.
     *
     * @param value The integer to set
     */
    public void setPositiveIntAsText (final int value)
    {
        this.setText (value < 0 ? "" : Integer.toString (value));
    }
}
