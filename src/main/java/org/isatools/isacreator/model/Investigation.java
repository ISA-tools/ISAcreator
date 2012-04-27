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

import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Investigation object which will contain all Study and Assay Definitions
 *
 * @author Eamonn Maguire
 */
public class Investigation extends ISASection {

    public static final String INVESTIGATION_ID_KEY = "Investigation Identifier";
    public static final String INVESTIGATION_TITLE_KEY = "Investigation Title";
    public static final String INVESTIGATION_DESCRIPTION_KEY = "Investigation Description";
    public static final String INVESTIGATION_SUBMISSION_DATE_KEY = "Investigation Submission Date";
    public static final String INVESTIGATION_PUBLIC_RELEASE_KEY = "Investigation Public Release Date";
    public static final String CONFIGURATION_CREATED_WITH = "Comment [Created With Configuration]";
    public static final String CONFIGURATION_LAST_OPENED_WITH = "Comment [Last Opened With Configuration]";

    private InvestigationDataEntry userInterface;

    private List<Publication> publications;
    private List<Contact> contacts;
    private Map<String, Study> studies;
    private Map<String, String> assays;

    private String reference;


    public Investigation() {
        super();
        initialise();
    }

    /**
     * Investigation Object contains one to many Study Objects and is the top most element
     * in the ISA structure.
     *
     * @param investigationTitle       - Title given to the investigation
     * @param investigationDescription - Description of the Investigation including aims, hypotheses, and so forth.
     */
    public Investigation(String investigationTitle, String investigationDescription) {
        this("", investigationTitle, investigationDescription, "", "");
    }

    /**
     * Investigation Object contains one to many Study Objects and is the top most element
     * in the ISA structure.
     *
     * @param investigationId          - ID to be given to the investigation
     * @param investigationTitle       - Title given to the investigation
     * @param investigationDescription - Description of the Investigation including aims, hypotheses, and so forth.
     * @param submissionDate           - Date the Investigation was submitted.
     * @param publicReleaseDate        - Date the Investigation should be available to the public.
     */
    public Investigation(String investigationId, String investigationTitle, String investigationDescription,
                         String submissionDate, String publicReleaseDate) {

        super();
        setInvestigationId(investigationId);
        setInvestigationTitle(investigationTitle.equals("") ? "Investigation" : investigationTitle);
        setInvestigationDescription(investigationDescription);
        setSubmissionDate(submissionDate);
        setPublicReleaseDate(publicReleaseDate);


        initialise();
    }

    private void initialise() {
        studies = new ListOrderedMap<String, Study>();
        assays = new HashMap<String, String>();
        contacts = new ArrayList<Contact>();
        publications = new ArrayList<Publication>();
        OntologyManager.newInvestigation(getInvestigationId().equals("") ? "investigation-" + System.currentTimeMillis() : getInvestigationId());
    }

    @Override
    public void setReferenceObjectForSection() {
        setReferenceObject(ApplicationManager.getInvestigationDataEntryReferenceObject());
    }

    public boolean addStudy(Study study) {

        if (studies.get(study.getStudyId()) == null) {
            studies.put(study.getStudyId(), study);
            return true;
        }
        return false;
    }

    /**
     * Add an Assay to the Investigation. This is called by the Study class and is only
     * used provide a reference to the investigation so as to locate the study an assay
     * is associated with given a simple lookup.
     *
     * @param assayRef - Reference to be given to the assay
     * @param studyId  - Study the assay is associated with.
     * @return true if added, false otherwise.
     */
    public boolean addToAssays(String assayRef, String studyId) {
        if (!assays.containsKey(assayRef)) {
            assays.put(assayRef, studyId);
            return true;
        }
        return false;
    }

    /**
     * Add a Publication to the Investigation.
     *
     * @param publication - Publication to be added to the Investigation.
     * @return true if added, false otherwise.
     */
    public boolean addPublication(Publication publication) {

        if (!checkPublicationExists(publication.getPublicationTitle())) {
            publication.setReferenceObject(getReferenceObject(), InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION);
            publications.add(publication);
            return true;
        }
        return false;
    }

