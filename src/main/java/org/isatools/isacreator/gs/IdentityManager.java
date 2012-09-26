package org.isatools.isacreator.gs;

import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.client.exceptions.InternalServerException;
import org.genomespace.client.exceptions.ServerNotFoundException;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 26/09/2012
 * Time: 14:41
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class IdentityManager {

    private GsSession session = null;
    private User user = null;

    public GsSession getSession(){
        return session;
    }

    public String getUsername(){
        return user.getUsername();
    }

    public boolean login(String username, String password) {

        try{
            session = new GsSession();
            user = session.login(username, password);
            return true;

        }catch(InternalServerException isex){
            return false;
        }catch(ServerNotFoundException snfex){
            return false;
        }
    }

    public boolean isLoggedIn() {
        if (session==null)
            return false;
        return session.isLoggedIn();
    }

    public boolean registerUser(String username, String password, String emailAddress) {

        try {
            User newUser = session.registerUser(username,
                    password, emailAddress);
            return true;
        }catch(InternalServerException isex){
            return false;
        }
    }

    public boolean logout() {
        if (session==null)
            return false;
        session.logout();
        return true;
    }

}
