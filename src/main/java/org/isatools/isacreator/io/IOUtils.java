package org.isatools.isacreator.io;

import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

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

    /**
     * Outputs ontology fields as a string for output in ISA-Tab
     *
     * @param ontologyTerms
     * @param fieldValues
     * @return
     */
    public static Map<String, String> processOntologyField(Map<String, String> ontologyTerms, Map<String, String> fieldValues) {

        String term = fieldValues.get(ontologyTerms.get(IOUtils.TERM));
        // At this point, we not have the term, accession and source ref fields. Next, is to set them to their correct values

        String tmpTerm = "";
        String tmpAccession = "";
        String tmpSourceRefs = "";

        if (term != null && term.contains(";")) {
            // then we have multiple values
            String[] ontologies = term.split(";");

            int numberAdded = 0;
            for (String ontologyTerm : ontologies) {
                OntologyTerm oo = OntologyManager.getOntologySelectionHistory().get(ontologyTerm);


                if (oo != null) {
                    tmpTerm += oo.getOntologyTermName();
                    tmpAccession += oo.getOntologySourceAccession();
                    tmpSourceRefs += oo.getOntologySource();
                } else {
                    if (term.contains(":")) {
                        String[] termAndSource = term.split(":");

                        if (termAndSource.length > 1) {
                            tmpSourceRefs += termAndSource[0];
                            tmpTerm += termAndSource[1];
                        } else {
                            tmpTerm = termAndSource[0];
                        }
                    }
                }

                if (numberAdded < ontologies.length - 1) {
                    tmpTerm += ";";
                    tmpAccession += ";";
                    tmpSourceRefs += ";";
                }
                numberAdded++;
            }

        } else if (term != null && term.contains(":")) {

            OntologyTerm oo = OntologyManager.getOntologySelectionHistory().get(term);

            if (oo.getOntologyTermName() != null) {
                tmpTerm = oo.getOntologyTermName();
                tmpAccession = oo.getOntologySourceAccession();
                tmpSourceRefs = oo.getOntologySource();
            } else {
                if (term.contains(":")) {
                    String[] termAndSource = term.split(":");

                    tmpSourceRefs = termAndSource[0];
                    tmpTerm = termAndSource[1];
                } else {
                    tmpTerm = term;
                    tmpAccession = "";
                    tmpSourceRefs = "";
                }
            }
        } else {
            tmpTerm = term;
            tmpAccession = ontologyTerms.get(IOUtils.ACCESSION) == null ? "" : ontologyTerms.get(IOUtils.ACCESSION);
            tmpSourceRefs = ontologyTerms.get(IOUtils.SOURCE_REF) == null ? "" : ontologyTerms.get(IOUtils.SOURCE_REF);
        }

        Map<String, String> result = new HashMap<String, String>();

        result.put(ontologyTerms.get(IOUtils.TERM), tmpTerm);
        result.put(ontologyTerms.get(IOUtils.ACCESSION), tmpAccession);
        result.put(ontologyTerms.get(IOUtils.SOURCE_REF), tmpSourceRefs);
        return result;
    }

}
