package org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyFormats;
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

    /**
     * Parse the config.xml file
     *
     * @param fileLoc - location of file to be parsed!
     */
    public List<Ontology> parseFile(String fileLoc) {
        List<Ontology> result = new ArrayList<Ontology>();
        System.out.println(fileLoc);

        XPathReader reader = null;
        try {
            reader = new XPathReader(new FileInputStream(fileLoc));
            NodeList ontologyBeans = (NodeList) reader.read("/success/data/list/ontologyBean", XPathConstants.NODESET);

            if (ontologyBeans.getLength() > 0) {
                for (int sectionIndex = 0; sectionIndex <= ontologyBeans.getLength(); sectionIndex++) {
                    String abbreviation = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/abbreviation", XPathConstants.STRING);

                    if (!abbreviation.isEmpty() && !(abbreviation.toLowerCase().contains("test") || abbreviation.toLowerCase().contains("installation."))) {

                        Ontology ontology = new Ontology();
                        String ontologyId = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/ontologyId", XPathConstants.STRING);
                        String version = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/id", XPathConstants.STRING);
                        String label = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/displayLabel", XPathConstants.STRING);
                        String format = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/format", XPathConstants.STRING);
                        String isView = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/isView", XPathConstants.STRING);
                        String contactName = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/contactName", XPathConstants.STRING);
                        String contactEmail = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/contactEmail", XPathConstants.STRING);
                        String url = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/homepage", XPathConstants.STRING);

                        NodeList categories = (NodeList) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/categoryIds/int", XPathConstants.NODESET);

                        if (categories.getLength() > 0) {
                            for (int categoryIndex = 0; categoryIndex <= categories.getLength(); categoryIndex++) {
                                String categoryId = (String) reader.read("/success/data/list/ontologyBean[" + sectionIndex + "]/categoryIds/int[" + categoryIndex + "]", XPathConstants.STRING);
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
