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

package org.isatools.isacreator.model;

import org.isatools.isacreator.gui.StudySubData;

import java.io.Serializable;


/**
 * Object to store Protocol information.
 *
 * @author Eamonn Maguire
 */
public class Protocol extends ISASection implements StudySubData, Serializable, Comparable<Protocol> {

    public static final String PROTOCOL_NAME = "Study Protocol Name";
    public static final String PROTOCOL_TYPE = "Study Protocol Type";
    public static final String PROTOCOL_TYPE_TERM_ACCESSION = "Study Protocol Type Term Accession Number";
    public static final String PROTOCOL_TYPE_TERM_SOURCE_REF = "Study Protocol Type Term Source REF";
    public static final String PROTOCOL_DESCRIPTION = "Study Protocol Description";
    public static final String PROTOCOL_URI = "Study Protocol URI";
    public static final String PROTOCOL_VERSION = "Study Protocol Version";
    public static final String PROTOCOL_PARAMETER_NAME = "Study Protocol Parameters Name";
    public static final String PROTOCOL_PARAMETER_NAME_TERM_ACCESSION = "Study Protocol Parameters Name Term Accession Number";
    public static final String PROTOCOL_PARAMETER_NAME_TERM_SOURCE_REF = "Study Protocol Parameters Name Term Source REF";
    public static final String PROTOCOL_COMPONENT_NAME = "Study Protocol Components Name";
    public static final String PROTOCOL_COMPONENT_TYPE = "Study Protocol Components Type";
    public static final String PROTOCOL_COMPONENT_TYPE_TERM_ACCESSION = "Study Protocol Components Type Term Accession Number";
    public static final String PROTOCOL_COMPONENT_TYPE_TERM_SOURCE_REF = "Study Protocol Components Type Term Source REF";


    public Protocol() {
        super();
    }


    /**
     * @param protocolName          - Name for protocol
     * @param protocolType          - Type of protocol
     * @param protocolDescription   - Description for protocol
     * @param protocolURL           - URL for protocol
     * @param protocolVersion       - Version of protocol
     * @param protocolParameterName - Name of parameter for protocol
     * @param protocolComponentName - name of component used in protocol
     * @param protocolComponentType - Type of component being used.
     */
    public Protocol(String protocolName, String protocolType,
                    String protocolDescription, String protocolURL,
                    String protocolVersion, String protocolParameterName,
                    String protocolComponentName, String protocolComponentType) {
        this(protocolName, protocolType, "", "", protocolDescription, protocolURL, protocolVersion,
                protocolParameterName, "", "", protocolComponentName, protocolComponentType, "", "");
    }

