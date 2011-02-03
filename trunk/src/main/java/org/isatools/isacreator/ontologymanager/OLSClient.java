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
import org.isatools.isacreator.configuration.OntologyFormats;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologyselectiontool.OntologySourceManager;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the main business class that will communicate with the OLS
 * webservice and serve data to the GUI classes.
 *
 * @author Majority of class Supplied By R.G. Cote (EBI), developer of the OLS web service.
 */
public class OLSClient implements OntologyService {
    private static final Logger log = Logger.getLogger(OLSClient.class.getName());

    public List<Ontology> getOntologies() {
        List<Ontology> ontologies = new ArrayList<Ontology>();

        Map<String, String> ontologyNames = getOntologyNames();
        Map<String, String> ontologyVersions = getOntologyVersions();

        String olsVersion = ontologyVersions.get("OLS");

        for (String ontologyAbbreviation : ontologyNames.keySet()) {
            Ontology newOntology = new Ontology("", olsVersion, ontologyAbbreviation, ontologyNames.get(ontologyAbbreviation));
            newOntology.setFormat(OntologyFormats.OBO);
            ontologies.add(newOntology);
        }

        return ontologies;
    }

    public Map<String, String> getOntologyNames() {
        Map<String, String> answer = new HashMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();

        try {
            Query service = locator.getOntologyQuery();
            Map<String, String> ontologyNames = service.getOntologyNames();

            if (ontologyNames != null) {
                answer = ontologyNames;
            }
        } catch (RemoteException e) {
            log.error("remote exception thrown: " + e.getMessage());
        } catch (Exception e) {
            log.error(
                    "unexpected exception occurred. probably as a result of no internet connection!");
        }

        return answer;
    }

    /**
     * Return terms by their accession ID.
     *
     * @param id - accession id, e.g. 9606
     * @return Map of matching accessions and terms. e.g. 9606 homo sapiens.
     */
    public Map<String, String> getTermByAccessionId(String id) {
        Map<String, String> answer = new HashMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();

        try {
            String source = "NEWT";
            String idShort = id;

            if (id.contains(":")) {
                source = id.substring(0, id.indexOf(":"));
                idShort = idShort.substring(idShort.indexOf(":") + 1);
            }

            Query service = locator.getOntologyQuery();
            String termVal = service.getTermById(id, source);

            if (termVal != null) {
                answer.put(source + ":" + idShort, termVal);
            }
        } catch (RemoteException e) {
            log.error("remote exception thrown: " + e.getMessage());
        } catch (Exception e) {
            log.error(
                    "unexpected exception occurred. probably as a result of no internet connection!");
        }
        return answer;
    }

    /**
     * calls OLS webserver and gets metadata for a termId
     *
     * @param termId   - Term Id to search for
     * @param ontology - Ontology to search in.
     * @return Map of metadata - key is data type, value is data value.
     *         Map should not be null.
     */
    public Map<String, String> getTermMetadata(String termId, String ontology) {
        Map<String, String> answer = new ListOrderedMap<String, String>();
        QueryServiceLocator locator = new QueryServiceLocator();

        try {
            Query service = locator.getOntologyQuery();
            Map<String, String> metadata = service.getTermMetadata(termId, ontology);

            answer.put("accession", termId);
            if (metadata != null) {
                answer.putAll(metadata);
            }
        } catch (RemoteException e) {
            log.error("remote exception thrown " + e.getMessage());
        } catch (Exception e) {
            log.error(
                    "unexpected exception occurred. probably as a result of no internet connection!");
        }

        return answer;
    }


    /**
     * Search the OLS for a partial term in an ontology source.
     *
     * @param term         - Term to search for
     * @param source       - Ontology source to search in e.g. EFO
     * @param reverseOrder - Whether or not to flip the mappings from term -> source:accession to source:accession -> term.
     * @return - Mapping of source:accession to term, depending on reverse order being true or false.
     */
    public Map<String, String> getTermsByPartialNameFromSource(String term,
                                                               String source, boolean reverseOrder) {
        Map<String, String> answer = new HashMap<String, String>();

        try {
            QueryServiceLocator locator = new QueryServiceLocator();
            Query service = locator.getOntologyQuery();
            Map<String, String> matchingTerms = service.getTermsByName(term, source, reverseOrder);

            System.out.println("found the following terms in OLS:");
            for (String accession : matchingTerms.keySet()) {
                System.out.println("\t accession: " + accession + " -> " + matchingTerms.get(accession));
            }

            if (matchingTerms != null) {
                answer = matchingTerms;
            }
        } catch (RemoteException e) {
            log.error("remote exception thrown " + e.getMessage());
        } catch (Exception e) {
            log.error(
                    "unexpected exception occurred. probably as a result of no internet connection!");
        }
        return answer;
    }

