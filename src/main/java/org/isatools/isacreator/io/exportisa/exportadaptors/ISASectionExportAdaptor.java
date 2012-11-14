package org.isatools.isacreator.io.exportisa.exportadaptors;

import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.ISASection;
import org.isatools.isacreator.model.InvestigationContact;
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
                isaSection.getFieldValues().put(ontologyField.get(IOUtils.TERM),
                        processedOntologyField.get(IOUtils.TERM));
                isaSection.getFieldValues().put(ontologyField.get(IOUtils.ACCESSION),
                        processedOntologyField.get(IOUtils.ACCESSION));
                isaSection.getFieldValues().put(ontologyField.get(IOUtils.SOURCE_REF),
                        processedOntologyField.get(IOUtils.SOURCE_REF));
            }
        }
    }

    public static String exportISASectionAsString(List<? extends ISASection> isaSections, InvestigationFileSection fileSection) {
        return exportISASectionAsString(isaSections, fileSection, new HashMap<String, String>());
    }

    public static String exportISASectionAsString(List<? extends ISASection> isaSections, InvestigationFileSection fileSection, Map<String, String> aliasesToRealNames) {
        // find an efficient way of outputting the block.

        StringBuilder output = new StringBuilder();
        output.append(fileSection).append("\n");

        ISASection firstSection = isaSections.get(0);
        List<String> fieldNames = firstSection.getFieldKeysAsList();
        for (int fieldIndex = 0; fieldIndex < getNumberOfLinesInSection(firstSection); fieldIndex++) {
            StringBuilder line = new StringBuilder();
            String fieldName = fieldNames.get(fieldIndex);

            line.append(fieldName).append("\t");

            int sectionCount = 0;
            for (ISASection section : isaSections) {
                line.append(section.getValue(fieldName));
                line.append(sectionCount != isaSections.size() - 1 ? "\t" : "\n");
                sectionCount++;
            }

            output.append(line);
        }

        return output.toString();
    }

    private static int getNumberOfLinesInSection(ISASection section) {
        return section.getFieldValues().size();
    }
}
