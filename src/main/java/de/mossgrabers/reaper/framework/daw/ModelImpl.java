// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.AbstractModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.data.CursorDeviceImpl;
import de.mossgrabers.reaper.framework.daw.data.CursorTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.DrumDeviceImpl;
import de.mossgrabers.reaper.framework.daw.data.EqualizerDeviceImpl;
import de.mossgrabers.reaper.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.SlotImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.MarkerBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.SlotBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.TrackBankImpl;
import de.mossgrabers.reaper.framework.daw.data.bank.UserParameterBankImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelImpl extends AbstractModel
{
    private final DataSetupEx              dataSetup;
    private final List<ITrackBank>         trackBanks = new ArrayList<> ();
    private final Map<Integer, ISceneBank> sceneBanks = new HashMap<> (1);


    /**
     * Constructor.
     *
     * @param configuration The configuration
     * @param modelSetup The configuration parameters for the model
     * @param dataSetup Some setup variables
     * @param scales The scales object
     * @param iniFiles The INI configuration files
     */
    public ModelImpl (final Configuration configuration, final ModelSetup modelSetup, final DataSetupEx dataSetup, final Scales scales, final IniFiles iniFiles)
    {
        super (modelSetup, dataSetup, scales);

        this.dataSetup = dataSetup;

        this.application = new ApplicationImpl (dataSetup);
        this.arranger = new ArrangerImpl (dataSetup);
        this.mixer = new MixerImpl (dataSetup);
        this.project = new ProjectImpl (dataSetup, this, configuration);
        this.transport = new TransportImpl (dataSetup, this, iniFiles);
        this.groove = new GrooveImpl (dataSetup);
        this.markerBank = new MarkerBankImpl (dataSetup, modelSetup.getNumMarkers ());
        this.cursorTrack = new CursorTrackImpl (this);

        dataSetup.setCursorTrack ((CursorTrackImpl) this.cursorTrack);
        dataSetup.setTransport (this.transport);

        final int numTracks = modelSetup.getNumTracks ();
        final int numSends = modelSetup.getNumSends ();
        final int numScenes = modelSetup.getNumScenes ();

        //////////////////////////////////////////////////////////////////////////////
        // Create devices

        final int numDevicesInBank = modelSetup.getNumDevicesInBank ();
        final int numParams = modelSetup.getNumParams ();
        final int numDeviceLayers = modelSetup.getNumDeviceLayers ();
        final int numDrumPadLayers = modelSetup.getNumDrumPadLayers ();

        // Cursor device
        this.cursorDevice = new CursorDeviceImpl (dataSetup, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

        // Drum Machine
        final List<IDrumDevice> drumDevices = new ArrayList<> ();
        if (modelSetup.wantsDrumDevice ())
        {
            this.drumDevice = new DrumDeviceImpl (dataSetup, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
            drumDevices.add (this.drumDevice);

            // Drum Machine 64 pads
            if (modelSetup.wantsDrum64Device ())
            {
                this.drumDevice64 = new DrumDeviceImpl (dataSetup, 0, 0, 0, 64, 64);
                drumDevices.add (this.drumDevice64);
            }
        }

        for (final DeviceID deviceID: modelSetup.getDeviceIDs ())
        {
            switch (deviceID)
            {
                case FIRST_INSTRUMENT, NI_KOMPLETE:
                    final ISpecificDevice specificDevice = new CursorDeviceImpl (dataSetup, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
                    this.specificDevices.put (DeviceID.FIRST_INSTRUMENT, specificDevice);
                    if (deviceID == DeviceID.NI_KOMPLETE)
                        this.specificDevices.put (deviceID, new KompleteDevice (specificDevice));
                    break;

                case EQ:
                    this.specificDevices.put (deviceID, new EqualizerDeviceImpl (dataSetup));
                    break;

                default:
                    // Not used
                    break;
            }
        }

        //////////////////////////////////////////////////////////////////////////////
        // Create track banks

        final TrackBankImpl trackBankImpl = new TrackBankImpl (dataSetup, (ApplicationImpl) this.application, drumDevices, numTracks, numScenes, numSends, modelSetup.hasFlatTrackList (), modelSetup.hasFullFlatTrackList ());
        this.trackBank = trackBankImpl;
        this.masterTrack = new MasterTrackImpl (dataSetup, trackBankImpl, numSends);
        trackBankImpl.setMasterTrack ((TrackImpl) this.masterTrack);
        this.trackBanks.add (this.trackBank);
        this.effectTrackBank = null;

        this.userParameterBank = new UserParameterBankImpl (dataSetup, modelSetup.getNumUserPageSize (), this);

        final int numResults = modelSetup.getNumResults ();
        if (numResults > 0)
            this.browser = new BrowserImpl (dataSetup, this.cursorDevice, modelSetup.getNumFilterColumnEntries (), numResults);

        this.currentTrackBank = this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank createSceneBank (final int numScenes)
    {
        return this.sceneBanks.computeIfAbsent (Integer.valueOf (numScenes), key -> {
            final TrackBankImpl tb = new TrackBankImpl (this.dataSetup, (ApplicationImpl) this.application, 1, numScenes, this.modelSetup.getNumSends (), true, false);
            this.trackBanks.add (tb);
            return tb.getSceneBank ();
        });
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getNoteClip (final int cols, final int rows)
    {
        synchronized (this.cursorClips)
        {
            return this.cursorClips.computeIfAbsent (cols + "-" + rows, k -> new CursorClipImpl (this.dataSetup, cols, rows));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void createNoteClip (final ITrack track, final ISlot slot, final int lengthInBeats, final boolean overdub)
    {
        track.createClip (slot.getIndex (), lengthInBeats);
        slot.select ();
        if (overdub)
            this.transport.startRecording ();
    }


    /** {@inheritDoc} */
    @Override
    public void recordNoteClip (final ITrack track, final ISlot slot)
    {
        ((TrackImpl) track).recordClip ();
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getCursorClip ()
    {
        if (this.cursorClips.isEmpty ())
            throw new FrameworkException ("No cursor clip created!");
        return this.cursorClips.values ().iterator ().next ();
    }


    /** {@inheritDoc} */
    @Override
    public void ensureClip ()
    {
        this.getNoteClip (0, 0);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorDeviceOnMasterTrack ()
    {
        return this.getMasterTrack ().isSelected ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canConvertClip ()
    {
        return true;
    }


    /**
     * Store all notes of the cursor clip.
     *
     * @param notes The notes to store
     */
    public void setCursorClipNotes (final List<Note> notes)
    {
        synchronized (this.cursorClips)
        {
            for (final INoteClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setNotes (notes);
        }
    }


    /**
     * Set the exists state for all cursor clip objects.
     *
     * @param exists The exists state
     */
    public void setCursorClipExists (final boolean exists)
    {
        synchronized (this.cursorClips)
        {
            for (final INoteClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setExistsValue (exists);
        }
    }


    /**
     * Set the play start for all cursor clip objects.
     *
     * @param start The start
     */
    public void setCursorClipPlayStart (final double start)
    {
        synchronized (this.cursorClips)
        {
            for (final INoteClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setPlayStartIntern (start);
        }
    }


    /**
     * Set the play end for all cursor clip objects.
     *
     * @param end The end
     */
    public void setCursorClipPlayEnd (final double end)
    {
        synchronized (this.cursorClips)
        {
            for (final INoteClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setPlayEndIntern (end);
        }
    }


    /**
     * Set the play position for all cursor clip objects.
     *
     * @param playPosition The play position
     */
    public void setCursorClipPlayPosition (final double playPosition)
    {
        synchronized (this.cursorClips)
        {
            for (final INoteClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setPlayPosition (playPosition);
        }
    }


    /**
     * Set clip color value.
     *
     * @param color Array with 3 elements: red, green, blue (0..1)
     */
    public void setCursorClipColorValue (final double [] color)
    {
        synchronized (this.cursorClips)
        {
            for (final INoteClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setColorValue (color);
        }
    }


    /**
     * Set clip loop source value.
     *
     * @param isLoopEnabled True if loop source is enabled
     */
    public void setCursorClipLoopIsEnabled (final boolean isLoopEnabled)
    {
        synchronized (this.cursorClips)
        {
            for (final INoteClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setLoopEnabledState (isLoopEnabled);
        }
    }


    /**
     * Set all clips.
     *
     * @param clipsStr The encoded clips formatted like
     *            "trackindex1:clipindex1:name1:position1:color1;..."
     */
    public void setClips (final String clipsStr)
    {
        if (clipsStr == null)
            return;

        final String [] clipParts = clipsStr.trim ().split (";");
        int pos = 0;
        int maxSlotCount = 0;
        final TrackBankImpl tb = (TrackBankImpl) this.trackBank;
        while (pos < clipParts.length)
        {
            final int trackIndex = Integer.parseInt (clipParts[pos++]);
            final TrackImpl track = tb.getUnpagedItem (trackIndex);
            final SlotBankImpl slotBank = (SlotBankImpl) track.getSlotBank ();
            slotBank.setTrack (trackIndex);

            final int numClips = Integer.parseInt (clipParts[pos++]);
            if (numClips > maxSlotCount)
                maxSlotCount = numClips;
            slotBank.setItemCount (numClips);

            for (int i = 0; i < numClips; i++)
            {
                final String name = clipParts[pos++];
                final boolean isSelected = Integer.parseInt (clipParts[pos++]) > 0;
                final Optional<double []> color = this.parseColor (clipParts[pos++]);

                final SlotImpl slot = slotBank.getUnpagedItem (i);
                slot.setPosition (i);
                slot.setSelected (isSelected);
                slot.setName (name);
                if (color.isPresent ())
                    slot.setColor (new ColorEx (color.get ()));
                slot.setExists (true);
                slot.setHasContent (true);
            }
        }

        // Set all scene banks to the same size
        final int size = tb.getItemCount ();
        for (int i = 0; i < size; i++)
        {
            final TrackImpl track = tb.getUnpagedItem (i);
            final SlotBankImpl slotBank = (SlotBankImpl) track.getSlotBank ();

            final int itemCount = slotBank.getItemCount ();
            slotBank.setMaxSlotCount (maxSlotCount);

            for (int slotIndex = itemCount; slotIndex < maxSlotCount; slotIndex++)
            {
                final SlotImpl slot = slotBank.getUnpagedItem (slotIndex);
                slot.setExists (true);
                slot.setHasContent (false);
            }
        }
    }


    /**
     * Parse three double values separated by a space character.
     *
     * @param value The string to parse
     * @return The three double values
     */
    public Optional<double []> parseColor (final String value)
    {
        final String [] values = value.split (" ");
        if (values.length != 3)
        {
            this.host.error ("Color: Wrong number of arguments: " + values.length);
            final StringBuilder str = new StringBuilder ();
            for (final String value2: values)
                str.append (value2).append (':');
            this.host.error (str.toString ());
            return Optional.empty ();
        }
        final double d1 = Double.parseDouble (values[0]);
        if (d1 < 0)
            return Optional.empty ();
        return Optional.of (new double []
        {
            d1 / 255.0,
            Double.parseDouble (values[1]) / 255.0,
            Double.parseDouble (values[2]) / 255.0
        });
    }


    /**
     * Get all track banks.
     *
     * @return The track banks
     */
    public List<ITrackBank> getTrackBanks ()
    {
        return this.trackBanks;
    }
}