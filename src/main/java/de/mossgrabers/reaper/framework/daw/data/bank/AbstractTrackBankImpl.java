// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IIndexedValueObserver;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.ApplicationImpl;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * An abstract track bank.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractTrackBankImpl extends AbstractPagedBankImpl<TrackImpl, ITrack> implements ITrackBank, IBankPageObserver
{
    private static final String   SELECT_COMMAND = "/select";

    private final ApplicationImpl application;
    private final int             numScenes;
    private final int             numSends;
    private final int             numParams;
    private final ISceneBank      sceneBank;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param application The application
     * @param numTracks The number of tracks of a bank page
     * @param sceneBank The scene bank
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     * @param numParams The number of parameters
     */
    protected AbstractTrackBankImpl (final DataSetupEx dataSetup, final ApplicationImpl application, final int numTracks, final ISceneBank sceneBank, final int numScenes, final int numSends, final int numParams)
    {
        super (dataSetup, numTracks, EmptyTrack.getInstance (numSends));

        this.application = application;

        this.numScenes = numScenes;
        this.numSends = numSends;
        this.numParams = numParams;

        this.sceneBank = sceneBank;
        this.sceneBank.addPageObserver (this);
    }


    /** {@inheritDoc}} */
    @Override
    protected TrackImpl createItem (final int position)
    {
        return new TrackImpl (this.dataSetup, this, position, this.getPageSize (), this.numSends, this.numScenes, this.numParams);
    }


    /** {@inheritDoc} */
    @Override
    public void pageAdjusted ()
    {
        // Callback from scene bank
        this.updateSlotBanks (this.sceneBank.getScrollPosition ());
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        if (this.items.isEmpty ())
            return;

        final boolean state = !this.items.get (0).isRecArm ();
        for (final ITrack track: this.items)
            track.setRecArm (state);
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
        final Optional<ITrack> sel = this.getSelectedItem ();
        if (sel.isEmpty ())
            return DAWColor.COLOR_OFF.name ();
        return DAWColor.getColorID (sel.get ().getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.sceneBank;
    }


    /** {@inheritDoc} */
    @Override
    public void stop (final boolean isAlternative)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IIndexedValueObserver<String> observer)
    {
        for (int index = 0; index < this.getPageSize (); index++)
        {
            final ITrack track = this.getUnpagedItem (index);
            track.addNameObserver (value -> observer.update (track.getIndex (), value));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.scrollTo (position, true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        if (position < 0 || position >= this.getItemCount ())
            return;
        final int pageSize = this.getPageSize ();
        final int pos = adjustPage ? position / pageSize * pageSize : position;
        this.sendTrackOSC (pos + "/scrollto");
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollPageBackwards ()
    {
        super.scrollPageBackwards ();

        // Un-select previous selected track (if any)
        final Optional<ITrack> selectedTrack = this.getSelectedItem ();
        if (selectedTrack.isPresent ())
            this.sendTrackOSC (selectedTrack.get ().getPosition () + SELECT_COMMAND, 0);

        // Select item on new page
        final int selIndex = this.pageSize - 1;
        final int selPos = this.getItem (selIndex).getPosition ();
        this.sendTrackOSC (selPos + SELECT_COMMAND, 1);
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollPageForwards ()
    {
        super.scrollPageForwards ();

        // Deselect previous selected track (if any)
        final Optional<ITrack> selectedTrack = this.getSelectedItem ();
        if (selectedTrack.isPresent ())
            this.sendTrackOSC (selectedTrack.get ().getPosition () + SELECT_COMMAND, 0);

        // Select item on new page
        final int selPos = this.getItem (0).getPosition ();
        this.sendTrackOSC (selPos + SELECT_COMMAND, 1);
    }


    protected void updateSlotBanks (final int slotBankOffset)
    {
        final int trackCount = this.items.size ();
        for (int position = 0; position < trackCount; position++)
            ((SlotBankImpl) this.getUnpagedItem (position).getSlotBank ()).setBankOffset (slotBankOffset);
    }


    /**
     * Check if any of the tracks is soloed.
     *
     * @return True if there is at least one soloed track
     */
    public boolean hasSolo ()
    {
        for (final ITrack element: this.items)
        {
            if (element.isSolo ())
                return true;
        }
        return false;
    }


    /**
     * Check if any of the tracks is muted.
     *
     * @return True if there is at least one muted track
     */
    public boolean hasMute ()
    {
        for (final ITrack element: this.items)
        {
            if (element.isMute ())
                return true;
        }
        return false;
    }


    /**
     * Deactivate all solo states of all tracks.
     */
    public void clearSolo ()
    {
        for (final ITrack element: this.items)
            element.setSolo (false);
    }


    /**
     * Deactivate all mute states of all tracks.
     */
    public void clearMute ()
    {
        for (final ITrack element: this.items)
            element.setMute (false);
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type)
    {
        this.addChannel (type, null);
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type, final String name)
    {
        this.addChannel (type, name, DAWColor.getNextColor ().getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type, final String name, final ColorEx color)
    {
        this.application.addChannel (type, name, color, Collections.emptyList ());
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type, final String name, final List<IDeviceMetadata> devices)
    {
        this.application.addChannel (type, name, DAWColor.getNextColor ().getColor (), devices);
    }


    protected void sendTrackOSC (final String command, final int value)
    {
        this.sender.processIntArg (Processor.TRACK, command, value);
    }


    protected void sendTrackOSC (final String command)
    {
        this.sender.processNoArg (Processor.TRACK, command);
    }
}