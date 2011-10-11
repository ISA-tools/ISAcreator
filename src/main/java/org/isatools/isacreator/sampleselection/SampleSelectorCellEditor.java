package org.isatools.isacreator.sampleselection;

import org.isatools.isacreator.apiutils.StudyUtils;
import org.isatools.isacreator.autofilterfield.AutoCompleteUI;
import org.isatools.isacreator.autofilterfield.DefaultAutoFilterCellEditor;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
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
        if (StudyUtils.shouldRunUpdate(study.getStudyId())) {
            try {
                selector.updateContent(StudyUtils.getStudySampleInformation(study).values());
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

        TableReferenceObject tableReferenceObject = study.getStudySample().getTableReferenceObject();

        List<FieldObject> recordedFactors = tableReferenceObject.getRecordedFactors();

        Map<String, SampleInformation> allSampleInformation = StudyUtils.getStudySampleInformation(study);

        Spreadsheet studySampleSheet = study.getStudySample().getSpreadsheetUI().getTable();

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

                factorColumn = spreadsheet.getSpreadsheetFunctions().addColumn(newFactor.getFieldName());

                if (field.getDatatype() == DataTypes.STRING) {

                    FieldObject unitFo = new FieldObject(spreadsheet.getColumnCount(),
                            "Unit", "Unit for definition of value",
                            DataTypes.ONTOLOGY_TERM, "", false, false,
                            false);

                    spreadsheet.getSpreadsheetFunctions().addFieldToReferenceObject(unitFo);

                    unitColumn = spreadsheet.getSpreadsheetFunctions().addColumn("Unit");

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
                selector = new AutoCompleteUI<SampleInformation>(this, StudyUtils.getStudySampleInformation(study).values(), new SampleSelectionListCellRenderer());
                selector.createGUI();
                selector.setLocation(calculateDisplayLocation(currentTable, currentRow, currentColumn));
            } catch (Exception e) {
                System.out.println("Study samples haven't been updated yet.");
            }
        }

        updateContent();
    }
}
