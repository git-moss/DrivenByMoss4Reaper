// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.ui.dialog;

import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.reaper.framework.daw.data.bank.ParameterBankImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.ParameterImpl;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMap;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPage;
import de.mossgrabers.reaper.framework.daw.data.parameter.map.ParameterMapPageParameter;
import de.mossgrabers.reaper.ui.widget.BoxPanel;
import de.mossgrabers.reaper.ui.widget.Functions;
import de.mossgrabers.reaper.ui.widget.JListX;
import de.mossgrabers.reaper.ui.widget.TwoColsPanel;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.Position;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Dialog for editing the parameter mappings of the currently selected device.
 *
 * @author Jürgen Moßgraber
 */
public class ParameterMappingDialog extends BasicDialog
{
    private static final long                 serialVersionUID = 3773770769128020035L;

    private final ICursorDevice               cursorDevice;
    private final ParameterMap                parameterMap;

    private JListX<ParameterImpl>             parametersListBox;
    private JTextField                        searchField;

    private JListX<ParameterMapPage>          pagesListBox;
    private JListX<ParameterMapPageParameter> pageParametersListBox;

    private JButton                           addPageButton;
    private JButton                           removePageButton;
    private JButton                           editPageButton;
    private JButton                           movePageUpButton;
    private JButton                           movePageDownButton;

    private JButton                           assignParamButton;
    private JButton                           clearParamButton;
    private JButton                           editParamButton;
    private JButton                           moveParamUpButton;
    private JButton                           moveParamDownButton;


    /**
     * Constructor.
     *
     * @param owner The owner of the dialog
     * @param cursorDevice The device for which to map the parameters
     * @param parameterMap The parameter map to edit
     */
    public ParameterMappingDialog (final Window owner, final ICursorDevice cursorDevice, final ParameterMap parameterMap)
    {
        super ((JFrame) owner, "Parameter Mapping", true, true);

        this.setMinimumSize (new Dimension (800, 600));
        this.setSize (600, 600);

        this.cursorDevice = cursorDevice;
        this.parameterMap = parameterMap;

        this.basicInit ();
    }


    /** {@inheritDoc} */
    @Override
    protected void setFocusOn ()
    {
        this.searchField.requestFocus ();
    }


