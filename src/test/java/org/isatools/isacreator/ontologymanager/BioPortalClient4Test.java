package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by eamonnmaguire on 17/12/2013.
 */
public class BioPortalClient4Test {

    private BioPortal4Client client = new BioPortal4Client();
    private String testOntologySource = "EFO";
    private String testTermAccession = "http://www.ebi.ac.uk/efo/EFO_0000428";
    private String testSearchTerm = "assay";
    private String ontologyId = "http://data.bioontology.org/ontologies/EFO";
    private String obiOntologyId = "http://data.bioontology.org/ontologies/OBI";


    @Test
    public void exactSearchTest(){
        System.out.println("_____Testing exactSearch()____");

        Map<String, List<OntologyTerm>> result = client.exactSearch(testSearchTerm, obiOntologyId);

        List<OntologyTerm> terms = result.get(obiOntologyId);

        OntologyTerm term = terms.get(0);

        System.out.println("term URI ="+term.getOntologyTermURI());

    }

    @Test
    public void getTermTest(){
        System.out.println("_____Testing getTerm()____");

        OntologyTerm result = client.getTerm(testTermAccession, ontologyId);

        System.out.println(result.getOntologyTermName() + " - " + result.getOntologySource() + " - " + result.getOntologyTermURI() );
    }

    @Test
    public void getTermsByPartialNameFromSource1Test() {
        System.out.println("_____Testing getTermsByPartialNameFromSource()____");

        long startTime = System.currentTimeMillis();
        Map<OntologySourceRefObject, List<OntologyTerm>> result = client.getTermsByPartialNameFromSource(testSearchTerm, obiOntologyId, false);
        System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms to do query '" + testSearchTerm +"' in OBI.");

        for (OntologySourceRefObject source : result.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : result.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologyTermAccession() + ")");
            }
        }
    }

    @Test
    public void getTermsByPartialNameFromSource2Test() {
        long startTime = System.currentTimeMillis();
        Map<OntologySourceRefObject, List<OntologyTerm>> result = client.getTermsByPartialNameFromSource(testSearchTerm, "all", false);
        System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms to do that query.");


        for (OntologySourceRefObject source : result.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : result.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologyTermAccession() + ")");
            }
        }

    }

    @Test
    public void getTermsByPartialNameFromSource3Test() {
        long startTime = System.currentTimeMillis();

        Map<OntologySourceRefObject, List<OntologyTerm>> result = client.getTermsByPartialNameFromSource("cy5", "all", false);
        System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms to do that query.");

        for (OntologySourceRefObject source : result.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : result.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologyTermAccession() + ")");
            }
        }

    }


    @Test
    public void getTermMetadata() {
        System.out.println("_____Testing getTermMetadata()____");

        Map<String, String> termInfo = client.getTermMetadata(testTermAccession, ontologyId);
        assertTrue("Oh no! No additional information for term! ", termInfo.size() > 0);
    }

    @Test
    public void getAllOntologies() {
        System.out.println("_____Testing getAllOntologies()____");

        Collection<Ontology> ontologies = client.getAllOntologies();

        assertTrue("Oh no! No returned ontologies (empty result)! ", ontologies.size() > 0);

        System.out.println("Found " + ontologies.size() + " ontologies \n");
        for (Ontology ontology : ontologies) {
            System.out.println(ontology.getOntologyID() + " - " + ontology.getOntologyAbbreviation() + " -> " + ontology.getOntologyDisplayLabel()
                    + " -> " + ontology.getOntologyVersion() + " - " + ontology.getHomepage() + " " + ontology.getContactName());
        }
    }

    @Test
    public void getOntologyRoots() {
        System.out.println("_____Testing getOntologyRoots()____");
        Map<String, OntologyTerm> ontologyRoots = client.getOntologyRoots(testOntologySource);

        assertTrue("No ontology roots found for " + testOntologySource, ontologyRoots.size() > 0);

        for(String key : ontologyRoots.keySet()) {
            System.out.println(key + " - " + ontologyRoots.get(key).getOntologyTermName());
        }

    }

    @Test
    public void getTermParents() {
        System.out.println("_____Testing getTermChildrenOrParents()____");

        Map<String, OntologyTerm> parentTerms = client.getAllTermParents(testTermAccession, testOntologySource);

        assertTrue("No parents roots found for " + testTermAccession, parentTerms.size() > 0);

        for (OntologyTerm term : parentTerms.values()) {
            System.out.println(term);
        }

        System.out.println("Found " + parentTerms.size() + " parents for 45781");
    }

    @Test
    public void getTermChildren() {
        System.out.println("_____Testing getTermChildrenOrParents()____");

        Map<String, OntologyTerm> childTerms = client.getTermChildren("http://purl.obolibrary.org/obo/IAO_0000030", testOntologySource);

        assertTrue("No children found for " + testTermAccession, childTerms.size() > 0);

        for (OntologyTerm term : childTerms.values()) {
            System.out.println(term);
        }

        System.out.println("Found " + childTerms.size() + " children for information entity in " + testOntologySource);
    }

}
