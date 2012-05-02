package org.isatools.isacreator.ontologymanager.bioportal.io;

import org.w3c.dom.NodeList;
import uk.ac.ebi.utils.xml.XPathReader;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a file defining allowed BioPortal ontologies into a file.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         Date: 02/05/2012
 *         Time: 11:01
 */
public class AcceptedOntologiesLoader {

    private static final String FILE = "/defaultConfigs/bioportal-accepted-ontologies.xml";


    public static List<AcceptedOntology> getAcceptedOntologies() {
        XPathReader reader = new XPathReader(AcceptedOntologiesLoader.class.getResourceAsStream(FILE));

        List<AcceptedOntology> acceptedOntologies = new ArrayList<AcceptedOntology>();

        NodeList sections = (NodeList) reader.read("/acceptedOntologies/acceptedOntology", XPathConstants.NODESET);

        if (sections.getLength() > 0) {
            for (int sectionIndex = 0; sectionIndex <= sections.getLength(); sectionIndex++) {
                String id = (String) reader.read("/acceptedOntologies/acceptedOntology[" + sectionIndex + "]/@id", XPathConstants.STRING);
                String abbreviation = (String) reader.read("/acceptedOntologies/acceptedOntology[" + sectionIndex + "]/@abbreviation", XPathConstants.STRING);
                acceptedOntologies.add(new AcceptedOntology(id, abbreviation));
            }
        }

        return acceptedOntologies;
    }

    public static void main(String[] args) {
        getAcceptedOntologies();
    }

}
