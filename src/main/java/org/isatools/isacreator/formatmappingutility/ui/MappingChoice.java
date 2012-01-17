/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isatools.isacreator.formatmappingutility.ui;

import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.formatmappingutility.io.ISAField;
import org.isatools.isacreator.formatmappingutility.logic.MappingTypes;
import org.isatools.isacreator.formatmappingutility.tablebrowser.IncomingFileBrowser;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MappingChoice
 *
 * @author Eamonn Maguire
 * @date Oct 6, 2009
 */


public class MappingChoice extends JPanel {

    @InjectedResource
    public ImageIcon addButtonIcon, addButtonIconOver, removeButtonIcon, removeButtonIconOver;

    private JComboBox typeSelection;
    private JComboBox fieldSelection;
    private JComponent fieldDrop;
    private JTextField literalEntry;
    private JLabel addRemoveMappingChoice;
    private JPanel changeableContentContainer;


    // the preexisting mapping is used to automatically create mappings based on those loaded from a previous
    // mapping session.
    private ISAField preExistingMapping = null;
    private String[][] incomingFileSummaryData;
    private boolean lastInList = false;
    private String[] columnsToBeMappedTo;

    private IncomingFileBrowser fileBrowserUI;
    private DropDownComponent dropdown;

    /**
     * Create a new MappingChoice.
     *
     * @param columnsToBeMappedTo - the column choices to be presented to the user when creating a new mapping
     * @param lastInList          - is this mapping field the last in the list? if so, then interface will provide a remove button
     */
    public MappingChoice(String[] columnsToBeMappedTo, boolean lastInList) {
        this(columnsToBeMappedTo, lastInList, null);
    }


    public MappingChoice(String[] columnsToBeMappedTo, boolean lastInList, ISAField preExistingMapping) {
        this(columnsToBeMappedTo, lastInList, preExistingMapping, MappingEntryGUI.getInitialData());
    }

    public MappingChoice(String[] columnsToBeMappedTo, boolean lastInList, ISAField preExistingMapping, String[][] incomingFileSummaryData) {
        this.columnsToBeMappedTo = columnsToBeMappedTo;
        this.lastInList = lastInList;
        this.preExistingMapping = preExistingMapping;
        this.incomingFileSummaryData = incomingFileSummaryData;

        ResourceInjector.get("formatmappingutility-package.style").inject(this);

        setLayout(new FlowLayout());
        createGUI();
    }

