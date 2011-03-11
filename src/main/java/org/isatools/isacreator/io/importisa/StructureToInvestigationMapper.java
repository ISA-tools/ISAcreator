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

package org.isatools.isacreator.io.importisa;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.importisa.InvestigationFileProperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;

import java.util.*;

/**
 * Maps the DataStructure created by @see InvestigationImport to an Investigation object,
 * with the relevant Study data contained as well.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 */
public class StructureToInvestigationMapper {

    private List<OntologyObject> ontologyTermsDefined;
    private Set<String> messages;

    public StructureToInvestigationMapper() {
        ontologyTermsDefined = new ArrayList<OntologyObject>();
        messages = new HashSet<String>();
    }

    public Investigation createInvestigationFromDataStructure(
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


        return investigation;
    }

    private Investigation processInvestigation(OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>> investigationSections) {
        List<OntologySourceRefObject> ontologySources = new ArrayList<OntologySourceRefObject>();
        List<Contact> contacts = new ArrayList<Contact>();
        List<Publication> publications = new ArrayList<Publication>();

        Investigation investigation = null;

        OrderedMap<InvestigationFileSection, Set<String>> sectionFields = new ListOrderedMap<InvestigationFileSection, Set<String>>();

        for (InvestigationFileSection investigationSection : investigationSections.keySet()) {
            if (investigationSection == InvestigationFileSection.INVESTIGATION_SECTION) {
                investigation = processInvestigationSection(investigationSections.get(investigationSection));
            } else if (investigationSection == InvestigationFileSection.ONTOLOGY_SECTION) {
                ontologySources = processOntologySourceReferences(investigationSections.get(investigationSection));
            } else if (investigationSection == InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION) {

                Pair<Set<String>, List<Publication>> processedPublicationSection = processPublication(investigationSection,
                        investigationSections.get(investigationSection));
                sectionFields.put(investigationSection, processedPublicationSection.fst);
                publications = processedPublicationSection.snd;

            } else if (investigationSection == InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION) {
                contacts = processContacts(investigationSection, investigationSections.get(investigationSection));
            }
        }

        if (investigation != null) {
            investigation.setOntologiesUsed(ontologySources);
            investigation.addToPublications(publications);
            investigation.addToContacts(contacts);

            investigation.setReferenceObject(new DataEntryReferenceObject(sectionFields));
        }

        return investigation;
    }

    private Investigation processInvestigationSection(OrderedMap<String, List<String>> investigationSection) {
        Investigation investigation = new Investigation();

        investigation.addToFields(getRecord(investigationSection, 0));

        return investigation;
    }

    private Study processStudy(OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>> studySections) {
        Study study = null;

        List<Protocol> protocols = new ArrayList<Protocol>();
        List<Factor> factors = new ArrayList<Factor>();
        List<Contact> contacts = new ArrayList<Contact>();
        List<Publication> publications = new ArrayList<Publication>();
        List<Assay> assays = new ArrayList<Assay>();
        List<StudyDesign> studyDesigns = new ArrayList<StudyDesign>();


        // todo: here we should create a DataEntryReferenceObject for each Study. Somehow, this should be passed back to the
        // todo calling method so that the DataEntry interface for the Study or Investigation can use it to build the interface.

        OrderedMap<InvestigationFileSection, Set<String>> sectionFields = new ListOrderedMap<InvestigationFileSection, Set<String>>();

        for (InvestigationFileSection studySection : studySections.keySet()) {
            if (studySection == InvestigationFileSection.STUDY_SECTION) {
                study = processStudySection(studySections.get(studySection));
            } else if (studySection == InvestigationFileSection.STUDY_FACTORS) {
                factors = processFactors(studySections.get(studySection));
            } else if (studySection == InvestigationFileSection.STUDY_DESIGN_SECTION) {
                studyDesigns = processStudyDesigns(studySections.get(studySection));
            } else if (studySection == InvestigationFileSection.STUDY_ASSAYS) {
                assays = processAssay(studySections.get(studySection));
            } else if (studySection == InvestigationFileSection.STUDY_PUBLICATIONS) {

                Pair<Set<String>, List<Publication>> processedPublicationSection = processPublication(studySection, studySections.get(studySection));
                sectionFields.put(studySection, processedPublicationSection.fst);
                publications = processedPublicationSection.snd;

            } else if (studySection == InvestigationFileSection.STUDY_PROTOCOLS) {
                protocols = processProtocol(studySections.get(studySection));
            } else if (studySection == InvestigationFileSection.STUDY_CONTACTS) {
                contacts = processContacts(studySection, studySections.get(studySection));
            }

        }

        if (study != null) {
            study.addToAssays(assays);
            study.setProtocols(protocols);
            study.setContacts(contacts);
            study.setStudyDesigns(studyDesigns);
            study.setFactors(factors);
            study.setPublications(publications);

            study.setReferenceObject(new DataEntryReferenceObject(sectionFields));
        }

        return study;
    }

