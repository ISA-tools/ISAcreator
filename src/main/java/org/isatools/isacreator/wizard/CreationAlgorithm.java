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
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creation Algorithm Interface
 */
public abstract class CreationAlgorithm extends JPanel {

    protected List<String> headers = new ArrayList<String>();
    protected Set<Integer> colsToUse;
    protected TableReferenceObject buildingModel;
    protected Study study;
    protected List<TempFactors> factorsToAdd;
    protected Map<Integer, String[]> tableStructure;

    public CreationAlgorithm(TableReferenceObject buildingModel, Study study, List<TempFactors> factorsToAdd) {
        this.buildingModel = buildingModel;
        this.study = study;
        this.factorsToAdd = factorsToAdd;
    }

    abstract Map<String, GeneratedSampleDetails> getSampleData();

    abstract void performAssayCentricTask();

    protected void runAlgorithm() {
        // Retrieve a list of string values dictating where elements are supposed to occur in a table.
        System.out.println("Running algorithm");
        tableStructure = buildingModel.getTableStructure();
        headers = new ArrayList<String>();
        colsToUse = new ListOrderedSet<Integer>();
        int count = tableStructure.size() + 1;
        for (int i = 0; i < tableStructure.size(); i++) {
            // modify this to only add those factors which appear in temp factors.
            if (tableStructure.get(i)[0].toLowerCase().equals("factors")) {
                for (TempFactors factor : factorsToAdd) {
                    String newFactor = "Factor Value[" + factor.getFactorName().trim() + "]";
                    addFactor(count, newFactor, factor, i);
                }
            } else if (tableStructure.get(i)[0].toLowerCase().contains("factor value[")) {
                // doing nothing with this factor
            } else if (tableStructure.get(i)[0].toLowerCase().contains("characteristics[")) {

                String newChar = tableStructure.get(i)[0];

                headers.add(newChar);
                colsToUse.add(i);

            } else {
                headers.add(tableStructure.get(i)[0]);
                colsToUse.add(i);
            }
        }

        performAssayCentricTask();
    }

    private int addFactor(int count, String newFactor, TempFactors factor, int nextIndex) {
        headers.add(newFactor);
        colsToUse.add(nextIndex);

        FieldObject newFo;
        boolean unitAdded = false;

        for (TimeUnitPair tup : factor.getFactorLevels()) {
            if (!tup.getUnit().trim().equals("")) {
                headers.add("Unit");
                unitAdded = true;
                break;
            }
        }

        if (unitAdded) {
            // add factor column field first
            newFo = new FieldObject(count,
                    newFactor, "Factor", DataTypes.STRING, "",
                    false, false, false);
            buildingModel.addField(newFo);
            count++;

            newFo = new FieldObject(count, "Unit",
                    "Unit to give meaning to it's associated value",
                    DataTypes.ONTOLOGY_TERM, "",
                    false, false, false);
            buildingModel.addField(newFo);
            count++;
        } else {
            newFo = new FieldObject(count,
                    newFactor, "Factor", DataTypes.ONTOLOGY_TERM, "",
                    false, false, false);
            buildingModel.addField(newFo);
            count++;
        }

        return count;
    }

    /**
     * This is a check employed to ensure that we are not adding factors that
     * are already defined inside the configuration.
     *
     * @param columnName - Name of column to be added.
     * @return boolean - true if already present, false otherwise
     */
    protected boolean checkForFactorExistence(String columnName) {

        for (TempFactors tf : factorsToAdd) {
            String fullFactorName = "Factor Value[" + tf.getFactorName() + "]";
            if (fullFactorName.equalsIgnoreCase(columnName)) {
                return true;
            }
        }

        return false;
    }


    public String replaceStringModelValues(String dataWithReplacementTerms,
                                           String institution, int groupNo, int subjectNo, String targetExtract,
                                           int labelNo, String arrayDesign) {
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[INSTITUTION\\]",
                institution);
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[GROUP_NO\\]",
                String.valueOf(groupNo));
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[SUBJECT_NO\\]",
                String.valueOf(subjectNo));
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[SAMPLE_EXTRACT\\]",
                targetExtract);
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[EXTRACT_COUNT\\]",
                String.valueOf(labelNo));
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[LABEL_COUNT\\]",
                String.valueOf(labelNo));
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[ARRAY_DESIGN\\]",
                arrayDesign);
        dataWithReplacementTerms = dataWithReplacementTerms.replaceAll("\\[HYB_COUNT\\]",
                String.valueOf(1));

        return dataWithReplacementTerms;
    }


}
