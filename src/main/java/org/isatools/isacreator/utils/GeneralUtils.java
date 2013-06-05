/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isatools.isacreator.utils;

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.GeneralFieldTypes;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import uk.ac.ebi.utils.io.DownloadUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class GeneralUtils {

    private static final Logger log = Logger.getLogger(GeneralUtils.class);
    public static final int BUFFER_SIZE = 1024;


    public static Map<String, FieldObject> findMissingFields(String[] headers, TableReferenceObject stdTableDefinition) {

        Set<String> headerSet = new HashSet<String>();

        headerSet.addAll(Arrays.asList(headers));

        Map<String, FieldObject> missingHeaders = new HashMap<String, FieldObject>();

        for (String field : stdTableDefinition.getHeaders().toArray(new String[stdTableDefinition.getHeaders().size()])) {
            if (!headerSet.contains(field) && !field.equals(TableReferenceObject.ROW_NO_TEXT)) {
                missingHeaders.put(field, stdTableDefinition.getFieldByName(field));
            }
        }

        return missingHeaders;
    }

    public static boolean isValueURL(String value) {
        Pattern urlPattern = Pattern.compile("((((ht|f)tp(s?)):(//)?)(\\S+)?)");
        Matcher m = urlPattern.matcher(value);
        return m.matches();
    }

    public static String getShortString(String toShorten) {
        toShorten = toShorten.replaceAll("(\\[|\\])", "");
        if (toShorten.contains(GeneralFieldTypes.CHARACTERISTIC.name)) {
            return toShorten.replaceAll(GeneralFieldTypes.CHARACTERISTIC.name, "") + " " + GeneralFieldTypes.CHARACTERISTIC.abbreviation;
        } else if (toShorten.contains(GeneralFieldTypes.PARAMETER_VALUE.name)) {
            return toShorten.replaceAll(GeneralFieldTypes.PARAMETER_VALUE.name, "") + " " + GeneralFieldTypes.PARAMETER_VALUE.abbreviation;
        } else if (toShorten.contains(GeneralFieldTypes.FACTOR_VALUE.name)) {
            return toShorten.replaceAll(GeneralFieldTypes.FACTOR_VALUE.name, "") + " " + GeneralFieldTypes.FACTOR_VALUE.abbreviation;
        } else if (toShorten.contains(GeneralFieldTypes.COMMENT.name)) {
            return toShorten.replaceAll(GeneralFieldTypes.COMMENT.name, "") + " " + GeneralFieldTypes.COMMENT.abbreviation;
        } else {
            return toShorten;
        }
    }

    public static boolean charIsAlphanumeric(char c) {
        return String.valueOf(c).matches("[\\p{Alnum}]*");
    }

    public static String createTmpDirectory(String name){

        String localTmpDirectory = System.getProperty("java.io.tmpdir") + File.separator +  name + File.separator + System.currentTimeMillis() + File.separator;
        boolean success = new File(localTmpDirectory).mkdirs();
        if (success) {
            System.out.println("Directory: "+ localTmpDirectory + " created");
            return localTmpDirectory;
        }else{
            System.out.println("Could not create "+localTmpDirectory);
            System.exit(-1);
        }
        return null;
    }

    public static String unzip(String toUnpackName) throws IOException {
        File file = new File(toUnpackName);
        return unzip(file);
    }

    public static String unzip(File toUnpack) throws IOException {
        ZipFile zf = new ZipFile(toUnpack);

        String parentDir = toUnpack.getParent();
        String pathToReturn = parentDir;

        Enumeration<? extends ZipEntry> entries = zf.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            // ignore any silly files as a result of the mac os file system (e.g. __MACOSX or .DS_STORE)
            if (!entry.getName().startsWith("_") && !entry.getName().startsWith(".")) {
                if (entry.isDirectory()) {
                    // Assume directories are stored parents first then children.
                    File newDirectory = new File(parentDir + File.separator + entry.getName());
                    pathToReturn = newDirectory.getPath();
                    if (!newDirectory.exists()) {
                        newDirectory.mkdirs();
                    }
                    continue;
                }
                copyInputStream(zf.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(parentDir + File.separator + entry.getName())));
            }
        }


        zf.close();
        return pathToReturn;

    }


    public static void copyInputStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }


    public static void main(String[] args) throws Exception {

        String configurationFilesLocation = PropertyFileIO.retrieveDefaultSettings().getProperty("configurationFilesLocation");
        String tmpDirectory = GeneralUtils.createTmpDirectory("Configurations");
        String downloadedFile = tmpDirectory+"config.zip";
        boolean downloaded = DownloadUtils.downloadFile(configurationFilesLocation, downloadedFile);
        System.out.println("downloadedFile="+downloadedFile);
        try{
            String unzipped = GeneralUtils.unzip(downloadedFile);
            System.out.println("unzipped="+unzipped);
        }catch(IOException ex){
            ex.printStackTrace();

        }
    }

}
