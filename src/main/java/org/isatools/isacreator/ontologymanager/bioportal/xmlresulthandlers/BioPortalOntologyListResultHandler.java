package org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyFormats;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntology;
import org.w3c.dom.NodeList;
import uk.ac.ebi.utils.xml.XPathReader;

import javax.xml.xpath.XPathConstants;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 11/07/2012
 *         Time: 14:31
 */
public class BioPortalOntologyListResultHandler {

    private static final Logger log = Logger.getLogger(BioPortalOntologyListResultHandler.class.getName());
    // maps an ontology with it's name to a Search result object

    private boolean loadAllOntologies = false;

    private static final String NO_LIST_BASEPATH = "/success/data/ontologyBean";
    private static final String LIST_BASEPATH = "/success/data/list/ontologyBean";

    /**
     * Parse the config.xml file
     *
     * @param fileLoc - location of file to be parsed!
     */
    public List<Ontology> parseFile(String fileLoc, boolean singleOntologyExpected) {
        List<Ontology> result = new ArrayList<Ontology>();

        XPathReader reader = null;
        try {
            reader = new XPathReader(new FileInputStream(fileLoc));
            
            String basePath = singleOntologyExpected ? NO_LIST_BASEPATH : LIST_BASEPATH;
            NodeList ontologyBeans = (NodeList) reader.read(basePath, XPathConstants.NODESET);

            if (ontologyBeans.getLength() > 0) {
                for (int sectionIndex = 0; sectionIndex <= ontologyBeans.getLength(); sectionIndex++) {
                    String abbreviation = (String) reader.read(basePath + "[" + sectionIndex + "]/abbreviation", XPathConstants.STRING);
                    String ontologyId = (String) reader.read(basePath + "[" + sectionIndex + "]/ontologyId", XPathConstants.STRING);


                    if (!abbreviation.isEmpty() && !(abbreviation.toLowerCase().contains("test") || abbreviation.toLowerCase().contains("installation."))
                            && shouldAddOntology(ontologyId)) {

                        Ontology ontology = new Ontology();
                        String version = (String) reader.read(basePath + "[" + sectionIndex + "]/id", XPathConstants.STRING);
                        String label = (String) reader.read(basePath + "[" + sectionIndex + "]/displayLabel", XPathConstants.STRING);
                        String format = (String) reader.read(basePath + "[" + sectionIndex + "]/format", XPathConstants.STRING);
                        String isView = (String) reader.read(basePath + "[" + sectionIndex + "]/isView", XPathConstants.STRING);
                        String contactName = (String) reader.read(basePath + "[" + sectionIndex + "]/contactName", XPathConstants.STRING);
                        String contactEmail = (String) reader.read(basePath + "[" + sectionIndex + "]/contactEmail", XPathConstants.STRING);
                        String url = (String) reader.read(basePath + "[" + sectionIndex + "]/homepage", XPathConstants.STRING);

                        NodeList categories = (NodeList) reader.read(basePath + "[" + sectionIndex + "]/categoryIds/int", XPathConstants.NODESET);

                        if (categories.getLength() > 0) {
                            for (int categoryIndex = 0; categoryIndex <= categories.getLength(); categoryIndex++) {
                                String categoryId = (String) reader.read(basePath + "[" + sectionIndex + "]/categoryIds/int[" + categoryIndex + "]", XPathConstants.STRING);
                                if (!categoryId.isEmpty()) {
                                    ontology.addCategory(categoryId);
                                }
                            }
                        }

                        ontology.setOntologyID(ontologyId);
                        ontology.setOntologyAbbreviation(abbreviation);
                        ontology.setOntologyVersion(version);
                        ontology.setOntologyDisplayLabel(label);
                        if (isFormatSupported(format)) {
                            ontology.setFormat(OntologyFormats.valueOf(format));
                        }
                        ontology.setContactName(contactName);
                        ontology.setContactEmail(contactEmail);
                        ontology.setHomePage(url);
                        ontology.setIsView(Boolean.valueOf(isView));

                        result.add(ontology);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return result;
    }

    private boolean shouldAddOntology(String ontologyId) {
        if (!loadAllOntologies) {
            for (AcceptedOntology acceptedOntology : AcceptedOntologies.values()) {
                if (acceptedOntology.toString().equals(ontologyId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Normally, we only parse those ontologies that are defined in the accepted ontologies xml document.
     * However, this can be overriden by setting this flag to true.
     *
     * @param loadAllOntologies - set to true to tell parser to load all ontologies, not just the 'accepted ontologies'.
     */
    public void setLoadAllOntologies(boolean loadAllOntologies) {
        this.loadAllOntologies = loadAllOntologies;
    }


    private boolean isFormatSupported(String format) {
        for (OntologyFormats of : OntologyFormats.values()) {
            if (of.getFormat().equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }
}
