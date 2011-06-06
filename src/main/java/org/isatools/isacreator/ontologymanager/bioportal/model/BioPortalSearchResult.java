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

package org.isatools.isacreator.ontologymanager.bioportal.model;

import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ISAcreator has been designed to structure experimental metadata in ISA-TAB format.
 * More information at: http://isatab.sourceforge.net/isacreator.html
 * <p/>
 * Work carried out by:
 * - Eamonn Maguire (software engineer)
 * - Philippe Rocca-Serra (user requirements and wizard tool specifications)
 * - Susanna-Assunta Sansone (coordination and funds)
 * - Team page: http://www.ebi.ac.uk/net-project/
 * <p/>
 * License
 * (Attribution-No Derivative Works 3.0 Unported)
 * You are free to Share — to copy, distribute and transmit the work under the following conditions:
 * <p/>
 * - Attribution. You must attribute the work in the manner specified by the author or licensor
 * (but not in any way that suggests that they endorse you or your use of the work).
 * - No Derivative Works. You may not alter, transform, or build upon this work.
 * <p/>
 * http://creativecommons.org/licenses/by-nd/3.0/
 * http://creativecommons.org/licenses/by-nd/3.0/legalcode
 * <p/>
 * Sponsors
 * This work has been funded mainly by the EU Carcinogenomics (http://www.carcinogenomics.eu) and in part by the
 * EU NuGO (http://www.nugo.org/everyone) projects.
 */


public class BioPortalSearchResult {
    private Map<OntologySourceRefObject, List<OntologyTerm>> result;

    private Map<String, OntologySourceRefObject> sourceReferences;

    public BioPortalSearchResult() {
        result = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();
        sourceReferences = new HashMap<String, OntologySourceRefObject>();
    }

    public Map<OntologySourceRefObject, List<OntologyTerm>> getResult() {
        return result == null ? new HashMap<OntologySourceRefObject, List<OntologyTerm>>() : result;
    }

    public void addToResult(OntologySourceRefObject referenceObject, OntologyTerm ontology) {

        if (!sourceReferences.containsKey(referenceObject.getSourceName())) {
            sourceReferences.put(referenceObject.getSourceName(), referenceObject);
            result.put(referenceObject, new ArrayList<OntologyTerm>());
        }

        if (ontology != null) {
            if (ontology.getOntologyTermName() != null) {
                result.get(sourceReferences.get(referenceObject.getSourceName())).add(ontology);
            }
        }

    }
}
