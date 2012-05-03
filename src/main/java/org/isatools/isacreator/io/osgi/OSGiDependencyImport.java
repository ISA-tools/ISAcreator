package org.isatools.isacreator.io.osgi;

import org.w3c.dom.NodeList;
import uk.ac.ebi.utils.xml.XPathReader;

import javax.xml.xpath.XPathConstants;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 02/05/2012
 *         Time: 11:35
 */
public class OSGiDependencyImport {

    private static final String FILE = "/defaultConfigs/osgi-framework-packages.xml";


    public static String getDependencies() {
        XPathReader reader = new XPathReader(OSGiDependencyImport.class.getResourceAsStream(FILE));

        StringBuilder packages = new StringBuilder();

        NodeList sections = (NodeList) reader.read("/osgiDependencies/dependency", XPathConstants.NODESET);

        if (sections.getLength() > 0) {
            for (int sectionIndex = 0; sectionIndex <= sections.getLength(); sectionIndex++) {
                String dependency = (String) reader.read("/osgiDependencies/dependency[" + sectionIndex + "]/@package", XPathConstants.STRING);
                if (!dependency.isEmpty()) {
                    packages.append(dependency).append(",");

                }
            }
        }

        return packages.substring(0, packages.length() - 1);
    }

}
