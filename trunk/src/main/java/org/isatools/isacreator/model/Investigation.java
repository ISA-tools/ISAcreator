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
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;

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
public class Investigation {

    private InvestigationDataEntry userInterface;
    private String reference = "";
    private String investigationIdentifier = "";
    private String investigationTitle = "";
    private String investigationDescription = "";
    private String submissionDate = "";
    private String publicReleaseDate = "";

    private List<OntologySourceRefObject> ontologiesUsed;
    private List<Publication> publications;
    private List<Contact> contacts;
    private Map<String, Study> studies;
    private Map<String, String> assays;

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
     * @param investigationIdentifier  - ID to be given to the investigation
     * @param investigationTitle       - Title given to the investigation
     * @param investigationDescription - Description of the Investigation including aims, hypotheses, and so forth.
     * @param submissionDate           - Date the Investigation was submitted.
     * @param publicReleaseDate        - Date the Investigation should be available to the public.
     */
    public Investigation(String investigationIdentifier, String investigationTitle, String investigationDescription,
                         String submissionDate, String publicReleaseDate) {
        this.investigationIdentifier = investigationIdentifier;
        this.investigationTitle = investigationTitle.equals("") ? "Investigation" : investigationTitle;
        this.investigationDescription = investigationDescription;
        this.submissionDate = submissionDate;
        this.publicReleaseDate = publicReleaseDate;
        studies = new ListOrderedMap<String, Study>();
        assays = new HashMap<String, String>();
        contacts = new ArrayList<Contact>();
        publications = new ArrayList<Publication>();
        instantiateOntologySources();
    }

    private void instantiateOntologySources() {
        ontologiesUsed = new ArrayList<OntologySourceRefObject>();
        ontologiesUsed.add(new OntologySourceRefObject("OBI", "", "", "Ontology for Biomedical Investigations"));
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
        if (!checkPublicationExists(publication.getPubmedId())) {
            publications.add(publication);
            return true;
        }
        return false;
    }

    private boolean checkPublicationExists(String pubmedId) {
        for (Publication p : publications) {
            if (p.getPubmedId().equals(pubmedId)) {
                return true;
            }
        }
        return false;
    }

    public List<OntologySourceRefObject> getOntologiesUsed() {
        return ontologiesUsed;
    }

    public void setOntologiesUsed(List<OntologySourceRefObject> ontologiesUsed) {
        this.ontologiesUsed = ontologiesUsed;
    }

    public boolean checkOntologySourceRefExists(OntologySourceRefObject osro) {
        for (OntologySourceRefObject o : ontologiesUsed) {
            if (o.getSourceName().equals(osro.getSourceName())) {
                return true;
            }
        }

        return false;
    }

    public void clearUsedOntologies() {
        ontologiesUsed.clear();
    }

    public void addToOntologies(List<OntologySourceRefObject> ontologiesToAdd) {
        for (OntologySourceRefObject osro : ontologiesToAdd) {
            if (!checkOntologySourceRefExists(osro)) {
                ontologiesUsed.add(osro);
            }
        }
    }

    public void addToPublications(List<Publication> publicationsToAdd) {
        for (Publication p : publicationsToAdd) {
            addPublication(p);
        }
    }

    public void addToContacts(List<Contact> contactssToAdd) {
        for (Contact c : contactssToAdd) {
            addContact(c);
        }
    }

    /**
     * Return a HashMap containing AssayRef <-> StudyId pairs
     *
     * @return HashMap<String, String>
     */
    public Map<String, String> getAssays() {
        return assays;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public String getInvestigationDescription() {
        return investigationDescription;
    }

    public String getInvestigationIdentifier() {
        return investigationIdentifier;
    }

    public String getPublicReleaseDate() {
        return publicReleaseDate;
    }


    public String getInvestigationTitle() {
        return investigationTitle;
    }

    public String getReference() {
        return reference;
    }

    public Map<String, Study> getStudies() {
        return studies;
    }

    public InvestigationDataEntry getUserInterface() {
        return userInterface;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setInvestigationDescription(String investigationDescription) {
        this.investigationDescription = investigationDescription;
    }

    public void setInvestigationIdentifier(String investigationIdentifier) {
        this.investigationIdentifier = investigationIdentifier;
    }


    public void setPublicReleaseDate(String publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }

    public void setInvestigationTitle(String investigationTitle) {
        this.investigationTitle = investigationTitle;
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

    public void setReference(String reference) {
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
        return investigationTitle;
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
            contacts.add(contact);

            return true;
        }

        return false;
    }

    private boolean checkContactExists(String forename, String surname,
                                       String email) {
        for (Contact c : contacts) {
            if (c.getFirstName().equals(forename) &&
                    c.getLastName().equals(surname) &&
                    c.getEmail().equals(email)) {
                return true;
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
}
