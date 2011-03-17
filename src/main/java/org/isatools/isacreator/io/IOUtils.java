package org.isatools.isacreator.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/03/2011
 *         Time: 11:06
 */
public class IOUtils {

    public static final String TERM = "term";
    public static final String SOURCE_REF = "term source";
    public static final String ACCESSION = "term accession";

    /**
     * Ontology terms are detected when there is a presence of 3 values in the field set with the same base name and
     * the words "Term Accession Number" & "Term Source Ref" are found.
     *
     * @param fieldNames - field names for the section @see Set<String>
     * @return Map from hashcode for field to a Map indicating which fields are source refs, terms and term accessions.
     */
    public static Map<Integer, Map<String, String>> getOntologyTerms(Set<String> fieldNames) {

        Map<Integer, Map<String, String>> fields = new HashMap<Integer, Map<String, String>>();

        if (fieldNames != null) {

            Set<String> ontologyFields = filterFields(fieldNames, ACCESSION, SOURCE_REF);

            for (String ontologyValues : ontologyFields) {
                String actualFieldName = ontologyValues.substring(0, ontologyValues.toLowerCase().indexOf("term")).trim();

                int hash = actualFieldName.hashCode();

                if (!fields.containsKey(hash)) {

                    fields.put(hash, new HashMap<String, String>());

                    if (actualFieldName.contains("[")) {
                        actualFieldName += "]";
                    }

                    fields.get(hash).put(TERM, actualFieldName);
                }

                if (ontologyValues.toLowerCase().contains(ACCESSION)) {
                    fields.get(hash).put(ACCESSION, ontologyValues);
                } else if (ontologyValues.toLowerCase().contains(SOURCE_REF)) {
                    fields.get(hash).put(SOURCE_REF, ontologyValues);
                }

            }
        }


        return fields;
    }


    public static Set<String> filterFields(Set<String> toFilter, String... filters) {
        Set<String> result = new HashSet<String>();
        for (String value : toFilter) {
            for (String filter : filters) {
                if (value.toLowerCase().contains(filter)) {
                    result.add(value);
                }
            }
        }

        return result;
    }

}
