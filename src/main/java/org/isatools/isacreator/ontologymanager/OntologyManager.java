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
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 * @date Feb 9, 2009
 */
public class OntologyManager {

    //ontology term providers
    public static final String OLS = "OLS";
    public static final String BIO_PORTAL = "BioPortal";

    private static boolean foundURIs = false;

    //All the ontologySourceRefObject, indexed by their (abbreviated) name
    private static Map<String, OntologySourceRefObject> ontologySources = new HashMap<String, OntologySourceRefObject>();

    //a map with <ontology term label, ontology term object> for each of the ontology terms used for annotating the ISA-TAB dataset
    private static Map<String, OntologyTerm> ontologyTerms = new HashMap<String, OntologyTerm>();

    //a map with <ontology term label, ontology term object> = history of the ontology terms used - maintained in the user profile
    private static Map<String, OntologyTerm> ontologyTermHistory = new HashMap<String, OntologyTerm>();

    //map with <shortForm, OntologyTerm> - used for ontology terms for which it is not possible to find a URI
    private static Map<String, OntologyTerm> noURITermMap = new HashMap<String, OntologyTerm>();

    private static ResultCache<String, Map<OntologySourceRefObject, List<OntologyTerm>>> searchResultCache = new ResultCache<String, Map<OntologySourceRefObject, List<OntologyTerm>>>();

    private static void addToNoURITermMap(String k, OntologyTerm v){
        noURITermMap.put(k, v);
    }

    public static void foundURI(){
        foundURIs = true;
    }

    /***   ontologyTermHistory methods ****/

    public static void addToOntologyTermHistory(OntologyTerm oo) {
        if (oo!=null)
            addToOntologyTermHistory(oo.getShortForm(), oo);
    }

    public static Map<String, OntologyTerm> getOntologySelectionHistory() {
        return ontologyTerms;
    }

    /**
     *
     * @param label
     * @param term
     */
    public static void addToOntologyTermHistory(String label, OntologyTerm term) {
        if (ISAcreatorProperties.getOntologyTermURIProperty())
            if (term.getOntologyTermURI()!=null && !term.getOntologyTermURI().equals(""))
                ontologyTermHistory.put(label, term);
        else
            ontologyTermHistory.put(label, term);

    }

    public static void setOntologyTermHistory(Map<String, OntologyTerm> history) {
        if (history!=null && history.size()==0)
            return;
        ontologyTermHistory = new HashMap<String, OntologyTerm>();
        ontologyTermHistory.putAll(history);
    }

    public static void clearOntologyTermHistory() {
        ontologyTermHistory.clear();
    }
    /***   end of ontologyTermHistory methods ****/

    /***   ontologyTerms methods ****/
    public static void addToOntologyTerms(Map<String, OntologyTerm> osh) {
        for(String key: osh.keySet())
            addToOntologyTerms(key, osh.get(key));
    }

    public static void addToOntologyTerms(Collection<OntologyTerm> terms) {
        for(OntologyTerm term: terms)
            addToOntologyTerms(term.getShortForm(), term);
    }

    public static void addToOntologyTerms(OntologyTerm term){
        if (term!=null)
            addToOntologyTerms(term.getShortForm(), term);
    }

    public static void addToOntologyTerms(String label, OntologyTerm term) {
        if (noURITermMap.containsKey(label))
            return;
        if (ontologyTerms.containsKey(label))
            return;
        if (term.getOntologyTermURI()!=null && !term.getOntologyTermURI().equals("")){
            ontologyTerms.put(label, term);
            addToOntologyTermHistory(label, term);
            //TODO do I need this? --- addToUsedOntologies(term.getOntologySourceInformation());
        } else {
            addToNoURITermMap(label, term);
        }
    }

    public static int getOntologyTermsSize(){
        return ontologyTerms.size();
    }

    public static Collection<OntologyTerm> getOntologyTermsValues(){
        return ontologyTerms.values();
    }

    public static Set<String> getOntologyTermsKeySet(){
        return ontologyTerms.keySet();
    }

    public static OntologyTerm getOntologyTerm(String key){
        return ontologyTerms.get(key);
    }

