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


package org.isatools.isacreator.ontologymanager;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.bioportal.model.BioPortalOntology;
import org.isatools.isacreator.ontologymanager.bioportal.utils.BioPortalXMLModifier;
import org.isatools.isacreator.ontologymanager.bioportal.utils.Modifier;
import org.isatools.isacreator.ontologymanager.bioportal.utils.NCIThesaurusModifier;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.BioPortalClassBeanResultHandler;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.BioPortalOntologyListResultHandler;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.BioPortalSearchBeanResultHandler;
import org.isatools.isacreator.ontologymanager.utils.DownloadUtils;
import org.isatools.isacreator.ontologyselectiontool.OntologySourceManager;

import java.io.File;
import java.util.*;


public class BioPortalClient implements OntologyService {
    private static final Logger log = Logger.getLogger(BioPortalClient.class.getName());

    public static final int PARENTS = 0;

    public static final int CHILDREN = 1;

    public static final String REST_URL = "http://rest.bioontology.org/bioportal/";

    private Set<String> noChildren;

    private boolean doneOntologyCheck = false;

    private List<Ontology> ontologies;

    private Map<String, String> ontologySources;
    private Map<String, String> ontologyVersions;

    private Map<String, BioPortalOntology> searchResults;
    private Map<String, Map<String, String>> cachedNodeChildrenQueries;


    public BioPortalClient() {
        ontologySources = new HashMap<String, String>();
        ontologyVersions = new HashMap<String, String>();
        searchResults = new HashMap<String, BioPortalOntology>();
        cachedNodeChildrenQueries = new HashMap<String, Map<String, String>>();

        noChildren = new HashSet<String>();
    }

    public List<Ontology> getAllOntologies() {

        if (ontologies == null || ontologies.size() == 0) {

            String searchString = REST_URL + "ontologies";

            DownloadUtils.downloadFile(searchString, DownloadUtils.DOWNLOAD_FILE_LOC + "ontologies" + DownloadUtils.XML_EXT);

            BioPortalOntologyListResultHandler parser = new BioPortalOntologyListResultHandler();

            ontologies = parser.parseFile(DownloadUtils.DOWNLOAD_FILE_LOC + "ontologies" + DownloadUtils.XML_EXT);

            for (Ontology ontology : ontologies) {
                ontologyVersions.put(ontology.getOntologyAbbreviation(), ontology.getOntologyVersion());
                ontologySources.put(ontology.getOntologyAbbreviation(), ontology.getOntologyDisplayLabel());
            }
        }

        return ontologies;
    }

    public Ontology getOntologyById(String ontologyId) {
        String searchString = REST_URL + "virtual/ontology/" + ontologyId;
        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + "ontology-info-" + ontologyId + DownloadUtils.XML_EXT;

        DownloadUtils.downloadFile(searchString, downloadLocation);

        BioPortalOntologyListResultHandler parser = new BioPortalOntologyListResultHandler();

        List<Ontology> ontologies = parser.parseFile(downloadLocation);

        if (ontologies != null && !ontologies.isEmpty()) {
            return ontologies.get(0);
        }

        return null;
    }

    public String getLatestOntologyVersion(String ontologyId) {

        Ontology o = getOntologyById(ontologyId);

        if (o == null) {
            return null;
        } else {
            return o.getOntologyVersion();
        }
    }

    public Map<String, String> getOntologyNames() {
        if (ontologySources.size() == 0) {
            getAllOntologies();
        }

        return ontologySources;
    }

    public Map<String, String> getOntologyVersions() {
        if (ontologyVersions.size() == 0) {
            getAllOntologies();
        }

        return ontologyVersions;
    }

    public Map<String, String> getTermMetadata(String termAccession, String ontology) {

        BioPortalOntology ontologyResult = getTermInformation(termAccession, ontology);
        Map<String, String> result = new ListOrderedMap<String, String>();
        result.put("accession", termAccession);

        result.putAll(ontologyResult.getComments());

        return result;
    }

    public BioPortalOntology getTermInformation(String termAccession, String ontology) {
        BioPortalOntology bpo;
        if (searchResults.containsKey(ontology + "-" + termAccession)) {
            bpo = searchResults.get(ontology + "-" + termAccession);
            if (bpo != null) {
                if (bpo.getOntologyPurl() == null ||
                        bpo.getOntologyPurl().trim().equals("")) {
                    bpo = performMetadataQuery(termAccession, ontology);
                    searchResults.put(ontology + "-" + termAccession, bpo);
                } else {
                    return bpo;
                }
            }
        } else {
            bpo = performMetadataQuery(termAccession, ontology);
            searchResults.put(ontology + "-" + termAccession,
                    bpo);
        }
        return bpo;
    }

