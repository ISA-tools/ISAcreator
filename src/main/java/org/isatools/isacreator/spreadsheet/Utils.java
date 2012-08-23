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

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * IOUtils: a number of utility methods required by the Spreadsheet.
 *
 * @author Eamonn Maguire
 * @date Jan 11, 2010
 */


public class Utils {

    public static int[] getArrayOfVals(int startVal, int endVal) {
        int[] vals = new int[(endVal - startVal) + 1];
        if (startVal > -1 && startVal <= endVal) {
            int count = 0;
            for (int val = startVal; val <= endVal; val++) {
                vals[count] = val;
                count++;
            }
        }

        return vals;
    }

    public static int[] convertSelectedColumnsToModelIndices(JTable table, int[] columns) {
        int[] convertedCols = new int[columns.length];

        for (int i = 0; i < columns.length; i++) {
            convertedCols[i] = table.convertColumnIndexToModel(columns[i]);
        }

        return convertedCols;
    }

    /**
     * Get a column name given a column number.
     *
     * @param table- JTable component to act upon
     * @param colNo  - column number
     * @return Name of column
     */
    public static String getColName(JTable table, int colNo) {
        return table.getColumnName(colNo);
    }


    /**
     * Converts a double value to contain decimal places in the frequency the original value was recorded.
     *
     * @param originalVal  - e.g. "2.34"
     * @param valToProcess e.g. 2.999987895
     * @return - 3.00 in the case of the example above as a Double
     */
    public static Double formatDoubleValue(String originalVal, Double valToProcess) {
        StringBuffer format = new StringBuffer("#");
        int numDecimals;
        int numKeyVals = originalVal.length();
        if (originalVal.contains(".")) {
            format.append(".");
            numDecimals = originalVal.substring(originalVal.lastIndexOf(".") + 1).trim().length();
            for (int i = 0; i < numDecimals; i++) {
                format.append("#");
            }

            numKeyVals = originalVal.substring(0, originalVal.lastIndexOf(".")).trim().length();
        }

        for (int i = 0; i < numKeyVals; i++) {
            format.insert(0, "#");
        }

        DecimalFormat df = new DecimalFormat(format.toString());
        return Double.valueOf(df.format(valToProcess));

    }

    /**
     * Converts an index as it appears in a model to one in the view
     *
     * @param table      - JTable component to act upon
     * @param modelIndex - index to convert
     * @return - view index.
     */
    public static int convertModelIndexToView(JTable table, int modelIndex) {
        for (int col = 0; col < table.getColumnCount(); col++) {
            if (table.getColumnModel().getColumn(col).getModelIndex() == modelIndex) {
                return col;
            }
        }

        return -1;
    }

    /**
     * arrayToList converts an int array to an ArrayList object,
     * then sorts it in descending order.
     * Required to allow for multiple column deletions, otherwise indexes
     * get deleted before they are referenced.
     *
     * @param array - int array to be converted to a descending list
     * @return - descending list
     */
    public static List<Integer> arrayToList(int[] array) {
        List<Integer> al = new ArrayList<Integer>();

        for (int i : array) {
            al.add(i);
        }

        Collections.sort(al,
                new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        if (o1 > (int) o2) {
                            return -1;
                        } else if (o1 < (int) o2) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });


        return al;


    }

}