    /** {@inheritDoc} */
    @Override
    protected Container init ()
    {
        final JPanel contentPane = new JPanel (new BorderLayout ());

        // Title

        final TwoColsPanel titlePane = new TwoColsPanel (true);
        contentPane.add (titlePane, BorderLayout.NORTH);

        titlePane.addComponent (new JLabel (this.cursorDevice.getName ()), "Device:", null, BoxPanel.NORMAL);

        // Main content

        final JPanel center = new JPanel (new BorderLayout ());
        contentPane.add (center, BorderLayout.CENTER);

        // Left column - Parameter list + search field

        final BoxPanel searchPane = new BoxPanel (BoxLayout.X_AXIS, true);
        this.searchField = searchPane.createField ("Search:", "S", BoxPanel.SMALL);
        searchPane.createButton (Functions.getIcon ("Clear", 16), BoxPanel.SMALL).addActionListener (event -> this.clearSearchField ());
        searchPane.createButton (Functions.getIcon ("Up", 16), BoxPanel.SMALL).addActionListener (event -> this.searchParam (Position.Bias.Backward));
        final JButton searchNextButton = searchPane.createButton (Functions.getIcon ("Down", 16), BoxPanel.NONE);
        searchNextButton.addActionListener (event -> this.searchParam (Position.Bias.Forward));

        final BoxPanel parameterPane = new BoxPanel (BoxLayout.Y_AXIS, true);
        this.parametersListBox = parameterPane.createListBox ("Parameters:", "P", BoxPanel.NONE, this.getParameters ());

        final JPanel leftColumn = new JPanel (new BorderLayout ());
        leftColumn.add (searchPane, BorderLayout.NORTH);
        leftColumn.add (parameterPane, BorderLayout.CENTER);

        center.add (leftColumn, BorderLayout.CENTER);

        // Right column - Parameter pages

        final JPanel rightColumn = new JPanel (new BorderLayout ());
        center.add (rightColumn, BorderLayout.EAST);

        final BoxPanel pagesBoxWrapper = new BoxPanel (BoxLayout.Y_AXIS, true);
        final List<ParameterMapPage> pages = this.parameterMap.getPages ();
        this.pagesListBox = pagesBoxWrapper.createListBox ("Pages:", "G", BoxPanel.NONE, pages);

        final BoxPanel pagesButtonsPanel = new BoxPanel (BoxLayout.Y_AXIS, true);
        pagesButtonsPanel.createSpace (BoxPanel.GLUE);
        this.addPageButton = pagesButtonsPanel.createButton (Functions.getIcon ("Add"), "Add", null, BoxPanel.SMALL);
        this.addPageButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.addPageButton.addActionListener (event -> this.addParamPage ());
        this.removePageButton = pagesButtonsPanel.createButton (Functions.getIcon ("Remove"), "Remove", null, BoxPanel.SMALL);
        this.removePageButton.addActionListener (event -> this.removeParamPage ());
        this.removePageButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.editPageButton = pagesButtonsPanel.createButton (Functions.getIcon ("Edit"), "Rename", null, BoxPanel.NONE);
        this.editPageButton.addActionListener (event -> this.editParamPage ());
        this.editPageButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.movePageUpButton = pagesButtonsPanel.createButton (Functions.getIcon ("Up"), "Move Up", null, BoxPanel.NONE);
        this.movePageUpButton.addActionListener (event -> this.moveParamPageUp ());
        this.movePageUpButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.movePageDownButton = pagesButtonsPanel.createButton (Functions.getIcon ("Down"), "Move Down", null, BoxPanel.NONE);
        this.movePageDownButton.addActionListener (event -> this.moveParamPageDown ());
        this.movePageDownButton.setHorizontalAlignment (SwingConstants.LEFT);
        pagesButtonsPanel.sizeEqual ();

        final JPanel pagesRowPanel = new JPanel (new BorderLayout ());
        pagesRowPanel.add (pagesBoxWrapper, BorderLayout.CENTER);
        pagesRowPanel.add (pagesButtonsPanel, BorderLayout.EAST);

        this.pagesListBox.addListSelectionListener (e -> this.handlePageListChanges ());

        // Right column - Parameters assigned to page

        final BoxPanel paramsOfPageBoxWrapper = new BoxPanel (BoxLayout.Y_AXIS, true);
        final List<ParameterMapPageParameter> content = pages.isEmpty () ? Collections.emptyList () : pages.get (0).getParameters ();
        this.pageParametersListBox = paramsOfPageBoxWrapper.createListBox ("Parameters assigned to page:", "T", BoxPanel.NONE, content);

        final BoxPanel paramButtonsPanel = new BoxPanel (BoxLayout.Y_AXIS, true);
        paramButtonsPanel.createSpace (BoxPanel.GLUE);
        this.assignParamButton = paramButtonsPanel.createButton (Functions.getIcon ("Assign"), "Assign", null, BoxPanel.SMALL);
        this.assignParamButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.assignParamButton.addActionListener (event -> this.assignParam ());
        this.clearParamButton = paramButtonsPanel.createButton (Functions.getIcon ("Remove"), "Clear", null, BoxPanel.SMALL);
        this.clearParamButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.clearParamButton.addActionListener (event -> this.clearParam ());
        this.editParamButton = paramButtonsPanel.createButton (Functions.getIcon ("Edit"), "Rename", null, BoxPanel.NONE);
        this.editParamButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.editParamButton.addActionListener (event -> this.editParam ());
        this.moveParamUpButton = paramButtonsPanel.createButton (Functions.getIcon ("Up"), "Move Up", null, BoxPanel.NONE);
        this.moveParamUpButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.moveParamUpButton.addActionListener (event -> this.moveParamUp ());
        this.moveParamDownButton = paramButtonsPanel.createButton (Functions.getIcon ("Down"), "Move Down", null, BoxPanel.NONE);
        this.moveParamDownButton.setHorizontalAlignment (SwingConstants.LEFT);
        this.moveParamDownButton.addActionListener (event -> this.moveParamDown ());
        paramButtonsPanel.sizeEqual ();

        final JPanel paramsOfPageRowPanel = new JPanel (new BorderLayout ());
        paramsOfPageRowPanel.add (paramsOfPageBoxWrapper, BorderLayout.CENTER);
        paramsOfPageRowPanel.add (paramButtonsPanel, BorderLayout.EAST);

        rightColumn.add (pagesRowPanel, BorderLayout.CENTER);
        rightColumn.add (paramsOfPageRowPanel, BorderLayout.SOUTH);

        this.pageParametersListBox.addListSelectionListener (e -> this.updateParameterListButtons ());

        // Button Pane

        final BoxPanel buttons = new BoxPanel (BoxLayout.X_AXIS, true);
        contentPane.add (buttons, BorderLayout.SOUTH);
        buttons.createSpace (BoxPanel.GLUE);

        final JButton discardButton = buttons.createButton (Functions.getIcon ("Remove"), "Discard", null, BoxPanel.NORMAL);
        final JButton saveButton = buttons.createButton (Functions.getIcon ("Confirm"), "Save", null, BoxPanel.NONE);
        Functions.asWidthAs (discardButton, saveButton);

        this.setButtons (saveButton, discardButton);

        this.getRootPane ().setDefaultButton (searchNextButton);

        this.handlePageListChanges ();

        return contentPane;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean onOk ()
    {
        final List<ParameterMapPage> pages = this.parameterMap.getPages ();
        pages.clear ();

        final DefaultListModel<ParameterMapPage> model = this.pagesListBox.getModel ();
        for (int i = 0; i < model.getSize (); i++)
            pages.add (model.get (i));

        return super.onOk ();
    }


    /**
     * A different parameter page was selected.
     */
    private void handlePageListChanges ()
    {
        final int selectedIndex = this.pagesListBox.getSelectedIndex ();
        final boolean hasSelection = selectedIndex >= 0;

        this.removePageButton.setEnabled (hasSelection);
        this.editPageButton.setEnabled (hasSelection);
        this.movePageUpButton.setEnabled (hasSelection && selectedIndex > 0);
        this.movePageDownButton.setEnabled (hasSelection && selectedIndex < this.pagesListBox.getModel ().getSize () - 1);

        this.fillParameterBox ();
    }


    /**
     * Update the content of the parameter list box.
     */
    private void fillParameterBox ()
    {
        final DefaultListModel<ParameterMapPageParameter> model = this.pageParametersListBox.getModel ();
        final int selectedPage = this.pagesListBox.getSelectedIndex ();
        model.clear ();
        if (selectedPage < 0)
            return;
        final int selectedIndex = this.pageParametersListBox.getSelectedIndex ();
        model.addAll (this.pagesListBox.getModel ().get (selectedPage).getParameters ());
        this.pageParametersListBox.setSelectedIndex (selectedIndex < 0 ? 0 : selectedIndex);
    }


    /**
     * Update the related button states depending on the content of the selected parameter page.
     */
    private void updateParameterListButtons ()
    {
        final int selectedIndex = this.pageParametersListBox.getSelectedIndex ();
        final boolean hasSelection = selectedIndex >= 0;
        final DefaultListModel<ParameterMapPageParameter> model = this.pageParametersListBox.getModel ();
        final boolean isAssigned = hasSelection && model.get (selectedIndex).isAssigned ();

        this.assignParamButton.setEnabled (hasSelection);
        this.clearParamButton.setEnabled (hasSelection && isAssigned);
        this.editParamButton.setEnabled (hasSelection && isAssigned);
        this.moveParamUpButton.setEnabled (hasSelection && selectedIndex > 0);
        this.moveParamDownButton.setEnabled (hasSelection && selectedIndex < model.getSize () - 1);
    }


    /**
     * Add a new parameter page.
     */
    private void addParamPage ()
    {
        final DefaultListModel<ParameterMapPage> model = this.pagesListBox.getModel ();
        final int size = model.getSize ();
        model.addElement (new ParameterMapPage ("Page " + (size + 1)));
        this.pagesListBox.setSelectedIndex (size);
    }


    /**
     * Remove a parameter page.
     */
    private void removeParamPage ()
    {
        int selectedIndex = this.pagesListBox.getSelectedIndex ();
        if (selectedIndex < 0)
            return;
        final DefaultListModel<ParameterMapPage> model = this.pagesListBox.getModel ();
        model.remove (selectedIndex);
        if (model.isEmpty ())
            return;
        selectedIndex = Math.min (selectedIndex, model.getSize () - 1);
        this.pagesListBox.setSelectedIndex (selectedIndex);
    }


    /**
     * Edit the name of a parameter page.
     */
    private void editParamPage ()
    {
        final ParameterMapPage page = this.pagesListBox.getSelectedValue ();
        if (page == null)
            return;
        final String result = JOptionPane.showInputDialog ("Page Name:", page.getName ());
        if (result == null || result.isBlank ())
            return;
        page.setName (result);
        this.pagesListBox.updateUI ();
    }


    /**
     * Move the selected parameter page down in the list.
     */
    private void moveParamPageDown ()
    {
        final int selectedIndex = this.pagesListBox.getSelectedIndex ();
        if (selectedIndex >= 0 && selectedIndex < this.pagesListBox.getModel ().getSize () - 1)
            this.swapParamPage (selectedIndex, selectedIndex + 1);
    }


    /**
     * Move the selected parameter page up in the list.
     */
    private void moveParamPageUp ()
    {
        final int selectedIndex = this.pagesListBox.getSelectedIndex ();
        if (selectedIndex > 0)
            this.swapParamPage (selectedIndex, selectedIndex - 1);
    }


    /**
     * Swap two parameter pages.
     *
     * @param selectedIndex The first parameter page
     * @param destinationIndex The second page, which will be selected after the method executed
     */
    private void swapParamPage (final int selectedIndex, final int destinationIndex)
    {
        final DefaultListModel<ParameterMapPage> model = this.pagesListBox.getModel ();
        final ParameterMapPage page1 = model.get (selectedIndex);
        final ParameterMapPage page2 = model.get (destinationIndex);
        model.set (selectedIndex, page2);
        model.set (destinationIndex, page1);
        this.pagesListBox.setSelectedIndex (destinationIndex);
    }


    /**
     * Assign a parameter to the selected parameter slot on the selected page.
     */
    private void assignParam ()
    {
        final ParameterImpl selectedParameter = this.parametersListBox.getSelectedValue ();
        final ParameterMapPageParameter selectedValue = this.pageParametersListBox.getSelectedValue ();
        if (selectedParameter == null || selectedValue == null)
            return;
        selectedValue.assign (selectedParameter.getPosition (), selectedParameter.getName ());
        this.pageParametersListBox.repaint ();
        this.updateParameterListButtons ();
    }


    /**
     * Clear the selected parameter slot on the selected page.
     */
    private void clearParam ()
    {
        final ParameterMapPageParameter selectedValue = this.pageParametersListBox.getSelectedValue ();
        if (selectedValue == null)
            return;
        selectedValue.assign (-1, "");
        this.pageParametersListBox.repaint ();
        this.updateParameterListButtons ();
    }


    /**
     * Edit the name of the selected parameter slot on the selected page.
     */
    private void editParam ()
    {
        final ParameterMapPageParameter selectedParam = this.pageParametersListBox.getSelectedValue ();
        if (selectedParam == null)
            return;
        final String result = JOptionPane.showInputDialog ("Parameter Name:", selectedParam.getName ());
        if (result == null)
            return;
        selectedParam.setName (result);
        this.pageParametersListBox.updateUI ();
    }


    /**
     * Move the selected parameter down in the list.
     */
    private void moveParamDown ()
    {
        final int selectedIndex = this.pageParametersListBox.getSelectedIndex ();
        if (selectedIndex >= 0 && selectedIndex < this.pageParametersListBox.getModel ().getSize () - 1)
            this.swapParam (selectedIndex, selectedIndex + 1);
    }


    /**
     * Move the selected parameter up in the list.
     */
    private void moveParamUp ()
    {
        final int selectedIndex = this.pageParametersListBox.getSelectedIndex ();
        if (selectedIndex > 0)
            this.swapParam (selectedIndex, selectedIndex - 1);
    }


    /**
     * Swap two parameter slots.
     *
     * @param selectedIndex The first parameter slot
     * @param destinationIndex The second slot, which will be selected after the method executed
     */
    protected void swapParam (final int selectedIndex, final int destinationIndex)
    {
        final int selectedPage = this.pagesListBox.getSelectedIndex ();
        if (selectedPage < 0)
            return;

        // Swap it in the page
        final ParameterMapPage parameterMapPage = this.pagesListBox.getModel ().get (selectedPage);
        parameterMapPage.swapParameters (selectedIndex, destinationIndex);

        // Swap it in the UI
        final DefaultListModel<ParameterMapPageParameter> model = this.pageParametersListBox.getModel ();
        final ParameterMapPageParameter param1 = model.get (selectedIndex);
        final ParameterMapPageParameter param2 = model.get (destinationIndex);
        model.set (selectedIndex, param2);
        model.set (destinationIndex, param1);
        this.pageParametersListBox.setSelectedIndex (destinationIndex);
    }


    /**
     * Clear the text in the search field.
     */
    private void clearSearchField ()
    {
        this.searchField.setText ("");
        this.searchField.requestFocus ();
    }


    /**
     * Searches the next parameter in the parameter list starting from the current selection which
     * matches the search text.
     *
     * @param direction The search direction
     */
    private void searchParam (final Position.Bias direction)
    {
        if (this.parametersListBox.getModel ().getSize () == 0)
            return;

        final String searchText = this.searchField.getText ();
        if (searchText.isBlank ())
            return;

        int index = this.parametersListBox.getSelectedIndex ();
        if (index < 0)
            index = 0;

        final int nextIndex = Functions.getNextMatch (this.parametersListBox, searchText, index, direction);
        if (nextIndex >= 0)
            this.parametersListBox.setSelectedIndex (nextIndex);
    }


    /**
     * Get all parameters from the currently selected device.
     *
     * @return The parameters
     */
    private List<ParameterImpl> getParameters ()
    {
        if (this.cursorDevice.getParameterBank () instanceof final ParameterBankImpl parameterBank)
        {
            final int count = parameterBank.getUnpagedItemCount ();
            final List<ParameterImpl> params = new ArrayList<> (count);
            for (int i = 0; i < count; i++)
                params.add (parameterBank.getUnpagedItem (i));
            return params;
        }
        return Collections.emptyList ();
    }
}
