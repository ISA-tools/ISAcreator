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

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

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

        for (String key : ontologyNames.keySet()) {
            System.out.println(key + " -> " + ontologyNames.get(key));
        }

        assertTrue("No ontology names found", ontologyNames.size() > 0);

        System.out.println("Found " + ontologyNames.size() + " ontology names\n");
    }


    @Test
    public void getPartialNameFromSource() {
        System.out.println("Testing ____getPartialNameFromSource()____");

        Map<OntologySourceRefObject, List<OntologyTerm>> matchingTerms = client.getTermsByPartialNameFromSource(testOntologyTerm, "ENVO", false);

        assertTrue("No matches found for " + testOntologyTerm, matchingTerms.size() > 0);

        System.out.println("Found " + matchingTerms.values().size() + " matching terms in " + matchingTerms.keySet().size() + " ontologies \n");

        for (OntologySourceRefObject source : matchingTerms.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : matchingTerms.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologySourceAccession() + ")");
            }
        }
    }

    @Test
    public void getPartialNameFromSourceWithRecommendedOntologies() {
        System.out.println("Testing ____getPartialNameFromSourceWithRecommendedOntologies()____");

        List<RecommendedOntology> ros = new ArrayList<RecommendedOntology>();
        RecommendedOntology ro1 = new RecommendedOntology(new Ontology("", "", "ENVO", "Environment Ontology"));
        ros.add(ro1);

        Map<OntologySourceRefObject, List<OntologyTerm>> matchingTerms = client.getTermsByPartialNameFromSource(testOntologyTerm, ros);

        System.out.println("Found " + matchingTerms.values().size() + " matching terms in " + matchingTerms.keySet().size() + " ontologies \n");

        for (OntologySourceRefObject source : matchingTerms.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : matchingTerms.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologySourceAccession() + ")");
            }
        }

        assertTrue("No matches found for " + testOntologyTerm, matchingTerms.size() > 0);

    }
}