    /**
     * Return the current version of the OLS
     *
     * @return a String representation of the version. String should not be null.
     */
    public Map<String, String> getOntologyVersions() {
        QueryServiceLocator locator = new QueryServiceLocator();
        Map<String, String> versions = new HashMap<String, String>();

        try {
            Query service = locator.getOntologyQuery();
            String tempVersion = service.getVersion();
            Pattern p = Pattern.compile("[a-zA-Z]+ [0-9]+.[0-9]+");
            Matcher m = p.matcher(tempVersion);

            if (m.find()) {
                tempVersion = tempVersion.substring(m.start(), m.end());
            }
            versions.put(OntologySourceManager.OLS_TEXT, tempVersion);
        } catch (RemoteException e) {
            log.error("remote exception thrown " + e.getMessage());
        } catch (Exception e) {
            log.error(
                    "unexpected exception occurred. probably as a result of no internet connection!");
        }

        return versions;
    }

    /**
     * Get the root terms of an Ontology
     *
     * @param ontologyAbbreviation - The ontology to get the root terms for e.g. EFO, ENVO, CHEBI
     * @return Map<String, String> representing the Root terms with the Source:Accession as the key and the Label as the Value
     */
    public Map<String, String> getOntologyRoots(String ontologyAbbreviation) {
        QueryServiceLocator locator = new QueryServiceLocator();
        Map<String, String> answer = new HashMap<String, String>();
        try {
            Query service = locator.getOntologyQuery();
            Map<String, String> rootTerms = service.getRootTerms(ontologyAbbreviation);
            if (rootTerms != null) {
                answer = rootTerms;
            }
        } catch (RemoteException re) {
            log.error("remote exception thrown " + re.getMessage());
        } catch (ServiceException e) {
            log.error("service exception thrown " + e.getMessage());
        }
        return answer;
    }

    /**
     * Get the parent terms of an Ontology
     *
     * @param termAccession        - Source:Accession string for the term to get children for
     * @param ontologyAbbreviation - The ontology to get the root terms for e.g. EFO, ENVO, CHEBI
     * @return Map<String, String> representing the child terms with the Source:Accession as the key and the Label as the Value
     */
    public Map<String, String> getTermParent(String termAccession, String ontologyAbbreviation) {
        QueryServiceLocator locator = new QueryServiceLocator();
        Map<String, String> answer = new HashMap<String, String>();
        try {
            Query service = locator.getOntologyQuery();
            Map<String, String> parentTerms = service.getTermParents(termAccession, ontologyAbbreviation);
            if (parentTerms != null) {
                answer = parentTerms;
            }
        } catch (RemoteException re) {
            log.error("remote exception thrown " + re.getMessage());
        } catch (ServiceException e) {
            log.error("service exception thrown " + e.getMessage());
        }
        return answer;
    }

    /**
     * Get the root terms of an Ontology
     *
     * @param termAccession        - Source:Accession string for the term to get children for
     * @param ontologyAbbreviation - The ontology to get the root terms for e.g. EFO, ENVO, CHEBI
     * @return Map<String, String> representing the child terms with the Source:Accession as the key and the Label as the Value
     */
    public Map<String, String> getTermChildren(String termAccession, String ontologyAbbreviation) {
        QueryServiceLocator locator = new QueryServiceLocator();
        Map<String, String> answer = new HashMap<String, String>();
        try {
            Query service = locator.getOntologyQuery();
            Map<String, String> childTerms = service.getTermChildren(termAccession, ontologyAbbreviation, 1, null);
            if (childTerms != null) {
                answer = childTerms;
            }
        } catch (RemoteException re) {
            log.error("remote exception thrown " + re.getMessage());
        } catch (ServiceException e) {
            log.error("service exception thrown " + e.getMessage());
        }
        return answer;
    }

