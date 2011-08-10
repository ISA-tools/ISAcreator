package org.isatools.isacreator.assayselection.platform;

import org.isatools.isacreator.assayselection.AssayType;
import org.isatools.isacreator.io.xpath.XPathReader;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlatformParser {

    private static final String PLATFORM_FILE = "/required/platforms.xml";


    public Map<AssayType, List<Platform>> loadPlatformFile() {
        XPathReader reader = new XPathReader(getClass().getResourceAsStream(PLATFORM_FILE));

        NodeList sections = (NodeList) reader.read("/technology-platforms/technology", XPathConstants.NODESET);

        if (sections.getLength() > 0) {

            Map<AssayType, List<Platform>> platforms = new HashMap<AssayType, List<Platform>>();

            for (int sectionIndex = 0; sectionIndex <= sections.getLength(); sectionIndex++) {
                String technologyType = (String) reader.read("/technology-platforms/technology[" + sectionIndex + "]/@type", XPathConstants.STRING);

                AssayType assayType = AssayType.extractRelevantType(technologyType);

                if (assayType != null) {

                    if (!platforms.containsKey(assayType)) {
                        platforms.put(assayType, new ArrayList<Platform>());
                    }

                    NodeList platformList = (NodeList) reader.read("/technology-platforms/technology[" + sectionIndex + "]/platforms/platform", XPathConstants.NODESET);

                    for (int platformIndex = 0; platformIndex <= platformList.getLength(); platformIndex++) {
                        String vendor = (String) reader.read("/technology-platforms/technology[" + sectionIndex + "]/platforms/platform[" + platformIndex + "]/vendor", XPathConstants.STRING);
                        String machine = (String) reader.read("/technology-platforms/technology[" + sectionIndex + "]/platforms/platform[" + platformIndex + "]/machine", XPathConstants.STRING);

                        if (!vendor.equals("") || !machine.equals("")) {
                            platforms.get(assayType).add(new Platform(vendor, machine));
                        }

                    }
                }
            }

            return platforms;
        }

        return new HashMap<AssayType, List<Platform>>();
    }

}
