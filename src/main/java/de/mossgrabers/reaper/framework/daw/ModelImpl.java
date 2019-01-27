// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractModel;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.reaper.framework.daw.data.SlotImpl;
import de.mossgrabers.reaper.framework.daw.data.TrackImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelImpl extends AbstractModel
{
    private final MessageSender    sender;
    private final List<ITrackBank> trackBanks = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param iniFiles The INI configuration files
     * @param sender The OSC sender
     * @param host The DAW host
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales object
     * @param modelSetup The configuration parameters for the model
     */
    public ModelImpl (final IniFiles iniFiles, final MessageSender sender, final IHost host, final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final ModelSetup modelSetup)
    {
        super (colorManager, valueChanger, scales, modelSetup);

        this.sender = sender;
        this.host = host;

        final int numTracks = modelSetup.getNumTracks ();
        final int numScenes = modelSetup.getNumScenes ();
        final int numSends = modelSetup.getNumSends ();
        this.masterTrack = new MasterTrackImpl (host, sender, valueChanger, numSends);
        this.trackBank = new TrackBankImpl (host, sender, valueChanger, numTracks, numScenes, numSends, modelSetup.hasFlatTrackList (), modelSetup.hasFullFlatTrackList (), this.masterTrack);
        this.trackBanks.add (this.trackBank);
        this.effectTrackBank = null;

        final int numDevicesInBank = modelSetup.getNumDevicesInBank ();
        final int numParams = modelSetup.getNumParams ();
        final int numDeviceLayers = modelSetup.getNumDeviceLayers ();
        final int numDrumPadLayers = modelSetup.getNumDrumPadLayers ();
        this.instrumentDevice = new CursorDeviceImpl (host, sender, valueChanger, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        this.cursorDevice = new CursorDeviceImpl (host, sender, valueChanger, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        if (numDrumPadLayers > 0)
            this.drumDevice64 = new CursorDeviceImpl (host, sender, valueChanger, 0, 0, 0, 64, 64);

        final int numResults = modelSetup.getNumResults ();
        if (numResults > 0)
            this.browser = new BrowserImpl (sender, this.cursorDevice, modelSetup.getNumFilterColumnEntries (), numResults);

        this.application = new ApplicationImpl (host, sender);
        this.arranger = new ArrangerImpl ();
        this.mixer = new MixerImpl (host, sender);
        this.project = new ProjectImpl (host, sender);
        this.transport = new TransportImpl (host, sender, valueChanger, this.trackBank, iniFiles);

        this.groove = new GrooveImpl (host, sender, valueChanger, iniFiles);
        this.markerBank = new MarkerBankImpl (host, sender, valueChanger, modelSetup.getNumMarkers ());

        this.currentTrackBank = this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank createSceneViewTrackBank (final int numTracks, final int numScenes)
    {
        final TrackBankImpl tb = new TrackBankImpl (this.host, this.sender, this.valueChanger, numTracks, numScenes, this.modelSetup.getNumSends (), true, false, null);
        this.trackBanks.add (tb);
        return tb;
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getNoteClip (final int cols, final int rows)
    {
        synchronized (this.cursorClips)
        {
            return (INoteClip) this.cursorClips.computeIfAbsent (cols + "-" + rows, k -> new CursorClipImpl (this.host, this.sender, this.valueChanger, cols, rows));
        }
    }


    /** {@inheritDoc} */
    @Override
    public IClip getClip ()
    {
        if (this.cursorClips.isEmpty ())
            throw new RuntimeException ("No cursor clip created!");
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
    public boolean isCursorTrackPinned ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCursorTrackPinned ()
    {
        // Not supported
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
     * @param notesStr Formatted like start1:end1:pitch1:velocity1;...;startN:endN:pitchN:velocityN;
     */
    public void setCursorClipNotes (final String notesStr)
    {
        final List<NoteImpl> notes = new ArrayList<> ();
        if (notesStr != null)
        {
            for (final String part: notesStr.trim ().split (";"))
            {
                final String [] noteParts = part.split (":");
                final double start = Double.parseDouble (noteParts[0]);
                final double end = Double.parseDouble (noteParts[1]);
                final int pitch = Integer.parseInt (noteParts[2]);
                final int velocity = Integer.parseInt (noteParts[3]);
                notes.add (new NoteImpl (start, end, pitch, velocity));
            }
        }

        synchronized (this.cursorClips)
        {
            for (final IClip clip: this.cursorClips.values ())
                ((CursorClipImpl) clip).setNotes (notes);
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
            for (final IClip clip: this.cursorClips.values ())
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
            for (final IClip clip: this.cursorClips.values ())
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
            for (final IClip clip: this.cursorClips.values ())
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
            for (final IClip clip: this.cursorClips.values ())
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
            for (final IClip clip: this.cursorClips.values ())
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
            final TrackImpl track = tb.getTrack (trackIndex);
            final SlotBankImpl slotBank = (SlotBankImpl) track.getSlotBank ();
            slotBank.setTrack (trackIndex);

            final int numClips = Integer.parseInt (clipParts[pos++]);
            if (numClips > maxSlotCount)
                maxSlotCount = numClips;
            slotBank.setSlotCount (numClips);

            for (int i = 0; i < numClips; i++)
            {
                final String name = clipParts[pos++];
                final boolean isSelected = Integer.parseInt (clipParts[pos++]) > 0;
                final double [] color = this.parseColor (clipParts[pos++]);

                final SlotImpl slot = slotBank.getSlot (i);
                slot.setPosition (i);
                slot.setSelected (isSelected);
                slot.setName (name);
                if (color != null)
                    slot.setColor (color[0], color[1], color[2]);
                slot.setExists (true);
            }
        }

        // Set all scene banks to the same size
        final int size = tb.getItemCount ();
        for (int i = 0; i < size; i++)
        {
            final TrackImpl track = tb.getTrack (i);
            final SlotBankImpl slotBank = (SlotBankImpl) track.getSlotBank ();
            slotBank.setMaxSlotCount (maxSlotCount);
        }
    }


    /**
     * Parse three double values separated by a space character.
     *
     * @param value The string to parse
     * @return The three double values
     */
    public double [] parseColor (final String value)
    {
        final String [] values = value.split (" ");
        if (values.length != 3)
        {
            this.host.error ("Color: Wrong number of arguments: " + values.length);
            final StringBuilder str = new StringBuilder ();
            for (final String value2: values)
                str.append (value2).append (':');
            this.host.error (str.toString ());
            return null;
        }
        double d1 = Double.parseDouble (values[0]);
        if (d1 < 0)
            return null;
        return new double []
        {
            d1 / 255.0,
            Double.parseDouble (values[1]) / 255.0,
            Double.parseDouble (values[2]) / 255.0
        };
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