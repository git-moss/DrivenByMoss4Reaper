// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one;

import java.util.List;
import java.util.UUID;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Definition class for the OXI One controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneMk2ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("4E7F5AC9-DE44-4D4A-ABDF-643EC9B6A585");


    /**
     * Constructor.
     */
    public OxiOneMk2ControllerDefinition ()
    {
        super (EXTENSION_ID, "One Mk2", "OXI", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case WINDOWS:
                midiDiscoveryPairs.addAll (this.createDeviceDiscoveryPairs ("OXI ONE MKII"));
                break;

            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("OXI ONE MKII MIDI 1", "OXI ONE MKII MIDI 1"));
                midiDiscoveryPairs.addAll (this.createLinuxDeviceDiscoveryPairs ("OXI ONE MKII Jack 1", "OXI ONE MKII Jack 1"));
                break;

            case MAC, MAC_ARM:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("OXI ONE MKII Jack 1", "OXI ONE MKII Jack 1"));
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("OXI ONE MKII Anschluss 1", "OXI ONE MKII Anschluss 1"));
                break;

            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
