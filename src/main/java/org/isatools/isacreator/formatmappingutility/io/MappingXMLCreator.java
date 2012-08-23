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

package org.isatools.isacreator.formatmappingutility.io;

import org.isatools.isacreator.assayselection.AssaySelection;
import org.isatools.isacreator.io.CustomizableFileFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * MappingXMLCreator creates the XML required by the mapping tool to recreate mappings for fields!
 *
 * @author Eamonn Maguire
 * @date Oct 29, 2009
 */


public class MappingXMLCreator {

    public void createXMLFile(String fileName, Map<String, ISAFieldMapping> mappings, List<AssaySelection> assaySelections) throws FileNotFoundException {

        StringBuffer xmlRep = createMappingXML(mappings, assaySelections);

        File mappingFile = new File(fileName);

        if (mappingFile.isDirectory()) {
            String mappingFileName = mappingFile.getAbsolutePath() + File.separator + "isacreator_mapping.xml";
            mappingFile = new File(mappingFileName);
        } else if (!CustomizableFileFilter.getExtension(mappingFile).equalsIgnoreCase("xml")) {
            String mappingFileName = mappingFile.getAbsolutePath();
            if (mappingFileName.contains(".")) {
                mappingFile = new File(mappingFile.getAbsolutePath().substring(0, mappingFileName.lastIndexOf(".")) + "xml");
            } else {
                mappingFile = new File(mappingFile.getAbsolutePath() + ".xml");
            }
        }

        PrintStream ps = new PrintStream(mappingFile);
        ps.print(xmlRep.toString());
        ps.close();
    }


    /**
     * Creates the XML and returns it in a StringBuffer
     *
     * @param mappings - Map containing ISAFieldMappings to be output
     * @return a StringBuffer containing the XML representation of the Mapping
     */
    private StringBuffer createMappingXML(Map<String, ISAFieldMapping> mappings, List<AssaySelection> assaySelections) {
        StringBuffer xmlRep = new StringBuffer();

        xmlRep.append("<isa-field-mappings xmlns=\"http://www.ebi.ac.uk/bii/isacreator_mapping_configuration#\">");

        for (String field : mappings.keySet()) {

            xmlRep.append("<isa-field column-name=\"").append(field).append("\">");

            ISAFieldMapping mappingDetails = mappings.get(field);
            // create field-mapping section - should always be present
            createRepresentationOfISAField(mappingDetails.getField(), "field-mapping", xmlRep);

            // create unit mapping section, if unit is available
            if (mappingDetails.hasUnit()) {
                createRepresentationOfISAField(mappingDetails.getUnit(), "unit-mapping", xmlRep);
            }

            // create date mapping section, if date is available
            if (mappingDetails.hasDate()) {
                createRepresentationOfISAField(mappingDetails.getDate(), "date-mapping", xmlRep);
            }

            // create provider mapping section, if provider is available
            if (mappingDetails.hasProvider()) {
                createRepresentationOfISAField(mappingDetails.getPerformer(), "performer-mapping", xmlRep);
            }

            xmlRep.append("</isa-field>");
        }

        xmlRep.append(createAssayInformation(assaySelections));

        xmlRep.append("</isa-field-mappings>");

        return xmlRep;
    }

    private StringBuffer createAssayInformation(List<AssaySelection> assaySelections) {
        StringBuffer assayXMLRep = new StringBuffer();

        assayXMLRep.append("<assays-used>");
        for (AssaySelection assaySelection : assaySelections) {
            assayXMLRep.append("<assayUsed>");
            assayXMLRep.append("<measurement>").append(assaySelection.getMeasurement()).append("</measurement>");

            if (!assaySelection.getTechnology().isEmpty()) {
                assayXMLRep.append("<technology>").append(assaySelection.getTechnology()).append("</technology>");
            }

            if (!assaySelection.getPlatform().isEmpty()) {
                assayXMLRep.append("<platform>").append(assaySelection.getPlatform()).append("</platform>");
            }

            assayXMLRep.append("</assayUsed>");
        }
        assayXMLRep.append("</assays-used>");

        return assayXMLRep;
    }

    private void createRepresentationOfISAField(List<ISAField> mappings, String type, StringBuffer xml) {
        xml.append("<").append(type).append(">");
        for (ISAField mappingField : mappings) {
            xml.append("<mapped-field>");
            xml.append("<value>").append(mappingField.getMapping()).append("</value>");
            xml.append("<type>").append(mappingField.getType().toString()).append("</type>");
            xml.append("</mapped-field>");
        }
        xml.append("</").append(type).append(">");
    }

}
