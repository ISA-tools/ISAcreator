package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by eamonnmaguire on 17/12/2013.
 */
public class BioPortalClient4Test {

    private static BioPortal4Client client = new BioPortal4Client();
    public static String testOntologyID = "EFO";
    public static String testTermAccession = "efo:EFO_0000428";
    public static String testOntologyVersion = "45781";
    public static String testSearchTerm = "dose";

    public static String obiID = "1123";
    public static String obiVersion = "47893";


    @Test
    public void getTermsByPartialNameFromSource() {
        System.out.println("_____Testing getTermsByPartialNameFromSource()____");

        Map<OntologySourceRefObject, List<OntologyTerm>> result = client.getTermsByPartialNameFromSource(testSearchTerm, testOntologyID, false);

        assertTrue("No results found for " + testSearchTerm + " in " + testOntologyID, result.size() > 0);

        for (OntologySourceRefObject source : result.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : result.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologyTermAccession() + ")");
            }
        }

        System.out.println("Found " + result.values().size() + " matches...\n");
    }

    @Test
    public void getAllOntologies() {
        System.out.println("_____Testing getAllOntologies()____");

        List<Ontology> ontologies = client.getAllOntologies();

        assertTrue("Oh no! No returned ontologies (empty result)! ", ontologies.size() > 0);

        System.out.println("Found " + ontologies.size() + " ontologies \n");
        for (Ontology ontology : ontologies) {
            System.out.println(ontology.getOntologyID() + " - " + ontology.getOntologyAbbreviation() + " -> " + ontology.getOntologyDisplayLabel()
                    + " -> " + ontology.getOntologyVersion() + " - " + ontology.getHomepage() + " " + ontology.getContactName());
        }
    }
}
