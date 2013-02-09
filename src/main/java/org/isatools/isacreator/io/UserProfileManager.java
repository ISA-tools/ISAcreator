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

import org.apache.log4j.Logger;
import org.isatools.isacreator.common.EncryptedObject;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;


/**
 * UserProfileIO class handles the input/output of all objects relating to UserProfiles, e.g. Contacts, Ontologies, Protocols,
 * and the actual UserProfile Object itself.
 *
 * @author Eamonn Maguire
 */

public class UserProfileManager {
    private static final Logger log = Logger.getLogger(UserProfileManager.class.getName());

    private static String USER_PROFILE_FILENAME = "profiles.sup";

    private static UserProfile currentUser;
    private static List<UserProfile> userProfiles;
    public static final String PASSPHRASE = "eamonniscool";

    public static void saveUserProfiles() {
        //.sup extension -> secure user profiles
        String userProfileLocation = ISAcreatorProperties.getProperty("isacreator.userProfileLocation");
        System.out.println("User profile location: " + userProfileLocation);
        File f = new File(userProfileLocation.equals("") ? USER_PROFILE_FILENAME : userProfileLocation + File.separator + USER_PROFILE_FILENAME);

        log.info("Saving user profile to: " + f.getAbsolutePath());

        EncryptedObject eo = new EncryptedObject();
        try {
            eo.generateKey("eamonniscool");

            // we convert to an ArrayList since this is Serializable, but a Generic List is not!
            updateUserProfileInformation(getCurrentUser());
            SealedObject so = eo.encryptObject((ArrayList) getUserProfiles());
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                    f));
            oos.writeObject(so);
            oos.close();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            log.error(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            log.error(e.getMessage());
        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error(e.getMessage());
        } catch (NoSuchPaddingException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCurrentUser(UserProfile currentUser) {
        UserProfileManager.currentUser = currentUser;
    }

    public static UserProfile getCurrentUser() {
        return currentUser;
    }

    public static void loadUserProfiles() {
        EncryptedObject eo = new EncryptedObject();

        String userProfileLocation = ISAcreatorProperties.getProperty("isacreator.userProfileLocation");
        System.out.println("User profile location: " + userProfileLocation);

        File f = new File(userProfileLocation.equals("") ? USER_PROFILE_FILENAME : userProfileLocation + File.separator + USER_PROFILE_FILENAME);
        System.out.println(f.getAbsolutePath());
        log.info("Loading user profile from: " + f.getAbsolutePath());
        if (f.exists()) {
            try {
                eo.generateKey(PASSPHRASE);

                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                        f));

                SealedObject so = (SealedObject) ois.readObject();
                userProfiles = (List<UserProfile>) eo.decryptObject(so);
                System.out.println("User profiles loaded, we have " + userProfiles.size());
            } catch (NoSuchAlgorithmException e) {
                log.error(e.getMessage());
            } catch (NoSuchPaddingException e) {
                log.error(e.getMessage());
            } catch (InvalidKeyException e) {
                log.error(e.getMessage());
            } catch (InvalidKeySpecException e) {
                log.error(e.getMessage());
            } catch (InvalidAlgorithmParameterException e) {
                log.error(e.getMessage());
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage());
            } catch (BadPaddingException e) {
                log.error(e.getMessage());
            } catch (IllegalBlockSizeException e) {
                log.error(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (userProfiles == null) {
            userProfiles = new ArrayList<UserProfile>();
        }
    }

    public static void saveOntologyLibrary(OntologyLibrary library, File dir) throws IOException {

        String ONTOLOGY_LIBRARY_OUTPUT_NAME = "isacreator-ontologies.ontlib";
        File f = new File(dir.getPath() + File.separator + ONTOLOGY_LIBRARY_OUTPUT_NAME);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                f));
        oos.writeObject(library);
        oos.close();

    }

    public static OntologyLibrary loadOntologyLibrary(File f) throws IOException, ClassNotFoundException {
        if (f.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            return (OntologyLibrary) ois.readObject();
        }
        return null;
    }

    public static void saveContactsLibrary(List<Contact> library, File dir) throws IOException {
        String CONTACT_LIBRARY_OUTPUT_NAME = "isacreator-contacts.contlib";
        File f = new File(dir.getPath() + File.separator + CONTACT_LIBRARY_OUTPUT_NAME);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                f));
        oos.writeObject(library);
        oos.close();
    }

    public static List<Contact> loadContactsLibrary(File f) throws IOException, ClassNotFoundException {
        if (f.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            return (List<Contact>) ois.readObject();
        }
        return null;
    }

    public static void saveProtocolLibrary(List<Protocol> library, File dir) throws IOException {
        String PROTOCOL_LIBRARY_OUTPUT_NAME = "isacreator-protocols.protlib";
        File f = new File(dir.getPath() + File.separator + PROTOCOL_LIBRARY_OUTPUT_NAME);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                f));
        oos.writeObject(library);
        oos.close();
    }

    public static List<Protocol> loadProtocolLibrary(File f) throws IOException, ClassNotFoundException {
        if (f.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            return (List<Protocol>) ois.readObject();
        }
        return null;
    }

    public static void updateUserProfileInformation(UserProfile up) {
        if (ApplicationManager.getCurrentApplicationInstance()!=null && ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment() != null) {
            Investigation inv = ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation();

            // update the user profiles to contain the previously added factors, protocols, and contacts.
            if (inv != null && inv.getStudies() != null) {
                for (Study s : inv.getStudies().values()) {
                    for (Protocol p : s.getProtocols()) {
                        up.addProtocol(p);
                    }

                    for (Factor f : s.getFactors()) {
                        up.addFactor(f);
                    }

                    for (Contact c : s.getContacts()) {
                        up.addContact(c);
                    }
                }

                up.setFtpManager(Spreadsheet.fileSelectEditor.getFTPManager());

                // update used ontology sources
                for (OntologySourceRefObject osro : OntologyManager.getOntologiesUsed()) {
                    up.addOntologyReference(osro);
                }
            }
        }
    }

    public static void updateUserProfiles() {
        for (UserProfile up : UserProfileManager.getUserProfiles()) {

            if (up.getUsername()!=null && up.getUsername().equals(UserProfileManager.getCurrentUser().getUsername())) {
                up.setUserHistory(OntologyManager.getOntologySelectionHistory());
                updateUserProfileInformation(up);
                break;
            }
        }
    }

    public static List<UserProfile> getUserProfiles() {
        return userProfiles == null ? new ArrayList<UserProfile>() : userProfiles;
    }
}
