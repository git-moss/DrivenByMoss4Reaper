// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.awt.Toolkit;


/**
 * Limits the characters in the document to numbers and a maximum number length.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class WholeNumberDocument extends MaxLengthDocument
{
    private static final long serialVersionUID = 3257007674414675511L;

    boolean                   onlyPositive     = true;


    /**
     * Default constructor. Allows only positive numbers and has no length limit.
     */
    public WholeNumberDocument ()
    {
        this (true, -1);
    }


    /**
     * Constructor.
     *
     * @param onlyPositive Also forbids negative numbers (0 is allowed)
     */
    public WholeNumberDocument (final boolean onlyPositive)
    {
        this (onlyPositive, -1);
    }


    /**
     * Constructor.
     *
     * @param onlyPositive Also forbids negative numbers
     * @param maxLength The maximum number of characters of the document
     */
    public WholeNumberDocument (final boolean onlyPositive, final int maxLength)
    {
        super (maxLength);
        this.onlyPositive = onlyPositive;
    }


    /**
     * Overwritten to limit the insertion of text to numbers.
     *
     * @param offs The offset in the text
     * @param str The string to insert
     * @param a The attributes of the string
     * @throws BadLocationException Something crashed
     */
    @Override
    public void insertString (final int offs, final String str, final AttributeSet a) throws BadLocationException
    {
        final char [] source = str.toCharArray ();
        final char [] result = new char [source.length];

        int j = 0;
        for (int i = 0; i < result.length; i++)
        {
            if (Character.isDigit (source[i]) || !this.onlyPositive && offs == 0 && i == 0 && source[i] == '-')
                result[j++] = source[i];
            else
                Toolkit.getDefaultToolkit ().beep ();
        }
        super.insertString (offs, new String (result, 0, j), a);
    }
}