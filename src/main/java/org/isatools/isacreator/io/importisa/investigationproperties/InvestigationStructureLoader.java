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

package org.isatools.isacreator.io.importisa.investigationproperties;

import org.isatools.isacreator.io.xpath.XPathReader;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * InvestigationStructureLoader loads the XML file describing the salient items of an Investigation file
 * using XPath
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 */
public class InvestigationStructureLoader {

    private static final String INVESTIGATION_STRUCTURE_FILE = "/defaultConfigs/investigationStructure.xml";

    private Map<InvestigationFileSection, InvestigationSection> investigationSections;

    public Map<InvestigationFileSection, InvestigationSection> loadInvestigationStructure() {
        XPathReader reader = new XPathReader(getClass().getResourceAsStream(INVESTIGATION_STRUCTURE_FILE));

        NodeList sections = (NodeList) reader.read("/investigationFileStructure/section", XPathConstants.NODESET);

        if (sections.getLength() > 0) {

            investigationSections = new HashMap<InvestigationFileSection, InvestigationSection>();

            for (int sectionIndex = 0; sectionIndex <= sections.getLength(); sectionIndex++) {
                String tmpName = (String) reader.read("/investigationFileStructure/section[" + sectionIndex + "]/name", XPathConstants.STRING);

                // we don't want any empty sections getting through
                if (!tmpName.trim().equals("")) {

                    InvestigationFileSection name = InvestigationFileSection.convertToInstance(tmpName);

                    InvestigationSection currentSection = new InvestigationSection(name);

                    NodeList requiredValueNodeList = (NodeList) reader.read("/investigationFileStructure/section[" + sectionIndex + "]/requiredValues/value", XPathConstants.NODESET);

                    for (int requiredValueIndex = 0; requiredValueIndex <= requiredValueNodeList.getLength(); requiredValueIndex++) {
                        String requiredLabel = (String) reader.read("/investigationFileStructure/section[" + sectionIndex + "]/requiredValues/value[" + requiredValueIndex + "]", XPathConstants.STRING);

                        if (!requiredLabel.trim().equals("")) {
                            currentSection.getRequiredValues().add(requiredLabel);
                        }
                    }
                    investigationSections.put(name, currentSection);
                }
            }

            return investigationSections;
        }

        return new HashMap<InvestigationFileSection, InvestigationSection>();
    }

    /**
     * Given "Investigation" or "Study" will return all of the elements required for each part.
     *
     * @param type - "Investigation" or "Study"
     * @return @see Set<InvestigationFileSection>
     */
    public Set<InvestigationFileSection> getRequiredSections(String type) {
        Set<InvestigationFileSection> result = new HashSet<InvestigationFileSection>();

        type = type.toUpperCase();

        for (InvestigationFileSection section : investigationSections.keySet()) {
            if (section.toString().contains(type)) {
                result.add(section);
            }
        }

        if (type.equalsIgnoreCase("INVESTIGATION")) {
            result.add(InvestigationFileSection.ONTOLOGY_SECTION);
        }

        return result;
    }
}
