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
 * The Spreadsheet clipboard paradigm is taken from the JSpreadsheet Project and modified for
 * more complex purposes.
 */
public class SpreadsheetClipboard {
    /**
     * the range of Cells from which the clipboard was created
     */
    private SpreadsheetCellRange source;

    /**
     * holds the objects that are cut or copied
     */
    private String text;

    /**
     * Creates new SpreadsheetClipboard. If it is a cut, then it clears the range it
     * was created from.
     *
     * @param model the SpreadsheetModel you are operating on
     * @param range an array of CellPoint objects where the first is the
     *              upper left hand corner and the second entry is the
     *              lower right hand corner coordinates
     * @param isCut true only if this is a cut
     */
    public SpreadsheetClipboard(SpreadsheetModel model, SpreadsheetCellRange range, boolean isCut) {

        text = range.getColumnList() ==
                null ?
                model.toString(range, '\t') :
                model.extendedToString(range, '\t');
        source = range;

        //if it is a cut, set the old cells to null
        if (isCut) {
            model.clearRange(range);
        }

    }

    /**
     * This gets the actual range of a paste from a corner point. This is
     * actually a helper method for paste
     *
     * @param corner the upper left corner coordinate
     * @param model  the SpreadsheetModel you are using
     * @return the actual cell range; null if it's beyond the table range
     */
    public SpreadsheetCellRange getRange(SpreadsheetModel model, SpreadsheetCellPoint corner) {
        //limit to paste region

        //calculate dimensions of clipboard
        int cornerCol = ((CustomTable) model.getTable()).columnModelIndextoView(corner.getCol());

        SpreadsheetCellPoint size = SpreadsheetModel.getSize(text, '\t');
        //cannot paste to nonexistent cells
        if ((corner.getRow() < 0) || (cornerCol < 0)) {
            return null;
        } else {
            //paste as much as you can
            // need to return the rows as an array and the columns as an array of converted indices

            int[] rows = new int[size.getRow()];

            int rowToAdd = corner.getRow();
            for (int row = 0; row < rows.length; row++) {
                rows[row] = rowToAdd;
                rowToAdd++;
            }

            int[] cols = new int[size.getCol()];

            int colToAdd = cornerCol;
            for (int col = 0; col < cols.length; col++) {
                cols[col] = ((CustomTable) model.getTable()).columnViewIndextoModel(colToAdd);
                colToAdd++;
            }

            return new SpreadsheetCellRange(rows, cols);
        }
    }

    /**
     * Set the source of the clipboard
     *
     * @param x CellRange to set as the source of the clipboard
     */
    public void setSource(SpreadsheetCellRange x) {
        source = x;
    }

    /**
     * This method returns the range the clipboard originally came from
     *
     * @return the range the clipboard originally can from
     */
    public SpreadsheetCellRange getSource() {
        return source;
    }

    /**
     * This pastes the current contents of the spreadsheet object
     * on to the region defined by the coordinates of the upper
     * right hand corner. If the contents cannot be entirely pasted
     * on this region, it pastes as much as it can
     *
     * @param table  SpreadsheetModel model you are pasting to
     * @param corner coordinate of upper left hand corner
     */
    public void paste(SpreadsheetModel table, SpreadsheetCellPoint corner) {
        //if region to paste to is out of bounds
        SpreadsheetCellRange range = getRange(table, corner);
        paste(table, range);
    }

    /**
     * This is similar to the other paste, but take range as a parameter.
     * This is used together with getRange.
     *
     * @param table SharpTableModel you are pasting to
     * @param range range you are pasting to
     */
    public void paste(SpreadsheetModel table, SpreadsheetCellRange range) {
        //if region to paste to is out of bounds
        if (range != null) {
            int[] columns = range.getColumnList();
            if (columns != null) {
                table.extendedFromString(text, '\t', range);
            } else {
                table.fromString(text, '\t', range);
            }
        }
    }

    /**
     * Return the string representation of contents of the clipboard
     *
     * @return the string representation of contents of the clipboard
     */
    public String toString() {
        return text;
    }
}