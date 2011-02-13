/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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


public class SpreadsheetCellPoint implements Comparable {

    private int col;
    private int row;


    public SpreadsheetCellPoint(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int compareTo(Object x) {
        if (x instanceof SpreadsheetCellPoint) {
            SpreadsheetCellPoint y = (SpreadsheetCellPoint) x;

            //by row
            if (this.row > y.row) {
                return 1;
            } else {
                if (this.row == y.row) {
                    //by column
                    if (this.col > y.col) {
                        return 1;
                    } else {
                        //equal
                        if (this.col == y.col) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                } else {
                    return -1;
                }
            }
        } else {
            return 2; /* can't compare non-CellPoint objects */
        }
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public boolean equals(Object x) {
        if (x instanceof SpreadsheetCellPoint) {
            SpreadsheetCellPoint y = (SpreadsheetCellPoint) x;

            return ((this.row == y.row) && (this.col == y.col));
        } else {
            return false;
        }
    }

    /**
     * Sets column coordinate.
     *
     * @param col value to set column coordinate to
     */
    void setCol(int col) {
        this.col = col;
    }

    /**
     * Sets the row coordinate of cell reference to be stored.
     *
     * @param row value to set row to
     */
    void setRow(int row) {
        this.row = row;
    }

    /**
     * Increments column coordinate
     *
     * @param increment - amount to increment column coordinate
     */
    void incrementColumn(int increment) {
        this.col += increment;
    }

    /**
     * Increments row coordinate.
     *
     * @param increment amount to increment row coordinate by
     */
    void incrementRow(int increment) {
        this.row += increment;
    }
}
