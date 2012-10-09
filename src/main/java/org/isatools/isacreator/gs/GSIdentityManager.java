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
 * Identity Manager for Genome Space
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSIdentityManager {

    private Map<String, GsSession> userSessions = new HashMap<String, GsSession>();

    /**
     * Gets user session.
     *
     * @param userName
     * @return
     */
    public GsSession getSession(String userName){
        return userSessions.get(userName);
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
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

    /**
     *
     * @param username
     * @return
     */
    public boolean isLoggedIn(String username) {
        GsSession session = userSessions.get(username);
        if (session==null)
            return false;
        return session.isLoggedIn();
    }

    /**
     *
     * @param username
     * @param password
     * @param emailAddress
     * @return
     */
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

    /**
     *
     *
     * @param username
     * @return
     */
    public boolean logout(String username) {
        GsSession session = userSessions.get(username);
        if (session==null)
            return false;
        session.logout();
        return true;
    }

}
