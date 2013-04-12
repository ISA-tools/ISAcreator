/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
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
package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.visualization.glyphbasedworkflow.WorkflowGraphCreator;
import org.isatools.isacreator.visualization.workflowvisualization.WorkflowVisualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 11/02/2011
 *         Time: 10:03
 */
public class SpreadsheetPopupMenus {

    private Spreadsheet spreadsheet;

    public SpreadsheetPopupMenus(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    /**
     * Popup menu supplies the main form of menu in the draw tool
     *
     * @param jc         the component the popup menu is to be added to
     * @param x          horizontal position where the popup position should be
     * @param y          vertical position for the location of the popup menu.
     * @param columnName -> name of column where the popup was called.
     */
    public void popupMenu(JComponent jc, final int x, final int y, String columnName) {
        final JPopupMenu popup = new JPopupMenu("Utilities");
        popup.setLightWeightPopupEnabled(false);
        popup.setBackground(new Color(0, 104, 56, 50));

        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.undoManager.undo();
            }
        });

        undo.setEnabled(spreadsheet.undoManager.canUndo());


        JMenuItem redo = new JMenuItem("Redo");
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.undoManager.redo();
            }
        });

        redo.setEnabled(spreadsheet.undoManager.canRedo());


        JMenu addRow = new JMenu("Add Row(s)");

        JMenuItem addRowsAtEndOfTable = new JMenuItem("Add rows to end of table");
        addRowsAtEndOfTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.showMultipleRowsGUI();
            }
        });

        JMenuItem addRowsBeforeSelectedRow = new JMenuItem("Add row before this row");
        addRowsBeforeSelectedRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.spreadsheetFunctions.insertRowInPosition(spreadsheet.getTable().getSelectedRow());
            }
        });

        addRow.add(addRowsBeforeSelectedRow);
        addRow.add(addRowsAtEndOfTable);

        JMenuItem deleteRow = new JMenuItem("Remove Row(s)");
        deleteRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = spreadsheet.getTable().getSelectedRows();
                spreadsheet.spreadsheetFunctions.deleteRow(selectedRows);
            }
        });

        JMenu addColumn = new JMenu("Add Column");


        JMenuItem addSampleName = new JMenuItem("Add Sample Name");
        addSampleName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                popup.setVisible(false);

                FieldObject fo = new FieldObject(spreadsheet.getTable().getColumnCount(),
                        "Sample Name", "The name of the sample being used",
                        DataTypes.STRING, "", false, false, false);

                spreadsheet.spreadsheetFunctions.addFieldToReferenceObject(fo);

                spreadsheet.spreadsheetFunctions.addColumnAfterPosition("Sample Name", null, fo.isRequired(), -1);
            }
        });

        JMenuItem addMaterialType = new JMenuItem("Add Material Type");
        addMaterialType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                popup.setVisible(false);


                FieldObject fo = new FieldObject(spreadsheet.getTable().getColumnCount(),
                        "Material Type", "The type of material used for analysis in this assay",
                        DataTypes.ONTOLOGY_TERM, "", false, false, false);

                spreadsheet.spreadsheetFunctions.addFieldToReferenceObject(fo);

                spreadsheet.spreadsheetFunctions.addColumnAfterPosition("Material Type", null, fo.isRequired(), -1);
            }
        });


        JMenuItem addCharacteristic = new JMenuItem("Add Characteristic");
        addCharacteristic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                spreadsheet.showAddColumnsGUI(AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);
            }
        });

        JMenuItem addFactor = new JMenuItem("Add Factor");
        addFactor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                spreadsheet.showAddColumnsGUI(AddColumnGUI.ADD_FACTOR_COLUMN);
            }
        });

        JMenuItem addProtocol = new JMenuItem("Add Protocol");
        addProtocol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);

                FieldObject fo = new FieldObject(spreadsheet.getTable().getColumnCount(),
                        "Protocol REF", "Protocol used for experiment",
                        DataTypes.LIST, "", false, false, false);


                fo.setFieldList(spreadsheet.getStudyDataEntryEnvironment().getProtocolNames());

                spreadsheet.spreadsheetFunctions.addFieldToReferenceObject(fo);

                spreadsheet.spreadsheetFunctions.addColumnAfterPosition("Protocol REF", null, fo.isRequired(), -1);
            }
        });

        JMenuItem addParameter = new JMenuItem("Add Parameter");
        addParameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                spreadsheet.showAddColumnsGUI(AddColumnGUI.ADD_PARAMETER_COLUMN);
            }
        });

        JMenuItem addComment = new JMenuItem("Add Comment");
        addComment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                spreadsheet.showAddColumnsGUI(AddColumnGUI.ADD_COMMENT_COLUMN);
            }
        });

        JMenuItem addDate = new JMenuItem("Add Date");
        addDate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);

                FieldObject fo = new FieldObject(spreadsheet.getTable().getColumnCount(),
                        "Date", "Date field", DataTypes.DATE, "", false,
                        false, false);

                spreadsheet.spreadsheetFunctions.addFieldToReferenceObject(fo);

                spreadsheet.spreadsheetFunctions.addColumnAfterPosition("Date", "", fo.isRequired(), -1);
            }
        });

        JMenuItem addPerformer = new JMenuItem("Add Performer");
        addPerformer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);

                FieldObject fo = new FieldObject(spreadsheet.getTable().getColumnCount(),
                        "Performer",
                        "Performer of this hybridisation/sample preparation",
                        DataTypes.STRING, "", false, false, false);

                spreadsheet.spreadsheetFunctions.addFieldToReferenceObject(fo);

                spreadsheet.spreadsheetFunctions.addColumnAfterPosition("Performer", null, fo.isRequired(), -1);
            }
        });


        addColumn.add(addSampleName);
        addColumn.add(addMaterialType);

        if (spreadsheet.spreadsheetFunctions.checkColumnExists("Material Type")) {
            addMaterialType.setEnabled(false);
        }

        final String[] toRemove = new String[]{null};

        final TableReferenceObject tro = spreadsheet.getTableReferenceObject();

        addMissingFields(popup, addColumn, toRemove, tro);

        addColumn.add(new JSeparator());

        addColumn.add(addCharacteristic);
        addColumn.add(addFactor);

        if (columnName.toLowerCase().contains("protocol")) {
            addColumn.add(addParameter);
        }

        addCheckAndAddProtocol(columnName, addColumn, addProtocol);

        addColumn.add(new JSeparator());
        addColumn.add(addComment);

        if (columnName.toLowerCase().contains("protocol")) {
            addColumn.add(addDate);
            addColumn.add(addPerformer);
        }

        JMenuItem deleteColumn = new JMenuItem("Remove Column");
        deleteColumn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(spreadsheet.getTable().getSelectedColumns().length > 1)) {
                    spreadsheet.spreadsheetFunctions.deleteColumn(spreadsheet.getTable().getSelectedColumn());
                } else {
                    spreadsheet.showColumnErrorMessage();
                }
            }
        });


        JMenu unhideColumns = new JMenu("Add previously removed column(s)");
        if (spreadsheet.hiddenColumns.size() > 0) {

            for (final String hiddenColumn : spreadsheet.hiddenColumns) {
                JMenuItem item = new JMenuItem(hiddenColumn);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        spreadsheet.spreadsheetFunctions.addColumnAfterPosition(hiddenColumn, null, false, -1);
                        spreadsheet.hiddenColumns.remove(hiddenColumn);
                    }
                });
                unhideColumns.add(item);
            }
        }

        JMenuItem copyColumnDown = new JMenuItem("Copy Column Downwards");
        copyColumnDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.spreadsheetFunctions.copyColumnDownwards(spreadsheet.getTable().getSelectedRow(),
                        spreadsheet.getTable().getSelectedColumn());
            }
        });

        JMenuItem copyRowDown = new JMenuItem("Copy Row Downwards");
        copyRowDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.spreadsheetFunctions.copyRowDownwards(spreadsheet.getTable().getSelectedRow());
            }
        });

        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.spreadsheetFunctions.copy();
            }
        });

        JMenuItem paste = new JMenuItem("Paste");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.spreadsheetFunctions.paste(-1, -1, true);
            }
        });


        JMenuItem cut = new JMenuItem("Cut");
        cut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.spreadsheetFunctions.doCopy(true, null);
            }
        });

        JMenuItem multipleSort = new JMenuItem("Perform Multiple Sort");
        multipleSort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spreadsheet.showMultipleColumnSortGUI();
            }
        });


        JMenuItem clearField = new JMenuItem("Clear Field");
        clearField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                spreadsheet.spreadsheetFunctions.clearCells(spreadsheet.getTable().getSelectedRow(), spreadsheet.getTable().getSelectedColumn(), spreadsheet.getTable().getSelectedRow(), spreadsheet.getTable().getSelectedColumn());
            }
        });

        JMenu highlightGroups = new JMenu("Highlight groups");
        createMenuItemsForHighlighter(highlightGroups);

        JMenuItem removeHighlight = new JMenuItem("Remove Highlight");
        removeHighlight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (spreadsheet.highlightActive) {
                    spreadsheet.setRowsToDefaultColor();
                }
            }
        });

        JMenuItem highlightRequiredFields = new JMenuItem("Highlight Required Fields");
        highlightRequiredFields.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                spreadsheet.highlightRequiredFields();
            }
        });

        JMenuItem viewWorkflowForAssays = new JMenuItem("View workflow for this assay");
        viewWorkflowForAssays.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                WorkflowGraphCreator workflowGraphCreator = new WorkflowGraphCreator();
                File graphMLFile = workflowGraphCreator.createGraphMLFileForExperiment(true);
                new WorkflowVisualization(graphMLFile).createGUI();
            }
        });

        JMenuItem viewWorkflowForSelectedSamples = new JMenuItem("View workflow for selected samples");
        viewWorkflowForSelectedSamples.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                WorkflowGraphCreator workflowGraphCreator = new WorkflowGraphCreator();
                Set<String> selectedSamples = spreadsheet.getSpreadsheetFunctions().getValuesInSelectedRowsForColumn("Sample Name");
                File graphMLFile = workflowGraphCreator.createGraphMLFileForExperiment(true, selectedSamples);
                new WorkflowVisualization(graphMLFile).createGUI();
            }
        });

        JMenuItem mapFilesToDirectory = new JMenuItem("Resolve file names");
        mapFilesToDirectory.setToolTipText("<html>" +
                "<strong>resolve file names</strong>" +
                "<p>you can select a directory and <strong>ISAcreator</strong> will resolve the correct,</p> " +
                "<p>absolute file location (where possible!)</p>" +
                "</html>");

        mapFilesToDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                spreadsheet.spreadsheetFunctions.resolveFileLocations();
            }
        });

        if (spreadsheet.getTable().getSelectedRows().length == 0) {
            deleteRow.setEnabled(false);
        }

        if (spreadsheet.getTable().getSelectedColumns().length == 0) {
            deleteColumn.setEnabled(false);
        }

        if (spreadsheet.getTable().getRowCount() == 0) {
            multipleSort.setEnabled(false);
        }

        popup.add(undo);
        popup.add(redo);
        popup.add(new JSeparator());
        popup.add(addRow);
        popup.add(deleteRow);

        popup.add(new JSeparator());
        checkAndAddIfShouldAddColumn(columnName, popup, addColumn);

        if (spreadsheet.hiddenColumns.size() > 0) {
            popup.add(unhideColumns);
        }
        popup.add(deleteColumn);
        popup.add(new JSeparator());
        popup.add(copyColumnDown);
        popup.add(copyRowDown);
        popup.add(new JSeparator());
        popup.add(copy);
        popup.add(paste);
        popup.add(cut);
        popup.add(new JSeparator());
        popup.add(multipleSort);
        popup.add(new JSeparator());
        popup.add(clearField);
        popup.add(new JSeparator());

        // only show the resolve file names item if the current column is a file.
        if (spreadsheet.getTableReferenceObject().acceptsFileLocations(columnName)) {
            popup.add(mapFilesToDirectory);
            popup.add(new JSeparator());
        }

        popup.add(highlightGroups);
        popup.add(highlightRequiredFields);
        if (spreadsheet.highlightActive) {
            popup.add(removeHighlight);
        }

        if (isSpreadsheetSampleDefinitions()) {
            popup.add(new JSeparator());
            popup.add(viewWorkflowForAssays);
            popup.add(viewWorkflowForSelectedSamples);
        }
        popup.show(jc, x, y);
    }

    private void checkAndAddIfShouldAddColumn(String columnName, JPopupMenu popup, JMenu addColumn) {
        if (isNotStandardFieldType(columnName)
                && spreadsheet.getStudyDataEntryEnvironment() != null) {
            popup.add(addColumn);
        }
    }

    private void addCheckAndAddProtocol(String columnName, JMenu addColumn, JMenuItem addProtocol) {
        if (isNotStandardFieldType(columnName)) {
            addColumn.add(addProtocol);
        }
    }

    private boolean isNotStandardFieldType(String columnName) {
        columnName = columnName.toLowerCase();
        return !columnName.contains("characteristic") &&
                !columnName.contains("unit") &&
                !columnName.contains("factor") &&
                !columnName.contains("date") &&
                !columnName.contains("performer") &&
                !columnName.contains("provider") &&
                !columnName.contains("comment") &&
                !columnName.contains("material type");
    }

    private void addMissingFields(final JPopupMenu popup, JMenu addColumn, final String[] toRemove, final TableReferenceObject tro) {
        if (spreadsheet.getTableReferenceObject().getMissingFields() != null && tro.getMissingFields().size() != 0) {
            for (final String missingField : tro.getMissingFields().keySet()) {
                if (!spreadsheet.spreadsheetFunctions.checkColumnExists(missingField)) {
                    JMenuItem item = new JMenuItem(missingField);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            popup.setVisible(false);

                            FieldObject missingFieldToAdd = tro.getMissingFields().get(missingField);
                            spreadsheet.spreadsheetFunctions.addFieldToReferenceObject(missingFieldToAdd);

                            spreadsheet.spreadsheetFunctions.addColumnAfterPosition(missingField, "", missingFieldToAdd.isRequired(), -1);
                            toRemove[0] = missingField;

                        }
                    });
                    addColumn.add(item);
                }
            }
            if (toRemove[0] != null) {
                tro.getMissingFields().remove(toRemove[0]);
            }
        }
    }

    /**
     * Creates a popup when the user has dragged across cells which allows the user to autofill the columns
     * (based on values dragged from), copy the selection, or clear the fields.
     *
     * @param jc - Parent Component to display popup in.
     * @param x  - x position to display popup in
     * @param y  - y position for where to display popup.
     */
    public void dragCellPopupMenu(JComponent jc, final int x, final int y) {
        final JPopupMenu popup = new JPopupMenu("Utilities");
        popup.setLightWeightPopupEnabled(false);
        popup.setBackground(new Color(0, 104, 56, 50));
        jc.add(popup);

        JMenuItem autoIncrementCells = new JMenuItem("Autofill");
        autoIncrementCells.setForeground(UIHelper.DARK_GREEN_COLOR);
        autoIncrementCells.setBackground(UIHelper.BG_COLOR);

        autoIncrementCells.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (spreadsheet.getTable().getValueAt(spreadsheet.startRow, spreadsheet.startCol) != null) {
                    int endRow = spreadsheet.getTable().rowAtPoint(new Point(x, y));

                    String columnName = spreadsheet.getTable().getColumnModel().getColumn(spreadsheet.startCol)
                            .getHeaderValue().toString();

                    if (!columnName.contains(TableReferenceObject.ROW_NO_TEXT)) {

                        String startVal = spreadsheet.getTable().getValueAt(spreadsheet.startRow, spreadsheet.startCol).toString();
                        Pattern p = Pattern.compile("[[0-9]*.]*[0-9]+");
                        Matcher m = p.matcher(startVal);
                        int finalStartIndex = -1;
                        int finalStopIndex = -1;

                        while (m.find()) {
                            finalStartIndex = m.start();
                            finalStopIndex = m.end();
                        }

                        if (finalStartIndex != -1 && !isOntologyTerm(startVal, columnName)) {

                            String startVal2 = "";
                            int finalStartIndex2 = -1;
                            int finalStopIndex2 = -1;

                            if (spreadsheet.getTable().getValueAt(spreadsheet.startRow + 1, spreadsheet.startCol) != null) {
                                // we have a 2nd value to determine the increment with
                                startVal2 = spreadsheet.getTable().getValueAt(spreadsheet.startRow + 1,
                                        spreadsheet.startCol).toString();

                                if (!startVal2.equals("")) {
                                    m = p.matcher(startVal2);

                                    while (m.find()) {
                                        finalStartIndex2 = m.start();
                                        finalStopIndex2 = m.end();
                                    }
                                }
                            }

                            String strippedStartVal = startVal.substring(0,
                                    finalStartIndex);

                            boolean unconventionalFormatting = false;

                            double valToIncrement;
                            try {
                                valToIncrement = Double.valueOf(startVal.substring(
                                        finalStartIndex, finalStopIndex));
                            } catch (NumberFormatException nfe) {
                                // in the event where there is a strange value e.g. 1.254.213, then we need to take the last value as the incrementer!
                                valToIncrement = Double.valueOf(startVal.substring(startVal.lastIndexOf(".") + 1));
                                strippedStartVal = startVal.substring(0, startVal.lastIndexOf(".") + 1);
                                unconventionalFormatting = true;
                            }
                            double difference = 0;

                            if (finalStartIndex2 != -1) {

                                double valToIncrement2;
                                try {
                                    valToIncrement2 = Double.valueOf(startVal2.substring(
                                            finalStartIndex2, finalStopIndex2));
                                } catch (NumberFormatException nfe) {
                                    // in the event where there is a strange value e.g. 1.254.213, then we need to take the last value as the incrementer!
                                    valToIncrement2 = Double.valueOf(startVal2.substring(startVal2.lastIndexOf(".") + 1));
                                }

                                difference = valToIncrement2 -
                                        valToIncrement;
                                valToIncrement = valToIncrement2;
                            } else {
                                valToIncrement = valToIncrement + 1;
                            }

                            String autofillContents = "";

                            for (int row = spreadsheet.startRow + 1; row <= endRow; row++) {
                                String strValOfInc = String.valueOf(valToIncrement);
                                strValOfInc = strValOfInc.endsWith(".0") ? strValOfInc.substring(0, strValOfInc.length() - 2) : strValOfInc;

                                autofillContents += strippedStartVal + strValOfInc;
                                if (row != endRow) {
                                    autofillContents += "\n";
                                }

                                if (finalStartIndex2 != -1) {
                                    valToIncrement += difference;
                                    if (valToIncrement < 0 && unconventionalFormatting) {
                                        // positify it! :o) this is the behaviour in excel
                                        valToIncrement *= -1;
                                        difference *= -1;
                                    }
                                    valToIncrement = Utils.formatDoubleValue(startVal, valToIncrement);
                                } else {
                                    valToIncrement++;
                                }
                            }
                            spreadsheet.spreadsheetFunctions.putStringOnClipboard(autofillContents);
                            spreadsheet.spreadsheetFunctions.paste(spreadsheet.startRow + 1, spreadsheet.startCol, true);
                        } else {
                            // fill as string
                            String autofillContents = "";

                            for (int row = spreadsheet.startRow + 1; row <= endRow; row++) {
                                autofillContents += startVal;
                                if (row != endRow) {
                                    autofillContents += "\n";
                                }
                            }

                            spreadsheet.spreadsheetFunctions.putStringOnClipboard(autofillContents);
                            spreadsheet.spreadsheetFunctions.paste(spreadsheet.startRow + 1, spreadsheet.startCol, true);

                        }
                    }
                }
            }
        });

        JMenuItem mapFilesToDirectory = new JMenuItem("Resolve file names");
        mapFilesToDirectory.setToolTipText("<html>" +
                "<strong>resolve file names</strong>" +
                "<p>you can select a directory and <strong>ISAcreator</strong> will resolve the correct,</p> " +
                "<p>absolute file location (where possible!)</p>" +
                "</html>");

        mapFilesToDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                spreadsheet.spreadsheetFunctions.resolveFileLocations();
            }
        });

        JMenuItem copyData = new JMenuItem("Copy selection");
        copyData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                spreadsheet.spreadsheetFunctions.copy();
            }
        });

        JMenuItem clearData = new JMenuItem("Clear fields");
        clearData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!spreadsheet.getTable().getColumnModel().getColumn(spreadsheet.startCol)
                        .getHeaderValue().toString().contains("Row")) {

                    spreadsheet.spreadsheetFunctions.clearCells(spreadsheet.startRow, spreadsheet.startCol,
                            spreadsheet.getTable().rowAtPoint(new Point(x, y)),
                            spreadsheet.getTable().columnAtPoint(new Point(x, y)));
                }
            }
        });

        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                popup.setVisible(false);
            }
        });

        JMenuItem viewWorkflowForSelectedSamples = new JMenuItem("View workflow for selected samples");
        viewWorkflowForSelectedSamples.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                WorkflowGraphCreator workflowGraphCreator = new WorkflowGraphCreator();
                Set<String> selectedSamples = spreadsheet.getSpreadsheetFunctions().getValuesInSelectedRowsForColumn("Sample Name");
                File graphMLFile = workflowGraphCreator.createGraphMLFileForExperiment(true, selectedSamples);
                System.out.println("graph file is " + graphMLFile);
                new WorkflowVisualization(graphMLFile).createGUI();
            }
        });

        popup.add(autoIncrementCells);
        popup.add(new JSeparator());
        popup.add(mapFilesToDirectory);
        popup.add(new JSeparator());
        popup.add(copyData);
        popup.add(clearData);
        if (isSpreadsheetSampleDefinitions()) {
            popup.add(new JSeparator());
            popup.add(viewWorkflowForSelectedSamples);
        }
        popup.add(new JSeparator());
        popup.add(close);



        popup.show(jc, x, y);
    }

    private boolean isSpreadsheetSampleDefinitions() {
        return !spreadsheet.getSpreadsheetTitle().contains("Sample Def");
    }

    /**
     * We only want to stop autocomplete if the column is of type ontology term AND the value currently
     * inside the column is an ontology term, e.g. with the formation (OBI:Intervention Design)
     *
     * @param value      - Value of the field
     * @param columnName - Name of the column being looked at
     * @return - a boolean indicating whether or not the current field should be treated as an ontology term or not
     */
    private boolean isOntologyTerm(String value, String columnName) {
        if (spreadsheet.getTableReferenceObject().getColumnType(columnName) != DataTypes.ONTOLOGY_TERM) {
            if (value.contains(":")) {
                return true;
            }
        }
        return false;
    }


    protected void createMenuItemsForHighlighter(JMenu toAddTo) {
        JMenu characteristicsMenu = new JMenu("Characteristics");
        JMenu factors = new JMenu("Factors");

        Map<String, Set<String>> groupInfo = spreadsheet.getColumnGroups();
        JMenuItem mi;
        for (String group : groupInfo.keySet()) {
            if (group.equals("Normal")) {
                for (final String column : groupInfo.get(group)) {
                    if (!column.equals(TableReferenceObject.ROW_NO_TEXT)) {
                        mi = new JMenuItem(column);
                        mi.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                spreadsheet.highlight(column, true, false);
                            }
                        });
                        toAddTo.add(mi);
                    }
                }
            } else if (group.equals("Characteristics")) {

                mi = new JMenuItem("All Characteristics");
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        spreadsheet.highlight("Characteristics", false, false);
                    }
                });
                characteristicsMenu.add(mi);
                for (final String column : groupInfo.get(group)) {
                    mi = new JMenuItem(column);
                    mi.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            spreadsheet.highlight(column, true, false);
                        }
                    });
                    characteristicsMenu.add(mi);
                }

                toAddTo.add(characteristicsMenu);

            } else if (group.equals("Factor")) {

                mi = new JMenuItem("All Factors");
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        spreadsheet.highlight("Factor Value", false, false);
                    }
                });
                factors.add(mi);
                for (final String column : groupInfo.get(group)) {
                    mi = new JMenuItem(column);
                    mi.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            spreadsheet.highlight(column, true, false);
                        }
                    });
                    factors.add(mi);
                }
                toAddTo.add(factors);
            }
        }
    }

    public void setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }
}
