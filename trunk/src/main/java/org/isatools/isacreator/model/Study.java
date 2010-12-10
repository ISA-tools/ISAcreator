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

import org.isatools.isacreator.gui.StudyDataEntry;

import java.util.*;


/**
 * Representation of Study data as an object.
 *
 * @author Eamonn Maguire
 */
public class Study {

    private Assay studySampleRecord;
    private String dateOfSubmission = "";
    private String publicReleaseDate = "";
    private String studyDesc = "";
    private String studyId;
    private String studySample = "";
    private String studyTitle = "";

    private Map<String, Assay> assays;
    private List<Contact> contacts;
    private List<Factor> factors;
    private List<Protocol> protocols;
    private List<Publication> publications;
    private List<StudyDesign> studyDesigns;
    private Set<String> previousProtocols;
    private Set<String> previousFactors;

    private StudyDataEntry ui;

    private Map<String, Set<String>> termsToBeReplaced;
    private Map<String, Set<String>> termsToReplaceWith;

    public static final String PROTOCOL_IDENT = "protocols";
    public static final String FACTOR_IDENT = "factors";

    /**
     * A Study is the all-encompassing object which groups together an assay
     * or set of Assays with the aim of answering some biological quaestion.
     *
     * @param studyId - an ID to be given to the study
     */
    public Study(String studyId) {
        this(studyId, "", "", "", "", "");
    }

    /**
     * A Study is the all-encompassing object which groups together an assay
     * or set of Assays with the aim of answering some biological quaestion.
     *
     * @param studyId           - an ID to be given to the study
     * @param studyTitle        - a meaningful title to be given to the Study
     * @param dateOfSubmission  - Date the Study was submitted.
     * @param publicReleaseDate - Date the study will be publicly released.
     * @param studyDesc         - Description of the study including aims, hypotheses etc.
     * @param studySample       - Reference to the Study Sample object (Define)
     */
    public Study(String studyId, String studyTitle, String dateOfSubmission,
                 String publicReleaseDate, String studyDesc, String studySample) {
        this.studyId = studyId;
        this.studyTitle = studyTitle;
        this.dateOfSubmission = dateOfSubmission;
        this.publicReleaseDate = publicReleaseDate;
        this.studyDesc = studyDesc;
        this.studySample = studySample;

        assays = new HashMap<String, Assay>();
        factors = new ArrayList<Factor>();
        protocols = new ArrayList<Protocol>();
        contacts = new ArrayList<Contact>();
        publications = new ArrayList<Publication>();
        studyDesigns = new ArrayList<StudyDesign>();
        termsToBeReplaced = new HashMap<String, Set<String>>();
        termsToReplaceWith = new HashMap<String, Set<String>>();
    }

    public boolean addAssay(Assay assay) {
        if (!assays.containsKey(assay.getAssayReference())) {
            assays.put(assay.getAssayReference(), assay);

            return true;
        }

        return false;
    }

    /**
     * Add a Contact (@see org.isatools.isacreator.model.Contact) to this Study
     *
     * @param contact - The Contact to Add to the Study.
     * @return - true if the contact was added, false otherwise.
     */
    public boolean addContact(Contact contact) {
        if (!checkContactExists(contact.getFirstName(), contact.getLastName(), contact.getEmail())) {
            contacts.add(contact);

            return true;
        }

        return false;
    }

    /**
     * Add a Publication (@see org.isatools.isacreator.model.Publication) to the Study
     *
     * @param publication - The Publication to be added to the Study
     * @return true if the publication was added, false otherwise.
     */
    public boolean addPublication(Publication publication) {
        if (!checkPublicationExists(publication.getPubmedId())) {
            publications.add(publication);

            return true;
        }

        return false;
    }

    /**
     * Add a Factor (@see org.isatools.isacreator.model.Factor) to the Study
     *
     * @param factor - The Factor to be added to the Study
     * @return true if the factor was added, false otherwise.
     */
    public boolean addFactor(Factor factor) {
        if (!checkFactorExists(factor.getFactorName())) {
            factors.add(factor);

            return true;
        }

        return false;
    }

