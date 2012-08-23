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

/**
 * /**
 * This class specifies how a range of cells are represented.
 * <p/>
 * A range is a continuous rectangular area that can be specified by its
 * upper left corner (minCorner) and lower right corner (maxCorner).
 * <p/>
 * WRT CellRange.java - idea for implementation taken from that implemented in the JSpreadsheet project java.dev.net
 *
 * @author Ricky Chin
 */
public class SpreadsheetCellRange {

    // Lower right corner of range
    SpreadsheetCellPoint maxCorner;

    //Upper left corner of range
    SpreadsheetCellPoint minCorner;

    private int[] columnList = null;

    /**
     * Creates new CellRange. It assumes that the array parameters have
     * entries listed in ascending order. In other words, the row array
     * has the row with the smallest number in index 0 and largest
     * row in the last index. The same goes for the column array.
     *
     * @param rows the array range of rows
     * @param cols the array range of columns
     */
    public SpreadsheetCellRange(int[] rows, int[] cols) {
        //rows selected are in ascending order
        this.columnList = cols;

        int minRow = rows[0];
        int maxRow = rows[rows.length - 1];

        //columns selected are in ascending order
        int minCol = cols[0];
        int maxCol = cols[cols.length - 1];

        minCorner = new SpreadsheetCellPoint(minRow, minCol);
        maxCorner = new SpreadsheetCellPoint(maxRow, maxCol);
    }

    /**
     * This contructor takes x1, x2, y1, y2 and constructs a range.
     *
     * @param minRow upper left corner row coordinate
     * @param minCol upper left corner col coordinate
     * @param maxRow lower right corner row coordinate
     * @param maxCol lower right corner col coordinate
     */
    public SpreadsheetCellRange(int minRow, int maxRow, int minCol, int maxCol) {
        minCorner = new SpreadsheetCellPoint(minRow, minCol);
        maxCorner = new SpreadsheetCellPoint(maxRow, maxCol);
    }

    /**
     * This constructor takes two CellPoints to construct a range.
     *
     * @param ULHCorner upper left corner
     * @param LRHCorner lower right corner
     */
    public SpreadsheetCellRange(SpreadsheetCellPoint ULHCorner, SpreadsheetCellPoint LRHCorner) {
        minCorner = ULHCorner;
        maxCorner = LRHCorner;
    }

    /**
     * This returns the last column in the range.
     *
     * @return last column of range
     */
    public int getEndCol() {
        return maxCorner.getCol();
    }

    /**
     * This returns the last row in the range.
     *
     * @return last row of range
     */
    public int getEndRow() {
        return maxCorner.getRow();
    }

    public int[] getColumnList() {
        return columnList;
    }

    /**
     * This returns the number of rows in the range.
     *
     * @return number of rows in range
     */
    public int getHeight() {
        return getEndRow() - getStartRow() + 1;
    }

    /**
     * This returns the first column in the range.
     *
     * @return first column of range
     */
    public int getStartCol() {
        return minCorner.getCol();
    }

    /**
     * This returns the first row in the range.
     *
     * @return first row of range
     */
    public int getStartRow() {
        return minCorner.getRow();
    }

    /**
     * This returns the number of columns in the range.
     *
     * @return number of columns in range
     */
    public int getWidth() {
        return getEndCol() - getStartCol() + 1;
    }

    /**
     * This returns the lower right corner of the range
     *
     * @return the lower right corner of the range
     */
    public SpreadsheetCellPoint getmaxCorner() {
        return maxCorner;
    }

    /**
     * This returns the upper left corner of the range
     *
     * @return the upper left corner of the range
     */
    public SpreadsheetCellPoint getminCorner() {
        return minCorner;
    }

    /**
     * This returns a string represetation of the difference of the upper left
     * and lower right corners
     *
     * @return string minCorner - maxCorner
     */
    public String toString() {
        return minCorner.toString() + ':' + maxCorner.toString();
    }
}
