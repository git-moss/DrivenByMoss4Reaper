// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.reaper.framework.daw.data.DrumPadImpl;
import de.mossgrabers.reaper.framework.daw.data.ParameterImpl;
import de.mossgrabers.transformator.communication.MessageSender;


/**
 * Proxy to the Reaper Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorDeviceImpl extends BaseImpl implements ICursorDevice
{
    private int           numParams;
    private int           numDevicesInBank;
    private int           numDeviceLayers;
    private int           numDrumPadLayers;

    private boolean       exists                = false;
    private boolean       isEnabled             = false;
    private String        name;
    private int           position;
    private boolean       isWindowOpen;
    private boolean       isExpanded;
    private String []     siblings;
    private IParameter [] fxparams;
    private IDrumPad []   drumPadLayers;

    private int           selectedDevice        = 0;
    private int           selectedParameterPage = 0;
    private int           selectedParameterBank = 0;
    private int           deviceCount           = 0;

    private String []     parameterPageNames    = new String [0];


    /**
     * Constructor.
     *
     * @param sender The OSC sender
     * @param host The host
     * @param valueChanger The value changer
     * @param numSends The number of sends
     * @param numParams The number of parameters
     * @param numDevicesInBank The number of devices
     * @param numDeviceLayers The number of layers
     * @param numDrumPadLayers The number of drum pad layers
     */
    public CursorDeviceImpl (final MessageSender sender, final IHost host, final IValueChanger valueChanger, final int numSends, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (sender, host);

        this.numParams = numParams >= 0 ? numParams : 8;
        this.numDevicesInBank = numDevicesInBank >= 0 ? numDevicesInBank : 8;
        this.numDeviceLayers = numDeviceLayers >= 0 ? numDeviceLayers : 8;
        this.numDrumPadLayers = numDrumPadLayers >= 0 ? numDrumPadLayers : 16;

        if (this.numDevicesInBank > 0)
        {
            this.siblings = new String [this.numDevicesInBank];
            for (int i = 0; i < this.numDevicesInBank; i++)
                this.siblings[i] = "";
        }

        if (this.numParams > 0)
        {
            this.fxparams = new IParameter [this.numParams];
            for (int i = 0; i < this.numParams; i++)
                this.fxparams[i] = new ParameterImpl (sender, valueChanger, i);
        }

        if (this.numDrumPadLayers > 0)
        {
            this.drumPadLayers = new IDrumPad [this.numDrumPadLayers];
            for (int i = 0; i < this.numDrumPadLayers; i++)
                this.drumPadLayers[i] = new DrumPadImpl (host, sender, valueChanger, i, numSends);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void browseToReplaceDevice ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertBeforeDevice ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAfterDevice ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void selectChannel ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.exists;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return this.isEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name == null ? "" : this.name;
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        String n = this.getName ();
        if (n.length () < limit)
            return n;

        final String [] split = n.split (": ");
        if (split.length == 2)
            n = split[1];

        if (n.length () < limit)
            return n;

        return n.substring (0, limit);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlugin ()
    {
        // Not supported
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionInChain ()
    {
        return this.position;
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionInBank ()
    {
        return this.selectedDevice;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectPreviousFX ()
    {
        return this.selectedDevice > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectNextFX ()
    {
        return this.position < this.deviceCount - 1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isExpanded ()
    {
        return this.isExpanded;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isParameterPageSectionVisible ()
    {
        // Not supported
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWindowOpen ()
    {
        return this.isWindowOpen;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isNested ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumPads ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasLayers ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSlots ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPinned ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeParameter (final int index, final int control)
    {
        this.getFXParam (index).changeValue (control);
    }


    /** {@inheritDoc} */
    @Override
    public void setParameter (final int index, final int value)
    {
        this.getFXParam (index).setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetParameter (final int index)
    {
        this.getFXParam (index).resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void indicateParameter (final int index, final boolean indicate)
    {
        this.getFXParam (index).setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void touchParameter (final int index, final boolean indicate)
    {
        this.getFXParam (index).touchValue (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public void previousParameterPage ()
    {
        // To support displaying the newly selected device
        if (this.selectedParameterPage > 0)
        {
            this.selectedParameterPage--;
            this.sender.sendOSC ("/device/param/-", null);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void nextParameterPage ()
    {
        // To support displaying the newly selected device
        if (this.selectedParameterPage < this.parameterPageNames.length - 1)
        {
            this.selectedParameterPage++;
            this.sender.sendOSC ("/device/param/+", null);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setSelectedParameterPage (final int index)
    {
        this.selectedParameterPage = index;
        this.selectedParameterBank = this.selectedParameterPage / this.numDevicesInBank;
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedParameterPage ()
    {
        return this.selectedParameterPage;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousParameterPage ()
    {
        return this.selectedParameterPage > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextParameterPage ()
    {
        return this.selectedParameterPage < this.parameterPageNames.length - 1;
    }


    /** {@inheritDoc} */
    @Override
    public String [] getParameterPageNames ()
    {
        return this.parameterPageNames;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedParameterPageName ()
    {
        final int sel = this.getSelectedParameterPage ();
        return sel >= 0 && sel < this.parameterPageNames.length ? this.parameterPageNames[sel] : "";
    }


    /** {@inheritDoc} */
    @Override
    public void setSelectedParameterPageInBank (final int index)
    {
        this.selectedParameterPage = this.selectedParameterBank * 8 + index;
        this.sender.sendOSC ("/device/param/bank/selected", Integer.valueOf (this.selectedParameterPage + 1));
    }


    /** {@inheritDoc} */
    @Override
    public void previousParameterPageBank ()
    {
        this.sender.sendOSC ("/device/param/bank/-", null);
    }


    /** {@inheritDoc} */
    @Override
    public void nextParameterPageBank ()
    {
        this.sender.sendOSC ("/device/param/bank/+", null);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabledState ()
    {
        this.sender.sendOSC ("/device/bypass", Integer.valueOf (this.isEnabled ? 1 : 0));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWindowOpen ()
    {
        this.sender.sendOSC ("/device/window", Integer.valueOf (this.isWindowOpen ? 0 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public void selectPrevious ()
    {
        // To support displaying the newly selected device
        if (this.selectedDevice > 0)
            this.name = this.siblings[this.selectedDevice - 1];

        this.sender.sendOSC ("/device/-", null);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNext ()
    {
        // To support displaying the newly selected device
        if (this.selectedDevice < this.numDevicesInBank - 1)
            this.name = this.siblings[this.selectedDevice + 1];

        this.sender.sendOSC ("/device/+", null);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesSiblingExist (final int index)
    {
        return !this.siblings[index].isEmpty ();
    }


    /** {@inheritDoc} */
    @Override
    public String getSiblingDeviceName (final int index)
    {
        return this.siblings[index];
    }


    /** {@inheritDoc} */
    @Override
    public String getSiblingDeviceName (final int index, final int limit)
    {
        final String sibling = this.siblings[index];
        return sibling != null && sibling.length () >= limit ? sibling.substring (0, limit) : sibling;
    }


    /** {@inheritDoc} */
    @Override
    public void selectSibling (final int index)
    {
        this.selectedDevice = index;
        this.sender.sendOSC ("/device/selected", Integer.valueOf (index + 1));
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousBank ()
    {
        this.sender.sendOSC ("/device/page/-", null);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextBank ()
    {
        this.sender.sendOSC ("/device/page/+", null);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSelectedDevice ()
    {
        return this.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getFXParam (final int index)
    {
        return this.fxparams[index];
    }


    /** {@inheritDoc} */
    @Override
    public void toggleExpanded ()
    {
        this.isExpanded = !this.isExpanded;
        this.sender.sendOSC ("/device/expand", Integer.valueOf (this.isExpanded ? 1 : 0));
    }


    /** {@inheritDoc} */
    @Override
    public void toggleParameterPageSectionVisible ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getLayerOrDrumPad (final int index)
    {
        return this.hasDrumPads () ? this.getDrumPad (index) : this.getLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getSelectedLayerOrDrumPad ()
    {
        return this.hasDrumPads () ? this.getSelectedDrumPad () : this.getSelectedLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.selectDrumPad (index);
        else
            this.selectLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayerOrDrumPad ()
    {
        if (this.hasDrumPads ())
            this.previousDrumPad ();
        else
            this.previousLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayerOrDrumPad ()
    {
        if (this.hasDrumPads ())
            this.nextDrumPad ();
        else
            this.nextLayer ();
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayerOrDrumPadBank ()
    {
        if (this.hasDrumPads ())
            this.previousDrumPadBank ();
        else
            this.previousLayerBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayerOrDrumPadBank ()
    {
        if (this.hasDrumPads ())
            this.nextDrumPadBank ();
        else
            this.nextLayerBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void enterLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.enterDrumPad (index);
        else
            this.enterLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public void selectFirstDeviceInLayerOrDrumPad (final int index)
    {
        if (this.hasDrumPads ())
            this.selectFirstDeviceInDrumPad (index);
        else
            this.selectFirstDeviceInLayer (index);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersOrDrumPadsUp ()
    {
        return this.hasDrumPads () ? this.canScrollDrumPadsUp () : this.canScrollLayersUp ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersOrDrumPadsDown ()
    {
        return this.hasDrumPads () ? this.canScrollDrumPadsDown () : this.canScrollLayersDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersOrDrumPadsPageUp ()
    {
        if (this.hasDrumPads ())
            this.scrollDrumPadsPageUp ();
        else
            this.scrollLayersPageUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersOrDrumPadsPageDown ()
    {
        if (this.hasDrumPads ())
            this.scrollDrumPadsPageDown ();
        else
            this.scrollLayersPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadColor (final int index, final double red, final double green, final double blue)
    {
        if (this.hasDrumPads ())
            this.setDrumPadColor (index, red, green, blue);
        else
            this.setLayerColor (index, red, green, blue);
    }


    /** {@inheritDoc} */
    @Override
    public String getLayerOrDrumPadColorEntry (final int index)
    {
        return DAWColors.getColorIndex (this.getLayerOrDrumPad (index).getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerOrDrumPadVolume (final int index, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadVolume (index, control);
        else
            this.changeLayerVolume (index, control);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadVolume (final int index, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadVolume (index, value);
        else
            this.setLayerVolume (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerOrDrumPadVolume (final int index)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadVolume (index);
        else
            this.resetLayerVolume (index);
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerOrDrumPadVolume (final int index, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadVolume (index, isBeingTouched);
        else
            this.touchLayerVolume (index, isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerOrDrumPadPan (final int index, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadPan (index, control);
        else
            this.changeLayerPan (index, control);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadPan (final int index, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadPan (index, value);
        else
            this.setLayerPan (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerOrDrumPadPan (final int index)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadPan (index);
        else
            this.resetLayerPan (index);
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerOrDrumPadPan (final int index, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadPan (index, isBeingTouched);
        else
            this.touchLayerPan (index, isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerOrDrumPadSend (final int index, final int send, final int control)
    {
        if (this.hasDrumPads ())
            this.changeDrumPadSend (index, send, control);
        else
            this.changeLayerSend (index, send, control);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadSend (final int index, final int send, final int value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadSend (index, send, value);
        else
            this.setLayerSend (index, send, value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerOrDrumPadSend (final int index, final int send)
    {
        if (this.hasDrumPads ())
            this.resetDrumPadSend (index, send);
        else
            this.resetLayerSend (index, send);
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerOrDrumPadSend (final int index, final int send, final boolean isBeingTouched)
    {
        if (this.hasDrumPads ())
            this.touchDrumPadSend (index, send, isBeingTouched);
        else
            this.touchLayerSend (index, send, isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerOrDrumPadIsActivated (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadIsActivated (index);
        else
            this.toggleLayerIsActivated (index);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerOrDrumPadMute (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadMute (index);
        else
            this.toggleLayerMute (index);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadMute (final int index, final boolean value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadMute (index, value);
        else
            this.setLayerMute (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerOrDrumPadSolo (final int index)
    {
        if (this.hasDrumPads ())
            this.toggleDrumPadSolo (index);
        else
            this.toggleLayerSolo (index);
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerOrDrumPadSolo (final int index, final boolean value)
    {
        if (this.hasDrumPads ())
            this.setDrumPadSolo (index, value);
        else
            this.setLayerSolo (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasZeroLayers ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getLayer (final int index)
    {
        // Not supported
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getSelectedLayer ()
    {
        // Not supported
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void selectLayer (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayer ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayer ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void previousLayerBank ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void nextLayerBank ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void enterLayer (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void selectFirstDeviceInLayer (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersUp ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollLayersDown ()
    {
        // Not supported
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersPageUp ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollLayersPageDown ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerColor (final int index, final double red, final double green, final double blue)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerVolume (final int index, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerVolume (final int index, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerVolume (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerVolume (final int index, final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerPan (final int index, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerPan (final int index, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerPan (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerPan (final int index, final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeLayerSend (final int index, final int sendIndex, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerSend (final int index, final int sendIndex, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetLayerSend (final int index, final int sendIndex)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchLayerSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerIsActivated (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerMute (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerMute (final int index, final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLayerSolo (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setLayerSolo (final int index, final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public IDrumPad getDrumPad (final int index)
    {
        return this.drumPadLayers[index];
    }


    /** {@inheritDoc} */
    @Override
    public IChannel getSelectedDrumPad ()
    {
        // Not supported
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void selectDrumPad (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void previousDrumPad ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void nextDrumPad ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void previousDrumPadBank ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void nextDrumPadBank ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void enterDrumPad (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void selectFirstDeviceInDrumPad (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollDrumPadsUp ()
    {
        return this.canScrollLayersUp ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollDrumPadsDown ()
    {
        return this.canScrollLayersDown ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsPageUp ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsPageDown ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsUp ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void scrollDrumPadsDown ()
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadColor (final int index, final double red, final double green, final double blue)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeDrumPadVolume (final int index, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadVolume (final int index, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetDrumPadVolume (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchDrumPadVolume (final int index, final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeDrumPadPan (final int index, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadPan (final int index, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetDrumPadPan (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchDrumPadPan (final int index, final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void changeDrumPadSend (final int index, final int sendIndex, final int control)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadSend (final int index, final int sendIndex, final int value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void resetDrumPadSend (final int index, final int sendIndex)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void touchDrumPadSend (final int index, final int sendIndex, final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDrumPadIsActivated (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDrumPadMute (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadMute (final int index, final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void toggleDrumPadSolo (final int index)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public void setDrumPadSolo (final int index, final boolean value)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getNumLayers ()
    {
        return this.numDeviceLayers;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumParameters ()
    {
        return this.numParams;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumDevices ()
    {
        return this.numDevicesInBank;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumDrumPads ()
    {
        return this.numDrumPadLayers;
    }


    /**
     * Set the position (index) of the device on the track.
     *
     * @param position The index
     */
    public void setPosition (final int position)
    {
        this.position = position;
        this.selectedDevice = position % this.getNumDevices ();
    }


    /**
     * Set if there is an existing device selected.
     *
     * @param exists True if exists
     */
    public void setExists (final boolean exists)
    {
        this.exists = exists;
    }


    /**
     * Set the name of the device.
     *
     * @param name The name
     */
    public void setName (final String name)
    {
        this.name = name;
    }


    /**
     * Set if the device is enabled.
     *
     * @param isEnabled The enabled state
     */
    public void setEnabled (final boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }


    /**
     * Set if the UI window displays the fx chain.
     *
     * @param isExpanded True if expanded
     */
    public void setExpanded (final boolean isExpanded)
    {
        this.isExpanded = isExpanded;
    }


    /**
     * Set if the UI window of the device is open.
     *
     * @param isWindowOpen True if open
     */
    public void setWindowOpen (final boolean isWindowOpen)
    {
        this.isWindowOpen = isWindowOpen;
    }


    /**
     * Set the name of one of the devices siblings.
     *
     * @param index The index of the sibling
     * @param name The name
     */
    public void setSiblingDeviceName (final int index, final String name)
    {
        this.siblings[index] = removeTypeAndManufacturer (name);
    }


    /**
     * Set the number of devices of the channel.
     *
     * @param deviceCount The number of devices
     */
    public void setDeviceCount (final int deviceCount)
    {
        this.deviceCount = deviceCount;
    }


    /**
     * Set the overall number of parameters of the selected device.
     *
     * @param count The number of parameters
     */
    public void setParameterCount (final int count)
    {
        final int numOfPages = count / this.numDevicesInBank + (count % this.numDevicesInBank > 0 ? 1 : 0);
        this.parameterPageNames = new String [numOfPages];
        for (int i = 0; i < numOfPages; i++)
            this.parameterPageNames[i] = "Page " + (i + 1);
    }


    private static String removeTypeAndManufacturer (final String name)
    {
        if (name == null)
            return "";
        if (name.startsWith ("VSTi: "))
            return removeManufacturer (name.substring (6));
        if (name.startsWith ("VST: "))
            return removeManufacturer (name.substring (5));
        if (name.startsWith ("VST3i: "))
            return removeManufacturer (name.substring (7));
        if (name.startsWith ("VST3: "))
            return removeManufacturer (name.substring (6));
        if (name.startsWith ("JS: "))
            return removeManufacturer (name.substring (4));
        return name;
    }


    private static String removeManufacturer (final String name)
    {
        final int index = name.indexOf ('(');
        return index > 1 ? name.substring (0, index).trim () : name;
    }
}