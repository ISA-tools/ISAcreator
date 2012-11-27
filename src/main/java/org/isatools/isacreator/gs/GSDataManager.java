package org.isatools.isacreator.gs;

import org.apache.log4j.Logger;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.GsSession;
import org.genomespace.client.exceptions.ForbiddenException;
import org.genomespace.client.exceptions.NotFoundException;
import org.genomespace.client.utils.WebClientBuilder;
import org.genomespace.datamanager.core.GSDirectoryListing;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static Logger log = Logger.getLogger(GSDataManager.class);

    private GsSession gsSession = null;

    /***
     * Constructor. The data manager works for a particular GS session.
     *
     * @param session
     */
    public GSDataManager(GsSession session){
        gsSession = session;
    }

    public DataManagerClient getDataManagerClient(){
        return gsSession.getDataManagerClient();
    }

    /**
     * List files in given directory
     *
     * @param dirPath
     */
    public List<String> ls(String dirPath){
        try{
            DataManagerClient dmClient = gsSession.getDataManagerClient();
            GSDirectoryListing dirListing = dmClient.list(dirPath);
            List<GSFileMetadata> fileMetadataList = dirListing.getContents();
            List<String> listing = new ArrayList<String>();
            for(GSFileMetadata fileMetadata:fileMetadataList){
                listing.add(fileMetadata.getName());
            }
            return listing;
        }catch(NotFoundException e){
           System.err.println("The directory path "+dirPath+" was not found in Genome Space.");
           System.exit(-1);
        }
        return null;
    }

    /**
     * Get InputStreams for all the files in a directory
     *
     * @param dirPath
     * @return
     */
    public List<InputStream> lsInputStreams(String dirPath) {
        //setting the max number of concurrent connections
       WebClientBuilder.setDefaultMaxConnectionsPerHost(10);
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSDirectoryListing dirListing = dmClient.list(dirPath);
        List<GSFileMetadata> fileMetadataList = dirListing.getContents();
        List<InputStream> listing = new ArrayList<InputStream>();
        for(GSFileMetadata fileMetadata:fileMetadataList){
            System.out.println("fileMetadata="+fileMetadata);
            InputStream is = dmClient.getInputStream(fileMetadata);
            listing.add(is);
        }
        return listing;
    }

    /**
     * List files in home directory
     *
     * @param username
     */
    public void lsHome(String username){
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSDirectoryListing homeDirInfo = dmClient.listDefaultDirectory();
    }


    public GSFileMetadata getFileMetadata(String url){
        //System.out.println("at getFileMetadata -> url="+url);
        //System.out.println("is logged in?="+gsSession.isLoggedIn());
        String filePath = transformURLtoFilePath(url) ;
        //System.out.println("filePath="+filePath);
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSFileMetadata fileMetadata = dmClient.getMetadata(filePath);
        //System.out.println("fileMetadata="+fileMetadata);
        return fileMetadata;
    }

    public boolean uploadFiles() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Download a file to a given directory
     *
     * @param fileToDownload
     * @param localDirPath
     * @return
     */
    public ErrorMessage downloadFile(String fileToDownload, String localDirPath) {
        log.debug("fileToDownload="+fileToDownload);
        fileToDownload = transformURLtoFilePath(fileToDownload);
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSFileMetadata fileToDownloadMetadata = dmClient.getMetadata(fileToDownload);
        System.out.println("remote file ="+fileToDownloadMetadata);
        String localFilePath = localDirPath+fileToDownloadMetadata.getName();
        System.out.println("local file = "+localFilePath);
        File localTargetFile = new File(localFilePath);
        dmClient.downloadFile(fileToDownloadMetadata, localTargetFile, true);
        return null;
    }


    /**
     * Given a directory path in GS and a local directory path, it downloads all the files in the GS directory to the local directory.
     *
     * @param dirPath
     * @param localDirPath
     * @return
     */
    public List<ErrorMessage> downloadAllFilesFromDirectory(String dirPath, String localDirPath, String pattern) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        dirPath = transformURLtoFilePath(dirPath);

        GSDirectoryListing dirListing = null;
        try{
            dirListing = dmClient.list(dirPath);
        }catch(NotFoundException ex){
            ex.printStackTrace();
            errors.add(new ErrorMessage(ErrorLevel.ERROR, "The directory "+dirPath+" was not found"));
             return errors;

        }catch(ForbiddenException e){
            errors.add(new ErrorMessage(ErrorLevel.ERROR, "Access forbidden to directory "+dirPath+" in Genome Space"));
            return errors;
        }catch(IllegalArgumentException e){
            errors.add(new ErrorMessage(ErrorLevel.ERROR, "The directory "+dirPath+" is not correct in Genome Space"));
            return errors;
        }

        List<GSFileMetadata> fileMetadataList = dirListing.getContents();
        for(GSFileMetadata fileToDownload: fileMetadataList){
             if (pattern!=null && !fileToDownload.getName().matches(pattern))
                 continue;
             String localFilePath = localDirPath+fileToDownload.getName();
             File localTargetFile = new File(localFilePath);
             dmClient.downloadFile(fileToDownload, localTargetFile,true);
        }
        return errors;
    }


    /**
     * Only download the files from the directory that follow the pattern given
     *
     * @param dirPath
     * @param localDirPath
     * @param pattern
     * @return
     */
    public List<ErrorMessage> downloadAllPatternFilesFromDirectory(String dirPath, String localDirPath, String pattern) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        dirPath = transformURLtoFilePath(dirPath);

        GSDirectoryListing dirListing = null;
        try{
            dirListing = dmClient.list(dirPath);
        }catch(NotFoundException ex){
            ex.printStackTrace();
            errors.add(new ErrorMessage(ErrorLevel.ERROR, "The directory "+dirPath+" was not found"));
            return errors;

        }catch(ForbiddenException e){
            errors.add(new ErrorMessage(ErrorLevel.ERROR, "Access forbidden to directory "+dirPath+" in Genome Space"));
            return errors;
        }catch(IllegalArgumentException e){
            errors.add(new ErrorMessage(ErrorLevel.ERROR, "The directory "+dirPath+" is not correct in Genome Space"));
            return errors;
        }

        List<GSFileMetadata> fileMetadataList = dirListing.getContents();
        for(GSFileMetadata fileToDownload: fileMetadataList){
            String localFilePath = localDirPath+fileToDownload.getName();
            File localTargetFile = new File(localFilePath);
            dmClient.downloadFile(fileToDownload, localTargetFile,true);
        }
        return errors;
    }

    private String transformURLtoFilePath(String url){
        if (url==null) return null;
        Pattern HOME = Pattern.compile("/Home/");
        Matcher m = HOME.matcher(url);
        while (m.find()) {
            return url.substring(m.start());
        }
        return null;
    }

    public GSFileMetadata mkDir(String newDirectoryName, GSFileMetadata parentDirectoryName) {
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        GSFileMetadata newDirMeta = dmClient.createDirectory(parentDirectoryName,newDirectoryName);
        return newDirMeta;
    }

    public boolean saveFile(File localFile, GSFileMetadata parentDirectory){
        DataManagerClient dmClient = gsSession.getDataManagerClient();
        dmClient.uploadFile(localFile, parentDirectory);
        return true;
    }




}
