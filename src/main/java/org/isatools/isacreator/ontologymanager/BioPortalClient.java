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
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntology;
import org.isatools.isacreator.ontologymanager.bioportal.utils.BioPortalXMLModifier;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.BioPortalClassBeanResultHandler;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.BioPortalOntologyListResultHandler;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.BioPortalSearchBeanResultHandler;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import uk.ac.ebi.utils.io.DownloadUtils;

import java.io.File;
import java.util.*;


public class BioPortalClient implements OntologyService {
    private static final Logger log = Logger.getLogger(BioPortalClient.class.getName());

    public static final int PARENTS = 0;

    public static final int CHILDREN = 1;

    public static final String REST_URL = "http://rest.bioontology.org/bioportal/";
    public static final String API_KEY = "apikey=fd88ee35-6995-475d-b15a-85f1b9dd7a42";

    private Set<String> noChildren;

    private boolean doneOntologyCheck = false;

    private List<Ontology> ontologies;

    private Map<String, String> ontologySources;
    private Map<String, String> ontologyVersions;

    private Map<String, OntologyTerm> searchResults;
    private Map<String, Map<String, OntologyTerm>> cachedNodeChildrenQueries;

    public static final String DIRECT_ONTOLOGY_URL = "http://bioportal.bioontology.org/ontologies/";


    public BioPortalClient() {
        ontologySources = new HashMap<String, String>();
        ontologyVersions = new HashMap<String, String>();
        searchResults = new HashMap<String, OntologyTerm>();
        cachedNodeChildrenQueries = new HashMap<String, Map<String, OntologyTerm>>();

        noChildren = new HashSet<String>();
    }

    public List<Ontology> getAllOntologies() {
        return getAllOntologies(false);
    }

    public List<Ontology> getAllOntologies(boolean loadAll) {

        if (ontologies == null || ontologies.size() == 0) {

            String searchString = REST_URL + "ontologies/?" + API_KEY;

            log.info("Getting all ontologies: query string is " + searchString);

            DownloadUtils.downloadFile(searchString, DownloadUtils.DOWNLOAD_FILE_LOC + "ontologies" + DownloadUtils.XML_EXT);

            BioPortalOntologyListResultHandler parser = new BioPortalOntologyListResultHandler();
            parser.setLoadAllOntologies(loadAll);

            ontologies = parser.parseFile(DownloadUtils.DOWNLOAD_FILE_LOC + "ontologies" + DownloadUtils.XML_EXT, false);

            if (ontologies != null) {
                for (Ontology ontology : ontologies) {
                    ontologyVersions.put(ontology.getOntologyAbbreviation(), ontology.getOntologyVersion());
                    ontologySources.put(ontology.getOntologyAbbreviation(), ontology.getOntologyDisplayLabel());
                }
            }
        }

        return ontologies;
    }

    public Ontology getOntologyById(String ontologyId) {
        String searchString = REST_URL + "virtual/ontology/" + ontologyId + "/?" + API_KEY;

        System.out.println(searchString);
        log.info("Getting ontology by id : query string is " + searchString);

        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + "ontology-info-" + ontologyId + DownloadUtils.XML_EXT;

        DownloadUtils.downloadFile(searchString, downloadLocation);

        BioPortalOntologyListResultHandler parser = new BioPortalOntologyListResultHandler();

        List<Ontology> ontologies = parser.parseFile(downloadLocation, true);

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

        OntologyTerm ontologyResult = getTermInformation(termAccession, ontology);
        Map<String, String> result = new ListOrderedMap<String, String>();
        result.put("accession", termAccession);

        result.putAll(ontologyResult.getComments());

        return result;
    }

