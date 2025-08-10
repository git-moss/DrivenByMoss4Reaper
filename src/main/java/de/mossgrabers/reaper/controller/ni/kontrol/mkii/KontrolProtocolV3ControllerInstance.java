// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.ni.kontrol.mkii;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolControllerDefinition;
import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolControllerSetup;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolDeviceDescriptorV3;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.BackendExchange;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * Komplete Kontrol NIHIA protocol version 3 controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolV3ControllerInstance extends AbstractControllerInstance<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    /** The controller definition instance. */
    public static final KontrolProtocolControllerDefinition CONTROLLER_DEFINITION = new KontrolProtocolControllerDefinition (new KontrolProtocolDeviceDescriptorV3 ());


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public KontrolProtocolV3ControllerInstance (final LogModel logModel, final WindowManager windowManager, final BackendExchange sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<KontrolProtocolControlSurface, KontrolProtocolConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new KontrolProtocolControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI, KontrolProtocol.VERSION_3);
    }
}
