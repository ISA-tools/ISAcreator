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
import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.utils.StringProcessing;

import java.io.Serializable;
import java.util.Map;


/**
 * Object to store Factor objects entered in a study.
 *
 * @author Eamonn Maguire
 */
public class Factor extends ISASection implements StudySubData, Serializable {

    public static final String FACTOR_NAME = "Study Factor Name";
    public static final String FACTOR_TYPE = "Study Factor Type";
    public static final String FACTOR_TYPE_TERM_ACCESSION = "Study Factor Type Term Accession Number";
    public static final String FACTOR_TYPE_SOURCE_REF = "Study Factor Type Term Source REF";


    public Factor() {
        super();
    }

    /**
     * Factor Object
     *
     * @param factorName - Name of Factor (e.g. Dose)
     * @param factorType - Type of Factor (e.g. Treatment Factor)
     */
    public Factor(String factorName, String factorType) {
        this(factorName, factorType, "", "");
    }

    /**
     * Factor Object
     *
     * @param factorName              - Name of Factor (e.g. Dose)
     * @param factorType              - Type of Factor (e.g. Treatment Factor)
     * @param factorTypeTermAccession - (If the term describing a Factor type is an ontology term) accession for Term (e.g. 0003422)
     * @param factorTypeTermSource    - (If the term describing a Factor type is an ontology term) source ref for the term (e.g. EFO)
     */
    public Factor(String factorName, String factorType,
                  String factorTypeTermAccession, String factorTypeTermSource) {
        super();
        fieldValues.put(FACTOR_NAME, factorName);
        fieldValues.put(FACTOR_TYPE, factorType);
        fieldValues.put(FACTOR_TYPE_TERM_ACCESSION, factorTypeTermAccession);
        fieldValues.put(FACTOR_TYPE_SOURCE_REF, factorTypeTermSource);
    }

    /**
     * Returns the name of the Factor
     *
     * @return String denoting the Factor Name
     */
    public String getFactorName() {
        return getValue(FACTOR_NAME);
    }


    /**
     * Returns the type of the Factor
     *
     * @return String denoting the Factor type
     */
    public String getFactorType() {
        return getValue(FACTOR_TYPE);
    }

    /**
     * Returns the type of the Factor
     *
     * @return String denoting the Factor Type Term Accession
     */
    public String getFactorTypeTermAccession() {
        return getValue(FACTOR_TYPE_TERM_ACCESSION);
    }

    /**
     * Returns the Term Source of the Factor type
     *
     * @return String denoting the Factor Type Term Source
     */
    public String getFactorTypeTermSource() {
        return getValue(FACTOR_TYPE_SOURCE_REF);
    }

    /**
     * Returns the identifer of the Factor.
     *
     * @return String denoting the Factor name
     * @see org.isatools.isacreator.gui.StudySubData getIdentifier()
     */
    public String getIdentifier() {
        return getFactorName();
    }

    /**
     * sets the Factor Type
     *
     * @param factorType - new Factor Type
     */
    public void setFactorType(String factorType) {
        fieldValues.put(FACTOR_TYPE, factorType);
    }

}