    public Map<String, String> getTermsByPartialNameFromSource(String term, List<RecommendedOntology> recommendedOntology) {
        Map<String, String> searchResult = new HashMap<String, String>();

        for (RecommendedOntology ro : recommendedOntology) {
            Map<String, String> subSearchResult = null;

            if (ro.getBranchToSearchUnder() == null) {

                subSearchResult = getTermsByPartialNameFromSource(term, ro.getOntology().getOntologyAbbreviation(), false);

            } else {
                subSearchResult = getTermsByPartialNameFromSource(term, ro);
            }

            if (subSearchResult != null) {
                searchResult.putAll(subSearchResult);
            }
        }

        return searchResult;
    }

    /**
     * Retrieves the terms with labels matching a particular String (term) and which occur under a specific branch
     * in an ontology.
     *
     * @param termLabel           - String representing the term label being searched for e.g. 'cancer'
     * @param recommendedOntology - Recommended ontology to be searched under e.g. OBI_0003212
     * @return Map<String, String> comprising of the matching term source:accession pair to the term label e.g. ENVO:00002216 -> vegetable.
     */
    private Map<String, String> getTermsByPartialNameFromSource(String termLabel, RecommendedOntology recommendedOntology) {

        Map<String, String> filteredResult = new HashMap<String, String>();
        // first step is to search for the term in OLS
        Map<String, String> termSearchResult = getTermsByPartialNameFromSource(termLabel, recommendedOntology.getOntology().getOntologyAbbreviation(), false);

        Set<String> termsInBranch = getAllTermsAccessionsBelowBranch(recommendedOntology.getBranchToSearchUnder().getBranchIdentifier(),
                recommendedOntology.getOntology().getOntologyAbbreviation());

        // now, since we have the terms in the branch, we simple filter out any Terms in termSearchResult which do not have
        // accessions in the termsInBranch Set
        for (String termAccession : termSearchResult.keySet()) {
            if (termsInBranch.contains(termAccession)) {
                filteredResult.put(termAccession, termSearchResult.get(termAccession));
            }
        }

        return filteredResult;
    }

    /**
     * We only get the accessions so as to reduce the memory needs. Accessions are sufficient for our purposes
     * Recursively called method.
     *
     * @param branchTerm           - branch in the ontology to search under.
     * @param ontologyAbbreviation - abbreviation of Ontology being searched under e.g. ENVO for the Environment Ontology
     * @return Set<String> representing all the terms under a particular branch.
     */
    public Set<String> getAllTermsAccessionsBelowBranch(String branchTerm, String ontologyAbbreviation) {
        // need to retrieve and add all terms below the branch
        Set<String> result = new HashSet<String>();

        Map<String, String> branchChildren = getTermChildren(branchTerm, ontologyAbbreviation);

        if (branchChildren.size() > 0) {
            for (String childTermAccession : branchChildren.keySet()) {
                // we recursively call this method to get all of the children and add the subsequent result to the Set<String>
                result.addAll(getAllTermsAccessionsBelowBranch(childTermAccession, ontologyAbbreviation));
            }
        }
        result.add(branchTerm);

        return result;
    }

    /**
     * Method will search the Ontology space to determine the parents of a given term. This can then be used in searches to make
     * location of a term within an Ontology much quicker.
     *
     * @param termAccession        - the accession of the term being searched on e.g. ENVO:00003073
     * @param ontologyAbbreviation - Abbreviation for the Ontology e.g. ENVO
     * @return Map<String, String> representing the parents of the Term
     */
    public Map<String, String> getAllTermParents(String termAccession, String ontologyAbbreviation) {
        // we use a ListOrderedSet so that order is maintained! This is important since order will dictate the way
        // we search within the tree whilst navigating from parent A -> B -> C to get to term ENVO:00001234. If the parents were in
        // an incorrect order, we'd end up with something like B -> A -> C.
        Map<String, String> result = new ListOrderedMap<String, String>();

        Map<String, String> termParents = getTermParent(termAccession, ontologyAbbreviation);
        if (termParents.size() > 0) {
            for (String parentTermAccession : termParents.keySet()) {
                result.putAll(getAllTermParents(parentTermAccession, ontologyAbbreviation));
            }
        }

        result.putAll(termParents);

        return result;
    }


    public static void main(String[] args) {
        OLSClient olsClient = new OLSClient();
        Map<String, String> results = olsClient.getTermChildren("ENVO:00002297", "ENVO");

        System.out.println("found " + results.size() + " terms");
        for (String term : results.keySet()) {
            System.out.println(term + " -> " + results.get(term));
        }
    }


}
