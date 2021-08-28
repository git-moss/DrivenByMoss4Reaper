// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.ni.kontrol.mkii;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolControllerDefinition;
import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolControllerSetup;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolDeviceDescriptorV2;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Komplete Kontrol NIHIA protocol version 2 controller instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolProtocolV2ControllerInstance extends AbstractControllerInstance<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    /** The controller definition instance. */
    public static final KontrolProtocolControllerDefinition CONTROLLER_DEFINITION = new KontrolProtocolControllerDefinition (new KontrolProtocolDeviceDescriptorV2 ());


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public KontrolProtocolV2ControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<KontrolProtocolControlSurface, KontrolProtocolConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new KontrolProtocolControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, KontrolProtocol.VERSION_2);
    }
}
