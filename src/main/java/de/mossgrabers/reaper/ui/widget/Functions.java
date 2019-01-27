package de.mossgrabers.reaper.ui.widget;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Provides some useful static functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public final class Functions
{
    private static ResourceBundle messages;
    private static Dimension      openSaveDlgDim;


    /**
     * Initialise the Singleton object.
     *
     * @param messages A resource bundle which contains message texts
     */
    public static void init (final ResourceBundle messages)
    {
        init (messages, null);
    }


    /**
     * Get the size of the open and save dialog used for the getFileFromUser functions.
     *
     * @return The size of the open and save dialog used for the getFileFromUser functions
     */
    static Dimension getOpenSaveDlgDim ()
    {
        return openSaveDlgDim;
    }


    /**
     * Set the size of the open and save dialog used for the getFileFromUser functions.
     *
     * @param openSaveDlgDim The size of the open and save dialog used for the getFileFromUser
     *            functions.
     */
    static void setOpenSaveDlgDim (final Dimension openSaveDlgDim)
    {
        Functions.openSaveDlgDim = openSaveDlgDim;
    }


    /**
     * Initialise the Singleton object.
     *
     * @param messages A resource bundle which contains message texts
     * @param openSaveDlgDim The size of the open and save dialog used for the getFileFromUser
     *            functions.
     */
    public static void init (final ResourceBundle messages, final Dimension openSaveDlgDim)
    {
        Functions.messages = messages;
        setOpenSaveDlgDim (openSaveDlgDim);
    }


    /**
     * Get a message.
     *
     * @param messageID The ID of the message to get
     * @param replaceStrings Replaces the %1..%n in the message with the strings
     * @return The message
     * @see ResourceBundle#getString
     */
    public static String getMessage (final String messageID, final String... replaceStrings)
    {
        String message = messages == null ? "" : messages.getString (messageID);
        if (replaceStrings != null)
            message = replacePercentNWithStrings (message, replaceStrings);
        return message;
    }


    /**
     * Get a message which contains a %1. Replaces the %1 with the message text of the exception. If
     * the exception has no message the class name of the Exception is inserted.
     *
     * @param messageID The ID of the message to get
     * @param ex An exception
     * @return The message
     * @see ResourceBundle#getString
     */
    public static String getMessage (final String messageID, final Throwable ex)
    {
        final String msg = ex.getLocalizedMessage ();
        return getMessage (messageID, msg == null ? ex.getClass ().getName () : msg);
    }


    /**
     * Reads the text from the functions-resourcebundle if text starts with '@' otherwise the text
     * itself is returned.
     *
     * @param text The text or a message id starting with '@'
     * @return The loaded text
     */
    public static String getText (final String text)
    {
        if (noEmptyString (text) == null)
            return "";
        return text.charAt (0) == '@' ? getMessage (text.substring (1)) : text;
    }


    /**
     * Shows a message dialog with the message of an exception. If the exception does not contain a
     * message the exceptions class name is shown.
     *
     * @param ex An exception
     */
    public static void message (final Throwable ex)
    {
        final String msg = ex.getLocalizedMessage ();
        message (msg == null ? ex.getClass ().getName () : msg, (String) null);
    }


    /**
     * Shows a message dialog with the message of an exception inserted into a message. If the
     * exception does not contain a message the exceptions class name is shown.
     *
     * @param message The message to display or a resource key
     * @param ex An exception
     */
    public static void message (final String message, final Throwable ex)
    {
        final String msg = ex.getLocalizedMessage ();
        message (message, msg == null ? ex.getClass ().getName () : msg);
    }


    /**
     * Shows a message dialog. If the message starts with a '@' the message is interpreted as a
     * identifier for a string located in the resource file.
     *
     * @param message The message to display or a resource key
     * @param replaceStrings Replaces the %1..%n in the message with the strings
     * @see ResourceBundle#getString
     */
    public static void message (final String message, final String... replaceStrings)
    {
        final String t = getText (message);
        JOptionPane.showMessageDialog (getFocusedFrame (), replaceStrings == null ? t : replacePercentNWithStrings (t, replaceStrings));
    }


    /**
     * Shows a yes or no choice dialog.
     *
     * @param message The text of the message
     * @return true if yes is selected
     */
    public static boolean yesOrNo (final String message)
    {
        return yesOrNo (message, (String) null);
    }


    /**
     * Shows a yes or no choice dialog.
     *
     * @param frame The owner frame
     * @param message The text of the message
     * @return true if yes is selected
     */
    public static boolean yesOrNo (final Frame frame, final String message)
    {
        return yesOrNo (frame, message, (String) null);
    }


    /**
     * Shows a yes or no choice dialog.
     *
     * @param message The text of the message
     * @param replaceStrings Replaces the %1..%n in the message with the strings
     * @return True if yes is selected
     */
    public static boolean yesOrNo (final String message, final String... replaceStrings)
    {
        return yesOrNo (getFocusedFrame (), message, replaceStrings);
    }


    /**
     * Shows a yes or no choice dialog.
     *
     * @param frame The owner frame
     * @param message The text of the message
     * @param replaceStrings Replaces the %1..%n in the message with the strings
     * @return True if yes is selected
     */
    public static boolean yesOrNo (final Frame frame, final String message, final String... replaceStrings)
    {
        final String t = getText (message);
        return JOptionPane.showConfirmDialog (frame, replaceStrings == null ? t : replacePercentNWithStrings (t, replaceStrings), frame.getTitle (), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }


    /**
     * Choose 1 from 2 options.
     *
     * @param message The text of the message
     * @param title The title of the dialog
     * @param options The texts for the options
     * @return True if the first option was selected
     */
    public static boolean chooseFromTwo (final String message, final String title, final String... options)
    {
        return chooseFromTwo (getFocusedFrame (), message, title, options);
    }


    /**
     * Choose 1 from 2 options.
     *
     * @param parent The owning component
     * @param message The text of the message
     * @param title The title of the dialog
     * @param options The texts for the options
     * @return True if the first option was selected
     */
    public static boolean chooseFromTwo (final Component parent, final String message, final String title, final String... options)
    {
        return choose (parent, message, title, options) == JOptionPane.YES_OPTION;
    }


    /**
     * Choose from N options.
     *
     * @param message The text of the message
     * @param title The title of the dialog
     * @param options The texts for the options
     * @return The id of the selected option
     */
    public static int choose (final String message, final String title, final String... options)
    {
        return choose (getFocusedFrame (), message, title, options);
    }


    /**
     * Choose from N options.
     *
     * @param parent The owning component
     * @param message The text of the message
     * @param title The title of the dialog
     * @param options The texts for the options
     * @return The id of the selected option
     */
    public static int choose (final Component parent, final String message, final String title, final String... options)
    {
        for (int i = 0; i < options.length; i++)
            options[i] = getText (options[i]);
        return JOptionPane.showOptionDialog (parent, getText (message), getText (title), options.length == 2 ? JOptionPane.YES_NO_OPTION : JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
    }


    /**
     * If value contains an empty string the function returns null otherwise the unmodified string.
     *
     * @param value The value to check/modify
     * @return Null or the string
     */
    public static String noEmptyString (final String value)
    {
        return value != null && value.length () == 0 ? null : value;
    }


    /**
     * If value is null the function returns an empty string otherwise the unmodified string.
     *
     * @param value The value to check/modify
     * @return An empty string or the unmodified string
     */
    public static String noNullString (final String value)
    {
        return value == null ? "" : value;
    }


    /**
     * Helper function to compare to objects which may be null. If both are null they are considered
     * as equal.
     *
     * @param o1 One element to compare
     * @param o2 The other element to compare
     * @return True if the elements are equal
     */
    public static boolean compare (final Object o1, final Object o2)
    {
        return o1 == o2 || o1 != null && o1.equals (o2);
    }


    /**
     * Replaces '%X' X=[0, replaceStrings.length-1] in a message with the replaceStrings.
     *
     * @param message The message to modify
     * @param replaceStrings The strings to insert
     * @return The modified string
     */
    public static String replacePercentNWithStrings (final String message, final String [] replaceStrings)
    {
        String m = message;
        for (int i = 0; i < replaceStrings.length; i++)
        {
            final int pos = m.indexOf ("%" + (i + 1));
            if (pos != -1 && replaceStrings[i] != null)
                m = new StringBuilder (m.substring (0, pos)).append (replaceStrings[i]).append (m.substring (pos + 2)).toString ();
        }
        return m;
    }


    /**
     * Get an icon from a jar file.
     *
     * @param iconName The name (and path) of the icon
     * @return The retrieved icon
     */
    public static ImageIcon iconFor (final String iconName)
    {
        if (iconName == null)
            return null;

        URL resource = ClassLoader.getSystemResource (iconName);
        if (resource == null) // Works with JNLP / WebStart
            resource = Thread.currentThread ().getContextClassLoader ().getResource (iconName);

        return resource != null ? new ImageIcon (resource) : null;
    }


    /**
     * Sets the fan's height to the idol's size.
     *
     * @param idol Use its height for the fan
     * @param fan Gets the height of the idol set
     */
    public static void asHighAs (final JComponent idol, final JComponent fan)
    {
        final Dimension idolSize = idol.getPreferredSize ();
        final Dimension fanSize = fan.getPreferredSize ();
        fanSize.height = idolSize.height;
        fan.setMaximumSize (fanSize);
        fan.setPreferredSize (fanSize);
    }


    /**
     * Sets the fan's width to the idol's size.
     *
     * @param idol Use its width for the fan
     * @param fan Gets the width of the idol set
     */
    public static void asWidthAs (final JComponent idol, final JComponent fan)
    {
        final Dimension idolSize = idol.getPreferredSize ();
        final Dimension fanSize = fan.getPreferredSize ();
        fanSize.width = idolSize.width;
        fan.setMaximumSize (fanSize);
        fan.setPreferredSize (fanSize);
    }


    /**
     * Sets a minimum and a preferred size for the scroll pane of the given component.
     *
     * @param component The component which to size
     * @param width The preferred width
     * @param height The preferred height
     */
    public static void fixScrollPaneTo (final JComponent component, final int width, final int height)
    {
        final Component comp = component.getParent ().getParent ();
        if (comp instanceof JComponent)
        {
            final JComponent c = (JComponent) comp;
            final Dimension dim = new Dimension (width, height);
            c.setMinimumSize (dim);
            c.setPreferredSize (dim);
        }
    }


    /**
     * Sets a minimum and a preferred width for the scroll pane of the given component.
     *
     * @param component The component which to size
     * @param width The preferred width
     */
    public static void fixScrollPaneTo (final JComponent component, final int width)
    {
        final Component comp = component.getParent ().getParent ();
        if (comp instanceof JComponent)
        {
            final JComponent c = (JComponent) comp;
            final Dimension dim = c.getPreferredSize ();
            dim.width = width;
            c.setMinimumSize (dim);
            c.setPreferredSize (dim);
        }
    }


    /**
     * Dis-/Enables a component with all their subcomponents.
     *
     * @param c The component to en-/disable
     * @param enable What to do
     */
    public static void enableComponent (final Component c, final boolean enable)
    {
        c.setEnabled (enable);
        if (c instanceof Container)
        {
            final Component [] cs = ((Container) c).getComponents ();
            for (final Component element: cs)
                enableComponent (element, enable);
        }
    }


    /**
     * Positions a popup menu on the screen where the given mouse click did occur. Takes care that
     * the menu is fully visible on the screen.
     *
     * @param menu The menu to display
     * @param e The mouse event
     */
    public static void showPopupMenu (final JPopupMenu menu, final MouseEvent e)
    {
        showPopupMenu (menu, e.getComponent (), e.getX (), e.getY ());
    }


    /**
     * Positions a popup menu on the screen at the given x/y position. Takes care that the menu is
     * fully visible on the screen.
     *
     * @param menu The menu to display
     * @param component The component in whose space the popup menu is to appear
     * @param x The x position where the menu should be displayed
     * @param y The y position where the menu should be displayed
     */
    public static void showPopupMenu (final JPopupMenu menu, final Component component, final int x, final int y)
    {
        final Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
        final Point pos = component.getLocationOnScreen ();
        final Dimension menuSize = menu.getPreferredSize ();
        final int larger = pos.x + x + menuSize.width - screenSize.width;
        menu.show (component, larger > 0 ? x - larger : x, pos.y + y + menuSize.height > screenSize.height ? y - menuSize.height : y);
    }


    /**
     * Show the WAIT-Cursor for the mouse.
     *
     * @param busy True if the WAIT-Curor should be shown
     */
    public static void setBusy (final boolean busy)
    {
        final Frame frame = getFocusedFrame ();
        if (frame instanceof JFrame)
            ((JFrame) frame).getGlassPane ().setVisible (busy);
    }

    /**
     * Adds some functionality to a JFileChooser.
     */
    static class Chooser extends JFileChooser
    {
        private static final long serialVersionUID = 3977304317382505779L;

        String                    existanceMsg;
        boolean                   open;


        /**
         * Constructor.
         *
         * @param open True if this is an open dialog otherwise it is an save dialog
         * @param currentPath The current path to set
         * @param existanceMsg The message to display if this is a save dialog and the file does
         *            already exist
         * @param allowFolders If true also folder may be selected
         */
        public Chooser (final boolean open, final String currentPath, final String existanceMsg, final boolean allowFolders)
        {
            this.open = open;
            if (currentPath != null)
            {
                final File cp = new File (currentPath);
                if (cp.isDirectory ())
                    this.setCurrentDirectory (cp);
                else
                    this.setSelectedFile (cp);
            }
            this.existanceMsg = existanceMsg;
            if (this.existanceMsg != null && this.existanceMsg.length () > 0 && this.existanceMsg.charAt (0) == '@')
                this.existanceMsg = Functions.getMessage (this.existanceMsg.substring (1));
            this.setFileSelectionMode (allowFolders ? JFileChooser.FILES_AND_DIRECTORIES : JFileChooser.FILES_ONLY);
            this.setFileHidingEnabled (true);

            final Dimension size = getOpenSaveDlgDim ();
            if (size != null)
                this.setPreferredSize (size);
        }


        /** {@inheritDoc} */
        @Override
        public void approveSelection ()
        {
            setOpenSaveDlgDim (this.getSize ());

            final File file = this.getSelectedFile ();
            if (!this.open && file.exists () && !yesOrNo (this.existanceMsg, file.getPath ()))
                return;
            super.approveSelection ();
        }
    }


    /**
     * Get the frame with the focus.
     *
     * @return The frame with the focus
     */
    public static Frame getFocusedFrame ()
    {
        final Frame [] frames = Frame.getFrames ();
        for (final Frame element: frames)
        {
            if (element.getFocusOwner () != null)
                return element;
        }
        return frames.length > 0 ? frames[0] : null;
    }


    /**
     * Private constructor because this is a utility class.
     */
    private Functions ()
    {
        // Empty by intention
    }
}
