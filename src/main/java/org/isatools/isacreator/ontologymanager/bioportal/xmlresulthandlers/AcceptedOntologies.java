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


package org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers;

import java.util.Set;

/**
 * AcceptedOntologies
 *
 * @author eamonnmaguire
 * @date Mar 30, 2010
 */
public enum AcceptedOntologies {
    OBI("1123", "OBI"), EFO("1136", "EFO"), NCI_THESAURUS("1032", "NCIt"), CHEBI("1007", "CHEBI"), NPO("1083", "NPO"),
    SNOMED("1353", "SNOMEDCT"), MDR("1422", "MDR"), PATO("1107", "PATO"), UO("1112", "UO"), BFO("1005", "BFO"),
    NCBITaxon("1132", "NCBITaxon"), PLANT_ONTOLOGY("1587", "PO"), BAO("1533", "BAO"), EDAM("1498", "EDAM");

    private String ontologyID;
    private String ontologyAbbreviation;

    AcceptedOntologies(String ontologyID, String ontologyAbbreviation) {

        this.ontologyID = ontologyID;
        this.ontologyAbbreviation = ontologyAbbreviation;
    }

    @Override
    public String toString() {
        return ontologyID;
    }

    public String getOntologyAbbreviation() {
        return ontologyAbbreviation;
    }

    public String getOntologyID() {
        return ontologyID;
    }

    /**
     * Returns say OBI when 1123 is supplied
     *
     * @param ontologyId e.g. 1123 for OBI
     * @return when 1123 is supplied, OBI will be returned
     */
    public static String getOntologyAbbreviationFromId(String ontologyId) {
        for (AcceptedOntologies ontology : values()) {
            if (ontology.getOntologyID().equals(ontologyId)) {
                return ontology.getOntologyAbbreviation();
            }
        }
        return null;
    }

    public static String getAllowedOntologyIds(Set<AcceptedOntologies> toIgnore) {
        StringBuilder allowedOntologies = new StringBuilder();

        int count = 0;
        for (AcceptedOntologies ontology : values()) {
            if (!toIgnore.contains(ontology)) {
                allowedOntologies.append(ontology.getOntologyID());
                if (count != values().length - 1) {
                    allowedOntologies.append(",");
                }

            }
            count++;
        }

        return allowedOntologies.toString();
    }
}