    /**
     * Add a Protocol (@see org.isatools.isacreator.model.Protocol) to the Study
     *
     * @param protocol - The Protocol to be added to the Study
     * @return true if the protocol was added, false otherwise.
     */
    public boolean addProtocol(Protocol protocol) {
        if (!checkProtocolExists(protocol.getProtocolName())) {
            protocols.add(protocol);

            return true;
        }

        return false;
    }

    /**
     * Return a List (@see java.util.List<Publication> ) of Publications contained within
     * this Study
     *
     * @return java.util.List (@see java.util.List<Publication> ) of Publications
     */
    public List<Publication> getPublications() {
        return publications;
    }

    /**
     * Return a List (@see java.util.List<StudyDesign> ) of StudyDesign objects contained within
     * this Study
     *
     * @return java.util.List (@see java.util.List<StudyDesign> ) of StudyDesign
     */
    public List<StudyDesign> getStudyDesigns() {
        return studyDesigns;
    }

    /**
     * Set the Publications contained within a Study to those provided.
     *
     * @param publications - a list (@see java.util.List) of Publications.
     */
    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }

    /**
     * Set the StudyDesign(s) contained within a Study to those provided.
     *
     * @param studyDesigns - a list (@see java.util.List) of StudyDesign(s).
     */
    public void setStudyDesigns(List<StudyDesign> studyDesigns) {
        this.studyDesigns = studyDesigns;
    }

    /**
     * Remove a StudyDesign from the StudyDesigns referenced in this study.
     *
     * @param studyDesignType - a String denoting the StudyDesign to be removed.
     */
    public void removeStudyDesign(String studyDesignType) {
        StudyDesign toRemove = null;
        for (StudyDesign sd : studyDesigns) {
            if (sd.getStudyDesignType().equals(studyDesignType)) {
                toRemove = sd;
                break;
            }
        }

        if (toRemove != null) {
            studyDesigns.remove(toRemove);
        }
    }

    /**
     * Remove a Publication by providing the PubMedId or the PublicationTitle of the Publication. One of the parameters needs to have data in it,
     * otherwise this method would be useless.
     *
     * @param pubMedId         - the PubMed ID for the publication to be removed. Can be left as an empty string if there is no PubMedID
     * @param publicationTitle - the Publication Title for the publication to be removed. Can be left as an empty String if not known.
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

    /**
     * Returns a Map containing AssayReferences pointing to their respective Assay objects
     *
     * @return Map<String, Assay> where the String is the AssayReference. Map used to make lookups quicker.
     */
    public Map<String, Assay> getAssays() {
        return assays;
    }

    /**
     * Return the List of contacts contained within this Assay
     *
     * @return java.util.List containing Contact Objects.
     */
    public List<Contact> getContacts() {
        return contacts;
    }

    public String getDateOfSubmission() {
        return dateOfSubmission;
    }

    /**
     * Return the List of Factors contained within this Assay
     *
     * @return java.util.List containing Factor Objects.
     */
    public List<Factor> getFactors() {
        return factors;
    }

    /**
     * Return the List of Protocols contained within this Assay
     *
     * @return java.util.List containing Protocol Objects.
     */
    public List<Protocol> getProtocols() {
        return protocols;
    }

    public String getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public String getStudyDesc() {
        return studyDesc;
    }

    public String getStudyId() {
        return studyId;
    }

    public Assay getStudySample() {
        if (studySampleRecord == null) {
            return null;
        }

        return studySampleRecord;
    }

    public String getStudySampleFileIdentifier() {
        if (studySample.equals("")) {
            return "s_" + studyId + ".txt";
        }

        return studySample;
    }

    public String getStudyTitle() {
        return studyTitle;
    }

    public StudyDataEntry getUserInterface() {
        return ui;
    }

    /**
     * Remove assay from references.
     *
     * @param assayRef - reference to the assay
     */
    public void removeAssay(String assayRef) {
        if (assays.containsKey(assayRef)) {
            assays.remove(assayRef);
        }
    }

    /**
     * Removes all references to a factor from all assays and study sample files.
     *
     * @param factorName - the factor to be located and removed
     */
    public void removeFactor(String factorName) {
        for (String key : assays.keySet()) {
            assays.get(key).getSpreadsheetUI().getTable()
                    .removeColumnByName("Factor Value[" + factorName + "]");
        }

        studySampleRecord.getSpreadsheetUI().getTable()
                .removeColumnByName("Factor Value[" + factorName + "]");

        Factor toRemove = null;

        for (Factor f : factors) {
            if (f.getFactorName().equals(factorName)) {
                toRemove = f;

                break;
            }
        }

        if (toRemove != null) {
            factors.remove(toRemove);
        }
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
     * Remove a protocol with a given name from the list of protocols maintained in the study.
     *
     * @param protocolName - Name of protocol to be removed from this study.
     */
    public void removeProtocol(String protocolName) {
        Protocol toRemove = null;

        for (Protocol p : protocols) {
            if (p.getProtocolName().equals(protocolName)) {
                toRemove = p;
                break;
            }
        }

        if (toRemove != null) {
            protocols.remove(toRemove);
        }
    }

    /**
     * Update all the factor names
     *
     * @param previousFactorName - The factor name which would exist in the current tables
     * @param newFactorName      - the values to replace the old factor names with
     */
    public void replaceFactors(String previousFactorName, String newFactorName) {
        previousFactorName = "Factor Value[" + previousFactorName + "]";
        newFactorName = "Factor Value[" + newFactorName + "]";

        for (String key : assays.keySet()) {
            assays.get(key).getSpreadsheetUI().getTable()
                    .substituteHeaderNames(previousFactorName, newFactorName);
            System.out.print(".");
        }

        studySampleRecord.getSpreadsheetUI().getTable()
                .substituteHeaderNames(previousFactorName,
                        newFactorName);
    }

    /**
     * Replaces all previously entered protocols with it's substituted value.
     *
     * @param previousProtocolName - protocol with the name to find
     * @param newProtocolName      - new protocol to be used
     */
    public void replaceProtocols(String previousProtocolName,
                                 String newProtocolName) {
        for (String key : assays.keySet()) {
            assays.get(key).getSpreadsheetUI().getTable()
                    .susbstituteTermsInColumn("Protocol REF",
                            previousProtocolName, newProtocolName);
        }

        studySampleRecord.getSpreadsheetUI().getTable()
                .susbstituteTermsInColumn("Protocol REF",
                        previousProtocolName, newProtocolName);

        if (newProtocolName.equals("")) {
            // we are removing the protocol
            Protocol toRemove = null;

            for (Protocol p : protocols) {
                if (p.getProtocolName().equals(previousProtocolName)) {
                    toRemove = p;

                    break;
                }
            }

            if (toRemove != null) {
                protocols.remove(toRemove);
            }
        }
    }

    public void setAssays(Map<String, Assay> assays) {
        this.assays = assays;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setDateOfSubmission(String dateOfSubmission) {
        this.dateOfSubmission = dateOfSubmission;
    }


    public void setFactors(List<Factor> factors) {

        if (previousFactors == null) {
            previousFactors = new HashSet<String>();
        }

        previousFactors.clear();

        for (Factor f : this.factors) {
            previousFactors.add(f.getFactorName());
        }

        this.factors = factors;

        for (String f : previousFactors) {
            if (!doesFactorWithNameExist(f)) {
                if (termsToBeReplaced.get(FACTOR_IDENT) == null) {
                    termsToBeReplaced.put(FACTOR_IDENT, new HashSet<String>());
                }
                termsToBeReplaced.get(FACTOR_IDENT).add(f);
            }
        }

        for (Factor f : factors) {
            if (!previousFactors.contains(f.getFactorName())) {
                if (termsToReplaceWith.get(FACTOR_IDENT) == null) {
                    termsToReplaceWith.put(FACTOR_IDENT, new HashSet<String>());
                }

                termsToReplaceWith.get(FACTOR_IDENT).add(f.getFactorName());
            }
        }
    }

    public void setProtocols(List<Protocol> protocols) {

        if (previousProtocols == null) {
            previousProtocols = new HashSet<String>();
        }
        previousProtocols.clear();
        for (Protocol p : this.protocols) {
            previousProtocols.add(p.getProtocolName());
        }

        this.protocols = protocols;

        for (String p : previousProtocols) {
            if (!doesProtocolWithNameExist(p)) {
                if (termsToBeReplaced.get(PROTOCOL_IDENT) == null) {
                    termsToBeReplaced.put(PROTOCOL_IDENT, new HashSet<String>());
                }
                termsToBeReplaced.get(PROTOCOL_IDENT).add(p);
            }
        }

        for (Protocol p : protocols) {
            if (!previousProtocols.contains(p.getProtocolName())) {
                if (termsToReplaceWith.get(PROTOCOL_IDENT) == null) {
                    termsToReplaceWith.put(PROTOCOL_IDENT, new HashSet<String>());
                }

                termsToReplaceWith.get(PROTOCOL_IDENT).add(p.getProtocolName());
            }
        }

    }

    public Map<String, String[]> getTermsToBeReplaced() {
        return convertMap(termsToBeReplaced);
    }

    public Map<String, String[]> getTermsToReplaceWith() {
        return convertMap(termsToReplaceWith);
    }

    private Map<String, String[]> convertMap(Map<String, Set<String>> toConvert) {
        Map<String, String[]> modifiedResult = new HashMap<String, String[]>();

        for (String key : toConvert.keySet()) {
            modifiedResult.put(key, new String[toConvert.get(key).size()]);

            int count = 0;
            for (String term : toConvert.get(key)) {
                modifiedResult.get(key)[count] = term;
                count++;
            }

        }

        return modifiedResult;
    }


    public void clearTermReplacementHistory() {
        termsToBeReplaced = new HashMap<String, Set<String>>();
        termsToReplaceWith = new HashMap<String, Set<String>>();
    }

    public void setPublicReleaseDate(String publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }

    public void setSampleFileName(String sampleFile) {
        String studySampleName = sampleFile;

        if (!studySampleName.startsWith("s_")) {
            studySampleName = "s_" + studySampleName;
        }

        if (!studySampleName.endsWith(".txt")) {
            studySampleName += ".txt";
        }

        this.studySample = studySampleName;
    }

    public void setStudyDesc(String studyDesc) {
        this.studyDesc = studyDesc;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public void setStudySamples(Assay studySamples) {
        this.studySampleRecord = studySamples;
    }

    public void setStudyTitle(String studyTitle) {
        this.studyTitle = studyTitle;
    }

    public void setUI(StudyDataEntry ui) {
        this.ui = ui;
    }

    public String toString() {
        return studyId;
    }

    /**
     * Returns a 2D Array representation of the Data contained inside the Study Sample.
     *
     * @return Object[][] containing all the data contained in the Study Sample. Top Most row (Object[0] will contain all the headers for the Study Sample.)
     */
    public Object[][] getStudySampleDataMatrix() {
        return studySampleRecord.getTableReferenceObject().getDataAsArray();
    }

    public String[] getProtocolNames() {
        String[] protocolList = new String[protocols.size()];

        int count = 0;
        for (Protocol p : protocols) {
            protocolList[count] = p.getProtocolName();
            count++;
        }

        return protocolList;
    }


    private boolean doesFactorWithNameExist(String factorName) {
        for (Factor f : factors) {
            if (f.getFactorName().equals(factorName)) {
                return true;
            }
        }
        return false;
    }

    private boolean doesProtocolWithNameExist(String protocolName) {
        for (Protocol p : protocols) {
            if (p.getProtocolName().equals(protocolName)) {
                return true;
            }
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

    private boolean checkPublicationExists(String pubmedId) {
        for (Publication p : publications) {
            if (p.getPubmedId().equals(pubmedId)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkFactorExists(String n) {
        for (Factor f : factors) {
            if (f.getFactorName().equals(n)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkProtocolExists(String n) {
        for (Protocol p : protocols) {
            if (p.getProtocolName().equals(n)) {
                return true;
            }
        }

        return false;
    }


}
