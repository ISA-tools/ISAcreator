package org.isatools.isacreator.gs;

import org.apache.log4j.Logger;
import org.genomespace.client.GsSession;
import org.genomespace.client.exceptions.InternalServerException;
import org.isatools.isacreator.api.Authentication;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 05/10/2012
 * Time: 14:02
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 *
 * Based on org.broad.igv.org.isatools.isacreator.gs.GSUtils
 *
 */
public class GSSingleSignOnManager implements Authentication {

    private static final Logger log = Logger.getLogger(GSSingleSignOnManager.class);

    private boolean sso = false;

    /*
    * Directory and filenames to save the token and username to facilitate SSO
    */
    private static String tokenSaveDir = ".gs";
    private static String tokenSaveFileName = ".gstoken";
    private static String usernameSaveFileName = ".gsusername";

    //TODO analyse possible integration with ISAcreator user profiles
    //TODO this currently works for a single user
    private static String gsUser = null;
    private static String gsToken = null;

    private GSIdentityManager identityManager = null;

    public GSSingleSignOnManager(){
        identityManager = new GSIdentityManager();
    }

    public GSSingleSignOnManager(boolean b){
        setSSO(b);
        identityManager = new GSIdentityManager();
    }

    public void setSSO(boolean b){
        sso = b;
    }

    public GsSession getGSSession(){
        return identityManager.getSession();//identityManager.getSession(username);
    }

    public GSDataManager getGSDataManager(){
        return identityManager.getGsDataManager();
    }

    /*** Interface ***/

    /**
     * Login method
     *
     * @param username a string
     * @param password an array of characters
     * @return true if login was successful, false otherwise
     */
    public boolean login(String username, char[] password) {
        if (username==null || (username!=null && username.trim().equals(""))){
            return false;
        }
        if (password==null){
            return false;
        }
        boolean successful = identityManager.login(username, password);

        if (successful && sso){
            //single sign on stuff
            GsSession session = identityManager.getSession();//identityManager.getSession(username);
            String token = session.getAuthenticationToken();
            setGSToken(token);
        }

        return successful;
    }

    /***
     *
     * Login with no username/password, only valid if authentication token has previously been saved.
     *
     * @return
     */
    public boolean login(String u){
        String token = getGSToken();
        if (token==null)
            return false;
        try{
            GsSession gsSession = new GsSession(token);
            //identityManager.addSession(gsSession);
            identityManager.setSession(gsSession);
            return true;
        }catch(InternalServerException e){
            log.debug(e.getMessage());
            return false;
        }
    }

    public boolean logout(String username) {
        gsToken = null;
        gsUser = null;

        File userfile = getUsernameFile();
        if (userfile.exists()) {
            userfile.delete();
        }
        File tokenFile = getTokenFile();
        if (tokenFile.exists()) {
            tokenFile.delete();
        }

        try {
            URI gsURI = new URI("https://dm.genomespace.org/datamanager/v1.0/");
            final CookieStore cookieStore = ((CookieManager) CookieManager.getDefault()).getCookieStore();
            List<HttpCookie> cookies = new ArrayList<HttpCookie>(cookieStore.get(gsURI));
            if (cookies != null) {
                for (HttpCookie cookie : cookies) {
                    final String name = cookie.getName();
                    if (name.equals("gs-token") || name.equals("gs-username")) {
                        cookieStore.remove(gsURI, cookie);
                    }
                }
            }
        } catch (URISyntaxException e) {
            log.error("Error creating GS URI", e);
            return false;
        }

        return true;

    }



    /**********************************************************************************************************************************/
    /*** Private methods ***/

    /**
     *
     * Obtain the GS authentication token
     *
     * @return a string with the token (or null if not available)
     */
    private String getGSToken() {
        if (gsToken == null && sso) {
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

    /***
     *
     * Sets a new token for authentication with GS - if the directory and file do not exist, they are created
     *
     * @param newToken authentication token (string) for single sign on
     */
    private void setGSToken(String newToken) {
        if (!sso) return;
        if (gsToken == null || !gsToken.equals(newToken)) {
            gsToken = newToken;
            BufferedWriter bw = null;

            File gsDir = getTokenSaveDir();
            if (!gsDir.isDirectory()) {
                log.error("Could not store token for SSO.  File " + gsDir.getAbsolutePath() + "exists and is not a directory.");
                return; // someone made a file with this name...
            }
            File tokenFile = getTokenFile();
            if (tokenFile.exists()) tokenFile.delete();
            writeToFile(gsToken, tokenFile);
        }
    }


    private void setGSUser(String newUser) {
        if (!sso) return;
        if (gsUser == null || !gsUser.equals(newUser)) {
            gsUser = newUser;
            BufferedWriter bw = null;

            File gsDir = getTokenSaveDir();
            if (!gsDir.isDirectory()) {
                log.error("Could not store token for SSO.  File " + gsDir.getAbsolutePath() + "exists and is not a directory.");
                return; // someone made a file with this name...
            }
            File userFile = getUsernameFile();
            if (userFile.exists()) userFile.delete();
            writeToFile(gsUser, userFile);
        }
    }

    /**
     * Returns the directory where tokens are saved.
     *
     * @return
     */
    private File getTokenSaveDir() {
        String userDir = System.getProperty("user.home");

         File gsDir = new File(userDir, tokenSaveDir);
         if (gsDir.exists()) {
             //single sign on has been used before
             sso = true;
             return gsDir;
         } else if (sso) {
             //directory doesn't exist, if sso, create it
             gsDir.mkdir();
             return gsDir;
         }
         return null;
    }

    /**
     * Returns the token file.
     *
     * @return
     */
    private File getTokenFile() {
        File gsDir = getTokenSaveDir();
        return (gsDir != null && gsDir.exists() & sso) ? new File(gsDir, tokenSaveFileName) : null;
    }

    private File getUsernameFile() {
        File gsDir = getTokenSaveDir();
        return (gsDir != null && gsDir.exists() && sso) ? new File(gsDir, usernameSaveFileName) : null;
    }


    private void writeToFile(String line, File aFile) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(aFile));
            bw.write(line);

            bw.close();
        } catch (Exception e) {
            log.error("Failed to save the token for later Single Sign on", e);
        } finally {
            try {
                if (bw != null) bw.close();
            } catch (Exception e) {
            }
        }
    }

}
