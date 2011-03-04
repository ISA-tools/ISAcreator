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

package org.isatools.isacreator.gui.formelements;

import org.isatools.isacreator.autofiltercombo.AutoFilterComboCellEditor;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.model.Assay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Subform is used for data entry in the Study definition form. Can mould itself for various types of data entry
 *
 * @author Eamonn Maguire
 */
public class AssaySubForm extends SubForm implements Serializable {

    public AssaySubForm(String title, FieldTypes fieldType,
                        List<SubFormField> fields, int initialNoFields, int width,
                        int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    public AssaySubForm(String title, FieldTypes fieldType,
                        List<SubFormField> fields, int initialNoFields, int width,
                        int height, DataEntryEnvironment dep) {
        super(title, fieldType, fields, initialNoFields, width, height, dep);

    }

    /**
     * Add a column to the subform
     *
     * @return Boolean - true if added, false otherwise
     */
    public boolean doAddColumn(DefaultTableModel model, TableColumn col) {

        if (fieldType == FieldTypes.ASSAY && (parent != null)) {
            // if adding the assay was succesful, then stop the column from being edited
            int colToCheck = dtm.getColumnCount() - 1;

            if (dtm.getValueAt(1, colToCheck) == null ||
                    dtm.getValueAt(1, colToCheck).toString().equals(AutoFilterComboCellEditor.BLANK_VALUE)) {
                dtm.setValueAt("", 1, colToCheck);
            }

            if ((dtm.getValueAt(0, colToCheck) != null) &&
                    (dtm.getValueAt(3, colToCheck) != null)) {
                if (!dtm.getValueAt(3, colToCheck).toString().equals("")) {
                    String assayName = dtm.getValueAt(3, colToCheck).toString();

                    if (!assayName.startsWith("a_")) {
                        assayName = "a_" + assayName;
                    }

                    String extension = assayName.substring(assayName.lastIndexOf(
                            ".") + 1);

                    if (!extension.equals("txt")) {
                        assayName += ".txt";
                    }

                    dtm.setValueAt(assayName, 3, colToCheck);

                    if (dtm.getValueAt(2, colToCheck) == null) {
                        dtm.setValueAt("", 2, colToCheck);
                    }

                    if (parent.getDataEntryEnvironment()
                            .addAssay(dtm.getValueAt(0, colToCheck)
                                    .toString(),
                                    dtm.getValueAt(1, colToCheck).toString(),
                                    dtm.getValueAt(2, colToCheck).toString(),
                                    dtm.getValueAt(3, colToCheck).toString())) {
                        // set previous column to be uneditable
                        uneditableRecords.add(dtm.getColumnCount() - 1);
                        scrollTable.addColumn(col);
                        model.addColumn(fieldType);
                        model.fireTableStructureChanged();

                        return true;
                    }
                }
            } else {
                JOptionPane optionPane = new JOptionPane(
                        "Problem occurred when attempting to add an Assay... " +
                                "\n All fields for the assay definition are not complete!",
                        JOptionPane.OK_OPTION);
                UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
                optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getPropertyName()
                                .equals(JOptionPane.VALUE_PROPERTY)) {
                            parent.getDataEntryEnvironment().getParentFrame().hideSheet();
                        }
                    }
                });
                parent.getDataEntryEnvironment().getParentFrame()
                        .showJDialogAsSheet(optionPane.createDialog(this,
                                "All fields not completed"));
            }
        }

        return false;
    }

    public void reformPreviousContent() {
        if (parent != null) {
            reformItems();
        }
    }

    public void reformItems() {
        Map<String, Assay> assays = parent.getAssays();
        int colCount = 1;

        if (assays != null) {
            for (String assayRef : assays.keySet()) {

                Assay assay = assays.get(assayRef);
                String[] assayInfo = {
                        assay.getMeasurementEndpoint(), assay.getTechnologyType(),
                        assay.getAssayPlatform(), assay.getAssayReference()
                };

                for (int j = 0; j < assayInfo.length; j++) {
                    dtm.setValueAt(assayInfo[j], j, colCount);
                }

                colCount++;
            }

            for (int i = 1; i < (dtm.getColumnCount() - 1); i++) {
                uneditableRecords.add(i);
            }
        }
    }

    protected void removeItem(int assayToRemove) {
        // get factor name which is in the 2nd row (index 1) of the table
        if (scrollTable.getModel().getValueAt(3, assayToRemove) != null) {
            String assayRef = scrollTable.getModel().getValueAt(3, assayToRemove)
                    .toString();

            // remove factor from assays
            parent.removeAssay(assayRef);
        }
        // remove column
        removeColumn(assayToRemove);
    }

    public void update() {
        // nothing required here.
    }

    public void updateItems() {
        // nothing required here.
    }

    /**
     * Implementing this method allows for the creation of additional menu
     * elements in the options panel of the subform.
     */
    public void createCustomOptions() {
    }
}
