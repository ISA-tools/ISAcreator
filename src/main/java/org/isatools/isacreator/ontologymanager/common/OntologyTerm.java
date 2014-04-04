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
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.utils.OntologyTermUtils;
import org.isatools.isacreator.settings.ISAcreatorProperties;

import java.util.HashMap;
import java.util.Map;

public class OntologyTerm implements Comparable<OntologyTerm> {

    public static final String THING = "Thing";

    private OntologySourceRefObject ontologySourceInformation;

    private String ontologyTermAccession = null;
    private String ontologyTermName = null;
    private String ontologyTermIRI = null;

    // extra terms for metadata processing
    private Map<String, String> comments;

    /***
     *
     * Constructor
     *
     * @param termName the label for the term
     * @param accession the term identifier
     * @param iri the iri for the term
     * @param ontologySourceRefObject an object representing the source ontology
     *
     */
    public OntologyTerm(String termName, String accession, String iri, OntologySourceRefObject ontologySourceRefObject) {
        ontologyTermName = termName;
        ontologyTermAccession = accession;
        ontologySourceInformation = ontologySourceRefObject;

        if (iri!=null)
            ontologyTermIRI = iri;
        else
            ontologyTermIRI = getOntologyTermURI();
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

    public String getOntologyTermAccession() {
        return ontologyTermAccession;
    }

    public OntologySourceRefObject getOntologySourceInformation() {
        return ontologySourceInformation;
    }

    public void setOntologySourceInformation(OntologySourceRefObject ontologySourceInformation) {
        this.ontologySourceInformation = ontologySourceInformation;
    }

    public void setOntologyTermAccession(String conceptIdShort) {

        if (conceptIdShort.contains("/")) {
            conceptIdShort = conceptIdShort.substring(conceptIdShort.lastIndexOf("/") + 1);
        }

        this.ontologyTermAccession = conceptIdShort;
        // we set this as the accession by default in case the xml does not contain all of the information.
        if (ontologyTermName == null) {
            this.ontologyTermName = conceptIdShort;
        }
    }

    public String getOntologyTermName() {
        return ontologyTermName;
    }

    public String getOntologyTermURI() {
        if (ontologyTermIRI==null){
            String iri = "";
            //System.out.println("term====>" + getOntologyTermName()+", iri ===> "+iri);
            if (iri!=null) {
                ontologyTermIRI = iri;
                OntologyManager.foundURI();
            }else
                ontologyTermIRI = "";
        }
        return ontologyTermIRI;
    }


    public void setOntologyTermName(String ontologyTermName) {
        this.ontologyTermName = ontologyTermName;
    }

    public void setOntologyTermIRI(String purl) {
        this.ontologyTermIRI = purl;
    }

    public void addToComments(String key, String value) {
        if (comments == null) {
            comments = new ListOrderedMap<String, String>();
        }
        comments.put(key, value);
    }

    /**
     * Retrieves a Map with the comments. Note that a copy of the map is retrieved to avoid
     * callers of the method being able to modify the private field.
     * @return
     */
    public Map<String, String> getComments() {
        return comments == null ? new HashMap<String, String>() : new HashMap<String, String>(comments);
    }

    @Override
    public String toString() {
        return getShortForm();
    }

    /***
     *
     * This method returns the string used for visualising the ontology term in the interface.
     *
     * This is "<ontology source>:<ontology label>".
     *
     * For example "OBI:parallel group design".
     *
     * @return
     */
    public String getShortForm() {
        String ontologySource = getOntologySource();
        if (!ontologySource.equals("")){
            if (ISAcreatorProperties.getOntologyTermURIProperty())
                if (ontologyTermIRI!=null && !ontologyTermIRI.equals(""))
                    return ontologySource + ":" + getOntologyTermName();
        }
        return getOntologyTermName();
    }

    public String getLongForm(){
        String ontologySource = getOntologySource();
        if (!ontologySource.equals("")) {
            return getOntologyTermName()+","+ getOntologyTermURI()+","+ontologySource;
        }
        return getOntologyTermName();
    }

    public int compareTo(OntologyTerm ontologyTerm) {
        if (getOntologyTermURI()!=null && ontologyTerm.getOntologyTermURI()!=null)
            return getOntologyTermURI().compareTo(ontologyTerm.getOntologyTermURI());
        return getShortForm().toLowerCase().compareTo(ontologyTerm.getShortForm().toLowerCase());
    }

}
