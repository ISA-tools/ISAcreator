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

package org.isatools.isacreator.apiutils;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.Utils;
import org.isatools.isacreator.spreadsheet.sampleselection.SampleInformation;

import javax.swing.table.TableColumn;
import java.util.*;

/**
 * SpreadsheetUtils
 *
 * @author eamonnmaguire
 * @date Sep 30, 2010
 */


public class SpreadsheetUtils {


    /**
     * @param targetSheet - Spreadsheet to 'look at' for extraction of desired column names
     * @param toIgnore    - Set<String> containing the columns to be ignored.
     * @return Map containing all the desired columns along with their index.
     */
    public static Map<String, Integer> getColumnNames(Spreadsheet targetSheet, Set<String> toIgnore) {

        Map<String, Integer> columnNamesToIndex = new ListOrderedMap<String, Integer>();

        Map<Integer, String> columnIndexToName = getColumns(targetSheet, toIgnore);

        for (Integer columnIndex : columnIndexToName.keySet()) {
            columnNamesToIndex.put(columnIndexToName.get(columnIndex), columnIndex);
        }

        return columnNamesToIndex;
    }

    public static Map<Integer, String> getColumns(Spreadsheet targetSheet, Set<String> toIgnore) {
        int columnCount = targetSheet.getTable().getColumnCount();

        Map<Integer, String> columnNames = new ListOrderedMap<Integer, String>();

        for (int column = 1; column < columnCount; column++) {
            String colName = targetSheet.getTable().getColumnName(column);

            if (toIgnore != null) {

                boolean isIgnored = false;

                for (String ignoredFields : toIgnore) {
                    if (colName.contains(ignoredFields)) {
                        isIgnored = true;
                        break;
                    }
                }

                if (!isIgnored) {
                    columnNames.put(column, colName);
                }

            } else {
                columnNames.put(column, colName);
            }
        }
        return columnNames;
    }

    /**
     * Pulls out all values of a particular column and associates the other metadata with it as well
     *
     * @param primaryColumnName - e.g. Sample Name to pull out all the unique sample names available.
     * @param targetSheet       - Spreadsheet to 'look at' for extraction of desired column names
     * @return Map<String, Map<String, String>> -> primary Column values mapped to the key/value pairs describing the particular group
     */
    public static Map<String, SampleInformation> getGroupInformation(String primaryColumnName, Spreadsheet targetSheet) {

        Map<String, SampleInformation> groupInformation = new HashMap<String, SampleInformation>();

        Map<Integer, String> columnIndicesToName = getColumns(targetSheet, new HashSet<String>());

        for (int rowNo = 0; rowNo < targetSheet.getTable().getRowCount(); rowNo++) {

            String primaryColumnValue = "";

            if (!groupInformation.containsKey(primaryColumnName)) {

                Map<String, String> keyValues = new ListOrderedMap<String, String>();
                Map<String, Integer> columnNameToIndex = new HashMap<String, Integer>();

                for (int column = 1; column < targetSheet.getColumnCount(); column++) {
                    Object dataVal = targetSheet.getTable().getValueAt(rowNo, column);

                    String columnName = columnIndicesToName.get(column);

                    if (columnName.equalsIgnoreCase(primaryColumnName)) {
                        primaryColumnValue = dataVal == null ? "" : dataVal.toString();
                    } else {
                        keyValues.put(columnName, dataVal == null ? "" : dataVal.toString());
                        columnNameToIndex.put(columnName, column);
                    }
                }
                groupInformation.put(primaryColumnValue,
                        new SampleInformation(rowNo, primaryColumnValue, keyValues, columnNameToIndex));
            }
        }

        return groupInformation;
    }

    public static Set<String> getDataInColumn(Spreadsheet targetSheet, int tableViewIndex) {
        int rowCount = targetSheet.getTable().getRowCount();

        Set<String> columnContents = new ListOrderedSet<String>();

        if (tableViewIndex < targetSheet.getTable().getColumnCount()) {
            for (int rowNo = 0; rowNo < rowCount; rowNo++) {
                columnContents.add(targetSheet.getTable().getValueAt(rowNo, tableViewIndex).toString());
            }
        }

        return columnContents;
    }

