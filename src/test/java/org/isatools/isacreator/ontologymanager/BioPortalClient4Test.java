package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
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
    private String testOntologyID = "EFO";
    private String testTermAccession = "http://www.co-ode.org/ontologies/galen#Melanoma";
    private String testSearchTerm = "dose";
    private String ontologyId = "http://data.bioontology.org/ontologies/GALEN";


    @Test
    public void getTermsByPartialNameFromSource() {
        System.out.println("_____Testing getTermsByPartialNameFromSource()____");

        long startTime = System.currentTimeMillis();
        Map<OntologySourceRefObject, List<OntologyTerm>> result = client.getTermsByPartialNameFromSource(testSearchTerm, "all", false);
        System.out.println("Took " + (System.currentTimeMillis()-startTime) + "ms to do that query.");


        for (OntologySourceRefObject source : result.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : result.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologyTermAccession() + ")");
            }
        }

        System.out.println();

        startTime = System.currentTimeMillis();
        result = client.getTermsByPartialNameFromSource("cy5", "all", false);
        System.out.println("Took " + (System.currentTimeMillis()-startTime) + "ms to do that query.");

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

        client.getTermMetadata(testTermAccession,ontologyId);
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
        Map<String, OntologyTerm> ontologyRoots = client.getOntologyRoots(testOntologyID);

        assertTrue("No ontology roots found for " + testOntologyID, ontologyRoots.size() > 0);

        System.out.println("Found " + ontologyRoots.size() + " roots for " + testOntologyID);
    }
}
