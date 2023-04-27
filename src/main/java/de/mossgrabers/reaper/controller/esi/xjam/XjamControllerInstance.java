// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.controller.esi.xjam;

import de.mossgrabers.controller.esi.xjam.XjamConfiguration;
import de.mossgrabers.controller.esi.xjam.XjamControllerDefinition;
import de.mossgrabers.controller.esi.xjam.XjamControllerSetup;
import de.mossgrabers.controller.esi.xjam.controller.XjamControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.reaper.communication.MessageSender;
import de.mossgrabers.reaper.controller.AbstractControllerInstance;
import de.mossgrabers.reaper.framework.IniFiles;
import de.mossgrabers.reaper.framework.ReaperSetupFactory;
import de.mossgrabers.reaper.ui.WindowManager;
import de.mossgrabers.reaper.ui.utils.LogModel;


/**
 * ESI Xjam controller instance.
 *
 * @author Jürgen Moßgraber
 */
public class XjamControllerInstance extends AbstractControllerInstance<XjamControlSurface, XjamConfiguration>
{
    /** The controller definition instance. */
    public static final XjamControllerDefinition CONTROLLER_DEFINITION = new XjamControllerDefinition ();


    /**
     * Constructor.
     *
     * @param logModel The logging model
     * @param windowManager The window manager for the configuration dialog
     * @param sender The sender
     * @param iniFiles The INI configuration files
     */
    public XjamControllerInstance (final LogModel logModel, final WindowManager windowManager, final MessageSender sender, final IniFiles iniFiles)
    {
        super (CONTROLLER_DEFINITION, logModel, windowManager, sender, iniFiles);
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<XjamControlSurface, XjamConfiguration> createControllerSetup (final ReaperSetupFactory setupFactory)
    {
        return new XjamControllerSetup (this.host, setupFactory, this.globalSettingsUI, this.documentSettingsUI);
    }
}
