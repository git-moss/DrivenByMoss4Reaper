// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data.bank;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IIndexedValueObserver;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.framework.daw.DataSetupEx;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.util.Optional;


/**
 * An abstract track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackBankImpl extends AbstractPagedBankImpl<TrackImpl, ITrack> implements ITrackBank
{
    private static final String SELECT_COMMAND = "/select";

    private final IApplication  application;
    private final ICursorTrack  cursorTrack;
    private int                 numScenes;
    private int                 numSends;
    private ISceneBank          sceneBank;


    /**
     * Constructor.
     *
     * @param dataSetup Some configuration variables
     * @param cursorTrack The cursor track
     * @param application The application
     * @param numTracks The number of tracks of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param numSends The number of sends of a bank page
     */
    protected AbstractTrackBankImpl (final DataSetupEx dataSetup, final ICursorTrack cursorTrack, final IApplication application, final int numTracks, final int numScenes, final int numSends)
    {
        super (dataSetup, numTracks, EmptyTrack.INSTANCE);

        this.application = application;
        this.cursorTrack = cursorTrack;

        this.numScenes = numScenes;
        this.numSends = numSends;

        this.sceneBank = new SceneBankImpl (dataSetup, this, this.numScenes);
    }


    /** {@inheritDoc}} */
    @Override
    protected TrackImpl createItem (final int position)
    {
        return new TrackImpl (this.dataSetup, this, position, this.getPageSize (), this.numSends, this.numScenes);
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
        return DAWColor.getColorIndex (sel.get ().getColor ());
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

        // Unselect previous selected track (if any)
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
        final DAWColor color = DAWColor.getNextColor ();
        this.addChannel (type, name, color.getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void addChannel (final ChannelType type, final String name, final ColorEx color)
    {
        this.addTrack (type);

        if (name == null && color == null)
            return;

        this.host.scheduleTask ( () -> {

            if (!this.cursorTrack.doesExist ())
                return;
            if (name != null)
                this.cursorTrack.setName (name);
            if (color != null)
                this.cursorTrack.setColor (color);

        }, 300);
    }


    /**
     * Adds a new track to this track bank.
     * 
     * @param type The type of the track to add
     */
    protected void addTrack (final ChannelType type)
    {
        switch (type)
        {
            case HYBRID:
            case INSTRUMENT:
                this.application.addInstrumentTrack ();
                break;

            case EFFECT:
                this.application.addEffectTrack ();
                break;

            default:
            case AUDIO:
                this.application.addAudioTrack ();
                break;
        }
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