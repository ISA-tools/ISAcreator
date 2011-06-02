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

package org.isatools.isacreator.ontologyselectiontool;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eamonn Maguire
 * @date Feb 9, 2009
 */


public class OntologySourceManager {
    public static final String OLS_TEXT = "OLS";

    private static Map<String, String> ontologyDescription = new HashMap<String, String>();
    private static Map<String, String> ontologyVersions = new HashMap<String, String>();


    public static void appendOntologyDescriptions(Map<String, String> ontologyDescription) {
        if (ontologyDescription != null) {
            OntologySourceManager.ontologyDescription.putAll(ontologyDescription);
        }
    }

    public static void appendOntologyVersions(Map<String, String> ontologyVersions) {
        if (ontologyVersions != null) {
            OntologySourceManager.ontologyVersions.putAll(ontologyVersions);
        }
    }

    public static String getOntologyDescription(String shortSource) {
        String source = ontologyDescription.get(shortSource);
        if (source != null) {
            return source;
        } else {
            return "";
        }
    }

    public static String getOntologyVersion(String shortSource) {
        String version = "";

        printMap(ontologyVersions);

        if (ontologyVersions.containsKey(shortSource)) {
            version = ontologyVersions.get(shortSource);
        } else {
            version = ontologyVersions.get(OLS_TEXT);
        }

        if (version != null) {
            return version;
        } else {
            return "";
        }
    }

    private static void printMap(Map<String, String> map) {
        for(String key : map.keySet()) {
            System.out.println(key + " -> " + map.get(key));
        }
    }

    public static void placeRecommendedOntologyInformationInRecords(Collection<RecommendedOntology> recommendedOntologies) {
        if (recommendedOntologies != null) {
            for (RecommendedOntology ro : recommendedOntologies) {
                Ontology o = ro.getOntology();
                ontologyDescription.put(o.getOntologyAbbreviation(), o.getOntologyDisplayLabel());
                ontologyVersions.put(o.getOntologyAbbreviation(), o.getOntologyVersion());
            }
        }
    }
}
