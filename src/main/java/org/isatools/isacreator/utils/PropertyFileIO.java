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
import org.isatools.isacreator.settings.ISAcreatorProperties;

import java.io.*;
import java.util.Properties;

/**
 * @author Eamonn Maguire
 * @date Aug 29, 2009
 */


public class PropertyFileIO {
    private static final Logger log = Logger.getLogger(PropertyFileIO.class.getName());


    public static final String SETTINGS_DIR = "Settings";
    public static final String DEFAULT_CONFIGS_SETTINGS_PROPERTIES = "/defaultConfigs/settings/defaultsettings.properties";

    private static Properties defaultProperties;

    public static Properties retrieveDefaultSettings() {
        if (defaultProperties == null) {
            defaultProperties = new Properties();
            try {
                defaultProperties.load(PropertyFileIO.class.getResourceAsStream(DEFAULT_CONFIGS_SETTINGS_PROPERTIES));
            } catch (IOException e) {
                log.error("Unable to load default properties file");
            } catch (Exception e) {
                log.error("Unexpected exception occurred when trying to read in default properties file");
            }
        }
        return defaultProperties;
    }

    public static Properties loadSettings(String propertiesFile) {
        Properties p = new Properties();
        InputStream is = null;
        try {
            File f = new File(SETTINGS_DIR + File.separator + propertiesFile);
            if (f.exists()) {
                is = new FileInputStream(f);
                p.load(is);
                setProxy(p);
                return overrideWithDefaultProperties(p);
            }
        } catch (IOException e) {
            log.error("problem loading settings properties: " + e.getMessage());
            return new Properties();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return retrieveDefaultSettings();
    }

    private static Properties overrideWithDefaultProperties(Properties userSettings) {
        for (String key : retrieveDefaultSettings().stringPropertyNames()) {
            userSettings.put(key, retrieveDefaultSettings().get(key).toString());
        }

        return userSettings;
    }

    public static void updateISAcreatorProperties(Properties programProperties) {
        for (String propertyName : programProperties.stringPropertyNames()) {
            ISAcreatorProperties.setProperty(propertyName, programProperties.get(propertyName).toString());
        }
    }

    public static void saveProperties(Properties programProperties, String propertiesFile) {
        try {

            File settingsDir = new File(SETTINGS_DIR);
            File settingsFile = new File(SETTINGS_DIR + File.separator + propertiesFile);
            if (!settingsDir.exists()) {
                settingsDir.mkdir();
            }
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
            }

            OutputStream fos = new FileOutputStream(settingsFile);
            programProperties.store(fos, "settings");
            fos.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void setProxy(Properties p) {

        try {
            System.getProperties().put("proxySet", p.getProperty("httpProxy.usedLast"));

            System.getProperties().put("http.proxyHost", Boolean.valueOf(p.getProperty("httpProxy.usedLast"))
                    ? p.getProperty("httpProxy.hostname") : "");

            System.getProperties().put("http.proxyPort", Boolean.valueOf(p.getProperty("httpProxy.usedLast"))
                    ? p.getProperty("httpProxy.portNumber") : "");

            System.getProperties().put("http.proxyUser", Boolean.valueOf(p.getProperty("httpProxy.useAuth"))
                    ? p.getProperty("httpProxy.login") : "");

            System.getProperties().put("http.proxyPassword", Boolean.valueOf(p.getProperty("httpProxy.useAuth"))
                    ? p.getProperty("httpProxy.password") : "");

        } catch (NullPointerException npe) {
            // thrown when a property doesn't exist. In this case, the program should not fail, just carry on. We should also report it
            log.error(npe.getMessage());
            log.info("Setting the proxy information was problematic. Some values didn't exist.");

        }
    }
}
