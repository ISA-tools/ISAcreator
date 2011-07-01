/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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

import bioontology.bioportal.classBean.schema.*;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * BioPortalClassBeanResultHandler
 *
 * @author eamonnmaguire
 * @date Feb 18, 2010
 */


public class BioPortalClassBeanResultHandler {


    public SuccessDocument getDocument(String fileLocation) {
        SuccessDocument resultDocument = null;
        try {
            resultDocument = bioontology.bioportal.classBean.schema.SuccessDocument.Factory.parse(new File(fileLocation));
        } catch (org.apache.xmlbeans.XmlException e) {
            System.err.println("XML Exception encountered");
        } catch (java.io.IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        return resultDocument;
    }


    public OntologyTerm parseMetadataFile(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);
        ClassBeanDocument.ClassBean classBean = resultDocument.getSuccess().getData().getClassBean();

        OntologyTerm ontology = createOntologyFromClassBean(classBean);

        for (RelationsDocument.Relations relation : classBean.getRelationsArray()) {
            for (EntryDocument.Entry entry : relation.getEntryArray()) {
                if (entry.getStringArray().length > 0) {
                    String entryType = entry.getStringArray(0);
                    if (entry.getListArray().length > 0) {
                        for (ListDocument.List listItem : entry.getListArray()) {
                            for (String item : listItem.getStringArray()) {
                                ontology.addToComments(entryType, item);
                            }
                        }
                    }

                }
            }
        }

        return ontology;
    }

    public Map<String, OntologyTerm> parseRootConceptFile(String fileLocation, Set<String> termHasNoChildren) {
        SuccessDocument resultDocument = getDocument(fileLocation);

        Map<String, OntologyTerm> result = new HashMap<String, OntologyTerm>();

        if (resultDocument == null) {
            return result;
        }

        ClassBeanDocument.ClassBean upperLevelClass = resultDocument.getSuccess().getData().getClassBean();

        for (RelationsDocument.Relations relation : upperLevelClass.getRelationsArray()) {
            for (EntryDocument.Entry entry : relation.getEntryArray()) {
                if (entry.getStringArray().length > 0) {
                    String entryType = entry.getStringArray(0);

                    if (entryType.equalsIgnoreCase("subclass")) {
                        if (entry.getListArray().length > 0) {
                            for (ListDocument.List listItem : entry.getListArray()) {

                                for (ClassBeanDocument.ClassBean classBeanItem : listItem.getClassBeanArray()) {

                                    OntologyTerm ontology = createOntologyFromClassBean(classBeanItem);

                                    result.put(classBeanItem.getIdArray(0), ontology);
                                    for (RelationsDocument.Relations classBeanRelation : classBeanItem.getRelationsArray()) {
                                        for (EntryDocument.Entry relationEntry : classBeanRelation.getEntryArray()) {
                                            if (relationEntry.getStringArray(0).equals("ChildCount")) {


                                                // if there are no children, add the term accession to quicken up later queries
                                                if (relationEntry.getInt().intValue() == 0) {
                                                    termHasNoChildren.add(ontology.getOntologySourceAccession());
                                                    break;
                                                }


                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public Map<String, OntologyTerm> parseOntologyParentPathFile(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);

        Map<String, OntologyTerm> result = new ListOrderedMap<String, OntologyTerm>();

        if (resultDocument == null) {
            return result;
        }

        ClassBeanDocument.ClassBean upperLevelClass = resultDocument.getSuccess().getData().getClassBean();

        result = getOntologyTermParents(result, upperLevelClass);

        return result;
    }

    private Map<String, OntologyTerm> getOntologyTermParents(Map<String, OntologyTerm> terms, ClassBeanDocument.ClassBean upperLevelClass) {
        for (RelationsDocument.Relations relation : upperLevelClass.getRelationsArray()) {
            for (EntryDocument.Entry entry : relation.getEntryArray()) {
                if (entry.getStringArray().length > 0) {
                    String entryType = entry.getStringArray(0);
                    if (entryType.equalsIgnoreCase("subclass")) {
                        OntologyTerm ontology = createOntologyFromClassBean(upperLevelClass);

                        if (!ontology.getOntologyTermName().equals(OntologyTerm.THING)) {
                            terms.put(ontology.getOntologySourceAccession(), ontology);
                        }

                        if (entry.getListArray().length > 0) {
                            ListDocument.List[] items = entry.getListArray();

                            for (ListDocument.List listItem : items) {
                                for (ClassBeanDocument.ClassBean bean : listItem.getClassBeanArray()) {
                                    terms.putAll(getOntologyTermParents(terms, bean));
                                }
                            }
                        }
                    }
                }
            }
        }

        return terms;
    }


    public OntologyTerm createOntologyFromClassBean(ClassBeanDocument.ClassBean classToConvert) {
        OntologyTerm ontology = new OntologyTerm();

        if (classToConvert.getIdArray().length > 0) {
            ontology.setOntologySourceAccession(classToConvert.getIdArray(0));
        }
        if (classToConvert.getLabelArray().length > 0) {
            ontology.setOntologyTermName(classToConvert.getLabelArray(0));
        }
        if (classToConvert.getFullIdArray().length > 0) {
            ontology.setOntologyPurl(classToConvert.getFullIdArray(0));
        }

        return ontology;
    }

    private boolean proceedWithProcessing(SuccessDocument successDoc) {
        return successDoc != null &&
                successDoc.getSuccess() != null &&
                successDoc.getSuccess().getData() != null;

    }

}
