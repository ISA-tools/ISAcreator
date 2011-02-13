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

package org.isatools.isacreator.mgrast.utils;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

import java.util.*;

/**
 * APIHook
 *
 * @author eamonnmaguire
 * @date Sep 30, 2010
 */


public class APIHook {
    private ISAcreator isacreatorEnvironment;
    private String studyId;

    // Map contains mapping from either
    private Map<Object, Map<String, Integer>> isatabFields;
    private Map<Object, Integer> studyOrAssayToSampleNameColumn;
    private Set<String> sampleNames;

    public APIHook(ISAcreator isacreatorEnvironment, String studyId) {

        this.isacreatorEnvironment = isacreatorEnvironment;
        this.studyId = studyId;
    }

    /**
     * Method goes through the ISAtab model and pulls out the fields describing the sample (from study sample files) and
     * and fields describing
     */
    private Map<Object, Map<String, Integer>> loadFields() {
        isatabFields = new HashMap<Object, Map<String, Integer>>();

        Set<String> unwantedColumns = generateUnwantedColumnsSet("Unit", "Protocol REF", "File");

        Study study = getInvestigation().getStudies().get(studyId);

        if (study != null) {
            isatabFields.put(study,
                    SpreadsheetUtils.getColumnNames(study.getStudySample().getSpreadsheetUI().getTable(), unwantedColumns));

            for (Assay a : study.getAssays().values()) {
                if (a.getTechnologyType().contains("sequencing")) {
                    isatabFields.put(a,
                            SpreadsheetUtils.getColumnNames(a.getSpreadsheetUI().getTable(), unwantedColumns));
                }
            }
        }
        return isatabFields;
    }

    /**
     * Method pulls out all the information of interest for output. It returns each sample along with the data corresponding to
     * that sample.
     *
     * @param sampleNames - Sample names we are looking for.
     * @return Map<String, Map<String,String>> - SampleName ->Columns|Values
     */
    public Map<String, Map<String, List<String>>> getDataForSamples(Set<String> sampleNames) {
        // map of column name to value
        Map<String, Map<String, List<String>>> sampleData = new HashMap<String, Map<String, List<String>>>();

        // precautionary measure in case the user hasn't looked at the sample id assignments!
        if (studyOrAssayToSampleNameColumn == null) {
            getSampleNames();
        }

        for (Object assayOrStudy : isatabFields.keySet()) {
            // check for rows of interest.

            // get table
            Spreadsheet spreadsheet;

            if (assayOrStudy instanceof Study) {
                spreadsheet = ((Study) assayOrStudy).getStudySample().getSpreadsheetUI().getTable();
            } else {
                spreadsheet = ((Assay) assayOrStudy).getSpreadsheetUI().getTable();
            }

            for (int rowNo = 0; rowNo < spreadsheet.getTable().getRowCount(); rowNo++) {
                // test to ensure that the row contains the sample we want
                String candidateSample = spreadsheet.getTable().getValueAt(rowNo, studyOrAssayToSampleNameColumn.get(assayOrStudy)).toString();

                if (sampleNames.contains(candidateSample)) {
                    // then proceed with adding details to the record
                    if (!sampleData.containsKey(candidateSample)) {
                        sampleData.put(candidateSample, new HashMap<String, List<String>>());
                    }

                    for (String field : isatabFields.get(assayOrStudy).keySet()) {

                        int column = isatabFields.get(assayOrStudy)
                                .get(field);

                        String value = spreadsheet.getTable().getValueAt(rowNo, column).toString();

                        // check if the field has a unit, if there is a unit, it will be added to the
                        String unit = spreadsheet.getAssignedUnitForColumn(column, rowNo);

                        // try and pick out a unit from the header value

                        if (unit.equals("")) {
                            unit = extractUnitFromFieldName(field);
                            if (!unit.equals("")) {
                                value += " " + unit;
                            }
                        } else {
                            value += " " + unit;
                        }

                        if (!sampleData.get(candidateSample).containsKey(field)) {
                            sampleData.get(candidateSample).put(field, new ArrayList<String>());
                        }
                        // todo investigate why this is not adding multiple values for one sample (replicates)!
                        sampleData.get(candidateSample).get(field).add(value);
                    }
                }
            }
        }

        return sampleData;
    }

