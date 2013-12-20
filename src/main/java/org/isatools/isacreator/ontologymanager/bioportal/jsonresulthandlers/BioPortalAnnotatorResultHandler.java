package org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers;

import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import javax.json.*;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BioPortalAnnotatorResultHandler {

    BioPortalSearchResultHandler searchHandler;

    public BioPortalAnnotatorResultHandler() {
        this(new BioPortalSearchResultHandler());
    }

    public BioPortalAnnotatorResultHandler(BioPortalSearchResultHandler searchHandler) {
        this.searchHandler = searchHandler;
    }

    public Map<String, Map<String, AnnotatorResult>> getSearchResults(String queryContents, String originalText, Set<String> originalTerms) {

        // map from search term to a map of full id to the ontology term.
        Map<String, Map<String, AnnotatorResult>> result = new HashMap<String, Map<String, AnnotatorResult>>();
        // for each token, we wan to find the matches and add them to the list

        StringReader reader = new StringReader(queryContents);

        JsonReader rdr = Json.createReader(reader);

        JsonArray obj = rdr.readArray();

        for (JsonObject annotationItem : obj.getValuesAs(JsonObject.class)) {

            AnnotatorResult annotatorResult = extractAnnotatorResult(annotationItem);

            if (annotatorResult != null) {

                String originalTerm = originalText.substring(annotatorResult.getStartIndex()-1, annotatorResult.getEndIndex());
                if (originalTerms.contains(originalTerm)) {

                    if (!result.containsKey(originalTerm)) {
                        result.put(originalTerm, new HashMap<String, AnnotatorResult>());
                    }

                    String ontologySource = annotatorResult.getOntologySource().getOntologyAbbreviation();

                    if (!result.get(originalTerm).containsKey(ontologySource)) {
                        result.get(originalTerm).put(ontologySource, annotatorResult);
                    }
                }
            }
        }

        return result;
    }

    private AnnotatorResult extractAnnotatorResult(JsonObject resultItem) {

        JsonObject annotatedClass = resultItem.getJsonObject("annotatedClass");
        JsonObject links = annotatedClass.getJsonObject("links");

        String ontologyId = links.getJsonString("ontology").toString();

        OntologyTerm ontologyTerm = searchHandler.getTermMetadata(annotatedClass.getString("@id"), ontologyId);

        if (ontologyTerm != null) {

            JsonNumber from = null, to = null;

            for (JsonObject annotation : resultItem.getJsonArray("annotations").getValuesAs(JsonObject.class)) {
                from = annotation.getJsonNumber("from");
                to = annotation.getJsonNumber("to");
            }

            if (from != null && to != null) {
                return new AnnotatorResult(ontologyTerm, AcceptedOntologies.getAcceptedOntologies().get(ontologyId), 1,
                        from.intValue(), to.intValue());
            }
        }

        return null;
    }
}
