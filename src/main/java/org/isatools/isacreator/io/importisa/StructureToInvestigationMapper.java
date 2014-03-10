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

package org.isatools.isacreator.io.importisa;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.io.importisa.errorhandling.exceptions.MalformedOntologyTermException;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.utils.GeneralUtils;
import uk.ac.ebi.utils.collections.Pair;

import java.util.*;

/**
 * Maps the DataStructure created by @see InvestigationImport to an Investigation object,
 * with the relevant Study data contained as well.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 */
public class StructureToInvestigationMapper {

    private static Logger log = Logger.getLogger(StructureToInvestigationMapper.class.getName());


    private List<OntologyTerm> ontologyTermsDefined;
    private List<ErrorMessage> messages;


    public StructureToInvestigationMapper() {
        ontologyTermsDefined = new ArrayList<OntologyTerm>();
        messages = new ArrayList<ErrorMessage>();
    }


    public Pair<Boolean, Investigation> createInvestigationFromDataStructure(
            OrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>> investigationStructure) {

        Investigation investigation = null;

        List<Study> studies = new ArrayList<Study>();

        for (String majorSection : investigationStructure.keySet()) {
            if (majorSection.contains("Investigation")) {

                investigation = processInvestigation(investigationStructure.get(majorSection));
                System.out.println(investigation.getInvestigationTitle());
            }

            if (majorSection.contains("Study")) {

                Study newStudy = processStudy(investigationStructure.get(majorSection));
                studies.add(newStudy);
            }
        }

        if (investigation != null) {
            for (Study study : studies) {
                investigation.addStudy(study);
            }
        }

        return new Pair<Boolean, Investigation>(validateInvestigationFile(investigation), investigation);
    }

    private Investigation processInvestigation(OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>> investigationSections) {
        Set<OntologySourceRefObject> ontologySources = new HashSet<OntologySourceRefObject>();
        List<Contact> contacts = new ArrayList<Contact>();
        List<Publication> publications = new ArrayList<Publication>();

        Investigation tmpInvestigation = null;

        OrderedMap<InvestigationFileSection, Set<String>> sectionFields = new ListOrderedMap<InvestigationFileSection, Set<String>>();

        for (InvestigationFileSection investigationSection : investigationSections.keySet()) {
            if (investigationSection == InvestigationFileSection.INVESTIGATION_SECTION) {

                Pair<Set<String>, Investigation> processedInvestigationSection = processInvestigationSection(investigationSections.get(investigationSection));
                sectionFields.put(investigationSection, processedInvestigationSection.fst);
                tmpInvestigation = processedInvestigationSection.snd;

            } else if (investigationSection == InvestigationFileSection.ONTOLOGY_SECTION) {

                Pair<Set<String>, List<OntologySourceRefObject>> processedFactorsSection = processOntologySourceReferences(investigationSections.get(investigationSection));
                sectionFields.put(investigationSection, processedFactorsSection.fst);
                ontologySources = new HashSet<OntologySourceRefObject>(processedFactorsSection.snd);

            } else if (investigationSection == InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION) {

                Pair<Set<String>, List<Publication>> processedPublicationSection = processPublication(investigationSection,
                        investigationSections.get(investigationSection));
                sectionFields.put(investigationSection, processedPublicationSection.fst);
                publications = processedPublicationSection.snd;

            } else if (investigationSection == InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION) {
                Pair<Set<String>, List<Contact>> processedContactSection = processContacts(investigationSection, investigationSections.get(investigationSection));
                sectionFields.put(investigationSection, processedContactSection.fst);
                contacts = processedContactSection.snd;
            }

        }

        if (tmpInvestigation != null) {
            OntologyManager.setOntologySources(ontologySources);
            tmpInvestigation.addToPublications(publications);
            tmpInvestigation.addToContacts(contacts);

            tmpInvestigation.addToReferenceObject(sectionFields);
        }

        return tmpInvestigation;
    }

    private Pair<Set<String>, Investigation> processInvestigationSection(OrderedMap<String, List<String>> investigationSection) {
        Investigation investigation = new Investigation();

        Set<String> sectionFields = getFieldList(investigationSection);

        Map<String, String> record = getRecord(investigationSection, 0);

        investigation.addToFields(record);
        if (StringUtils.trimToNull(investigation.getInvestigationId()) == null) {
            investigation.setDefaultInvestigationId();
        }

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        for (int hashCode : ontologyFields.keySet()) {
            Map<String, String> ontologyField = ontologyFields.get(hashCode);

            try {
                String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                investigation.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);
            } catch (MalformedOntologyTermException e) {
                messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
            }
        }

