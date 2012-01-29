package org.isatools.isacreator.googlespreadsheet.authentication;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 25/10/2011
 *         Time: 12:00
 */
public class UserAccount {

    private String userName;
    private String password;

    public UserAccount(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
