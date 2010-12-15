package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.bioportal.model.BioPortalOntology;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: Dec 15, 2010
 *         Time: 1:52:52 PM
 */
public class BioPortalClientTest {

    private static BioPortalClient client = new BioPortalClient();
    public static String testOntologyID = "1353";
    public static String testTermAccession = "421563008";
    public static String testOntologyVersion = "44777";
    public static String testSearchTerm = "medicine";

    @Test
    private void getAllOntologies() {
        System.out.println("_____Testing getAllOntologies()____");

        List<Ontology> ontologies = client.getAllOntologies();

        assertTrue("Oh no! No returned ontologies (empty result)! ", ontologies.size() > 0);

        System.out.println("Found " + ontologies.size() + " ontologies \n");

    }

    @Test
    private void getOntologyById() {
        System.out.println("_____Testing getOntologyById()____");

        Ontology ontology = client.getOntologyById(testOntologyID);

        assertTrue("No ontology found for " + testOntologyID, ontology != null);

        System.out.println("Ontology found -> " + ontology.toString() + "\n");
    }

    @Test
    private void getLatestOntologyVersion() {
        System.out.println("_____Testing getLatestOntologyVersion()____");

        String ontologyVersion = client.getLatestOntologyVersion(testOntologyID);

        assertTrue("Latest ontology version not found for " + testOntologyID, ontologyVersion != null);

        System.out.println("Latest ontology version found for " + testOntologyID + " -> " + ontologyVersion + "\n");
    }

    @Test
    private void getTermsByPartialNameFromSource() {
        System.out.println("_____Testing getTermsByPartialNameFromSource()____");

        Map<String, String> result = client.getTermsByPartialNameFromSource(testSearchTerm, testOntologyID, false);

        assertTrue("No results found for " + testSearchTerm + " in " + testOntologyID, result.size() > 0);

        System.out.println("Found " + result.values().size() + " matches...\n");
    }

    @Test
    private void getOntologyRoots() {
        Map<String, String> ontologyRoots = client.getOntologyRoots(testOntologyID);

        assertTrue("No ontology roots found for " + testOntologyID, ontologyRoots.size() > 0);

        System.out.println("Found " + ontologyRoots.size() + " roots for " + testOntologyID);
    }

    @Test
    private void getAllTermParents() {
        Map<String, String> termParents = client.getAllTermParents(testTermAccession, testOntologyID);

        assertTrue("No term parents found for " + testTermAccession + " in " + testOntologyID, termParents.size() > 0);

        System.out.println("Found " + termParents.size() + " parents for " + testTermAccession + " in " + testOntologyID + "\n");
    }


}