    public static String getOntologyTermURI(String dataValue){
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

    public static void clearOntologyTerms(){
        ontologyTerms.clear();
    }

    /**
     * Retrieves the ontology sources corresponding to used ontology terms
     *
     *
     * @return
     */
    public static Set<OntologySourceRefObject> getOntologiesUsed(){
        Set<OntologySourceRefObject> set = new HashSet<OntologySourceRefObject>();
        for(OntologyTerm ot: ontologyTerms.values()){
            set.add(ot.getOntologySourceInformation());
        }
        return set;
    }

    /***   end of ontologyTerms methods ****/

    /***   ontologySources methods ****/

    public static void placeRecommendedOntologyInformationInRecords(Collection<RecommendedOntology> recommendedOntologies) {
        if (recommendedOntologies != null) {
            for (RecommendedOntology ro : recommendedOntologies) {
                Ontology o = ro.getOntology();

                ontologySources.put(o.getOntologyAbbreviation(),
                        new OntologySourceRefObject(o.getOntologyAbbreviation(), "", o.getOntologyVersion(), o.getOntologyDisplayLabel()));
            }
        }
    }

    public static void addOntologySource(OntologySourceRefObject osro){
        ontologySources.put(osro.getSourceName(), osro);
    }

    public static String getOntologyDescription(String ontologyAbbreviation) {
        if (ontologySources.containsKey(ontologyAbbreviation)) {
            return ontologySources.get(ontologyAbbreviation).getSourceDescription();
        }
        return "";
    }

    public static String getOntologyVersion(String ontologyAbbreviation) {
        if (ontologySources.containsKey(ontologyAbbreviation)) {
            return ontologySources.get(ontologyAbbreviation).getSourceVersion();
        }

        return "";
    }

    public static String getSourceFile(String ontologyAbbreviation) {
        if (ontologySources.containsKey(ontologyAbbreviation)) {
            return ontologySources.get(ontologyAbbreviation).getSourceFile();
        }

        return "";
    }

    public static void addOLSOntologyDefinitions(Map<String, String> ontologyAbbrevToNames, Map<String, String> ontologyAbbrevToVersion) {

        for (String ontologyAbbreviation : ontologyAbbrevToNames.keySet()) {
            ontologySources.put(ontologyAbbreviation, new OntologySourceRefObject(ontologyAbbreviation, "",
                    ontologyAbbrevToVersion.get(ontologyAbbreviation), ontologyAbbrevToNames.get(ontologyAbbreviation)));
        }
    }

    public static Collection<OntologySourceRefObject> getOntologySources(){
        return ontologySources.values();
    }

    public static void clearOntologySources(){
        ontologySources.clear();
    }

    public static void setOntologySources(Set<OntologySourceRefObject> ontologiesUsed) {
        for (OntologySourceRefObject sourceRefObject: ontologiesUsed)
            ontologySources.put(sourceRefObject.getSourceName(), sourceRefObject);
    }

    public static OntologySourceRefObject getOntologySourceReferenceObjectByAbbreviation(String source) {
        for (OntologySourceRefObject ontologySourceRefObject : getOntologySources()) {
            if (source.equalsIgnoreCase(ontologySourceRefObject.getSourceName())) {
                return ontologySourceRefObject;
            }
        }
        return null;
    }
    /*** end of ontologySources methods ***/


    /*** searchResultCache methods ***/

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
    /*** end of searchResultCache methods ***/


    /*** getURIMappingInfo ***/

    public static String getURIMappingInfo(){

        if (!foundURIs)
            return "";

        StringBuilder builder = new StringBuilder();

        if (!noURITermMap.isEmpty()){
            builder.append("ISA-TAB dataset loaded with URIs");
            builder.append("\nTerms that could not be mapped to a URI: ");
            for(String key: noURITermMap.keySet()){
                OntologyTerm ot = noURITermMap.get(key);
                builder.append("\n\t"+ ot.getOntologyTermName()+ "\t"+ ot.getOntologySource() +"\t" + ot.getOntologyTermURI());
                }
        }

        if (!ontologyTerms.isEmpty()){
            builder.append("\nTerms that could be mapped to a URI: ");
            for(String key: ontologyTerms.keySet()){
                OntologyTerm ot = ontologyTerms.get(key);
                builder.append("\n\t"+ ot.getOntologyTermName() + "\t"+ ot.getOntologySource() +"\t" + ot.getOntologyTermURI());
            }
            }
        return builder.toString();
     }

    public static String getURIMappingInfoHTML(){

        if (!foundURIs)
            return "";

        StringBuilder builder = new StringBuilder();

        if (!noURITermMap.isEmpty()){
            builder.append("ISA-TAB dataset loaded with URIs");
            builder.append("<br><br>Terms that could not be mapped to a URI: ");
            for(String key: noURITermMap.keySet()){
                OntologyTerm ot = noURITermMap.get(key);
                builder.append("<p style=\"padding-left:10px\">"+ ot.getOntologySource()+":"+ot.getOntologyTermName() +" </p>");
            }
        }

        if (!ontologyTerms.isEmpty()){
            builder.append("<br>Terms that could be mapped to a URI: ");
            for(String key: ontologyTerms.keySet()){
                OntologyTerm ot = ontologyTerms.get(key);
                builder.append("<p style=\"padding-left:10px\">"+ ot.getOntologySource()+":"+ot.getOntologyTermName() +" -> <b>" + ot.getOntologyTermURI()+" </b> </p>");
            }

        }
        return builder.toString();
    }

    /*** end of getURIMappingInfo ***/

}
