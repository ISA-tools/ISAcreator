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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class Toolbox extends JPanel {
    public static final String ENTER_A_QUALIFIER = "Enter a qualifier";

    // toolbox will have 5 buttons in it's entirety for addition of characteristics, factors, protocols, parameters and comments.

    @InjectedResource
    private ImageIcon addField, addFieldOver, removeField, removeFieldOver, add, addOver;

    private JPanel southPanel, addButtonContainer;
    private JPanel qualifierEntryPanel;
    private JTextField qualifier;
    private JComboBox fieldOptions;
    private int currentlySelectedForQualifierEntry = -1;

    private String fieldToAdd;
    private boolean validFieldName;

    public Toolbox() {
        ResourceInjector.get("formatmappingutility-package.style").inject(this);

        setLayout(new BorderLayout());
        createQualifierEntryPanel();
        setContents(null);
    }

    public void setContents(String currentField) {
        Box fieldBox = Box.createHorizontalBox();
        fieldBox.add(UIHelper.createLabel("Add a", UIHelper.VER_10_BOLD, UIHelper.LIGHT_GREY_COLOR));
        fieldOptions = new JComboBox();
        fieldOptions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (fieldOptions.getSelectedItem() != null && !(fieldOptions.getSelectedItem() == FieldTypes.SELECT_A_FIELD)) {
                    boolean showQualifierEntry = shouldDisplayQualifierEntry(fieldOptions.getSelectedItem().toString());
                    if (showQualifierEntry) {
                        qualifierEntryPanel.setVisible(showQualifierEntry);
                        addButtonContainer.setVisible(showQualifierEntry);
                        currentlySelectedForQualifierEntry = ((FieldTypes) fieldOptions.getSelectedItem()).id;
                        // property change event should be thrown from within the qualifier entry panel add button!
                    } else {
                        qualifierEntryPanel.setVisible(showQualifierEntry);
                        // now fire an property change to inform listening component that a node should be added to the
                        // tree in the position directly after the currently selected node item in the tree.
                        currentlySelectedForQualifierEntry = -1;
                        validFieldName = true;
                        fieldToAdd = fieldOptions.getSelectedItem().toString();
                        addButtonContainer.setVisible(true);
                    }
                } else {
                    addButtonContainer.setVisible(false);
                    qualifierEntryPanel.setVisible(false);
                }
            }
        });
        updateFieldOptions(currentField);
        fieldBox.add(fieldOptions);

        add(fieldBox, BorderLayout.NORTH);
    }

    private boolean shouldDisplayQualifierEntry(String field) {
        return (field.equals(FieldTypes.COMMENT.name) || field.equals(FieldTypes.PARAMETER.name)
                || field.equals(FieldTypes.CHARACTERISTIC.name) || field.equals(FieldTypes.FACTOR.name));
    }

    public void updateFieldOptions(String currentField) {
        Object[] options;
        if (currentField == null) {
            options = new Object[]{FieldTypes.SELECT_A_FIELD};
        } else {
            options = FieldTypes.values();
            if (!currentField.contains(FieldTypes.PROTOCOL.name)) {
                options = FieldTypes.values(FieldTypes.PARAMETER);
            }
        }

        fieldOptions.removeAllItems();

        for (Object option : options) {
            fieldOptions.addItem(option);
        }
    }

    private void createQualifierEntryPanel() {
        qualifierEntryPanel = new JPanel();
        qualifierEntryPanel.setLayout(new BoxLayout(qualifierEntryPanel, BoxLayout.PAGE_AXIS));
        qualifierEntryPanel.setPreferredSize(new Dimension(170, 20));

        qualifier = new RoundedJTextField(12);
        qualifier.setText(ENTER_A_QUALIFIER);
        UIHelper.renderComponent(qualifier, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, false);

        qualifierEntryPanel.add(qualifier);

        qualifierEntryPanel.add(Box.createGlue());
        qualifierEntryPanel.setVisible(false);

        southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));

        createAddButtonContainer();

        southPanel.add(qualifierEntryPanel);
        southPanel.add(addButtonContainer);
        // add stuff to it.
        add(southPanel, BorderLayout.SOUTH);
    }

    private void createAddButtonContainer() {
        final JLabel addButton = new JLabel(add);
        addButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                addButton.setIcon(addOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addButton.setIcon(add);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addButton.setIcon(add);

                String qualifierText;
                if (qualifierEntryPanel.isVisible()) {
                    if (!(qualifierText = qualifier.getText().trim()).equals("") && !qualifierText.equals(ENTER_A_QUALIFIER)) {
                        if (currentlySelectedForQualifierEntry == FieldTypes.CHARACTERISTIC.id) {
                            fieldToAdd = "Characteristics[" + qualifierText + "]";
                        } else if (currentlySelectedForQualifierEntry == FieldTypes.FACTOR.id) {
                            fieldToAdd = "Factor Value[" + qualifierText + "]";
                        } else if (currentlySelectedForQualifierEntry == FieldTypes.PARAMETER.id) {
                            fieldToAdd = "Parameter Value[" + qualifierText + "]";
                        } else {
                            fieldToAdd = "Comment[" + qualifierText + "]";
                        }
                        firePropertyChange("nodeAdded", "", fieldToAdd);
                        updateFieldOptions(null);
                    } else {
                        qualifier.setText(ENTER_A_QUALIFIER);
                    }
                } else {
                    firePropertyChange("nodeAdded", "", fieldToAdd);
                    updateFieldOptions(null);
                }
            }

        });

        addButtonContainer = new JPanel();
        addButtonContainer.add(addButton);
    }

    enum FieldTypes {
        SELECT_A_FIELD("Field Type", -1), CHARACTERISTIC("Characteristic", 0), FACTOR("Factor Value", 1),
        PROTOCOL("Protocol REF", 2), PARAMETER("Parameter Value", 3), COMMENT("Comment", 4),
        SAMPLE("Sample Name", 5), MATERIAL("Material Type", 6);

        private String name;
        private int id;

        FieldTypes(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return name;
        }

        public static FieldTypes[] values(FieldTypes exclusion) {
            List<FieldTypes> fields = new ArrayList<FieldTypes>();
            for (FieldTypes type : values()) {
                if (exclusion != type) {
                    fields.add(type);
                }
            }

            return fields.toArray(new FieldTypes[fields.size()]);
        }
    }

}
