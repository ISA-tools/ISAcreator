package org.isatools.isacreator.ontologymanager.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/01/2011
 *         Time: 11:55
 */
public class DownloadUtils {

    private static Logger log = Logger.getLogger(DownloadUtils.class.getName());

    public static final String DOWNLOAD_ONTOLOGY_LOC = "Data" + File.separator + "ontologies" + File.separator;
    public static final String DOWNLOAD_FILE_LOC = "Data" + File.separator + "ontologies_matching_";
    public static final String XML_EXT = ".xml";

    public static boolean downloadFile(String fileLocation, String downloadLocation) {
        URL url;
        OutputStream os = null;
        InputStream is = null;

        try {
            url = new URL(fileLocation);

            URLConnection urlConn = url.openConnection();
            urlConn.setReadTimeout(10000);
            urlConn.setUseCaches(true);

            is = urlConn.getInputStream();

            os = new BufferedOutputStream(new FileOutputStream(downloadLocation));

            byte[] inputBuffer = new byte[1024];
            int numBytesRead;

            while ((numBytesRead = is.read(inputBuffer)) != -1) {
                os.write(inputBuffer, 0, numBytesRead);
            }

            return true;
        } catch (MalformedURLException e) {
            log.error("url malformed: " + e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            log.error("file not found" + e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("io exception caught" + e.getMessage());
            // we allow one retry attempt due to problems with BioPortal not always serving
            // back results on the first attempt!
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                log.error("io exception caught: " + ioe.getMessage());

            }
        }
    }

    public static void deleteFile(String file) {
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        }
    }
}
