package org.isatools.isacreator.api;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.managers.ApplicationManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/08/2012
 * Time: 12:38
 *
 * Functionality for validating user profile fields and creating user profile
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class CreateProfile {

    private static ISAcreator main = ApplicationManager.getCurrentApplicationInstance();


    public static boolean emptyPassword(char[] cpassword){
        String password = new String(cpassword);
        return password.equals("");
    }

    public static boolean emptyField(String field){
        return field.equals("");
    }

    public static boolean matchingPasswords(char[] cpassword1, char[] cpassword2){
        String password1 = new String(cpassword1);
        String password2 = new String(cpassword2);
        return password1.equals(password2);
    }

    public static boolean validEmail(String email){
        Pattern p = Pattern.compile("[.]*@[.]*");
        Matcher m = p.matcher(email);
        return m.find();
    }

    public static boolean duplicateUser(String username){
        for (UserProfile up : main.getUserProfiles()) {
            if (up.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static void createProfile(String username,char[] password, String firstname, String surname, String institution, String email){

        UserProfile newUser = new UserProfile(username,
                new String(password).hashCode(),
                firstname,
                surname,
                institution,
                email);

        main.getUserProfiles().add(newUser);
        main.setCurrentUser(newUser);
        main.setUserOntologyHistory(newUser.getUserHistory());
        main.saveUserProfiles();
    }

}