    public static String[][] getSpreadsheetDataSubset(Spreadsheet targetSheet) {
        // initalise array to be the number of columns -1 to account for Row No. column.
        int rowCount = targetSheet.getTable().getRowCount();


        return getSpreadsheetDataSubset(targetSheet, rowCount);
    }

    public static String[][] getSpreadsheetDataSubset(Spreadsheet targetSheet, int numberOfRows) {
        // initalise array to be the number of columns -1 to account for Row No. column.
        int columnCount = targetSheet.getTable().getColumnCount();

        String[][] data = new String[numberOfRows][targetSheet.getColumnCount() - 1];

        for (int rowNo = 0; rowNo < numberOfRows; rowNo++) {
            for (int column = 1; column < columnCount; column++) {
                Object dataVal = targetSheet.getTable().getValueAt(rowNo, column);
                data[rowNo][column - 1] = dataVal == null ? "" : dataVal.toString();
            }
        }

        return data;
    }

    /**
     * Gets the freetext terms (which ideally should be ontology terms) in a Spreadsheet object
     *
     * @param spreadsheet @see Spreadsheet
     * @return Map<Column Name, Set<Column Values>>
     */
    public static Map<String, Set<String>> getFreetextInSpreadsheet(Spreadsheet spreadsheet) {
        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();

        Map<String, Set<String>> columnToFreeText = new HashMap<String, Set<String>>();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (spreadsheet.getTableReferenceObject().getClassType(tc.getHeaderValue().toString().trim())
                    == DataTypes.ONTOLOGY_TERM) {

                int colIndex = Utils.convertModelIndexToView(spreadsheet.getTable(), tc.getModelIndex());

                for (int row = 0; row < spreadsheet.getTable().getRowCount(); row++) {

                    String columnValue = (spreadsheet.getTable().getValueAt(row, colIndex) == null) ? ""
                            : spreadsheet.getTable().getValueAt(row,
                            colIndex).toString();

                    if (columnValue != null && !columnValue.trim().equals("") && !columnValue.contains(":")) {
                        if (!columnToFreeText.containsKey(tc.getHeaderValue().toString())) {
                            columnToFreeText.put(tc.getHeaderValue().toString(), new HashSet<String>());
                        }
                        columnToFreeText.get(tc.getHeaderValue().toString()).add(columnValue);
                    }
                }
            }
        }

        return columnToFreeText;
    }

    public static void replaceFreeTextWithOntologyTerms(Spreadsheet spreadsheet, Map<String, OntologyTerm> annotations) {
        Enumeration<TableColumn> columns = spreadsheet.getTable().getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (spreadsheet.getTableReferenceObject().getClassType(tc.getHeaderValue().toString().trim())
                    == DataTypes.ONTOLOGY_TERM) {
                int colIndex = Utils.convertModelIndexToView(spreadsheet.getTable(), tc.getModelIndex());

                for (int row = 0; row < spreadsheet.getTable().getRowCount(); row++) {

                    String columnValue = (spreadsheet.getTable().getValueAt(row, colIndex) == null) ? ""
                            : spreadsheet.getTable().getValueAt(row,
                            colIndex).toString();

                    if (annotations.containsKey(columnValue)) {
                        System.out.println("Replacing " + columnValue + " with " + annotations.get(columnValue).getUniqueId());
                        spreadsheet.getTable().setValueAt(annotations.get(columnValue).getUniqueId(), row, colIndex);
                    }
                }
            }
        }
    }

    /**
     * Method returns a Set of all the files defined in a spreadsheet. These locations are used to zip up the data files
     * in the ISArchive for submission to the index.
     *
     * @return Set of files defined in the spreadsheet
     */
    public static Set<String> getFilesDefinedInTable
    (Spreadsheet
             spreadsheet) {
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
}
