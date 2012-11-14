package org.isatools.isacreator.visualization.workflowvisualization.taxonomy;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 25/06/2012
 *         Time: 22:36
 */
public class TaxonomyLevel {

    private String name;
    private OrderedMap<String, TaxonomyItem> taxonomyItems;

    public TaxonomyLevel(String name) {
        this.name = name;
        this.taxonomyItems = new ListOrderedMap<String, TaxonomyItem>();
    }

    public String getName() {
        return name;
    }

    public Map<String, TaxonomyItem> getTaxonomyItems() {
        return taxonomyItems;
    }

    public void setTaxonomyItems(OrderedMap<String, TaxonomyItem> taxonomyItems) {
        this.taxonomyItems = taxonomyItems;
    }

    public void addTaxonomyItem(TaxonomyItem item) {
        taxonomyItems.put(item.getFileLocation(), item);
    }
    
    public String toString() {
        StringBuilder toString = new StringBuilder();
        for(String key : taxonomyItems.keySet()) {
            toString.append(key).append(";");
        }
        return toString.toString();
    }
}
