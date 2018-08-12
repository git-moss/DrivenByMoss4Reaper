// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.transformator.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * Limits the number of characters in the document to a maximum length.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaxLengthDocument extends PlainDocument
{
    private static final long serialVersionUID = 3907209364334065207L;

    protected int             maxLength;


    /**
     * Constructor.
     *
     * @param maxLength The maximum number of characters of the document
     */
    public MaxLengthDocument (final int maxLength)
    {
        super ();
        this.maxLength = maxLength;
    }


    /**
     * Overwritten to limit the insertion of text.
     *
     * @param offs The offset in the text
     * @param str The string to insert
     * @param a The attributes of the string
     * @throws BadLocationException Something crashed
     */
    @Override
    public void insertString (final int offs, final String str, final AttributeSet a) throws BadLocationException
    {
        if (str == null)
            return;
        String s = str;
        if (this.maxLength != -1)
        {
            final int length = s.length ();
            final int tooMuch = this.getLength () + length - this.maxLength;
            if (tooMuch > 0)
                s = s.substring (0, length - tooMuch);
        }
        super.insertString (offs, s, a);
    }
}
