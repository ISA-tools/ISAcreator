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

package org.isatools.isacreator.utils;

import org.apache.log4j.Logger;
import org.isatools.isacreator.spreadsheet.CustomTable;

import javax.swing.table.TableColumn;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ColumnOrderModule
 *
 * @author Eamonn Maguire
 * @date Jan 14, 2010
 */


public class ColumnOrderModule implements CheckingModule {
    private static final Logger log = Logger.getLogger(ColumnOrderModule.class.getName());


    public static final String PROTOCOL_REGEX_PATTERN = "Protocol REF[\\s]*(Parameter Value\\[.+\\](\\s)*(Unit)*)*";
    public static final String PARAMETER_REGEX_PATTERN = "Parameter Value\\[.+\\](\\s)*(Unit)*";
    public static final String FACTOR_REGEX_PATTERN = "Factor Value\\[.+\\](\\s)*(Unit)*";
    public static final String CHARACTERISTICS_REGEX_PATTERN = "Characteristics\\[.+\\](\\s)*(Unit)*";

    private Map<String, List<String>> report = new HashMap<String, List<String>>();
    private List<IncorrectColumnPositioning> incorrectColumnPositions = new ArrayList<IncorrectColumnPositioning>();
    private String filename;
    private CustomTable table;
    private Map<TableColumn, List<TableColumn>> dependencies;

    public ColumnOrderModule(String filename, CustomTable table, Map<TableColumn, List<TableColumn>> dependencies) {
        this.filename = filename;
        this.table = table;
        this.dependencies = dependencies;
    }

    public boolean doCheck() {
        for (TableColumn tc : dependencies.keySet()) {
            boolean errorAlreadyFound = false;

            List<Integer> columnViewIndices = new ArrayList<Integer>();
            List<Integer> columnViewIndicesWithAddedDependencies = new ArrayList<Integer>();

            Map<Integer, String> viewToNameMapping = new HashMap<Integer, String>();

            int columnViewIndex = table.convertColumnIndexToView(tc.getModelIndex());
            int parentColIndex = columnViewIndex;

            columnViewIndices.add(columnViewIndex);
            columnViewIndicesWithAddedDependencies.add(columnViewIndex);

            viewToNameMapping.put(columnViewIndex, tc.getHeaderValue().toString());

            for (TableColumn dependentcolumn : dependencies.get(tc)) {
                columnViewIndex = table.convertColumnIndexToView(dependentcolumn.getModelIndex());
                columnViewIndices.add(columnViewIndex);
                columnViewIndicesWithAddedDependencies.add(columnViewIndex);
                viewToNameMapping.put(columnViewIndex, dependentcolumn.getHeaderValue().toString());

                if (dependencies.get(dependentcolumn) != null) {
                    for (TableColumn additionalDependency : dependencies.get(dependentcolumn)) {
                        columnViewIndicesWithAddedDependencies.add(table.convertColumnIndexToView(additionalDependency.getModelIndex()));
                    }
                }
            }

            Collections.sort(columnViewIndices);
            StringBuffer pattern = new StringBuffer();
            for (Integer i : columnViewIndices) {
                pattern.append(viewToNameMapping.get(i)).append(" ");
            }

            Collections.sort(columnViewIndicesWithAddedDependencies);

            int nextExpectedValue = columnViewIndicesWithAddedDependencies.get(0);

            String hasAText = tc.getHeaderValue().toString().contains("Factor Value") ||
                    tc.getHeaderValue().toString().contains("Characteristics") ||
                    tc.getHeaderValue().toString().contains("Parameter") ? "Unit" : "Parameter";

            for (Integer i : columnViewIndicesWithAddedDependencies) {
                if (nextExpectedValue != i) {
                    if (!tc.getHeaderValue().toString().toLowerCase().equals("row no.")) {
                        if (!report.containsKey(filename)) {
                            report.put(filename, new ArrayList<String>());
                        }
                        report.get(filename).add(tc.getHeaderValue() + " has a " + hasAText + " column in the wrong position");
                        errorAlreadyFound = true;
                        break;
                    } else {
                        break;
                    }
                } else {
                    nextExpectedValue++;
                }
            }

            if (!errorAlreadyFound) {
                // do a check on the actual order of the column headers rather than just the sorted indexes
                boolean patternMatches = patternMatch(tc.getHeaderValue().toString(), pattern.toString(), filename, hasAText);
                errorAlreadyFound = !patternMatches;
            }

            if (errorAlreadyFound) {
                IncorrectColumnPositioning icor = new IncorrectColumnPositioning(columnViewIndices, parentColIndex);
                incorrectColumnPositions.add(icor);
            }
        }

        return report.isEmpty();
    }

    public Map<String, List<String>> getReport() {
        return report;
    }

    public List<IncorrectColumnPositioning> getIncorrectColumnPositions() {
        return incorrectColumnPositions;
    }

    public boolean patternMatch(String headerValue, String pattern, String fileName, String hasAText) {

        String patternBeingMatchedAgainst = getPattern(headerValue);
        Pattern p = Pattern.compile(patternBeingMatchedAgainst);
        Matcher m = p.matcher(pattern.trim());

        log.info("pattern = " + pattern);

        if (!m.matches()) {
            if (!report.containsKey(fileName)) {
                report.put(fileName, new ArrayList<String>());
            }
            report.get(fileName).add(headerValue + " has a " + hasAText + " column in the wrong position");
            return false;
        }
        return true;
    }

    private String getPattern(String dependentColumn) {
        String dependentColumnLC = dependentColumn.toLowerCase();
        if (dependentColumnLC.contains("factor")) {
            return FACTOR_REGEX_PATTERN;
        } else if (dependentColumnLC.contains("protocol")) {
            return PROTOCOL_REGEX_PATTERN;
        } else if (dependentColumnLC.contains("characteristic")) {
            return CHARACTERISTICS_REGEX_PATTERN;
        } else if (dependentColumnLC.contains("parameter")) {
            return PARAMETER_REGEX_PATTERN;
        }

        return ".*";
    }


}
