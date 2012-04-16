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

package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Vector;

/**
 * Model for the Spreadsheet.
 *
 * @author Eamonn Maguire
 */
public class SpreadsheetModel extends DefaultTableModel {
    // Object which can sort the table by specific/multiple columns.

    // row indexes.
    int[] indexes;

    /**
     * holds the history information for the table
     */
    private SpreadsheetHistory spreadsheetHistory;

    /**
     * Stores file name of current document
     */
    private JTable table;
    private TableReferenceObject tro;

    /**
     * Constructs a default SharpTableModel which is a table of zero columns and
     * zeros rows.
     *
     * @param table the gui component to tie it to
     * @param tro   - The TableReferenceObject associated with the Spreadsheet!
     */
    public SpreadsheetModel(JTable table, TableReferenceObject tro) {
        super();

        // initialize state to unmodified and file to untitled
        this.table = table;
        this.tro = tro;
    }

    public SpreadsheetModel(TableReferenceObject tro) {
        super();
        this.tro = tro;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    /**
     * Returns the columnIdentifiers as a Vector.
     *
     * @return Vector
     */
    public Vector<Object> getColumnIdentifiers() {
        return columnIdentifiers;
    }

    public void addUndoableEditListener(UndoableEditListener listener) {
        listenerList.add(UndoableEditListener.class, listener);
    }

    /**
     * Get size of the row index array.
     *
     * @return Integer - size of row index array.
     */
    public int getIndexSize() {
        return indexes.length;
    }


    /**
     * Gets the array of Integer values containing the row indices.
     *
     * @return Array of Integer values depicting the row indices.
     */
    public int[] getIndexes() {
        int n = getRowCount();

        if (indexes != null && indexes.length == n) {
            return indexes;
        }

        indexes = new int[n];

        for (int i = 0; i < n; i++) {
            indexes[i] = i;
        }

        return indexes;
    }

    /**
     * This method sorts an arbitrary range in the table.
     *
     * @param area       range to sort
     * @param primary    primary row/column to sort by
     * @param second     second row/column to sort by (set equal to primary if there is
     *                   no secondary criteria specified.
     * @param ascend     true if sorting in ascending order by primary criteria
     * @param tiebreaker true if sorting in ascending order by secondary criteria data
     *                   structure
     */
    public void sort(SpreadsheetCellRange area, int primary, int second, boolean ascend, boolean tiebreaker) {
        /*
           * original data order will be saved here and placed on clipboard for
              * undo
           */
        SpreadsheetClipboard[] data;

        data = new SpreadsheetClipboard[area.getHeight()];
        for (int i = 0; i < data.length; i++) {
            SpreadsheetCellRange temp = new SpreadsheetCellRange(new int[]{area.getStartRow() + i}, area.getColumnList());
            data[i] = new SpreadsheetClipboard(this, temp, false);
        }

        /*
            * We are going to do the sort within the world of the data array First,
           * we do index sorting to create an index array. Then according to the
           * index array, we paste the entries in data back in the sorted order.
           */

        //do index sorting
        int[] indices = internalSort(area, primary, second, ascend, tiebreaker);

        //paste accordingly

        for (int i = area.getStartRow(); i <= area.getEndRow(); i++) {
            //point to paste at
            SpreadsheetCellPoint point = new SpreadsheetCellPoint(i, 1);

            int y = i - area.getStartRow();
            data[indices[y] - area.getStartRow()].paste(this, point);
        }

    }

    /**
     * Helper for sort that does the sorting. To implement different algorithms
     * for sorting modify this method. Returns an index array after index
     * sorting
     *
     * @param area       area to sort in
     * @param primary    primary criteria to sort
     * @param second     secondary criteria (set equal to primary if not specified)
     * @param ascend     true if sort ascending by primary
     * @param tiebreaker true if sort ascending by secondary
     * @return index array with row/col numbers of how cells should be arranged.
     */
    private int[] internalSort(SpreadsheetCellRange area, int primary, int second, boolean ascend, boolean tiebreaker) {
        //initialize index array
        int[] index;

        index = new int[area.getHeight()];
        for (int i = 0; i < index.length; i++) {
            index[i] = i + area.getStartRow();
        }


        int j;

        for (int p = 1; p < index.length; p++) {
            int tmp = index[p];

            for (j = p;
                 ((j > 0) && rightOrder(primary, second, tmp, index[j - 1], ascend, tiebreaker));
                 j--) {
                index[j] = index[j - 1];
            }
            index[j] = tmp;
        }

        return index;
    }

    /**
     * This is a helper function that compares rows or columns
     *
     * @param primary   first criteria to sort by
     * @param ascending - self explanatory
     * @param i         column or row you are comparing
     * @param j         column or row to compare i to
     * @return -1 if i < j, 0 if i = j, 1 if i > j
     */
    private int compareLines(int primary, boolean ascending, int i, int j) {
        SpreadsheetCell x = getCriteria(primary, i);
        SpreadsheetCell y = getCriteria(primary, j);
        return x.compare(y, tro.getColumnType(table.getColumnName(primary)), ascending);
    }

    /**
     * used to make sorting method and helper methods for sort treat row sorting
     * the same as column sorting.
     *
     * @param interest criteria coordinate for sort
     * @param i        other coordinate
     * @return cell at those coordinates
     */
    private SpreadsheetCell getCriteria(int interest, int i) {
        return getCellAt(i, interest);
    }

    /**
     * Determines if cells are in the wrong order Used only as helper method for
     * sort.
     */
    private boolean rightOrder(int primary, int second, int i, int j, boolean ascend, boolean order) {
        //compare by first criteria
        int result = compareLines(primary, ascend, i, j);

        //if equal, use second as tiebreaker
        if (result == 0) {
            result = compareLines(second, order, i, j);

            if (order) {
                return (result < 0);
            } else {
                return (result > 0);
            }

            //otherwise just return results from primary criteria
        } else {
            if (ascend) {
                return (result < 0);
            } else {
                return (result > 0);
            }
        }
    }


    /**
     * Refresh the row numbers, since they will be mixed up after a sort operation.
     */
    public void updateRowCount() {
        for (int rows = 0; rows < getIndexes().length; rows++) {
            doSetValueAt(rows + 1, rows, 0);
        }
    }


    /**
     * This method gets the whole Cell object located at the these coordinates.
     * This method avoids the casting required when using getValueAt.
     * <p/>
     * Note: here we need to make row 1-based.
     * <p/>
     * <p/>
     * If the coordinates specify a cell that is out of bounds then it returns
     * null. If a cell does not exist in the SharpTableModel at that valid
     * coordinate, it creates an empty cell, places it at that spot and returns
     * it.
     *
     * @param aRow    the row of the cell
     * @param aColumn the column of the cell
     * @return the Cell object at this location
     */
    public SpreadsheetCell getCellAt(int aRow, int aColumn) {
        /* check for out of bounds */
        if ((aRow < 0) || (aRow >= getRowCount()) || (aColumn < 0) || (aColumn >= getColumnCount())) {
            return null;
        }

        return (SpreadsheetCell) super.getValueAt(aRow, aColumn);
    }

    /**
     * This method copies the cells in a range into a two-dimensional array of
     * cells.
     *
     * @param range range of cells to copy
     * @return copy of range
     */
    public SpreadsheetCell[][] getRange(SpreadsheetCellRange range) {
        //get dimensions of range
        SpreadsheetCell[][] board = new SpreadsheetCell[range.getHeight()][range.getWidth()];

        //copy the cells
        for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
            for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {
                //translate to coordinates in copy array
                int x = i - range.getStartRow();
                int y = j - range.getStartCol();

                SpreadsheetCell field = getCellAt(i, j);

                /*
                     * if it is a formula copy both the value and the formula The
                     * value will be useful with a paste by value
                    */

                //value cells have immutable objects
                board[x][y] = new SpreadsheetCell(field.getValue());

            }
        }

        return board;
    }


