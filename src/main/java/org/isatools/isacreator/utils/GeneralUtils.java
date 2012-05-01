/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
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
import org.isatools.isacreator.model.GeneralFieldTypes;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GeneralUtils {

    private static final Logger log = Logger.getLogger(GeneralUtils.class);

    public static boolean downloadFile(String fileLocation, String downloadLocation) {
        URL url;
        OutputStream os = null;
        InputStream is = null;

        try {
            url = new URL(fileLocation);

            URLConnection urlConn = url.openConnection();
            urlConn.setConnectTimeout(1000);
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
        } catch (FileNotFoundException e) {
            log.error("file not found: " + e.getMessage());
        } catch (IOException e) {
            log.error("io exception caught: " + e.getMessage());
        } catch (Exception e) {
            log.error("unexpected error occurred: " + e.getMessage());
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
        return false;
    }

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
            return toShorten.replaceAll(GeneralFieldTypes.CHARACTERISTIC.name, GeneralFieldTypes.CHARACTERISTIC.abbreviation);
        } else if (toShorten.contains(GeneralFieldTypes.PARAMETER_VALUE.name)) {
            return toShorten.replaceAll(GeneralFieldTypes.PARAMETER_VALUE.name, GeneralFieldTypes.PARAMETER_VALUE.abbreviation);
        } else if (toShorten.contains(GeneralFieldTypes.FACTOR_VALUE.name)) {
            return toShorten.replaceAll(GeneralFieldTypes.FACTOR_VALUE.name, GeneralFieldTypes.FACTOR_VALUE.abbreviation);
        } else {
            return toShorten;
        }
    }

    public static boolean charIsAlphanumeric(char c) {
        return String.valueOf(c).matches("[\\p{Alnum}]*");
    }


}
