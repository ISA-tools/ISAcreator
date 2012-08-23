package org.isatools.isacreator.api;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/08/2012
 * Time: 12:02
 *
 * Functionality to authenticate a user - the GUI compoment using this functionality is org.isatools.isacreator.gui.menu.AuthenticationMenu
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class Authentication {

    private static ISAcreator main = ApplicationManager.getCurrentApplicationInstance();

    /**
     * Checks if username/password exist in user profiles, sets current user and updates ontology history
     * @param username
     * @param password
     */
    public static boolean login(String username, char[] password) {

        if (username==null && password==null)
            return false;

        for (UserProfile up : main.getUserProfiles()) {

            //user exists
            if (up.getUsername()!=null && up.getUsername().equals(username)) {

                if (password==null && up.getPassword()==0) {
                    setCurrentUser(up);
                    return true;
                }

                char[] pwd = password;
                String pwdAsString = "";

                for (char c : pwd) {
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


    private static void setCurrentUser(UserProfile up){
        ISAcreator.setCurrentUser(up);
        ISAcreator.setUserOntologyHistory(up.getUserHistory());
        Spreadsheet.fileSelectEditor.setFtpManager(up.getFtpManager());
    }
}