    /**
     * set table selection to the range sel
     *
     * @param sel the range to be selected
     */
    public void setSelection(SpreadsheetCellRange sel) {
        // validate sel
        int maxRow = table.getRowCount() - 1;
        int maxCol = table.getColumnCount() - 1;

        int startRow = sel.getStartRow();
        int startCol = sel.getStartCol();
        int endRow = sel.getEndRow();
        int endCol = sel.getEndCol();

        table.setColumnSelectionInterval(Math.min(startCol, maxCol), Math.min(endCol, maxCol));
        table.setRowSelectionInterval(Math.min(startRow, maxRow), Math.min(endRow, maxRow));

        ((CustomTable) table).scrollToCellLocation(startRow, table.convertColumnIndexToView(startCol));
    }

    private int findMinMaxValue(int[] candidateVals, boolean findMin) {
        int val = findMin ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        CustomTable cTable = ((CustomTable) table);
        if (candidateVals != null) {
            for (int candidate : candidateVals) {
                int convertedCandidate = cTable.convertColumnIndexToView(candidate);
                if (findMin) {
                    if (convertedCandidate < val) {
                        val = convertedCandidate;
                    }
                } else {
                    if (convertedCandidate > val) {
                        val = convertedCandidate;
                    }
                }
            }
        }

        return val;
    }

