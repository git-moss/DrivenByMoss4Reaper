// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractBankImpl<ITrack> implements ITrackBank
{
    private int        numScenes;
    private int        numSends;
    private ISceneBank sceneBank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param sender The OSC sender
     * @param valueChanger The value changer
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    public AbstractTrackBankImpl (final IHost host, final MessageSender sender, final IValueChanger valueChanger, final int numTracks, final int numScenes, final int numSends)
    {
        super (host, sender, valueChanger, numTracks);
        this.numScenes = numScenes;
        this.numSends = numSends;

        this.initItems ();

        this.sceneBank = new SceneBankImpl (host, sender, this.numScenes);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedItem ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getIndex () + 1 + "/select", Integer.valueOf (0));
        this.sendTrackOSC ("bank/-", null);
        this.sendTrackOSC (this.getPageSize () + "/select", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        // Deselect previous selected track (if any)
        final ITrack selectedTrack = this.getSelectedItem ();
        if (selectedTrack != null)
            this.sendTrackOSC (selectedTrack.getIndex () + 1 + "/select", Integer.valueOf (0));
        this.sendTrackOSC ("bank/+", null);
        this.sendTrackOSC ("1/select", Integer.valueOf (1));
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new TrackImpl (this.host, this.sender, this.valueChanger, i, this.numSends, this.numScenes));
    }


    /**
     * Sets the number of tracks.
     *
     * @param trackCount The number of tracks
     */
    public void setTrackCount (final int trackCount)
    {
        this.itemCount = trackCount;
    }


    protected void sendTrackOSC (final String command, final Object value)
    {
        this.sender.sendOSC ("/track/" + command + "/", value);
    }


    /**
     * Handles track changes. Notifies all track change observers.
     *
     * @param index The index of the newly de-/selected track
     * @param isSelected True if selected
     */
    public void handleBankTrackSelection (final int index, final boolean isSelected)
    {
        if (index < 0)
            return;
        this.getItem (index).setSelected (isSelected);
        this.notifySelectionObservers (index, isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void selectChildren ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isClipRecording ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final ITrack sel = this.getSelectedItem ();
        if (sel == null)
            return DAWColors.COLOR_OFF;
        final double [] color = sel.getColor ();
        return DAWColors.getColorIndex (color[0], color[1], color[2]);
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.sceneBank;
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        if (position >= 0 && position < this.getItemCount ())
            this.sendTrackOSC ("scrollto/" + position, null);
    }
}