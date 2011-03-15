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

package org.isatools.isacreator.io.importisa.investigationfileproperties;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eamonn Maguire
 * @date Jul 9, 2009
 */


public class ISAPropertiesLoader extends DefaultHandler {
    private static final Logger log = Logger.getLogger(ISAPropertiesLoader.class.getName());

    private final static String FILE_LOC = "/defaultConfigs/InvestigationFileStructure.xml";
    private final static int SECTION = 0;
    private final static int INV_STRUCTURE = 1;
    private final static int STUDY_STRUCTURE = 2;
    private Map<String, ISAPropertiesSection> sections;
    private List<String> columnsToIgnore;
    private ISAPropertiesSection ISAPropertiesSection;
    private StringBuilder preferredFileStructure = new StringBuilder();

    private int readStatus = -1;
    private boolean problemsEncountered = false;
    private String problemLog;

    public ISAPropertiesLoader() {
        sections = new HashMap<String, ISAPropertiesSection>();
        columnsToIgnore = new ArrayList<String>();
        parseConfigFile();
    }


    public void characters(char[] ch, int start, int length) {
        String data = new String(ch, start, length);
        if (!data.trim().equals("")) {
            if (readStatus == SECTION) {

                if (isInteger(data)) {
                    ISAPropertiesSection.setNumRows(Integer.valueOf(data));
                    sections.put(ISAPropertiesSection.getName(), ISAPropertiesSection);
                } else {
                    ISAPropertiesSection = new ISAPropertiesSection();
                    ISAPropertiesSection.setName(data);
                }
            }

            if (readStatus == INV_STRUCTURE) {
                preferredFileStructure.append(data.trim());
            }

            if (readStatus == STUDY_STRUCTURE) {
                preferredFileStructure.append(" [");
                preferredFileStructure.append(data.trim());
                preferredFileStructure.append("]*");
            }
        }
    }

    public String getProblemLog() {
        return "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" + ".bodyFont {" +
                "   font-family: Verdana;" + "   font-size: 10px;" +
                "   color: #BF1E2D;" + "}" + "-->" + "</style>" + "</head>" +
                "<body class=\"bodyFont\">" +
                "<b>Problem loading configuration files</b>" + problemLog +
                "</body></html>";
    }

    public boolean isProblemsEncountered() {
        return problemsEncountered;
    }

    public List<String> getColumnsToIgnore() {
        return columnsToIgnore;
    }

    public Map<String, ISAPropertiesSection> getSections() {
        return sections;
    }

    /**
     * Parse the config.xml file
     */
    private void parseConfigFile() {
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(getClass().getResourceAsStream(FILE_LOC), this);
        } catch (SAXException se) {
            problemsEncountered = true;
            problemLog = "<p>" + FILE_LOC + " file is malformed " + se.getMessage() + "</p>";
            log.error("SAX Exception Caught: \n Details are: \n" +
                    se.getMessage());
        } catch (ParserConfigurationException pce) {
            problemsEncountered = true;
            problemLog = "<p>" + FILE_LOC + " file is malformed " + pce.getMessage() + "</p>";
            log.error(
                    "Parser Configuration Exception Caught: \n Details are: \n" +
                            pce.getMessage());
        } catch (IOException ioe) {
            problemsEncountered = true;
            problemLog = "<p>" + FILE_LOC + " file not found in the selected directory...</p>";
            log.error("File not found: \n Details are: \n" +
                    ioe.getMessage());
        }
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
        if (qName.equalsIgnoreCase("section")) {
            readStatus = SECTION;
        } else if (qName.equalsIgnoreCase("investigationStructure")) {
            readStatus = INV_STRUCTURE;
        } else if (qName.equalsIgnoreCase("studyStructure")) {
            readStatus = STUDY_STRUCTURE;
        }
    }

    private boolean isInteger(String data) {
        try {
            Integer.valueOf(data);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public String getPreferredFileStructure() {
        return preferredFileStructure.toString();
    }
}