    /**
     * set table selection to the range sel
     *
     * @param sel the range to be selected
     */
    public void extendedSetSelection(SpreadsheetCellRange sel) {
        // validate sel
        int maxRow = table.getRowCount() - 1;
        int startRow = sel.getStartRow();
        int startCol = sel.getStartCol();
        int endRow = sel.getEndRow();

        // convert model indices to view!
        table.setColumnSelectionInterval(findMinMaxValue(sel.getColumnList(), true), findMinMaxValue(sel.getColumnList(), false));
        table.setRowSelectionInterval(Math.min(startRow, maxRow), Math.min(endRow, maxRow));

        ((CustomTable) table).scrollToCellLocation(startRow, table.convertColumnIndexToView(startCol));
    }

    /**
     * From a string input determine how many rows/columns it requires for the
     * table - it corresponds to the number of newlines and tabs.
     *
     * @param input the string to analyze
     * @param delim - delimiter used to split data
     * @return a CellPoint representing the size of the data in rows and columns.
     */
    static public SpreadsheetCellPoint getSize(String input, char delim) {
        BufferedReader in = new BufferedReader(new StringReader(input));
        String line;
        int rowcount = 0;
        int colcount = 0;

        try {
            while ((line = in.readLine()) != null) {
                rowcount++;

                // initialize new tokenizer on line with tab delimiter.
                //		tokenizer = new StringTokenizer(line, "\t");
                int index;
                int prev = 0;

                // set col to 1 before each loop
                int col = 0;

                while (true) {
                    index = line.indexOf(delim, prev);
                    prev = index + 1;

                    // increment column number
                    col++;

                    if (index == -1) {
                        break;
                    }
                }

                if (colcount < col) {
                    colcount = col;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return new SpreadsheetCellPoint(rowcount, colcount);
    }

    /**
     * Determines if a cell is empty
     *
     * @param row row coordinate of cell
     * @param col column coordinate of cell
     * @return true if cell is empty
     */
    public boolean isEmptyCell(int row, int col) {
        return getCellAt(row, col).getValue().equals("");
    }

    /**
     * Returns JTable
     *
     * @return JTable
     */
    public JTable getTable() {
        return table;
    }

    /**
     * This class returns the cell object at those coordinates. It does exactly
     * the same thing as getCellAt except that the return type is Object. It is
     * implemented because TableModel requires this method return an Object.
     *
     * @param aRow    the row coordinate
     * @param aColumn the column coordinate
     * @return the Cell
     */
    public Object getValueAt(int aRow, int aColumn) {

        return getCellAt(aRow, aColumn);
    }

    /**
     * This method does not recognize formula strings. It is used for dialogue
     * box input where there will be no formulas inputted or expected to be
     * inputted.
     *
     * @param input input string to parse
     * @return appropriate object after parsing
     */
    public static Serializable fieldParser(String input) {
        if (input == null) {
            return "";
        }

        /* try making it a number */
        try {
            if (input.contains(".")) {
                return new Double(input);
            } else {
                return new Integer(input);
            }
        } catch (NumberFormatException e) {
            /* all else fails treat as string */
            return input;
        }
    }

    /**
     * This object assumes that the object passes to it is already the correct
     * object to set the value of the cell as. For a formula, it also
     * calculcates the value of the formula and records that in the cell.
     *
     * @param input   object to set the Cell value as
     * @param aRow    row of cell to set
     * @param aColumn column of cell to set
     */
    public void setCellAt(Object input, int aRow, int aColumn) {
        SpreadsheetCell temp = getCellAt(aRow, aColumn);

        /* if for some reason value out of bounds ignore */
        if (temp != null) {
            //always remove references old formula referred to

            //insert new formula

            temp.setData(input);

        }
    }

    /**
     * This method sets the cells given by the range to the cooresponding value
     * in the Object array. In other words, this method pastes the object array
     * onto the range. It is assumed that the range and Object array have the
     * same dimensions. (a "placeAt" method for ranges)
     *
     * @param range the range of cells to paste to
     * @param data  the data to paste
     */
    public void setRange(SpreadsheetCellRange range, Object[][] data) {
        /* Loop through the paste range */
        for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
            for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {
                //calculate the corresponding entry in data array
                int x = i - range.getStartRow();
                int y = j - range.getStartCol();

                //place data entry at that place
                doSetValueAt(data[x][y], i, j);
            }
        }
    }

    /**
     * This is a method used to paste cells onto the table. This method is used
     * by the SharpClipboard class. It's feature is that it can paste only the
     * old evaluated values or it can be told to paste the data cells and
     * formulas.
     *
     * @param range   range to paste to
     * @param data    cells that need to be pasted
     * @param byValue true if only paste values if there are formula
     */
    public void setRange(SpreadsheetCellRange range, SpreadsheetCell[][] data, boolean byValue) {
        /*
                 * there may be formula so if byValue is true paste evaluated formula
                 * value into the range as a data cell
                 */
        if (byValue) {
            for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
                for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {
                    int x = i - range.getStartRow();
                    int y = j - range.getStartCol();

                    //get only value of a formula cell not formula
                    doSetValueAt(data[x][y].getValue(), i, j);
                }
            }
        } else {
            for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
                for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {
                    int x = i - range.getStartRow();
                    int y = j - range.getStartCol();
                    SpreadsheetCell info = data[x][y];

                    //paste new formula to recalculate

                    doSetValueAt(info.getValue(), i, j);

                }
            }
        }
    }

