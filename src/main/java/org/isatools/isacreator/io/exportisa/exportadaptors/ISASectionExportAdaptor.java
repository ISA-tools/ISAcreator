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
package org.isatools.isacreator.io.exportisa.exportadaptors;

import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.utils.StringProcessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         Date: 02/07/2012
 *         Time: 12:01
 */
public class ISASectionExportAdaptor {

    public static String exportISASectionAsString(ISASection isaSection, InvestigationFileSection fileSection) {
        return exportISASectionAsString(isaSection, fileSection, new HashMap<String, String>());
    }

    public static String exportISASectionAsString(ISASection isaSection, InvestigationFileSection fileSection, Map<String, String> aliasesToRealNames) {
        StringBuilder output = new StringBuilder();
        output.append(fileSection).append("\n");
        processSectionOntologyFields(isaSection);

        // now, do output
        for (String fieldName : isaSection.getFieldValues().keySet()) {

            String tmpFieldName = fieldName;

            if (aliasesToRealNames.containsKey(fieldName)) {
                tmpFieldName = aliasesToRealNames.get(fieldName);
            }
            output.append(tmpFieldName).append("\t\"").append((
                    StringProcessing.cleanUpString(isaSection.getFieldValues().get(tmpFieldName)))).append("\"\n");
        }

        return output.toString();
    }

    private static void processSectionOntologyFields(ISASection isaSection) {
        Set<String> ontologyFields = IOUtils.filterFields(isaSection.getFieldValues().keySet(), IOUtils.ACCESSION, IOUtils.SOURCE_REF);
        Map<Integer, Map<String, String>> ontologyTerms = IOUtils.getOntologyTerms(isaSection.getFieldValues().keySet());
        // now, do ontology processing
        for (String fieldName : ontologyFields) {
            int fieldHashCode = fieldName.substring(0, fieldName.toLowerCase().indexOf("term")).trim().hashCode();
            if (ontologyTerms.containsKey(fieldHashCode)) {
                Map<String, String> ontologyField = ontologyTerms.get(fieldHashCode);
                Map<String, String> processedOntologyField = IOUtils.processOntologyField(ontologyField, isaSection.getFieldValues());

                for (String key : processedOntologyField.keySet()) {
                    if (processedOntologyField.get(key)!=null && !processedOntologyField.get(key).isEmpty()) {
                        isaSection.getFieldValues().put(key, processedOntologyField.get(key));
                    }
                }
            }
        }
    }

    public static String exportISASectionAsString(List<? extends ISASection> isaSections, InvestigationFileSection fileSection) {
        return exportISASectionAsString(isaSections, fileSection, new HashMap<String, String>());
    }

    public static String exportISASectionAsString(List<? extends ISASection> isaSections, InvestigationFileSection isaSection, Map<String, String> aliasesToRealNames) {
        // find an efficient way of outputting the block.

        StringBuilder output = new StringBuilder();
        output.append(isaSection).append("\n");

        addMissingSectionIfRequired(isaSections, isaSection);

        ISASection firstSection = isaSections.get(0);
        List<String> fieldNames = firstSection.getFieldKeysAsList();

        for (ISASection section : isaSections) {
            processSectionOntologyFields(section);
        }

        for (int fieldIndex = 0; fieldIndex < getNumberOfLinesInSection(firstSection); fieldIndex++) {
            StringBuilder line = new StringBuilder();
            String fieldName = fieldNames.get(fieldIndex);

            line.append(fieldName).append("\t");

            int sectionCount = 0;
            for (ISASection section : isaSections) {

                String value = section.getValue(fieldName);

                line.append("\"").append(value).append("\"");
                line.append(sectionCount != isaSections.size() - 1 ? "\t" : "\n");
                sectionCount++;
            }

            output.append(line);
        }

        return output.toString();
    }

    private static void addMissingSectionIfRequired(List<? extends ISASection> isaSections, InvestigationFileSection fileSection) {
        if (isaSections.size() == 0) {
            if (fileSection == InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION) {
                ((List<InvestigationPublication>) isaSections).add(new InvestigationPublication());
            }
            if (fileSection == InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION) {

                ((List<InvestigationContact>) isaSections).add(new InvestigationContact());
            }

            // for each study generate study sections if they don't exist.

            if (fileSection == InvestigationFileSection.STUDY_DESIGN_SECTION) {
                ((List<StudyDesign>) isaSections).add(new StudyDesign());
            }

            if (fileSection == InvestigationFileSection.STUDY_FACTORS) {
                ((List<Factor>) isaSections).add(new Factor());
            }

            if (fileSection == InvestigationFileSection.STUDY_ASSAYS) {
                ((List<Assay>) isaSections).add(new Assay());
            }

            if (fileSection == InvestigationFileSection.STUDY_PROTOCOLS) {
                ((List<Protocol>) isaSections).add(new Protocol());
            }

            if (fileSection == InvestigationFileSection.STUDY_PUBLICATIONS) {
                ((List<StudyPublication>) isaSections).add(new StudyPublication());
            }

            if (fileSection == InvestigationFileSection.STUDY_CONTACTS) {
                ((List<StudyContact>) isaSections).add(new StudyContact());
            }
        }

    }

    private static int getNumberOfLinesInSection(ISASection section) {
        return section.getFieldValues().size();
    }
}
