package org.isatools.isacreator.gs;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.datamanager.core.GSDirectoryListing;
import org.genomespace.datamanager.core.GSFileMetadata;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 26/09/2012
 * Time: 14:41
 *
 * Data Manager for GenomeSpace
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSDataManager {


    /**
     * List files in given directory
     *
     * @param username
     * @param dirPath
     */
    public List<String> ls(String username, String dirPath){
        GsSession gsSession = GSIdentityManager.getSession(username);
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSDirectoryListing dirListing = dmClient.list(dirPath);
        List<GSFileMetadata> fileMetadataList = dirListing.getContents();
        List<String> listing = new ArrayList<String>();
        for(GSFileMetadata fileMetadata:fileMetadataList){
            listing.add(fileMetadata.getName());
        }
        return listing;
    }

    /**
     * Get InputStreams for all the files in a directory
     *
     * This doesn't work at the moment because there is a restriction of two open concurrent connections at a time in GS.
     *
     * @param username
     * @param dirPath
     * @return
     */
    public List<InputStream> lsInputStreams(String username, String dirPath){
        GsSession gsSession = GSIdentityManager.getSession(username);
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSDirectoryListing dirListing = dmClient.list(dirPath);
        List<GSFileMetadata> fileMetadataList = dirListing.getContents();
        List<InputStream> listing = new ArrayList<InputStream>();
        for(GSFileMetadata fileMetadata:fileMetadataList){
            listing.add(dmClient.getInputStream(fileMetadata));
        }
        return listing;
    }


    /**
     * List files in home directory
     * @param username
     */
    public void lsHome(String username){
        GsSession gsSession = GSIdentityManager.getSession(username);
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSDirectoryListing homeDirInfo = dmClient.listDefaultDirectory();
    }


    public GSFileMetadata getFileMetadata(String username, String filePath){
        GsSession gsSession = GSIdentityManager.getSession(username);

        DataManagerClient dmClient = gsSession.getDataManagerClient();

        GSFileMetadata fileMetadata = dmClient.getMetadata(filePath);

        return fileMetadata;
    }

    public boolean uploadFiles() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean downloadFiles(String username, GSFileMetadata fileToDownload, File localTargetFile) {

        GsSession gsSession = GSIdentityManager.getSession(username);

        DataManagerClient dmClient = gsSession.getDataManagerClient();

        GSDirectoryListing dirListing = dmClient.listDefaultDirectory();

        dmClient.downloadFile(fileToDownload, localTargetFile,true);
        return true;
    }

    public boolean mkDir() {
        return false;
    }

    public void ls() {

    }

}
