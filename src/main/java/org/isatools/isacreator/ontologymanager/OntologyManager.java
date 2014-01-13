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

package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.settings.ISAcreatorProperties;

import java.util.*;

/**
 * @author Eamonn Maguire
 * @date Feb 9, 2009
 */
public class OntologyManager {

    //ontology term providers
    public static final String OLS = "OLS";
    public static final String BIO_PORTAL = "BioPortal";

    //a map with <investigation, list of ontology source reference objects>
    private static Map<String, List<OntologySourceRefObject>> usedOntologySources = new HashMap<String, List<OntologySourceRefObject>>();

    private static Map<String, OntologySourceRefObject> completeOntologySourceDictionary = new HashMap<String, OntologySourceRefObject>();

    private static Map<String, OntologyTerm> ontologySelectionHistory = new HashMap<String, OntologyTerm>();

    private static ResultCache<String, Map<OntologySourceRefObject, List<OntologyTerm>>> searchResultCache = new ResultCache<String, Map<OntologySourceRefObject, List<OntologyTerm>>>();

    //map with <shortForm, new OntologyTerm> used, for ontology terms for which it is not possible to find a URI
    private static Map<String, OntologyTerm> noURITermMap = new HashMap<String, OntologyTerm>();

    private static void addToNoURITermMap(String k, OntologyTerm v){
        noURITermMap.put(k, v);
    }

    public static void addToUserHistory(OntologyTerm oo) {
        if (oo!=null)
            addToOntologySelectionHistory(oo.getShortForm(), oo);//ontologySelectionHistory.put(oo.getShortForm(), oo);
    }

    public static void setOntologySelectionHistory(Map<String, OntologyTerm> ontologySelectionHistory) {
        OntologyManager.ontologySelectionHistory = new HashMap<String, OntologyTerm>();
        OntologyManager.ontologySelectionHistory.putAll(ontologySelectionHistory);
    }

    public static void addToOntologySelectionHistory(String label, OntologyTerm term) {
        if (noURITermMap.containsKey(label))
            return;
        if (ontologySelectionHistory.containsKey(label))
            return;
        if (ISAcreatorProperties.getOntologyTermURIProperty() && term.getOntologyTermURI()!=null && !term.getOntologyTermURI().equals("")){
            ontologySelectionHistory.put(label, term);
        } else {
            addToNoURITermMap(label, term);
            //System.out.println("Term does not have a URI ---> " + label);
        }
    }

    public static void addToOntologySelectionHistory(Map<String, OntologyTerm> osh) {
        //OntologyManager.ontologySelectionHistory.putAll(osh);
        for(String key: osh.keySet())
            addToOntologySelectionHistory(key, osh.get(key));
    }

    public static int getOntologySelectionHistorySize(){
        return ontologySelectionHistory.size();
    }

    public static Collection<OntologyTerm> getOntologySelectionHistoryValues(){
        return ontologySelectionHistory.values();
    }

    public static Set<String> getOntologySelectionHistoryKeySet(){
        return ontologySelectionHistory.keySet();
    }

    public static OntologyTerm getOntologyTerm(String key){
        return ontologySelectionHistory.get(key);
//        if (ISAcreatorProperties.getProperty(ISAcreatorProperties.ONTOLOGY_TERM_URI).equals("true")){
//            if (noURITermMap.containsKey(key))
//                return noURITermMap.get(key);
//            else
//                return null;
//        }else return ontologySelectionHistory.get(key);
    }

    public static String getOntologyTermPurl(String dataValue){
        OntologyTerm ontologyTerm = getOntologyTerm(dataValue);
        return ontologyTerm!=null? ontologyTerm.getOntologyTermURI() : null;
    }

    public static String getOntologyTermAccession(String dataValue){
        OntologyTerm ontologyTerm = getOntologyTerm(dataValue);
        return ontologyTerm!=null? ontologyTerm.getOntologyTermAccession() : null;
    }

    public static String getOntologyTermSource(String dataValue){
        OntologyTerm ontologyTerm = getOntologyTerm(dataValue);
        return ontologyTerm!=null? ontologyTerm.getOntologySource() : null;
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
        //removeAnyPreexistingOntologySourceRefForUpdate(key, osro);
        getOntologiesUsed(key).add(osro);
        //addToUsedOntologySources(key, osro);
    }

