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

import com.sun.tools.corba.se.idl.toJavaPortable.StringGen;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;

import java.util.*;

/**
 * @author Eamonn Maguire
 * @date Feb 9, 2009
 */


public class OntologySourceManager {
    public static final String OLS_TEXT = "OLS";

    // map from the investigation accession to the OntologySourceReference Objects used
    private static Map<String, List<OntologySourceRefObject>> usedOntologySources = new HashMap<String, List<OntologySourceRefObject>>();

    private static Map<String, OntologySourceRefObject> completeOntologySourceDictionary = new HashMap<String, OntologySourceRefObject>();

    private static Map<String, OntologyObject> ontologySelectionHistory = new HashMap<String, OntologyObject>();

    public static Map<String, OntologyObject> getUserOntologyHistory() {
        return ontologySelectionHistory;
    }

    public static void setOntologySelectionHistory(Map<String, OntologyObject> ontologySelectionHistory) {
        OntologySourceManager.ontologySelectionHistory = ontologySelectionHistory;
    }


    public static void placeRecommendedOntologyInformationInRecords(Collection<RecommendedOntology> recommendedOntologies) {
        if (recommendedOntologies != null) {
            for (RecommendedOntology ro : recommendedOntologies) {
                Ontology o = ro.getOntology();

                completeOntologySourceDictionary.put(o.getOntologyAbbreviation(),
                        new OntologySourceRefObject(o.getOntologyAbbreviation(), "", o.getOntologyVersion(), o.getOntologyDisplayLabel()));
            }
        }
    }

    public static String getOntologyDescription(String ontologyAbbreviation) {
        if (completeOntologySourceDictionary.containsKey(ontologyAbbreviation)) {
            return completeOntologySourceDictionary.get(ontologyAbbreviation).getSourceDescription();
        }

        return "";
    }

    public static String getOntologyVersion(String ontologyAbbreviation) {
        if (completeOntologySourceDictionary.containsKey(ontologyAbbreviation)) {
            return completeOntologySourceDictionary.get(ontologyAbbreviation).getSourceVersion();
        }

        return "";
    }

    public static void addOLSOntologyDefinitions(Map<String, String> ontologyAbbrevToNames, Map<String, String> ontologyAbbrevToVersion) {

        for (String ontologyAbbreviation : ontologyAbbrevToNames.keySet()) {
            completeOntologySourceDictionary.put(ontologyAbbreviation, new OntologySourceRefObject(ontologyAbbreviation, "",
                    ontologyAbbrevToVersion.get(ontologyAbbreviation), ontologyAbbrevToNames.get(ontologyAbbreviation)));
        }
    }

    /**
     * Add an OntologySourceRefObject to the list of defined Ontologies
     *
     * @param osro - OntologySourceReferenceObject to be added.
     */
    public static void addToUsedOntologies(OntologySourceRefObject osro) {
        String key = getValidKey();
        removeAnyPreexistingOntologySourceRefForUpdate(key, osro);
        getOntologiesUsed(key).add(osro);
    }

    private static String getValidKey() {
        if(usedOntologySources.isEmpty()) {
            usedOntologySources.put("investigation", new ArrayList<OntologySourceRefObject>());
            return "investigation";
        } else {
            for (String key : usedOntologySources.keySet()) {
                return key;
            }
        }

        return "investigation";
    }

    /**
     * Add an OntologySourceRefObject to the list of defined Ontologies
     *
     * @param osro - OntologySourceReferenceObject to be added.
     */
    public static void addToUsedOntologies(String investigationAccession, OntologySourceRefObject osro) {
        removeAnyPreexistingOntologySourceRefForUpdate(investigationAccession, osro);
        getOntologiesUsed(investigationAccession).add(osro);
    }

    public static void clearUsedOntologies(String investigationAccession) {
        if (usedOntologySources.containsKey(investigationAccession)) {
            usedOntologySources.remove(investigationAccession);
        }
    }

    public static List<OntologySourceRefObject> getOntologiesUsed(String investigationAccession) {
        return usedOntologySources.get(investigationAccession);
    }

    public static List<OntologySourceRefObject> getOntologiesUsed() {
        return getAllOntologiesUsed();
    }

    private static List<OntologySourceRefObject> getAllOntologiesUsed() {
        List<OntologySourceRefObject> sourceRefObjects = new ArrayList<OntologySourceRefObject>();
        for (String investigationAcc : usedOntologySources.keySet()) {
            sourceRefObjects.addAll(usedOntologySources.get(investigationAcc));
        }

        return sourceRefObjects;
    }

    public static void setOntologiesUsed(String investigationAccession, List<OntologySourceRefObject> ontologiesUsed) {
        usedOntologySources.put(investigationAccession, ontologiesUsed);
    }

    public static void removeAnyPreexistingOntologySourceRefForUpdate(String investigationAccession, OntologySourceRefObject osro) {

        if (usedOntologySources.get(investigationAccession) != null) {
            Iterator<OntologySourceRefObject> iterator = usedOntologySources.get(investigationAccession).iterator();
            while (iterator.hasNext())

                if (iterator.next().getSourceName().equals(osro.getSourceName())) {
                    iterator.remove();
                    return;
                }
        }
    }

    public static void clearReferencedOntologySources() {
        usedOntologySources.clear();
    }

    public static void clearUserHistory() {
        ontologySelectionHistory.clear();
    }

    public static void addToUsedOntologySources(String investigationAccession, List<OntologySourceRefObject> ontologiesToAdd) {
        for (OntologySourceRefObject osro : ontologiesToAdd) {
            addToUsedOntologySources(investigationAccession, osro);
        }
    }

    public static void addToUsedOntologySources(String investigationAccession, OntologySourceRefObject ontologyToAdd) {
        removeAnyPreexistingOntologySourceRefForUpdate(investigationAccession, ontologyToAdd);
        usedOntologySources.get(investigationAccession).add(ontologyToAdd);

    }




    public static void newInvestigation(String investigationAccession) {

        usedOntologySources.put(investigationAccession, new ArrayList<OntologySourceRefObject>());
        usedOntologySources.get(investigationAccession).add(new OntologySourceRefObject("OBI", "", "", "Ontology for Biomedical Investigations"));
    }

    public static void addToUserHistory(OntologyObject oo) {
        if (!ontologySelectionHistory.containsKey(oo.getUniqueId())) {
            ontologySelectionHistory.put(oo.getUniqueId(), oo);
        }
    }
}
