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

package org.isatools.isacreator.spreadsheet.utils;

import org.isatools.isacreator.spreadsheet.Spreadsheet;

import java.util.HashMap;
import java.util.Map;

/**
 * TableDataStructureCreator
 *
 * @author eamonnmaguire
 * @date Sep 23, 2010
 */


public class TableDataStructureCreator {

    private Spreadsheet sheet;

    private Map<Integer, String> columnIndexToName;
    private Object[][] dataMatrix;

    public TableDataStructureCreator(Spreadsheet sheet) {
        this.sheet = sheet;
        generateColumnMappings();
        generateDataMatrix();
    }

    private void generateDataMatrix() {
        int columnCount = sheet.getTable().getColumnCount() - 1;
        int rowCount = sheet.getTable().getRowCount();

        dataMatrix = new Object[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            // start at 1 to miss out the row number column...
            for (int col = 1; col < columnCount + 1; col++) {
                dataMatrix[row][col - 1] = sheet.getTable().getValueAt(row, col);
            }
        }
    }

    private void generateColumnMappings() {
        int columnCount = sheet.getTable().getColumnCount();

        columnIndexToName = new HashMap<Integer, String>();

        for (int column = 1; column < columnCount; column++) {
            String colName = sheet.getTable().getColumnName(column);

            columnIndexToName.put(column - 1, colName);
        }
    }

    public Map<Integer, String> getColumnIndexToName() {
        return columnIndexToName;
    }

    /**
     * Returns the data matrix representing the data in the spreadsheet.
     *
     * @return - Object[rows][columns]
     */
    public Object[][] getDataMatrix() {
        return dataMatrix;
    }

    /**
     * Returns array of Objects representing the contents of a row if it exists, @see null otherwise
     *
     * @param rowNumber - integer representing the row number to fetch data for.
     * @return Object[] containing row data if the row exists, null otherwise.
     */
    public Object[] getRowData(int rowNumber) {
        if (rowNumber < dataMatrix.length) {
            return dataMatrix[rowNumber];
        }

        return null;
    }
}
