package org.isatools.isacreator.gs;

import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.client.exceptions.InternalServerException;
import org.genomespace.client.exceptions.ServerNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 26/09/2012
 * Time: 14:41
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class IdentityManager {

    private static Map<String, GsSession> userSessions = new HashMap<String, GsSession>();


    public static GsSession getSession(String userName){
        return userSessions.get(userName);
    }


    public boolean login(String username, String password) {

        try{
            GsSession session = new GsSession();
            User user = session.login(username, password);

            userSessions.put(user.getUsername(),session);
            return true;

        }catch(InternalServerException isex){
            return false;
        }catch(ServerNotFoundException snfex){
            return false;
        }
    }

    public boolean isLoggedIn(String username) {
        GsSession session = userSessions.get(username);
        if (session==null)
            return false;
        return session.isLoggedIn();
    }

    public boolean registerUser(String username, String password, String emailAddress) {

        GsSession session = userSessions.get(username);
        if (session==null)
            return false;

        try {
            User newUser = session.registerUser(username,
                    password, emailAddress);
            return true;
        }catch(InternalServerException isex){
            return false;
        }
    }

    public boolean logout(String username) {
        GsSession session = userSessions.get(username);
        if (session==null)
            return false;
        session.logout();
        return true;
    }

}
