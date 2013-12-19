package org.isatools.isacreator.ontologymanager;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers.BioPortalSearchResultHandler;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.*;

/**
 * Created by eamonnmaguire on 17/12/2013.
 */
public class BioPortal4Client implements OntologyService {

    private static final Logger log = Logger.getLogger(BioPortal4Client.class);

    private BioPortalSearchResultHandler handler;


    public static final String REST_URL = "http://data.bioontology.org/";

    public BioPortal4Client() {
        this.handler = new BioPortalSearchResultHandler();
    }

    public Map<String, String> getOntologyNames() {
        return AcceptedOntologies.getOntologySourceToNames();
    }

    public Map<String, String> getTermMetadata(String termId, String ontology) {
//        http://data.bioontology.org/ontologies/GALEN/classes/http%3A%2F%2Fwww.co-ode.org%2Fontologies%2Fgalen%23Melanoma"
        OntologyTerm ontologyTerm = handler.getTermMetadata(termId,ontology);
        return null;
    }

    public Map<OntologySourceRefObject, List<OntologyTerm>> getTermsByPartialNameFromSource(String term, String source, boolean reverseOrder) {

        term = correctTermForHTTPTransport(term);

        if (source.equals("all")) {
            source = AcceptedOntologies.getAllowedOntologyAcronyms(new HashSet<Ontology>());
        }
        Map<String, List<OntologyTerm>> searchResult = handler.getSearchResults(term, source, null);

        return convertStringKeyMapToOntologySourceRefKeyMap(searchResult);
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
                    subtree = ro.getBranchToSearchUnder().getBranchIdentifier();
                }

                Map<String, List<OntologyTerm>> searchResult = handler.getSearchResults(term, ro.getOntology().getOntologyID(), subtree);

                if (searchResult != null) {
                    result.putAll(convertStringKeyMapToOntologySourceRefKeyMap(searchResult));
                }
            }
        }
        return result;
    }

    public Map<String, String> getOntologyVersions() {
        return AcceptedOntologies.getOntologySourceToVersion();
    }

    public Map<String, OntologyTerm> getOntologyRoots(String ontology) {
//        http://data.bioontology.org/ontologies/EFO/classes/roots
        return null;
    }

    public Map<String, OntologyTerm> getTermParent(String termAccession, String ontology) {

        return null;
    }

    public Map<String, OntologyTerm> getTermChildren(String termAccession, String ontology) {
//        http://data.bioontology.org/ontologies/EFO/classes/http%3A%2F%2Fwww.ebi.ac.uk%2Fefo%2FEFO_0000001/children
        return null;
    }

    public Map<String, OntologyTerm> getAllTermParents(String termAccession, String ontology) {
//        http://data.bioontology.org/ontologies/EFO/classes/http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FIAO_0000030/parents
        return null;
    }

    public Collection<Ontology> getAllOntologies() {
        return handler.getAllOntologies().values();
    }

    private Map<OntologySourceRefObject, List<OntologyTerm>> convertStringKeyMapToOntologySourceRefKeyMap(Map<String, List<OntologyTerm>> toConvert) {

        Map<OntologySourceRefObject, List<OntologyTerm>> convertedMap = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();
        for (String ontologyId : toConvert.keySet()) {
            Ontology ontology = AcceptedOntologies.getAcceptedOntologies().get(ontologyId);
            if (ontology != null) {
                OntologySourceRefObject osro = new OntologySourceRefObject(ontology.getOntologyAbbreviation(),
                        ontologyId, ontology.getOntologyVersion(), ontology.getOntologyDisplayLabel());

                convertedMap.put(osro, new ArrayList<OntologyTerm>());

                for(OntologyTerm ontologyTerm : toConvert.get(ontologyId)){
                    ontologyTerm.setOntologySourceInformation(osro);
                    convertedMap.get(osro).add(ontologyTerm);
                }

            }
        }
        return convertedMap;
    }

    private String correctTermForHTTPTransport(String term) {
        return term.replaceAll("[\\s]+", "%20");
    }
}