    public OntologyTerm getTermInformation(String termAccession, String ontology) {
        OntologyTerm bpo;
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

    private OntologyTerm performMetadataQuery(String termAccession, String ontology) {
        String searchString = REST_URL + "concepts/" + ontology + "/" + termAccession + "/?" + API_KEY;

        System.out.println(searchString);

        log.info("Getting term information for " + termAccession + " -> " + searchString);

        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-" + termAccession + DownloadUtils.XML_EXT;

        DownloadUtils.downloadFile(searchString, downloadLocation);

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

        if (fileWithNameSpace != null) {

            BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

            return handler.parseMetadataFile(fileWithNameSpace.getAbsolutePath());
        } else {
            return null;
        }
    }


    public Map<OntologySourceRefObject, List<OntologyTerm>> getTermsByPartialNameFromSource(String term, List<RecommendedOntology> recommendedOntologies) {

        term = correctTermForHTTPTransport(term);

        // need to accommodate more complicated search strings in the case where the recommended source contains the branch
        // to search under as well!
        Map<OntologySourceRefObject, List<OntologyTerm>> result = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();

        // need to do a loop over all branches and do a single query on those recommended ontologies only defined
        // by the source, not the branch.
        for (RecommendedOntology ro : recommendedOntologies) {

            if (ro.getOntology() != null) {

                StringBuilder searchString = new StringBuilder();

                searchString.append(REST_URL);

                searchString.append("search/").append(term).append("/?ontologyids=").append(ro.getOntology().getOntologyID());


                if (ro.getBranchToSearchUnder() != null && !ro.getBranchToSearchUnder().getBranchIdentifier().equals("")) {
                    String branch = ro.getBranchToSearchUnder().getBranchIdentifier();
                    searchString.append("&subtreerootconceptid=").append(branch);
                }

                searchString.append("&").append(API_KEY);

                System.out.println("sending query: " + searchString);

                Map<OntologySourceRefObject, List<OntologyTerm>> searchResult = downloadAndProcessBranch(term, searchString.toString());

                if (searchResult != null) {
                    result.putAll(searchResult);
                }
            }
        }


        return result;
    }


    private Map<OntologySourceRefObject, List<OntologyTerm>> downloadAndProcessBranch(String term, String searchString) {
        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + term + DownloadUtils.XML_EXT;


        DownloadUtils.downloadFile(searchString, downloadLocation);

        BioPortalSearchBeanResultHandler handler = new BioPortalSearchBeanResultHandler();

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/resultBeanSchema#", "<success>");

        if (fileWithNameSpace != null) {
            Map<OntologySourceRefObject, List<OntologyTerm>> result = handler.getSearchResults(fileWithNameSpace.getAbsolutePath());

            updateOntologyManagerWithOntologyInformation();

            return result;
        }

        return new HashMap<OntologySourceRefObject, List<OntologyTerm>>();

    }

    public Map<OntologySourceRefObject, List<OntologyTerm>> getTermsByPartialNameFromSource(String term, String source, boolean reverseOrder) {

        System.out.println("Searching for source: " + source);

        term = correctTermForHTTPTransport(term);

        String searchString = REST_URL + "search/" + term + "/?ontologyids=" +
                (((source == null) || source.trim().equalsIgnoreCase("") || source.trim().equalsIgnoreCase("all"))
                        ? constructSourceStringFromAllowedOntologies() : source);

        searchString += "&" + API_KEY;

        log.info("search string " + searchString);

        Map<OntologySourceRefObject, List<OntologyTerm>> searchResult = downloadAndProcessBranch(term, searchString);

        log.info("found " + (searchResult == null ? "0" : searchResult.size()) + " ontology terms");

        return searchResult == null ? new HashMap<OntologySourceRefObject, List<OntologyTerm>>() : searchResult;
    }

    private void updateOntologyManagerWithOntologyInformation() {
        if (!doneOntologyCheck) {
            for (AcceptedOntology ao : AcceptedOntologies.values()) {
                Ontology o = getOntologyById(ao.getOntologyID());
                if (o != null) {
                    OntologyManager.addOLSOntologyDefinitions(Collections.singletonMap(o.getOntologyAbbreviation(),
                            o.getOntologyDisplayLabel()), Collections.singletonMap(o.getOntologyAbbreviation(), o.getOntologyVersion()));
                }
            }
            doneOntologyCheck = true;
        }
    }

    private String constructSourceStringFromAllowedOntologies() {
        String allowedOntologies = "";

        int count = 0;
        for (AcceptedOntology ao : AcceptedOntologies.values()) {
            allowedOntologies += ao.getOntologyID();
            if (count < AcceptedOntologies.values().size() - 1) {
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
    public Map<String, OntologyTerm> getOntologyRoots(String ontology) {

        if (!cachedNodeChildrenQueries.containsKey(ontology)) {

            String searchString = REST_URL + "concepts/" + ontology + "/root/?" + API_KEY;


            System.out.println("Search string is: " + searchString);

            String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-roots" + DownloadUtils.XML_EXT;

            DownloadUtils.downloadFile(searchString, downloadLocation);

            File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

            BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

            if (fileWithNameSpace == null) {
                return new HashMap<String, OntologyTerm>();
            }

            Map<String, OntologyTerm> result = handler.parseRootConceptFile(fileWithNameSpace.getAbsolutePath(), noChildren);

            if (result != null) {
                searchResults.putAll(result);
                cachedNodeChildrenQueries.put(ontology, result);
            }

            return result;
        } else {
            System.out.println("using cached version for display.");
            return cachedNodeChildrenQueries.get(ontology);
        }
    }

    public Map<String, OntologyTerm> getTermParent(String termAccession, String ontology) {
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
    public Map<String, OntologyTerm> getTermChildren(String termAccession, String ontology) {
        return getTermChildOrParent(termAccession, ontology, CHILDREN);
    }

    /**
     * Gets the children or parents of a term
     *
     * @param termAccession - accession of term e.g. snap:Continuant
     * @param ontology      - ontology version id e.g. 40832 for OBI
     * @param type          - @see PARENTS, CHILDREN - what type of search to make
     * @return Map<OntologySourceRefObject, List<OntologyTerm>> from Ontology Source object -> List of found terms
     */
    public Map<String, OntologyTerm> getTermChildOrParent(String termAccession, String ontology, int type) {

        if (!noChildren.contains(termAccession)) {
            if (!cachedNodeChildrenQueries.containsKey(ontology + "-" + termAccession)) {

                String searchString = REST_URL + "concepts/" + ((type == PARENTS) ? "parents/" : "") + "" + ontology + "?conceptid=" + termAccession + "&" + API_KEY;

                System.out.println("Searching for : " + searchString);

                String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-" + termAccession + DownloadUtils.XML_EXT;

                DownloadUtils.downloadFile(searchString, downloadLocation);

                BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

                File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation),
                        "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

                if (fileWithNameSpace != null) {

                    Map<String, OntologyTerm> result = handler.parseRootConceptFile(fileWithNameSpace.getAbsolutePath(), noChildren);

                    if (result != null) {
                        searchResults.putAll(result);
                        cachedNodeChildrenQueries.put(ontology + "-" + termAccession, result);
                    }

                    return result;
                } else {
                    return new HashMap<String, OntologyTerm>();
                }
            } else {
                return cachedNodeChildrenQueries.get(ontology + "-" + termAccession);
            }
        } else {
            return new HashMap<String, OntologyTerm>();
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
    public Map<String, OntologyTerm> getAllTermParents(String termAccession, String ontology) {
        String searchString = REST_URL + "path/" + ontology + "/?source=" + termAccession + "&target=root&" + API_KEY;

        System.out.println("Searching for parent terms: " + searchString);

        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + ontology + "-all-parents-" + termAccession + DownloadUtils.XML_EXT;

        DownloadUtils.downloadFile(searchString, downloadLocation);

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(new File(downloadLocation), "http://bioontology.org/bioportal/classBeanSchema#", "<success>");

        BioPortalClassBeanResultHandler handler = new BioPortalClassBeanResultHandler();

        return handler.parseOntologyParentPathFile(fileWithNameSpace.getAbsolutePath());
    }

    private String correctTermForHTTPTransport(String term) {
        return term.replaceAll("[\\s]+", "%20");
    }

    public String getOntologyURL() {
        return DIRECT_ONTOLOGY_URL;
    }

}
