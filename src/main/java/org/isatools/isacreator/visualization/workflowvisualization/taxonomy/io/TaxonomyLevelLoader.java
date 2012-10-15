package org.isatools.isacreator.visualization.workflowvisualization.taxonomy.io;

import org.isatools.isacreator.visualization.workflowvisualization.taxonomy.TaxonomyItem;
import org.isatools.isacreator.visualization.workflowvisualization.taxonomy.TaxonomyLevel;
import org.w3c.dom.NodeList;
import uk.ac.ebi.utils.xml.XPathReader;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 25/06/2012
 *         Time: 22:46
 */
public class TaxonomyLevelLoader {

    private static final String TAXONOMY_FILE = "/defaultConfigs/visualisation/taxonomy.xml";

    private static List<TaxonomyLevel> taxonomyLevels = new ArrayList<TaxonomyLevel>();

    public static void loadTaxonomyLevels() {
        XPathReader reader = new XPathReader(TaxonomyLevelLoader.class.getResourceAsStream(TAXONOMY_FILE));

        NodeList levels = (NodeList) reader.read("/taxonomy/level", XPathConstants.NODESET);

        if (levels.getLength() > 0) {
            taxonomyLevels.clear();

            for (int levelIndex = 0; levelIndex <= levels.getLength(); levelIndex++) {
                String name = (String) reader.read("/taxonomy/level[" + levelIndex + "]/@name", XPathConstants.STRING);

                NodeList values = (NodeList) reader.read("/taxonomy/level[" + levelIndex + "]/value", XPathConstants.NODESET);

                if (!name.equals("")) {
                    TaxonomyLevel newTaxonomyLevel = new TaxonomyLevel(name);

                    if (values.getLength() > 0) {
                        for (int valueIndex = 0; valueIndex <= values.getLength(); valueIndex++) {
                            String valueName = (String) reader.read("/taxonomy/level[" + levelIndex + "]/value[" + valueIndex + "]/@name", XPathConstants.STRING);
                            String valueImage = (String) reader.read("/taxonomy/level[" + levelIndex + "]/value[" + valueIndex + "]/@image", XPathConstants.STRING);
                            if (!valueName.equals("")) {
                                newTaxonomyLevel.addTaxonomyItem(new TaxonomyItem(valueName, valueImage));
                            }
                        }
                    }
                    taxonomyLevels.add(newTaxonomyLevel);
                }
            }
        }

    }

    public static List<TaxonomyLevel> getTaxonomyLevels() {
        return taxonomyLevels;
    }

}
