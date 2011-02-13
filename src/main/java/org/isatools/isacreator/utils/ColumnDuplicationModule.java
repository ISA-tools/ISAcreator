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

package org.isatools.isacreator.utils;

import org.isatools.isacreator.spreadsheet.CustomTable;

import javax.swing.table.TableColumn;
import java.util.*;

/**
 * ColumnDuplicationModule: checks for repeating Columns right beside each other (e.g. Unit & Unit)
 *
 * @author Eamonn Maguire
 * @date Jan 14, 2010
 */


public class ColumnDuplicationModule implements CheckingModule {
    private String filename;
    private CustomTable table;
    private Map<String, List<String>> report = new HashMap<String, List<String>>();
    private List<IncorrectColumnPositioning> incorrectColumnPositions = new ArrayList<IncorrectColumnPositioning>();


    public ColumnDuplicationModule(String filename, CustomTable table) {
        this.filename = filename;
        this.table = table;
    }

    public boolean doCheck() {

        checkForRepeatingColumns();

        return report.isEmpty();
    }

    private void checkForRepeatingColumns() {
        Enumeration<TableColumn> tableColumns = table.getColumnModel().getColumns();
        TableColumn lastColumn = null;
        while (tableColumns.hasMoreElements()) {
            if (lastColumn == null) {
                lastColumn = tableColumns.nextElement();
            } else {
                TableColumn newColumn = tableColumns.nextElement();
                if (newColumn.getHeaderValue().toString().equals(lastColumn.getHeaderValue().toString())) {
                    addToReport(
                            newColumn.getHeaderValue().toString(),
                            table.convertColumnIndexToView(newColumn.getModelIndex()),
                            table.convertColumnIndexToView(lastColumn.getModelIndex())
                    );

                }
                lastColumn = newColumn;
            }
        }
    }

    private void addToReport(String columnName, int offendingIndexA, int offendingIndexB) {
        if (!report.containsKey(filename)) {
            report.put(filename, new ArrayList<String>());
        }
        report.get(filename).add("Repeating columns found for " + columnName + " in positions " + offendingIndexA + " and " + offendingIndexB);
        incorrectColumnPositions.add(new IncorrectColumnPositioning(Collections.singletonList(offendingIndexA), offendingIndexB));
    }

    public Map<String, List<String>> getReport() {
        return null;
    }

    public List<IncorrectColumnPositioning> getIncorrectColumnPositions() {
        return incorrectColumnPositions;
    }
}