    /**
     * If, for example we have mg/L as a string in the Characteristics[Chla mg/L] field,
     * we want to extract this as the unit automatically
     *
     * @param fieldName - field to check
     * @return Unit if one is available
     */
    private String extractUnitFromFieldName(String fieldName) {
        String unit = "";
        System.out.println("attempting to extract unit from " + fieldName);
        if (fieldName.contains("Characteristic") || fieldName.contains("Factor") || fieldName.contains("Parameter")
                || fieldName.contains("Comment")) {
            String tmpField = cleanupString(fieldName);
            String[] fragments = tmpField.split(" ");
            if (fragments.length > 1) {
                String lastFragment = fragments[fragments.length - 1].trim();
                if (lastFragment.length() <= 4 || lastFragment.contains("\\") || lastFragment.contains("/")) {
                    return lastFragment;
                }
            }
        }

        return unit;
    }

    private String cleanupString(String toClean) {
        return toClean.replaceAll("(Characteristics\\[)|(Factor Value\\[)|(Parameter Value\\[)|(Comment\\[)|\\]", "");
    }


    public Set<String> getISAtabFields() {
        if (isatabFields == null) {
            loadFields();
        }

        Set<String> fields = new ListOrderedSet<String>();

        for (Object studyOrAssay : isatabFields.keySet()) {
            Map<String, Integer> columns = isatabFields.get(studyOrAssay);
            fields.addAll(columns.keySet());
        }

        return fields;
    }

    public Set<String> getSampleNames() {
        // if the set has already been created, the just use this :)

        if (sampleNames != null) {
            return sampleNames;
        }

        // else retrieve the sample names since it's being performed for the first time.
        if (isatabFields == null) {
            loadFields();
        }

        studyOrAssayToSampleNameColumn = new HashMap<Object, Integer>();

        for (Object studyOrAssay : isatabFields.keySet()) {

            Map<String, Integer> columns = isatabFields.get(studyOrAssay);

            for (String column : columns.keySet()) {
                if (column.equalsIgnoreCase("sample name")) {
                    studyOrAssayToSampleNameColumn.put(studyOrAssay, columns.get(column));
                    break;
                }
            }

        }

        return extractSampleIds(studyOrAssayToSampleNameColumn);
    }

    private Set<String> extractSampleIds(Map<Object, Integer> studyOrAssayToSampleNameColumn) {
        sampleNames = new ListOrderedSet<String>();

        // add all sample names from all assays of interest (i.e. those employing sequencing)...
        for (Object assay : studyOrAssayToSampleNameColumn.keySet()) {

            if (assay instanceof Assay) {
//
//				JTable dataSource = ((Assay) assay).getSpreadsheetUI().getTable().getTable();
//
//				for (int rowNo = 0; rowNo < dataSource.getRowCount(); rowNo++) {
//					String value = dataSource.getValueAt(rowNo, studyOrAssayToSampleNameColumn.get(assay)).toString();
//					if (value != null && !value.trim().equals("")) {
//						sampleNames.add(value);
//					}
//				}

                sampleNames.addAll(SpreadsheetUtils.getDataInColumn(((Assay) assay).getSpreadsheetUI().getTable(),
                        studyOrAssayToSampleNameColumn.get(assay)));
            }
        }

        return sampleNames;
    }

    private Set<String> generateUnwantedColumnsSet(String... unwantedColumns) {
        Set<String> result = new HashSet<String>();

        result.addAll(Arrays.asList(unwantedColumns));

        return result;
    }

    private Investigation getInvestigation() {
        return isacreatorEnvironment.getDataEntryEnvironment().getInvestigation();
    }
}
