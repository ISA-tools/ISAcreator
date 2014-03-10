package org.isatools.isacreator.ontologymanager.bioportal.io;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.BioPortal4Client;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Comment;
import uk.ac.ebi.utils.xml.XPathReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Loads a file defining allowed BioPortal ontologies into a file.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         Date: 02/05/2012
 *         Time: 11:01
 */
public class AcceptedOntologiesLoader {

    private static final String FILE = "ProgramData/cache/bioportal-ontology-detail.cache";


    public static void populateAcceptedOntologies() {

        System.out.println("_____populateAcceptedOntologies()____");
        BioPortal4Client client = new BioPortal4Client();

        Collection<Ontology> ontologies = client.getAllOntologies();

        System.out.println("Found " + ontologies.size() + " ontologies \n");

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();

            Comment comment = doc.createComment("Defines the accepted ontologies for BioPortal");
            doc.appendChild(comment);


            Element rootElement = doc.createElement("acceptedOntologies");
            doc.appendChild(rootElement);

            //<acceptedOntology abbreviation="OBI" id="1123"/>
            for (Ontology ontology : ontologies) {

                Element acceptedOntologyElement = doc.createElement("acceptedOntology");

                Attr abbreviation = doc.createAttribute("abbreviation");
                abbreviation.setValue(ontology.getOntologyAbbreviation());
                acceptedOntologyElement.setAttributeNode(abbreviation);

                Attr name = doc.createAttribute("name");
                name.setValue(ontology.getOntologyDisplayLabel());
                acceptedOntologyElement.setAttributeNode(name);

                Attr version = doc.createAttribute("version");
                version.setValue(ontology.getSubmissionId());
                acceptedOntologyElement.setAttributeNode(version);

                Attr uri = doc.createAttribute("uri");
                uri.setValue(ontology.getHomepage());
                acceptedOntologyElement.setAttributeNode(uri);

                Attr id = doc.createAttribute("id");
                id.setValue(ontology.getOntologyID());
                acceptedOntologyElement.setAttributeNode(id);

                rootElement.appendChild(acceptedOntologyElement);

            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            String fullPath = FILE;

            File outputFile = new File(fullPath);

            StreamResult result = new StreamResult(outputFile.getAbsolutePath());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }


    public static Map<String, Ontology> getAcceptedOntologies() {
        Map<String, Ontology> acceptedOntologies = new HashMap<String, Ontology>();

        if(!new File(FILE).exists()) {
            // then create it
            new File(FILE).getParentFile().mkdirs();
            populateAcceptedOntologies();
        }

        try {
            XPathReader reader = new XPathReader(new FileInputStream(FILE));
            NodeList sections = (NodeList) reader.read("/acceptedOntologies/acceptedOntology", XPathConstants.NODESET);

            if (sections.getLength() > 0) {
                for (int sectionIndex = 0; sectionIndex <= sections.getLength(); sectionIndex++) {
                    String id = (String) reader.read("/acceptedOntologies/acceptedOntology[" + sectionIndex + "]/@id", XPathConstants.STRING);
                    String abbreviation = (String) reader.read("/acceptedOntologies/acceptedOntology[" + sectionIndex + "]/@abbreviation", XPathConstants.STRING);
                    String name = (String) reader.read("/acceptedOntologies/acceptedOntology[" + sectionIndex + "]/@name", XPathConstants.STRING);
                    String version = (String) reader.read("/acceptedOntologies/acceptedOntology[" + sectionIndex + "]/@version", XPathConstants.STRING);
                    String homepage = (String) reader.read("/acceptedOntologies/acceptedOntology[" + sectionIndex + "]/@uri", XPathConstants.STRING);

                    Ontology ontology = new Ontology(id, version, abbreviation, name);
                    ontology.setHomePage(homepage);

                    acceptedOntologies.put(id, ontology);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return acceptedOntologies;
    }

    public static void main(String[] args) {
        //getAcceptedOntologies();
        populateAcceptedOntologies();
    }

}
