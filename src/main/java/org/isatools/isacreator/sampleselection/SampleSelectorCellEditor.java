package org.isatools.isacreator.sampleselection;
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

import org.isatools.isacreator.apiutils.StudyUtils;
import org.isatools.isacreator.autofilterfield.AutoCompleteUI;
import org.isatools.isacreator.autofilterfield.DefaultAutoFilterCellEditor;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.spreadsheet.Utils;

import javax.swing.table.TableColumn;
import java.util.*;
import java.util.List;

public class SampleSelectorCellEditor extends DefaultAutoFilterCellEditor<SampleInformation> {


    public SampleSelectorCellEditor(Spreadsheet spreadsheet) {
        super(spreadsheet);
    }

    @Override
    protected void updateContent() {
        if (StudyUtils.shouldRunUpdate(getStudyFromSpreadsheet().getStudyId())) {
            try {
                selector.updateContent((List<SampleInformation>) StudyUtils.getStudySampleInformation(getStudyFromSpreadsheet()).values());
            } catch (Exception e) {
                System.out.println("Problem encountered when updating study sample list.");
            }
        }

    }

    @Override
    public void performAdditionalTasks() {
        propagateFactorsToAssay(getText());
    }

    private void propagateFactorsToAssay(String selectedSampleName) {

        TableReferenceObject tableReferenceObject = getStudyFromSpreadsheet().getStudySample().getTableReferenceObject();

        List<FieldObject> recordedFactors = tableReferenceObject.getRecordedFactors();

        Map<String, SampleInformation> allSampleInformation = StudyUtils.getStudySampleInformation(getStudyFromSpreadsheet());

        Spreadsheet studySampleSheet = ((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(getStudyFromSpreadsheet().getStudySample())).getSpreadsheet();

        Map<TableColumn, TableColumn> studySampleSheetFactors = studySampleSheet.getSpreadsheetFunctions().getFactors();

        // add columns and/or values
        for (FieldObject field : recordedFactors) {

            TableColumn factorColumn = null;
            TableColumn unitColumn = null;

            Map<TableColumn, TableColumn> assaySheetFactors = spreadsheet.getSpreadsheetFunctions().getFactors();

            for (TableColumn column : assaySheetFactors.keySet()) {
                if (column.getHeaderValue().equals(field.getFieldName())) {
                    factorColumn = column;
                    unitColumn = assaySheetFactors.get(column);
                }
            }

            if (factorColumn == null) {

                int factorIndex = spreadsheet.getColumnCount();

                FieldObject newFactor;
                if ((newFactor = spreadsheet.getTableReferenceObject().getFieldByName(field.getFieldName())) == null) {
                    newFactor = new FieldObject(factorIndex,
                            field.getFieldName(), field.getDescription(), field.getDatatype(),
                            field.getDefaultVal(), field.isRequired(), field.isAcceptsMultipleValues(), field.isAcceptsFileLocations());
                }

                spreadsheet.getSpreadsheetFunctions().addFieldToReferenceObject(newFactor);

                factorColumn = spreadsheet.getSpreadsheetFunctions().addColumn(newFactor.getFieldName(), newFactor.isRequired());

                if (field.getDatatype() == DataTypes.STRING) {

                    FieldObject unitFo = new FieldObject(spreadsheet.getColumnCount(),
                            "Unit", "Unit for definition of value",
                            DataTypes.ONTOLOGY_TERM, "", false, false,
                            false);

                    spreadsheet.getSpreadsheetFunctions().addFieldToReferenceObject(unitFo);

                    unitColumn = spreadsheet.getSpreadsheetFunctions().addColumn("Unit", newFactor.isRequired());

                    spreadsheet.getSpreadsheetFunctions().addColumnToDependencies(factorColumn, unitColumn);

                }

            }

            // Study sample file
            //      1      2        3
            // FV [Dose] Unit FV [Compound]

            // Assay file
            //     3       4          5
            // FV [Dose] Unit FV [Compound]

            // Now we just need to map across since all the columns should be in place
            // We have the TableColumns from the StudySample File
            // We have the TableColumns from the Assay File.

            if (factorColumn != null) {
                for (TableColumn studySampleColumn : studySampleSheetFactors.keySet()) {

                    if (studySampleColumn.getHeaderValue().toString().equals(factorColumn.getHeaderValue().toString())) {
                        // we have the first value

                        if (allSampleInformation.get(selectedSampleName) != null) {
                            Object value = studySampleSheet.getTable().getValueAt(allSampleInformation.get(selectedSampleName).getRowNumber(),
                                    Utils.convertModelIndexToView(studySampleSheet.getTable(), studySampleColumn.getModelIndex()));

                            spreadsheet.getTable().setValueAt(value == null ? "" : value.toString(), currentRow,
                                    Utils.convertModelIndexToView(spreadsheet.getTable(), factorColumn.getModelIndex()));

                            if (studySampleSheetFactors.get(studySampleColumn) != null) {

                                Object unit = studySampleSheet.getTable().getValueAt(allSampleInformation.get(selectedSampleName).getRowNumber(),
                                        Utils.convertModelIndexToView(studySampleSheet.getTable(), studySampleSheetFactors.get(studySampleColumn).getModelIndex()));

                                spreadsheet.getTable().setValueAt(unit == null ? "" : unit.toString(), currentRow,
                                        Utils.convertModelIndexToView(spreadsheet.getTable(), unitColumn.getModelIndex()));
                            }
                        }

                        break;
                    }
                }
            }
        }
    }

    public void instantiateSelectorIfRequired() {
        if (selector == null) {
            try {
                List<SampleInformation> sampleInformation = new ArrayList<SampleInformation>();
                sampleInformation.addAll(StudyUtils.getStudySampleInformation(getStudyFromSpreadsheet()).values());

                selector = new AutoCompleteUI<SampleInformation>(this, sampleInformation, new SampleSelectionListCellRenderer());
                selector.createGUI();
                selector.setLocation(calculateDisplayLocation(currentTable, currentRow, currentColumn));
            } catch (Exception e) {
                System.out.println("Study samples haven't been updated yet.");
            }
        }

        updateContent();
    }
}
