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

package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.model.ISASection;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;

import java.io.Serializable;


/**
 * Ontology object stores ontologies used by the user when describing their experiment, and contains
 * definitions of the ontologies.
 *
 * @author Eamonn Maguire
 * @date Jun 12, 2008
 */
public class OntologySourceRefObject extends ISASection implements Serializable, Comparable<OntologySourceRefObject> {

    public static final String SOURCE_NAME = "Term Source Name";
    public static final String SOURCE_FILE = "Term Source File";
    public static final String SOURCE_VERSION = "Term Source Version";
    public static final String SOURCE_DESCRIPTION = "Term Source Description";

    public OntologySourceRefObject() {
        super();
    }

    /**
     * @param sourceName        - e.g. GO
     * @param sourceFile        - e.g. URL (e.g. the BioPortal virtual id)
     * @param sourceVersion     - e.g. 0.2.1 or the BioPortal version id
     * @param sourceDescription e.g. Gene Ontology
     */
    public OntologySourceRefObject(String sourceName, String sourceFile,
                                   String sourceVersion, String sourceDescription) {
        super();
        fieldValues.put(SOURCE_NAME, sourceName);
        fieldValues.put(SOURCE_FILE, sourceFile);
        fieldValues.put(SOURCE_VERSION, sourceVersion);
        fieldValues.put(SOURCE_DESCRIPTION, sourceDescription);
        update();
    }

    public void update(){
        if (getSourceName()==null)
            return;
        String ontologyId = "http://data.bioontology.org/ontologies/"+getSourceName();

        Ontology associatedOntologySource = AcceptedOntologies.getAcceptedOntologies().get(ontologyId);

        if (associatedOntologySource==null)
            return;

        setSourceFile(associatedOntologySource.getOntologyID());
        setSourceVersion( associatedOntologySource.getOntologyVersion());
        setSourceDescription(associatedOntologySource.getOntologyDisplayLabel());
    }

    public String getSourceDescription() {
        return fieldValues.get(SOURCE_DESCRIPTION);
    }

    public String getSourceFile() {
        return fieldValues.get(SOURCE_FILE);
    }

    public String getSourceName() {
        return fieldValues.get(SOURCE_NAME);
    }

    public String getSourceVersion() {
        return fieldValues.get(SOURCE_VERSION);
    }

    public void setSourceName(String sourceName) {
        fieldValues.put(SOURCE_NAME, sourceName);
    }

    public void setSourceDescription(String sourceDescription) {
        fieldValues.put(SOURCE_DESCRIPTION, sourceDescription);
    }

    public void setSourceFile(String sourceFile) {
        fieldValues.put(SOURCE_FILE, sourceFile);
    }

    public void setSourceVersion(String sourceVersion) {
        fieldValues.put(SOURCE_VERSION, sourceVersion);
    }

    public String toString() {
        return getSourceName() + " - " + getSourceDescription();
    }

    public int compareTo(OntologySourceRefObject o) {
        if (o == null) {
            return -1;
        } else {
            return getSourceName().compareToIgnoreCase(o.getSourceName());
        }
    }

    public boolean equals(Object object){
        if (object == null)
            return false;
        if (object == this)
            return true;
        if (!(object instanceof OntologySourceRefObject))
            return false;

        OntologySourceRefObject sourceRefObject = (OntologySourceRefObject) object;
        boolean result = true;
        if (getSourceName()!=null && sourceRefObject.getSourceName()!=null)
            result = result && (getSourceName().equals(sourceRefObject.getSourceName()));

        if (getSourceDescription()!=null && sourceRefObject.getSourceDescription()!=null)
            result = result && getSourceDescription().equals(sourceRefObject.getSourceDescription());

        if (getSourceFile()!=null && sourceRefObject.getSourceFile()!=null)
            result = result &&  getSourceFile().equals(sourceRefObject.getSourceFile());

         if (getSourceVersion()!=null && sourceRefObject.getSourceFile()!=null)
             result = result && getSourceVersion().equals(sourceRefObject.getSourceVersion());

        return result;
    }

    public int hashCode(){
        int result = 0;

        if (getSourceName()!=null)
            result += getSourceName().hashCode();

        if (getSourceDescription()!=null)
            result += getSourceDescription().hashCode();

        if (getSourceFile()!=null)
            result += getSourceFile().hashCode();

        if (getSourceVersion()!=null)
            result += getSourceVersion().hashCode();

        return result;
    }

}
