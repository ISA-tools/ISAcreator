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

package org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyFormats;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eamonnmaguire
 * @date Jul 17, 2009
 */


public class BioPortalOntologyListResultHandler extends DefaultHandler {
    private static final Logger log = Logger.getLogger(BioPortalOntologyListResultHandler.class.getName());
    // maps an ontology with it's name to a Search result object

    private Ontology currentOntologyEntry;
    private List<Ontology> result;

    private static final int READING_ONTOLOGY = 1;
    private static final int READING_ONT_ID = 2;
    private static final int READING_ONT_LABEL = 3;
    private static final int READING_ONT_ABBREVIATION = 4;
    private static final int READING_ONT_VERSION = 5;
    private static final int READING_IS_FOUNDRY = 6;
    private static final int READING_FORMAT = 7;
    private static final int READING_CONTACT = 8;
    private static final int READING_CONTACT_EMAIL = 9;
    private static final int READING_URL = 10;
    private static final int READING_IS_VIEW = 11;

    private static final int IGNORE_READ = -1;

    private int currentReadStatus = -1;


    /**
     * Parse the config.xml file
     *
     * @param fileLoc - location of file to be parsed!
     */
    public List<Ontology> parseFile(String fileLoc) {
        result = new ArrayList<Ontology>();
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(fileLoc, this);
            return result;
        } catch (SAXException se) {
            log.error("SAX Exception Caught: \n Details are: \n" +
                    se.getMessage());
        } catch (ParserConfigurationException pce) {
            log.error(
                    "Parser Configuration Exception Caught: \n Details are: \n" +
                            pce.getMessage());
        } catch (IOException ioe) {
            log.error("File not found: \n Details are: \n" +
                    ioe.getMessage());
        }

        return null;
    }

    /**
     * Process start element to determine if information about tables or mappings is being read in.
     *
     * @param namespaceURI *
     * @param localName    *
     * @param qName        - qualifier name for XML tag.
     * @param atts         - Attributes of element.
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) {
        if (qName.equalsIgnoreCase("ontologyBean")) {
            currentReadStatus = READING_ONTOLOGY;
            currentOntologyEntry = new Ontology();
        } else if (qName.equalsIgnoreCase("ontologyId")) {
            currentReadStatus = READING_ONT_ID;
        } else if (qName.equalsIgnoreCase("displayLabel")) {
            currentReadStatus = READING_ONT_LABEL;
        } else if (qName.equalsIgnoreCase("abbreviation")) {
            currentReadStatus = READING_ONT_ABBREVIATION;
        } else if (qName.equalsIgnoreCase("id")) {
            currentReadStatus = READING_ONT_VERSION;
        } else if (qName.equalsIgnoreCase("isFoundry")) {
            currentReadStatus = READING_IS_FOUNDRY;
        } else if (qName.equalsIgnoreCase("format")) {
            currentReadStatus = READING_FORMAT;
        } else if (qName.equalsIgnoreCase("homepage")) {
            currentReadStatus = READING_URL;
        } else if (qName.equalsIgnoreCase("contactName")) {
            currentReadStatus = READING_CONTACT;
        } else if (qName.equalsIgnoreCase("contactEmail")) {
            currentReadStatus = READING_CONTACT_EMAIL;
        } else if (qName.equalsIgnoreCase("isView")) {
            currentReadStatus = READING_IS_VIEW;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {
        if (qName.equalsIgnoreCase("ontologyBean")) {

            if (!currentOntologyEntry.getOntologyAbbreviation().contains("test") &&
                    !currentOntologyEntry.getOntologyDisplayLabel().contains("test"))
                result.add(currentOntologyEntry);
            currentReadStatus = IGNORE_READ;
        }
    }

    public void characters(char[] ch, int start, int length) {
        String data = new String(ch, start, length).trim();
        if (!data.equals("")) {
            if (currentReadStatus == READING_ONT_ID) {
                currentOntologyEntry.setOntologyID(data);
            } else if (currentReadStatus == READING_ONT_LABEL) {
                currentOntologyEntry.setOntologyDisplayLabel(data);
            } else if (currentReadStatus == READING_ONT_ABBREVIATION) {
                currentOntologyEntry.setOntologyAbbreviation(data);
            } else if (currentReadStatus == READING_ONT_VERSION) {
                currentOntologyEntry.setOntologyVersion(data);
            } else if (currentReadStatus == READING_IS_FOUNDRY) {
                boolean isFoundry = data.trim().equals("1");
                currentOntologyEntry.setFoundry(isFoundry);
            } else if (currentReadStatus == READING_FORMAT) {
                String trimmedSource = data.contains("-") ? data.substring(0, data.indexOf("-")).trim().toUpperCase() : data.trim().toUpperCase();
                if (isFormatSupported(trimmedSource)) {
                    currentOntologyEntry.setFormat(OntologyFormats.valueOf(trimmedSource));
                }
            } else if (currentReadStatus == READING_CONTACT) {
                currentOntologyEntry.setContactName(data);
            } else if (currentReadStatus == READING_CONTACT_EMAIL) {
                currentOntologyEntry.setContactEmail(data);
            } else if (currentReadStatus == READING_URL) {
                currentOntologyEntry.setHomePage(data);
            } else if (currentReadStatus == READING_IS_VIEW) {
                boolean isView = Boolean.valueOf(data);
                currentOntologyEntry.setIsView(isView);
            }
            currentReadStatus = IGNORE_READ;
        }
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
