package org.isatools.isacreator.gs;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.datamanager.core.GSDirectoryListing;
import org.genomespace.datamanager.core.GSFileMetadata;

import java.io.File;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 26/09/2012
 * Time: 14:41
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSDataManager {


    /**
     * List files in home directory
     * @param username
     */
    public void lsHome(String username){
        GsSession gsSession = IdentityManager.getSession(username);
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSDirectoryListing homeDirInfo = dmClient.listDefaultDirectory();
    }

    public boolean uploadFiles() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean downloadFiles(String username, GSFileMetadata fileToDownload, File localTargetFile) {

        GsSession gsSession = IdentityManager.getSession(username);

        DataManagerClient dmClient = gsSession.getDataManagerClient();

        GSDirectoryListing dirListing = dmClient.listDefaultDirectory();

        dmClient.downloadFile(fileToDownload, localTargetFile,true);
        return true;
    }


    public GSFileMetadata getFileMetadata(String username, String filePath){
        GsSession gsSession = IdentityManager.getSession(username);

        DataManagerClient dmClient = gsSession.getDataManagerClient();

        GSFileMetadata fileMetadata = dmClient.getMetadata(filePath);

        return fileMetadata;
    }

    public boolean mkDir() {
        return false;
    }

    public void ls() {

    }

}
