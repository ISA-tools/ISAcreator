package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: Dec 15, 2010
 *         Time: 3:45:11 PM
 */
public class OLSClientTest {

    private OLSClient client = new OLSClient();

    public static String testOntologyAccession = "9606";
    public static String testOntologyTerm = "sea";

    @Test
    public void getOntologyNames() {
        System.out.println("Testing ____getOntologyNames()____");

        Map<String, String> ontologyNames = client.getOntologyNames();

        assertTrue("No ontology names found", ontologyNames.size() > 0);

        System.out.println("Found " + ontologyNames.size() + " ontology names\n");
    }

    @Test
    public void getTermByAccessionId() {
        System.out.println("Testing ____getTermByAccessionId()____");

        Map<String, String> matchingTerms = client.getTermByAccessionId(testOntologyAccession);

        assertTrue("No matches found for " + testOntologyAccession, matchingTerms.size() > 0);

        System.out.println("Found " + matchingTerms.values().size() + " matching terms in " + matchingTerms.keySet().size() + " ontologies \n");
    }

    @Test
    public void getPartialNameFromSource() {
        System.out.println("Testing ____getPartialNameFromSource()____");

        Map<String, String> matchingTerms = client.getTermsByPartialNameFromSource(testOntologyTerm, "ENVO", false);

        assertTrue("No matches found for " + testOntologyTerm, matchingTerms.size() > 0);

        System.out.println("Found " + matchingTerms.values().size() + " matching terms in " + matchingTerms.keySet().size() + " ontologies \n");
    }

    @Test
    public void getPartialNameFromSourceWithRecommendedOntologies() {
        System.out.println("Testing ____getPartialNameFromSourceWithRecommendedOntologies()____");

        List<RecommendedOntology> ros =  new ArrayList<RecommendedOntology>();
        RecommendedOntology ro1 = new RecommendedOntology(new Ontology("", "", "ENVO", "Environment Ontology"));
        ros.add(ro1);

        Map<String, String> matchingTerms = client.getTermsByPartialNameFromSource(testOntologyTerm, ros);

        assertTrue("No matches found for " + testOntologyTerm, matchingTerms.size() > 0);

        System.out.println("Found " + matchingTerms.values().size() + " matching terms in " + matchingTerms.keySet().size() + " ontologies \n");
    }
}