    public void createGUI() {
        typeSelection = new JComboBox(MappingTypes.values());
        fieldSelection = new JComboBox(columnsToBeMappedTo);
        literalEntry = new RoundedJTextField(10);
        literalEntry.setText("-");

        // set the appearance for ach of the fields!
        UIHelper.renderComponent(typeSelection, UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR, false);
        UIHelper.setJComboBoxAsHeavyweight(typeSelection);
        UIHelper.renderComponent(fieldSelection, UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR, false);
        UIHelper.setJComboBoxAsHeavyweight(fieldSelection);
        UIHelper.renderComponent(literalEntry, UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR, false);

        typeSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (typeSelection.getSelectedIndex() == -1) {
                    typeSelection.setSelectedIndex(0);
                }
                showHideFields();
            }
        });


        fieldSelection.setPreferredSize(new Dimension(130, 25));

        fieldDrop = createTableBrowseDropdown(fieldSelection);

        fieldDrop.setPreferredSize(new Dimension(160, 25));

        changeableContentContainer = new JPanel();
        changeableContentContainer.setLayout(new BoxLayout(changeableContentContainer, BoxLayout.LINE_AXIS));
        changeableContentContainer.setPreferredSize(new Dimension(160, 25));

        add(typeSelection);
        add(changeableContentContainer);


        if (preExistingMapping != null) {
            typeSelection.setSelectedItem(preExistingMapping.getType());

            // check if columns available contains the item it is to be set to.
            if (preExistingMapping.getType() == MappingTypes.LITERAL) {
                literalEntry.setText(preExistingMapping.getMapping());
            } else {
                if (checkFieldExists(preExistingMapping.getMapping())) {
                    fieldSelection.setSelectedItem(preExistingMapping.getMapping());
                }
            }
        }

        showHideFields();

        ImageIcon imageToUse = (lastInList) ? addButtonIcon : removeButtonIcon;


        addRemoveMappingChoice = new JLabel(imageToUse);
        addRemoveMappingChoice.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (addRemoveMappingChoice.isEnabled()) {
                    addRemoveMappingChoice.setIcon(lastInList ? addButtonIcon : removeButtonIcon);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (addRemoveMappingChoice.isEnabled()) {
                    addRemoveMappingChoice.setIcon(lastInList ? addButtonIconOver : removeButtonIconOver);
                }
            }

            public void mousePressed(MouseEvent mouseEvent) {

                if (addRemoveMappingChoice.isEnabled()) {
                    if (lastInList) {
                        // tell the parent to add a new mapping
                        addRemoveMappingChoice.setIcon(addButtonIcon);
                        firePropertyChange("addNewMapping", "1", "2");
                    } else {
                        // tell the parent to remove this mapping!
                        firePropertyChange("removeThisMapping", "1", getCurrentInstance());
                    }
                }
            }
        });

        add(addRemoveMappingChoice);
    }

    public JComponent createTableBrowseDropdown(final JComboBox field) {

        fileBrowserUI = new IncomingFileBrowser(columnsToBeMappedTo, incomingFileSummaryData);
        dropdown = new DropDownComponent(field, fileBrowserUI, DropDownComponent.TABLE_BROWSER);
        dropdown.setToolTipText("<html><strong>Browse incoming file structure</strong</html>");
        fileBrowserUI.addPropertyChangeListener("selectedColumn",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(fileBrowserUI);
                        field.setSelectedItem(evt.getNewValue().toString());
                    }
                });

        fileBrowserUI.addPropertyChangeListener("noneSelected",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(fileBrowserUI);
                    }
                });

        return dropdown;
    }

    private boolean checkFieldExists(String field) {
        for (String f : columnsToBeMappedTo) {
            if (field.equals(f)) {
                return true;
            }
        }
        return false;
    }

    public void setLastInListProperty(boolean lastInList) {
        if (this.lastInList != lastInList) {
            this.lastInList = lastInList;
            ImageIcon imageToUse = (lastInList) ? addButtonIcon : removeButtonIcon;
            addRemoveMappingChoice.setIcon(imageToUse);
        }
    }

    private JPanel getCurrentInstance() {
        return this;
    }

    public String getTypeSelected() {
        return typeSelection.getSelectedItem().toString();
    }

    public String getValueEntered() {
        if (typeSelection.getSelectedItem().equals(MappingTypes.DATA_FIELD)) {
            return "<<" + fieldSelection.getSelectedIndex() + ">>";
        } else {
            return literalEntry.getText();
        }
    }

    public String getValueForVisualization() {
        if (typeSelection.getSelectedItem().equals(MappingTypes.DATA_FIELD)) {
            return fieldSelection.getSelectedItem().toString();
        } else {
            return literalEntry.getText() + " (literal)";
        }
    }

    private void showHideFields() {
        if (typeSelection.getSelectedItem() == MappingTypes.LITERAL) {
            if (!literalEntry.isShowing()) {
                changeableContentContainer.remove(fieldDrop);
                changeableContentContainer.add(literalEntry);
            }
        } else {
            if (!fieldSelection.isShowing()) {
                changeableContentContainer.remove(literalEntry);
                changeableContentContainer.add(fieldDrop);

            }
        }
        changeableContentContainer.validate();
        changeableContentContainer.repaint();
    }

    public void disableEnableComponents(boolean disable) {
        typeSelection.setEnabled(disable);
        fieldSelection.setEnabled(disable);
        fieldDrop.setEnabled(disable);
        literalEntry.setEnabled(disable);
        addRemoveMappingChoice.setEnabled(disable);
    }


    /**
     * This method is used to create the ISAField for use in creating the xml when saving the mappings out
     * to file
     *
     * @return ISAField
     */
    public ISAField createISAField() {
        String mappingValue;

        if (typeSelection.getSelectedItem() == MappingTypes.DATA_FIELD) {
            mappingValue = fieldSelection.getSelectedItem().toString();
        } else {
            mappingValue = literalEntry.getText();
        }

        return new ISAField(MappingTypes.resolveTypeFromString(typeSelection.getSelectedItem().toString()), mappingValue);


    }
}