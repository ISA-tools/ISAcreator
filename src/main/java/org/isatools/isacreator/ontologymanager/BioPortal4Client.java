package org.isatools.isacreator.ontologymanager;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntology;
import org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers.BioPortalQueryEndpoint;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by eamonnmaguire on 17/12/2013.
 */
public class BioPortal4Client implements OntologyService {

    private static final Logger log = Logger.getLogger(BioPortal4Client.class);

    private BioPortalQueryEndpoint handler;

    public static final String REST_URL = "http://data.bioontology.org/";

    public BioPortal4Client() {
        this.handler = new BioPortalQueryEndpoint();
    }

    public Map<String, String> getOntologyNames() {
        return null;
    }

    public Map<String, String> getTermMetadata(String termId, String ontology) {
        return null;
    }

    public Map<OntologySourceRefObject, List<OntologyTerm>> getTermsByPartialNameFromSource(String term, String source, boolean reverseOrder) {

        System.out.println("Searching for source: " + source);

        term = correctTermForHTTPTransport(term);


        if(source.equals("all")) {
            source = AcceptedOntologies.getAllowedOntologyAcronyms(new HashSet<AcceptedOntology>());
        }
        Map<OntologySourceRefObject, List<OntologyTerm>> searchResult = handler.getSearchResults(term, source, null);

        log.info("found " + (searchResult == null ? "0" : searchResult.size()) + " ontology terms");

        return searchResult == null ? new HashMap<OntologySourceRefObject, List<OntologyTerm>>() : searchResult;
    }

    public Map<OntologySourceRefObject, List<OntologyTerm>> getTermsByPartialNameFromSource(String term, List<RecommendedOntology> recommendedOntology) {
        term = correctTermForHTTPTransport(term);

        // need to accommodate more complicated search strings in the case where the recommended source contains the branch
        // to search under as well!
        Map<OntologySourceRefObject, List<OntologyTerm>> result = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();

        // need to do a loop over all branches and do a single query on those recommended ontologies only defined
        // by the source, not the branch.
        for (RecommendedOntology ro : recommendedOntology) {

            if (ro.getOntology() != null) {

                String subtree = null;
                if (ro.getBranchToSearchUnder() != null && !ro.getBranchToSearchUnder().getBranchIdentifier().equals("")) {
                    String branch = ro.getBranchToSearchUnder().getBranchIdentifier();
                    subtree = branch;

                }

                Map<OntologySourceRefObject, List<OntologyTerm>> searchResult = handler.getSearchResults(term, ro.getOntology().getOntologyID(), subtree);

                if (searchResult != null) {
                    result.putAll(searchResult);
                }
            }
        }


        return result;
    }

    public Map<String, String> getOntologyVersions() {
        return null;
    }

    public Map<String, OntologyTerm> getOntologyRoots(String ontology) {
        return null;
    }

    public Map<String, OntologyTerm> getTermParent(String termAccession, String ontology) {
        return null;
    }

    public Map<String, OntologyTerm> getTermChildren(String termAccession, String ontology) {
        return null;
    }

    public Map<String, OntologyTerm> getAllTermParents(String termAccession, String ontology) {
        return null;
    }

    public String getOntologyURL() {
        return null;
    }

    public List<Ontology> getAllOntologies() {
        handler.getAllOntologies();
        return null;
    }




    private String correctTermForHTTPTransport(String term) {
        return term.replaceAll("[\\s]+", "%20");
    }
}
