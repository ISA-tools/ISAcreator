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

package org.isatools.isacreator.io;

import org.isatools.isacreator.filechooser.FTPManager;
import org.isatools.isacreator.gui.StudySubData;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.Factor;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * UserProfile object stores information pertaining to a user including the protocols, factors, contacts, and ontologies
 * which were entered in previous submissions.
 * ** Future developments :
 * 1. Store changes made in structure of tables e.g. the assay type, which columns were added, where they were
 * added, and any associated columns so that the same modifications can be made in any future submissions
 * if the number of times the modifications are made exceeds the number of times the modifications are not made.
 * 2. Store frequently inputed text so as to provide autocomplete across multiple sessions
 *
 * @author Eamonn Maguire
 */
public class UserProfile implements Serializable {
    private Map<String, OntologyTerm> userHistory;
    private Map<String, OntologySourceRefObject> usedOntologySources;
    private List<StudySubData> previouslyUsedFactors;
    private List<StudySubData> previouslyUsedProtocols;
    private List<StudySubData> previouslyUsedContacts;
    private FTPManager ftpManager;
    private String email;
    private String forename;
    private String institution;
    private String surname;
    private String username;

    // password is stored as its hashvalue
    private long password;

    private static final long serialVersionUID = -1;

    public UserProfile(String username, long password, String forename,
                       String surname, String institution, String email) {
        this.username = username;
        this.password = password;
        this.forename = forename;
        this.surname = surname;
        this.institution = institution;
        this.email = email;
        userHistory = new HashMap<String, OntologyTerm>();
        previouslyUsedContacts = new ArrayList<StudySubData>();
        previouslyUsedFactors = new ArrayList<StudySubData>();
        previouslyUsedProtocols = new ArrayList<StudySubData>();
        usedOntologySources = new HashMap<String, OntologySourceRefObject>();
        ftpManager = new FTPManager();
    }

    public void addContact(Contact usedContact) {
        if (!checkExists(usedContact, previouslyUsedContacts) &&
                !usedContact.getFirstName().trim().equals("")) {
            previouslyUsedContacts.add(usedContact);
        }
    }

    public void addFactor(Factor usedFactor) {
        if (!checkExists(usedFactor, previouslyUsedFactors) &&
                !usedFactor.getFactorName().trim().equals("")) {
            previouslyUsedFactors.add(usedFactor);
        }
    }

    public void addOntologyReference(OntologySourceRefObject osro) {
        usedOntologySources.put(osro.getSourceName(), osro);
    }

    public void addProtocol(Protocol usedProtocol) {
        if (!checkExists(usedProtocol, previouslyUsedProtocols) &&
                !usedProtocol.getProtocolName().trim().equals("")) {
            previouslyUsedProtocols.add(usedProtocol);
        }
    }

    private boolean checkExists(StudySubData toCheck,
                                List<StudySubData> data) {
        for (StudySubData ssd : data) {
            if (ssd.getIdentifier().equals(toCheck.getIdentifier())) {
                return true;
            }
        }

        return false;
    }

    public String getEmail() {
        return email;
    }

    public String getForename() {
        return forename;
    }

    public String getInstitution() {
        return institution;
    }

    public OntologySourceRefObject getOntologySource(String source) {
        return usedOntologySources.get(source);
    }

    public long getPassword() {
        return password;
    }

    public List<Contact> getPreviouslyUsedContacts() {
        List<Contact> contacts = new ArrayList<Contact>();

        for (StudySubData ssd : previouslyUsedContacts) {
            if (!((Contact) ssd).getFirstName().trim().equals("")) {
                contacts.add((Contact) ssd);
            }
        }

        return contacts;
    }

    public List<Factor> getPreviouslyUsedFactors() {
        List<Factor> factors = new ArrayList<Factor>();

        for (StudySubData ssd : previouslyUsedFactors) {
            if (!((Factor) ssd).getFactorName().trim().equals("")) {
                factors.add((Factor) ssd);
            }
        }

        return factors;
    }

    public List<Protocol> getPreviouslyUsedProtocols() {
        List<Protocol> protocols = new ArrayList<Protocol>();

        for (StudySubData ssd : previouslyUsedProtocols) {
            if (!((Protocol) ssd).getProtocolName().trim().equals("")) {
                protocols.add((Protocol) ssd);
            }
        }

        return protocols;
    }

    public String getSurname() {
        return surname;
    }

    public Map<String, OntologySourceRefObject> getUsedOntologySources() {
        return usedOntologySources;
    }

    public Map<String, OntologyTerm> getUserHistory() {
        return userHistory;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setIntsitution(String institution) {
        this.institution = institution;
    }

    public void setPassword(long password) {
        this.password = password;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setUsedOntologySources(
            Map<String, OntologySourceRefObject> usedOntologySources) {
        this.usedOntologySources = usedOntologySources;
    }

    public void setUserHistory(Map<String, OntologyTerm> userHistory) {
        this.userHistory = userHistory;
    }

    public FTPManager getFtpManager() {
        return ftpManager;
    }

    public void setFtpManager(FTPManager ftpManager) {
        this.ftpManager = ftpManager;
    }

    public void setPreviouslyUsedContacts(List<StudySubData> previouslyUsedContacts) {
        this.previouslyUsedContacts = previouslyUsedContacts;
    }

    public void setPreviouslyUsedProtocols(List<StudySubData> previouslyUsedProtocols) {
        this.previouslyUsedProtocols = previouslyUsedProtocols;
    }

    public void setPreviouslyUsedFactors(List<StudySubData> previouslyUsedFactors) {
        this.previouslyUsedFactors = previouslyUsedFactors;
    }
}
