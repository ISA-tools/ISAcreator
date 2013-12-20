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


package org.isatools.isacreator.ontologymanager.bioportal.io;

import org.apache.commons.lang.StringUtils;
import org.isatools.isacreator.configuration.Ontology;

import java.util.*;

/**
 * AcceptedOntologies
 *
 * @author eamonnmaguire
 * @date Mar 30, 2010
 */
public class AcceptedOntologies {

    private static Map<String, Ontology> acceptedOntologies;

    // This is used to cache lookups on ontology sources, e.g. EFO to their equivalent ID in BioPortal
    private static Map<String,String> ontologySourceToIDCache;

    static {
        updateAcceptedOntologies();
    }

    public static void updateAcceptedOntologies() {
        acceptedOntologies = AcceptedOntologiesLoader.getAcceptedOntologies();
        ontologySourceToIDCache = new HashMap<String, String>();
    }

    /**
     * Returns say OBI when 1123 is supplied
     *
     * @param ontologyId e.g. 1123 for OBI
     * @return when 1123 is supplied, OBI will be returned
     */
    public static String getOntologyAbbreviationFromId(String ontologyId) {
        if(acceptedOntologies.containsKey(ontologyId))
            return acceptedOntologies.get(ontologyId).getOntologyAbbreviation();
        return null;
    }


    public static Map<String, String> getOntologySourceToNames() {
        Map<String, String> ontologySourceToName = new HashMap<String, String>();
        for (Ontology ontology : acceptedOntologies.values()) {
            ontologySourceToName.put(ontology.getOntologyAbbreviation(), ontology.getOntologyDisplayLabel());
        }
        return ontologySourceToName;
    }

    public static Map<String, String> getOntologySourceToVersion() {
        Map<String, String> ontologySourceToName = new HashMap<String, String>();
        for (Ontology ontology : acceptedOntologies.values()) {
            ontologySourceToName.put(ontology.getOntologyAbbreviation(), ontology.getOntologyVersion());
        }
        return ontologySourceToName;
    }

    public static String getOntologyIdForAbbreviation(String abbreviation) {
        if(ontologySourceToIDCache.containsKey(abbreviation)) return ontologySourceToIDCache.get(abbreviation);
        for(Ontology ontology :  acceptedOntologies.values()) {
            if(ontology.getOntologyAbbreviation().equals(abbreviation)) {
                ontologySourceToIDCache.put(abbreviation, ontology.getOntologyID());
                return ontology.getOntologyID();
            }
        }

        return null;
    }


    public static String getAllowedOntologyAcronyms(Set<Ontology> toIgnore) {
        StringBuilder allowedOntologies = new StringBuilder();

        int count = 0;
        for (Ontology ontology : acceptedOntologies.values()) {
            if (!toIgnore.contains(ontology) && StringUtils.trimToNull(ontology.getOntologyAbbreviation()) != null) {
                allowedOntologies.append(ontology.getOntologyAbbreviation());
                if (count != acceptedOntologies.size() - 1) {
                    allowedOntologies.append(",");
                }
            }
            count++;
        }

        return allowedOntologies.toString();
    }



    public static Map<String, Ontology> getAcceptedOntologies() {
        return acceptedOntologies;
    }

    public static Collection<Ontology> values() {
        return acceptedOntologies.values();
    }
}