    /**
     * @param protocolName                   - Name for protocol
     * @param protocolType                   - Type of protocol
     * @param protocolTypeTermAccession      - Accession (e.g. 0000123) for the Ontology term defined for protocolType
     * @param protocolTypeTermSourceRef      - Source REF (e.g. OBI) for the Ontology term defined for protocolType
     * @param protocolDescription            - Description for protocol
     * @param protocolURL                    - URL for protocol
     * @param protocolVersion                - Version of protocol
     * @param protocolParameterName          - Name of parameter for protocol (; separated)
     * @param protocolParameterNameAccession - Accession (e.g. 0000123) for the Ontology term defined for protocolParameterName (; separated)
     * @param protocolParameterNameSource    - Source REF (e.g. OBI) for the Ontology term defined for protocolParameterName (; separated)
     * @param protocolComponentName          - name of component used in protocol
     * @param protocolComponentType          - Type of component being used.
     * @param protocolComponentTypeAccession - Accession (e.g. 0000123) for the Ontology term defined for protocolComponentType (; separated)
     * @param protocolComponentTypeSource    - Source REF (e.g. OBI) for the Ontology term defined for protocolComponentType (; separated)
     */
    public Protocol(String protocolName, String protocolType, String protocolTypeTermAccession, String protocolTypeTermSourceRef, String protocolDescription, String protocolURL, String protocolVersion, String protocolParameterName, String protocolParameterNameAccession, String protocolParameterNameSource, String protocolComponentName, String protocolComponentType, String protocolComponentTypeAccession, String protocolComponentTypeSource) {
        super();
        fieldValues.put(PROTOCOL_NAME, protocolName);
        fieldValues.put(PROTOCOL_TYPE, protocolType);
        fieldValues.put(PROTOCOL_TYPE_TERM_ACCESSION, protocolTypeTermAccession);
        fieldValues.put(PROTOCOL_TYPE_TERM_SOURCE_REF, protocolTypeTermSourceRef);
        fieldValues.put(PROTOCOL_DESCRIPTION, protocolDescription);
        fieldValues.put(PROTOCOL_URI, protocolURL);
        fieldValues.put(PROTOCOL_VERSION, protocolVersion);
        fieldValues.put(PROTOCOL_PARAMETER_NAME, protocolParameterName);
        fieldValues.put(PROTOCOL_PARAMETER_NAME_TERM_ACCESSION, protocolParameterNameAccession);
        fieldValues.put(PROTOCOL_PARAMETER_NAME_TERM_SOURCE_REF, protocolParameterNameSource);
        fieldValues.put(PROTOCOL_COMPONENT_NAME, protocolComponentName);
        fieldValues.put(PROTOCOL_COMPONENT_TYPE, protocolComponentType);
        fieldValues.put(PROTOCOL_COMPONENT_TYPE_TERM_ACCESSION, protocolComponentTypeAccession);
        fieldValues.put(PROTOCOL_COMPONENT_TYPE_TERM_SOURCE_REF, protocolComponentTypeSource);
    }

    public String getIdentifier() {
        return getValue(PROTOCOL_NAME);
    }

    public String getProtocolComponentName() {
        return getValue(PROTOCOL_COMPONENT_NAME);
    }

    public String getProtocolDescription() {
        return getValue(PROTOCOL_DESCRIPTION);
    }

    public String getProtocolName() {
        return getValue(PROTOCOL_NAME);
    }

    public String getProtocolParameterName() {
        return getValue(PROTOCOL_PARAMETER_NAME);
    }

    public String getProtocolType() {
        return getValue(PROTOCOL_TYPE);
    }

    public String getProtocolTypeTermAccession() {
        return getValue(PROTOCOL_TYPE_TERM_ACCESSION);
    }

    public String getProtocolTypeTermSourceRef() {
        return getValue(PROTOCOL_TYPE_TERM_SOURCE_REF);
    }

    public String getProtocolURL() {
        return getValue(PROTOCOL_URI);
    }

    public String getProtocolVersion() {
        return getValue(PROTOCOL_VERSION);
    }

    public void setProtocolType(String protocolType) {
        fieldValues.put(PROTOCOL_TYPE, protocolType);
    }

    public void setProtocolComponentType(String protocolComponentType) {
        fieldValues.put(PROTOCOL_COMPONENT_TYPE, protocolComponentType);
    }

    public void setProtocolParameterName(String protocolParameterName) {
        fieldValues.put(PROTOCOL_PARAMETER_NAME, protocolParameterName);
    }

    public String getProtocolParameterNameAccession() {
        return getValue(PROTOCOL_PARAMETER_NAME_TERM_ACCESSION);
    }

    public String getProtocolParameterNameSource() {
        return getValue(PROTOCOL_PARAMETER_NAME_TERM_SOURCE_REF);

    }

    public String getProtocolComponentType() {
        return getValue(PROTOCOL_COMPONENT_TYPE);

    }

    public String getProtocolComponentTypeAccession() {
        return getValue(PROTOCOL_COMPONENT_TYPE_TERM_ACCESSION);
    }

    public String getProtocolComponentTypeSource() {
        return getValue(PROTOCOL_COMPONENT_TYPE_TERM_SOURCE_REF);
    }

    @Override
    public String toString() {
        return getProtocolName();
    }

    public int compareTo(Protocol protocol) {
        return getProtocolName().compareToIgnoreCase(protocol.getProtocolName());
    }
}