    private BioPortalOntology performMetadataQuery(String termAccession, String ontology) {
        String searchString = REST_URL + "concepts/" + ontology + "/" + termAccession;

        System.out.println("Search string is: " + searchString);
        log.info("Search string is: " + searchString);

        log.info("Getting term information for " + termAccession + " -> " + searchString);

        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-" + termAccession + DownloadUtils.XML_EXT;

        DownloadUtils.downloadFile(searchString, downloadLocation);

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

        if (fileWithNameSpace != null) {

            BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

            BioPortalOntology result = handler.parseMetadataFile(fileWithNameSpace.getAbsolutePath());

            return result;
        } else {
            return null;
        }
    }

    public Map<String, String> getTermByAccessionId(String id) {
        String searchString = REST_URL + "ontologies";

        DownloadUtils.downloadFile(searchString, DownloadUtils.DOWNLOAD_FILE_LOC + id + DownloadUtils.XML_EXT);

        return null;
    }

    public Map<String, String> getTermsByPartialNameFromSource(String term, List<RecommendedOntology> recommendedOntologies) {

        term = correctTermForHTTPTransport(term);

        // need to accommodate more complicated search strings in the case where the recommended source contains the branch
        // to search under as well!
        Map<String, String> result = new HashMap<String, String>();

        // need to do a loop over all branches and do a single query on those recommended ontologies only defined
        // by the source, not the branch.
        for (RecommendedOntology ro : recommendedOntologies) {

            if (ro.getOntology() != null) {

                StringBuilder searchString = new StringBuilder();

                searchString.append(REST_URL);

                searchString.append("search/").append(term).append("/?ontologyids=").append(ro.getOntology().getOntologyID());


                if (ro.getBranchToSearchUnder() != null && !ro.getBranchToSearchUnder().getBranchIdentifier().equals("")) {
                    String branch = ro.getBranchToSearchUnder().getBranchIdentifier();

                    if (ro.getOntology().getOntologyID().equals(AcceptedOntologies.NCI_THESAURUS.getOntologyID()) || ro.getOntology().getOntologyID().equals(AcceptedOntologies.NPO.getOntologyID())) {
                        Modifier modifier = new NCIThesaurusModifier();
                        branch = modifier.modifySearch(branch);
                    }

                    searchString.append("&subtreerootconceptid=").append(branch);
                }

                System.out.println("sending query: " + searchString);

                Map<String, String> searchResult = downloadAndProcessBranch(term, searchString.toString());

                if (searchResult != null) {
                    result.putAll(searchResult);
                }
            }
        }


        return result;
    }


    private Map<String, String> downloadAndProcessBranch(String term, String searchString) {
        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + term + DownloadUtils.XML_EXT;

        DownloadUtils.downloadFile(searchString, downloadLocation);

        BioPortalSearchBeanResultHandler handler = new BioPortalSearchBeanResultHandler();

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/resultBeanSchema#", "<success>");

        if (fileWithNameSpace != null) {
            Map<String, BioPortalOntology> result = handler.getSearchResults(fileWithNameSpace.getAbsolutePath());

            updateOntologyManagerWithOntologyInformation();

            return processBioPortalOntology(result);
        }

        return new HashMap<String, String>();

    }

    public Map<String, String> getTermsByPartialNameFromSource(String term, String source, boolean reverseOrder) {

        System.out.println("Searching for source: " + source);


        term = correctTermForHTTPTransport(term);
        String searchString = REST_URL + "search/" + term + "/?ontologyids=" + (((source == null) || source.trim().equalsIgnoreCase("") || source.trim().equalsIgnoreCase("all")) ? constructSourceStringFromAllowedOntologies() : source);
        log.info("search string " + searchString);

        Map<String, String> searchResult = downloadAndProcessBranch(term, searchString);

        log.info("found " + (searchResult == null ? "0" : searchResult.size()) + " ontology terms");

        return searchResult == null ? new HashMap<String, String>() : searchResult;
    }

    private void updateOntologyManagerWithOntologyInformation() {
        if (!doneOntologyCheck) {
            for (AcceptedOntologies ao : AcceptedOntologies.values()) {
                Ontology o = getOntologyById(ao.getOntologyID());
                if (o != null) {
                    OntologySourceManager.addOLSOntologyDefinitions(Collections.singletonMap(o.getOntologyAbbreviation(),
                            o.getOntologyDisplayLabel()), Collections.singletonMap(o.getOntologyAbbreviation(), o.getOntologyVersion()));
                }
            }
            doneOntologyCheck = true;
        }
    }

    private String constructSourceStringFromAllowedOntologies() {
        String allowedOntologies = "";

        int count = 0;
        for (AcceptedOntologies ao : AcceptedOntologies.values()) {
            allowedOntologies += ao.getOntologyID();
            if (count < AcceptedOntologies.values().length - 1) {
                allowedOntologies += ",";
            }
            count++;
        }

        return allowedOntologies;
    }

