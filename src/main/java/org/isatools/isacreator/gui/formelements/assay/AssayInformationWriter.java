package org.isatools.isacreator.gui.formelements.assay;

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

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.Assay;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by the ISA team
 */
public class AssayInformationWriter {


    public String printAssays(Collection<Assay> assays, List<MappingObject> mappingObjects) {


        Map<String, OrderedMap<String, String>> assayToInformation = new HashMap<String, OrderedMap<String, String>>();

        if (assays.size() > 0) {


            for (Assay assay : assays) {

                assayToInformation.put(assay.getAssayReference(), new ListOrderedMap<String, String>());

                MappingObject technology = getMappingObjectForAssayValue(assay.getTechnologyType(), mappingObjects);
                MappingObject measurement = getMappingObjectForAssayValue(assay.getMeasurementEndpoint(), mappingObjects);


                assayToInformation.get(assay.getAssayReference()).put(Assay.MEASUREMENT_ENDPOINT, assay.getMeasurementEndpoint());
                assayToInformation.get(assay.getAssayReference()).put(Assay.MEASUREMENT_ENDPOINT + " Term Source REF", measurement == null
                        ? "" : measurement.getMeasurementSource());
                assayToInformation.get(assay.getAssayReference()).put(Assay.MEASUREMENT_ENDPOINT + " Term Accession Number", measurement == null
                        ? "" : measurement.getMeasurementAccession());


                assayToInformation.get(assay.getAssayReference()).put(Assay.TECHNOLOGY_TYPE, assay.getTechnologyType());
                assayToInformation.get(assay.getAssayReference()).put(Assay.TECHNOLOGY_TYPE + " Term Source REF", technology == null
                        ? "" : technology.getTechnologySource());
                assayToInformation.get(assay.getAssayReference()).put(Assay.TECHNOLOGY_TYPE + " Term Accession Number", technology == null
                        ? "" : technology.getTechnologyAccession());

                assayToInformation.get(assay.getAssayReference()).put(Assay.ASSAY_PLATFORM, assay.getAssayPlatform());
                assayToInformation.get(assay.getAssayReference()).put(Assay.ASSAY_REFERENCE, assay.getAssayReference());


            }
        } else {
            createDefaultAssaySection(assayToInformation);
        }

        return outputAssayMapAsString(assayToInformation);
    }

    private void createDefaultAssaySection(Map<String, OrderedMap<String, String>> assayToInformation) {
        assayToInformation.put("", new ListOrderedMap<String, String>());

        assayToInformation.get("").put(Assay.MEASUREMENT_ENDPOINT, "");
        assayToInformation.get("").put(Assay.MEASUREMENT_ENDPOINT + " Term Source REF", "");
        assayToInformation.get("").put(Assay.MEASUREMENT_ENDPOINT + " Term Accession Number", "");


        assayToInformation.get("").put(Assay.TECHNOLOGY_TYPE, "");
        assayToInformation.get("").put(Assay.TECHNOLOGY_TYPE + " Term Source REF", "");
        assayToInformation.get("").put(Assay.TECHNOLOGY_TYPE + " Term Accession Number", "");

        assayToInformation.get("").put(Assay.ASSAY_PLATFORM, "");
        assayToInformation.get("").put(Assay.ASSAY_REFERENCE, "");

    }

    private String outputAssayMapAsString(Map<String, OrderedMap<String, String>> assayToInformation) {

        Map<Integer, StringBuilder> lineNumberToContents = new HashMap<Integer, StringBuilder>();

        int count = 0;
        for (String assayReference : assayToInformation.keySet()) {

            int lineNumber = 0;

            for (String field : assayToInformation.get(assayReference).keySet()) {

                if (!lineNumberToContents.containsKey(lineNumber)) {
                    lineNumberToContents.put(lineNumber, new StringBuilder());
                }

                if (count == 0) {
                    lineNumberToContents.get(lineNumber).append(field);
                    lineNumberToContents.get(lineNumber).append("\t");
                }

                lineNumberToContents.get(lineNumber).append("\"").append(assayToInformation.get(assayReference).get(field)).append("\"");


                if (count != assayToInformation.size() - 1) {
                    lineNumberToContents.get(lineNumber).append("\t");
                }

                lineNumber++;

            }

            count++;
        }

        StringBuilder finalRepresentation = new StringBuilder();

        finalRepresentation.append(InvestigationFileSection.STUDY_ASSAYS.toString()).append("\n");

        for (int lineNumber = 0; lineNumber < lineNumberToContents.size(); lineNumber++) {
            finalRepresentation.append(lineNumberToContents.get(lineNumber)).append("\n");
        }

        return finalRepresentation.toString();
    }

    private MappingObject getMappingObjectForAssayValue(String value, List<MappingObject> mappingObjects) {
        for (MappingObject mo : mappingObjects) {
            if (mo.getMeasurementEndpointType().equals(value) || mo.getTechnologyType().equals(value)) {
                return mo;
            }
        }

        return null;
    }
}
