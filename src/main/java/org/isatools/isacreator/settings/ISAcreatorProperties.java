package org.isatools.isacreator.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/08/2011
 *         Time: 16:15
 */
public class ISAcreatorProperties {

    public static final String CURRENT_CONFIGURATION = "current_configuration";
    public static final String CURRENT_ISATAB = "current_isatab";

    private static Map<String, String> properties = new HashMap<String, String>();

    public static void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public static String getProperty(String key) {
        if (properties.containsKey(key)) {
            return properties.get(key);
        }
        return "";
    }


}