    private Study processStudySection(OrderedMap<String, List<String>> studySection) {
        Study study = new Study();

        study.addToFields(getRecord(studySection, 0));

        return study;
    }

    private List<OntologySourceRefObject> processOntologySourceReferences(OrderedMap<String, List<String>> ontologySection) {
        List<OntologySourceRefObject> ontologySources = new ArrayList<OntologySourceRefObject>();

        int recordCount = getLoopCount(ontologySection);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            OntologySourceRefObject ontologySource = new OntologySourceRefObject();
            Map<String, String> record = getRecord(ontologySection, recordIndex);
            if (!isNullRecord(record)) {
                ontologySource.addToFields(record);
                ontologySources.add(ontologySource);
            }
        }

        return ontologySources;
    }

    private Pair<Set<String>, List<Publication>> processPublication(InvestigationFileSection section, OrderedMap<String, List<String>> publicationSection) {
        List<Publication> publications = new ArrayList<Publication>();

        int recordCount = getLoopCount(publicationSection);

        Set<String> sectionFields = getFieldList(publicationSection);


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
                publications.add(p);
            }
        }

        return new Pair<Set<String>, List<Publication>>(sectionFields, publications);
    }

    private List<StudyDesign> processStudyDesigns(OrderedMap<String, List<String>> studyDesignSection) {
        List<StudyDesign> studyDesigns = new ArrayList<StudyDesign>();

        int recordCount = getLoopCount(studyDesignSection);


        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            StudyDesign design = new StudyDesign();
            Map<String, String> record = getRecord(studyDesignSection, recordIndex);

            if (!isNullRecord(record)) {
                design.addToFields(record);
                studyDesigns.add(design);
            }
        }

        return studyDesigns;
    }

    private List<Contact> processContacts(InvestigationFileSection section, OrderedMap<String, List<String>> contactSection) {
        List<Contact> contacts = new ArrayList<Contact>();

        int recordCount = getLoopCount(contactSection);

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
                contacts.add(c);
            }
        }

        return contacts;
    }

    private List<Factor> processFactors(OrderedMap<String, List<String>> factorSection) {
        List<Factor> factors = new ArrayList<Factor>();

        int recordCount = getLoopCount(factorSection);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Factor f = new Factor();
            Map<String, String> record = getRecord(factorSection, recordIndex);

            if (!isNullRecord(record)) {
                f.addToFields(record);
                factors.add(f);
            }
        }

        return factors;
    }

    private List<Protocol> processProtocol(OrderedMap<String, List<String>> protocolSection) {
        List<Protocol> protocols = new ArrayList<Protocol>();

        int recordCount = getLoopCount(protocolSection);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Protocol p = new Protocol();
            Map<String, String> record = getRecord(protocolSection, recordIndex);

            if (!isNullRecord(record)) {
                p.addToFields(record);
                protocols.add(p);
            }
        }

        return protocols;
    }

    private List<Assay> processAssay(OrderedMap<String, List<String>> assaySection) {

        List<Assay> assays = new ArrayList<Assay>();

        int recordCount = getLoopCount(assaySection);

        for (int recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            Assay a = new Assay();
            Map<String, String> record = getRecord(assaySection, recordIndex);

            if (!isNullRecord(record)) {
                a.addToFields(record);
                assays.add(a);
            }
        }

        return assays;
    }


    private int getLoopCount(OrderedMap<String, List<String>> assayStructure) {
        return assayStructure.get(assayStructure.firstKey()).size();
    }

    private Map<String, String> getRecord(Map<String, List<String>> records, int recordIndex) {
        Map<String, String> fields = new HashMap<String, String>();

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
}
