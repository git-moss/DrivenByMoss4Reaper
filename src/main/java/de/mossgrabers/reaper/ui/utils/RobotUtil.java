// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.utils;

import de.mossgrabers.framework.utils.OperatingSystem;

import java.awt.AWTException;
import java.awt.Robot;


/**
 * Robot wrapper for safe instantiation.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RobotUtil
{
    private static Robot robot;


    /**
     * Get an robot instance if possible.
     *
     * @return The robot or null if not available
     */
    private static Robot getRobot ()
    {
        if (robot == null)
        {
            try
            {
                // Freezes Reaper UI on Linux
                if (OperatingSystem.get () != OperatingSystem.LINUX)
                {
                    robot = new Robot ();
                    robot.setAutoDelay (250);
                }
            }
            catch (final AWTException ex)
            {
                robot = null;
            }
        }
        return robot;
    }


    /**
     * Util class.
     */
    private RobotUtil ()
    {
        // Intentionally emtpy
    }


    /**
     * Check if a robot can be instantiated.
     *
     * @return True if available
     */
    public static boolean exists ()
    {
        return getRobot () != null;
    }


    /**
     * Send a press and release event key.
     *
     * @param key THe key to send
     */
    public static void sendKey (final int key)
    {
        final Robot robot = getRobot ();
        if (robot == null)
            return;
        robot.keyPress (key);
        robot.keyRelease (key);
    }
}
