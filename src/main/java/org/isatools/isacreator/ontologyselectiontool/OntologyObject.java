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

package org.isatools.isacreator.ontologyselectiontool;

import java.io.Serializable;

/**
 * OntologyObject is used to save the ontology selections made by users and to provide a way of saving
 * the selections over multiple sessions.
 *
 * @author Eamonn Maguire
 */
public class OntologyObject implements Serializable {
    // standard information pertaining to an Ontology item
    private String term;
    private String termAccession;
    private String termSourceRef;

    // id which is shown in cell/field for identification of relevant history object for tooltip text
    private String uniqueId;

    // no. of times this term has been selected previously
    private int usageFrequency;

    public OntologyObject(String term, String termAccession,
                          String termSourceRef) {
        this.term = term;
        this.termAccession = termAccession;
        this.termSourceRef = termSourceRef;

        uniqueId = this.termSourceRef + ":" + this.term;

        // history object is being created for something which was created, any history object must have been selected
        // at least once!
        usageFrequency = 1;
    }

    /**
     * Get the Term itself e.g. Homo Sapiens
     *
     * @return String - should not be null
     */
    public String getTerm() {
        return term;
    }

    /**
     * Get the term accession e.g. 9606
     *
     * @return String
     */
    public String getTermAccession() {
        return termAccession;
    }

    /**
     * Get the term source reference e.g. NEWT
     *
     * @return String
     */
    public String getTermSourceRef() {
        return termSourceRef;
    }

    /**
     * Get the unique id for the term, formed as SOURCE:TERM e.g. NEWT:Homo Sapiens
     *
     * @return String
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Get number of times this term was selected
     *
     * @return Integer
     */
    public int getUsageFrequency() {
        return usageFrequency;
    }

    /**
     * Increment the number of times this term was used.
     */
    public void incrementUsageFrequency() {
        usageFrequency++;
    }
}