    /**
     * Sets the value of the cell. It takes care of formulas and data. If aValue
     * is a string, it parses it to see if it is a formula (begins with an "=")
     * or a number. It then sets the value of the cell accordingly.
     * <p/>
     * This function is called by JTable automatically, which means user has
     * manually input something. Thus, it records the previous value of the cell
     * into the History object associated with this SharpTableModel.
     * <p/>
     * We should never call it directly (use doSetValueAt instead).
     *
     * @param aValue  the formula or data you want to set cell to
     * @param aRow    row coordinate
     * @param aColumn column coordinate
     */
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        SpreadsheetCellPoint point = new SpreadsheetCellPoint(aRow, aColumn);
        spreadsheetHistory.add(new SpreadsheetCellRange(point, point));
        doSetValueAt(aValue, aRow, aColumn);
    }


    /**
     * This method clears all cells in the range but leaves the reference lists
     * alone.
     *
     * @param range range to clear
     */
    public void clearRange(SpreadsheetCellRange range) {
        fill(range, null);
    }

    /**
     * This method sets the value of the cell specified with these coordinates
     * to aValue. It does the parsing of string objects to see if they are
     * numbers or formulas. If you do not want any parsing at all, use
     * setCellAt.
     *
     * @param aValue  value to set cell to
     * @param aRow    row coordinate of cell
     * @param aColumn column coordinate of cell
     */
    public void doSetValueAt(Object aValue, int aRow, int aColumn) {
        if (aValue == null) {
            aValue = "";
        }


        if (aValue instanceof String) {
            String input = (String) aValue;

            /* try making it a formula */

            try {
                if (input.contains(".")) {
                    Double data = new Double(input);
                    setCellAt(data, aRow, aColumn);
                    System.out.println("Value was a double, so set it as a double value");
                } else {
                    Integer data = new Integer(input);
                    setCellAt(data, aRow, aColumn);
                }

            } catch (NumberFormatException e2) {
                /* all else fails treat as string */
                setCellAt(aValue, aRow, aColumn);
            }


        } else {
            System.out.println("aValue was instance of " + aValue.getClass().toString());
            System.out.println("In else clause. Setting value: " + aValue);

            setCellAt(aValue, aRow, aColumn);

        }
    }

    /**
     * toString is used to convert a range of cells into a string. One row per
     * line, and each column is tab-delimited.
     *
     * @param range the range in the table
     * @param delim - separator for when the data goes on the clipboard
     * @return a string
     * @see SpreadsheetClipboard
     */
    public String toString(SpreadsheetCellRange range, char delim) {
        StringBuffer sbf = new StringBuffer();

        for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
            for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {

                sbf.append(getValueAt(i, j));


                if (j < range.getEndCol()) {
                    sbf.append(delim);
                }
            }
            sbf.append("\n");
        }
        return sbf.toString();
    }

    /**
     * toString with modification to cope with changing column indexes in main
     * spreadsheet class. this method will iterate around specific columns to extract the
     * into a string appropriate for the clipboard!
     *
     * @param range the range in the table
     * @param delim - separator for when the data goes on the clipboard
     * @return a string
     * @see SpreadsheetClipboard
     */
    public String extendedToString(SpreadsheetCellRange range, char delim) {
        StringBuffer sbf = new StringBuffer();
        int[] columns = range.getColumnList();
        if (columns != null) {

            for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
                for (int j = 0; j < columns.length; j++) {

                    sbf.append(getValueAt(i, columns[j]));

                    if (j < columns.length - 1) {
                        sbf.append(delim);
                    }

                }
                sbf.append("\n");
            }
        }
        return sbf.toString();
    }

    /**
     * convert the whole table to a string.
     *
     * @return a string
     */
    public String toString(char delim) {
        return toString(new SpreadsheetCellRange(0, getRowCount() - 1, 0, getColumnCount() - 1), delim);
    }

    public String toString() {
        return toString(new SpreadsheetCellRange(0, getRowCount() - 1, 0, getColumnCount() - 1), '\t');
    }


    /**
     * This method is used to implement the fills of the spreadsheet. It takes a
     * range and fills the range with the object. For formula, it is equivalent
     * to pasting the formula on every cell in the range.
     *
     * @param range range to fill
     * @param input object to fill range with
     */
    protected void fill(SpreadsheetCellRange range, Object input) {
        //loop through range

        for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
            for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {
                doSetValueAt(input, i, j);
            }
        }
    }

    protected void fillRange(SpreadsheetCellRange range, String s) {
        if (range.getColumnList() != null) {
            extendedFill(range, SpreadsheetModel.fieldParser(s));
        } else {
            fill(range, SpreadsheetModel.fieldParser(s));
        }
    }

    protected void extendedFill(SpreadsheetCellRange range, Object input) {
        int[] columns = range.getColumnList();
        for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
            for (int column : columns) {
                doSetValueAt(input, i, column);
            }
        }
    }

    /**
     * This method associated the proper undo object to this SharpTableModel. It
     * must be called right after the constructor.
     *
     * @param h the History object to associate with this SharpTableModel
     */
    void setHistory(SpreadsheetHistory h) {
        spreadsheetHistory = h;
    }

    /**
     * fromString is used to convert a string to valus in a range of cells. One
     * row per line, and each column is tab-delimited.
     *
     * @param text  the string
     * @param range the range to paste
     * @see SpreadsheetClipboard
     */
    void fromString(String text, char delim, SpreadsheetCellRange range) {
        try {
            BufferedReader in = new BufferedReader(new StringReader(text));
            String line;
            int row = range.getStartRow();

            while (row <= range.getEndRow()) {
                line = in.readLine();

                int index;
                int prev = 0;

                // set col to startCol before each loop
                int col = range.getStartCol();
                String value;

                while (col <= range.getEndCol()) {
                    index = line.indexOf(delim, prev);
                    if (index >= 0) {
                        value = line.substring(prev, index);
                    } else {
                        value = line.substring(prev);
                    }

                    doSetValueAt(value, row, col);

                    prev = index + 1;

                    // increment column number
                    col++;

                    if (index == -1) {
                        break;
                    }
                }

                row++;
            }
        } catch (Exception e) {
        }
    }


    /**
     * extendedFromString is used to convert a string to valus in a range of cells. One
     * row per line, and each column is tab-delimited.
     *
     * @param text  the string
     * @param delim separator
     * @param range the range to paste
     * @see SpreadsheetClipboard
     */
    void extendedFromString(String text, char delim, SpreadsheetCellRange range) {
        try {
            BufferedReader in = new BufferedReader(new StringReader(text));
            String line;
            int row = range.getStartRow();

            int[] columns = range.getColumnList();


            if (columns != null) {

                while (row <= range.getEndRow()) {
                    line = in.readLine();

                    int index;
                    int prev = 0;

                    String value;

                    for (int column : columns) {

                        if (column != -1) {
                            index = line.indexOf(delim, prev);
                            if (index >= 0) {
                                value = line.substring(prev, index);
                            } else {
                                value = line.substring(prev);
                            }

                            doSetValueAt(value, row, column);

                            prev = index + 1;

                            if (index == -1) {
                                break;
                            }
                        }

                    }

                    row++;
                }
            }
        } catch (Exception e) {
        }
    }


    /**
     * Helper method for insertColumn. This method will not send the appropriate
     * notification to JTable. Please use insertColumn method instead.
     *
     * @param headerLabel - label to be given to the new column
     */
    public void addToColumns(String headerLabel) {
        columnIdentifiers.addElement(headerLabel);

        for (Object aDataVector : dataVector) {
            ((Vector) aDataVector).addElement(new SpreadsheetCell(""));
        }
    }

}


