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
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface OntologyService {


    public Map<String, String> getOntologyNames();

    public Map<String, String> getTermMetadata(String termId, String ontology);

    public Map<OntologySourceRefObject, List<OntologyTerm>> getTermsByPartialNameFromSource(String term, String source, boolean reverseOrder);

    public Map<OntologySourceRefObject, List<OntologyTerm>> getTermsByPartialNameFromSource(String term,
                                                               List<RecommendedOntology> recommendedOntology);

    public Map<String, String> getOntologyVersions();

    /**
     * Return the root concepts for an ontology
     *
     * @param ontology - ontology to search in
     * @return Map<String,String> with mappings from term accession to the term label
     */
    public Map<String, OntologyTerm> getOntologyRoots(String ontology);

    /**
     * Return the parents of a term
     *
     * @param termAccession - term to search under
     * @param ontology      - ontology term is located in
     * @return Map<String,String> with mappings from term accession to the term label
     */
    public Map<String, OntologyTerm> getTermParent(String termAccession, String ontology);

    /**
     * Return the children of a term
     *
     * @param termAccession - term to search under
     * @param ontology      - ontology term is located in
     * @return Map<String,String> with mappings from term accession to the term label
     */
    public Map<String, OntologyTerm> getTermChildren(String termAccession, String ontology);

    /**
     * Method will search the Ontology space to determine the parents of a given term. This can then be used in searches to make
     * location of a term within an Ontology much quicker.
     *
     * @param termAccession - the accession of the term being searched on e.g. ENVO:00003073
     * @param ontology      - Abbreviation for the Ontology e.g. ENVO for OLS or 40832 for OBI.
     * @return Map<String, String> representing the parents of the Term
     */
    public Map<String, OntologyTerm> getAllTermParents(String termAccession, String ontology);


    //public String getOntologyURL();

    public Collection<Ontology> getAllOntologies();

}
