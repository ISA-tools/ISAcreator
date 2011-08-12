package org.isatools.isacreator.gui.formelements.assay;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.Assay;

import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 12/08/2011
 *         Time: 15:45
 */
public class AssayInformationWriter {


    public String printAssays(
            Collection<Assay> assays, List<MappingObject> mappingObjects) {


        Map<String, OrderedMap<String, String>> assayToInformation = new HashMap<String, OrderedMap<String, String>>();

        for (Assay assay : assays) {

            assayToInformation.put(assay.getAssayReference(), new ListOrderedMap<String, String>());

            MappingObject technology = getMappingObjectForAssayValue(assay.getTechnologyType(), mappingObjects);
            MappingObject measurement = getMappingObjectForAssayValue(assay.getMeasurementEndpoint(), mappingObjects);

            assayToInformation.get(assay.getAssayReference()).put(Assay.MEASUREMENT_ENDPOINT, assay.getMeasurementEndpoint());
            assayToInformation.get(assay.getAssayReference()).put(Assay.MEASUREMENT_ENDPOINT + " Term Source REF", measurement.getMeasurementSource());
            assayToInformation.get(assay.getAssayReference()).put(Assay.MEASUREMENT_ENDPOINT + " Term Accession Number", measurement.getMeasurementAccession());


            assayToInformation.get(assay.getAssayReference()).put(Assay.TECHNOLOGY_TYPE, assay.getTechnologyType());
            assayToInformation.get(assay.getAssayReference()).put(Assay.TECHNOLOGY_TYPE + " Term Source REF", technology.getTechnologySource());
            assayToInformation.get(assay.getAssayReference()).put(Assay.TECHNOLOGY_TYPE + " Term Accession Number", technology.getTechnologyAccession());

            assayToInformation.get(assay.getAssayReference()).put(Assay.ASSAY_PLATFORM, assay.getAssayPlatform());
            assayToInformation.get(assay.getAssayReference()).put(Assay.ASSAY_REFERENCE, assay.getAssayReference());

        }

        return outputAssayMapAsString(assayToInformation);
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
