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

package org.isatools.isacreator.wizard;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class StudySampleCreationAlgorithm extends CreationAlgorithm {

    private static final Logger log = Logger.getLogger(StudySampleCreationAlgorithm.class.getName());

    private static final String SOURCE_NAME_PLACE_HOLDER = "<SOURCE_NAME>";
    private static final String SAMPLE_NAMES_PLACE_HOLDER = "<SAMPLE_NAME>";

    private Assay studySample;
    private Map<String, GeneratedSampleDetails> generatedSampleInfo;
    private TableReferenceObject buildingModel;
    private String organism;
    private String rowFormat;
    private List<PropertyType> factorsToAdd;

    public StudySampleCreationAlgorithm(Study study, Assay studySample, List<PropertyType> factorsToAdd,
                                        Map<String, GeneratedSampleDetails> generatedSampleInfo, String organism,
                                        TableReferenceObject buildingModel) {
        super(buildingModel, study, factorsToAdd);

        this.studySample = studySample;
        this.factorsToAdd = factorsToAdd;
        this.generatedSampleInfo = generatedSampleInfo;
        this.buildingModel = buildingModel;
        this.organism = organism;
    }

    private void addRowsToModel(String[] headers) {
        for (String studySampleRecord : generatedSampleInfo.keySet()) {

            GeneratedSampleDetails gsd = generatedSampleInfo.get(studySampleRecord);

            String extractName = gsd.getExtractName().contains(".Pooled") ? gsd.getExtractName().replaceAll(".Pooled", "") : gsd.getExtractName();
            String sourceName = gsd.getSourceName();

            String rowInfo = rowFormat.replaceAll(SOURCE_NAME_PLACE_HOLDER,
                    sourceName);

            rowInfo = rowInfo.replaceAll(SAMPLE_NAMES_PLACE_HOLDER,
                    studySampleRecord);

            rowInfo = rowInfo.replaceAll("\\[ORGANISM_PART\\]", extractName);
            // replace with val of map :o)
            rowInfo = rowInfo.replaceAll("\\[FACTORS\\]", gsd.getTreatmentGroup());
            log.info("row info for study file : " + rowInfo);
            buildingModel.addRowData(headers, rowInfo.split("\t"));

        }
    }

    public Map<String, GeneratedSampleDetails> getSampleData() {
        return null;
    }

    void performAssayCentricTask() {
        // do nothing!
    }

    public void runAlgorithm() {
        // need to create table reference object for study sample file first.
        tableStructure = buildingModel.getTableStructure();

        headers = new ArrayList<String>();

        FieldObject newFo;

        int count = tableStructure.size() + 1;
        rowFormat = "";

        for (int i = 0; i < tableStructure.size(); i++) {

            String fieldName = tableStructure.get(i)[0];
            if (fieldName == null) {
                fieldName = "";
            }

            if (colsToUse == null) {
                colsToUse = new ListOrderedSet<Integer>();
            }

            if (fieldName.toLowerCase().equals("characteristics")) {

                headers.add("Characteristics[Organism]");
                newFo = new FieldObject(count, "Characteristics[Organism]",
                        "The organism being studied", DataTypes.ONTOLOGY_TERM, "",
                        false, false, false);
                // todo set recommended ontology for organism...
                buildingModel.addField(newFo);

                count++;
                rowFormat += (organism + "\t");

                headers.add("Characteristics[Organism Part]");
                newFo = new FieldObject(count, "Characteristics[Organism Part]", "Part of the organism being studied", DataTypes.ONTOLOGY_TERM, "",
                        false, false, false);
                buildingModel.addField(newFo);
                count++;
                rowFormat += "[ORGANISM_PART]\t";

            } else if (fieldName.toLowerCase().contains("characteristics[")) {
                // then a check needs to be done to ensure the characteristic isn't added if it is already there!
                String value = fieldName.toLowerCase();
                if (!value.contains("organism") && !value.contains("organism part")) {
                    headers.add(fieldName);
                    colsToUse.add(i);
                    rowFormat += " \t";
                }
            } else if (fieldName.toLowerCase().equals("factors")) {
                for (PropertyType tf : factorsToAdd) {

                    headers.add("Factor Value[" + tf.getPropertyName().trim() + "]");

                    boolean unitAdded = false;

                    for (TimeUnitType tup : tf.getValuesAndUnits()) {
                        if (!tup.getUnit().trim().equals("")) {
                            headers.add("Unit");
                            unitAdded = true;

                            break;
                        }
                    }

                    if (unitAdded) {
                        // add factor column field first
                        newFo = new FieldObject(count,
                                "Factor Value[" + tf.getPropertyName().trim() +
                                        "]", "Factor", DataTypes.STRING, "", false,
                                false, false);
                        buildingModel.addField(newFo);
                        count++;

                        newFo = new FieldObject(count, "Unit",
                                "Unit to give meaning to it's associated value",
                                DataTypes.ONTOLOGY_TERM, "", false, false, false);
                        buildingModel.addField(newFo);
                        count++;
                    } else {
                        newFo = new FieldObject(count,
                                "Factor Value[" + tf.getPropertyName().trim() +
                                        "]", "Factor", DataTypes.ONTOLOGY_TERM, "",
                                false, false, false);
                        buildingModel.addField(newFo);
                        count++;
                    }
                }

                rowFormat += "[FACTORS]";

            } else if (fieldName.toLowerCase().contains("factor value[")) {
                if (!checkForFactorExistence(fieldName)) {
                    headers.add(fieldName);
                    colsToUse.add(i);
                    rowFormat += " \t";
                }
            } else {

                headers.add(fieldName);

                if (fieldName.equalsIgnoreCase("source name")) {
                    rowFormat += (SOURCE_NAME_PLACE_HOLDER + "\t");
                } else if (fieldName.equalsIgnoreCase(
                        "sample name")) {
                    rowFormat += SAMPLE_NAMES_PLACE_HOLDER + "\t";
                } else {
                    rowFormat += " \t";
                }
            }
        }

        Vector<String> headersForReferenceObject = new Vector<String>();
        String[] headersAsArray = new String[headers.size()];
        headersForReferenceObject.add(TableReferenceObject.ROW_NO_TEXT);

        for (int i = 0; i < headers.size(); i++) {
            headersForReferenceObject.add(headers.get(i));
            headersAsArray[i] = headers.get(i);
        }

        buildingModel.setPreDefinedHeaders(headersForReferenceObject);

        addRowsToModel(headersAsArray);

        studySample.setTableReferenceObject(buildingModel);
    }
}
