package org.isatools.isacreator.ontologiser.logic.impl;

import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;


public class AnnotatorSearchClientTest {

    @Test
    public void testAnnotatorClient() {
         System.out.println("_____Testing NCBO Annotator search client____");
        AnnotatorSearchClient sc = new AnnotatorSearchClient();

        Set<String> testTerms = new HashSet<String>();
        testTerms.add("CY3");
        testTerms.add("DOSE");
        testTerms.add("ASSAY");
        testTerms.add("SULPHUR");

        Map<String, Map<String, AnnotatorResult>> result = sc.searchForTerms(testTerms);

        for (String key : result.keySet()) {
            System.out.println(key + " matched:");
            for (String ontologyVersion : result.get(key).keySet()) {
                System.out.println("\t" + ontologyVersion + " -> " + result.get(key).get(ontologyVersion).getOntologyTerm().getOntologyTermName() + " (" + result.get(key).get(ontologyVersion).getOntologySource().getOntologyDisplayLabel() + ")");
            }
        }

        assertTrue("No matches found", result.size() > 0);
    }
}