        return new Pair<Set<String>, Investigation>(sectionFields, investigation);
    }

    private Study processStudy(OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>> studySections) {
        Study study = null;

        List<Protocol> protocols = new ArrayList<Protocol>();
        List<Factor> factors = new ArrayList<Factor>();
        List<Contact> contacts = new ArrayList<Contact>();
        List<Publication> publications = new ArrayList<Publication>();
        List<Assay> assays = new ArrayList<Assay>();
        List<StudyDesign> studyDesigns = new ArrayList<StudyDesign>();

        OrderedMap<InvestigationFileSection, Set<String>> sectionFields = new ListOrderedMap<InvestigationFileSection, Set<String>>();

        for (InvestigationFileSection studySection : studySections.keySet()) {
            if (studySection == InvestigationFileSection.STUDY_SECTION) {

                Pair<Set<String>, Study> processedStudySection = processStudySection(studySections.get(studySection));
                study = processedStudySection.snd;
                sectionFields.put(studySection, processedStudySection.fst);

            } else if (studySection == InvestigationFileSection.STUDY_FACTORS) {

                Pair<Set<String>, List<Factor>> processedFactorsSection = processFactors(studySections.get(studySection));
                sectionFields.put(studySection, processedFactorsSection.fst);
                factors = processedFactorsSection.snd;

            } else if (studySection == InvestigationFileSection.STUDY_DESIGN_SECTION) {

                Pair<Set<String>, List<StudyDesign>> processedStudyDesignSection = processStudyDesigns(studySections.get(studySection));
                sectionFields.put(studySection, processedStudyDesignSection.fst);

                studyDesigns = processedStudyDesignSection.snd;

            } else if (studySection == InvestigationFileSection.STUDY_ASSAYS) {

                Pair<Set<String>, List<Assay>> processedAssaySection = processAssay(studySections.get(studySection));
                sectionFields.put(studySection, processedAssaySection.fst);
                assays = processedAssaySection.snd;

            } else if (studySection == InvestigationFileSection.STUDY_PUBLICATIONS) {

                Pair<Set<String>, List<Publication>> processedPublicationSection = processPublication(studySection, studySections.get(studySection));
                sectionFields.put(studySection, processedPublicationSection.fst);
                publications = processedPublicationSection.snd;

            } else if (studySection == InvestigationFileSection.STUDY_PROTOCOLS) {

                Pair<Set<String>, List<Protocol>> processedProtocolSection = processProtocol(studySections.get(studySection));
                sectionFields.put(studySection, processedProtocolSection.fst);
                protocols = processedProtocolSection.snd;

            } else if (studySection == InvestigationFileSection.STUDY_CONTACTS) {

                Pair<Set<String>, List<Contact>> processedContactSection = processContacts(studySection, studySections.get(studySection));
                sectionFields.put(studySection, processedContactSection.fst);
                contacts = processedContactSection.snd;
            }

        }

        if (study != null) {
            study.addToAssays(assays);
            study.setProtocols(protocols);
            study.setContacts(contacts);

            study.setStudyDesigns(studyDesigns);
            study.setFactors(factors);
            study.setPublications(publications);

            // we want to add new fields, but want to keep the general working of the configuration. So we do just that.
            // new fields (unknown) will be treated as Strings, other known values will acquire the properties
            // specified of them in the configuration XML.
            study.addToReferenceObject(sectionFields);
        }

        return study;
    }

    private Pair<Set<String>, Study> processStudySection(OrderedMap<String, List<String>> studySection) {
        Study study = new Study();

        Set<String> sectionFields = getFieldList(studySection);

        Map<String, String> record = getRecord(studySection, 0);

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        study.addToFields(record);

        for (int hashCode : ontologyFields.keySet()) {
            Map<String, String> ontologyField = ontologyFields.get(hashCode);

            try {
                String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                study.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);
            } catch (MalformedOntologyTermException e) {
                messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
            }
        }

        return new Pair<Set<String>, Study>(sectionFields, study);
    }

    private Pair<Set<String>, List<OntologySourceRefObject>> processOntologySourceReferences(OrderedMap<String, List<String>> ontologySection) {
        List<OntologySourceRefObject> ontologySources = new ArrayList<OntologySourceRefObject>();

        int recordCount = getLoopCount(ontologySection);

        Set<String> sectionFields = getFieldList(ontologySection);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            OntologySourceRefObject ontologySource = new OntologySourceRefObject();
            Map<String, String> record = getRecord(ontologySection, recordIndex);
            if (!isNullRecord(record)) {
                ontologySource.addToFields(record);
                ontologySource.completeFields();
                ontologySources.add(ontologySource);
            }
        }

        return new Pair<Set<String>, List<OntologySourceRefObject>>(sectionFields, ontologySources);
    }

    private Pair<Set<String>, List<Publication>> processPublication(InvestigationFileSection section, OrderedMap<String, List<String>> publicationSection) {
        List<Publication> publications = new ArrayList<Publication>();

        int recordCount = getLoopCount(publicationSection);

        Set<String> sectionFields = getFieldList(publicationSection);

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Publication p;
            if (section == InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION) {
                p = new InvestigationPublication();
            } else {
                p = new StudyPublication();
            }

            Map<String, String> record = getRecord(publicationSection, recordIndex);

            if (!isNullRecord(record)) {
                p.addToFields(record);

                for (int hashCode : ontologyFields.keySet()) {
                    Map<String, String> ontologyField = ontologyFields.get(hashCode);

                    try {
                        String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                        p.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);
                    } catch (MalformedOntologyTermException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    }
                }


                publications.add(p);

                // do ontology term grouping here...
            }
        }

        return new Pair<Set<String>, List<Publication>>(sectionFields, publications);
    }

    private Pair<Set<String>, List<StudyDesign>> processStudyDesigns(OrderedMap<String, List<String>> studyDesignSection) {
        List<StudyDesign> studyDesigns = new ArrayList<StudyDesign>();

        int recordCount = getLoopCount(studyDesignSection);

        Set<String> sectionFields = getFieldList(studyDesignSection);

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            StudyDesign design = new StudyDesign();
            Map<String, String> record = getRecord(studyDesignSection, recordIndex);

            if (!isNullRecord(record)) {
                design.addToFields(record);

                for (int hashCode : ontologyFields.keySet()) {
                    Map<String, String> ontologyField = ontologyFields.get(hashCode);

                    try {
                        String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                        //TODO add URI to accession
                        design.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);
                    } catch (MalformedOntologyTermException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    }
                }

                studyDesigns.add(design);
            }
        }

        return new Pair<Set<String>, List<StudyDesign>>(sectionFields, studyDesigns);
    }

    private Pair<Set<String>, List<Contact>> processContacts(InvestigationFileSection section, OrderedMap<String, List<String>> contactSection) {
        List<Contact> contacts = new ArrayList<Contact>();

        int recordCount = getLoopCount(contactSection);

        Set<String> sectionFields = getFieldList(contactSection);

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Contact c;
            if (section == InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION) {
                c = new InvestigationContact();
            } else {
                c = new StudyContact();
            }

            Map<String, String> record = getRecord(contactSection, recordIndex);

            if (!isNullRecord(record)) {
                c.addToFields(record);

                for (int hashCode : ontologyFields.keySet()) {
                    Map<String, String> ontologyField = ontologyFields.get(hashCode);

                    try {
                        String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                        c.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);
                    } catch (MalformedOntologyTermException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    }
                }

                contacts.add(c);
            }
        }

        return new Pair<Set<String>, List<Contact>>(sectionFields, contacts);
    }

    private Pair<Set<String>, List<Factor>> processFactors(OrderedMap<String, List<String>> factorSection) {
        List<Factor> factors = new ArrayList<Factor>();

        int recordCount = getLoopCount(factorSection);

        Set<String> sectionFields = getFieldList(factorSection);

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Factor f = new Factor();
            Map<String, String> record = getRecord(factorSection, recordIndex);

            if (!isNullRecord(record)) {
                f.addToFields(record);

                for (int hashCode : ontologyFields.keySet()) {
                    Map<String, String> ontologyField = ontologyFields.get(hashCode);

                    try {
                        String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                        if (!value.contains(":")){
                            f.getFieldValues().put(ontologyField.get(IOUtils.ACCESSION), "");
                            f.getFieldValues().put(ontologyField.get(IOUtils.SOURCE_REF), "");
                        }
                        f.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);

                    } catch (MalformedOntologyTermException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    }
                }

                factors.add(f);
            }
        }

        return new Pair<Set<String>, List<Factor>>(sectionFields, factors);
    }

    private Pair<Set<String>, List<Protocol>> processProtocol(OrderedMap<String, List<String>> protocolSection) {
        List<Protocol> protocols = new ArrayList<Protocol>();

        int recordCount = getLoopCount(protocolSection);

        Set<String> sectionFields = getFieldList(protocolSection);

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Protocol p = new Protocol();
            Map<String, String> record = getRecord(protocolSection, recordIndex);

            if (!isNullRecord(record)) {
                p.addToFields(record);
                for (int hashCode : ontologyFields.keySet()) {
                    Map<String, String> ontologyField = ontologyFields.get(hashCode);

                    try {
                        String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                        p.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);
                    } catch (MalformedOntologyTermException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    }
                }
                protocols.add(p);
            }
        }

        return new Pair<Set<String>, List<Protocol>>(sectionFields, protocols);
    }

    private Pair<Set<String>, List<Assay>> processAssay(OrderedMap<String, List<String>> assaySection) {

        List<Assay> assays = new ArrayList<Assay>();

        int recordCount = getLoopCount(assaySection);

        Set<String> sectionFields = getFieldList(assaySection);

        Map<Integer, Map<String, String>> ontologyFields = IOUtils.getOntologyTerms(sectionFields);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Assay a = new Assay();
            Map<String, String> record = getRecord(assaySection, recordIndex);

            if (!isNullRecord(record)) {
                a.addToFields(record);

                for (int hashCode : ontologyFields.keySet()) {
                    Map<String, String> ontologyField = ontologyFields.get(hashCode);
                    try {
                        String value = groupElements(ontologyField.get(IOUtils.TERM), record.get(ontologyField.get(IOUtils.TERM)), record.get(ontologyField.get(IOUtils.ACCESSION)), record.get(ontologyField.get(IOUtils.SOURCE_REF)));
                        a.getFieldValues().put(ontologyField.get(IOUtils.TERM), value);
                    } catch (MalformedOntologyTermException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    }
                }

                assays.add(a);
            }
        }

        return new Pair<Set<String>, List<Assay>>(sectionFields, assays);
    }


    private int getLoopCount(OrderedMap<String, List<String>> assayStructure) {
        return assayStructure.get(assayStructure.firstKey()).size();
    }

    private Map<String, String> getRecord(Map<String, List<String>> records, int recordIndex) {
        OrderedMap<String, String> fields = new ListOrderedMap<String, String>();

        for (String fieldName : records.keySet()) {
            String value = "";
            if (recordIndex < records.get(fieldName).size()) {
                value = records.get(fieldName).get(recordIndex);
            }
            fields.put(fieldName, value);

        }

        return fields;
    }

    private Set<String> getFieldList(Map<String, List<String>> records) {
        // we also need to add missing fields here as well.
        return records.keySet();
    }

    private boolean isNullRecord(Map<String, String> record) {
        boolean allNulls = true;
        for (String key : record.keySet()) {
            if (record.get(key) != null && !record.get(key).trim().equals("")) {
                allNulls = false;
            } else {
                // just in case not everything is null, we should assign the value to an empty string.
                record.put(key, "");
            }
        }
        return allNulls;
    }

    /**
     * @param fieldBeingCombined
     * @param term
     * @param accession
     * @param sourceRef
     * @return
     * @throws MalformedOntologyTermException
     */
    private String groupElements(String fieldBeingCombined, String term,
                                 String accession, String sourceRef)
            throws MalformedOntologyTermException {
        String toReturn = "";

        term = term == null ? "" : term;
        accession = accession == null ? "" : accession;
        sourceRef = sourceRef == null ? "" : sourceRef;
        // if all elements contain ; then we are dealing with multiple ontology terms
        if (term.contains(";") && sourceRef.contains(";")) {
            String[] splitTerms = term.split(";");

            // small hack to ensure that we have equal numbers of terms!
            if (sourceRef.endsWith(";")) {
                sourceRef += " ";
            }

            String[] splitSourceRefs = sourceRef.split(";");

            String[] splitAccession = null;

            if (accession.split(";").length > 0) {
                splitAccession = accession.split(";");
            }

            if ((splitTerms.length == splitSourceRefs.length)) {
                // we know that everything is properly formed between the split terms and the source refs

                // we know that everything is properly formed between the source refs, accessions, and the terms.
                for (int i = 0; i < splitTerms.length; i++) {
                    if (splitSourceRefs[i].equals("") ||
                            splitSourceRefs[i].equals(" ")) {
                        toReturn += splitTerms[i];
                    } else {
                        toReturn += (splitSourceRefs[i] + ":" + splitTerms[i]);

                        String accToAdd = "";

                        if ((splitAccession != null) &&
                                (i < splitAccession.length)) {
                            accToAdd = splitAccession[i];
                        }

                        ontologyTermsDefined.add(new OntologyTerm(
                                splitTerms[i], accToAdd, null, getOntologySource(splitSourceRefs[i])));

                    }

                    if (i != (splitTerms.length - 1)) {
                        toReturn += ";";
                    }
                }
            } else {
                throw new MalformedOntologyTermException("Problem with Ontology field " + fieldBeingCombined +
                        ". There should be an equal number of values in the term source and term accession number fields separated by ;");
            }
        } else {
            // can assume that there is only one value in each cell. so group them
            if (sourceRef.equals("")) {
                toReturn = term;
            } else {
                toReturn = sourceRef + ":" + term;


                if (!term.trim().equals("") &&
                        !sourceRef.trim().equals("")) {

                    if (accession.contains("http://"))
                        ontologyTermsDefined.add(new OntologyTerm(
                                term, accession, accession, getOntologySource(sourceRef)));
                    else {
                        OntologyTerm ot = new OntologyTerm(term, accession, null, getOntologySource(sourceRef));
                        ontologyTermsDefined.add(ot);
                        if (!(ISAcreatorProperties.getOntologyTermURIProperty() && ot.getOntologyTermURI()!=null && !ot.getOntologyTermURI().equals("")))
                            toReturn = term;
                        OntologyManager.addToOntologyTerms(ot);
                    }
                }

            }
        }

        return toReturn;
    }

    /**
     * It returns an OntologySourceRefObject given an ontology abbreviation
     *
     * @param source
     * @return
     */
    private OntologySourceRefObject getOntologySource(String source) {
        return OntologyManager.getOntologySourceReferenceObjectByAbbreviation(source);
    }

    /**
     * Checks for duplicate assay names across all studies.
     *
     * @param investigation
     * @return
     */
    private boolean validateInvestigationFile(Investigation investigation) {

        Set<String> assayNames = new HashSet<String>();
        Set<String> studyNames = new HashSet<String>();

        for (Study study : investigation.getStudies().values()) {
            if (studyNames.contains(study.getStudyId())) {
                String message = "Duplicate study names found in investigation! Study with with ID : " + study.getStudyId() + " already exists!";
                messages.add(new ErrorMessage(ErrorLevel.ERROR, message));
                log.info(message);
                return false;
            } else {
                studyNames.add(study.getStudyId());
            }

            for (Assay assay : study.getAssays().values()) {
                if (assayNames.contains(assay.getAssayReference())) {
                    String message = "Duplicate assay found in investigation! Assay with with name : " + assay.getAssayReference() + " already exists!";
                    messages.add(new ErrorMessage(ErrorLevel.ERROR, message));
                    log.info(message);
                    return false;
                } else {
                    assayNames.add(assay.getAssayReference());
                }
            }
        }

        // check that all ontologies have been defined

        // build up set of ontology sources that have been defined
        Set<String> definedOntologySources = new HashSet<String>();

        for (OntologySourceRefObject osro : OntologyManager.getOntologySources()) {
            definedOntologySources.add(osro.getSourceName());
        }

        // now search through added ontology objects to determine which ontologies haven't been defined
        Set<String> missingOntologyObjects = new HashSet<String>();

        for (OntologyTerm oo : ontologyTermsDefined) {
            if (!definedOntologySources.contains(oo.getOntologySource()) &&
                    !oo.getOntologySource().equals("")) {
                System.out.println(oo.getShortForm());
                if (!GeneralUtils.isValueURL(oo.getShortForm())) {
                    missingOntologyObjects.add(oo.getOntologySource());
                }
            }
        }

        if (missingOntologyObjects.size() > 0) {
            String missing = "";

            for (String m : missingOntologyObjects) {
                missing += (m + " ");
            }

            messages.add(new ErrorMessage(ErrorLevel.ERROR, "Some ontology sources are not defined in the ONTOLOGY SOURCE REFERENCE section -> " + missing));
            log.info("Some ontology sources are not defined in the ONTOLOGY SOURCE REFERENCE section -> " + missing);
            return false;
        }

        return true;
    }

    public List<ErrorMessage> getMessages() {
        return messages;
    }

    public List<OntologyTerm> getOntologyTermsDefined() {
        return ontologyTermsDefined;
    }
}
