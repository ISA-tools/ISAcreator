package org.isatools.isacreator.model;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;

import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 08/03/2011
 *         Time: 13:19
 */
public class ISASection {

    protected OrderedMap<String, String> fieldValues;

    public ISASection() {
        fieldValues = new ListOrderedMap<String, String>();
    }

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public void addComment(String commentType, String commentValue) {
        fieldValues.put(commentType, commentValue);
    }

    public String getComment(String commentType) {
        return (fieldValues.get(commentType) == null) ? "" : fieldValues.get(commentType);
    }

}
