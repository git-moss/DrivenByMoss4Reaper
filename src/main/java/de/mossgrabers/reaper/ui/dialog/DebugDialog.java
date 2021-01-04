// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.dialog;

import de.mossgrabers.reaper.AppCallback;
import de.mossgrabers.reaper.communication.Processor;
import de.mossgrabers.reaper.ui.widget.BoxPanel;
import de.mossgrabers.reaper.ui.widget.TwoColsPanel;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;


/**
 * Dialog for configuring some debug settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DebugDialog extends BasicDialog
{
    private static final long         serialVersionUID = 3020319469692178785L;

    private static final String []    LABELS           =
    {
        "Transport",
        "Project",
        "Track",
        "Playing Notes",
        "Device",
        "Mastertrack",
        "Browser",
        "Marker",
        "Clip",
        "Session"
    };

    private static final String []    MNEMONICS        =
    {
        "T",
        "P",
        "R",
        "N",
        "D",
        "M",
        "B",
        "A",
        "C",
        "S"
    };

    private static final Processor [] PROCESSORS       =
    {
        Processor.TRANSPORT,
        Processor.PROJECT,
        Processor.TRACK,
        Processor.PLAYINGNOTES,
        Processor.DEVICE,
        Processor.MASTER,
        Processor.BROWSER,
        Processor.MARKER,
        Processor.CLIP,
        Processor.SESSION
    };

    private final AppCallback         callback;
    private final JCheckBox []        boxes            = new JCheckBox [LABELS.length];


    /**
     * Constructor.
     *
     * @param owner The owner of the dialog
     * @param callback Where to send the debug settings
     */
    public DebugDialog (final Window owner, final AppCallback callback)
    {
        super ((JFrame) owner, "Debug", true, true);

        this.callback = callback;

        this.setResizable (false);
        this.setMinimumSize (new Dimension (300, 400));

        this.basicInit ();
    }


    /** {@inheritDoc} */
    @Override
    protected Container init () throws Exception
    {
        final JPanel contentPane = new JPanel (new BorderLayout ());

        final TwoColsPanel mainColumn = new TwoColsPanel (true);
        mainColumn.createLabel ("Enable data update from:", null, BoxPanel.NORMAL);

        for (int i = 0; i < LABELS.length; i++)
        {
            final int count = i;
            this.boxes[i] = mainColumn.createCheckBox (LABELS[i], MNEMONICS[i], BoxPanel.NORMAL);
            this.boxes[i].setSelected (true);
            this.boxes[i].addActionListener (event -> this.callback.enableUpdates (PROCESSORS[count], this.boxes[count].isSelected ()));
        }

        contentPane.add (mainColumn, BorderLayout.CENTER);

        // Close button
        final BoxPanel buttons = new BoxPanel (BoxLayout.X_AXIS, true);
        buttons.createSpace (BoxPanel.GLUE);
        this.setButtons (null, buttons.createButton ("Close", null, BoxPanel.NONE));
        contentPane.add (buttons, BorderLayout.SOUTH);

        return contentPane;
    }
}
