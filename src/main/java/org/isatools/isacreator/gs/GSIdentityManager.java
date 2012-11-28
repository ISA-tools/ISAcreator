package org.isatools.isacreator.gs;

import org.apache.log4j.Logger;
import org.genomespace.client.GsSession;
import org.genomespace.client.User;
import org.genomespace.client.UserManagerClient;
import org.genomespace.client.exceptions.AuthorizationException;
import org.genomespace.client.exceptions.InternalServerException;
import org.genomespace.client.exceptions.ServerNotFoundException;
import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.api.AuthenticationManager;
import org.isatools.isacreator.api.CreateProfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
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
public class GSIdentityManager implements Authentication {

    //maintaining a single session
    private GsSession session = null;

    //private String gsUser = null;
    private String gsToken = null;

    private String tokenSaveDir = ".gs";
    private String tokenSaveFileName = ".gstoken";
    private String usernameSaveFileName = ".gsusername";

    private AuthenticationManager authenticationManager = new AuthenticationManager();

    private static final Logger log = Logger.getLogger(GSIdentityManager.class);

    private static GSIdentityManager instance = null;

    public static GSIdentityManager getInstance(){
        if (instance==null){
            instance = new GSIdentityManager();
        }
        return instance;
    }

    /**
     * Empty constructor
     */
    public GSIdentityManager(){
        try{
            session = new GsSession();
        }catch(InternalServerException e){
            e.printStackTrace();
        }
    }


    /**
     *
     * @param username
     * @param pass an array of characters with the user's password
     * @return
     */
    public boolean login(String username, char[] pass) {
        if (username==null || pass==null)
            return false;

        try{
            String password = new String(pass);

            User user = session.login(username, password);

            log.info("Logged into GenomeSpace as "+username);

            //local login
            boolean result = authenticationManager.login(username, pass);

            if (!result){
                //create user
                CreateProfile.createProfile(username, pass, "", "", "", "");

            }

            return true;
        }catch(AuthorizationException e){
            return false;
        }catch(InternalServerException isex){
            return false;
        }catch(ServerNotFoundException snfex){
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
        if (session==null)
            return false;
        session.logout();
        return true;
    }

    /**
     * The GSIdentityManager does not support single sign on, thus it returns false
     *
     * @return
     */
    public boolean login(String u){
        String token = getGSToken();
        if (token==null)
            return false;
        try{

            GsSession gsSession = new GsSession(token);
            setSession(gsSession);

            String username = gsSession.getUserManagerClient().getTokenUsername(token);

            //local login
            boolean result = authenticationManager.login(username);

            if (!result){
                //create user
                CreateProfile.createProfile(username, username.toCharArray(), "", "", "", "");

            }




            return true;

        }catch(InternalServerException e){
            e.printStackTrace();
            log.debug(e.getMessage());
            return false;
        }
    }

    /**
     *
     *
     * @return
     */
    public boolean isLoggedIn() {
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
    public static String registerUser(String username, String password, String emailAddress) {

        try {
            GsSession session = new GsSession();
            User newUser = session.registerUser(username,
                    password, emailAddress);
            return "";
        }catch(InternalServerException isex){
            isex.printStackTrace();
            return "Error registering user "+isex.getMessage();
        }
    }


    /**
     * Gets user session.
     *
     *
     * @return
     */
    //public GsSession getSession(String userName){
    public GsSession getSession(){
        //return userSessions.get(userName);
        return session;
    }

    public void setSession(GsSession gsSession){
        if (gsSession.isLoggedIn()){
            UserManagerClient userManagerClient = gsSession.getUserManagerClient();

            try{
                long time = userManagerClient.getRemainingTokenTime();
                if (time!=0){
                    String username = gsSession.getCachedUsernameForSSO();
                    //Collection<String> users = userManagerClient.getAllUsernames();
                    //for(String username: users){
                        //userSessions.put(username, gsSession);
                    session = gsSession;
                    //}
                }
            }catch(InternalServerException e){
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public GSDataManager getGsDataManager(){
        System.out.println("session: is logged in when retrieving DM?"+session.isLoggedIn());
        return new GSDataManager(session);
    }

    private String getGSToken() {
        if (gsToken == null) {
            File file = getTokenFile();
            if (file!=null && file.exists()) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(file));
                    gsToken = br.readLine();
                } catch (IOException e) {
                    log.error("Error reading GS cookie", e);
                } finally {
                    if (br != null) try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return gsToken;
    }

    private File getTokenFile() {
        File gsDir = getTokenSaveDir();
        return (gsDir != null && gsDir.exists()) ? new File(gsDir, tokenSaveFileName) : null;
    }

    private File getTokenSaveDir() {
        String userDir = System.getProperty("user.home");
        File gsDir = new File(userDir, tokenSaveDir);
        if (!gsDir.exists()) {
            gsDir.mkdir();
        }
        return gsDir;
    }

}
