/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 �The contents of this file are subject to the CPAL version 1.0 (the �License�);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an �AS IS� basis,
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
import org.isatools.isacreator.configuration.TableFieldObject;
import org.isatools.isacreator.filterablelistselector.FilterableListCellEditor;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.utils.GeneralUtils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 11/02/2011
 *         Time: 10:39
 */
public class SpreadsheetFunctions {

    private Spreadsheet spreadsheet;

    public SpreadsheetFunctions(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    /**
     * Substitutes headers - replaces a column name with another one
     *
     * @param prevHeaderName - Previous column header
     * @param newHeaderName  - New column header
     */
    public void substituteHeaderNames(String prevHeaderName, String newHeaderName) {
        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().toString().equals(prevHeaderName)) {
                col.setHeaderValue(newHeaderName);
            }
        }

        spreadsheet.getTable().addNotify();
    }

    /**
     * Substitutes a given value prevTerm with a new value newTerm in a column whose header
     * is colName
     *
     * @param colName  - Column to be searched in.
     * @param prevTerm - the term to replace
     * @param newTerm  - the new term to be used instead.
     */
    public void susbstituteTermsInColumn(String colName, String prevTerm, String newTerm) {

        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().toString().equals(colName)) {
                int colIndex = col.getModelIndex();

                for (int i = 0; i < spreadsheet.spreadsheetModel.getRowCount(); i++) {
                    // safety precaution to finalise any cells. otherwise their value would be missed!
                    if (spreadsheet.getTable().getCellEditor(i, colIndex) != null) {
                        spreadsheet.getTable().getCellEditor(i, colIndex).stopCellEditing();
                    }

                    if (spreadsheet.spreadsheetModel.getValueAt(i, colIndex) != null && spreadsheet.spreadsheetModel.getValueAt(i, colIndex).toString()
                            .equals(prevTerm)) {
                        spreadsheet.spreadsheetModel.setValueAt(newTerm, i, colIndex);
                    }
                }
            }
        }
    }

    /**
     * Copy the contents of a row downwards.
     *
     * @param rowId - row to copy from
     */
    public void copyRowDownwards(int rowId) {
        // if there is a row selected
        if (rowId > -1) {
            String rowRepresentationAsString = getRowAsString(rowId);
            StringBuffer totalRepresentation = new StringBuffer("");
            int numRows = spreadsheet.spreadsheetModel.getRowCount() - rowId;

            for (int i = 0; i < numRows; i++) {
                totalRepresentation.append(rowRepresentationAsString).append(System.getProperty("line.separator"));
            }

            int[] rows = Utils.getArrayOfVals(rowId, spreadsheet.getTable().getRowCount() - 1);
            int[] cols = Utils.getArrayOfVals(1, spreadsheet.getTable().getColumnCount() - 1);

            SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(rows, Utils.convertSelectedColumnsToModelIndices(spreadsheet.getTable(), cols));

            spreadsheet.spreadsheetHistory.add(affectedRange);
            spreadsheet.spreadsheetModel.extendedFromString(totalRepresentation.toString(), '\t', affectedRange);

        }

    }

    private String getRowAsString(int rowId) {
        StringBuffer rowRepresentation = new StringBuffer("");
        for (int col = 1; col < spreadsheet.getTable().getColumnCount(); col++) {
            if (col != spreadsheet.getTable().getColumnCount() - 1) {
                rowRepresentation.append(spreadsheet.getTable().getValueAt(rowId, col)).append("\t");
            }
        }

        return rowRepresentation.toString();
    }

    public void performMultipleSort(int primaryColumn,
                                    int secondaryColumn,
                                    boolean primaryAscending,
                                    boolean secondaryAscending) {
        SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(Utils.getArrayOfVals(0, spreadsheet.getTable().getRowCount() - 1), Utils.convertSelectedColumnsToModelIndices(spreadsheet.getTable(), Utils.getArrayOfVals(1, spreadsheet.getTable().getColumnCount() - 1)));
        spreadsheet.spreadsheetHistory.add(affectedRange);
        spreadsheet.setRowsToDefaultColor();
        spreadsheet.spreadsheetModel.sort(affectedRange, spreadsheet.getTable().columnViewIndextoModel(primaryColumn), spreadsheet.getTable().columnViewIndextoModel(secondaryColumn), primaryAscending, secondaryAscending);
    }

    /**
     * Method returns a Set of all the files defined in a spreadsheet. These locations are used to zip up the data files
     * in the ISArchive for submission to the index.
     *
     * @return Set of files defined in the spreadsheet
     *         <p/>
     *         todo move to spreadsheetutils class
     */
    public Set<String> getFilesDefinedInTable() {
        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();
        Set<String> files = new HashSet<String>();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (spreadsheet.getTableReferenceObject().acceptsFileLocations(tc.getHeaderValue().toString())) {
                int colIndex = Utils.convertModelIndexToView(spreadsheet.getTable(), tc.getModelIndex());

                for (int row = 0; row < spreadsheet.getTable().getRowCount(); row++) {
                    String s = (spreadsheet.getTable().getValueAt(row, colIndex) == null) ? ""
                            : spreadsheet.getTable().getValueAt(row,
                            colIndex).toString();

                    if (s != null && !s.trim().equals("")) {
                        files.add(s);
                    }
                }
            }
        }

        return files;
    }

    /**
     * Exports current Table into a CSV or TAB file depending on separator chosen.
     *
     * @param f                  File to be written to
     * @param separator          -> "\t" for tab separation or "," for CSV
     * @param removeEmptyColumns - should empty columns be removed from the submission?
     * @throws java.io.FileNotFoundException - never thrown since the file is created if it doesn't exist, and over written if
     *                                       it already exists
     *                                       <p/>
     *                                       todo Move this code into a utils class
     */
    public boolean exportTable(File f, String separator, boolean removeEmptyColumns)
            throws FileNotFoundException {
        PrintStream ps = new PrintStream(f);

        Set<TableColumn> emptyColumns = new HashSet<TableColumn>();

        Map<String, OntologyObject> history = spreadsheet.getDataEntryEnv().getUserHistory();

        for (int col = 1; col < spreadsheet.getTable().getColumnCount(); col++) {
            TableColumn tc = spreadsheet.getTable().getColumnModel().getColumn(col);
            // only hide columns which are empty and that are not necessarily required!
            if (removeEmptyColumns && checkIsEmpty(tc) && !spreadsheet.getTableReferenceObject().isRequired(tc.getHeaderValue().toString())) {
                emptyColumns.add(tc);
            } else {
                if (col == 1) {
                    ps.print("\"" + tc.getHeaderValue() + "\"");
                } else {
                    ps.print(separator + "\"" + tc.getHeaderValue() + "\"");
                }

                if (tc.getCellEditor() instanceof OntologyCellEditor ||
                        tc.getHeaderValue().toString().equalsIgnoreCase("unit")) {
                    ps.print(separator + "\"Term Source REF\"");
                    ps.print(separator + "\"Term Accession Number\"");
                }
            }
        }

        ps.print("\n");

        // write out each column
        for (int rows = 0; rows < spreadsheet.getTable().getRowCount(); rows++) {
            String rowInfo = "";

            for (int cols = 1; cols < spreadsheet.getTable().getColumnCount(); cols++) {
                // the value to be output to the field
                TableColumn tc = spreadsheet.getTable().getColumnModel().getColumn(cols);

                if (!emptyColumns.contains(tc)) {
                    String val;

                    // where the term came from if there is an ontology term.
                    String source = "";
                    String toAdd;

                    if (spreadsheet.getTable().getValueAt(rows, cols) != null) {
                        val = spreadsheet.getTable().getValueAt(rows, cols).toString();

                        if (tc.getCellEditor() instanceof OntologyCellEditor ||
                                tc.getHeaderValue().toString()
                                        .equalsIgnoreCase("unit")) {


                            String termAccession = "";

                            if (!GeneralUtils.isValueURL(val)) {
                                OntologyObject oo = history.get(val);

                                if (oo != null) {
                                    termAccession = oo.getTermAccession();
                                }

                                if (val.contains(":")) {
                                    source = val.substring(0, val.indexOf(":"));
                                    val = val.substring(val.indexOf(":") + 1);
                                }
                            }

                            toAdd = "\"" + val + "\"" + separator + "\"" + source + "\"" + separator +
                                    "\"" + termAccession + "\"";
                        } else {
                            toAdd = "\"" + val + "\"";
                        }
                    } else {
                        if (tc.getCellEditor() instanceof OntologyCellEditor ||
                                tc.getHeaderValue().toString()
                                        .equalsIgnoreCase("unit")) {
                            // add triple separated value for term : source : accession triple
                            toAdd = "\"\"" + separator + "\"\"" + separator + "\"\"";
                        } else {
                            toAdd = "\"\"";
                        }
                    }

                    // only add the row separator if we are not adding the last column!
                    if (cols == spreadsheet.getTable().getColumnCount() - 1) {
                        rowInfo += toAdd;
                    } else {
                        rowInfo += (toAdd + separator);
                    }
                }
            }

            if (rowInfo.length() > 0) {
                ps.println(rowInfo);
            }
        }

        ps.close();

        return spreadsheet.checkTableColumnOrderBad(f.getName());
    }

    private boolean checkIsEmpty(TableColumn tc) {
        int colIndex = tc.getModelIndex();
        int viewIndex = Utils.convertModelIndexToView(spreadsheet.getTable(), colIndex);
        for (int rowNo = 0; rowNo < spreadsheet.getTable().getRowCount(); rowNo++) {
            if (spreadsheet.getTable().getValueAt(rowNo, viewIndex) != null && !spreadsheet.getTable().getValueAt(rowNo, viewIndex).equals("")) {
                return false;
            }

        }
        return true;
    }

    protected void resolveFileLocations() {
        int[] selectedRows = spreadsheet.getTable().getSelectedRows();
        int selectedColumn = spreadsheet.getTable().getSelectedColumn();

        JFileChooser fileLocChooser = new JFileChooser();
        fileLocChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileLocChooser.setDialogTitle("Select directory to search through");
        fileLocChooser.setApproveButtonText("Select directory");

        if (fileLocChooser.showOpenDialog(spreadsheet) == JFileChooser.APPROVE_OPTION) {
            File dir = fileLocChooser.getSelectedFile();

            String[] files = new String[selectedRows.length];

            for (int row = 0; row < selectedRows.length; row++) {
                files[row] = spreadsheet.getTable().getValueAt(selectedRows[row], selectedColumn).toString();
            }

            FileLocationMapperUtil fmu = new FileLocationMapperUtil();
            Map<String, String> result = fmu.findProperFileLocations(files, dir);

            for (int selectedRow : selectedRows) {
                String candidateVal = spreadsheet.getTable().getValueAt(selectedRow, selectedColumn).toString();
                if (result.keySet().contains(candidateVal)) {
                    spreadsheet.getTable().setValueAt(result.get(candidateVal), selectedRow, selectedColumn);
                }
            }
        }

        // otherwise, do nothing
    }

    /**
     * Perfroms a clipboard function of COPY
     */
    public void copy() {
        doCopy(false, null); //sets isCut to false
    }

    /**
     * Perfroms a clipboard function of cutting (COPY + CLEAR)
     */
    public void cut() {
        doCopy(true, null); //sets isCut to true
    }

    /**
     * Performs a clipboard function of pasting
     *
     * @param startRow       - Where pasting is to start. If from selected row, supply -1 as a parameter
     * @param startCol       - Where pasting is to start. If from selected column, supply -1 as a parameter
     * @param storeInHistory - variable to determine whether or not to store the paste action in the history
     */
    public void paste(int startRow, int startCol, boolean storeInHistory) {
        CellEditor editor = spreadsheet.getTable().getCellEditor();
        if (editor != null) {
            editor.cancelCellEditing();
        }

        startRow = (startRow == -1) ? spreadsheet.getTable().getSelectedRow() : startRow;
        startCol = (startCol == -1) ? spreadsheet.getTable().getSelectedColumn() : startCol;

        //checks if anything is selected
        if (startRow != -1) {
            try {
                String trstring = (String) (spreadsheet.system.getContents(this).getTransferData(DataFlavor.stringFlavor));

                SpreadsheetCellPoint size = SpreadsheetModel.getSize(trstring, '\t');


                int rowSpaceToFill = (size.getRow());

                int endRow = Math.min(spreadsheet.getTable().getRowCount() - 1, (startRow + size.getRow()) - 1);
                int endCol = Math.min(spreadsheet.getTable().getColumnCount() - 1, (startCol + size.getCol()) - 1);

                int colSpaceToFill = (endCol - startCol) + 1;

                int[] colRange = new int[colSpaceToFill];
                int[] rowRange = new int[rowSpaceToFill];
                if (colSpaceToFill > 0) {

                    colRange = new int[colSpaceToFill];
                    int startVal = 0;
                    for (int val = startCol; val <= endCol; val++) {
                        colRange[startVal] = val;
                        startVal++;
                    }
                }

                if (rowSpaceToFill > 0) {
                    rowRange = new int[rowSpaceToFill];
                    int startVal = 0;
                    for (int val = startRow; val <= endRow; val++) {
                        rowRange[startVal] = val;
                        startVal++;
                    }
                }

                if (colRange.length == 0) {
                    colRange = new int[1];
                    colRange[0] = startCol;
                }

                if (rowRange.length == 0) {
                    rowRange = new int[1];
                    rowRange[0] = startRow;
                }

                SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(rowRange, Utils.convertSelectedColumnsToModelIndices(spreadsheet.getTable(), colRange));

                // add to history
                if (storeInHistory) {
                    spreadsheet.spreadsheetHistory.add(affectedRange);
                }
                spreadsheet.spreadsheetModel.extendedFromString(trstring, '\t', affectedRange);
                spreadsheet.spreadsheetModel.extendedSetSelection(affectedRange);

                spreadsheet.getTable().repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Performs a clipboard function of cut/copy
     *
     * @param isCut true for cut, false for copy
     * @param toUse - the SpreadsheetCellRange representing the values to be pasted into the spreadsheet
     */

    protected void doCopy(boolean isCut, SpreadsheetCellRange toUse) {
        CellEditor editor = spreadsheet.getTable().getCellEditor();
        if (editor != null) {
            editor.cancelCellEditing();
        }
        boolean modifiedCellRange = false;
        if (spreadsheet.getTable().getSelectedRowCount() != 0 || toUse != null) {
            if (toUse == null) {

                toUse = new SpreadsheetCellRange(spreadsheet.getTable().getSelectedRows(), Utils.convertSelectedColumnsToModelIndices(spreadsheet.getTable(), spreadsheet.getTable().getSelectedColumns()));
                modifiedCellRange = true;
            }

            if (isCut && modifiedCellRange) {
                spreadsheet.spreadsheetHistory.add(toUse);
            }

            String str = spreadsheet.spreadsheetModel.extendedToString(toUse, '\t');
            StringSelection stsel = new StringSelection(str);
            spreadsheet.system.setContents(stsel, stsel);

            if (isCut) {
                spreadsheet.spreadsheetModel.clearRange(toUse);
            }
        } else {
            System.out.println("no rows are selected so no copying has taken place.");
        }

    }


    /**
     * Removes a column from the list of dependencies
     *
     * @param col - Column to be removed
     */
    public void removeColumnFromDependencies
    (TableColumn
             col) {
        boolean removingParent = false;
        TableColumn toRemove = null;
        TableColumn parentColumn = null;

        for (TableColumn cp : spreadsheet.columnDependencies.keySet()) {
            if (cp == col) {
                toRemove = cp;
                removingParent = true;
                break;
            }

            for (TableColumn cc : spreadsheet.columnDependencies.get(cp)) {
                if (cc == col) {
                    toRemove = cc;
                    parentColumn = cp;
                    break;
                }
            }
        }


        if (toRemove != null) {
            if (removingParent) {
                //remove column with it's associated dependencies
                spreadsheet.columnDependencies.remove(toRemove);
            } else {
                // remove column from it's parent
                spreadsheet.columnDependencies.get(parentColumn).remove(toRemove);
            }
        } else {
            System.out.println("Dependents on this column not found, so not removing any other columns!");
        }
    }

    /**
     * if a given column has dependent columns, e.g. does a factor column have an associated unit column. if it does,
     * then remove the factor and the unit. this method does that.
     *
     * @param col - column to remove...
     */
    private void removeDependentColumns(TableColumn col) {

        try {
            if (spreadsheet.columnDependencies.containsKey(col)) {
                for (TableColumn tc : spreadsheet.columnDependencies.get(col)) {
                    spreadsheet.curColDelete = Utils.convertModelIndexToView(spreadsheet.getTable(), tc.getModelIndex());
                    removeColumn();
                }
                removeColumnFromDependencies(col);
            }
        } catch (ConcurrentModificationException cme) {
            // ignore this error
        }

    }

    protected void removeColumn() {
        if (spreadsheet.curColDelete != -1) {
            SpreadsheetModel model = (SpreadsheetModel) spreadsheet.getTable().getModel();
            TableColumn col = spreadsheet.getTable().getColumnModel().getColumn(spreadsheet.curColDelete);

            spreadsheet.hiddenColumns.add(col.getHeaderValue().toString());
            deleteColumn(model, col);

            removeDependentColumns(col);
            removeColumnFromDependencies(col);
        }
    }

    /**
     * Remove a column from the spreadsheet.getTable(), delete all the data associated with the column in the model, and keep indices
     * intact by decreasing the index of every column after the one deleted by one to stop fragmentation.
     *
     * @param model          - @see SpreadsheetModel to be acted on
     * @param columnToRemove - @see TableColumn representing column to remove.
     */
    private void deleteColumn(SpreadsheetModel model, TableColumn columnToRemove) {

        int columnModelIndex = columnToRemove.getModelIndex();
        Vector data = model.getDataVector();
        Vector colIds = model.getColumnIdentifiers();
        spreadsheet.getTable().removeColumn(columnToRemove);
        colIds.removeElementAt(columnModelIndex);

        // remove any data present in the column on deletion
        for (Object aData : data) {
            Vector row = (Vector) aData;
            row.removeElementAt(columnModelIndex);
        }

        model.setDataVector(data, colIds);

        // decrease each column index after deleted column by 1 so that indexes can be kept intact.
        Enumeration enumer = spreadsheet.getTable().getColumnModel().getColumns();

        while (enumer.hasMoreElements()) {
            TableColumn c = (TableColumn) enumer.nextElement();

            if (c.getModelIndex() >= columnModelIndex) {
                c.setModelIndex(c.getModelIndex() - 1);
            }
        }
        // update the model
        model.fireTableStructureChanged();
    }

    /**
     * Remove a column from a spreadsheet.getTable() given a column name
     *
     * @param colName - Name of column to be removed.
     */
    public void removeColumnByName(String colName) {
        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().toString().equals(colName)) {
                spreadsheet.curColDelete = Utils.convertModelIndexToView(spreadsheet.getTable(), col.getModelIndex());
                removeColumn();

                removeDependentColumns(col);
            }
        }

        ((SpreadsheetModel) spreadsheet.getTable().getModel()).fireTableStructureChanged();
    }

    /**
     * Remove a row, fire a table data changed event, and then update the row numbers.
     *
     * @param i - row to be removed.
     */
    private void removeRow(int i) {
        //rows.removeElementAt(convertViewRowToModel(i));
        spreadsheet.spreadsheetModel.removeRow(convertViewRowToModel(i));
        spreadsheet.spreadsheetModel.updateRowCount();
    }

    /**
     * Remove multiple rows from a table
     */
    protected void removeRows() {
        if (spreadsheet.rowsToDelete != null && spreadsheet.rowsToDelete.length > 0) {
            for (final int i : Utils.arrayToList(spreadsheet.rowsToDelete)) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        removeRow(i);
                    }
                });
            }
        }

        spreadsheet.getTable().addNotify();

        spreadsheet.currentState = Spreadsheet.DEFAULT_STATE;
    }

    /**
     * Check to see if a column with a given name exists.
     * Result is always false if the column allows multiple values.
     *
     * @param colName name of column to check for.
     * @return true if it exists, false otherwise.
     */
    public boolean checkColumnExists(String colName) {
        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();
        // if the column can be referenced multiple times, then we should return false in this check.
        System.out.println(colName);
        if (colName != null) {
            if (!spreadsheet.getTableReferenceObject().acceptsMultipleValues(colName)) {
                while (columns.hasMoreElements()) {
                    TableColumn col = columns.nextElement();

                    if (col.getHeaderValue().toString().equals(colName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void clearCells(int startRow, int startCol, int endRow, int endCol) {
        int[] rows = Utils.getArrayOfVals(startRow, endRow);
        int[] columns = Utils.getArrayOfVals(startCol, endCol);

        SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(rows, Utils.convertSelectedColumnsToModelIndices(spreadsheet.getTable(), columns));

        spreadsheet.spreadsheetHistory.add(affectedRange);

        fill(affectedRange, "");
    }


    private int convertViewRowToModel(int row) {
        return spreadsheet.spreadsheetModel.getIndexes()[row];
    }

    public void copyColumnDownwards(int rowId, int colInd) {
        int convColIndex = convertViewIndexToModelIndex(colInd);
        String val = spreadsheet.spreadsheetModel.getValueAt(rowId, convColIndex).toString();
        fill(new SpreadsheetCellRange(rowId, spreadsheet.getTable().getRowCount(), convColIndex, convColIndex), val);
    }

    public int convertViewIndexToModelIndex(int vColIndex) {
        if (vColIndex >= spreadsheet.getTable().getColumnCount()) {
            return -1;
        }
        return spreadsheet.getTable().getColumnModel().getColumn(vColIndex).getModelIndex();
    }


    /**
     * Fill a range of cells with a given value (or formula)
     *
     * @param range The range to fill
     * @param value The value to fill. Should begin with '=' for a formula.
     */
    public void fill(SpreadsheetCellRange range, String value) {
        CellEditor editor = spreadsheet.getTable().getCellEditor();
        if (editor != null) {
            editor.cancelCellEditing();
        }
        spreadsheet.spreadsheetHistory.add(range);
        spreadsheet.spreadsheetModel.fillRange(range, value);
        spreadsheet.spreadsheetModel.fireTableDataChanged();
    }

    /**
     * Adds a column to the table with a specified name
     *
     * @param headerLabel - name of column to be added
     */
    public void addColumn(Object headerLabel) {
        SpreadsheetModel model = (SpreadsheetModel) spreadsheet.getTable().getModel();
        TableColumn col = new TableColumn(spreadsheet.getTable().getModel().getColumnCount());
        col.setHeaderValue(headerLabel);
        col.setPreferredWidth(calcColWidths(headerLabel.toString()));

        // add a cell editor (if available to the column)
        addCellEditor(col);

        spreadsheet.getTable().addColumn(col);

        model.addColumn(headerLabel.toString());
        model.fireTableStructureChanged();

        spreadsheet.getTable().getColumnModel().getColumn(spreadsheet.getTable().getColumnCount() - 1)
                .setHeaderRenderer(spreadsheet.renderer);

        if (spreadsheet.getTable().getRowCount() > 0) {
            spreadsheet.getTable().setValueAt(spreadsheet.getTableReferenceObject().getDefaultValue(headerLabel.toString()), 0,
                    spreadsheet.getTable().getColumnCount() - 1);
            copyColumnDownwards(0, spreadsheet.getTable().getColumnCount() - 1);
            spreadsheet.getTableReferenceObject().getDefaultValue(headerLabel.toString());
        }
    }

    /**
     * Add a column after the currently selected column
     *
     * @param headerLabel             - name of column to add.
     * @param fixedVal                - initial value to populate column with, if any.
     * @param currentlySelectedColumn - place in table to add the column after.
     */
    public void addColumnAfterPosition(Object headerLabel, String fixedVal,
                                       int currentlySelectedColumn) {

        if (currentlySelectedColumn == -1) {
            currentlySelectedColumn = (spreadsheet.getTable().getSelectedColumn() == -1)
                    ? (spreadsheet.getTable().getColumnCount() - 1) : spreadsheet.getTable().getSelectedColumn();
        }

        SpreadsheetModel model = (SpreadsheetModel) spreadsheet.getTable().getModel();

        TableColumn col = new TableColumn(spreadsheet.getTable().getModel().getColumnCount());
        col.setHeaderValue(headerLabel);
        col.setPreferredWidth(calcColWidths(headerLabel.toString()));
        col.setHeaderRenderer(spreadsheet.renderer);

        addCellEditor(col);

        model.addToColumns(headerLabel.toString());
        model.addColumn(col);

        spreadsheet.getTable().addColumn(col);

        model.fireTableStructureChanged();
        model.fireTableDataChanged();

        // now move the column into it's correct position
        int stopValue = headerLabel.toString().equals("Unit")
                ? (spreadsheet.previouslyAddedCharacteristicPosition + 1)
                : (currentlySelectedColumn + 1);

        for (int i = spreadsheet.getTable().getColumnCount() - 1; i > stopValue; i--) {
            spreadsheet.getTable().getColumnModel().moveColumn(i - 1, i);
        }


        if (headerLabel.toString().equals("Unit")) {
            addColumnToDependencies(spreadsheet.getTable().getColumnModel()
                    .getColumn(spreadsheet.previouslyAddedCharacteristicPosition),
                    col);
        } else if (headerLabel.toString().contains("Parameter")) {
            addColumnToDependencies(spreadsheet.getTable().getColumnModel()
                    .getColumn(currentlySelectedColumn),
                    col);
        }


        if (headerLabel.toString().contains("Characteristics") ||
                headerLabel.toString().contains("Factor") ||
                headerLabel.toString().contains("Parameter")) {
            spreadsheet.previouslyAddedCharacteristicPosition = stopValue;
        }

        fixedVal = fixedVal == null ? "" : fixedVal;
        if (fixedVal != null && spreadsheet.getTable().getRowCount() > 0) {
            spreadsheet.getTable().setValueAt(fixedVal, 0, stopValue);
            copyColumnDownwards(0, stopValue);
        }

        spreadsheet.getTable().addNotify();
    }

    /**
     * Recovers cell editor for a field for attachment to a column
     *
     * @param col - Column to attach a custom cell editor to
     */
    @SuppressWarnings({"ConstantConditions"})
    protected void addCellEditor(TableColumn col) {
        ValidationObject vo = spreadsheet.getTableReferenceObject().getValidationConstraints(col.getHeaderValue()
                .toString());
        DataTypes classType = spreadsheet.getTableReferenceObject().getColumnType(col.getHeaderValue().toString());

        if (vo != null && classType == DataTypes.STRING) {
            StringValidation sv = ((StringValidation) vo);
            col.setCellEditor(new StringEditor(sv));
            return;
        }

        if (col.getHeaderValue().toString().equals("Protocol REF")) {
            col.setCellEditor(new FilterableListCellEditor(spreadsheet.getStudyDataEntryEnvironment().getStudy()));
            return;
        }

        if (spreadsheet.getTableReferenceObject().getClassType(col.getHeaderValue().toString()) == DataTypes.ONTOLOGY_TERM) {
            col.setCellEditor(new OntologyCellEditor(spreadsheet.getDataEntryEnv().getParentFrame(),
                    spreadsheet.getTableReferenceObject().acceptsMultipleValues(col.getHeaderValue().toString()),
                    spreadsheet.getTableReferenceObject().getRecommendedSource(col.getHeaderValue().toString())));
            return;
        }

        if (spreadsheet.getTableReferenceObject().getClassType(col.getHeaderValue().toString()) == DataTypes.LIST) {
            col.setCellEditor(new FilterableListCellEditor(spreadsheet.getTableReferenceObject().getListItems(col.getHeaderValue().toString())));
            return;
        }

        if (spreadsheet.getTableReferenceObject().getClassType(col.getHeaderValue().toString())
                == DataTypes.DATE) {
            col.setCellEditor(Spreadsheet.dateEditor);

            return;
        }

        if (spreadsheet.getTableReferenceObject().getClassType(col.getHeaderValue().toString()) == DataTypes.BOOLEAN) {
            col.setCellEditor(new StringEditor(new StringValidation("true|yes|TRUE|YES|NO|FALSE|no|false", "not a valid boolean!"), true));
            return;
        }

        if ((classType == DataTypes.STRING) &&
                spreadsheet.getTableReferenceObject().acceptsFileLocations(col.getHeaderValue().toString())) {
            col.setCellEditor(Spreadsheet.fileSelectEditor);
            return;
        }


        if (classType == DataTypes.INTEGER) {
            col.setCellEditor(new StringEditor(new StringValidation("[0-9]+", "Please enter an integer value!"), true));
            return;
        }

        if (classType == DataTypes.DOUBLE) {
            col.setCellEditor(new StringEditor(new StringValidation("[0-9]+[.]{0,1}[0-9]*", "Please enter a double value!"), true));
            return;
        }

        col.setCellEditor(new StringEditor(new StringValidation(".*", "")));
    }

    /**
     * Adds a Dependent column (dependentCol) to a Parent Column (parentCol) list of columns.
     *
     * @param parentCol    - Parent Column
     * @param dependentCol - Column Dependent on Parent e.g. Unit to a factor.
     */
    public void addColumnToDependencies(TableColumn parentCol,
                                        TableColumn dependentCol) {
        if (!spreadsheet.columnDependencies.containsKey(parentCol)) {
            spreadsheet.columnDependencies.put(parentCol, new ArrayList<TableColumn>());
        }

        if (dependentCol != null) {
            spreadsheet.columnDependencies.get(parentCol).add(dependentCol);
        }
    }

    /**
     * Add an Array of columns to the columns vector.
     *
     * @param colName - Array of column names to be added to the spreadsheet.
     */
    public void addColumns(String[] colName) {

        for (String aColName : colName) {
            spreadsheet.columns.addElement(aColName);
        }
    }

    /**
     * Add a field to the TableReferenceObject
     *
     * @param fo - Field object to add.
     */
    public void addFieldToReferenceObject(TableFieldObject fo) {
        spreadsheet.getTableReferenceObject().addField(fo);
    }

    public void addRow() {
        Vector r;
        r = createBlankElement(false);
        spreadsheet.rows.addElement(r);
        spreadsheet.getTable().addNotify();
    }


    /**
     * Add rows to the table
     *
     * @param number            - number of rows to add.
     * @param creatingFromEmpty - whether or not the rows have been added at the very start.
     */
    public void addRows(int number, boolean creatingFromEmpty) {
        for (int i = 0; i < number; i++) {
            // need to create separate references.
            Vector<SpreadsheetCell> r = createBlankElement(creatingFromEmpty);
            spreadsheet.rows.addElement(r);
        }

        spreadsheet.spreadsheetModel.fireTableStructureChanged();
        spreadsheet.getTable().addNotify();
        spreadsheet.spreadsheetModel.updateRowCount();
    }

    protected void insertRowInPosition(int selectedRow) {
        // insert row
        addRows(1, false);

        // cut cell range from selected row downwards
        int[] rows = Utils.getArrayOfVals(selectedRow, spreadsheet.spreadsheetModel.getRowCount() - 2);
        int[] cols = Utils.getArrayOfVals(1, spreadsheet.spreadsheetModel.getColumnCount() - 1);
        SpreadsheetCellRange toMove = new SpreadsheetCellRange(rows, Utils.convertSelectedColumnsToModelIndices(spreadsheet.getTable(), cols));

        doCopy(true, toMove);

        paste((selectedRow + 1), 1, false);

        spreadsheet.spreadsheetModel.setSelection(new SpreadsheetCellRange(new int[]{selectedRow}, cols));

    }

    /**
     * Harsh and extremely inaccurate way of calculating column width. Will change to use FontMetrics.
     *
     * @param colName - Name of column to check
     * @return an int for the width of the column.
     */
    protected int calcColWidths(String colName) {
        return (int) (((colName.length() + 3) * 8) * 1.20);
    }

    /**
     * Creates blank elements (or those including default data) into the rows being added
     * todo check this again and make sure the proper values are being obtained for default values.
     *
     * @param creatingFromEmpty - whether or not the rows have been added at the very start.
     * @return Vector containing elements to be added to the row.
     */
    private Vector<SpreadsheetCell> createBlankElement(boolean creatingFromEmpty) {
        Vector<SpreadsheetCell> columnValues = new Vector<SpreadsheetCell>();
        int totalColumns = spreadsheet.spreadsheetModel.getColumnCount();
        int curRowNo = spreadsheet.spreadsheetModel.getRowCount();

        // if we don't find a field, we should skip over the index? Or we include a padding factor which can change depending
        // on if a field isn't found in the current position. i.e, starts off at -1, then can change to zero.
        for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {
            String colName = spreadsheet.spreadsheetModel.getColumnName(columnIndex);
            // Col 0 = Row Number
            if (colName.equals(TableReferenceObject.ROW_NO_TEXT)) {
                columnValues.addElement(new SpreadsheetCell(String.valueOf(curRowNo + 1)));
            } else {
                // / Else, all other columns are data. We can fairly safely put this data in as a String for now. But maybe
                // we should use the previous code since it covered more cases. If it works after modification, I can
                // put the previous code back in again.
                String value = spreadsheet.getTableReferenceObject().getDefaultValue(columnIndex);
                columnValues.addElement(new SpreadsheetCell(creatingFromEmpty ? value : ""));
            }
        }
        return columnValues;
    }

    /**
     * Delete column given an index.
     *
     * @param vColIndex - column to remove.
     */
    protected void deleteColumn(int vColIndex) {
        spreadsheet.curColDelete = vColIndex;
        spreadsheet.currentState = Spreadsheet.DELETING_COLUMN;

        TableColumn col = spreadsheet.getTable().getColumnModel().getColumn(spreadsheet.curColDelete);


        if (!col.getHeaderValue().toString().equals(TableReferenceObject.ROW_NO_TEXT)) {
            if (spreadsheet.getTableReferenceObject().isRequired(col.getHeaderValue().toString()) && !areMultipleOccurences(col.getHeaderValue().toString())) {
                spreadsheet.optionPane = new JOptionPane("<html>This column can not be deleted due to it being a required field in this assay!</html>",
                        JOptionPane.OK_OPTION);
                spreadsheet.optionPane.setIcon(spreadsheet.requiredColumnWarningIcon);
                UIHelper.applyOptionPaneBackground(spreadsheet.optionPane, UIHelper.BG_COLOR);
                spreadsheet.optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getPropertyName()
                                .equals(JOptionPane.VALUE_PROPERTY)) {
                            spreadsheet.getDataEntryEnv().getParentFrame().hideSheet();
                        }
                    }
                });
                spreadsheet.getDataEntryEnv().getParentFrame()
                        .showJDialogAsSheet(spreadsheet.optionPane.createDialog(spreadsheet,
                                "Can not delete"));
            } else {
                spreadsheet.optionPane = new JOptionPane("<html>Are you sure you want to delete this column? <p>This Action can not be undone!</p></html>",
                        JOptionPane.INFORMATION_MESSAGE,
                        JOptionPane.YES_NO_OPTION,
                        spreadsheet.confirmRemoveColumnIcon);
                UIHelper.applyOptionPaneBackground(spreadsheet.optionPane, UIHelper.BG_COLOR);
                spreadsheet.optionPane.addPropertyChangeListener(spreadsheet);
                spreadsheet.getDataEntryEnv().getParentFrame()
                        .showJDialogAsSheet(spreadsheet.optionPane.createDialog(spreadsheet,
                                "Confirm Delete Column"));
            }
        }
    }

    protected boolean areMultipleOccurences(String colName) {
        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();

        int count = 0;
        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().equals(colName)) {
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Delete multiple rows
     *
     * @param index - array of ints containing indexes to remove.
     */
    protected void deleteRow(int[] index) {
        spreadsheet.rowsToDelete = index;
        spreadsheet.currentState = Spreadsheet.DELETING_ROW;

        spreadsheet.optionPane = new JOptionPane("<html>Are you sure you want to delete these rows? <p>This Action can not be undone!</p></html>",
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION,
                spreadsheet.confirmRemoveRowIcon);
        spreadsheet.optionPane.addPropertyChangeListener(spreadsheet);
        UIHelper.applyOptionPaneBackground(spreadsheet.optionPane, UIHelper.BG_COLOR);
        spreadsheet.getDataEntryEnv().getParentFrame()
                .showJDialogAsSheet(spreadsheet.optionPane.createDialog(spreadsheet,
                        "Confirm Delete Rows"));
    }

    /**
     * Delete a row with a given index.
     *
     * @param index - index of row to remove
     */
    protected void deleteRow(int index) {
        spreadsheet.rowsToDelete = new int[]{index};
        spreadsheet.currentState = Spreadsheet.DELETING_ROW;

        spreadsheet.optionPane = new JOptionPane("<html>Are you sure you want to delete this row? <p>This Action can not be undone!</p></html>",
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION,
                spreadsheet.confirmRemoveRowIcon);
        spreadsheet.optionPane.addPropertyChangeListener(spreadsheet);
        UIHelper.applyOptionPaneBackground(spreadsheet.optionPane, UIHelper.BG_COLOR);
        spreadsheet.getDataEntryEnv().getParentFrame()
                .showJDialogAsSheet(spreadsheet.optionPane.createDialog(spreadsheet,
                        "Confirm Delete Rows"));
    }


    protected void putStringOnClipboard(String toPlace) {
        StringSelection stsel = new StringSelection(toPlace);
        spreadsheet.system.setContents(stsel, stsel);
    }


}
