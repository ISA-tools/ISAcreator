/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
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


package org.isatools.isacreator.ontologymanager.common;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;

import java.util.HashMap;
import java.util.Map;


public class OntologyTerm implements Comparable<OntologyTerm> {

    public static final String THING = "Thing";

    private OntologySourceRefObject ontologySourceInformation;

    private String ontologySourceAccession;
    private String ontologyTermName;
    private String purl = "";

    // extra terms for metadata processing
    private Map<String, String> comments;

    public OntologyTerm() {
    }

    public OntologyTerm(String termName, String accession, OntologySourceRefObject ontologySourceRefObject) {
        ontologyTermName = termName;
        ontologySourceAccession = accession;
        ontologySourceInformation = ontologySourceRefObject;
    }

    public String getOntologyVersionId() {
        if (ontologySourceInformation == null) {
            return "";
        }
        return ontologySourceInformation.getSourceVersion();
    }

    public String getOntologySource() {
        if (ontologySourceInformation == null) {
            return "";
        }
        return ontologySourceInformation.getSourceName();
    }

    public String getOntologySourceAccession() {
        return ontologySourceAccession;
    }

    public void setOntologySourceInformation(OntologySourceRefObject ontologySourceInformation) {
        this.ontologySourceInformation = ontologySourceInformation;
    }

    public OntologySourceRefObject getOntologySourceInformation() {
        return ontologySourceInformation;
    }

    public void setOntologySourceAccession(String conceptIdShort) {

        if (conceptIdShort.contains("/")) {
            conceptIdShort = conceptIdShort.substring(conceptIdShort.lastIndexOf("/") + 1);
        }

        this.ontologySourceAccession = conceptIdShort;
        // we set this as the accession by default in case the xml does not contain all of the information.
        if (ontologyTermName == null) {
            this.ontologyTermName = conceptIdShort;
        }
    }


    public String getOntologyTermName() {
        if (ontologyTermName == null) {
            return null;
        } else {
            return ontologyTermName.contains(":")
                    ? ontologyTermName.substring(ontologyTermName.indexOf(":") + 1)
                    : ontologyTermName;
        }
    }

    public void setOntologyTermName(String ontologyTermName) {
        this.ontologyTermName = ontologyTermName;
    }

    public String getOntologyPurl() {
        return purl;
    }

    public void setOntologyPurl(String purl) {
        this.purl = purl;
    }

    @Override
    public String toString() {
        return getOntologyTermName() + "(" + getOntologySourceAccession() + ")";
    }

    public void addToComments(String key, String value) {
        if (comments == null) {
            comments = new ListOrderedMap<String, String>();
        }

        comments.put(key, value);
    }

    public Map<String, String> getComments() {
        return comments == null ? new HashMap<String, String>() : comments;
    }

    /***
     * TODO: Change this to return the PURL instead, if not null or empty
     *
     * @return
     */
    public String getUniqueId() {

        String ontologySource = getOntologySource();

        if (!ontologySource.equals("")) {
            return ontologySource + ":" + getOntologyTermName();
        }

        return getOntologyTermName();
    }


    public int compareTo(OntologyTerm ontologyTerm) {
        return getOntologyTermName().toLowerCase().compareTo(ontologyTerm.getOntologyTermName().toLowerCase());
    }
}