    private static String getValidKey() {
        if (usedOntologySources.isEmpty()) {
            usedOntologySources.put("investigation", new ArrayList<OntologySourceRefObject>());
            return "investigation";
        } else {
            if(usedOntologySources != null && usedOntologySources.size() > 0)
                return usedOntologySources.keySet().iterator().next();
        }
        return "investigation";
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
        List<OntologySourceRefObject> sourceRefObjects = new ArrayList<OntologySourceRefObject>();

        Set<String> addedOntologySourceRefs = new HashSet<String>();

        for (String investigationAcc : usedOntologySources.keySet()) {

            List<OntologySourceRefObject> refObjects = usedOntologySources.get(investigationAcc);

            for (OntologySourceRefObject refObject : refObjects) {

                if (!addedOntologySourceRefs.contains(refObject.getSourceName())) {
                    sourceRefObjects.add(refObject);

                    addedOntologySourceRefs.add(refObject.getSourceName());
                }
            }

        }

        return sourceRefObjects;
    }

    public static void setOntologiesUsed(String investigationAccession, List<OntologySourceRefObject> ontologiesUsed) {
        usedOntologySources.put(investigationAccession, ontologiesUsed);
    }

//    public static void removeAnyPreexistingOntologySourceRefForUpdate(String investigationAccession, OntologySourceRefObject osro) {
//
//        if (usedOntologySources.get(investigationAccession) != null) {
//            Iterator<OntologySourceRefObject> iterator = usedOntologySources.get(investigationAccession).iterator();
//            while (iterator.hasNext())
//
//                if (iterator.next().getSourceName().equals(osro.getSourceName())) {
//                    iterator.remove();
//                    return;
//                }
//        }
//    }

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
        //removeAnyPreexistingOntologySourceRefForUpdate(investigationAccession, ontologyToAdd);

        if (!usedOntologySources.containsKey(investigationAccession)) {
            usedOntologySources.put(investigationAccession, new ArrayList<OntologySourceRefObject>());
        }
        usedOntologySources.get(investigationAccession).add(ontologyToAdd);
    }


    public static void newInvestigation(String investigationAccession) {
        usedOntologySources.put(investigationAccession, new ArrayList<OntologySourceRefObject>());
    }

    public static OntologySourceRefObject getOntologySourceReferenceObjectByAbbreviation(String source) {
        for (OntologySourceRefObject ontologySourceRefObject : getOntologiesUsed()) {
            if (source.equalsIgnoreCase(ontologySourceRefObject.getSourceName())) {
                return ontologySourceRefObject;
            }
        }
        return null;
    }

    public static void clearResultCache() {
        searchResultCache.clearCache();
    }

    public static Map<OntologySourceRefObject, List<OntologyTerm>> getSearchResultCacheValue(String key) {
        return searchResultCache.get(key);
    }

    public static void addToCache(String key, Map<OntologySourceRefObject, List<OntologyTerm>> value) {
        searchResultCache.put(key, value);
    }

    public static boolean searchResultCacheContainsKey(String key){
        return searchResultCache.containsKey(key);
    }

    public static int searchResultCacheSize(){
        return searchResultCache.size();
    }


    public static String getURIMappingInfo(){

        StringBuilder builder = new StringBuilder();

        if (!noURITermMap.isEmpty()){
            builder.append("ISA-TAB dataset loaded with URIs");
            builder.append("\nTerms that could not be mapped to a URI: ");
            for(String key: noURITermMap.keySet()){
                OntologyTerm ot = noURITermMap.get(key);
                builder.append("\n\t"+ ot.getOntologyTermName()+ "\t"+ ot.getOntologySource() +"\t" + ot.getOntologyTermURI());
                }

            builder.append("\nTerms that could be mapped to a URI: ");
            for(String key: ontologySelectionHistory.keySet()){
                OntologyTerm ot = ontologySelectionHistory.get(key);
                builder.append("\n\t"+ ot.getOntologyTermName() + "\t"+ ot.getOntologySource() +"\t" + ot.getOntologyTermURI());
            }
            }
        return builder.toString();
     }

    public static String getURIMappingInfoHTML(){

        StringBuilder builder = new StringBuilder();

        if (!noURITermMap.isEmpty()){
            builder.append("ISA-TAB dataset loaded with URIs");
            builder.append("<br><br>Terms that could not be mapped to a URI: ");
            for(String key: noURITermMap.keySet()){
                OntologyTerm ot = noURITermMap.get(key);
                builder.append("<p style=\"padding-left:10px\">"+ ot.getOntologySource()+":"+ot.getOntologyTermName() +" </p>");
            }

            builder.append("<br>Terms that could be mapped to a URI: ");
            for(String key: ontologySelectionHistory.keySet()){
                OntologyTerm ot = ontologySelectionHistory.get(key);
                builder.append("<p style=\"padding-left:10px\">"+ ot.getOntologySource()+":"+ot.getOntologyTermName() +" -> <b>" + ot.getOntologyTermURI()+" </b> </p>");
            }

        }
        return builder.toString();
    }

}