    /**
     * Finds the root in an ontology
     *
     * @param ontology - ontology to search in as it's version ID e.g. 39002 for BRO
     * @return Map<String,String> representing ontology term accession to term label mappings.
     */
    public Map<String, String> getOntologyRoots(String ontology) {

        if (!cachedNodeChildrenQueries.containsKey(ontology)) {

            String searchString = REST_URL + "concepts/" + ontology + "/root";


            System.out.println("Search string is: " + searchString);

            String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-roots" + DownloadUtils.XML_EXT;

            DownloadUtils.downloadFile(searchString, downloadLocation);

            File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

            BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

            if (fileWithNameSpace == null) {
                return new HashMap<String, String>();
            }

            Map<String, BioPortalOntology> result = handler.parseRootConceptFile(fileWithNameSpace.getAbsolutePath(), noChildren);

            Map<String, String> processedResult = new HashMap<String, String>();

            if (result != null) {
                searchResults.putAll(result);
                processedResult.putAll(processBioPortalOntology(result));
                cachedNodeChildrenQueries.put(ontology, processedResult);
            }

            return processedResult;
        } else {
            System.out.println("using cached version for display.");
            return cachedNodeChildrenQueries.get(ontology);
        }
    }

    public Map<String, String> getTermParent(String termAccession, String ontology) {
        return getTermChildOrParent(termAccession, ontology, PARENTS);
    }


    /**
     * Return the children of a term with gathering extra metadata information about the term.
     * This makes creating tree views quicker, although it's primarily for BioPortal WS queries since
     * it requires an extra query per child term to resolve the PURLs etc.
     *
     * @param termAccession - term to search under
     * @param ontology      - ontology term is located in
     * @return Map<String,String> with mappings from term accession to the term label
     */
    public Map<String, String> getTermChildren(String termAccession, String ontology) {
        return getTermChildOrParent(termAccession, ontology, CHILDREN);
    }

    /**
     * Gets the children or parents of a term
     *
     * @param termAccession - accession of term e.g. snap:Continuant
     * @param ontology      - ontology version id e.g. 40832 for OBI
     * @param type          - @see PARENTS, CHILDREN - what type of search to make
     * @return Map<String, String> from term accession -> term label
     */
    public Map<String, String> getTermChildOrParent(String termAccession, String ontology, int type) {

        if (!noChildren.contains(termAccession)) {
            if (!cachedNodeChildrenQueries.containsKey(ontology + "-" + termAccession)) {

                String searchString = REST_URL + "concepts/" + ((type == PARENTS) ? "parents/" : "") + "" + ontology + "?conceptid=" + termAccession;

                System.out.println("Searching for : " + searchString);

                String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-" + termAccession + DownloadUtils.XML_EXT;

                DownloadUtils.downloadFile(searchString, downloadLocation);

                BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

                File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation),
                        "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

                if (fileWithNameSpace != null) {

                    Map<String, BioPortalOntology> result = handler.parseRootConceptFile(fileWithNameSpace.getAbsolutePath(), noChildren);

                    Map<String, String> processedResult = new HashMap<String, String>();

                    if (result != null) {
                        searchResults.putAll(result);
                        processedResult.putAll(processBioPortalOntology(result));
                        cachedNodeChildrenQueries.put(ontology + "-" + termAccession, processedResult);
                    }

                    return processedResult;
                } else {
                    return new HashMap<String, String>();
                }
            } else {
                return cachedNodeChildrenQueries.get(ontology + "-" + termAccession);
            }
        } else {
            return new HashMap<String, String>();
        }

    }

    /**
     * Method will search the Ontology space to determine the parents of a given term. This can then be used in searches to make
     * location of a term within an Ontology much quicker.
     *
     * @param termAccession - the accession of the term being searched on e.g. ENVO:00003073
     * @param ontology      - version access for the ontology you wish to query e.g. 40832 for OBI
     * @return Map<String, String> representing the parents of the Term
     */
    public Map<String, String> getAllTermParents(String termAccession, String ontology) {
        String searchString = REST_URL + "concepts/rootpath/" + ontology + "/" + termAccession;

        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-all-parents-" + termAccession + DownloadUtils.XML_EXT;

        DownloadUtils.downloadFile(searchString, downloadLocation);

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

        BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

        return handler.parseOntologyParentPathFile(fileWithNameSpace.getAbsolutePath());
    }

    public Map<String, BioPortalOntology> getSearchResults() {
        return searchResults;
    }

    private String correctTermForHTTPTransport(String term) {
        return term.replaceAll("[\\s]+", "%20");
    }

    private Map<String, String> processBioPortalOntology(Map<String, BioPortalOntology> toConvert) {
        Map<String, String> convertedMap = new HashMap<String, String>();
        for (String ontologyAccession : toConvert.keySet()) {
            BioPortalOntology bpo = toConvert.get(ontologyAccession);

            if (bpo != null) {
                convertedMap.put(bpo.getOntologySourceAccession(), bpo.getOntologyTermName());
            }
        }
        return convertedMap;
    }

}
