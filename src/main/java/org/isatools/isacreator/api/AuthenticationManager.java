package org.isatools.isacreator.api;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.io.UserProfileManager;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/08/2012
 * Time: 12:02
 * <p/>
 * Functionality to authenticate a user - the GUI compoment using this functionality is org.isatools.isacreator.gui.menu.AuthenticationMenu
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class AuthenticationManager implements Authentication {

    private static ISAcreator main = ApplicationManager.getCurrentApplicationInstance();

    /**
     * Checks if username/password exist in user profiles, sets current user and updates ontology history
     *
     * @param username a string with the user name
     * @param password an array of characters with the password
     */
    public boolean login(String username, char[] password) {

        if (username == null && password == null)
            return false;

        for (UserProfile up : UserProfileManager.getUserProfiles()) {

            //user exists
            if (up.getUsername() != null && up.getUsername().equals(username)) {

                if (password == null && up.getPassword() == 0) {
                    setCurrentUser(up);
                    return true;
                }

                String pwdAsString = "";

                for (char c : password) {
                    pwdAsString += c;
                }

                if (up.getPassword() == pwdAsString.hashCode()) {
                    setCurrentUser(up);
                    return true;
                }
            }
        }
        return false;
    }


    public boolean logout(String username) {
        return false;
    }

    /**
     * Used to login user (locally) when it is logged in in GenomeSpace
     *
     * @return
     */
    public boolean login(String username) {

        if (username == null)
            return false;

        for (UserProfile up : UserProfileManager.getUserProfiles()) {

            //user exists
            if (up.getUsername() != null && up.getUsername().equals(username)) {

                if (up.getPassword() == 0) {
                    setCurrentUser(up);
                    return true;
                }


            }
        }
        return false;
    }

    /**
     * Sets current user, ontology history and ftp manager
     *
     * @param up
     */
    private void setCurrentUser(UserProfile up) {
        UserProfileManager.setCurrentUser(up);
        OntologyManager.setOntologySelectionHistory(up.getUserHistory());
        Spreadsheet.fileSelectEditor.setFtpManager(up.getFtpManager());
    }


}
