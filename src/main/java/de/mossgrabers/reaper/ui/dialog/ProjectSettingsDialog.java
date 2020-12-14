// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.dialog;

import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.reaper.controller.IControllerInstance;
import de.mossgrabers.reaper.framework.configuration.DocumentSettingsUI;
import de.mossgrabers.reaper.framework.configuration.IfxSetting;
import de.mossgrabers.reaper.ui.widget.BoxPanel;
import de.mossgrabers.reaper.ui.widget.TitledSeparator;
import de.mossgrabers.reaper.ui.widget.TwoColsPanel;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;


/**
 * Dialog for editing all settings of a controller configuration.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProjectSettingsDialog extends BasicDialog
{
    private static final long                         serialVersionUID = 7245426678995058875L;

    private final transient List<IControllerInstance> instances;


    /**
     * Constructor.
     *
     * @param owner The owner of the dialog
     * @param instances The controller instances
     */
    public ProjectSettingsDialog (final Window owner, final List<IControllerInstance> instances)
    {
        super ((JFrame) owner, "Project Settings", true, true);

        this.setMinimumSize (new Dimension (600, 600));
        this.setSize (600, 600);

        this.instances = instances;

        this.basicInit ();
    }


    /** {@inheritDoc} */
    @Override
    protected Container init () throws Exception
    {
        final JPanel contentPane = new JPanel (new BorderLayout ());
        final JTabbedPane tabbedPane = new JTabbedPane ();
        contentPane.add (tabbedPane, BorderLayout.CENTER);

        for (final IControllerInstance instance: this.instances)
        {
            if (!instance.isRunning ())
                continue;

            final JPanel tabContentPane = new JPanel (new BorderLayout ());
            final IControllerDefinition definition = instance.getDefinition ();
            tabbedPane.addTab (definition.getHardwareModel (), tabContentPane);

            final TwoColsPanel mainColumn = new TwoColsPanel (true);
            final JPanel wrapper = new JPanel (new BorderLayout ());
            wrapper.add (mainColumn, BorderLayout.NORTH);

            final JScrollPane scrollPane = new JScrollPane (wrapper);
            scrollPane.setHorizontalScrollBarPolicy (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            tabContentPane.add (scrollPane, BorderLayout.CENTER);

            String category = null;
            final ISettingsUI settings = instance.getDocumentSettingsUI ();
            final List<IfxSetting> ifxSettings = ((DocumentSettingsUI) settings).getSettings ();
            if (ifxSettings.isEmpty ())
            {
                mainColumn.add (new JLabel ("This device has no project settings."));
                continue;
            }
            for (final IfxSetting s: ifxSettings)
            {
                final String cat = s.getCategory ();
                if (category == null || !category.equals (cat))
                {
                    category = s.getCategory ();
                    mainColumn.addComponent (new TitledSeparator (category), BoxPanel.NORMAL);
                }

                final JLabel label = s.getLabelWidget ();
                final JComponent widget = s.getWidget ();

                mainColumn.addComponent (widget, label, null, BoxPanel.NORMAL);
            }
        }

        // Close button
        final BoxPanel buttons = new BoxPanel (BoxLayout.X_AXIS, true);
        buttons.createSpace (BoxPanel.GLUE);
        this.setButtons (null, buttons.createButton ("Close", null, BoxPanel.NONE));
        contentPane.add (buttons, BorderLayout.SOUTH);

        this.addComponentListener (new ComponentAdapter ()
        {
            @Override
            public void componentResized (final ComponentEvent event)
            {
                final Rectangle b = ProjectSettingsDialog.this.getBounds ();
                final Dimension screenSize = Toolkit.getDefaultToolkit ().getScreenSize ();
                final int maxHeight = (int) screenSize.getHeight () - 200;
                if (b.height > maxHeight)
                {
                    b.height = maxHeight;
                    ProjectSettingsDialog.this.setBounds (b);
                }
                super.componentResized (event);
            }
        });

        return contentPane;
    }
}
