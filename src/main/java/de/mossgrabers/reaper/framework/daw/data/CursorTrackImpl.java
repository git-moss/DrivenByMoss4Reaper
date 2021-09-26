// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw.data;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.IDeviceMetadata;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.daw.data.empty.EmptySendBank;
import de.mossgrabers.framework.daw.data.empty.EmptySlotBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.observer.IValueObserver;

import java.util.Optional;


/**
 * The master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorTrackImpl implements ICursorTrack
{
    private final IModel model;
    private boolean      isPinned    = false;
    private ITrack       pinnedTrack = null;


    /**
     * Constructor.
     *
     * @param model The model
     */
    public CursorTrackImpl (final IModel model)
    {
        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getCrossfadeParameter ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getCrossfadeParameter () : EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGroup ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isGroup ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.hasParent ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecArm ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isRecArm ();
    }


    /** {@inheritDoc} */
    @Override
    public void setRecArm (final boolean value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setRecArm (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleRecArm ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMonitor ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isMonitor ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setMonitor (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleMonitor ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoMonitor ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isAutoMonitor ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setAutoMonitor (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleAutoMonitor ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldNotes ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.canHoldNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldAudioData ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.canHoldAudioData ();
    }


    /** {@inheritDoc} */
    @Override
    public ISlotBank getSlotBank ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getSlotBank () : EmptySlotBank.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public void createClip (final int slotIndex, final int lengthInBeats)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.createClip (slotIndex, lengthInBeats);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isPlaying ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.returnToArrangement ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecordQuantizationNoteLength ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isRecordQuantizationNoteLength ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecordQuantizationNoteLength ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleRecordQuantizationNoteLength ();
    }


    /** {@inheritDoc} */
    @Override
    public RecordQuantization getRecordQuantizationGrid ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getRecordQuantizationGrid () : RecordQuantization.RES_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void setRecordQuantizationGrid (final RecordQuantization recordQuantization)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setRecordQuantizationGrid (recordQuantization);
    }


    /** {@inheritDoc} */
    @Override
    public void addEqualizerDevice ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.addEqualizerDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isActivated ();
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getType () : ChannelType.UNKNOWN;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getVolumeParameter ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVolumeParameter () : EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVolumeStr () : "";
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr (final int limit)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVolumeStr (limit) : "";
    }


    /** {@inheritDoc} */
    @Override
    public int getVolume ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVolume () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int control)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.changeVolume (control);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final int value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.resetVolume ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.touchVolume (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final boolean indicate)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setVolumeIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedVolume ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getModulatedVolume () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getPanParameter ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getPanParameter () : EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getPanStr () : "";
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr (final int limit)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getPanStr (limit) : "";
    }


    /** {@inheritDoc} */
    @Override
    public int getPan ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getPan () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int control)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.changePan (control);
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final int value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setPan (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.resetPan ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.touchPan (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final boolean indicate)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setPanIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getModulatedPan () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setIsActivated (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleIsActivated ();
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getColor () : ColorEx.BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setColor (color);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isMute ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setMute (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleMute ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setSolo (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMutedBySolo ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.isMutedBySolo ();
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVu () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVuLeft () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVuRight () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuPeakLeft ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVuPeakLeft () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuPeakRight ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getVuPeakRight () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumDevice ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.hasDrumDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.remove ();
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.duplicate ();
    }


    /** {@inheritDoc} */
    @Override
    public ISendBank getSendBank ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getSendBank () : EmptySendBank.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public void enter ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.enter ();
    }


    /** {@inheritDoc} */
    @Override
    public void addColorObserver (final IValueObserver<ColorEx> observer)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.addColorObserver (observer);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null && selectedTrack.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getIndex () : -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getPosition () : 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        if (this.isPinned)
            return true;
        final ITrack selectedTrack = this.getSelectedTrack ();
        return selectedTrack != null && selectedTrack.isSelected ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setSelected (isSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.select ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getName () : "";
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        return selectedTrack != null ? selectedTrack.getName (limit) : "";
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.select ();
    }


    /** {@inheritDoc} */
    @Override
    public void setName (final String name)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.setName (name);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPinned ()
    {
        return this.isPinned;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        this.setPinned (!this.isPinned);
    }


    /** {@inheritDoc} */
    @Override
    public void setPinned (final boolean isPinned)
    {
        if (isPinned)
        {
            this.pinnedTrack = this.getSelectedTrack ();
            if (this.pinnedTrack == null || !this.pinnedTrack.doesExist ())
            {
                this.setPinned (false);
                return;
            }
        }
        else
            this.pinnedTrack = null;

        this.isPinned = isPinned;
        this.sendPositionedItemOSC ("pin", this.pinnedTrack == null ? -1 : this.pinnedTrack.getPosition ());
    }


    /** {@inheritDoc} */
    @Override
    public void addDevice (final IDeviceMetadata metadata)
    {
        final ITrack selectedTrack = this.getPinnedOrSelectedTrack ();
        if (selectedTrack != null)
            ((ChannelImpl) selectedTrack).addDevice (metadata);
    }


    private ITrack getPinnedOrSelectedTrack ()
    {
        if (this.isPinned)
            return this.pinnedTrack;
        return this.getSelectedTrack ();
    }


    private ITrack getSelectedTrack ()
    {
        // Is a "normal" track selected?
        ITrackBank tb = this.model.getTrackBank ();
        Optional<ITrack> sel = tb.getSelectedItem ();
        if (sel.isPresent ())
            return sel.get ();

        // Is an effect track selected?
        tb = this.model.getEffectTrackBank ();
        if (tb != null)
        {
            sel = tb.getSelectedItem ();
            if (sel.isPresent ())
                return sel.get ();
        }

        // Is the master track selected?
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        return masterTrack.isSelected () ? masterTrack : null;
    }


    /**
     * Send an item command with a position.
     *
     * @param command The command
     * @param value The value
     */
    public void sendPositionedItemOSC (final String command, final double value)
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack != null)
            ((ChannelImpl) selectedTrack).sendPositionedItemOSC (command, value);
    }


    private void sendPositionedItemOSC (final String command, final int value)
    {
        final ITrack selectedTrack = this.getSelectedTrack ();
        if (selectedTrack != null)
            ((ChannelImpl) selectedTrack).sendPositionedItemOSC (command, value);
    }


    /**
     * Get the pinned track.
     *
     * @return The pinned track or null if there is no pinned track
     */
    public ITrack getPinnedTrack ()
    {
        return this.pinnedTrack;
    }
}