// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.reaper.framework.Actions;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Proxy to the Bitwig Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipImpl extends BaseImpl implements ICursorClip
{
    private ITransport    transport;
    private IValueChanger valueChanger;

    private int           numSteps;
    private int           numRows;

    private double        clipStart = -1;
    private double        clipEnd   = -1;


    /**
     * Constructor.
     *
     * @param transport The transport
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numSteps The number of steps of the clip to monitor
     * @param numRows The number of note rows of the clip to monitor
     */
    public CursorClipImpl (final ITransport transport, final MessageSender sender, final IValueChanger valueChanger, final int numSteps, final int numRows)
    {
        super (sender, null);

        this.transport = transport;
        this.valueChanger = valueChanger;
        this.numSteps = numSteps;
        this.numRows = numRows;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.sendClipOSC ("color", "RGB(" + Math.round (red * 255) + "," + Math.round (green * 255) + "," + Math.round (blue * 255) + ")");
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        // Not used since there is no session view but could be retrieved (I_CUSTOMCOLOR)
        return new double []
        {
            0,
            0,
            0
        };
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayStart ()
    {
        return this.clipStart;
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayStart (final double start)
    {
        this.clipStart = start;
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayStart (final int control)
    {
        if (this.clipStart == -1)
            return;
        this.clipStart = Math.max (0, this.clipStart + this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
        this.sendClipOSC ("start", Double.valueOf (this.clipStart));
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayEnd ()
    {
        return this.clipEnd;
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayEnd (final double end)
    {
        this.clipEnd = end;
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayEnd (final int control)
    {
        if (this.clipEnd == -1)
            return;
        final double speed = this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1);
        this.clipEnd = Math.max (0, this.clipEnd + speed);
        this.sendClipOSC ("end", Double.valueOf (this.clipEnd));
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayRange (final double start, final double end)
    {
        this.setPlayStart (start);
        this.setPlayEnd (end);
        this.sendClipOSC ("start", Double.valueOf (this.clipStart));
        this.sendClipOSC ("end", Double.valueOf (this.clipEnd));
    }


    /** {@inheritDoc} */
    @Override
    public double getLoopStart ()
    {
        return this.getPlayStart ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopStart (final double start)
    {
        this.setPlayStart (start);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopStart (final int control)
    {
        this.changePlayStart (control);
    }


    /** {@inheritDoc} */
    @Override
    public double getLoopLength ()
    {
        return this.getPlayEnd () - this.getPlayStart ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopLength (final int length)
    {
        this.setPlayEnd (this.getPlayStart () + length);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopLength (final int control)
    {
        this.changePlayEnd (control);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLoopEnabled ()
    {
        // No loops in Reaper use the global loop
        return this.transport.isLoop ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopEnabled (final boolean enable)
    {
        // No loops in Reaper use the global loop
        this.transport.setLoop (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShuffleEnabled ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setShuffleEnabled (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public String getFormattedAccent ()
    {
        return Math.round (this.getAccent () * 10000) / 100 + "%";
    }


    /** {@inheritDoc} */
    @Override
    public double getAccent ()
    {
        // Not supported
        return 1;
    }


    /** {@inheritDoc} */
    @Override
    public void resetAccent ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeAccent (final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSteps ()
    {
        return this.numSteps;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumRows ()
    {
        return this.numRows;
    }


    /** {@inheritDoc} */
    @Override
    public int getCurrentStep ()
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getStep (final int step, final int row)
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleStep (final int step, final int row, final int velocity)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int step, final int row, final int velocity, final double duration)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void clearStep (final int step, final int row)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void clearRow (final int row)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRowData (final int row)
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getLowerRowWithData ()
    {
        // Not supported
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperRowWithData ()
    {
        // Not supported
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setStepLength (final double length)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public double getStepLength ()
    {
        // Not supported
        return 1;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int step, final int row)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToPage (final int page)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getEditPage ()
    {
        // Not supported
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollStepsPageBackwards ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollStepsPageForward ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsBackwards ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsForwards ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.invokeAction (Actions.DUPLICATE_ITEMS);
    }


    /** {@inheritDoc} */
    @Override
    public void duplicateContent ()
    {
        this.invokeAction (Actions.DUPLICATE_ITEMS);
        this.invokeAction (Actions.ADD_LEFT_ITEM_TO_SELECTION);
        this.invokeAction (Actions.GLUE_ITEMS);
    }


    /** {@inheritDoc} */
    @Override
    public void quantize (final double amount)
    {
        this.sender.sendOSC ("/quantize", null);
    }


    /** {@inheritDoc} */
    @Override
    public void transpose (final int semitones)
    {
        // Not supported
    }


    protected void sendClipOSC (final String command, final Object value)
    {
        this.sender.sendOSC ("/clip/" + command + "/", value);
    }
}