    private boolean checkPublicationExists(String publicationTitle) {
        if (publicationTitle != null) {
            for (Publication p : publications) {
                if (p.getPublicationTitle().equals(publicationTitle)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public void addToPublications(List<Publication> publicationsToAdd) {
        for (Publication p : publicationsToAdd) {
            addPublication(p);
        }
    }

    public void addToContacts(List<Contact> contactsToAdd) {
        for (Contact c : contactsToAdd) {
            addContact(c);
        }
    }

    public String getSubmissionDate() {
        return getValue(INVESTIGATION_SUBMISSION_DATE_KEY);
    }

    public String getInvestigationDescription() {
        return getValue(INVESTIGATION_DESCRIPTION_KEY);
    }

    public String getInvestigationId() {
        return getValue(INVESTIGATION_ID_KEY);
    }

    public String getPublicReleaseDate() {
        return getValue(INVESTIGATION_PUBLIC_RELEASE_KEY);
    }

    public String getInvestigationTitle() {
        return getValue(INVESTIGATION_TITLE_KEY);
    }

    public String getReference() {
        return reference;
    }

    public Map<String, String> getAssays() {
        return assays;
    }

    public Map<String, Study> getStudies() {
        return studies;
    }

    public InvestigationDataEntry getUserInterface() {
        return userInterface;
    }

    public void setSubmissionDate(String submissionDate) {
        fieldValues.put(INVESTIGATION_SUBMISSION_DATE_KEY, submissionDate);
    }

    public void setInvestigationDescription(String investigationDescription) {
        fieldValues.put(INVESTIGATION_DESCRIPTION_KEY, investigationDescription);
    }

    public void setInvestigationId(String investigationIdentifier) {
        fieldValues.put(INVESTIGATION_ID_KEY, investigationIdentifier);
    }

    public void setPublicReleaseDate(String publicReleaseDate) {
        fieldValues.put(INVESTIGATION_PUBLIC_RELEASE_KEY, publicReleaseDate);
    }

    public void setInvestigationTitle(String investigationTitle) {
        fieldValues.put(INVESTIGATION_TITLE_KEY, investigationTitle);
    }

    public String getLastConfigurationUsed() {
        return getValue(CONFIGURATION_LAST_OPENED_WITH);
    }

    public void setLastConfigurationUsed(String configurationName) {
        if (configurationName.contains(File.separator)) {
            configurationName = configurationName.substring(configurationName.lastIndexOf(File.separator) + 1);
        }

        fieldValues.put(CONFIGURATION_LAST_OPENED_WITH, configurationName);
    }

    public String getConfigurationCreateWith() {
        return getValue(CONFIGURATION_CREATED_WITH);
    }

    public void setConfigurationCreateWith(String configurationName) {
        fieldValues.put(CONFIGURATION_CREATED_WITH, configurationName);
    }


    public List<Contact> getContacts() {
        return contacts;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setFileReference(String reference) {
        String directory = reference.substring(0,
                reference.lastIndexOf(File.separator));
        String investigationFileName = reference.substring(reference.lastIndexOf(
                File.separator) + 1);

        if (!investigationFileName.startsWith("i_")) {
            investigationFileName = "i_" + investigationFileName;
        }

        if (!investigationFileName.endsWith(".txt")) {
            investigationFileName += ".txt";
        }

        this.reference = directory + File.separator + investigationFileName;
    }

    public void setUserInterface(InvestigationDataEntry ui) {
        this.userInterface = ui;
    }

    public String toString() {
        return getInvestigationTitle();
    }

    /**
     * Remove a contact from the list in the study
     *
     * @param forename     - forename of contact to remove
     * @param surname      - surname of contact to remove
     * @param emailAddress - email address of contact to remove
     */
    public void removeContact(String forename, String surname, String emailAddress) {
        Contact toRemove = null;

        for (Contact contact : contacts) {
            if (contact.getFirstName().equals(forename) && contact.getLastName().equals(surname) && emailAddress.equals(emailAddress)) {
                toRemove = contact;

                break;
            }
        }

        if (toRemove != null) {
            contacts.remove(toRemove);
        }
    }

    /**
     * Add a Contact to this Investigation Object.
     *
     * @param contact - Contact to be added.
     * @return true if added, false otherwise.
     */
    public boolean addContact(Contact contact) {
        if (!checkContactExists(contact.getFirstName(), contact.getLastName(), contact.getEmail())) {
            contact.setReferenceObject(getReferenceObject(), InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION);
            contacts.add(contact);

            return true;
        }

        return false;
    }

    private boolean checkContactExists(String forename, String surname,
                                       String email) {
        for (Contact c : contacts) {
            if (c.getFirstName() != null && c.getLastName() != null) {
                if (c.getFirstName().equals(forename) &&
                        c.getLastName().equals(surname) &&
                        c.getEmail().equals(email)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Remove a Publication from this Investigation given a pubmed id or a Publication Title
     *
     * @param pubMedId         - PubMed ID which uniquely identifies a Publication
     * @param publicationTitle - Title of Publication
     */
    public void removePublication(String pubMedId, String publicationTitle) {
        Publication toRemove = null;

        for (Publication pub : publications) {
            if (pub.getPublicationTitle().equals(publicationTitle) || pub.getPubmedId().equals(pubMedId)) {
                toRemove = pub;
                break;
            }
        }

        if (toRemove != null) {
            publications.remove(toRemove);
        }
    }

    public void setReferenceObject(DataEntryReferenceObject referenceObject) {
        setReferenceObject(referenceObject, InvestigationFileSection.INVESTIGATION_SECTION);
    }
}
