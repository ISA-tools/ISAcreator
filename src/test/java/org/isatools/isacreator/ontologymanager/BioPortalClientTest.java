/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 �The contents of this file are subject to the CPAL version 1.0 (the �License�);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an �AS IS� basis,
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
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

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
    public static String testOntologyID = "1136";
    public static String testTermAccession = "efo:EFO_0000428";
    public static String testOntologyVersion = "45781";
    public static String testSearchTerm = "dose";

    public static String obiID = "1123";
    public static String obiVersion = "47893";

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

    @Test
    public void getTermInformation() {
        System.out.println("_____Testing getTermInformation()____");

        OntologyTerm term = client.getTermInformation(testTermAccession, testOntologyVersion);

        assertTrue("No metadata found for " + testOntologyID, term != null);

        System.out.println("Found " + term.getComments().size() + " properties for " + term.getOntologyTermName());

        System.out.println("Comments " + term.getComments());

        System.out.println("Ontology PURL "+term.getOntologyTermURI());

    }


    @Test
    public void getOBITermInformation() {
        System.out.println("_____Testing getTermInformation()____");

        OntologyTerm term = client.getTermInformation("obo:OBI_0500006", obiVersion);

        assert(term!=null);

        System.out.println("Found " + term.getComments().size() + " properties for " + term.getOntologyTermName());

        System.out.println("Comments " + term.getComments());

        System.out.println("Ontology PURL "+term.getOntologyTermURI());

    }


    @Test
    public void getTermMetadata() {
        System.out.println("_____Testing getTermMetadata()____");

        Map<String, String> map = client.getTermMetadata(testTermAccession, testOntologyVersion);

        assertTrue("No metadata found for " + testOntologyID, map != null);

        System.out.println("Map " + map);

    }


    @Test
    public void getOntologyById() {
        System.out.println("_____Testing getOntologyById()____");

        Ontology ontology = client.getOntologyById(testOntologyID);

        assertTrue("No ontology found for " + testOntologyID, ontology != null);

        System.out.println("Ontology found -> " + ontology.toString() + "\n");
    }

    @Test
    public void getLatestOntologyVersion() {
        System.out.println("_____Testing getLatestOntologyVersion()____");

        String ontologyVersion = client.getLatestOntologyVersion(testOntologyID);

        assertTrue("Latest ontology version not found for " + testOntologyID, ontologyVersion != null);

        System.out.println("Latest ontology version found for " + testOntologyID + " -> " + ontologyVersion + "\n");
    }

    @Test
    public void getTermsByPartialNameFromSource() {
        System.out.println("_____Testing getTermsByPartialNameFromSource()____");

        Map<OntologySourceRefObject, List<OntologyTerm>> result = client.getTermsByPartialNameFromSource(testSearchTerm, "all", false);

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
    public void getOntologyRoots() {
        System.out.println("_____Testing getOntologyRoots()____");
        Map<String, OntologyTerm> ontologyRoots = client.getOntologyRoots(testOntologyVersion);

        assertTrue("No ontology roots found for " + testOntologyVersion, ontologyRoots.size() > 0);


        System.out.println("Found " + ontologyRoots.size() + " roots for " + testOntologyVersion);
    }

    @Test
    public void getTermParents() {
        System.out.println("_____Testing getTermChildrenOrParents()____");

        Map<String, OntologyTerm> parentTerms = client.getAllTermParents(testTermAccession, testOntologyVersion);

        assertTrue("No parents roots found for " + testTermAccession, parentTerms.size() > 0);

        for (OntologyTerm term : parentTerms.values()) {
            System.out.println(term);
        }

        System.out.println("Found " + parentTerms.size() + " parents for 45781");
    }

    @Test
    public void downloadOntologyTest() {
        System.out.println("_____Testing downloadOntology()____");

        //obi version
        client.downloadOntology("47893");
    }

    @Test
    public void downloadOntologyTest2() {
        System.out.println("_____Testing downloadOntology()____");

        //a previous obi version
        client.downloadOntology("47203");
    }

    @Test
    public void downloadLatestVersionOntologyTest(){
        System.out.println("_____Testing downloadLatestVersionOntology()____");

        //obi
        client.downloadLatestVersionOntology("1127");
    }

    @Test
    public void getRDFTermTest(){
        System.out.println("_____Testing getRDFTerm()____");

        //obi
        //client.getRDFTerm("47893", "obo:OBI_0000634");
        client.getRDFTerm("47893", "obo:OBI_0000634");
    }

    @Test
    public void getTermIRITest(){
        System.out.println("_____Testing getTermIRI()____");


        String termAccession = "obo:OBI_0000634";
        //obi
        //client.getRDFTerm("47893", "obo:OBI_0000634");
        String url = client.getTermIRI("47893", termAccession);

        System.out.println("URL for term "+termAccession+ " is "+ url );
    }

    //@Test
    public void getTermIRITest2(){
        System.out.println("_____Testing getTermIRI()____");

        String termAccession = "PATO:0000011";

        String url = client.getTermIRI("49640", termAccession);

        System.out.println("URL for term "+termAccession+ " is "+ url );
    }


}
